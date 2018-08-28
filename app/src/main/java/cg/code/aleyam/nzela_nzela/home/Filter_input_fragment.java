package cg.code.aleyam.nzela_nzela.home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.data_service.DepartFilterObject;
import cg.code.aleyam.nzela_nzela.data_service.Depart_data;
import cg.code.aleyam.nzela_nzela.data_service.FilteredDepartItem;
import cg.code.aleyam.nzela_nzela.data_service.ServiceTerminal;
import cg.code.aleyam.nzela_nzela.transaction.paiement.ServiceCallback;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class Filter_input_fragment extends android.support.v4.app.Fragment implements CompoundButton.OnCheckedChangeListener
         {


    Spinner origin , destination;
    String origin_tab[] , destination_tab[];
    CheckBox enable_sum;
    TextInputEditText min_input , max_input ;
    TextInputLayout min_layout , max_layout;
    ProgressBar filter_progress;
    TextView date , heure;
    Button bt_date , bt_houre;
    private View contentView = null;
    int date_value , month_value, year_value , hour_value , minutes_value;
    ServiceCallback<Depart_data<FilteredDepartItem>> sc = null;

    FloatingTextButton submit_filter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        contentView = inflater.inflate(R.layout.fragment_input_filter , container , false);


        origin = contentView.findViewById(R.id.origin);
        origin_tab = getResources().getStringArray(R.array.destinations);
        initSpinner(origin , origin_tab);
        destination = contentView.findViewById(R.id.destination);
        destination_tab  = getResources().getStringArray(R.array.destinations);
        initSpinner(destination , destination_tab);
        //on recupere l'entree de date.
        date = contentView.findViewById(R.id.date_dep_layout);
        bt_date = contentView.findViewById(R.id.edit_dep_date);
        bt_date.setOnClickListener(listener_for_time);
        //on recupere l'heure entree par l'utilisateur
        heure = contentView.findViewById(R.id.houre_dep);
        bt_houre = contentView.findViewById(R.id.edit_houre);
        bt_houre.setOnClickListener(listener_for_time);
        //ensuite dans la foulet on les met sur ecoute

        enable_sum = contentView.findViewById(R.id.enable_sum);
        enable_sum.setOnCheckedChangeListener(this);
        min_input = contentView.findViewById(R.id.min_input);
        max_input = contentView.findViewById(R.id.max_input);
        min_layout = contentView.findViewById(R.id.min_layout);
        max_layout = contentView.findViewById(R.id.max_layout);
        submit_filter = contentView.findViewById(R.id.submit_filter);
        submit_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFilterComplet()) {
                    ServiceTerminal st = ServiceTerminal.getInstance(getActivity());
                    boolean sent = st.requestFiteredDeparts(new DepartFilterObject(origin.getSelectedItem().toString()
                            , destination.getSelectedItem().toString()
                            , 2
                            , TextUtils.isEmpty(min_input.getText().toString())?0:Integer.parseInt(min_input.getText().toString())
                            , TextUtils.isEmpty(max_input.getText().toString())?0:Integer.parseInt(max_input.getText().toString()) , heure.getText().toString() , date.getText().toString() )  , sc.new ResultCallback());
                    if(!sent) {
                        //donc si c'est pas envoye car les service de connecitons ne sont pas actives
                        Toast.makeText(getActivity() , "Impossible de comparer car les services Internet sont desactive" , Toast.LENGTH_LONG).show();
                    } else {
                        Centrale_activity central = (Centrale_activity) getActivity();
                        android.support.v4.app.Fragment f = central.currentFragment;
                        if(f instanceof Home) {
                            //donc si le fragment home est celui qui est affiche pendant que ce deroule cette operation
                            //lol mais biensure que c'est affiche si non comment serions nous ici ? mais pour plus de securite et de formalisme on passe par tout ca ok!
                            ((Home)f).notifyLoading(Home.FROM_FILTER);
                        }
                        setProgressEnable(true);
                    }
                }

            }
        });
        filter_progress = contentView.findViewById(R.id.filter_progress);
        setProgressEnable(false);
        enable_sum.setChecked(true);

        return contentView;
    }

    public boolean isFilterComplet () {

        boolean ok = true;
        if(!heure.getText().toString().contains(":")) {
            ok = false;
            Toast.makeText(getActivity() , "Veuillez svp renseigner l'heure" , Toast.LENGTH_LONG).show();
            return ok;
        }
        if(!date.getText().toString().contains("-")) {
            ok = false;
            Toast.makeText(getActivity() , "Veuillez svp renseigner la date" , Toast.LENGTH_LONG).show();
            return ok;
        }
        if(!enable_sum.isChecked()) {
            //ca veut dire qu'on veut bien definir la limite des sommes
            if(TextUtils.isEmpty(min_input.getText().toString())) {
                ok = false;
                Toast.makeText(getActivity() , "Veuillez svp renseigner la somme minimale de votre budget de transport" , Toast.LENGTH_LONG).show();
                return ok;
            }
            if(TextUtils.isEmpty(max_input.getText().toString())) {
                ok = false;
                Toast.makeText(getActivity() , "Veuillez svp renseigner la somme maximale de votre budget de transport" , Toast.LENGTH_LONG).show();
                return ok;
            }
        }
        if(origin.getSelectedItem().toString().equalsIgnoreCase(destination.getSelectedItem().toString())) {
            ok = false;
            Toast.makeText(getActivity() , "l'origine et la destination ne peuvent pas etre identique" , Toast.LENGTH_LONG).show();
            return ok;
        }

        return ok;


    }


    View.OnClickListener listener_for_time = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();
            if(view.getId() == R.id.edit_dep_date) {
                //donc l'utilisateur veut inserer la date de destination
                //pour commencer on donne un repere a notre selection pour ce faire on definit le moi, la date , et l'annee
                year_value = calendar.get(Calendar.YEAR);
                month_value = calendar.get(Calendar.MONTH);
                date_value = calendar.get(Calendar.DATE);
                if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP ) {
                    DatePickerDialog dpd = new DatePickerDialog(getActivity() ,android.R.style.Theme_Material_Dialog_Alert , new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                            date.setText(y+"-"+m+"-"+d);
                        }
                    } , year_value , month_value, date_value);
                    dpd.show();
                } else {
                    DatePickerDialog dpd = new DatePickerDialog(getActivity() , new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                            date.setText(y+"-"+m+"-"+d);
                        }
                    } , year_value , month_value, date_value);
                    dpd.show();
                }


            } else if(view.getId() == R.id.edit_houre) {
                //donc l'utilisateur veut inserer l'heure a laquelle il voyage.
                final int h , m;
                h = calendar.get(Calendar.HOUR);
                m = calendar.get(Calendar.MINUTE);
                if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP ) {
                    TimePickerDialog tpd = new TimePickerDialog(getActivity(), android.R.style.Theme_Material_Dialog_Alert , new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            String houre = ((i<10)?"0"+i:i)+":"+((i1<30)?"00":"30")+":00";
                            heure.setText(houre);
                        }
                    } , h , m , true );
                    tpd.show();
                } else {
                    TimePickerDialog tpd = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            String houre = ((i<10)?"0"+i:i)+":"+((i1<30)?"00":"30")+":00";
                            heure.setText(houre);
                        }
                    } , h , m , true );
                    tpd.show();
                }

            }
        }
    };

    public void setServiceCallback(ServiceCallback<Depart_data<FilteredDepartItem>> sc) {
        this.sc = sc;
    }

    public void disablePrices(boolean gone) {
        if(gone) {
            min_input.setVisibility(View.GONE);
            max_layout.setVisibility(View.GONE);
            min_layout.setVisibility(View.GONE);
            max_input.setVisibility(View.GONE);
            min_input.setText("");
            max_input.setText("");
        } else {
            min_input.setVisibility(View.VISIBLE);
            max_layout.setVisibility(View.VISIBLE);
            min_layout.setVisibility(View.VISIBLE);
            max_input.setVisibility(View.VISIBLE);
        }
    }

    public void setProgressEnable(boolean enable) {
        if(!enable) {
            submit_filter.setEnabled(true);
            submit_filter.setVisibility(View.VISIBLE);
            filter_progress.setVisibility(View.GONE);
        } else {
            submit_filter.setEnabled(false);
            submit_filter.setVisibility(View.GONE);
            filter_progress.setVisibility(View.VISIBLE);
        }
    }

    public void initSpinner(Spinner sp , String[] contentTab) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext() , android.R.layout.simple_spinner_item , contentTab);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        disablePrices(isChecked);
    }


}
