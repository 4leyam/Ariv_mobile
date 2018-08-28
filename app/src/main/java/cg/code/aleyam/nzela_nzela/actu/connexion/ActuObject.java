package cg.code.aleyam.nzela_nzela.actu.connexion;

import java.text.DateFormat;
import java.util.Date;

public class ActuObject extends Post_ancestor {


    private long date ;
    private int type = 0;
    private double longitude;
    private long last;
    private long votes , total_votes;


    public ActuObject() {

    }



    public ActuObject(String image
            , String owner
            , String post_text
            , float pertinance

            , double lat
            , double lng
            , long date
            , int type
            , long last
            , long votes
            , long total_votes ) {

        super( image , owner , post_text , pertinance , lat );
        this.date = date;
        this.type = type;
        this.longitude = lng;
        this.last = last;
        this.votes = votes;
        this.total_votes = total_votes;

    }




    public long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setLng(double longitude) {
        this.longitude = longitude;
    }

    public double getLng() {
        return longitude;
    }

    public long getLast() {
        return last;
    }

    public long getVotes() {
        return votes;
    }


    public void setLast(long last) {
        this.last = last;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }

    public long getTotal_votes() {
        return total_votes;
    }

    public void setTotal_votes(long total_votes) {
        this.total_votes = total_votes;
    }

    public Post_ancestor getAncestor() {

        return new Post_ancestor(
                super.image ,
                super.owner ,
                super.post_text ,
                super. pertinance ,
                super.lat
                );

    }
    public EventObject getEvent() {
        return new EventObject(
                image ,
                owner ,
                post_text ,
                pertinance ,
                lat ,
                longitude ,
                last ,
                total_votes ,
                votes);
    }

}
