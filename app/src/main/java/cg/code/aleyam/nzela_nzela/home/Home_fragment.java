package cg.code.aleyam.nzela_nzela.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.centrale.CentraleInterface;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.data_service.Agence_data;
import cg.code.aleyam.nzela_nzela.data_service.Agence_first_info;
import cg.code.aleyam.nzela_nzela.data_service.Loader;
import cg.code.aleyam.nzela_nzela.event.Nzela_objet;
import cg.code.aleyam.nzela_nzela.refresh.Updater;


public class Home_fragment extends Fragment {



    public Home_adapter had = new Home_adapter();
    RecyclerView recyclerView = null;
    Home piole = null;

    private CentraleInterface mListener;

    public Home_fragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View roortView = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            // Inflate the layout for this fragment

            recyclerView = roortView.findViewById(R.id.home_item);
            recyclerView.setHasFixedSize(true);
            //on met le linear layout car au cas ou? on pourra afficher le message d'erreur.

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayout.HORIZONTAL , false));

            recyclerView.setAdapter(had);
        } catch (Exception e ) {
            Log.e("test" , "Mon pire cauchmard "+e.getCause());
        }
        if(getActivity() instanceof Centrale_activity) {
            Centrale_activity ca = (Centrale_activity) getActivity();
            if(ca.currentFragment instanceof Home) {
                piole = (Home)ca.currentFragment;
            }
        }
        return roortView;
    }

    @Override
    public void onResume() {

        if(!CommunicationCheck.isConnectionAvalable(getContext())) {
            CommunicationCheck.displayErrorMessage(recyclerView , had , null);
            if(piole != null) {
                piole.setOffVisibility(true);
            }
        }
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        OttoBus.bus.register(Home_fragment.this);

        if (context instanceof Centrale_activity) {
            Fragment fr = ((Centrale_activity) context).currentFragment;
            if( fr instanceof CentraleInterface) {
                mListener = (CentraleInterface) fr;
            }
        } else {
            throw new RuntimeException(context.toString()
                    + " Erreur d'implementation ");
        }

    }

    @Override
    public void onDetach() {
        OttoBus.bus.unregister(Home_fragment.this);
        super.onDetach();
        mListener = null;
    }

    @Subscribe
    public void retryHandler(String errorMessage) {
        //quand y a pas de donnees a partager c est toujours mieux de passer les evement du bus avec un code
        Context context = getActivity();
        if(!(recyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , true));
        }
        if(piole!=null) {
            piole.getAgences(getActivity());
        } else {
            Log.w("fail" , "la variable piole de Home est vide");
        }


    }
    public void setStaggerred() {
        StaggeredGridLayoutManager lm =  new StaggeredGridLayoutManager(3 , LinearLayout.VERTICAL);//new GridLayoutManager(getContext() , 2);
        lm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(lm);
    }
    @Subscribe
    public void fetchAgence(Nzela_objet<Object> objet) {
        Log.e("test" , "la belle methode a ete appelle");

        if(objet.ok) {
            try {
                //avant de recevoir les agences il faut faire un petit chargement.
                //TODO avant de faire le cast verifier l'instance
                if(objet.objet instanceof Agence_data) {
                    Agence_data les_depart = (Agence_data) objet.objet;
                    //apres avoir recu les Agences on les display.
                    //Log.e("test" , "les departs" +les_depart.getAgences());
                    setStaggerred();
                    had.setList_agence_info(les_depart.getAgences());
                    piole.setOffVisibility(false);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            piole.setOffVisibility(true);
            CommunicationCheck.displayErrorMessage(recyclerView, had ,  null);
            Toast.makeText(Home_fragment.this.getContext() , "echec "+objet.objet.toString() ,Toast.LENGTH_SHORT).show();
        }
        Loader.dismiss();
        //apres avoir recuperer les donnees on met les agences a l'ecoute
        Updater.listenAgence();
    }
}
