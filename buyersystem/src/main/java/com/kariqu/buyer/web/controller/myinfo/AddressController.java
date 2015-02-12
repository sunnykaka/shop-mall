package com.kariqu.buyer.web.controller.myinfo;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.common.WebSystemUtil;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.json.JsonUtil;
import com.kariqu.tradecenter.domain.Address;
import com.kariqu.tradecenter.service.AddressService;
import com.kariqu.tradecenter.service.LinkageService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 用户收货地址管理
 * User: Asion
 * Date: 12-5-30
 * Time: 下午5:25
 */
@Controller
public class AddressController {

    private final Log logger = LogFactory.getLog(AddressController.class);

    @Autowired
    private AddressService addressService;

    /**
     * 处理级联
     */
    @Autowired
    private LinkageService linkageService;

    @RenderHeaderFooter
    @RequestMapping(value = "/my/address")
    public String myAddress(Model model, HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        model.addAttribute("addressList", addressService.queryAllAddress(sessionUserInfo.getId()));
        model.addAttribute("site_title", "收货地址管理");

        // 查询所有省信息
        model.addAttribute("provinceList", linkageService.getAllProvince());

        model.addAttribute("contentVm", "myinfo/address.vm");
        return "myinfo/myInfoLayout";
    }


    @RequestMapping(value = "/my/province")
    public void queryProvince(HttpServletResponse response) throws IOException {
        try {
            new JsonResult(true).addData("provinceList", linkageService.getAllProvince()).toJson(response);
        } catch (IOException e) {
            logger.error("解析省份出错：", e);
            new JsonResult(false).toJson(response);
        }
    }

    @RequestMapping(value = "/my/city")
    public void queryCity(String code, HttpServletResponse response) throws IOException {
        try {
            new JsonResult(true).addData("cityList", linkageService.getCityByProvinceCode(code)).toJson(response);
        } catch (IOException e) {
            logger.error("解析区市出错：", e);
            new JsonResult(false).toJson(response);
        }
    }

    @RequestMapping(value = "/my/area")
    public void queryArea(String code, HttpServletResponse response) throws IOException {
        try {
            new JsonResult(true).addData("areaList", linkageService.getAreaByCityCode(code)).toJson(response);
        } catch (IOException e) {
            logger.error("解析区域/县出错：", e);
            new JsonResult(false).toJson(response);
        }
    }

    /**
     * 添加新的地址
     */
    @RequestMapping(value = "/user/address/add", method = RequestMethod.POST)
    public void createAddress(Address address, String city, String districts, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        try {
            if (checkUserAddressIsValid(address, response)) {
                List<Address> list = addressService.queryAllAddress(sessionUserInfo.getId());
                if (list.size() >= AddressConstants.Address_Max_Count) {
                    new JsonResult(false, "添加失败，地址记录不能超过十条!").toJson(response);
                } else {
                    address.setUserId(sessionUserInfo.getId());
                    address.setProvince(address.getProvince() + "," + city + getArea(districts));
                    if (list.size() == 0) {
                        address.setDefaultAddress(true);
                    }
                    address.setName(HtmlUtils.htmlEscape(address.getName().trim()));
                    address.setLocation(HtmlUtils.htmlEscape(address.getLocation().trim()));
                    addressService.createAddress(address);
                    new JsonResult(true).addData("address", JsonUtil.objectToJson(address)).addData("id", address.getId()).toJson(response);
                }
            }
        } catch (Exception e) {
            logger.error("个人[" + sessionUserInfo.getUserName() + "]中心添加地址失败", e);
            new JsonResult(false, "地址添加失败!").toJson(response);
        }
    }

    /**
     * 修改默认地址
     */
    @RequestMapping(value = "/user/address/setDefault", method = RequestMethod.POST)
    public void setDefault(int id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        try {
            if (id != 0) {
                addressService.updateDefaultAddress(sessionUserInfo.getId(), id);
                new JsonResult(true).toJson(response);
                return;
            } else {
                new JsonResult(false, "请选择要修改的地址！").toJson(response);
                return;
            }
        } catch (Exception e) {
            new JsonResult(false, "默认地址修改失败!").toJson(response);
            logger.error("个人中心(" + sessionUserInfo.getUserName() + ")修改地址失败", e);
        }
    }

