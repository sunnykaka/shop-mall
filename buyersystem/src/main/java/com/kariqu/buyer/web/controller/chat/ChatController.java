package com.kariqu.buyer.web.controller.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User: Asion
 * Date: 13-2-26
 * Time: 下午5:56
 */
@Controller
public class ChatController {


    @RequestMapping(value = "/chat")
    public String chat() {
        return "chat";
    }



}
