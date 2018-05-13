package com.example.siddh.talktry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by siddh on 24-Mar-18.
 */

public class chatMessage {

    private String messageText;
    private String messageUser;
    private String messageTime;
    private String userMailFormatted;

    public chatMessage(String messageText, String messageUser, String userMailFormatted ) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.userMailFormatted = userMailFormatted;

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());

        messageTime = formatter.format(calendar.getTime());
    }

    public chatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getUserMailFormatted() {
        return userMailFormatted;
    }

    public void setUserMailFormatted(String userMailFormatted) {
        this.userMailFormatted = userMailFormatted;
    }
}
