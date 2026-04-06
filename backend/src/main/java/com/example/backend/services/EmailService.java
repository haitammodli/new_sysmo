package com.example.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendModificationNotification(String toEmail, String numeroExpedition, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sysmo-noreply@sdtm.com");
        message.setTo(toEmail);
        message.setSubject("Mise à jour Sysmo : Expédition " + numeroExpedition);

        String detail = status.equals("APPROUVEE")
                ? "a été validée et appliquée."
                : "a été refusée par l'agent.";

        message.setText("Bonjour,\n\nVotre demande de modification pour l'expédition "
                + numeroExpedition + " " + detail + "\n\nCordialement,\nL'équipe SDTM.");

        mailSender.send(message);
    }
    @Async
    public void notifierAgentNouveauDemande(String[] emailsAgents, String numeroExpedition, String typeModif) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sysmo-system@sdtm.com");
        message.setTo(emailsAgents);
        message.setSubject("ALERTE : Nouvelle demande de modification - " + numeroExpedition);

        message.setText("Bonjour,\n\nUne nouvelle demande de modification a été soumise pour l'expédition : "
                + numeroExpedition + ".\n"
                + "Type de modification : " + typeModif + "\n\n"
                + "Merci de vous connecter au dashboard Sysmo pour traiter cette demande.\n"
                + "L'équipe Système SDTM.");

        mailSender.send(message);
    }
   // @Async // Essential to keep the app fast
    public void notifierAgentIndividuel(String emailDestinataire, String numeroExpedition, String typeModif) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("sysmo-noreply@sdtm.ma");
            message.setTo(emailDestinataire); // Sending to one specific person
            message.setSubject("ALERTE : Nouvelle demande Sysmo - " + numeroExpedition);

            message.setText("Bonjour,\n\nUne nouvelle demande de modification (" + typeModif + ") "
                    + "a été soumise pour l'expédition N° " + numeroExpedition + ".\n"
                    + "Veuillez vous connecter pour la traiter.\n\nL'équipe Système SDTM.");

            mailSender.send(message);
            System.out.println("DEBUG: Email individuel envoyé avec succès à " + emailDestinataire);
        } catch (Exception e) {
            System.err.println("DEBUG: Erreur d'envoi à " + emailDestinataire + " : " + e.getMessage());
        }
    }
}
