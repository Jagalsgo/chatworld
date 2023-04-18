    package com.jagalsgo.chatworld.websocket;

    import lombok.RequiredArgsConstructor;
    import org.springframework.messaging.handler.annotation.DestinationVariable;
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.messaging.handler.annotation.Payload;
    import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.messaging.simp.annotation.SubscribeMapping;
    import org.springframework.stereotype.Controller;

    @Controller
    @RequiredArgsConstructor
    public class ChatMessageController {
        private final SimpMessagingTemplate simpMessagingTemplate;

        @SubscribeMapping("/topic/chat/{chatId}")
        public void joinChat(@DestinationVariable Long chatId, @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
            if (chatMessage.getSender() == null) {
                throw new IllegalArgumentException("Sender cannot be null");
            }
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        }

        @MessageMapping("/chat.leave/{chatId}")
        public void leaveChat(@DestinationVariable Long chatId, SimpMessageHeaderAccessor headerAccessor) {
            headerAccessor.getSessionAttributes().remove("username");
        }

        @MessageMapping("/chat.send/{chatId}")
        public void sendMessage(@DestinationVariable Long chatId, @Payload ChatMessage chatMessage) {
            // 새로운 채팅 메시지를 받았을 때 호출되는 메소드
            // chatId: 채팅방 아이디
            // chatMessage: 채팅 메시지 객체
            simpMessagingTemplate.convertAndSend("/topic/chat/" + chatId, chatMessage);
        }

    }
