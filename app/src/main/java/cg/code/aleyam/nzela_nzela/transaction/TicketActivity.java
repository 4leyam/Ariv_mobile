package cg.code.aleyam.nzela_nzela.transaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.florent37.materialviewpager.MaterialViewPager;

import cg.code.aleyam.nzela_nzela.R;

public class TicketActivity extends AppCompatActivity implements WorkWithMVP {

    public static int NAV_COUNT = 2;
    private MaterialViewPager ticketView;
//    private View logo ;
//    private ImageView imageLogo;

    int backItem = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);


        ticketView = findViewById(R.id.materialviewpager);
        Toolbar tb = ticketView.getToolbar();
        if(tb != null) {
            setSupportActionBar(tb);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        logo = findViewById(R.id.ticket_logo);
//        imageLogo = findViewById(R.id.ticket_logo_content);

        ViewPager pager = ticketView.getViewPager();

        pager.setAdapter(new TicketFragmentStatePager(getSupportFragmentManager() , this));
        pager.setOffscreenPageLimit(NAV_COUNT);
        ticketView.getPagerTitleStrip().setViewPager(pager);


    }

    int oldItemPosition = -1;
    public void setPrimaryItem(ViewGroup container, int position, Object object) {


//seulement si la page est différente
        if (oldItemPosition != position) {
            oldItemPosition = position;

            //définir la nouvelle couleur et les nouvelles images

            int color = Color.BLACK;
            Drawable pagerBack = null;

            switch (position) {
                case 0:
                    color = Color.parseColor("#FCE32D");

                    pagerBack = getResources().getDrawable(R.drawable.valide);
                    break;
                case 1:

                    pagerBack = getResources().getDrawable(R.drawable.expire);
                    color = Color.rgb(155, 155, 155);
                    break;

            }

            //puis modifier les images/couleurs
            int fadeDuration = 1;
            ticketView.setColor(color, fadeDuration);
            ticketView.setImageDrawable(pagerBack, fadeDuration);
            //toggleLogo(newDrawable, color, fadeDuration);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
