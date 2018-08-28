package cg.code.aleyam.nzela_nzela.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import cg.code.aleyam.nzela_nzela.centrale.CentraleInterface;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.depart.Depart;
import cg.code.aleyam.nzela_nzela.depart.Depart_fragment;
import cg.code.aleyam.nzela_nzela.depart.InfoGenFragment;
import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.actu.Actu_route;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.data_service.Agence_first_info;
import cg.code.aleyam.nzela_nzela.data_service.Data_terminal;
import cg.code.aleyam.nzela_nzela.data_service.Nzela_service;
import cg.code.aleyam.nzela_nzela.event.ClicInfo;
import cg.code.aleyam.nzela_nzela.home.blurry.Blurry;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import cg.code.aleyam.nzela_nzela.offline.OffTransaction;
import cg.code.aleyam.nzela_nzela.transaction.TicketActivity;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class Home extends Fragment implements
        AdapterView.OnItemSelectedListener , Depart_fragment.FragmentEventManager , CentraleInterface{

    Toolbar tb = null;
    public static boolean RUN = false;
    //ce filtre est identiquement indexe a celui du service.
    String[] filterArray = {"les plus recentes" , "Les plus cotées" , "les plus anciennes"};
    Spinner filter = null;
    FloatingTextButton offline = null;
    public static int SELECTED_ITEM = 0;
    public static String fromHome = "cg.code.aleyam.nzela_nzela.home";
    public final String HOME_KEY = "j_h_k";
    ImageView img_home = null;

    //section du paginateur.
    ViewPager home_vp = null;
    TabLayout home_tbl = null;
    HomePagerAdapter adapter = null;
    public static final String FROM_FILTER = "from_filter" , FROM_COMPAGIES = "from_comp";


    View home_view = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        home_view = inflater.inflate(R.layout.top_fragment_home , container , false);

        //initialisation du Paginateur
        home_vp =  home_view.findViewById(R.id.home_view_p);
        home_tbl =  home_view.findViewById(R.id.home_tab);
        adapter = new HomePagerAdapter(getFragmentManager() , home_tbl);
        home_vp.setAdapter(adapter);
        home_tbl.setupWithViewPager(home_vp);
        //fin de l'initialisation du paginateur.

        filter = home_view.findViewById(R.id.filtre);

        initHome(home_view);

        //on retire l'indicateur de chargement
        if(Centrale_activity.getLoader_indicator() != null) {
            ((View)Centrale_activity.getLoader_indicator()).setVisibility(View.GONE);
        }
        //l'image de fond de l'activite home
        img_home = home_view.findViewById(R.id.grote_img_home);
        blurImage(Home.this.getActivity() , img_home , R.drawable.presentation, 90);


        return home_view;
    }


    public static void blurImage(Context context , ImageView im , int img_res , int radius) {
        try {
            BitmapDrawable ressource = (BitmapDrawable) context.getResources().getDrawable(img_res);
            Blurry.with(context).radius(radius).async().animate(5000).from(ressource.getBitmap()).into(im);
        } catch (Exception e) {
            Log.e("test", "blurImage: error while bluring ");
        }
    }

    /**
     * methode permettant de notifier une operation de chargement se deroulant dans Home precisement dans ses fragments.
     * cette methode pour un premier temps permet de retirer le boutton de reservation offline.
     * @param sourceKey
     */
    public void notifyLoading(String sourceKey) {
        //le fragment du filtre est en train de charger des ressources.
        if(sourceKey.equalsIgnoreCase(FROM_FILTER)) {
            if(offline != null) {
                //on remercie offline pour un temps
                offline.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        RUN = false;
        //TODO ici y compris (verificaiton de l'enregistrem)
        filter.setOnItemSelectedListener(null);
        OttoBus.bus.unregister(Home.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        RUN = true;
        //TODO verifier que les enregistrements ne sont pas fait en double.
        OttoBus.bus.register(Home.this);
        filter.setOnItemSelectedListener(Home.this);
    }

    @Override
    public void centraleOnResume() {
        RUN = true;
        //TODO verifier que les enregistrements ne sont pas fait en double.
        filter.setOnItemSelectedListener(Home.this);
    }

    @Override
    public void centraleOnPause () {
        RUN = false;
        //TODO ici y compris (verificaiton de l'enregistrem)
        filter.setOnItemSelectedListener(null);

    }

    public void initHome(View fragmentMainView) {

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(Home.this.getContext() , android.R.layout.simple_spinner_item , filterArray);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(filterAdapter);
        filter.setOnItemSelectedListener(Home.this);
        offline = fragmentMainView.findViewById(R.id.offLine);
        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offLineHandler();
            }
        });

    }

    public void offLineHandler() {
        TextView tv = new TextView(Home.this.getContext());
        tv.setText("Reservation Offline");
        tv.setTextColor(Color.BLACK);

        AlertDialog offline_dialog = new AlertDialog.Builder(Home.this.getContext())
                //.setCustomTitle(tv)
                .setTitle(InfoGenFragment.fromHtml(OffTransaction.fontdebut+"Reservation Offline"+OffTransaction.fontfin))
                .setPositiveButton("Moi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        OffTransaction.sendOfflineMess( OffTransaction.callCenterNo ,
//                                OffTransaction.transactionToString(new String[]{"\n"+jour , "\n"+amont , "\n"+aval , "\n"+ Arrays.toString(friend_info) } , false)
//                                , OffTransaction.this
//                        );
                        launchOff(true);
                    }
                })
                .setNegativeButton("Un proche", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        launchOff(false);

                    }
                })
                .setMessage(InfoGenFragment.fromHtml(OffTransaction.messfontdebut+"Voulez vous reserver pour vous ou pour un proche?"+OffTransaction.fontfin))
                .create();

        offline_dialog.show();
        Button positive = offline_dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setTextColor(Color.GRAY);
        Button negative = offline_dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negative.setTextColor(Color.GRAY);
    }

    public void launchOff(boolean forMe) {
        Intent i = new Intent(Home.this.getActivity() , OffTransaction.class);
        i.putExtra(fromHome , forMe);
        startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SELECTED_ITEM = position;
//        filterLabel.setText(choix_description[position]);
        Data_terminal.getAgence(Nzela_service.FILTER_LIST[position] , Home.this.getActivity() , RUN );

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Subscribe
    public void clickAgenceHandler(Agence_first_info afi) {
        onFragmentInteraction(afi);
    }

    public void onFragmentInteraction(Agence_first_info afi) {
        // le traitement est fait dans cette methode pour des question respect de bonne pratique a la demande de l usage d un fragment.
        if(getDeparts(Home.this.getActivity() , afi)) {
            Intent intention = new Intent(Home.this.getActivity() , Depart.class);
            intention.putExtra("ClickAgence" , new String[]{afi.getNom_agence() , ""+afi.getId_agence() , ""+afi.getAvis() , afi.getLogo_agence()});
            startActivity(intention);
        }

        // pratique douteuse car au cas ou le chargement n'aboutissait pas on tombera sur l'activite suivante mais sans
        //donnee le mieux serai d'arriver avec des donnees sur une nouvelle activite



    }

    public boolean getDeparts(Context context , Agence_first_info afi) {
        if(CommunicationCheck.isConnectionAvalable(context)) {

            Data_terminal.getDepart(afi.getId_agence() , context , RUN);
            setOffVisibility(false);
            return true;
        } else {

            homeErrorMaker("Aucune connexion internet, je suis dans l'impossibiliter d'afficher les departs ");
            setOffVisibility(true);
            return false;
        }
    }

    public void getAgences(Context context) {
        if(CommunicationCheck.isConnectionAvalable(context)) {

            Data_terminal.getInstance().getAgence(Nzela_service.FILTER_LIST[SELECTED_ITEM] , context , Home.RUN);
            setOffVisibility(false);
        } else {
            //on donnant null a la variable null on accepte le mess par defaut
            homeErrorMaker(null);
            setOffVisibility(true);
        }
    }

    public void homeErrorMaker(String message) {
        try {
            Home_fragment les_agences = (Home_fragment)Home.this.getFragmentManager().getFragments().get(0);
            CommunicationCheck.displayErrorMessage(les_agences.recyclerView, les_agences.had ,  message);
        } catch (Exception e) {
            Log.e("test", "homeErrorMaker: error lors de la recuperation du home_fragment" );
        }
    }


    public void setOffVisibility(boolean visible) {
        if(visible) {
            offline.setVisibility(View.VISIBLE);
        } else {
            offline.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void centraleNotification(String key, Object forShare) {

    }

    @Override
    public void fragmentEventHandler(String key, Object value) {
        if(key.equals(HOME_KEY)) {
            Agence_first_info afi = (Agence_first_info)value;
            onFragmentInteraction(afi);
        }
    }

    //faite pas attention a moi
    @Override
    public void clickHandler(ClicInfo ci) {
    }
}