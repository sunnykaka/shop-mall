package com.kariqu.usercenter.web;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import com.kariqu.accountcenter.domain.Account;
import com.kariqu.common.*;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.login.SessionUtils;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.usercenter.domain.User;
import com.kariqu.tradecenter.domain.Valuation;
import com.kariqu.usercenter.domain.UserGrade;
import com.kariqu.usercenter.helper.SendContent;
import com.kariqu.usercenter.helper.ValuationProduct;
import com.kariqu.usercenter.service.UserService;
import com.kariqu.tradecenter.service.ValuationQuery;
import com.kariqu.tradecenter.service.ValuationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User: Asion
 * Date: 13-3-6
 * Time: 下午2:12
 */
@Controller
public class ValuationController {

    private final Log logger = LogFactory.getLog(ValuationController.class);

    @Autowired
    private ValuationService valuationService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;


    /**
     * 按商品分组的评论列表
     *
     * @param start
     * @param limit
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/user/valuation/product/list")
    public void valuationProductList(@RequestParam(value = "searchProductId", defaultValue = "0") int searchProductId, @RequestParam int start, @RequestParam int limit, HttpServletResponse response) throws IOException {
        if (searchProductId != 0) {
            List<ValuationProduct> list = new LinkedList<ValuationProduct>();
            Product productById = productService.getProductById(searchProductId);
            if (productById != null) {
                ValuationProduct valuationProduct = new ValuationProduct();
                valuationProduct.setName(productById.getName());
                valuationProduct.setId(searchProductId);
                valuationProduct.setCount(valuationService.queryValuationCountByProductId(searchProductId));
                list.add(valuationProduct);
            }
            new JsonResult(true).addData("totalCount", list.size()).addData("result", list).toJson(response);

        } else {
            List<ValuationProduct> list = new LinkedList<ValuationProduct>();
            Page<Map<String, Long>> page = valuationService.queryValuationGroup(start, limit);

            for (Map<String, Long> map : page.getResult()) {
                long productId = map.get("productId");
                long count = map.get("count");
                ValuationProduct valuationProduct = new ValuationProduct();
                valuationProduct.setCount(count);
                Product product = productService.getProductById((int) productId);
                if (product != null) {
                    valuationProduct.setName(product.getName());
                    valuationProduct.setId(product.getId());
                    list.add(valuationProduct);
                }
            }
            new JsonResult(true).addData("totalCount", page.getTotalCount()).addData("result", list).toJson(response);
        }

    }

    /**
     * 某个商品的评论列表
     *
     * @param start
     * @param limit
     * @param productId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/user/valuation/list")
    public void valuationList(@RequestParam int start, @RequestParam int limit,
                              @RequestParam(value = "userName", defaultValue = "") String userName,
                              @RequestParam(value = "productId", defaultValue = "0") int productId,
                              @RequestParam(value = "isImportValuation", defaultValue = "false") boolean isImportValuation,
                              HttpServletResponse response) throws IOException {
        int pageNo = start / limit + 1;

        int userId = 0;
        if (StringUtils.isNotEmpty(userName)) {
            User user = userService.getUserByUserName(userName);
            userId = (user != null) ? user.getId() : -1;
        } else if (productId == 0) {
            userId = -1;
            userName = "-1";
        }
        ValuationQuery query = ValuationQuery.asProductIdAndUserId(productId, userId);
        query.setPageNo(pageNo);
        query.setPageSize(limit);

        Page<Valuation> valuationPage;
        if (isImportValuation) {
            query.setUserName(userName);
            valuationPage = valuationService.queryImportValuation(query);
        } else {
            valuationPage = valuationService.queryValuation(query);
        }

        List<Valuation> result = valuationPage.getResult();
        StringBuilder sbd = new StringBuilder();
        for (Valuation valuation : result) {
            sbd.delete(0, sbd.toString().length());

            sbd.append(DateUtils.formatDate(valuation.getCreateDate(), DateUtils.DateFormatType.DATE_FORMAT_STR))
                    .append(" 用户<span style=\"color: #0000ff\">(").append(valuation.getUserName()).append(")</span>评价: ").append(valuation.getContent());
            if (valuation.replyBeforeAppend()) {
                if (StringUtils.isNotBlank(valuation.getReplyContent())) {
                    sbd.append("<br/><br/><span style=\"color: #e7731f;\">")
                            .append(DateUtils.formatDate(valuation.getReplyTime(), DateUtils.DateFormatType.DATE_FORMAT_STR))
                            .append(" 客服").append(StringUtils.isNotBlank(valuation.getOperator()) ? "<span style=\"color: #ffa500\">(" + valuation.getOperator() + ")</span>" : "")
                            .append("回复 >> ").append(valuation.getReplyContent()).append("</span>");
                }
                if (StringUtils.isNotBlank(valuation.getAppendContent())) {
                    sbd.append("<br><br>").append(DateUtils.formatDate(valuation.getAppendDate(), DateUtils.DateFormatType.DATE_FORMAT_STR))
                            .append(" 用户追加:").append(valuation.getAppendContent());
                }
            } else {
                if (StringUtils.isNotBlank(valuation.getAppendContent())) {
                    sbd.append("<br><br><span style=\"color: #e7731f;\">")
                            .append(DateUtils.formatDate(valuation.getAppendDate(), DateUtils.DateFormatType.DATE_FORMAT_STR))
                            .append(" 用户追加: ").append(valuation.getAppendContent()).append("</span>");
                }
                if (StringUtils.isNotBlank(valuation.getReplyContent())) {
                    sbd.append("<br/><br/>").append(DateUtils.formatDate(valuation.getReplyTime(), DateUtils.DateFormatType.DATE_FORMAT_STR))
                            .append("客服").append(StringUtils.isNotBlank(valuation.getOperator()) ? "<span style=\"color: #ffa500\">(" + valuation.getOperator() + ")</span>" : "")
                            .append("回复 >> ").append(valuation.getReplyContent());
                }
            }
            if (StringUtils.isNotBlank(valuation.getAppendReplyContent())) {
                sbd.append("<br><br><span style=\"color: #e7731f;\">").append(DateUtils.formatDate(valuation.getAppendReplyDate(), DateUtils.DateFormatType.DATE_FORMAT_STR))
                        .append(" 客服").append(StringUtils.isNotBlank(valuation.getAppendOperator()) ? "(" + valuation.getAppendOperator() + ")" : "")
                        .append("追加回复 >> ").append(valuation.getAppendReplyContent()).append("</span>");
            }
            valuation.setContent(sbd.toString());
        }
        new JsonResult(true).addData("totalCount", valuationPage.getTotalCount()).addData("result", result).toJson(response);
    }

    /**
     * 后台手动添加评论
     *
     * @param valuation
     * @param userName
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/user/valuation/add", method = RequestMethod.POST)
    @Permission("手动添加评论")
    public void addValuation(Valuation valuation, String userName, HttpServletResponse response) throws IOException {
        User userByUserName = userService.getUserByUserName(userName);
        if (userByUserName == null) {
            new JsonResult(false, "没有这个用户").toJson(response);
            return;
        }
        valuation.setUserId(userByUserName.getId());

        Product productById = productService.getProductById(valuation.getProductId());
        if (productById == null) {
            new JsonResult(false, "没有这个商品").toJson(response);
            return;
        }

        valuationService.createValuation(valuation);
        new JsonResult(true).toJson(response);
    }


    @RequestMapping(value = "/user/valuation/reply", method = RequestMethod.POST)
    @Permission("回复评论")
    public void replayValuation(Valuation valuation, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Account currentAccount = SessionUtils.getLoginAccount(request.getSession());
        valuation.setOperator(currentAccount.getUserName());
        valuation.setOperatorId(currentAccount.getId());

        valuation.setReplyContent(HtmlUtils.htmlEscape(valuation.getReplyContent()));
        valuation.setReplyTime(new Date());

        valuationService.createValuationReply(valuation);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/user/valuation/appendReply", method = RequestMethod.POST)
    @Permission("追加回复评论")
    public void appendReplayValuation(Valuation valuation, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Account currentAccount = SessionUtils.getLoginAccount(request.getSession());
        valuation.setAppendOperator(currentAccount.getUserName());

        valuation.setAppendReplyContent(HtmlUtils.htmlEscape(valuation.getAppendReplyContent()));
        valuation.setAppendReplyDate(new Date());

        valuationService.createValuationReply(valuation);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/user/valuation/product/delete", method = RequestMethod.POST)
    @Permission("删除整个商品的评论")
    public void deleteValuationByProductId(int[] ids, HttpServletResponse response) throws IOException {
        for (int id : ids) {
            valuationService.deleteValuationByProductId(id);
        }
        new JsonResult(true).toJson(response);
    }


    @RequestMapping(value = "/user/valuation/delete", method = RequestMethod.POST)
    @Permission("删除评论")
    public void deleteValuationById(@RequestParam("ids") int[] ids,
                                    @RequestParam(value = "isImportValuation", defaultValue = "false") boolean isImportValuation,
                                    HttpServletResponse response) throws IOException {

        for (int id : ids) {
            if (isImportValuation) {
                valuationService.deleteImportValuation(id);
            } else {
                valuationService.deleteValuation(id);

            }
        }

        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/user/valuation/import", method = RequestMethod.POST)
    @Permission("导入商品评论")
    public void importValuation(MultipartFile uploadValuationFile, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Account currentAccount = SessionUtils.getLoginAccount(request.getSession());

        try {
            List<Valuation> valuationList = createValuations(uploadValuationFile, currentAccount.getUserName());
            addValuation(valuationList);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("导入商品评论异常", e);
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    /**
     * 将excel解析为评论对象
     *
     * @param uploadFile
     * @return
     * @throws Exception
     */
    private List<Valuation> createValuations(MultipartFile uploadFile, String operator) throws Exception {
        List<Valuation> valuationList = new ArrayList<Valuation>();
        InputStream inp = null;

        try {
            if (!SmsSendController.isExcel(uploadFile.getOriginalFilename())) {
                throw new Exception("该文件不是Excel文件，请重新选择");
            }

            inp = uploadFile.getInputStream();
            Workbook workbook = WorkbookFactory.create(inp);
            ExcelSheet sheet = new ExcelSheet(workbook.getSheetAt(0));
            Table<Integer, String, String> table = sheet.getAsTable();
            for (int i : table.rowKeySet()) {
                Map<String, String> row = table.row(i);
                valuationList.add(tidyValuation(row, i + 1, operator));
            }
        } finally {
            try {
                if (inp != null) {
                    inp.close();
                }
            } catch (IOException e) {
                if (inp != null) {
                    inp = null;
                }
            }
        }

        return valuationList;
    }

