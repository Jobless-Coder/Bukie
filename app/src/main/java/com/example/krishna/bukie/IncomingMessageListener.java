package com.example.krishna.bukie;

public interface IncomingMessageListener {
    public void receiveIncomingMessage(MessageItem c, String s);
    public void updateMessageStatus(MessageItem c);
}
