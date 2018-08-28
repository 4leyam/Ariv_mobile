package cg.code.aleyam.nzela_nzela.centrale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.Settings.SettingsActivity;
import cg.code.aleyam.nzela_nzela.Settings.SettingsManager;
import cg.code.aleyam.nzela_nzela.actu.Actu_route;
import cg.code.aleyam.nzela_nzela.actu.notification.AlertService;
import cg.code.aleyam.nzela_nzela.actu.notification.AlertsRequestsService;
import cg.code.aleyam.nzela_nzela.actu.notification.CustomNotificationBuilder;
import cg.code.aleyam.nzela_nzela.home.Home;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import cg.code.aleyam.nzela_nzela.transaction.TicketActivity;
import cg.code.aleyam.nzela_nzela.transaction.connexion.PostTransaction;

public class Centrale_activity extends AppCompatActivity{

    public Fragment currentFragment = null;
    public static boolean isRunning = false;
    private boolean isBound = false;
            Toolbar tb = null;
    String
            userInfos[] = null;
    NavigationView navigationView = null;
    DrawerLayout drawerLayout = null;
    TextView userNamePresentation = null;
    ActionBarDrawerToggle actionBarDrawerToggle = null;
    DatabaseManager dbManager = null;
    private static ProgressBar loader_indicator = null;
    private static Activity instance = null;

    public static Activity getActivityInstance() throws Exception {
        if(instance == null) {
            throw new Exception("l'instance de l'activite n'a pas encore ete cree");
        } else
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centrale);

        isRunning = true;

