package cg.code.aleyam.nzela_nzela.depart;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import cg.code.aleyam.nzela_nzela.R;

public class DepartMoreInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String[] titles = null;
    String[] value_content = null;

    public DepartMoreInfoAdapter(String[] titles , String[] value_content) {
        this.titles = titles;
        this.value_content = value_content;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View vue = inflater.inflate(R.layout.item_depart_more_info, parent, false);
        return new MoreInfoHolder(vue);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MoreInfoHolder) {
            final MoreInfoHolder dpmi = (MoreInfoHolder ) holder;
            dpmi.title.setText(titles[position]);
            dpmi.value_content.setText(value_content[position]);
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    private class MoreInfoHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView value_content;

        public MoreInfoHolder(View v) {
            super(v);
            title = v.findViewById(R.id.info_key);
            value_content = v.findViewById(R.id.info_value);

        }
    }
}
