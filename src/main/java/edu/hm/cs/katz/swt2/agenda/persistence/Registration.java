package edu.hm.cs.katz.swt2.agenda.persistence;

/**
 * Klasse für die Speicherung des Registrierschlüssels eines Topics.
 *
 * @author Fabian Rittmer (mailto: rittmeie@hm.edu)
 */
public class Registration {
    
    /**
     * JPA-kompatibler Kostruktor. Wird nur von JPA verwendet und darf private sein.
     */
    public Registration() {
        //Default-Konstruktor
    }
    
    private String key;
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
}