        instance = Centrale_activity.this;
        dbManager = DatabaseManager.getInstance(Centrale_activity.this);
        userInfos = dbManager.getCurrentUser();
//Suppression de toutes les transactions inachevees
        if(userInfos != null && userInfos.length!=0)
            PostTransaction.getInstance(userInfos[2] , Centrale_activity.this).removeIncompletTransactions();
        else
            PostTransaction.getInstance( ""+(new Date()).getTime(), Centrale_activity.this).removeIncompletTransactions();
        tb = findViewById(R.id.centrale_toolbare);
        tb.setTitle(R.string.app_name);
        loader_indicator = findViewById(R.id.loader_indicator);
        Drawable progress_drawable = loader_indicator.getIndeterminateDrawable().mutate();
        progress_drawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        loader_indicator.setIndeterminateDrawable(progress_drawable);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.san_nav);
        navigationView.setNavigationItemSelectedListener(onil);
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(Centrale_activity.this , drawerLayout , R.string.ouvert , R.string.ferme );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        defineSummaryState(navigationView);
        defineAlertState(navigationView);
        View v = navigationView.inflateHeaderView(R.layout.menu_sandwich);
        userNamePresentation = v.findViewById(R.id.user_info);
        if(userInfos != null)
            userNamePresentation.setText(userInfos[1]);
        dbManager.closeDB();

        setSelectedCentralFragment(savedInstanceState);
        initAlertRequestListener();
    }
    public void initAlertRequestListener() {
        if(!isBound) {
            Intent i = new Intent(Centrale_activity.this , AlertsRequestsService.class);
            Centrale_activity.this.startService(i);
            isBound = CustomNotificationBuilder.getInstance().bindRequestService(Centrale_activity.this , i);
        }

    }


    public void defineAlertState(NavigationView navigationView) {
        boolean alert = SettingsManager.getInstance(this).isAddOperationAllowed();
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.alerts).setVisible(alert);
    }

    public void defineSummaryState(NavigationView navigationView) {
        boolean summary = SettingsManager.getInstance(this).isSummaryNeeded();
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.summary).setVisible(summary);
    }

    private void setSelectedCentralFragment(Bundle savedInstanceState) {
        //on definit le fragment approprie a appliquer
        if(currentFragment == null) {
            boolean fromNotification = getIntent().getBooleanExtra(AlertService.FROM_NOTIFICATION , false);
            //si l'activite est lancee depuis la notification.
            boolean summary = SettingsManager.getInstance(this).isSummaryNeeded();
            if(fromNotification) {
                initCentrale(new Actu_route());
            } else {
                //on lance le fragment qui etait actif avant la pause.
                if(savedInstanceState == null) {
                    initCentrale(summary
                            ? new SummaryFragment()
                            : new Actu_route());
                } else {
                    String fragementKey = savedInstanceState.getString("currentFragment");
                    if(fragementKey != null && !TextUtils.isEmpty(fragementKey)) {
                        if(fragementKey.equalsIgnoreCase("Home")) {
                            initCentrale(new Home());
                        } else if(fragementKey.equalsIgnoreCase("Actu")) {
                            initCentrale(new Actu_route());
                        }
                    }
                }
            }
        } else {
            initCentrale(currentFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentFragment instanceof Home) {
            outState.putString("currentFragment" , "Home");
        } else {
            outState.putString("currentFragment" , "Actu");
        }
    }


    public static ProgressBar getLoader_indicator() {
        return loader_indicator;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        if(currentFragment instanceof CentraleInterface) {
            ((CentraleInterface)currentFragment).centraleOnPause();
        }
    }

    @Override
    protected void onResume() {
        isRunning = true;
        super.onResume();
        instance = Centrale_activity.this;
        if(currentFragment instanceof CentraleInterface) {
            ((CentraleInterface)currentFragment).centraleOnResume();
        }
    }

    public void initCentrale(Fragment incommingFragment ) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        this.currentFragment = incommingFragment;
        ft.replace(R.id.centrale_switch , incommingFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    NavigationView.OnNavigationItemSelectedListener onil = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            drawerLayout.closeDrawers();
            if(item.getItemId() == R.id.agences) {
                if(currentFragment != null && !(currentFragment instanceof Home)) {
                    new Switcher().execute(new Home());
                }
            }else if(item.getItemId() == R.id.alerts) {
                if(currentFragment instanceof  Actu_route) {
                    Actu_route ar = (Actu_route) currentFragment;
                    if(!ar.isReadNotified()) {
                        Log.e("test", "onNavigationItemSelected: alerts" );
                        ar = new Actu_route();
                        ar.setReadNotified(true);
                        new Switcher().execute(ar);
                    }
                } else {
                    Actu_route ar = new Actu_route();
                    ar.setReadNotified(true);
                    new Switcher().execute(ar);
                }

            }else if (item.getItemId() == R.id.signalé) {
                if(currentFragment instanceof  Actu_route) {
                    Actu_route ar = (Actu_route) currentFragment;
                    if(ar.isReadNotified()) {
                        Log.e("test", "onNavigationItemSelected: signele" );
                        ar = new Actu_route();
                        new Switcher().execute(ar);
                    }
                } else {
                    Actu_route ar = new Actu_route();
                    new Switcher().execute(ar);
                }
            } else if(item.getItemId() == R.id.ticket) {
                Intent i = new Intent(Centrale_activity.this , TicketActivity.class);
                startActivity(i);
            } else if(item.getItemId() == R.id.summary) {
                if(!(currentFragment instanceof  SummaryFragment)) {
                    new Switcher().execute(new SummaryFragment());
                }
            }
            return false;
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi ) {

        if(mi.getItemId() == R.id.home_settings) {
            startActivity(new Intent(Centrale_activity.this , SettingsActivity.class));
            SettingsManager.getInstance(Centrale_activity.this).initUserInfo();
        }

        if(actionBarDrawerToggle.onOptionsItemSelected(mi)) {
            return true;
        }
        return super.onOptionsItemSelected(mi);
    }
    //les deux methodes suivante sont specifique au sandwich dans le cas de cette classe.

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    public void actuDownloadViaCentrale() {
        if(currentFragment instanceof Actu_route) {
            ((Actu_route)currentFragment).download();
        }
    }

    //dans ce cas static ou pas je ne pense pas qu'il y a une difference le temps nous dira.
    class Switcher extends AsyncTask<Fragment , Void , Void> {
        @Override
        protected Void doInBackground(Fragment... fragments) {
            initCentrale(fragments[0]);
            return null;
        }
    }

}
