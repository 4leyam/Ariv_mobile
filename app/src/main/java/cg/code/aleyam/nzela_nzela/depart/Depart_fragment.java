package cg.code.aleyam.nzela_nzela.depart;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.event.ClicInfo;

public class Depart_fragment extends Fragment {
    RecyclerView liste_depart = null;

    int length = 1;
    ArrayList<String[]> information_complete = new ArrayList<>();


    private FragmentEventManager hostActivity = null;


    public Depart_fragment() {

    }

    public interface FragmentEventManager {
        public void clickHandler(ClicInfo ci);
    }



    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup viewGroup , Bundle si) {
        View rootView = inflater.inflate(R.layout.list_depart, viewGroup , false);

        liste_depart = rootView.findViewById(R.id.liste_depart);
        //depart adapter est l adaptater qui permet de metttre en forme les elments de depart.
        //fragment permettant d'afficher les liste de depart en fonction des jours ou des prix
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext() , LinearLayout.VERTICAL , false);
        liste_depart.addItemDecoration(new DividerItemDecoration(viewGroup.getContext() , LinearLayout.VERTICAL));
        liste_depart.setHasFixedSize(true);
        liste_depart.setLayoutManager(lm);
        //les deux derniers argument sont la pour la gestion des evenement. lors du clic.
        Log.e("test" , "objet complet on create: "+information_complete.toString());
        DepartAdapter dep = new DepartAdapter(information_complete);
        liste_depart.setAdapter(dep);




        return rootView;
    }

    @Override
    public void onAttach(Context cont) {
        super.onAttach(cont);

        if (cont instanceof Centrale_activity) {
            Fragment fr = ((Centrale_activity) cont).currentFragment;
            if( fr instanceof FragmentEventManager) {
                hostActivity = (FragmentEventManager) fr;
            }
        }else if (cont instanceof FragmentEventManager) {
            hostActivity = (FragmentEventManager) cont;
        } else {
            throw new ClassCastException(cont.toString()+"doit implementer l interface "+FragmentEventManager.class.toString());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        hostActivity = null;
    }
    /*
    getList est une methode qui permet de recuperer les differentes informations en fonction du titre
      qui vont constituer la listeView.
     */




    //get Element est appele depuis Pager adapter afin de constituer les onglets.
    public void setElements(ArrayList<String[]> information_complete) {

        Log.e("test" , "objet complet: "+information_complete.toString());
        this.information_complete = information_complete;

    }

    // les methodes qui viennent apres ne servent a rien et seront supprimer apres le test.
    public ArrayList<String[]> getList(String tab_title , String agence_name) {

        ArrayList<String[]> info_complet = null;

        if (Pager_adapter.isPrice()) {
            // on recupere tous les tableaux sauf celui des jours.


        } else {
            // on recupere tous les tableaux sauf celui des prix.


        }
        //les espaces vide sont la pour les futures procedure permettant de recupere

        //pour le test le rendu de toute la logique on a cree la methode test_dep_for() qui retourne des tab de depart et formalite aleatoirement.
        return test_dep_for();
    }


    //methode a supprimer apres les tests.

    public ArrayList<String[]> test_dep_for() {

        ArrayList<String[]> fake_list = new ArrayList<>();
        String[] liste = new String[10];

        if(Pager_adapter.isPrice()) {
            liste = new String[]{"Lundi" , "Mardi" ,"Mercredi", "Jeudi","Vendredi","Samedi","Dimanche","Lundi" , "Mardi" ,"Mercredi"};
        } else {
            for (int i = 0; i <10 ; i++) {
                //avant de cree l'adaptateur il faut une methode permettant de recupere les titres
                liste[i] = "1"+i+"K";
            }
        }
        fake_list.add(liste);
        liste = new String[10];
        for (int i = 0 ; i < 2 ; i++) {

            for(int j = 0 ; j < 10 ; j++) {
                liste[j] = j+6+"h";
            }
            fake_list.add(liste);
            liste = new String[10];
            //on ajoute la liste des departs et des formalites.
        }
        liste = new String[10];
        //on recupere le tarif pour les gosse pour chaque depart.
        for(int i = 0 ; i<10 ; i++) {
            liste[i] = 7+"k";
        }
        fake_list.add(liste);
        //
        liste = new String[10];
        for (int i = 0 ; i<10 ; i++) {
            liste[i] = ""+i;
        }
        fake_list.add(liste);
        fake_list.add(new String[]{"Brazzaville~Pointe-noire" , "Brazzaville~Pointe-noire" , "Brazzaville~Pointe-noire" , "Brazzaville~Pointe-noire" ,
                "Pointe-Noire~Brazzaville" , "Brazzaville~Dolisie" , "Brazzaville~Dolisie" , "Brazzaville~owando" , "Brazzaville~Djambala" , "Brazzaville~Djambala"});
        return fake_list;
    }


}