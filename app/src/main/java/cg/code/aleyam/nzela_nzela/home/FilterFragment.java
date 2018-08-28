package cg.code.aleyam.nzela_nzela.home;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import cg.code.aleyam.nzela_nzela.BottomSheetDialog;
import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.check.NoResult;
import cg.code.aleyam.nzela_nzela.data_service.DepartFilterObject;
import cg.code.aleyam.nzela_nzela.data_service.Depart_data;
import cg.code.aleyam.nzela_nzela.data_service.FilteredDepartItem;
import cg.code.aleyam.nzela_nzela.data_service.ServiceTerminal;
import cg.code.aleyam.nzela_nzela.depart.Depart;
import cg.code.aleyam.nzela_nzela.depart.DepartMoreInfoAdapter;
import cg.code.aleyam.nzela_nzela.depart.Depart_fragment;

import cg.code.aleyam.nzela_nzela.enregistrement.Register;
import cg.code.aleyam.nzela_nzela.event.ClicInfo;
import cg.code.aleyam.nzela_nzela.transaction.paiement.ServiceCallback;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class FilterFragment extends Fragment implements  Observer , BottomSheetDialog.SheetCallback  {


    FloatingTextButton  back_filter;
    View contentView = null;
    private final String AGENCE_SPLITER = "#~#";
    ServiceCallback<Depart_data<FilteredDepartItem>> sc = null;
    public static final String FILTER_KEY = "filter_key";
    public static boolean RUN = false;
    android.support.v4.app.Fragment previousFragment = null;
    private Map<String , ArrayList<String[]>> data = new HashMap<>();
    ClicInfo ci = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_filter, container , false);

        sc = new ServiceCallback<>(FILTER_KEY , FilterFragment.this);

        back_filter = contentView.findViewById(R.id.retour_filter);
        back_filter.setOnClickListener(listener_back_filter);
        //ensuite initialisation de bouton de soumission.

        initFilterInput();
        return contentView;

    }

    View.OnClickListener listener_back_filter = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            initFilterInput();
        }
    };

    public void initFilterInput() {
        //on repart sur le formulaire du filtre.
        Filter_input_fragment fif = new Filter_input_fragment();
        fif.setServiceCallback(FilterFragment.this.sc);
        switchFragment(fif);
        back_filter.setVisibility(View.GONE);
    }



    @Override
    public void onPause() {
        RUN = false;
        OttoBus.bus.unregister(FilterFragment.this);
        super.onPause();
    }

    @Override
    public void onResume() {
        RUN = true;
        OttoBus.bus.register(FilterFragment.this);
        super.onResume();
    }

    @Override
    public void update(Observable o , Object arg) {
        if(arg instanceof String) {
            String key = (String) arg;
            if(key.equals(FILTER_KEY)) {
                dataArrange(sc.response);

                ArrayList<String[]> departs = (ArrayList<String[]>) data.get("filtered");
                if(departs.isEmpty()) {
                    NoResult nr = new NoResult();
                    switchFragment(nr);
                } else {
                    Depart_fragment df = new Depart_fragment();
                    df.setElements(departs);
                    switchFragment(df);
                }
                back_filter.setVisibility(View.VISIBLE);
            }
        }
    }

    public void switchFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Log.e("test" , "switchFragment: isEmpty "+ft.isEmpty());
            this.previousFragment = fragment;
            ft.replace(R.id.fragment_anchor , fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }


    @Subscribe
    public void transferClick(ClicInfo ci) {

        clickHandler(ci);

    }

    public void clickHandler(ClicInfo ci) {
        this.ci = ci;
        if(ci == null || TextUtils.isEmpty(ci.id_depart) || ci.id_depart == null) return;
        displayMoreInfo();
    }
    public void displayMoreInfo() {
        BottomSheetDialog bsd = new BottomSheetDialog();
        bsd.setSheetCallBack(FilterFragment.this);
        bsd.show(getFragmentManager() , bsd.getTag());
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView =  View.inflate(getContext() , R.layout.layout_depart_more_info , null);
        (contentView.findViewById(R.id.confirm)).setOnClickListener(new View.OnClickListener() {
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
            RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext() , LinearLayout.VERTICAL , false);

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
        Intent register_intent = new Intent(getContext() , Register.class);

        String[] agenceInfo = splitAgenceInfo(ci.agence);
        Log.e("test", "agenceInfo "+ ci.agence);
        register_intent.putExtra("info transaction" , new String[] {
                agenceInfo[1] ,
                ci.price,
                ci.formalite ,
                ci.date ,
                ci.id_depart ,
                agenceInfo[0], //qui est en fait l'id de l'agence courante.
                ci.couple
        });


        if (Integer.parseInt(ci.remain)==0) {
            if(contentView != null) {
                Snackbar.make( contentView.findViewById(R.id.id_depart), R.string.plus_de_place , Snackbar.LENGTH_SHORT).show();
            }
        } else {
            startActivityForResult(register_intent , Depart.REQUEST_CODE);
            // onSaveInstanceState(new Bundle());
        }
    }

    /**
     * permet de separer le nom de l'agence de son id
     *et return un tableau ou l'id est a l'indice 0
     * @param agence
     * @return
     */
    private String[] splitAgenceInfo(String agence) {
        String[] id_name = agence.split(AGENCE_SPLITER);
        return id_name;
    }


    public void dataArrange(Depart_data<FilteredDepartItem> all_data) {
        Map<String , ArrayList<String[]>> contenu = new HashMap<>();
        ArrayList<FilteredDepartItem> di = all_data.getDepart();


        ArrayList<String[]> depart = new ArrayList<>();
        for (int i = 0 ; i<di.size() ; i++) {
            Log.e("test" , "ex: "+di.get(i));
                depart.add(dataClassify(di.get(i)));
                //contenu.get("filtered").add(dataClassify(di.get(i) , !isPrice));
        }
        contenu.put("filtered" , depart);
        data = contenu;
    }


    private String[] dataClassify(FilteredDepartItem di) {

        String[] depart_info = {
                di.getDate_depart() ,
                di.getFormalite() ,
                di.getDepart_h(),
                ""+di.getTarif_enfant(),
                ""+di.getPlace_disponible(),
                di.getLieu_amont()+"~"+di.getLieu_aval(),
                di.getImage_bus() ,
                ""+di.getId_depart() ,
                ""+di.getId_agence() ,
                ""+di.getTarif_adult()
        };
        return depart_info;
    }
}
