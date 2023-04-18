package com.jagalsgo.chatworld.controller;

import com.jagalsgo.chatworld.entity.Team;
import com.jagalsgo.chatworld.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final TeamRepository teamRepository;

    @GetMapping("/{chatId}")
    public String chatting(@PathVariable("chatId") int chatId, Model model) throws IOException, ExecutionException, InterruptedException {

        SockJsClient sockJsClient = new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient())));
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        stompClient.setMessageConverter(new StringMessageConverter());
        stompClient.connect("ws://localhost:8080/chatWebsocket", new StompSessionHandlerAdapter() {});

        // chatId에 해당하는 채팅방 정보를 가져옴
        Team team = teamRepository.getTeamById(chatId);
        model.addAttribute("team", team);

        return "chatting";
    }

}
