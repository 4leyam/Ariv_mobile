package cg.code.aleyam.nzela_nzela.depart;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.Arrays;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;
import cg.code.aleyam.nzela_nzela.authentication.Authentication;
import cg.code.aleyam.nzela_nzela.centrale.Centrale_activity;
import cg.code.aleyam.nzela_nzela.commentaire.CommentaireActivity;
import cg.code.aleyam.nzela_nzela.data_service.Agence_info;
import cg.code.aleyam.nzela_nzela.data_service.Comment_obj;
import cg.code.aleyam.nzela_nzela.data_service.Data_terminal;
import cg.code.aleyam.nzela_nzela.data_service.OutputResponse;
import cg.code.aleyam.nzela_nzela.event.Ftb_event;
import cg.code.aleyam.nzela_nzela.event.Nzela_objet;
import cg.code.aleyam.nzela_nzela.lite.DatabaseManager;
import cg.code.aleyam.nzela_nzela.lite.UserManipulation;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserObject;
import cg.code.aleyam.nzela_nzela.transaction.connexion.UserOperationCallback;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;


public class InfoGenFragment extends Fragment {

    RecyclerView rootView = null;
    Agence_info ai = null;
    public static String info_key = "cg.code.aleyam.nzela_nzela.info";
    Info_adapter adapter = null;

    public InfoGenFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contenu_info_gen, container, false);
        rootView = view.findViewById(R.id.info_gen_recycle);
        rootView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        adapter = new Info_adapter(ai , InfoGenFragment.this);
        rootView.setAdapter(adapter);
        return view;
    }

    public void setInfo(Agence_info ai) {
       // Toast.makeText(DepartAdapter.cont, "recuperation des informations generales", Toast.LENGTH_SHORT).show();
        this.ai = ai;
        /*if(rootView!=null)
        rootView.setAdapter(new Info_adapter(ai , InfoGenFragment.this));*/
        //init(rootView);
    }

    @Override
    public void onPause() {
        if(adapter!=null) OttoBus.bus.unregister(adapter);
        super.onPause();

    }

    @Override
    public void onResume() {
        if(adapter!=null) OttoBus.bus.register(adapter);
        super.onResume();
    }

    public static Spanned fromHtml(@NonNull String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            return Html.fromHtml(html);
        }
    }

}

