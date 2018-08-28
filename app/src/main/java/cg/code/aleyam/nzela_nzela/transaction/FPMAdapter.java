package cg.code.aleyam.nzela_nzela.transaction;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.enregistrement.Register;

public class FPMAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


   private int[] fpms_ico_id = null;
   private int[] fpm = null;

   public FPMCallback fpmCallback;



    public FPMAdapter(int[] fpms_ico_id , int[] fpm , FPMCallback fpmCallback) {
        this.fpm = fpm;
        this.fpms_ico_id = fpms_ico_id;
        this.fpmCallback = fpmCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View vue = inflater.inflate(R.layout.item_payment_option, parent , false);
        return new LocalPaymentMethodHolder(vue);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof LocalPaymentMethodHolder) {
            final LocalPaymentMethodHolder lpmh = (LocalPaymentMethodHolder) holder;
            final int currentFPM = fpm[position];
            int src_id = fpms_ico_id[position];

            String currentFPMName = "";
            switch (currentFPM) {
                case (Register.MOBICASH):
                    currentFPMName = "Mobicash";
                    break;
            }
            lpmh.fpm_name.setText(currentFPMName);

            Picasso.get()
                    .load(src_id)
                    .error(src_id)
                    .placeholder(src_id)
                    .into(lpmh.fpm_ic);
            lpmh.fpm_ic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fpmCallback.FPMselected(currentFPM);
                }
            });
            lpmh.fpm_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fpmCallback.FPMselected(currentFPM);
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return fpms_ico_id.length;
    }

    private class LocalPaymentMethodHolder extends RecyclerView.ViewHolder {
        ImageView fpm_ic;
        TextView fpm_name;

        public LocalPaymentMethodHolder(View v) {
            super(v);
            fpm_ic = v.findViewById(R.id.fpm_ico);
            fpm_name = v.findViewById(R.id.FPM_name);
        }
    }
}
