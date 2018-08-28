package cg.code.aleyam.nzela_nzela.transaction;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.data_service.Agence_data;
import cg.code.aleyam.nzela_nzela.data_service.Agence_first_info;
import cg.code.aleyam.nzela_nzela.data_service.Data_terminal;
import cg.code.aleyam.nzela_nzela.data_service.Depart_data;
import cg.code.aleyam.nzela_nzela.data_service.Depart_item;
import cg.code.aleyam.nzela_nzela.data_service.Loader;
import cg.code.aleyam.nzela_nzela.data_service.Nzela_service;
import cg.code.aleyam.nzela_nzela.transaction.connexion.DownloadCallback;
import cg.code.aleyam.nzela_nzela.transaction.connexion.TicketObject;
import cg.code.aleyam.nzela_nzela.transaction.connexion.TransactionGet;
import cg.code.aleyam.nzela_nzela.transaction.connexion.TransactionPost;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        DownloadCallback {

    List<TicketObject> ticketListe = null;

    ArrayList<Integer> invalide  = new ArrayList<>();
    private final int NO_TITEM_TYPE = 23;
    private String message = "Aucun Ticket pour l'instant veuillez selectionner une destination," +
            " reservez ou achetez votre place puis vous verrez apparaitre vos tickets par ici :) ";
    int position_adapter = 0;
    private boolean tryOption = false;
     MaterialViewPagerNotifier notifier;
     TicketFragment ticketFragment = null;
    int position = 0;

    public TicketAdapter(List<TicketObject> ticketListe ,  MaterialViewPagerNotifier notifier , TicketFragment ticketFragment , int position) {
        this.ticketListe = ticketListe;
        this.notifier = notifier;
        this.ticketFragment = ticketFragment;

        this.position_adapter = position;
        Log.e(  "test" , "position adapter "+position_adapter );

    }

    @Override
    public int getItemViewType(int position) {
        if(ticketListe == null || ticketListe.isEmpty()) {
            return NO_TITEM_TYPE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if(ticketListe == null || ticketListe.isEmpty()) {
            return 1;
        }
        return ticketListe.size();
    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater linf = LayoutInflater.from(parent.getContext());

        View ticketItem = null;

        if(viewType == NO_TITEM_TYPE) {

            ticketItem = linf.inflate(R.layout.communition_error , parent ,false);
            return new CommunicationCheck.NoConnexionHolder(ticketItem);
        }
        ticketItem = linf.inflate(R.layout.item_ticket, parent ,false);
        return new TicketViewHolder(ticketItem);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof TicketViewHolder) {
            //donc on a bien du contenu
            TicketObject to = ticketListe.get(position);
            TicketViewHolder m_holder = (TicketViewHolder)holder;
            m_holder.code_transaction.setText(to.getCode_transaction());
            m_holder.destination.setText("De "+to.getLieu_amont()+" pour "+to.getLieu_aval());
            m_holder.agence_name.setText(to.getNom_agence());
            String time = SimpleDateFormat.getDateTimeInstance().format(new Date(Long.parseLong(to.getDate())));

            m_holder.date.setText(time);
            String URLlogo = Nzela_service.WEBSITE+"web/"+to.getUrl_image_agence();

            Picasso.get()
                    .load(URLlogo)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.company_placeholder)
                    .placeholder(R.drawable.company_placeholder)
                    .into(m_holder.logo_agence);




             m_holder.indicator.setImageResource(to.reservation?R.drawable.ic_pets:R.drawable.ic_monetization);


        } else {

            //TODO prendre compte du mode offline.
            CommunicationCheck.NoConnexionHolder erreur = (CommunicationCheck.NoConnexionHolder)holder;

            erreur.card.setCardBackgroundColor(Color.TRANSPARENT);
            erreur.setErrorMessage(message , !tryOption);

        }


    }

    @Override
    public void added(TransactionPost new_data) {

        if(new_data instanceof TransactionGet ) {
            TransactionGet data = (TransactionGet) new_data;
            if(ticketListe == null) ticketListe = new ArrayList<>();

            TicketObject to = new TicketObject("Chargement..."
                    , "..."
                    , "..."
                    ,  ""+data.getDate()
                    , data.getCode()
                    , "no_url");
            to.reservation = data.isReservation();

            ticketListe.add(to);
            Data_terminal.getOneDepart(data.getId_depart() , new DepartCallback(to , position_adapter ));

            notifyDataSetChanged();
            this.notifier.notifyDataSetVariation();

        }


    }

    @Override
    public void failed(String message, boolean tryOption) {
        ticketListe = null;
        this.message = message;
        this.tryOption = tryOption;
        notifyDataSetChanged();
        this.notifier.notifyDataSetVariation();
        Loader.dismiss();
    }

    @Override
    public void downloaded(ArrayList<? extends TransactionPost> data) {

        if(ticketListe == null)ticketListe = new ArrayList<>();
        for(int i = 0 ; i < data.size() ; i ++) {
            TransactionGet tp = (TransactionGet) data.get(i);

            TicketObject to = new TicketObject("Chargement..."
                    , "..."
                    , "..."
                    ,  ""+tp.getDate()
                    , tp.getCode()
                    , "no_url");
            to.reservation = tp.isReservation();

            ticketListe.add(to);
            Data_terminal.getOneDepart(tp.getId_depart() , new DepartCallback(to , position_adapter ));



        }
        notifyDataSetChanged();
        this.notifier.notifyDataSetVariation();
        Loader.dismiss();
    }


    public class DepartCallback implements Callback<Depart_data<Depart_item>> {

        TicketObject position;
        int invalide = 0;
        public DepartCallback(TicketObject position , int invalide ) {
            this.invalide = invalide;
            this.position = position;
        }

        @Override
        public void onFailure(Call<Depart_data<Depart_item>> call, Throwable t) {
            Toast.makeText(ticketFragment.getActivity() , "Erreur lors de la recuperation des info complementaire de depart" , Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(Call<Depart_data<Depart_item>> call, Response<Depart_data<Depart_item>> response) {

            Depart_data<Depart_item> dd = response.body();
            if(dd != null) {
                ArrayList<Depart_item> depart_liste;
                if(dd.getDepart() != null && dd.getDepart().size() != 0) {
                    depart_liste = dd.getDepart();
                    Depart_item di = depart_liste.get(0);

                    if(di.valide == invalide) {
                        Log.e("test", ticketListe.size()+"invalide: "+invalide);
                        int index = ticketListe.indexOf(this.position);
                        if(index != -1 && index < ticketListe.size()) {
                            ticketListe.remove(index);
                        }

                        Log.e("test", ticketListe.size()+"supp: "+invalide);
                    } else {
                        TicketObject to = ticketListe.get(ticketListe.indexOf(this.position));
                        to.setLieu_amont(di.getLieu_amont());
                        to.setLieu_aval(di.getLieu_aval());
                        to.valide = di.valide;
                        Data_terminal.getOneAgence(di.getId_agence() , new AgenceCallback(ticketListe.indexOf(this.position)) );
                    }
                    //puis on recupere aussi les info depart


                    //on met a jours les modifications
                    notifier.notifyDataSetVariation();
                    TicketAdapter.this.notifyDataSetChanged();
                }

            }
        }

    }



    public  class AgenceCallback implements Callback<Agence_data> {
        int position;

        public AgenceCallback(int position ) {

            this.position = position;

        }



        @Override
        public void onResponse(Call<Agence_data> call, Response<Agence_data> response) {
            Agence_data ad = response.body();
            ArrayList<Agence_first_info> agence_liste = ad.getAgences();
            if(agence_liste != null && !agence_liste.isEmpty()) {
                Agence_first_info fi = agence_liste.get(0);
                TicketObject to = ticketListe.get(position);
                Log.e("test", "url set: "+fi.getLogo_agence());
                to.setUrl_image_agence(fi.getLogo_agence());
                to.setNom_agence(fi.getNom_agence());
                notifier.notifyDataSetVariation();
                TicketAdapter.this.notifyDataSetChanged();
                notifier.notifyDataSetVariation();
            }
        }

        @Override
        public void onFailure(Call<Agence_data> call, Throwable t) {
            Toast.makeText(ticketFragment.getActivity() , "Erreur lors de la recuperation complementaire d'agence" , Toast.LENGTH_SHORT).show();
        }
    };

    class TicketViewHolder extends RecyclerView.ViewHolder {

        ImageView logo_agence;
        TextView agence_name;
        TextView destination;
        TextView code_transaction;
        TextView date;
        ImageView indicator;

        public TicketViewHolder(View vue) {
            super(vue);
            logo_agence = vue.findViewById(R.id.agence_description_ticket);
            agence_name = vue.findViewById(R.id.nom_agence_tickets);
            destination = vue.findViewById(R.id.destinations_tickets);
            code_transaction = vue.findViewById(R.id.id_transaction_ticket);
            date = vue.findViewById(R.id.date_tickets);
            indicator = vue.findViewById(R.id.indicator);
        }
    }
}