class Info_adapter extends RecyclerView.Adapter<Info_adapter.InfoVH> implements ViewPager.OnPageChangeListener
        , Ftb_event.ActionListener , View.OnClickListener , RatingBar.OnRatingBarChangeListener , UserOperationCallback {
    Ftb_event ftb_event = new Ftb_event();
    FloatingTextButton suivant , precedent;
    int size = 2;
    LinearLayout indicator_layout = null;
    Button plus = null;
    ViewPager vp = null;
    TextView[] dot_indicator = null;
    public final int adapt_size = 1;
    Agence_info ai = null;
    InfoVH holder = null;
    InfoGenFragment infoGen = null;
    String commentContent = "";
    private boolean onAvisListener = false;
    DatabaseManager dbm = null;
    private final String VERIFICATION_KEY = "verification";
    private final String SUBMIT_KEY = "submit";

     Info_adapter(Agence_info ai , InfoGenFragment infoGen) {
        this.ai = ai;
        this.infoGen = infoGen;
    }

    @Override
    public InfoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        //on recupere l'element de liste
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View info = inflater.inflate(R.layout.fragment_info_gen , parent , false);
        dbm = DatabaseManager.getInstance(parent.getContext());
        return new InfoVH(info);

    }

    @Override
    public void onBindViewHolder(InfoVH holder, int position) {

        bind(holder);
        plus.setOnClickListener(this);
        //le veritable non de cette fonction devrait etre addActionListner
        ftb_event.setActionListenr(suivant , this);
        ftb_event.setActionListenr(precedent , this);

    }



    @Override
    public int getItemCount() {
        return adapt_size;
    }

    class InfoVH extends RecyclerView.ViewHolder {
        View info_view = null;
        RatingBar avis = null;
        ViewPager comment_slide = null;
        TextInputEditText input = null;
        TextView contact , site , adresse , mail , a_propos;

        public InfoVH(View info_view) {
            super(info_view);
            indicator_layout = info_view.findViewById(R.id.indicator);
            vp = info_view.findViewById(R.id.slider_comment);
            suivant = info_view.findViewById(R.id.suivant);
            precedent = info_view.findViewById(R.id.precedent);
            plus = info_view.findViewById(R.id.plus);
            contact = info_view.findViewById(R.id.contact_test);
            avis = info_view.findViewById(R.id.set_avis);
            site = info_view.findViewById(R.id.site_internet);
            adresse = info_view.findViewById(R.id.adresse_agence);
            mail = info_view.findViewById(R.id.mail_agence);
            comment_slide = info_view.findViewById(R.id.slider_comment);
            input = info_view.findViewById(R.id.response_for_comment);
            a_propos = info_view.findViewById(R.id.contenu_propos);
            this.info_view = info_view;
        }
    }

    private void bind(InfoVH holder) {
        if(ai != null && holder.info_view!=null) {
            Info_adapter.this.holder = holder;
            holder.contact.setText(infoGen.getResources().getString(R.string.contact_tel)+"  "+ai.getTel_agence());
            //TODO mettre un lien a l'endroit du champ site qui renvoie vers la page de l'agence sur le site.
            holder.site.setText(infoGen.getResources().getString(R.string.site_internet)+" "+InfoGenFragment.fromHtml("<a href='http://nzela-nzela.000webhostapp.com/web'>web.Nzela<a>") );
            holder.adresse.setText(infoGen.getResources().getString(R.string.adresse_agence)+"  "+ai.getAdresse_agence());
            holder.mail.setText(infoGen.getResources().getString(R.string.mail)+"  "+ai.getEmail_agence());
            holder.a_propos.setText(ai.getInformation_agence());
            RatingBar.OnRatingBarChangeListener avis_ecouteur = holder.avis.getOnRatingBarChangeListener();
            if(avis_ecouteur == null || !(avis_ecouteur instanceof InfoGenFragment)) {

                onAvisListener = false;
                holder.avis.setOnRatingBarChangeListener(this);

            }

            vp.setAdapter(new SlideCommentAdapter(holder.info_view.getContext()));
        }

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

         if(!onAvisListener) {
             Log.e("test" , "impatiement a l'ecoute");
             plus.setEnabled(true);
             plus.setVisibility(View.VISIBLE);
             suivant.setVisibility(View.VISIBLE);
             indicator_layout.setVisibility(View.VISIBLE);
             vp.setVisibility(View.VISIBLE);
             ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) vp.getLayoutParams(),
                     source_params = (ConstraintLayout.LayoutParams)ratingBar.getLayoutParams();

             params.height = 285;
             vp.requestLayout();
             vp.setLayoutParams(params);

             params = (ConstraintLayout.LayoutParams) plus.getLayoutParams();
             params.height =  ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
             plus.setLayoutParams(params);

             initIndicator(indicator_layout , size);
             vp.addOnPageChangeListener(this);
             onAvisListener = true;
         }


    }

    private void initIndicator(LinearLayout layout_indicator , int size) {
        layout_indicator.removeAllViews();
        dot_indicator = new TextView[size];
        for(int i = 0 ; i<dot_indicator.length ; i++ ) {
            dot_indicator[i] = new TextView(infoGen.getContext());
            dot_indicator[i].setText(InfoGenFragment.fromHtml("&#8226"));
            dot_indicator[i].setTextColor(infoGen.getResources().getColor(R.color.colorAccent));
            dot_indicator[i].setTextSize(25);
            layout_indicator.addView(dot_indicator[i]);
        }

    }

    @Override
    public void actionPerformed(FloatingTextButton source) {
        if(source.getId() == R.id.suivant ) {

            if(vp.getCurrentItem() == (vp.getAdapter().getCount() - 1)) {
                //derniere etape donc soumission de l'avis
                //avant de soumettre lavis il faut s'autentifier.
                if(dbm != null) {
                    //on verifie que l'utilisateur s'est deja authentifiee
                    String[] userInfo = dbm.getCurrentUser();
                    if(dbm.getCurrentUser() != null) {
                        //pour commencer on verifie si l'utilisateur a l'authorisation de commenter
                        UserManipulation.isCommentAllowed(userInfo[2] , Info_adapter.this , VERIFICATION_KEY);
                    } else  {
                        Snackbar.make(infoGen.getActivity().findViewById(android.R.id.content) , "Vous n'etes pas authentifié" , Snackbar.LENGTH_LONG)
                                .setAction("S'authentifier", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        infoGen.getActivity().startActivity(new Intent(infoGen.getActivity() , Authentication.class));
                                        if(dbm != null)
                                            dbm.closeDB();
                                        infoGen.getActivity().finish();
                                        try {
                                            Centrale_activity.getActivityInstance().finish();
                                        } catch (Exception e) {
                                            Log.e("test", "onClick: instance de central_activity pas encore etablie (Null) ");
                                        }
                                    }
                                })
                                .setActionTextColor(Color.GRAY)
                                .show();
                    }
                }
                vp.setCurrentItem(0);

            } else {
                vp.setCurrentItem(vp.getCurrentItem()+1);
            }
        } else if(source.getId() == R.id.precedent ) {
            vp.setCurrentItem(vp.getCurrentItem()-1);
        }
    }

    @Override
    public void succes(UserObject userObject) {

    }

    @Override
    public void failed() {

    }

    @Override
    public void operationResult( String key , Object object) {
        if(key.equals(VERIFICATION_KEY)) {
            //donc si c'est une reponse a verifiation
            String[] userInfo = dbm.getCurrentUser();
            boolean isAllow = Boolean.parseBoolean(object.toString());

            if(isAllow) {
                submitComment( userInfo[2] , userInfo[1]+" "+userInfo[0] , commentContent , (int) (holder.avis.getRating() == 0?1:holder.avis.getRating()));
            } else {
                String tmp = object.toString();
                if(!tmp.equalsIgnoreCase("false")) {
                    long l = Long.parseLong(tmp);
                    int id_agence = Integer.parseInt(Depart.activity_in_info[1]);
                    if(id_agence == (int)l) {
                        submitComment( userInfo[2] , userInfo[1]+" "+userInfo[0] , commentContent , (int) (holder.avis.getRating() == 0?1:holder.avis.getRating()));
                    } else {
                        displayInterditDialog();
                    }
                } else {
                    displayInterditDialog();
                }
            }

        } else Log.e("test", "la cle est incorrecte");
    }

    public void displayInterditDialog() {
        Activity activity = infoGen.getActivity();
        if(activity != null) {
            new AlertDialog.Builder(activity)
                    .setTitle("Interdit")
                    .setMessage("Impossible de partager votre avis car, Seul vos avis sur votre dernier voyage est Accepté.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }

    public void submitComment(String phoneNumber , String pseudo , String comment , int avis) {
        Comment_obj commentaire = new Comment_obj(pseudo , comment , avis , Integer.parseInt(Depart.activity_in_info[1]));
        Data_terminal.postComment(commentaire , infoGen.getContext() , Depart.RUN);
        //on notifi que cette utilisteur n'a plus le droit au commentaire.
        UserManipulation.notifyComment( phoneNumber ,  Info_adapter.this , SUBMIT_KEY);
    }

    @Subscribe
    public void setCommentInfo(String commentInfo) {
         if(holder!=null) {
            commentContent = commentInfo;
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        initIndicator(indicator_layout , size);
        if (position == size-1) {
            suivant.setTitle("soumettre");
            precedent.setEnabled(true);
            precedent.setVisibility(View.VISIBLE);
        } else if(position == 0) {
            suivant.setTitle("suivant");
            precedent.setEnabled(false);
            precedent.setVisibility(View.INVISIBLE);
        } else {
            //au cas les choses s'agrandissent
            suivant.setTitle("suivant");
            precedent.setEnabled(true);
            precedent.setVisibility(View.VISIBLE);
        }
        dot_indicator[position].setTextColor(Color.WHITE);

    }





    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.plus) {
            //clic sur le boutton qui permet de voir tous les commentaires
            plus_handler();
        }
    }

    @Subscribe
    public void response(Nzela_objet<Object> n_objet) {
     try {
         if(n_objet.objet instanceof OutputResponse) {

             OutputResponse reponse = (OutputResponse) n_objet.objet;

             if(reponse.isOk(Depart.agence_name)) {

                 Snackbar.make(infoGen.getActivity().findViewById(android.R.id.content) , "Avis poste avec succes" , Snackbar.LENGTH_LONG)
                         .setAction("Plus", new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 plus_handler();
                             }
                         })
                         .setActionTextColor(Color.GRAY)
                         .show();
             } else {
                 Snackbar.make(infoGen.getActivity().findViewById(android.R.id.content) , "Erreur lors de l'envoi" , Snackbar.LENGTH_LONG)
                         .setAction("Reesayer", new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 actionPerformed(suivant);
                             }
                         })
                         .setActionTextColor(Color.GRAY)
                         .show();
             }
         }
     } catch (Exception e) {

     }
    }
    public void plus_handler() {

        Intent i = new Intent(infoGen.getContext() , CommentaireActivity.class);

        i.putExtra(InfoGenFragment.info_key , new String[]{Depart.agence_name , ""+Depart.activity_in_info[1] });

        infoGen.startActivity(i);
    }

}
