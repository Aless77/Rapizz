package com.example.rapizz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private final int idUser;
    private final String nom;
    private final String prenom;
    private final String email;
    private final String adresse;
    private final String complementAdresse;
    private double solde;

    public static User getUserById(int idUser, Connection cnx) throws SQLException { // Méthode pour récupérer un utilisateur par son id, null si l'utilisateur n'existe pas
        String query = "SELECT * FROM user WHERE id_user = ?";

        // Utilisation de PreparedStatement pour sécuriser la requête
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, idUser); // Insertion de l'id user dans la requête

            // Exécution de la requête
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Création d'un objet User avec les informations récupérées
                    return new User(
                            rs.getInt("id_user"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("adresse"),
                            rs.getString("complement_adresse"),
                            rs.getDouble("solde")
                    );
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête, getUSerById : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public User(int idUser, String nom, String prenom, String email, String adresse, String complementAdresse, double solde) {
        // Initialisation des attributs
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
        this.complementAdresse = complementAdresse;
        this.solde = solde;
    }

    @Override
    public String toString() { // Méthode pour afficher les informations de l'utilisateur
        return nom + " " + prenom;
    }

    public int getIdUser() { // Méthode pour récupérer l'id de l'utilisateur
        return idUser;
    }

    public String getAdresse() { // Méthode pour récupérer l'adresse de l'utilisateur
        return adresse;
    }

    public String getComplementAdresse() { // Méthode pour récupérer le complément d'adresse de l'utilisateur
        return complementAdresse;
    }

    public String getInfoAdresse() { // Méthode pour récupérer l'adresse complète de l'utilisateur
        return adresse + " " + complementAdresse;
    }

    public String getEmail() { // Méthode pour récupérer l'email de l'utilisateur
        return email;
    }

    public double getSolde() { // Méthode pour récupérer le solde de l'utilisateur
        return solde;
    }

    public String showSolde() { // Méthode pour afficher le solde de l'utilisateur
        return solde + " €";
    }

    public void updateSolde(double montant, Connection cnx) throws SQLException { // Méthode pour mettre à jour le solde de l'utilisateur

        solde += montant; // Mise à jour du solde

        String query = "UPDATE user SET solde = ? WHERE id_user = ?";

        // Utilisation de PreparedStatement pour sécuriser la requête
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setDouble(1, montant); // Insertion du montant dans la requête
            pstmt.setInt(2, idUser); // Insertion de l'id user dans la requête

            // Exécution de la requête
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'exécution de la requête, updateSolde : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
