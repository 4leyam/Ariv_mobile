package cg.code.aleyam.nzela_nzela.commentaire;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.commentaire.ItemFragment.OnListFragmentInteractionListener;
import cg.code.aleyam.nzela_nzela.commentaire.dummy.DummyContent.DummyItem;
import cg.code.aleyam.nzela_nzela.data_service.Comment_obj;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 *
 */
public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CommunicationCheck.ConnexionError , View.OnClickListener{

    private  List<Comment_obj> comments = new ArrayList<>();
    private final OnListFragmentInteractionListener mListener;
    private final int vide_type = 2;
    String errorMessage = new String();


    @Override
    public int getItemViewType(int position) {
        if (comments == null || comments.isEmpty()) {
            //donc si les commentaires sont vide ou si l objet comment est inexistant
            return vide_type;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public void setErrorMessage(String message ) {

        this.errorMessage = message;
        comments = null;
        notifyDataSetChanged();
    }

    public CommentRecyclerViewAdapter(List<Comment_obj> comments, OnListFragmentInteractionListener listener) {
        this.comments = comments;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View vue = null;
        if(viewType == vide_type) {
            //inflation dans le cas ou la liste de commentaire est nulle
            vue = inflater.inflate(R.layout.communition_error , parent , false);
            return new CommunicationCheck.NoConnexionHolder(vue);
        } else {
            vue = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentHolder(vue);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof CommunicationCheck.NoConnexionHolder) {
            //ici NoConnexionHolder fait reference a une liste vide de commentaire.
            //il permet donc d'afficher le message pas de commentaires.

            CommunicationCheck.NoConnexionHolder vide_holder = (CommunicationCheck.NoConnexionHolder) holder;

            if(errorMessage != null) {
                vide_holder.message.setText(errorMessage);
                vide_holder.reessayer.setVisibility(View.INVISIBLE);
                String message = vide_holder.message.getText().toString().trim();
                if(!message.endsWith(")") && !message.equals("")) {

                    vide_holder.reessayer.setVisibility(View.VISIBLE);
                    vide_holder.reessayer.setText("Reessayer");

                }
                vide_holder.reessayer.setOnClickListener(this);

            }


        } else {

            CommentHolder comment_holder = (CommentHolder) holder;
            if(!comments.isEmpty()) {

                comment_holder.nom.setText(comments.get(position).getPseudo());
                comment_holder.avis.setRating(comments.get(position).getAvis());
                comment_holder.commentaire.setText(comments.get(position).getCommentaire_texte());

            }

        }


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.retry_button) {
            //donc si on retry
            OttoBus.bus.post(new Integer(0));
        }
    }

    public void setComments(ArrayList<Comment_obj> comments) {
        Log.e("test" , "Mise a jours des commentaires.");
        for (Comment_obj obj:
             comments) {

            Log.e("test" , ""+obj.getCommentaire_texte());

        }
        this.comments = comments;
        this.notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(comments == null || comments.isEmpty() ) return 1;
        return comments.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView nom;
        public TextView commentaire;
        public RatingBar avis;
        public View element;


        public CommentHolder(View element) {
            super(element);
            avatar = element.findViewById(R.id.id_cv_image);
            CommentHolder.this.element = element;
            avis = element.findViewById(R.id.avis_comment);
            nom =  element.findViewById(R.id.user_name);
            commentaire = element.findViewById(R.id.comment_content);
           // mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return "";
            //return super.toString() + " '" + mContentView.getText() + "'";
        }
    }



}











