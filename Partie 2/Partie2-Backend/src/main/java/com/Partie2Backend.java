/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com;

import com.model.Task;
import com.service.TaskService;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */

public class Partie2Backend {

    public static void main(String[] args) {

        // Création des tâches
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1,"Corriger le bug login","Auth",1, true,"Depend d'une API externe en maintenance"));
        tasks.add(new Task(2,"Ajouter les tests","Auth",2,false,null));
        tasks.add(new Task(3,"Optimiser les requetes","Backend",2, true,"En attente de validation DBA"));
        tasks.add(new Task(4,"Creer le dashboard","Frontend",3,true,"Maquette non approuvee"));
        tasks.add(new Task(5,"Ajouter les logs","Backend",1,true,"Serveur de logs indisponible"));
        tasks.add(new Task(6,"Rediger la doc","Backend",3, false, null));


        System.out.println("Taches bloquees:");
        List<Task> blocked = TaskService.getBlockedTasksSorted(tasks);
        for (Task t : blocked) {
            System.out.println(t);
        }

        // Exemple 2 : Blocage valide 
        System.out.println("\n Blocage valide");
        Task t7 = new Task(7,"Migrer la base", "Infra", 2);
        t7.setBlocked(true, "Serveur inaccessible");
        System.out.println(t7);


        // Exemple 3 : Déblocage 
        System.out.println("\n Deblocage");
        t7.setBlocked(false, null);
        System.out.println(t7);


        // Exemple 4 : Blocage invalide - raison vide 
        System.out.println("\n Blocage invalide (raison vide)");
        Task t8 = new Task(8,"Deployer en prod", "Infra", 1);
        try {
            t8.setBlocked(true, "   ");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        }


        // Exemple 5 : Blocage invalide - raison null
        System.out.println("\n Blocage invalide (raison null) ");
        try {
            t8.setBlocked(true, null);
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
