package com.example.rapizz;

public class Client extends User {
    
    private final int nbCommande;
    
    public Client (int idUser, String nom, String prenom, String email, String adresse, String complementAdresse, Double solde, boolean isOperateur, int nbCommande) {
        super(idUser, nom, prenom, email, adresse, complementAdresse, solde, isOperateur);
        this.nbCommande = nbCommande;
    }

    public String getNom() {
        return super.getNom();
    }

    public String getPrenom() {
        return super.getPrenom();
    }

    public String getEmail() {
        return super.getEmail();
    }

    public int getNbCommande() {
        return nbCommande;
    }
}
