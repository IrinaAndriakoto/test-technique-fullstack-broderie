/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.model;

/**
 *
 * @author ASUS
 */
public class Task {
    private int id;
    private String name;
    private String project;
    private int priority;
    private boolean isBlocked;
    private String blocked_reason;

    //constructeur vide
    public Task(){}
    
    //constructeur simple => tache non bloquee par defaut
    public Task(int id,String name, String project, int priority) {
        this.id = id;
        this.name = name;
        this.project = project;
        this.priority = priority;
        this.isBlocked = false;
        this.blocked_reason = null;
    }
    
    //const complet
    public Task(int id,String name, String project, int priority, boolean isBlocked, String blocked_reason) {
        this.id = id;
        this.name = name;
        this.project = project;
        this.priority = priority;
        this.isBlocked = isBlocked;
        this.blocked_reason = blocked_reason;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getBlocked_reason() {
        return blocked_reason;
    }

    public void setBlocked_reason(String blocked_reason) {
        this.blocked_reason = blocked_reason;
    }
    
    public void setBlocked(boolean isBlocked, String blocked_reason) {
        if (isBlocked && (blocked_reason == null || blocked_reason.trim().isEmpty())) {
            throw new IllegalArgumentException(
                "Une raison de blocage est obligatoire quand la tache est bloquee."
            );
        }

        this.isBlocked = isBlocked;

        // Si on débloque, on efface la raison automatiquement
        if (isBlocked == true) {
            this.blocked_reason = blocked_reason;
        } else {
            this.blocked_reason = null;
        }
    }
    
    @Override
    public String toString() {
        String raison = (blocked_reason != null) ? blocked_reason : "-";
        return "id: "+id
             + " [" + name + "]"
             + "| Projet: " + project
             + " | Priorite: " + priority
             + " | Bloquee: " + isBlocked
             + " | Raison: " + raison;
    }
    
}
