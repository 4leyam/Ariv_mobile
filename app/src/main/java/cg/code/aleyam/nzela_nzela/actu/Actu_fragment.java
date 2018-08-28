package cg.code.aleyam.nzela_nzela.actu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.actu.connexion.Post_ancestor;
import cg.code.aleyam.nzela_nzela.actu.map.Positionement;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class Actu_fragment extends Fragment  {


    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    public ActuRecyclerViewAdapter adapter = null;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Actu_fragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Actu_fragment newInstance(int columnCount) {
        Actu_fragment fragment = new Actu_fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_actu, container, false);
        boolean readNotifier = false;
        if(getParentFragment() instanceof Actu_route) {
            Actu_route ar = (Actu_route) getParentFragment();
            readNotifier = ar.isReadNotified();
        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new ActuRecyclerViewAdapter(new ArrayList<Post_ancestor>() , mListener , readNotifier);
            recyclerView.setAdapter(adapter);
            //Download.beginListening(adapter);
            Positionement.getInstance(this.getActivity()).checkSettings(false);

        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        if(Positionement.settingsOk) {
            mListener.download();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Centrale_activity) {
            Fragment fr = ((Centrale_activity) context).currentFragment;
            if( fr instanceof OnListFragmentInteractionListener) {
                mListener = (OnListFragmentInteractionListener) fr;
            }
        } else if(context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener)context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(LatLng coordonnees, String post, int event_type, ArrayList<HashMap<String, Object>> around);
        void download();
    }

}
