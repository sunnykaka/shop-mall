package com.kariqu.productmanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.productcenter.domain.AttentionInfo;
import com.kariqu.productcenter.service.AttentionInfoService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 使用注意事项以及保养注意管理
 * User: wendy
 * Date: 12-7-19
 * Time: 下午3:13
 */
@Controller
public class AttentionInfoController {

    private final Log logger = LogFactory.getLog(AttentionInfoController.class);

    @Autowired
    private AttentionInfoService attentionInfoService;

    /**
     * 根据商品Id查出使用注意信息列表
     * @param productId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/attentionInfo/useList/{productId}")
    public void queryUseListByproductId(@PathVariable("productId") int productId,HttpServletResponse response) throws IOException {
        List<AttentionInfo> UseList = attentionInfoService.queryAllUseByProductId(productId);
        new JsonResult(true).addData("UseList", UseList).toJson(response);
    }

    /**
     * 根据商品Id查出保养注意信息列表
     * @param productId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/attentionInfo/maintenanceList/{productId}")
    public void queryMaintenanceListByproductId(@PathVariable("productId") int productId,HttpServletResponse response) throws IOException {
        List<AttentionInfo> maintenanceList = attentionInfoService.queryAllMaintenanceByProductId(productId);
        new JsonResult(true).addData("maintenanceList", maintenanceList).toJson(response);
    }

    /**
     * 添加相关的注意实现
     * @param attentionInfo
     * @param info
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/attentionInfo/create", method = RequestMethod.POST)
    public void createAttention(AttentionInfo attentionInfo,String[] info, HttpServletResponse response) throws IOException {
        try{
            for(String in:info){
                if(!in.trim().equals("")){
                    attentionInfo.setInfo(in);
                    attentionInfoService.createAttention(attentionInfo);
                }
            }
        }catch (Exception e){
            logger.error("商品管理的添加相关注意信息异常："+e);
            new JsonResult(false,"添加相关信息出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 更新相关的注意事项
     * @param attentionInfo
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/attentionInfo/update", method = RequestMethod.POST)
    public void updateAttention(AttentionInfo attentionInfo, HttpServletResponse response) throws IOException {
        try{
            attentionInfoService.updateAttention(attentionInfo);
        }catch (Exception e){
            logger.error("商品管理的更新相关注意事项异常："+e);
            new JsonResult(false,"更新相关信息出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 删除相关的注意事项
     * @param ids
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/attentionInfo/delete")
    public void deleteAttentionById(int[] ids, HttpServletResponse response) throws IOException {
        try{
            for(int id:ids){
                attentionInfoService.deleteAttentionById(id);
            }
        }catch (Exception e){
            logger.error("商品管理的删除相关注意事项异常："+e);
            new JsonResult(false,"删除相关信息出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }
}
