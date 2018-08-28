package cg.code.aleyam.nzela_nzela.actu.connexion;

import java.text.DateFormat;
import java.util.Date;

public class EventObject extends Post_ancestor {

//Long date = null;

    double lng ;
    private long last;
    private long votes , total_votes;
public EventObject() {

}

    public EventObject(String image
         , String owner
         , String post_text
         , float pertinance
         , double lat
         , double lng
         , long last
         , long total_votes
         , long votes

         ) {

    super( image , owner , post_text , pertinance , lat );
    this.lng = lng;
    this.last = last;
    this.total_votes = total_votes;
    this.votes = votes;
//    this.date = date;

 }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
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

    //    public Long getDate() {
//        return date;
//    }
//
//    public void setDate(Long date) {
//        this.date = date;
//    }



}


