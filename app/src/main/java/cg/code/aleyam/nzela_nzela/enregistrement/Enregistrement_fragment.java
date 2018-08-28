package cg.code.aleyam.nzela_nzela.enregistrement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.event.Ftb_event;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class Enregistrement_fragment extends Fragment implements TextWatcher , View.OnFocusChangeListener, Ftb_event.ActionListener {

    public RegisterEvent gestionnaire_event = null;
    private boolean onRegister = false;
    private boolean disable_pnumber = false;
    private String user_infos[] = new String[6];
    private Spinner sexe_spiner = null;
    private View rootView = null;
    private String[] contentSexeSpin = {"Masculin" , "Feminin"};

    public interface RegisterEvent {
        //on verra apres pour le constructeur en fonction du besoin.

         void eventHandler(FloatingTextButton ftb_source, boolean inconformite, boolean empty_error);
    }

    TextInputEditText nom = null , prenom , telephone1 , telephone2 ,  onEdit , previous = null , adresse = null;
    TextInputLayout adresse_layout = null;
    FloatingTextButton ftb_reserver = null , ftb_acheter =  null ;
    private boolean inconformite = false , empty_error = true, done = false;

    public Enregistrement_fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_register, container , false);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view , savedInstanceState);
        if(rootView != null)
        initEditTexts(rootView);

    }

    public void initEditTexts(View rootview) {
        nom = rootview.findViewById(R.id.noms);
        onEdit = nom;
        previous = nom;
        nom.setOnFocusChangeListener(this);
        nom.addTextChangedListener(this);
        prenom = rootview.findViewById(R.id.prenoms);
        prenom.addTextChangedListener(this);
        prenom.setOnFocusChangeListener(this);
        telephone1 = rootview.findViewById(R.id.tel_pers);
        telephone1.addTextChangedListener(this);
        telephone1.setOnFocusChangeListener(this);
        telephone2 = rootview.findViewById(R.id.tel_proch);
        telephone2.addTextChangedListener(this);
        telephone2.setOnFocusChangeListener(this);
//        code_momo = rootview.findViewById(R.id.mdp);
//        code_momo.addTextChangedListener(this);
//        code_momo.setOnFocusChangeListener(this);
        adresse = rootview.findViewById(R.id.adresse);
        adresse.addTextChangedListener(this);
        adresse.setOnFocusChangeListener(this);
        adresse_layout = rootview.findViewById(R.id.adresse_layout);
        sexe_spiner = rootview.findViewById(R.id.sexe_span);
        ArrayAdapter<String> sex_adapter = new ArrayAdapter<String>(getActivity() , android.R.layout.simple_spinner_item , contentSexeSpin);
        sex_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexe_spiner.setAdapter(sex_adapter);



        ftb_reserver = rootview.findViewById(R.id.bt_reserver);

        ftb_acheter = rootview.findViewById(R.id.bt_acheter);

        new Ftb_event().setActionListenr(ftb_acheter , Enregistrement_fragment.this);
        new Ftb_event().setActionListenr(ftb_reserver , Enregistrement_fragment.this);

    }

    @Override
    public void actionPerformed(FloatingTextButton source   ) {
       gestionnaire_event.eventHandler(source , inconformite , empty_error );
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v instanceof TextInputEditText) {

                previous = onEdit;


            if (previous.getText().toString().trim().isEmpty()) {
                empty_error = true ;
                previous.setError("Vide, entrez une information correcte SVP");
            }

                onEdit = (TextInputEditText) v;


        }

    }
    public boolean isDone() {
        if (!nom.getText().toString().trim().isEmpty() && !prenom.getText().toString().trim().isEmpty() && (disable_pnumber || !telephone1.getText().toString().trim().isEmpty())
                && !telephone2.getText().toString().trim().isEmpty()
               ) {
            if(adresse.getVisibility() == View.INVISIBLE) {
                setUser_infos();
                return true;
            } else {
                if(!adresse.getText().toString().trim().isEmpty()) {
                    //Toast.makeText(this.getActivity() , "disable pn"+disable_pnumber , Toast.LENGTH_SHORT ).show();
                    setUser_infos();
                    return true;
                } else {
                    return false;
                }
            }




        }else return false;
    }



    @Override
    public void afterTextChanged(Editable s) {


        if (inconformite) {

           onEdit.setError("le caractere inscrit n'est pas pris en charge.");

        }
        done = isDone();


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //verification en temps reel de l integrite des entrees.

       if (!(inputOk(s.toString() , onEdit.getInputType() == InputType.TYPE_CLASS_PHONE || onEdit.getInputType() == 18))) {
            inconformite = true;
        }else if((inputOk(s.toString() , onEdit.getInputType() == InputType.TYPE_CLASS_PHONE || onEdit.getInputType() == 18))) {
           inconformite = false;
           empty_error = false;
       }
        //18 correspond au type input du code mobile money.
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    public boolean inputOk(String input , boolean digitInput) {

        Pattern  pattern ;
        if (digitInput) {
            pattern = Pattern.compile("^[+]([0-9])*$" , Pattern.CASE_INSENSITIVE);
        } else {
            pattern = Pattern.compile("^([ a-zA-Z0-9])*$" , Pattern.CASE_INSENSITIVE);
        }

        Matcher matcher = pattern.matcher(input);

        return matcher.find();
    }


    public void disableTransactionUI() {
        ftb_acheter.setVisibility(View.INVISIBLE);
        ftb_reserver.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof RegisterEvent ) {

            gestionnaire_event = (RegisterEvent) context;

        } else {
            throw new ClassCastException("l activite doit implementer l interface "+RegisterEvent.class);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setUser_infos() {

        user_infos[0] = nom.getText().toString().trim();
        user_infos[1] = prenom.getText().toString().trim();
        user_infos[2] = telephone1.getText().toString().trim();
        user_infos[3] = telephone2.getText().toString().trim();
        user_infos[4] = sexe_spiner.getSelectedItem().toString().trim();
        user_infos[5] = adresse.getText().toString().trim();

    }
    public String[] getUser_infos() {
        if(done) {
            return user_infos;
        } else return null;


    }



    public void notifyOnRegisterUser(String user_Pnumber) {

        try {
            genericOpTransaction();
            telephone1.setText(user_Pnumber);
            telephone1.setEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void setOffMode() {
        ftb_reserver.setVisibility(View.VISIBLE);
    }
    public void onCurrentUserTransaction(String[] user_infos , boolean onLine ) {
        nom.setText(user_infos[0]);
        prenom.setText(user_infos[1]);
        telephone1.setText(user_infos[2]);
        telephone2.setText(user_infos[3]);
        sexe_spiner.setSelection(user_infos[4].equals("Masculin")? 0 : 1);
        adresse.setText(user_infos[5]);
        if(!onLine) {
            genericOpTransaction();
            setOffMode();
        }


    }

    public void genericOpTransaction() {
        TextWatcher tw = (TextWatcher) Enregistrement_fragment.this;

        disable_pnumber = true;
        adresse_layout.setVisibility(View.VISIBLE);
        adresse.setVisibility(View.VISIBLE);
        disableTransactionUI();
    }
}


