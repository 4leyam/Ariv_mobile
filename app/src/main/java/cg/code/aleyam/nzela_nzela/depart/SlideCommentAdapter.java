package cg.code.aleyam.nzela_nzela.depart;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;

public class SlideCommentAdapter extends PagerAdapter implements TextWatcher {

    int count = 2;
    Context ct = null;
    boolean once = false;


    public SlideCommentAdapter(Context ct) {

        this.ct = ct;

    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
     return view == object;
    }

    @Override
    public int getCount() {

        return count;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View vue = inflater.inflate(R.layout.comment_step1 , container , false);
        TextView indicator = vue.findViewById(R.id.question);
        TextInputEditText reponse = vue.findViewById(R.id.response_for_comment);
        reponse.addTextChangedListener(this);

        //if(count<=2) position++;



        switch (position) {
            case 0:
                indicator.setText("pour l'instant vous n'avez droit qu'a un seul commentaire," +
                        " a chaque voyage avec cette agence vous aurez droit a un commentaire de plus afin de partager votre experience." +
                        "Slidez afin de comenter"); //on met la question de securite
                indicator.setTextSize(14);
                reponse.setVisibility(View.GONE);
                break;
            case 1:
                indicator.setVisibility(View.GONE);
                //Log.e("test" , "position: "+position);
                reponse.setHint("Resumez ce que vous pensez");
                break;
        }
        container.addView(vue);
        return vue;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((ConstraintLayout)object);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        OttoBus.bus.post(s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}