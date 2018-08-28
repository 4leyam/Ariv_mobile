package cg.code.aleyam.nzela_nzela.actu;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.actu.Actu_fragment.OnListFragmentInteractionListener;
import cg.code.aleyam.nzela_nzela.actu.connexion.ActuObject;
import cg.code.aleyam.nzela_nzela.actu.connexion.DownloadStateShare;
import cg.code.aleyam.nzela_nzela.actu.connexion.Post_ancestor;
import cg.code.aleyam.nzela_nzela.actu.connexion.RateNotifier;
import cg.code.aleyam.nzela_nzela.actu.connexion.Upload;
import cg.code.aleyam.nzela_nzela.actu.map.MapsActivity;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class ActuRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        DownloadStateShare ,
        RateNotifier{

    private static final int ERROR_TYPE = 1 , EMPTY_ERROR = 2;
    public List<? extends Post_ancestor> post_info = new LinkedList<>();
    private final OnListFragmentInteractionListener mListener;
    private Actu_route ar = null;
    private ViewHolder holder = null;
    private boolean empty = false;
    private boolean loading = false;
    private String message = "Pas de contenu pour l'instant, Actualisez votre position";
    private boolean readNotifier = false;


    public ActuRecyclerViewAdapter(List<? extends Post_ancestor> post_info, OnListFragmentInteractionListener listener , boolean readNotifier) {
        this.post_info = post_info;
        mListener = listener;
        this.readNotifier = readNotifier;
        if(mListener instanceof Actu_route) {
            ar = (Actu_route)mListener;
        }
    }

    @Override
    public void onPostedRemoved() {
        loading = false;
        notifyDataSetChanged();
    }

    @Override
    public void onPostAdded() {
        loading = false;
        notifyDataSetChanged();
    }

    @Override
    public void onPostChanged() {
        loading = false;
        notifyDataSetChanged();
    }

    @Override
    public void onRequestFail() {
        message = "Probleme de communication sur le reseau , reesayer pour voire!";
        post_info = null;
        ar.enableLoad(false , null);
        loading = false;
        notifyDataSetChanged();

    }

    @Override
    public void notifyDataReady(List<? extends Post_ancestor> new_data) {

        message = "Pas de contenu pour l'instant, Actualisez votre position";
        post_info = new_data;
        Log.e("test", "third");

            if(ar == null)
                ar = (Actu_route)mListener;

            //arrete le chargement.
            ar.enableLoad(false , null);


        loading = false;
        notifyDataSetChanged();


    }

    public Activity getActivity() {
        return ((Actu_route)mListener).getActivity();
    }

    @Override
    public int getItemViewType(int position) {
        if(post_info == null) {
            return ERROR_TYPE;
        } else if(post_info.isEmpty()) {
            return EMPTY_ERROR;
        } else return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view ;
        if(viewType == ERROR_TYPE || viewType == EMPTY_ERROR) {
            empty = viewType == EMPTY_ERROR;
            view = inflater.inflate(R.layout.communition_error , parent , false);
            return new CommunicationCheck.NoConnexionHolder(view);
        } else {
            view =  inflater.inflate(R.layout.item_actu, parent, false);
            return new ViewHolder(view , readNotifier);
        }



    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder up_holder, int position) {
        //pour ce qui concerne le nom de l agence et le reste c'est a plus tard
        //holder.agence_name.setText(post_info.get(position).getImage());
        if(up_holder instanceof ViewHolder) {

            ViewHolder holder = (ViewHolder) up_holder;

            this.holder = holder;
            Post_ancestor pa = post_info.get(position);

            ar.enableLoad(false , null);

            if(pa instanceof ActuObject) {

                final ActuObject ao = (ActuObject) pa;

                try {

                    Picasso
                            .get()
                            .load(ao.getImage())
                            .fit()
                            .centerCrop()

                            .error(R.drawable.post_placeholder)
                            .into(holder.post_image);

                    Bitmap logo = holder.post_image.getDrawingCache();

                    Picasso
                            .get()
                            .load(MapsActivity.getPicker(ao.getType()))
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.ic_stars_black_24dp)
                            .error(R.drawable.ic_error_black_24dp)
                            .into(holder.type);



                } catch (Exception e) {

                }
                holder.user_name.setText(ao.getOwner());
                holder.info_post.setText(ao.getPost_text());
                holder.metadata.setText(formatPostDate(new Date(ao.getDate())));
                holder.ratedCount.setText(ao.getVotes()+" avi(s)");
                holder.pertinance_neter.setRating(ao.getPertinance());

                holder.mView.setTag(position);
                holder.info_post.setTag(position);
                holder.post_image.setTag(position);


                holder.info_post.setOnClickListener(click);
                holder.post_image.setOnClickListener(click);
                holder.mView.setOnClickListener(click);

                holder.rate_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ar != null) {

                            View d_content_v = LayoutInflater.from(ar.getContext()).inflate(R.layout.dialog_event_rater, null);
                            Button submit = d_content_v.findViewById(R.id.submit_avis);
                            final RatingBar rb = d_content_v.findViewById(R.id.vote);
                            final Dialog dialog = new Dialog(ar.getContext());
                            dialog.setContentView(d_content_v);
                            dialog.setTitle("Vote");
                            dialog.setCancelable(true);
                            dialog.show();

                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Upload.getInstance(ar.getActivity()).voteEvent(ao , (int)rb.getRating() , ActuRecyclerViewAdapter.this);
                                    dialog.dismiss();
                                }
                            });

                        }

                    }
                });

            }

        } else {
            if (empty) {
                if(up_holder instanceof CommunicationCheck.NoConnexionHolder) {
                    CommunicationCheck.NoConnexionHolder errorHolder = (CommunicationCheck.NoConnexionHolder) up_holder;
                    if(!loading) {

                        if(readNotifier)
                            message = "Pas d'alerts pour l'instant, une fois qu'un evenement important sera Publié nous vous tiendrions au courant.";
                        errorHolder.setErrorMessage(message , readNotifier);
                        errorHolder.reessayer.setText("Actualiser");
                        errorHolder.card.setBackgroundColor(Color.TRANSPARENT);
                        if(!readNotifier) {
                            errorHolder.setRetryAction(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    ar.enableLoad(true , null);
                                    loading = true;
                                }
                            });
                        }

                        //loading = true;
                    } else {

                        errorHolder.setErrorMessage("chargement de votre postion..." , true);
                    }


                }
            }
        }



    }

    //View.OnClickListener card_rate_listner = ;

    @Override
    public void notAllow() {
        final Snackbar sn = Snackbar.make(this.holder.type , "Merci pour votre participation mais vous avez déjà voté :)" , Snackbar.LENGTH_SHORT);
        sn.show();
        sn.setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sn.dismiss();
                    }
                });

    }

    @Override
    public void rateDone() {
        final Snackbar sn = Snackbar.make(this.holder.type , "vote effectué avec succes :)" , Snackbar.LENGTH_SHORT);
        sn.show();
        sn.setAction("ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sn.dismiss();
            }
        });
    }

    @Override
    public void rateFailure() {
        final Snackbar sn = Snackbar.make(this.holder.type , "Impossible de voter veuillez verifier votre connexion Internet" , Snackbar.LENGTH_SHORT);
        sn.show();
        sn.setAction("ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sn.dismiss();
            }
        });
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Post_ancestor pa = post_info.get(Integer.parseInt(""+v.getTag()));
            if (null != mListener && pa instanceof ActuObject) {
                ActuObject ao = (ActuObject) pa;
                LatLng current_position = new LatLng(ao.getLat() , ao.getLng());
                mListener.onListFragmentInteraction(current_position
                        , ao.getPost_text()
                        , ao.getType()
                        , buildAroundEventCollection(post_info));
            }
        }
    };

    public  ArrayList<HashMap<String , Object>> buildAroundEventCollection(List<? extends Post_ancestor> post_info) {

        ArrayList<HashMap<String , Object>> aroundEvent = new ArrayList<>();
        for(int i = 0 ; i<post_info.size() ; i++) {
            ActuObject ao = (ActuObject) post_info.get(i);
            HashMap<String , Object> tmp_data = new HashMap();
            tmp_data.put("coordonnees" , new LatLng(ao.getLat() , ao.getLng()));
            tmp_data.put("post" , ao.getPost_text());
            tmp_data.put("type" , ao.getType());
            aroundEvent.add(tmp_data);
        }
        return aroundEvent;
    }

    @Override
    public int getItemCount() {

        //if(post_info.size() == 0 || post_info.isEmpty()) return 1;
        if(post_info == null || post_info.isEmpty()) return 1;
        return post_info.size();

    }

    public String formatPostDate(Date date) {

        Long wastedTime = new Date().getTime() - date.getTime();

        if(wastedTime < (60*1000) ) {
            return "à l'instant";
        } else if(wastedTime < (1000*60*60) ) {
            return "il y a "+wastedTime/(1000*60)+"min";
        } else if(wastedTime < (1000*60*60*24)) {
            return "il y a "+wastedTime/(1000*60*60)+"h";
        }else if(wastedTime >= (1000*60*60*24)) {
            return "il y a "+wastedTime/(1000*60*60*24)+"j";
        }  else {
            return "";
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public ImageView post_image;
        public TextView user_name;
        public TextView agence_name;
        public TextView info_post;
        public TextView metadata;
        public ImageView type;
        public CardView rate_card;
        public RatingBar pertinance_neter;
        public TextView ratedCount;
        public CardView cardItem;


        public ViewHolder(View view , boolean readNotified) {
            super(view);
            mView = view;
            if(!readNotified) {
                (mView.findViewById(R.id.alertBadg))
                        .setVisibility(View.GONE);
            }
            user_name = view.findViewById(R.id.user_name);
            agence_name = view.findViewById(R.id.agence_name_actu);
            metadata = view.findViewById(R.id.post_meta_data);
            info_post = view.findViewById(R.id.info_post);
            post_image = view.findViewById(R.id.logo_agence);
            type = view.findViewById(R.id.event_type);
            rate_card = view.findViewById(R.id.event_type_bt);
            pertinance_neter = view.findViewById(R.id.pertinance_meter);
            ratedCount = view.findViewById(R.id.rated_count);
        }

    }
}
