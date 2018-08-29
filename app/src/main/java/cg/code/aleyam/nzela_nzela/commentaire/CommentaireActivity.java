package cg.code.aleyam.nzela_nzela.commentaire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import cg.code.aleyam.nzela_nzela.depart.Depart;
import cg.code.aleyam.nzela_nzela.depart.InfoGenFragment;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.commentaire.dummy.DummyContent;
import cg.code.aleyam.nzela_nzela.data_service.Data_terminal;
import cg.code.aleyam.nzela_nzela.data_service.Loader;


public class CommentaireActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    public static boolean RUN = false;
    public static String agence_concernee = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Loader.getInstance(this).load(RUN);
        setContentView(R.layout.activity_commentaire);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        RUN = true;
       initComment(i.getStringArrayExtra(InfoGenFragment.info_key));

    }

    private void initComment(String[] extraData) {

        agence_concernee = extraData[0];
        //Toast.makeText(CommentaireActivity.this , "recuperation des commentaires de l agence: "+extraData[1] , Toast.LENGTH_SHORT).show();
        Data_terminal.getAgenceComment(Integer.parseInt(Depart.activity_in_info[1]) , CommentaireActivity.this , CommentaireActivity.RUN );


        getSupportActionBar().setTitle("Commentaire sur "+agence_concernee);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onPause() {
        RUN = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        RUN = true;
        super.onResume();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home ) {
            finish();
        }
        return true;
    }
}

