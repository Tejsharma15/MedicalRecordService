package com.example.EMR.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.EMR.models.Patient;

import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // public void sendHtmlEmail(String to, String subject) throws MessagingException {
    //     MimeMessage message = mailSender.createMimeMessage();

    //     message.setFrom(new InternetAddress("hadproject75@gmail.com"));
    //     message.setRecipients(MimeMessage.RecipientType.TO, to);
    //     message.setSubject(subject);

    //     String htmlContent = "<h1>This is a test Spring Boot email</h1>" +
    //             "<p>It can contain <strong>HTML</strong> content.</p>";
    //     message.setContent(htmlContent, "text/html; charset=utf-8");

    //     mailSender.send(message);
    // }

    public void sendHtmlEmail(String to, String subject, String htmlFileName, String publicId, Patient.PatientType patientType ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
    
        message.setFrom(new InternetAddress("hadproject75@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        System.out.println("Sending email to: " + to);
        message.setSubject(subject);
    
        String htmlContent = "";
        try {
            htmlContent = new String(Files.readAllBytes(Paths.get("src/main/java/com/his/his/services/Email HTML pages/" + htmlFileName)));
            htmlContent = htmlContent.replace("{publicId}", publicId);
            htmlContent = htmlContent.replace("{patientType}", patientType.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        message.setContent(htmlContent, "text/html; charset=utf-8");
    
        mailSender.send(message);
    }

    public void sendAddConsultationEmail(String to, String doctorId, String consultationId,String doctorName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
    
        message.setFrom(new InternetAddress("hadproject75@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        System.out.println("Sending email to: " + to);
        message.setSubject("Consultation Successfully Added");

        String htmlContent = "";
        try {
            htmlContent = new String(Files.readAllBytes(Paths.get("src/main/java/com/example/EMR/service/Email HTML pages/AddedConsultation.html" )));
            htmlContent = htmlContent.replace("{doctorName}", doctorName);
            htmlContent = htmlContent.replace("{consultationId}", consultationId);
            htmlContent = htmlContent.replace("{doctorId}", doctorId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    
        message.setContent(htmlContent, "text/html; charset=utf-8");
    
        mailSender.send(message);
    
    }

    public void sendDoctorReassignedEmail(String to, String doctorId, String patientId, String doctorName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
    
        message.setFrom(new InternetAddress("hadproject75@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        System.out.println("Sending email to: " + to);
        message.setSubject("Doctor Reassigned");
    
        String htmlContent = "";
        try {
            htmlContent = new String(Files.readAllBytes(Paths.get("src/main/java/com/example/EMR/service/Email HTML pages/DoctorReassigned.html")));
            htmlContent = htmlContent.replace("{doctorName}", doctorName);
            htmlContent = htmlContent.replace("{patientId}", patientId);
            htmlContent = htmlContent.replace("{doctorId}", doctorId);
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        message.setContent(htmlContent, "text/html; charset=utf-8");
    
        mailSender.send(message);
    }

}