    /**
     * 将一行excel数据整理成一个评论对象
     *
     * @param row
     * @param index
     * @return
     * @throws Exception
     */
    private Valuation tidyValuation(Map<String, String> row, int index, String operator) throws Exception {
        Valuation valuation = new Valuation();
        Date updateDate;

        // 判断商品id，商品id不能为空，且商品id必须为数字，并且存在该商品
        if (StringUtils.isBlank(row.get("商品id")) || !StringUtils.isNumeric(row.get("商品id"))) {
            throw new Exception("第 " + index + " 行, 商品id的数据必须是数字, 请检查数据是否正确");
        }
        Integer productId = Integer.parseInt(row.get("商品id"));
        Product product = productService.getProductById(productId);
        if (null == product) {
            throw new Exception("第 " + index + " 行, 商品id没有对应的商品信息, 请检查数据是否正确");
        }
        valuation.setProductId(productId);

        // 判断用户名，用户名不能为空，且用户名长度为4-20个字符
        String userName = row.get("用户名");
        int userNameLength = ChineseLength.length(userName);
        if (StringUtils.isBlank(userName)) {
            throw new Exception("第 " + index + " 行, 用户名不能为空, 请检查数据是否正确");
        }
        if (!(userNameLength >= 4 && userNameLength <= 20)) {
            throw new Exception("第 " + index + " 行, 用户名长度必须为4-20个字符, 请检查数据是否正确");
        }
        valuation.setUserName(userName);

        // 判断用户等级，用户等级必须为A,B,C三级，如果未填写则是A级
        String userGrade = row.get("用户等级");
        if (StringUtils.isBlank(userGrade)) {
            userGrade = "A";
        } else if (!(userGrade.compareTo("A") >= 0 && userGrade.compareTo("C") <= 0)) {
            throw new Exception("第 " + index + " 行, 用户等级必须为A,B,C三级, 请检查数据是否正确");
        }
        valuation.setGrade(UserGrade.valueOf(userGrade));

        // 判断评价分数，评价分数必须为1-5的数字，如果为空默认为5级
        String point = row.get("评论分数");
        if (StringUtils.isBlank(point)) {
            point = "5";
        } else if (!(point.compareTo("1") >= 0 && point.compareTo("5") <= 0)) {
            throw new Exception("第 " + index + " 行, 评价分数必须为1-5的数字, 请检查数据是否正确");
        }
        valuation.setPoint(Integer.parseInt(point));

        // 评价内容不能为空,不能超过2000字
        String content = HtmlUtils.htmlEscape(row.get("评论内容"));
        if (StringUtils.isEmpty(content)) {
            throw new Exception("第 " + index + " 行, 评价内容不能为空, 请检查数据是否正确");
        } else if (content.length() > 2000) {
            throw new Exception("第 " + index + " 行, 评价内容不能大于2000字, 请检查数据是否正确");
        }
        valuation.setContent(content);

        // 评论时间不能为空，格式校验
        if (StringUtils.isBlank(row.get("评论时间"))) {
            throw new Exception("第 " + index + " 行, 评论时间不能为空, 请检查数据是否正确");
        }
        Date contentTime;
        try {
            contentTime = DateUtils.parseDate(row.get("评论时间"), DateUtils.DateFormatType.DATE_FORMAT_STR);
        } catch (Exception e) {
            throw new Exception("第 " + index + " 行, 评论时间格式错误，格式为yyyy-MM-dd HH:mm, 请检查数据是否正确");
        }
        valuation.setCreateDate(contentTime);
        updateDate = contentTime;

        // 客服评论回复内容判断，如果有评论内容必须有评论时间，做时间格式判断,且客服回复评论时间必须在评论时间之后
        String replyContent = HtmlUtils.htmlEscape(row.get("客服评论回复内容"));
        Date replyTime = null;
        if (StringUtils.isNotBlank(replyContent)) {
            if (StringUtils.isBlank(row.get("客服回复评论时间"))) {
                throw new Exception("第 " + index + " 行, 因为有客服评论回复内容，客服回复评论时间不能为空, 请检查数据是否正确");
            }
            try {
                replyTime = DateUtils.parseDate(row.get("客服回复评论时间"), DateUtils.DateFormatType.DATE_FORMAT_STR);
            } catch (Exception e) {
                throw new Exception("第 " + index + " 行, 客服回复评论时间格式错误，格式为yyyy-MM-dd HH:mm, 请检查数据是否正确");
            }

            if (replyTime.compareTo(contentTime) <= 0) {
                throw new Exception("第 " + index + " 行, 客服回复评论时间必须在评论时间之后, 请检查数据是否正确");
            }
            valuation.setReplyContent(replyContent);
            valuation.setReplyTime(replyTime);
            updateDate = replyTime;
        }

        // 追加评论内容判断，如果有追加评论内容则必须有追加评论时间，做时间格式判断，且追加评论时间必须在评论时间之后
        String appendContent = HtmlUtils.htmlEscape(row.get("追加评论内容"));
        Date appendDate = null;
        if (StringUtils.isNotBlank(appendContent)) {
            if (appendContent.length() > 2000) {
                throw new Exception("第 " + index + " 行, 追加评论内容不能大于2000字, 请检查数据是否正确");
            } else if (StringUtils.isBlank(row.get("追加评论时间"))) {
                throw new Exception("第 " + index + " 行, 因为有追加评论内容，追加评论时间不能为空, 请检查数据是否正确");
            }
            try {
                appendDate = DateUtils.parseDate(row.get("追加评论时间"), DateUtils.DateFormatType.DATE_FORMAT_STR);
            } catch (Exception e) {
                throw new Exception("第 " + index + " 行, 追加评论时间格式错误，格式为yyyy-MM-dd HH:mm, 请检查数据是否正确");
            }

            if (appendDate.compareTo(contentTime) <= 0) {
                throw new Exception("第 " + index + " 行, 追加评论时间必须在评论时间之后, 请检查数据是否正确");
            }
            valuation.setAppendContent(appendContent);
            valuation.setAppendDate(appendDate);
            if (appendDate.compareTo(updateDate) > 0) {
                updateDate = appendDate;
            }
        }

        // 客服追加回复评价内容判断，如果有客服追加回复评价内容则必须有客服追加回复评价时间，时间格式判断，且客服追加回复评价时间必须在客服回复评论时间之后
        String appendReplyContent = HtmlUtils.htmlEscape(row.get("客服追加回复评价内容"));
        Date appendReplyDate = null;
        if (StringUtils.isNotBlank(appendReplyContent) && StringUtils.isNotBlank(replyContent)) {
            if (StringUtils.isBlank(row.get("客服追加回复评价时间"))) {
                throw new Exception("第 " + index + " 行, 因为有客服追加回复评价内容，客服追加回复评价时间不能为空, 请检查数据是否正确");
            }
            try {
                appendReplyDate = DateUtils.parseDate(row.get("客服追加回复评价时间"), DateUtils.DateFormatType.DATE_FORMAT_STR);
            } catch (Exception e) {
                throw new Exception("第 " + index + " 行, 客服追加回复评价时间格式错误，格式为yyyy-MM-dd HH:mm, 请检查数据是否正确");
            }


            if (appendReplyDate.compareTo(replyTime) <= 0) {
                throw new Exception("第 " + index + " 行, 客服追加回复评价时间必须在客服回复评论时间之后, 请检查数据是否正确");
            }
            valuation.setAppendReplyContent(appendReplyContent);
            valuation.setAppendReplyDate(appendReplyDate);
            if (appendReplyDate.compareTo(updateDate) > 0) {
                updateDate = appendReplyDate;
            }
        }
        valuation.setUpdateDate(updateDate);
        valuation.setOperator(operator);
        return valuation;
    }

    /**
     * 循环将数据存入数据库
     *
     * @param valuationList
     */
    private void addValuation(List<Valuation> valuationList) throws Exception {
        int index = 1;
        try {
            for (Valuation valuation : valuationList) {
                valuationService.createImportValuation(valuation);
                index++;
            }
        } catch (Exception e) {
            throw new Exception("第 " + index + " 行数据插入数据库错误");
        }
    }
}

