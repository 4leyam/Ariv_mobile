package cg.code.aleyam.nzela_nzela.refresh;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cg.code.aleyam.nzela_nzela.depart.Depart;
import cg.code.aleyam.nzela_nzela.commentaire.CommentaireActivity;
import cg.code.aleyam.nzela_nzela.data_service.Agence_first_info;
import cg.code.aleyam.nzela_nzela.data_service.Comment_obj;
import cg.code.aleyam.nzela_nzela.data_service.Data_terminal;
import cg.code.aleyam.nzela_nzela.data_service.Depart_item;
import cg.code.aleyam.nzela_nzela.data_service.Nzela_service;
import cg.code.aleyam.nzela_nzela.home.Home;

public class Updater {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static boolean onAgence = false , onComment = false , onDepart = false;


    public static void listenAgence() {
        if(!onAgence) {
            listen("Agences");
            onAgence = true;
        }
    }
    public static void listenDeparts() {
        if(!onDepart) {
            listen("Departs");
            onDepart = true;
        }
    }
    public static void listenComments() {
        if(!onComment) {
            listen("Comments");
            onComment = true;
        }
    }
    public static void listenTransaction() {

    }

    //TODO gerer le bug de des chargements des commentaires pour ce faire desacoupler les actualisations auto et voir le resultat
    //TODO Verifier que les commentaire qu'on recoit sont reelement incmoplet dans le but de desincriminer le server
    //


    private static void listen (final String ref_key) {

        database.getReference("/"+ref_key).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //actualisation provisoire dans le cadres des ajouts.
                refresh(dataSnapshot , ref_key);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                refresh(dataSnapshot , ref_key);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                refresh(dataSnapshot , ref_key);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                refresh(dataSnapshot , ref_key);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void refresh(DataSnapshot dataSnapshot , String haystack_type) {

        Log.e("test" , "la cle de la valeure alteree "+dataSnapshot.getKey() );
        switch (haystack_type) {
            case "Agences":

                if(Data_terminal.agences != null || Data_terminal.departs != null) {
                    ArrayList<Agence_first_info> haystack_agence = Data_terminal.agences.getAgences();
                    if(Home.RUN || Depart.RUN) {
                        for(int i = 0 ; i <  haystack_agence.size()  ; i++) {
                            String haystack_item = haystack_agence.get(i).getNom_agence();
                            if(haystack_item.equals(dataSnapshot.getKey()) ) {
                                if(Depart.RUN) {
                                    //permet de faire la mise a jour sur les departs quand un evements est declanche
                                    //sur les info de l agence pour ce faire avant de faire la mise a jours il
                                    //faut connaitre quelle activite est en cours d execution.
                                    Data_terminal.getDepart(Integer.parseInt(Depart.activity_in_info[1]) , null , Depart.RUN);
                                    break;
                                } else {
                                    Data_terminal.getAgence(Nzela_service.FILTER_LIST[Home.SELECTED_ITEM] , null , Depart.RUN);
                                    break;
                                }

                            }
                        }
                    }
                }
            break;
            case "Comments":

                //Log.e("test" , ""+Data_terminal.commentaires.toString());

                if(Data_terminal.commentaires != null) {
                    ArrayList<Comment_obj> haystack_comment = Data_terminal.commentaires.getCommentaire();

                    if(CommentaireActivity.RUN) {
                        //donc si l activite est en cours d execution

                        String agence_concernee = CommentaireActivity.agence_concernee;
                        if(agence_concernee != null) {
                            if(agence_concernee.equals(dataSnapshot.getKey())) {
                                //zero parceque tous les commentaires sont censee avoir le meme id

                                Data_terminal.getAgenceComment(Integer.parseInt(Depart.activity_in_info[1]) , null , CommentaireActivity.RUN);
                            }
                        }

                    }
                }

            break;
            case "Departs":
                if(Data_terminal.departs != null) {
                    ArrayList<Depart_item> haystack_depart = Data_terminal.departs.getDepart();
                    if(Depart.RUN) {
                        for (int i = 0 ; i < haystack_depart.size() ; i++) {
                            int id_depart = haystack_depart.get(i).getId_depart();
                            if(dataSnapshot.getKey().equals(""+id_depart)) {
                                Data_terminal.getDepart(Integer.parseInt(Depart.activity_in_info[1]) , null , Depart.RUN);
                                break;
                            }
                        }
                    }
                }
            break;
        }

    }


   /*
   * methode permettant de mettre a jours le count des commentaires
   * qui en passant declanche la mise a jours des commentaire aupres de ceux qui les
   * lisent.
   * */

    public static void updateComment(final String agence) {
        String chemin_de_reference = "/Comments/"+agence;
        final DatabaseReference commentaire_ref = database.getReference(chemin_de_reference);

        commentaire_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object valeure_brute = dataSnapshot.getValue();

                int valeur = valeure_brute == null ? 0 :  Integer.parseInt(""+dataSnapshot.getValue());
                Map<String , Object> cible = new HashMap<>();
                cible.put("/Comments/"+agence ,  ++valeur);
                database.getReference().updateChildren(cible);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




}