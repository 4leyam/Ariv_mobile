package cg.code.aleyam.nzela_nzela.data_service;

public class Comment_obj {

    String nom_utilisateur = null;
    String commentaire_texte = null;
    int avis = 0;
    int id_agence = 0;

    public Comment_obj() {

    }

    public void setAvis(int avis) {
        this.avis = avis;
    }

    public int getAvis() {

        return avis;
    }

    public Comment_obj(String pseudo , String contenu_commentaire , int avis) {
        this.nom_utilisateur = pseudo;
        this.commentaire_texte = contenu_commentaire;
        this.avis = avis;

    }
    public Comment_obj(String pseudo , String contenu_commentaire , int avis , int id_agence) {
        this.nom_utilisateur = pseudo;
        this.commentaire_texte = contenu_commentaire;
        this.avis = avis;
        this.id_agence = id_agence;

    }

    public int getId_agence() {
        return id_agence;
    }

    public void setId_agence(int id_agence) {
        this.id_agence = id_agence;
    }

    public void setPseudo(String pseudo) {
        this.nom_utilisateur = pseudo;
    }

    public void setCommentaire_texte(String commentaire_texte) {
        this.commentaire_texte = commentaire_texte;
    }

    public String getPseudo() {

        return nom_utilisateur;
    }

    public String getCommentaire_texte() {
        return commentaire_texte;
    }
}