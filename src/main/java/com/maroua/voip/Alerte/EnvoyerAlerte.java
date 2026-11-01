package com.maroua.voip.Alerte;
import com.maroua.voip.notifications.Notification;
public class EnvoyerAlerte {
    public   void envoyerAlerte(Notification notification, String message) {
        notification.envoyerNotification(message, "@adminVoip.com");     
       
    }
}
