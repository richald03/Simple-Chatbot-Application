package com.example.simplechatbot;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private final String text;
    private final boolean isUser;
    private final String time;

    public ChatMessage(String text, boolean isUser, String time) {
        this.text = text;
        this.isUser = isUser;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }

    public String getTime() {
        return time;
    }
}
