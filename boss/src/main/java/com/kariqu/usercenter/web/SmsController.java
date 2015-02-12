package com.kariqu.usercenter.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.MessageTask;
import com.kariqu.usercenter.domain.SmsCharacter;
import com.kariqu.usercenter.domain.SmsMould;
import com.kariqu.usercenter.service.MessageTaskService;
import com.kariqu.usercenter.service.SmsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-8-8
 * Time: 上午11:42
 */
@Controller
public class SmsController {

    private final Log logger = LogFactory.getLog(SmsController.class);

    @Autowired
    private SmsService smsService;

    @Autowired
    private MessageTaskService messageTaskService;

    @RequestMapping(value = "/sms/smsMould/list")
    public void querySmsMouIdList(HttpServletResponse response) throws IOException {
        List<SmsMould> smsMouldList=smsService.querySmsMouIdList();
        new JsonResult(true).addData("totalCount", smsMouldList.size()).addData("result", smsMouldList).toJson(response);
    }

    @RequestMapping(value = "/sms/smsMould/listComboBox")
    public void querySmsMouIdListComboBox(HttpServletResponse response) throws IOException {
        List<SmsMould> smsMouldList=smsService.querySmsMouIdList();
        new JsonResult(true).addData("result", smsMouldList).toJson(response);
    }

    /**
     * 根据短信模板Id 获取短信模板内容
     * @param response
     * @param id
     * @throws IOException
     */
    @RequestMapping(value = "/sms/smsMould/content/{id}")
    public void getSmsMouldById(HttpServletResponse response, @PathVariable("id") int id) throws IOException {
        SmsMould smsMould = smsService.getSmsMouldById(id);
        new JsonResult(true).addData("content", smsMould.getContent()).toJson(response);
    }

    @RequestMapping(value = "/sms/smsMould/add",method = RequestMethod.POST)
    public void smsMouldAdd(SmsMould smsMould,HttpServletResponse response) throws IOException {
        try{
            smsService.createSmsMould(smsMould);
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error("添加短信模板异常"+e);
            new JsonResult(false,"添加短息模板出错").toJson(response);
        }
    }

    @RequestMapping(value = "/sms/smsMould/update",method = RequestMethod.POST)
    public void smsMouldUpdate(SmsMould smsMould,HttpServletResponse response) throws IOException {
        try{
            SmsMould smsMouldVo=smsService.getSmsMouldById(smsMould.getId());
            smsMouldVo.setContent(smsMould.getContent());
            smsMouldVo.setDescription(smsMould.getDescription());
            smsService.updateSmsMould(smsMouldVo);
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error("修改短信模板异常"+e);
            new JsonResult(false,"修改短息模板出错").toJson(response);
        }
    }

    @RequestMapping(value = "/sms/smsMould/delete", method = RequestMethod.POST)
    public void deleteUserById(int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                smsService.deleteSmsMouldById(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除短信模板异常：" + e);
            new JsonResult(false, "删除短信模板失败").toJson(response);
        }
    }


    @RequestMapping(value = "/sms/smsCharacter/list")
    public void querySmsCharacterList(HttpServletResponse response) throws IOException {
        List<SmsCharacter> smsCharacterList=smsService.querySmsCharacterList();
        new JsonResult(true).addData("totalCount", smsCharacterList.size()).addData("result", smsCharacterList).toJson(response);
    }

    @RequestMapping(value = "/sms/smsCharacter/listComboBox")
    public void querySmsCharacterListComboBox(HttpServletResponse response) throws IOException {
        List<SmsCharacter> smsCharacterList=smsService.querySmsCharacterList();
        new JsonResult(true).addData("result", smsCharacterList).toJson(response);
    }

    @RequestMapping(value = "/sms/smsCharacter/add",method = RequestMethod.POST)
    public void SmsCharacterAdd(SmsCharacter smsCharacter,HttpServletResponse response) throws IOException {
       try{
           smsService.createSmsCharacter(smsCharacter);
           new JsonResult(true).toJson(response);
       }catch (Exception e){
          logger.error("添加短信字符异常"+e);
          new JsonResult(false,"添加短息字符出错").toJson(response);
       }
    }

    @RequestMapping(value = "/sms/smsCharacter/update",method = RequestMethod.POST)
    public void smsCharacterUpdate(SmsCharacter smsCharacter,HttpServletResponse response) throws IOException {
        try{
            SmsCharacter smsCharacterVo=smsService.getSmsCharacterById(smsCharacter.getId());
            smsCharacterVo.setValue(smsCharacter.getValue());
            smsCharacterVo.setName(smsCharacter.getName());
            smsService.updateSmsCharacter(smsCharacterVo);
            new JsonResult(true).toJson(response);
        }catch (Exception e){
            logger.error("修改短信字符异常"+e);
            new JsonResult(false,"修改短息字符出错").toJson(response);
        }
    }

    @RequestMapping(value = "/sms/smsCharacter/delete", method = RequestMethod.POST)
    public void deleteSmsCharacterById(int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                SmsCharacter smsCharacter=smsService.getSmsCharacterById(id);
                if(smsCharacter.getValue().equals("mobile") || smsCharacter.getValue().equals("sendTime")){
                    new JsonResult(false, "电话和发送时间不允许删除").toJson(response);
                    return;
                }
                smsService.deleteSmsCharacterById(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除短信字符异常：" + e);
            new JsonResult(false, "删除短信字符失败").toJson(response);
        }
    }


    @RequestMapping(value = "/sms/messageTask/list")
    public void querySendMessageTaskBySmsPage(@RequestParam("start") int start, @RequestParam("limit") int limit,HttpServletResponse response) throws IOException {
        Page<MessageTask> messageTaskPage=messageTaskService.querySendMessageTaskBySmsPage(new Page<MessageTask>(start / limit + 1, limit));
        new JsonResult(true).addData("totalCount", messageTaskPage.getTotalCount()).addData("result", messageTaskPage.getResult()).toJson(response);
    }

    @RequestMapping(value = "/sms/messageTask/delete", method = RequestMethod.POST)
    public void deleteMessageTaskById(int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                messageTaskService.deleteMessageTaskById(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除短信历史记录异常：" + e);
            new JsonResult(false, "删除短信历史记录失败").toJson(response);
        }
    }
}
