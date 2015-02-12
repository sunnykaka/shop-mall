package com.kariqu.omcenter.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Lottery;
import com.kariqu.tradecenter.domain.Rotary;
import com.kariqu.tradecenter.domain.RotaryMeed;
import com.kariqu.tradecenter.service.RotaryLotteryService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/rotary")
public class RotaryController {

    @Autowired
    private RotaryLotteryService rotaryLotteryService;

    @RequestMapping(value = "/list")
    public void list(HttpServletResponse response) throws IOException {
        try {
            List<Rotary> rotaryList = rotaryLotteryService.queryAllRotary();
            new JsonResult(true).addData("totalCount", rotaryList.size()).addData("result", rotaryList).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public void add(Rotary rotary, HttpServletResponse response) throws IOException {
        try {
            rotaryLotteryService.insertRotary(rotary);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(Rotary rotary, HttpServletResponse response) throws IOException {
        try {
            rotaryLotteryService.updateRotary(rotary);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void delete(Integer[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int rotaryId : ids) {
                rotaryLotteryService.deleteRotary(rotaryId);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }



    @RequestMapping(value = "/meed/{rotaryId}")
    public void meedList(@PathVariable("rotaryId") Integer rotaryId, HttpServletResponse response) throws IOException {
        try {
            List<RotaryMeed> meedList = rotaryLotteryService.queryAllMeedByRotaryIdOrderByIndex(rotaryId);
            new JsonResult(true).addData("totalCount", meedList.size()).addData("result", meedList).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/meed/add")
    public void addMeed(RotaryMeed meed, HttpServletResponse response) throws IOException {
        try {
            rotaryLotteryService.insertMeed(meed);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/meed/update")
    public void updateMeed(RotaryMeed meed, HttpServletResponse response) throws IOException {
        try {
            rotaryLotteryService.updateMeed(meed);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/meed/delete")
    public void deleteMeed(Integer[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int meedId : ids) {
                rotaryLotteryService.deleteMeed(meedId);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }



    @RequestMapping(value = "/lottery/{rotaryId}")
    public void lotteryList(@PathVariable("rotaryId") Integer rotaryId, String userName, Boolean needSend,
                            Boolean sendOut, Integer start, Integer limit, HttpServletResponse response) throws IOException {
        try {
            Page<Lottery> pageLottery = rotaryLotteryService.queryLotteryByQuery(rotaryId, userName,
                    needSend, sendOut, new Page<Lottery>(start / limit + 1, limit));
            new JsonResult(true).addData("totalCount", pageLottery.getTotalCount()).addData("result", pageLottery.getResult()).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/lottery/sendOut")
    public void sendOutLottery(Integer[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int lotteryId : ids) {
                rotaryLotteryService.sendOutLottery(lotteryId);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/lottery/update")
    public void updateLotteryConsignee(Integer id, String consigneeName, String consigneePhone,
                                       String consigneeAddress, HttpServletResponse response) throws IOException {
        try {
            Lottery lottery = rotaryLotteryService.queryLotteryById(id);
            if (StringUtils.isNotBlank(consigneeName)) lottery.setConsigneeName(consigneeName);
            if (StringUtils.isNotBlank(consigneePhone)) lottery.setConsigneePhone(consigneePhone);
            if (StringUtils.isNotBlank(consigneeAddress)) lottery.setConsigneeAddress(consigneeAddress);
            rotaryLotteryService.updateLottery(lottery);

            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/lottery/add/{rotaryId}")
    public String addLottery(@PathVariable("rotaryId") Integer rotaryId, Model model) throws IOException {
        Rotary rotary = rotaryLotteryService.queryRotaryById(rotaryId);
        if (rotary == null) {
            model.addAttribute("noRotary", true);
        } else {
            model.addAttribute("rotaryId", rotaryId);
            model.addAttribute("meedList", rotaryLotteryService.queryAllMeedByRotaryIdOrderByIndex(rotaryId));
        }
        return "lottery";
    }

    @RequestMapping(value = "/lottery/addMen")
    public void insertLottery(Integer rotaryId, String lotteryInfo, HttpServletResponse response) throws IOException {
        Rotary rotary = rotaryLotteryService.queryRotaryById(rotaryId);
        if (rotary == null) {
            new JsonResult(false, "无此抽奖信息").toJson(response);
            return;
        }
        if (StringUtils.isBlank(lotteryInfo)) {
            new JsonResult(false, "未填写中奖数据").toJson(response);
            return;
        }
        try {
            int i = 0;
            long time = System.currentTimeMillis();
            Date dt = new Date(time);
            for (String str : lotteryInfo.split(System.getProperty("line.separator"))) {
                if (StringUtils.isBlank(str)) continue;

                String[] nameAndMeed = str.split("\\|");
                if (nameAndMeed.length < 2) {
                    new JsonResult("第 " + (i + 1) + " 行格式不正确!");
                    return;
                }
                // 若有多条, 时间不需要一致, 有必要间隔
                if (i > 0) {
                    dt = new Date(time - (new Random().nextInt(60) * 1000));
                }
                rotaryLotteryService.insertMenLottery(rotaryId, nameAndMeed[0].trim(), NumberUtils.toInt(nameAndMeed[1].trim()), dt);

                i++;
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

}
