package cg.code.aleyam.nzela_nzela.depart;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.data_service.Nzela_service;
import cg.code.aleyam.nzela_nzela.event.ClicInfo;
import cg.code.aleyam.nzela_nzela.event.Ftb_event;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;


public class DepartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements  Ftb_event.ActionListener , View.OnClickListener{




    //pour eviter les erreurs tous les tableaux doivent avoir la meme taille.
    private String[] depart_h = null;
    private String[] formalite_h = null;
    private String[] prix = null;
    private String[] date = null;
    private String[] y_tarif = null;
    private String[] remain = null;
    private String[] couple_localite = null;
    private String[] image_bus = null;
    private String[] id_tab = null;
    private String[] id_agence = null;

    private Ftb_event ftb_e = new Ftb_event();

    private int compte = 0;
    private ArrayList<ArrayList<Integer>> posis_type = new ArrayList<>();
    //provisoire pour la realisation des tests.
    private Context cont = null;

    private static final int HEADER_TYPE = 1;





     DepartAdapter(ArrayList<String[]> informations_complete/*, int[] vehicule_img*/) {
        super();
         //les donnes sont pas classees de la meme maniere donc il faut les rendre compatible
         init(informations_complete);
        matchData(informations_complete);
        //compte = setInfo(informations_complete );


    }
    private void init(ArrayList<String[]> informations_complete) {
        prix = new String[informations_complete.size()];
        date = new String[informations_complete.size()];
        formalite_h = new String[informations_complete.size()];
        depart_h = new String[informations_complete.size()];
        y_tarif = new String[informations_complete.size()];
        remain = new String[informations_complete.size()];
        couple_localite = new String[informations_complete.size()];
        image_bus = new String[informations_complete.size()];
        id_tab = new String[informations_complete.size()];
        id_agence = new String[informations_complete.size()];
    }
    private void matchData(ArrayList<String[]> informations_complete) {
         for (int i = 0 ; i < informations_complete.size() ; i++) {

             date[i] = informations_complete.get(i)[0];
             formalite_h[i] = informations_complete.get(i)[1];
             depart_h[i] = informations_complete.get(i)[2];
             y_tarif[i] = informations_complete.get(i)[3];
             remain[i] = informations_complete.get(i)[4];
             couple_localite[i] = informations_complete.get(i)[5];
             image_bus[i] = informations_complete.get(i)[6];
             id_tab[i] = informations_complete.get(i)[7];
             id_agence[i] = informations_complete.get(i)[8];
             prix[i] = informations_complete.get(i)[9];

         }
         if(!informations_complete.isEmpty()) {
             posis_type = arrayViewType(couple_localite);
             compte = posis_type.get(0).size();
         }

    }


     private ArrayList<ArrayList<Integer>> arrayViewType(String[] couple_localite) {
         ArrayList<Integer> type = new ArrayList<>();
         ArrayList<Integer> position = new ArrayList<>();
        ArrayList<ArrayList<Integer>> liste = new ArrayList<>();
         String tmp = "";
         int i = 0 , j = 0;
         for(String str:couple_localite) {
             if(!tmp.equals(str)) {
                 tmp = str;
                 type.add(HEADER_TYPE);
                 type.add(0);
                 position.add(j);
                 position.add(i);
             } else {
                 type.add(0);
                 position.add(i);
             }
             i++;
             j++;
         }
         liste.add(type);
         liste.add(position);
         return liste;
    }

    @Override
    public int getItemViewType(int position) {
        if(posis_type.get(0).get(position) == HEADER_TYPE) {
            return HEADER_TYPE;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        //quand il faudra remettre le compte totale il faudrat aussi compter le compte total
        return compte;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        cont = parent.getContext();
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        if (viewType == HEADER_TYPE) {
            return new Header_depart(li.inflate(R.layout.item_headers_depart_layout, parent , false));
        }
        return new Normal_depart(li.inflate(R.layout.item_depart, parent , false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Resources res = cont.getResources();
        if (holder instanceof Header_depart) {
            Header_depart hd = (Header_depart) holder;
            String[] localite = couple_localite[posis_type.get(1).get(position)].split("~");
            String tmp = res.getString(R.string.point_A)+" "+localite[0]+" "+res.getString(R.string.point_B)+" "+localite[1];
            hd.info_localite.setText(tmp);
            hd.titre.setOnClickListener(DepartAdapter.this);

        } else {

            Normal_depart nd = (Normal_depart) holder;
            nd.cv.setOnClickListener(DepartAdapter.this);
            nd.cv.setTag(posis_type.get(1).get(position));
            String URL = Nzela_service.WEBSITE+"web/"+image_bus[posis_type.get(1).get(position)];
            try {

                Picasso
                        .get()
                        .load(URL)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.bus)
                        .error(R.drawable.bus)
                        .into(nd.description);

            } catch (Exception e) {
                Log.e("test", "depart Binded: "+e.getMessage());
            }
            ftb_e.setActionListenr(nd.ftb_info , DepartAdapter.this);
            nd.ftb_info.setTag(posis_type.get(1).get(position));
            String str = "Formalité: "+formalite_h[posis_type.get(1).get(position)];
            nd.formalite.setText(str);
            str = "Départ: "+depart_h[posis_type.get(1).get(position)];
            nd.depart.setText(str);
        }

    }

    @Override
    public void actionPerformed(FloatingTextButton source) {

        if (source.getId() == R.id.bt_info) {
            int position = (Integer) source.getTag();
            eventHandling(position);
        }
    }
    @Override
   public void onClick(View v) {
        if (v.getId() == R.id.cardView ) {
            int position = (int)v.getTag();
            eventHandling(position);
        }
   }

    public void eventHandling(int position) {
        OttoBus.bus.post(new ClicInfo(prix[position]
                , formalite_h[position]
                , depart_h[position]
                , y_tarif[position]
                , remain[position]
                , id_tab[position]
                , Nzela_service.WEBSITE+"web/"+image_bus[position]
                , couple_localite[position]
                , id_agence[position]
                , date[position]));
    }

   //celui la je sais plus trop cherchez!!!
   static class ViewHolder extends RecyclerView.ViewHolder{
       ViewHolder(View itemView){
           super(itemView);
       }
   }
   //celui qui contient les departs (le premiere information des departs)
   public class Normal_depart extends ViewHolder{

        AppCompatTextView formalite = null;
        AppCompatTextView depart = null;
        AppCompatImageView description = null;
        FloatingTextButton ftb_info = null;
        CardView cv = null;

         Normal_depart(View header) {
            super(header);
            description = itemView.findViewById(R.id.bus_image);
            depart =  itemView.findViewById(R.id.depart_view);
            formalite =  itemView.findViewById(R.id.formalite_view);
            ftb_info =  itemView.findViewById(R.id.bt_info);
            cv = itemView.findViewById(R.id.cardView);
        }
   }
   //celui qui contient la vue des destination des groupes de depart
   public class Header_depart extends ViewHolder{
        TextView info_localite = null;
        CardView titre = null;
         Header_depart(View itemView) {
            super(itemView);
            info_localite = itemView.findViewById(R.id.texte_entete);
            titre = itemView.findViewById(R.id.titre);

       }
   }
}