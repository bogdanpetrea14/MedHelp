package com.mobylab.springbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPrescriptionEmail(String toEmail, String patientName, String prescriptionCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@medconnect.com"); // Mailtrap ignoră asta oricum, dar Spring o cere
        message.setTo(toEmail);
        message.setSubject("Rețetă nouă emisă - MedConnect");
        message.setText("Salut " + patientName + ",\n\n" +
                "Doctorul tău a emis o rețetă nouă pentru tine.\n" +
                "Codul unic al rețetei este: " + prescriptionCode + "\n\n" +
                "Te poți prezenta la orice farmacie parteneră cu acest cod pentru a ridica medicamentele.\n\n" +
                "Sănătate multă,\nEchipa MedConnect");

        mailSender.send(message);
    }
}