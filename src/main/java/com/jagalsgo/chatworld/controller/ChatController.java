package com.jagalsgo.chatworld.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @GetMapping("/freeChatting")
    public String freeChatting(){
        return "chatting";
    }

}