    /**
     * 获取地址的详细信息
     *
     * @throws IOException
     */
    @RequestMapping(value = "/user/address/{id}")
    public void getAddressDetail(@PathVariable("id") int id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        if (id == 0) {
            new JsonResult(false, "没有该地址!").toJson(response);
            return;
        }
        Address address = addressService.getAddress(id, sessionUserInfo.getId());
        if (address != null) {
            new JsonResult(true).addData("address", address).toJson(response);
            return;
        } else {
            new JsonResult(false, "获取对象信息失败!").toJson(response);
            return;
        }
    }

    /**
     * 修改地址信息
     *
     * @throws IOException
     */
    @RequestMapping(value = "/user/address/update", method = RequestMethod.POST)
    public void updateAddress(Address address, String city, String districts, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        try {
            if (checkUserAddressIsValid(address, response)) {
                Address oldAddress = addressService.getAddress(address.getId(),sessionUserInfo.getId());
                oldAddress.setProvince(address.getProvince() + "," + city + getArea(districts));
//                address.setUserId(sessionUserInfo.getId());
                if (logger.isDebugEnabled()) {
                    logger.debug("用户" + sessionUserInfo.getUserName() + "id:" + sessionUserInfo.getId() + "修改的地址为："
                            + address.getProvince() + "，电话：" + address.getMobile());
                }
                oldAddress.setName(HtmlUtils.htmlEscape(address.getName().trim()));
                oldAddress.setLocation(HtmlUtils.htmlEscape(address.getLocation().trim()));
                oldAddress.setMobile(address.getMobile());
                oldAddress.setZipCode(address.getZipCode());
                addressService.updateAddress(oldAddress);
                /*final Address currentAddress = addressService.getAddress(address.getId(), sessionUserInfo.getId());
                new JsonResult(true).addData("address", JsonUtil.objectToJson(currentAddress)).toJson(response);*/
                new JsonResult(true).addData("address", JsonUtil.objectToJson(oldAddress)).toJson(response);
            }
        } catch (Exception e) {
            logger.error("修改地址信息：", e);
            new JsonResult(false, sessionUserInfo.getUserName() + "修改信息失败!").toJson(response);
        }

    }


    private boolean checkUserAddressIsValid(Address address, HttpServletResponse response) throws IOException {
        address.setLocation(HtmlUtils.htmlEscape(address.getLocation()));

        if (!WebSystemUtil.checkMobile(address.getMobile())) {
            new JsonResult(false, "手机号码输入有误").toJson(response);
            return false;
        }
        if (!WebSystemUtil.checkZipCode(address.getZipCode())) {
            new JsonResult(false, "邮政编码输入有误").toJson(response);
            return false;
        }
        if (address.getName().length() > 20) {
            new JsonResult(false, "收货人姓名长度不能超过20个字符范围").toJson(response);
            return false;
        }
        if (address.getLocation().length() > 200) {
            new JsonResult(false, "街道长度不能超过200个字符范围").toJson(response);
            return false;
        }
        return true;
    }


    private String getArea(String area) {
        if (StringUtils.isEmpty(area) || area.trim().equals("0")) {
            return "";
        } else {
            return "," + area;
        }
    }

    /**
     * 删除地址
     *
     * @throws IOException
     */
    @RequestMapping(value = "/user/address/delete", method = RequestMethod.POST)
    public void deleteAddress(int id, HttpServletResponse response, HttpServletRequest request) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        addressService.deleteAddress(id, sessionUserInfo.getId());
        new JsonResult(true).toJson(response);
    }


    /**
     * 查询剩下的不常用地址，在地址大于3的时候会返回数据
     *
     * @return
     */
    @RequestMapping(value = "/user/address/rest")
    public String queryTheRestAddress(HttpServletRequest request, Model model) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        List<Address> addressList = addressService.queryAllAddress(sessionUserInfo.getId());
        model.addAttribute("restAddress", addressList.subList(AddressConstants.Top_Max_Count, addressList.size()));
        return "restAddress";
    }

}
