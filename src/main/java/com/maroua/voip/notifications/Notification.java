package com.maroua.voip.notifications;

public class Notification {
   
     public Notification() {
       
    }
   public  void envoyerNotificationParEmail(String message, String destinataire){
         System.out.println("Envoi d'un email à " + destinataire + " avec le message : " + message);
    }

   public   void envoyerNotificationViaConsole(String message, String destinataire){
       System.out.println("Envoi d'une notification via console à " + destinataire + " avec le message : " + message);
    }
}
