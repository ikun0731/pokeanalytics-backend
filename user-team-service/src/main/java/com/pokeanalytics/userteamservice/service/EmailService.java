package com.pokeanalytics.userteamservice.service;

public interface EmailService {
    /**
     * 发送简单的文本邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param text 邮件内容
     */
    void sendSimpleMail(String to, String subject, String text);
}