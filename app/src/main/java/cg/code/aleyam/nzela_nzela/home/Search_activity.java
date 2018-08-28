package cg.code.aleyam.nzela_nzela.home;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class Search_activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        traitement_recherche(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //cette methode permet de definir l action a faire quand un nouvelle intent implicite se presente.
        super.onNewIntent(intent);
        traitement_recherche(getIntent());

    }

    private void traitement_recherche(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //donc si l'intent implicite recu a pour action la recherche on est preneur.
            rechercher(intent.getStringExtra(SearchManager.QUERY));
            //la cle pour recuperer le mot rechercher se trouve dans sm.query.
        }
    }
    private boolean rechercher(String mot_cle) {
        boolean found = false;
        Toast.makeText(getApplicationContext() , ""+mot_cle , Toast.LENGTH_SHORT).show();
        return found;
    }
}