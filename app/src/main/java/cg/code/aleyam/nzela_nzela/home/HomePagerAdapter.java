package cg.code.aleyam.nzela_nzela.home;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import cg.code.aleyam.nzela_nzela.R;

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    private final String PAGE_TITLE[] = {"Compagnies" , "Comparateur"};
    private TabLayout tabLayout = null;

    public HomePagerAdapter(FragmentManager fm , TabLayout tabLayout) {
        super(fm);
        this.tabLayout = tabLayout;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
            switch (position) {
                case 0:
                    //donc il faut aficher les differentes agences on donne donc le fragment qu'il faut.
                    tabLayout.getTabAt(position).setIcon(R.drawable.ic_location_city_black_24dp);
                    return new Home_fragment();
                case 1:
                    //on va afficher le filtre il faut donc recuperer le fragment adequat.
                    tabLayout.getTabAt(position).setIcon(R.drawable.ic_filter_tilt_shift_black_24dp);
                    return new FilterFragment();
            }
        } catch (Exception e) {
            Log.e("test", "getItem: Home icon null");
        }

        //avec beaucoup de chance ce cas n'arrivera jamais.
        return new Fragment();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLE[position];

    }
}
