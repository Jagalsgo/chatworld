package com.jagalsgo.chatworld.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    public enum MessageType {
        JOIN,
        LEAVE,
        CHAT
    }

    private MessageType type;
    private String sender;
    private String content;

}
