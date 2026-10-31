package com.maroua.voip.notifications;

public class NotificationViaConsole implements Notification {
    
    @Override
    public void envoyerNotification(String message, String destinataire) {
        System.out.println("Envoi d'une notification via console à " + destinataire + " avec le message : " + message);
    }
    
}
