package com.jagalsgo.chatworld.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    static final String[] team1 = {"울산", "전북", "포항", "인천", "제주", "강원", "수원FC", "대구", "FC서울", "수원삼성", "광주", "대전"};
    static final String[] team2 = {"김천", "성주", "안양", "부천", "경남", "아산", "서울이랜드", "김포", "안산", "부산", "전남", "대전"};

    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("team1", team1);
        model.addAttribute("team2", team2);
        return "index";
    }

}
