package cg.code.aleyam.nzela_nzela.data_service;

import java.util.ArrayList;

public class Commentaire_data {
    private String status ;
    private String message;
    private ArrayList<Comment_obj> commentaires;

    public Commentaire_data() {

    }
    public Commentaire_data(ArrayList<Comment_obj> commentaires) {
        this.commentaires = commentaires;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCommentaire(ArrayList<Comment_obj> commentaires) {
        this.commentaires = commentaires;
    }

    public String getStatus() {

        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Comment_obj> getCommentaire() {


        return commentaires;
    }
}