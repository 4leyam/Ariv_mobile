package cg.code.aleyam.nzela_nzela.transaction;


import android.app.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.florent37.materialviewpager.MaterialViewPager;

import java.util.ArrayList;

import cg.code.aleyam.nzela_nzela.R;

public class TicketFragmentStatePager extends FragmentStatePagerAdapter {


    ArrayList<Fragment> fragments = new ArrayList<>();
    MaterialViewPager ticketView;

    WorkWithMVP mvpView;

    public TicketFragmentStatePager(FragmentManager fm , WorkWithMVP mvpView) {
        super(fm);
        if(mvpView instanceof WorkWithMVP) {
            this.mvpView = mvpView;
        } else {
            throw new ClassCastException(mvpView.getClass().getName()
                    +" doit implementer l interface "+WorkWithMVP.class.getName());
        }

        this.ticketView = ticketView;
        int i = 0;
        while (i<2) {
            fragments.add(TicketFragment.getNewInstance(i));
            i++;
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Valide";

            case 1:
                return "Expiré";

        }
        return "Oups";
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        mvpView.setPrimaryItem(container , position , object);


    }




}