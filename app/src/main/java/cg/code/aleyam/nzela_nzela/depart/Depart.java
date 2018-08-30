package cg.code.aleyam.nzela_nzela.depart;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cg.code.aleyam.nzela_nzela.BottomSheetDialog;
import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.Settings.SettingsActivity;
import cg.code.aleyam.nzela_nzela.Settings.SettingsManager;
import cg.code.aleyam.nzela_nzela.analytics.Analytics;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.commentaire.CommentaireActivity;
import cg.code.aleyam.nzela_nzela.data_service.Data_terminal;
import cg.code.aleyam.nzela_nzela.data_service.Depart_data;
import cg.code.aleyam.nzela_nzela.data_service.Depart_item;
import cg.code.aleyam.nzela_nzela.data_service.Loader;
import cg.code.aleyam.nzela_nzela.data_service.Nzela_service;
import cg.code.aleyam.nzela_nzela.enregistrement.Register;
import cg.code.aleyam.nzela_nzela.event.ClicInfo;
import cg.code.aleyam.nzela_nzela.event.Nzela_objet;
import cg.code.aleyam.nzela_nzela.refresh.Updater;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;


public class Depart extends AppCompatActivity implements View.OnClickListener
        , Depart_fragment.FragmentEventManager
        , BottomSheetDialog.SheetCallback  {



    ViewPager vp = null;
    TabLayout tbl = null;
    Toolbar tb = null;
    FloatingActionButton fab_switch = null;
    public static Context ct = null;
    public static boolean RUN = false;

    public  Depart_data<Depart_item> all_data = new Depart_data<>();

    private boolean finish = false;
    private boolean isPrice_view = false;
    Pager_adapter adapter = null ;
    public static String agence_name = null;
    public static String[] activity_in_info = null;
    public static final String FOLOW_KEY = "cg.code.aleyam.nzela_nzela.folow";
    public static final String SAVED_INFO_ACTIVITY = "cg.code.aleyam.nzela_nzela.sia";
    public static final int REQUEST_CODE = 0;
    boolean back = false;
    Bundle savedInstanceState = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depart);
        if(!finish && Data_terminal.onDepartWaiting) {
            //TODO verifier que ce proceder fonctionne correctement au cas contraitre trouver une solution pour eviter les chargements infinis
            Loader.getInstance(Depart.this).load(true);
        }

        ct = Depart.this;
        //Toast.makeText(Depart.this , "onCreate" , Toast.LENGTH_SHORT).show();

        //Log.e("test" , ""+savedInstanceState.size());
        back = savedInstanceState == null?false:true;
        Log.e("test" , "Les Packets "+savedInstanceState);
        Log.e("test" , "onSaveInstance "+back);
        activity_in_info = getInData(back , savedInstanceState);
        loadImage(activity_in_info[3]);
        back = false;



        //mettre un verrou pour proteger contre la rupture de l execution du process.
        agence_name = activity_in_info[0];
        tb =  findViewById(R.id.ma_toolbar);
        tb.setTitle(agence_name);

        //ensuite on notify l'agence qui a ete selectionnee.
        new Analytics(Depart.this).notifyAgenceSelected(agence_name);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab_switch =  findViewById(R.id.bt_change_tab);//on recupere le boutton flottant inversant les tabs.
        fab_switch.setOnClickListener(Depart.this);

        vp =  findViewById(R.id.view_p);



        tbl =  findViewById(R.id.tab);


        adapter = makeAdapt(initView());
        vp.setAdapter(adapter);


        tbl.setupWithViewPager(vp );
    }

    public boolean initView() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Depart.this);
        String typeView = sp.getString("key_pref_trip_range" , "0");
        if(Integer.parseInt(typeView ) == 0) {
            isPrice_view = false;
            return isPrice_view;
        }
        else {
            isPrice_view = true;
            return isPrice_view;
        }
    }

    @Override

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("test"  , "appelle dans onSave");
        outState.putStringArray(Depart.SAVED_INFO_ACTIVITY , Depart.this.activity_in_info);

    }


    @Override
    public void onPause() {
         RUN = false;
        OttoBus.bus.unregister(Depart.this);
        super.onPause();
    }

    @Override
    public void onResume() {
        RUN = true;
        OttoBus.bus.register(Depart.this);
        super.onResume();
    }
    public String[] getInData(boolean back , Bundle savedInstance) {
        String[] activity_in_info;
        if (!back) {
            Intent intention = getIntent();
            activity_in_info = intention.getStringArrayExtra("ClickAgence");
        } else {
            Log.e("test" , "true dans la recuperation des donnees" );

            // a voir si c est une bonne methode
            activity_in_info = savedInstance.getStringArray(Depart.SAVED_INFO_ACTIVITY);

        }


        return activity_in_info;
    }
    @Override
    public void onClick(View v) {
        if (v.getId()== R.id.bt_change_tab) {

        //clique sur le boutton flottant.

            isPrice_view = !isPrice_view;
           switchTitle(tbl , isPrice_view);

           adapter.notifyDataSetChanged();
        }
    }
    @Subscribe
    public void fetcthDepart (Nzela_objet<Object> n_objet) {
        if(n_objet.ok) {
            try {

                if(n_objet.objet instanceof Depart_data) {
                    Depart_data<Depart_item> depart_data = (Depart_data<Depart_item>) n_objet.objet;
                    //Toast.makeText(Depart.this , "appelle "+ depart_data.getDepart(), Toast.LENGTH_LONG).show();
                    for(int i = 0 ; i<depart_data.getDepart().size() ; i++ ) {
                        Log.e("test" , depart_data.getDepart().get(i).getLieu_amont() );
                        Log.e("test" , depart_data.getDepart().get(i).getLieu_aval() );

                    }

                    // dans le cas la methode est appelee pour la recuperation des agences.

                    all_data = depart_data;
                    adapter = makeAdapt(isPrice_view);
                    vp.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }



            } catch (Exception e) {

            }
        } else {
            Toast.makeText(Depart.this , "Echange Impossible", Toast.LENGTH_LONG).show();
        }
        // est-ce utile?
        finish = true;
        Loader.dismiss();
        //on met a l'ecoute la section depart.
        Updater.listenDeparts();
    }

    public void loadImage(String URL_part) {

        String URL = Nzela_service.WEBSITE+"second/"+URL_part;
        Log.e("test" , "LoadingImage "+URL);
        AppCompatImageView bigAgenceLogo = findViewById(R.id.big_logo);
        Picasso
                .get()
                .load(URL).fit()
                .placeholder(R.drawable.compagnie_cover)
                .error(R.drawable.compagnie_cover)
                .into(bigAgenceLogo);
    }
    @Subscribe
    public void transferClick(ClicInfo ci) {

        clickHandler(ci);

    }



    public ClicInfo ci = null;
    //gere le clic sur les departs (info)
    @Override
    public void clickHandler(final ClicInfo co) {
        this.ci = co;
        if(ci == null || TextUtils.isEmpty(ci.id_depart) || ci.id_depart == null) return;
        displayMoreInfo();
    }


    public void displayMoreInfo() {
        BottomSheetDialog bsd = new BottomSheetDialog();
        bsd.setSheetCallBack(Depart.this);
        bsd.show(getSupportFragmentManager() , bsd.getTag());
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView =  View.inflate(Depart.this , R.layout.layout_depart_more_info , null);
        ((FloatingTextButton)contentView.findViewById(R.id.confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allerVers();
            }
        });
        ImageView bus_image = contentView.findViewById(R.id.big_bus);
        if(bus_image != null) {
            try {

                Picasso
                        .get()
                        .load(ci.bus_url)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.bus)
                        .error(R.drawable.bus)
                        .into(bus_image);

            } catch (Exception e) {

            }
        }
        RecyclerView supInfo = contentView.findViewById(R.id.detail_list);
        if(supInfo != null) {
            RecyclerView.LayoutManager lm = new LinearLayoutManager(Depart.this , LinearLayout.VERTICAL , false);

            supInfo.setLayoutManager(lm);
            String[] details_title = new String[]{
                    "Date de depart: " ,
                    "Tarif adulte: ",
                    "formalite: " ,
                    "Depart: ", "Tarif enfant: " , "Place restante: "
            };
            DepartMoreInfoAdapter dmia = new DepartMoreInfoAdapter(details_title , new String[]{
                    this.ci.date,
                    this.ci.price,
                    this.ci.formalite,
                    this.ci.depart,
                    this.ci.y_tarif,
                    this.ci.remain});
            if(dmia != null) supInfo.setAdapter(dmia);
        }

        dialog.setContentView(contentView);
    }

    public void allerVers() {
        Intent register_intent = new Intent(Depart.this , Register.class);
        register_intent.putExtra("info transaction" , new String[] {
                agence_name ,
                ci.price,
                ci.formalite ,
                ci.date ,
                ci.id_depart ,
                activity_in_info[1] , //qui est en fait l'id de l'agence courante.
                ci.couple
        });


        if (Integer.parseInt(ci.remain)==0) {
            Snackbar.make( findViewById(R.id.id_depart), R.string.plus_de_place , Snackbar.LENGTH_SHORT).show();
        } else {
            startActivityForResult(register_intent , Depart.REQUEST_CODE);
            // onSaveInstanceState(new Bundle());
        }
    }

    public void annuler() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        back = true;
        Log.e("test" , "backgroundHome dans la metode result"+back);


        if (requestCode == Depart.REQUEST_CODE && resultCode == Activity.RESULT_OK) {

           // Toast.makeText(getApplicationContext() , "onResult" , Toast.LENGTH_SHORT).show();


        }
    }

    /*
        methode permettant de changer les titre de la tabLayout.
         */
    public void switchTitle(TabLayout tbl , boolean isPrice_view) {

        adapter = makeAdapt(isPrice_view);

        vp.setAdapter(adapter);

        tbl.setupWithViewPager(vp );


    }

    /*make adapter permet de creer l adaptater adaquat pour */

    public Pager_adapter makeAdapt(boolean isPrice_view) {
        Pager_adapter adapter ;
        ArrayList<String> days = new ArrayList<>();
        days.add("");
        ArrayList<String> prix = new ArrayList<>();
        prix.add("0k");


        if (isPrice_view) {
                adapter = new Pager_adapter(getSupportFragmentManager(), prix , all_data , isPrice_view);
        } else {
                tbl.setTabMode(TabLayout.MODE_SCROLLABLE);
                adapter = new Pager_adapter(getSupportFragmentManager(), days , all_data , isPrice_view);
        }

        return  adapter;
    }


   @Override
    public boolean onCreateOptionsMenu(Menu m) {
      getMenuInflater().inflate(R.menu.depart_options_menu, m);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem mi ) {

        if(mi.getItemId() == R.id.reglage) {

            startActivity(new Intent(Depart.this , SettingsActivity.class));
            SettingsManager.getInstance(Depart.this).initUserInfo();

        } else if(mi.getItemId() == R.id.commentaire) {

            Intent i = new Intent(this , CommentaireActivity.class);

            i.putExtra(InfoGenFragment.info_key , new String[]{Depart.agence_name , ""+Depart.activity_in_info[1] });

            startActivity(i);

        } else if(mi.getItemId() == android.R.id.home) {
            SettingsManager.getInstance(this).getSharedPreferences().edit().putString("currentFragment" , "Home").apply();
            finish();
        }
        return super.onOptionsItemSelected(mi);
    }
}
