package cg.code.aleyam.nzela_nzela.Settings;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.firebase.database.DatabaseError;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.data_service.Loader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements SettingsManager.SettingOperationListener {


    private static PreferenceFragment certificationFragment = null;
    private static AlertDialog al = null;
    private static SettingsActivity sa = null;
    private static boolean firstTime = true;
    private static boolean ignoreFeedbackOnOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sa = SettingsActivity.this;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.layout_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        firstTime = true;
        ignoreFeedbackOnOpen = true;
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if(preference instanceof SwitchPreference) {

                if(preference.getKey().equalsIgnoreCase("pref_trip_shortcut")) {
                    boolean bool = Boolean.parseBoolean(stringValue);
                    if(bool)  preference.setSummary("Raccourci info Active");
                    else preference.setSummary("Raccourci info desactive");
                } else if(preference.getKey().equals("key_pref_certification_alerts_user_type")) {
                    boolean bool = Boolean.parseBoolean(stringValue);
                    if(bool)  preference.setSummary("Administrateur");
                    else preference.setSummary("Agent public");
                } else if(preference.getKey().equals("key_pref_certification_alertss")) {
                    //on est dans les preferences de certification ou compte public de l'utilisateur.
                    certificationHandler(preference , value);
                } else if(preference.getKey().equals("key_switch_display_home")) {
                    boolean bool = Boolean.parseBoolean(stringValue);
                    if(bool)  preference.setSummary("Afficher");
                    else preference.setSummary("Masquer");
                }

            } else if (preference instanceof MultiSelectListPreference) {

                if(value instanceof Set) {
                    HashSet<String> multiple =  (HashSet<String>)value;
                    String[] content = new String[multiple.size()];
                    int i = 0;
                    for(String str : multiple) {
                        content[i] = ""+((MultiSelectListPreference) preference).getEntries()[Integer.parseInt(str)];
                        i++;
                    }
                    preference.setSummary(TextUtils.join(" , "  , content ));
                }


            } else {
                if(preference.getKey().equals("key_feedback")) {
                    if(!ignoreFeedbackOnOpen) {

                        Intent Email = new Intent(Intent.ACTION_SEND);
                        Email.setType("text/email");
                        Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "aleyam4ndroid@gmail.com" });
                        Email.putExtra(Intent.EXTRA_SUBJECT, "Retour d'experience");
                        Email.putExtra(Intent.EXTRA_TEXT, stringValue);
                        preference.getContext().startActivity(Intent.createChooser(Email, "Retour d'experience"));

                    } ignoreFeedbackOnOpen = false;

                } else if(preference.getKey().equals("key_pref_user_addAlert")) {
                    //avant d'ajouter l'utilisateur on verifie dab si l'utilisateur est un administrateur.

                    if(!firstTime) {
                        boolean isAdmin = SettingsManager.getInstance(preference.getContext()).isAddOperationAllowed();
                        if(isAdmin) {
                            //ensuite on donne les authorisations a l'utilisateur.
                            addUserHandler(stringValue , preference);
                        } else {
                            Toast.makeText(preference.getContext() , "Action Impossible vous n'avez pas les authorisations necessaires" , Toast.LENGTH_SHORT).show();
                        }
                    } firstTime = false;

                } else {
                    initUserPref(preference , stringValue);
                    preference.setSummary(stringValue);
                }

            }
            return true;
        }
    };

    private static void addUserHandler(String phone , Preference preference) {
        if(CommunicationCheck.isConnectionAvalable(preference.getContext())) {

            Loader.getInstance(preference.getContext()).load(true);
            SettingsManager sm =  SettingsManager.getInstance(preference.getContext());
            Set<String> set = sm.getSharedPreferences().getStringSet("key_pref_certification_alerts_type" , new HashSet<String>());
            sm.addUserInAlert(TextUtils.join("$" , set.toArray(new String[]{})) , phone ,"key_pref_user_addAlert" , sa);
        } else {
            Toast.makeText(preference.getContext() , "Pas de Connexion Internet" , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void completed(String key, Object object) {


        if(key.equals("key_pref_certification_alertss")) {
            //verification du mot de pass du switch pour les alerts.
            if(object == null) {

                //on off le switch
                SettingsManager.getInstance(SettingsActivity.this).getSharedPreferences().edit().putBoolean("key_pref_certification_alertss" , false).apply();
                Loader.dismiss();
                al = getCertificationAlertDialog(SettingsActivity.this);
                //on affiche a nouveau afin de permettre a l'utilisateur de saisir son PW
                al.show();
                Toast.makeText(SettingsActivity.this , "Echec 138" , Toast.LENGTH_SHORT).show();
            } else if(object instanceof DatabaseError) {
                //une erreure est survenue
                Toast.makeText(SettingsActivity.this , "Erreur reseau" , Toast.LENGTH_SHORT).show();
                Loader.dismiss();
                al = getCertificationAlertDialog(SettingsActivity.this);
                //on affiche a nouveau afin de permettre a l'utilisateur de saisir son PW
                al.show();
            } else if(object instanceof Boolean) {
                boolean result = (boolean) object;
                if(result) {
                    Loader.dismiss();
                    al = null;
                    Toast.makeText(SettingsActivity.this , "Verifie" , Toast.LENGTH_SHORT).show();
                }
            }
        } else if(key.equals("key_pref_user_addAlert")) {
            if(object == null || object instanceof DatabaseError) {
                Toast.makeText(SettingsActivity.this , "Echec 2" , Toast.LENGTH_SHORT).show();
                Loader.dismiss();
            } else if(object instanceof Boolean){
                boolean result = (boolean) object;
                if(result) {
                    Loader.dismiss();
                    Toast.makeText(SettingsActivity.this , "Utilisateur ajoute avec succes" , Toast.LENGTH_SHORT).show();
                }
            }
        } else if(key.equals("key_pref_certification_alerts_type")) {
            //traitement d'ajout d'utilisateur.

            if(object == null) {
                //si les choses ne se sont passee comme il se doit.
                Toast.makeText(SettingsActivity.this , "Numero Incorrect" , Toast.LENGTH_SHORT).show();
            } else if(object instanceof DatabaseError) {
                //si les choses ne se sont passee comme il se doit.
                Toast.makeText(SettingsActivity.this , "Probleme de connexion " , Toast.LENGTH_SHORT).show();
            } else {
                //donc tout s'est bien passe.
                Toast.makeText(SettingsActivity.this , "Succes" , Toast.LENGTH_SHORT).show();
            }
            Loader.dismiss();
        }
    }

    public static void certificationHandler(final Preference preference , Object object) {
        //on commence par demander un mot de passe a l'utilisateur.
        if(object instanceof Boolean) {
            Boolean isOn = (Boolean) object;
            if(isOn) {
                //on initialise et affiche la boite de dialoque pour demander le mot de passe afin de modifier les paramettres.
                al = getCertificationAlertDialog(preference.getContext());
                al.show();
            }
        }

    }

    public static AlertDialog getCertificationAlertDialog(final Context ct ) {
        if(al == null) {
            final EditText et = new EditText(ct);
            et.setHint("Mot de passe");
            et.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);

            al = new AlertDialog.Builder(ct)
                    .setTitle("Protection")
                    .setView(et)
                    .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //on a taper le mot de passe
                            if(CommunicationCheck.isConnectionAvalable(ct)) {
                                if(!TextUtils.isEmpty(et.getText())) {
                                    //si le mot de passe n'est pas vide on passe a la comparaison ok.
                                    SettingsManager.getInstance(ct).verifyAdminPass("key_pref_certification_alertss" , et.getText().toString() , SettingsActivity.sa);
                                    dialogInterface.dismiss();
                                    //on charge pendant la verification du mot de pass.
                                    Loader.getInstance(ct).load(true);
                                } else {
                                    SettingsManager.getInstance(ct).getSharedPreferences().edit().putBoolean("key_pref_certification_alertss" , false).apply();
                                    Toast.makeText(ct , "Entrez un mot de passe valide SVP" , Toast.LENGTH_SHORT).show();
                                    certificationFragment.getActivity().finish();
                                    al = null;
                                }
                            } else {
                                Toast.makeText(ct , "Pas de Connexion internet" , Toast.LENGTH_SHORT).show();
                                certificationFragment.getActivity().finish();
                                al = null;
                                SharedPreferences sp = SettingsManager.getInstance(ct).getSharedPreferences();
                                boolean isAllowed = sp.getBoolean("key_pref_certification_alertss" , false);
                                Toast.makeText(ct , "pope: "+isAllowed , Toast.LENGTH_SHORT ).show();
//
                            }

                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(certificationFragment != null) {
                                //donc si l'utilisateur annule c'est qu'il ne veut rien modifier ou voir
                                //donc on ferme la chose.
                                dialogInterface.dismiss();
                                certificationFragment.getActivity().finish();
                                SharedPreferences sp = SettingsManager.getInstance(ct).getSharedPreferences();
                                boolean isAllowed = sp.getBoolean("key_pref_certification_alertss" , false);
                                sp.edit().putBoolean("key_pref_certification_alertss" , false).apply();
                                al = null;
                            }
                        }
                    })
                    .create();
        }
        return al;
    }

    public static void initUserPref(Preference preference , String stringValue) {
        switch (preference.getKey()) {
            case "key_pref_user_name":
                SettingsManager.getInstance(preference.getContext()).setUserInfo(SettingsManager.CHANGE_NAME , stringValue);
                break;
            case "key_pref_user_lastname":
                SettingsManager.getInstance(preference.getContext()).setUserInfo(SettingsManager.CHANGE_LASTNAME , stringValue);
                break;
            case "key_pref_user_pnumber":
                SettingsManager.getInstance(preference.getContext()).setUserInfo(SettingsManager.CHANGE_PHONE , stringValue);
                break;
            case "key_pref_user_cpnumber":
                SettingsManager.getInstance(preference.getContext()).setUserInfo(SettingsManager.CHANGE_CPHONE , stringValue);
                break;
            case "key_pref_user_adress":
                SettingsManager.getInstance(preference.getContext()).setUserInfo(SettingsManager.CHANGE_ADRESS , stringValue);
                break;
            default:
                break;
        }
    }



    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * methode permettant de changer le resumer d'un parametre quand ce dernier est modifie
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        if(preference instanceof SwitchPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        }else if(preference instanceof MultiSelectListPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getStringSet(preference.getKey(), new HashSet<String>()));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }


    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || UserFragment.class.getName().equals(fragmentName)
                || CertificationFragment.class.getName().equals(fragmentName)
                || NavigationFragment.class.getName().equals(fragmentName)
                || TripFragment.class.getName().equals(fragmentName)
                || AboutFragment.class.getName().equals(fragmentName)
                || OtherFragment.class.getName().equals(fragmentName) ;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UserFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            String[] infoUser = SettingsManager.getInfoUser(getActivity());
            if(infoUser == null) {
                addPreferencesFromResource(R.xml.pref_no_user);
            } else {

                addPreferencesFromResource(R.xml.pref_user_account);
                setHasOptionsMenu(true);


                bindPreferenceSummaryToValue(findPreference("key_pref_user_name"));
                bindPreferenceSummaryToValue(findPreference("key_pref_user_lastname"));
                bindPreferenceSummaryToValue(findPreference("key_pref_user_pnumber"));
                bindPreferenceSummaryToValue(findPreference("key_pref_user_cpnumber"));
                bindPreferenceSummaryToValue(findPreference("key_pref_user_adress"));
            }

        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CertificationFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            String[] infoUser = SettingsManager.getInfoUser(getActivity());
            if(infoUser == null) {
                addPreferencesFromResource(R.xml.pref_no_user);
            } else {

                addPreferencesFromResource(R.xml.pref_account_certification);
                setHasOptionsMenu(true);
                certificationFragment = this;

                bindPreferenceSummaryToValue(findPreference("key_pref_certification_alertss"));
                bindPreferenceSummaryToValue(findPreference("key_pref_certification_alerts_type"));
                bindPreferenceSummaryToValue(findPreference("key_pref_certification_alerts_user_type"));
                bindPreferenceSummaryToValue(findPreference("key_pref_user_addAlert"));
            }

        }

        @Override
        public void onDetach() {
            super.onDetach();
            firstTime = true;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            firstTime = true;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NavigationFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            String[] infoUser = SettingsManager.getInfoUser(getActivity());
            if(infoUser == null) {
                addPreferencesFromResource(R.xml.pref_no_user);
            } else {

                addPreferencesFromResource(R.xml.pref_navigation);
                setHasOptionsMenu(true);

                bindPreferenceSummaryToValue(findPreference("key_pref_navigation_events"));
                bindPreferenceSummaryToValue(findPreference("key_pref_navigation_radius"));
            }

        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class TripFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_trip);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("key_pref_trip_range"));
            bindPreferenceSummaryToValue(findPreference("pref_trip_shortcut"));
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AboutFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_app);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("key_feedback"));

        }

        @Override
        public void onDetach() {
            super.onDetach();
            ignoreFeedbackOnOpen = true;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            ignoreFeedbackOnOpen = true;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class OtherFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_other);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("key_switch_display_home"));
        }

    }

}
