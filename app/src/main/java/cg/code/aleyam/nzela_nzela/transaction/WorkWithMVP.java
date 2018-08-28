package cg.code.aleyam.nzela_nzela.transaction;

import android.view.ViewGroup;

/**
 * interface permettant de coupler un adapter avec un materialViewPager.
 */
public interface WorkWithMVP{
    void setPrimaryItem(ViewGroup container, int position, Object object);
}