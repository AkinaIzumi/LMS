package com.library.management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOverdueEmail(String toEmail, String memberName, String bookTitle, String dueDate, double fineAmount) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject("Library Book Overdue Notice");
    message.setText("Dear " + memberName + ",\n\n" +
            "Your borrowed book \"" + bookTitle + "\" was due on " + dueDate + ".\n" +
            "It is now overdue. A late fine of " + fineAmount + " MMK has been charged.\n\n" +
            "Please return the book as soon as possible.\n\n" +
            "Best regards,\nUCSP Library Management System");
    
    mailSender.send(message);
}
}