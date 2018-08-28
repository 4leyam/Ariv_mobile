package cg.code.aleyam.nzela_nzela.actu;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.actu.constatFragment.OnListFragmentInteractionListener;


import java.util.Map;


public class MyconstatRecyclerViewAdapter extends RecyclerView.Adapter<MyconstatRecyclerViewAdapter.ViewHolder> {

    private String[] main_event ;
    private final OnListFragmentInteractionListener mListener;
    private Map contenu_event ;
    private int selected = -1;

    public MyconstatRecyclerViewAdapter(String[] main_event , OnListFragmentInteractionListener listener) {
        this.main_event = main_event;
        mListener = listener;
        this.contenu_event = contenu_event;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_constat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //holder.event_img.setIconDrawable();

        int selected_index = 0;
        switch (position) {
            case 0:
                Picasso.get().load(R.drawable.policier).fit().centerCrop().error(R.drawable.policier).placeholder(R.drawable.policier).into(holder.event_img);
                selected_index = 0;
                break;
            case 1:
                Picasso.get().load(R.drawable.accident).fit().centerCrop().error(R.drawable.accident).placeholder(R.drawable.accident).into(holder.event_img);
                selected_index = 1;
                break;
            case 2:
                Picasso.get().load(R.drawable.traveaux).fit().centerCrop().error(R.drawable.traveaux).placeholder(R.drawable.traveaux).into(holder.event_img);
                selected_index = 2;
                break;
            case 3:
                Picasso.get().load(R.drawable.station).fit().centerCrop().error(R.drawable.station).placeholder(R.drawable.station).into(holder.event_img);
                selected_index = 3;
                break;
            case 4:
                Picasso.get().load(R.drawable.incivisme).fit().centerCrop().error(R.drawable.incivisme).placeholder(R.drawable.incivisme).into(holder.event_img);
                selected_index = 4;
                break;
            case 5:
                Picasso.get().load(R.drawable.danger).fit().centerCrop().error(R.drawable.danger).placeholder(R.drawable.danger).into(holder.event_img);
                selected_index = 5;
                break;
            case 6:
                Picasso.get().load(R.drawable.interdit).fit().centerCrop().error(R.drawable.interdit).placeholder(R.drawable.interdit).into(holder.event_img);
                selected_index = 6;
                break;

        }
        if(position == selected)
            holder.card_constat.setCardBackgroundColor(Color.rgb(100 , 159 , 250));
        else
            holder.card_constat.setCardBackgroundColor(Color.rgb(250 , 250 , 250));

        final int tmp = selected_index;
        holder.description.setText(main_event[position]);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    selected = tmp;
                    mListener.onListFragmentInteraction(tmp);
                    notifyDataSetChanged();

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return main_event.length;
    }

    public String[] getMain_event() {
        return main_event;
    }

    public void setMain_event(String[] main_event) {

        this.main_event = main_event;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public ImageView event_img;
        public TextView description;
        public CardView card_constat ;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            card_constat =  view.findViewById(R.id.event);
            event_img =  view.findViewById(R.id.event_img);
            description =  view.findViewById(R.id.description);
        }


    }
}
