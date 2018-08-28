package cg.code.aleyam.nzela_nzela.commentaire;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import cg.code.aleyam.nzela_nzela.depart.Depart;
import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.commentaire.dummy.DummyContent.DummyItem;
import cg.code.aleyam.nzela_nzela.data_service.Comment_obj;
import cg.code.aleyam.nzela_nzela.data_service.Commentaire_data;
import cg.code.aleyam.nzela_nzela.data_service.Data_terminal;
import cg.code.aleyam.nzela_nzela.data_service.Loader;
import cg.code.aleyam.nzela_nzela.event.Nzela_objet;
import cg.code.aleyam.nzela_nzela.refresh.Updater;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";
    private RecyclerView recyclerView = null;
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    public ArrayList<Comment_obj> commentaires = new ArrayList();
    CommentRecyclerViewAdapter comment_adapter = null;
    private boolean justeResume = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
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
        View view = inflater.inflate(R.layout.list_commentaire, container, false);

        // Set the adapter

            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.list);
            justeResume = false;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayout.VERTICAL , false));

            comment_adapter = new CommentRecyclerViewAdapter(commentaires , mListener);
            recyclerView.setAdapter(comment_adapter);


        return view;
    }


    @Override
    public void onAttach(Context context) {
        OttoBus.bus.register(ItemFragment.this);
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(CommunicationCheck.isConnectionAvalable(getContext())) {
            //donc si on est connecte
            if(justeResume) {
                //on evite de lancer encore la meme operation de recuperation des commentaire une fois de plus
                Data_terminal.getAgenceComment(Integer.parseInt(Depart.activity_in_info[1]) , getContext() , true );
            }

        } else {
            CommunicationCheck.displayErrorMessage(recyclerView , comment_adapter , null);
        }
        justeResume = true;
    }

    @Override
    public void onDetach() {
        OttoBus.bus.unregister(ItemFragment.this);
        super.onDetach();
        mListener = null;
    }
    @Subscribe
    public void fetchComment(Nzela_objet<Object> n_objet) {
        if(n_objet.ok) {
            Log.e("test" , "recuperation des commentaires");
            Commentaire_data commentaires = (Commentaire_data) n_objet.objet;
            this.commentaires = commentaires.getCommentaire();

            if(this.commentaires.isEmpty() || this.commentaires == null) {
                CommunicationCheck.displayErrorMessage(recyclerView , comment_adapter , this.commentaires.isEmpty() ?
                        "Cette agence n'a aucun commemtaire pour l'instant, vous pouvez etre le premier! :) " : null);
            }

            comment_adapter.setComments(this.commentaires);
            comment_adapter.notifyDataSetChanged();

            //apres faudrait retirer l'instruction au dessu afin de voir comment fonctionne reelement la chose.
        } else {
            Toast.makeText(ItemFragment.this.getContext() , "Probleme d'echange de donnees ", Toast.LENGTH_LONG).show();
        }
        //fin du chargement et mise a l'ecoute des departs.
        Loader.dismiss();
        Updater.listenComments();
    }

    @Subscribe
    public void refreshComment(Integer event_code) {
        //if(event_code == 0) {
            //le code permet d identifier d ou vient l evenement
            Data_terminal.getAgenceComment(Integer.parseInt(Depart.activity_in_info[1]) , getActivity() , true);
        //}
    }


    //methode de teste.

    /*public ArrayList<Comment_obj> getComment(int id_agence) {
        ArrayList<String[]> comments = new ArrayList<>();
        String str = "This interface must be implemented by activities that contain this\n" +
                "      fragment to allow an interaction in this fragment to be communicated\n" +
                "      to the activity and potentially other fragments contained in that\n" +
                "      activity.";

        for(int i = 0 ; i<20 ; i++ ) {
            comments.add(new String[]{
                    ""+(Math.random()*5),
                    str.split(" ")[i],
                    str.replaceAll(""+(char)(97+i) , str),

            });

        }

        return comments;
    }*/

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

        void onListFragmentInteraction(DummyItem item);
    }
}
