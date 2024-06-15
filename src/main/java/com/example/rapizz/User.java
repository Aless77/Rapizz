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
    private final boolean isOperateur;
    private double solde;
    private int telephone;
    private int nbPizzaBuy;

    public static User getUser(String email, String password, Connection cnx) throws SQLException { // Méthode pour récupérer un utilisateur par son id, null si l'utilisateur n'existe pas
        String query = "SELECT * FROM user WHERE email = ? AND password = ?";

        // Utilisation de PreparedStatement pour sécuriser la requête
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

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
                            rs.getDouble("solde"),
                            rs.getBoolean("operateur"),
                            rs.getInt("telephone")
                    );
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête, getUSerById : " + e.getMessage());
                return null;
            }
        }

        return null;
    }

    public User(int idUser, String nom, String prenom, String email, String adresse, String complementAdresse, double solde, boolean isOperateur, int telephone) {
        // Initialisation des attributs
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
        this.complementAdresse = complementAdresse;
        this.solde = solde;
        this.isOperateur = isOperateur;
        this.telephone = telephone;
    }

    public int calculateNbPizzaBuy(Connection cnx) throws SQLException {
        String query = "SELECT SUM(pd.quantite) AS nbPizza " +
                "FROM commande AS c JOIN produitsvendus AS pd ON pd.id_commande = c.id_commande " +
                "WHERE c.id_client = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, idUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.nbPizzaBuy = rs.getInt("nbPizza");
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'exécution de la requête, calculateNbPizzaBuy : " + e.getMessage());
                return 0;
            }
        }
        return 0;
    }

    public String getNom() { // Méthode pour récupérer le nom de l'utilisateur
        return nom;
    }

    public String getPrenom() { // Méthode pour récupérer le prénom de l'utilisateur
        return prenom;
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

    public boolean isOperateur() { // Méthode pour savoir si l'utilisateur est un opérateur
        return isOperateur;
    }

    public int getTelephone() { // Méthode pour récupérer le numéro de téléphone de l'utilisateur
        return telephone;
    }

    public String showSolde() { // Méthode pour afficher le solde de l'utilisateur
        return solde + " €";
    }

    public int getNbPizzaBuy() {
        return nbPizzaBuy;
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

