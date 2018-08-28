package cg.code.aleyam.nzela_nzela.actu.connexion;


import java.util.Date;

public class Post_ancestor {

    protected String image , owner , post_text;
    protected float pertinance = 0  ;
    protected double lat ;


    public Post_ancestor () {

    }

    public Post_ancestor(
              String image
            , String owner
            , String post_text
            , float pertinance

            , double lat

            ) {

        this.image = image;
        this.owner = owner;
        this.post_text = post_text;
        this.pertinance = pertinance;
        this.lat = lat;



    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPost_text() {
        return post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public float getPertinance() {
        return pertinance;
    }

    public void setPertinance(float pertinance) {
        this.pertinance = pertinance;
    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

//    public double getLng() {
//        return lng;
//    }
//
//    public void setLng(double lng) {
//        this.lng = lng;
//    }





}