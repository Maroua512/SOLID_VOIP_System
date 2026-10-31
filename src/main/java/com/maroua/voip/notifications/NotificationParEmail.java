package com.maroua.voip.notifications;

public class NotificationParEmail  implements Notification {
    
    @Override
    public void envoyerNotification(String message, String destinataire) {
        System.out.println("Envoi d'un email à " + destinataire + " avec le message : " + message);
    }
    
}
