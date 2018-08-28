package cg.code.aleyam.nzela_nzela.home;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.check.CommunicationCheck;
import cg.code.aleyam.nzela_nzela.data_service.Agence_first_info;
import cg.code.aleyam.nzela_nzela.data_service.Nzela_service;
import cg.code.aleyam.nzela_nzela.event.Ftb_event;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class Home_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, Ftb_event.ActionListener , CommunicationCheck.ConnexionError {

    private ArrayList<Agence_first_info> list_agence_info = new ArrayList<>();
    private static int ERROR_TYPE = 1 ;
    private String errorMessage = null;
    Ftb_event event = new Ftb_event();

    public Home_adapter() {
        super();
    }

    public void setList_agence_info(ArrayList<Agence_first_info> list_agence_info) {
        this.list_agence_info = list_agence_info;
        Home_adapter.this.notifyDataSetChanged();
    }

    public ArrayList<Agence_first_info> getList_agence_info() {
        return list_agence_info;
    }

    @Override
    public int getItemViewType(int position) {

        if(list_agence_info == null || list_agence_info.isEmpty()) {
            return ERROR_TYPE;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == ERROR_TYPE) {
            View error_view = inflater.inflate(R.layout.communition_error , parent , false);
            return new CommunicationCheck.NoConnexionHolder(error_view);
        } else {
            View main_view = inflater.inflate(R.layout.item_agence, parent , false);
            return new MyViewHolder(main_view);
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder prime_holder, int position) {



        if(prime_holder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) prime_holder;
            holder.appercu_agence.setTag(position);
            holder.cote.setRating(list_agence_info.get(position).getAvis());
            holder.nom_agence.setText(list_agence_info.get(position).getNom_agence());
            String partURL = list_agence_info.get(position).getLogo_agence();

            String URLlogo = Nzela_service.WEBSITE+"web/"+partURL;
            Picasso
                    .get()
                    .load(URLlogo)
                    .fit()
                    .placeholder(R.drawable.company_placeholder)
                    .error(R.drawable.company_placeholder)
                    .into(holder.logo_agence);

            Bitmap logo = holder.logo_agence.getDrawingCache();
        } else {
            CommunicationCheck.NoConnexionHolder erreur = (CommunicationCheck.NoConnexionHolder)prime_holder;
            if(list_agence_info != null) {

                erreur.message.setText("");
                erreur.reessayer.setVisibility(View.INVISIBLE);
                erreur.card.setBackgroundColor(Color.TRANSPARENT);

            } else {

                if(errorMessage != null) {
                    erreur.reessayer.setVisibility(View.VISIBLE);
                    erreur.card.setBackgroundColor(Color.WHITE);
                    erreur.message.setText(errorMessage);
                    erreur.reessayer.setText("Reessayer");
                    erreur.reessayer.setOnClickListener(this);
                    //event.setActionListenr(erreur.reessayer , Home_adapter.this);

                }

            }

        }



    }


    @Override
    public int getItemCount() {
        if(list_agence_info == null) return 1;
        if(list_agence_info.isEmpty()) return 1;
        else return list_agence_info.size();
    }
    public void setErrorMessage(String errorMessage) {

        this.errorMessage = errorMessage;
        list_agence_info = null;
        notifyDataSetChanged();
    }


     class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView appercu_agence = null;
        public AppCompatRatingBar cote = null;
        public ImageView logo_agence = null;
        public TextView nom_agence = null;


        public MyViewHolder(View mainView) {
            super(mainView);
            appercu_agence = mainView.findViewById(R.id.agence_item);
            cote = mainView.findViewById(R.id.appreciation);
            logo_agence =  mainView.findViewById(R.id.logo_agence);
            nom_agence = mainView.findViewById(R.id.agence_name);
            appercu_agence.setOnClickListener(Home_adapter.this);

        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.retry_button) {
            //click survenant au cas ou il y aurait une erreur de connection
            OttoBus.bus.post(errorMessage);
        } else {
            //on publie l element y compris un enregistrement de premiere information afin de permettre d autre traitements.
            Agence_first_info info = list_agence_info.get((Integer)v.getTag());
            OttoBus.bus.post(info);
        }



    }

    @Override
    public void actionPerformed(FloatingTextButton source) {

    }
}
