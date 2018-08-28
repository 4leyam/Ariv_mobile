package cg.code.aleyam.nzela_nzela.transaction;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import java.util.ArrayList;
import java.util.List;

import cg.code.aleyam.nzela_nzela.R;

import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.data_service.Loader;
import cg.code.aleyam.nzela_nzela.transaction.connexion.DownloadTickets;
import cg.code.aleyam.nzela_nzela.transaction.connexion.TicketObject;

public class TicketFragment extends Fragment implements MaterialViewPagerNotifier{

    RecyclerView.Adapter mAdapter;
    private static int position = 0;


    public static TicketFragment getNewInstance(int position) {
        //TicketFragment.position = position;
        return new TicketFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) (LayoutInflater.from(getActivity()).inflate(R.layout.list_ticket, container , false));
        LinearLayoutManager llm = new LinearLayoutManager(container.getContext() , LinearLayout.VERTICAL , false);
        recyclerView.setLayoutManager(llm);
        Context ct = TicketFragment.this.getActivity();
        if(CommunicationCheck.isConnectionAvalable(ct)) {
            Loader.getInstance(ct).load(true);
        }
        TicketAdapter ta = new TicketAdapter(null , this , TicketFragment.this , position);
        position++;
        //on lance la recuperation des tiquets.




        //penser à passer notre Adapter (ici : TestRecyclerViewAdapter) à un RecyclerViewMaterialAdapter
        mAdapter = new RecyclerViewMaterialAdapter(ta );

        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);


        MaterialViewPagerHelper.registerRecyclerView(getActivity(), recyclerView, null);

        DownloadTickets.getInstance().downloadMyTickects(ta);
        return recyclerView;
    }

    @Override
    public void onPause() {
        super.onPause();
        position = 0;
    }

    @Override
    public void notifyDataSetVariation() {
        mAdapter.notifyDataSetChanged();
    }

    public List<TicketObject> test_object() {

        ArrayList<TicketObject> factice = new ArrayList<>();

        for(int i = 0 ; i < 10 ; i++) {

            factice.add(new TicketObject("Melody express"
                    , "Brazzaville"
                    , "Pointe-Noire"
                    ,"2018-04-25"
                    , "24553-343-4343-3f"
                    , null));

        }

        return factice;
    }

}