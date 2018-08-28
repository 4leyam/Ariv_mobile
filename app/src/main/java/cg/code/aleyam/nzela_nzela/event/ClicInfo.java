package cg.code.aleyam.nzela_nzela.event;


public class ClicInfo {

    public String price = null , depart = null , formalite = null , y_tarif = null , remain = null , id_depart , bus_url , couple , agence , date;

    public ClicInfo (String price
            , String formalite
            , String depart
            , String y_tarif
            , String remain
            , String id_depart
            , String bus_url
            , String couple_localite
            , String agence
            , String date) {

       this.price = price;
       this.depart = depart;
       this.formalite = formalite;
       this.y_tarif = y_tarif;
       this.remain = remain;
       this.id_depart = id_depart;
       this.bus_url = bus_url;
       this.couple = couple_localite;
       this.agence = agence;
       this.date = date;
    }
}
