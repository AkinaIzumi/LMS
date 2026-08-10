package com.library.management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(messageBody);
        mailSender.send(message);
    }
    
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


    public void sendHoldAvailableEmail(String toEmail, String memberName, String bookTitle) {
        String subject = "📚 Your Book Hold is Available!";
        String message = "Hello " + memberName + ",\n\n"
                + "The book \"" + bookTitle + "\" you placed on hold is now available at the library for pickup.\n"
                + "Please come and collect it within the specified period.\n\n"
                + "Thank you,\nLibrary Management System";
        sendEmail(toEmail, subject, message);
    }

    public void sendHoldCancelledEmail(String toEmail, String memberName, String bookTitle) {
        String subject = "⚠️ Book Hold Cancelled Notice";
        String message = "Hello " + memberName + ",\n\n"
                + "We regret to inform you that your hold for the book \"" + bookTitle + "\" has been cancelled because the book was reported lost or damaged.\n\n"
                + "We apologize for the inconvenience.\n\n"
                + "Thank you,\nLibrary Management System";
        sendEmail(toEmail, subject, message);
    }
}