package cg.code.aleyam.nzela_nzela.actu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cg.code.aleyam.nzela_nzela.OttoBus;
import cg.code.aleyam.nzela_nzela.R;

public class PictureReport extends Fragment {

    public static final String  SAVED_PATH_KEY = "SPK_5";
    public static ImageView post_image = null;
    public static String lastImagePostedPath = "";
    private final String INTERNAL_PATH_KEY = "k_pey";


    private OnFragmentInteractionListener mListener;

    public PictureReport() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View report_pic = inflater.inflate(R.layout.fragment_picture_report, container, false);
        post_image = report_pic.findViewById(R.id.capturedPic);
        if(savedInstanceState != null) {
            //rechargement du fragment donc on restore l'image precedemet chagé
            catchNewPictureEvent(savedInstanceState.getString(INTERNAL_PATH_KEY));
        }
        return report_pic;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!TextUtils.isEmpty(lastImagePostedPath)) {
            outState.putString(INTERNAL_PATH_KEY , lastImagePostedPath);
        }
    }

    /**
     * methode appelee apres que la photo ai ete prise.
     *
     * @param path
     */
    @Subscribe
    public void catchNewPictureEvent(String path) {

        Log.i("test", "catchNewPictureEvent: chemin de la photo capturee  "+path );
        lastImagePostedPath = path;

        //post_image.setImageURI(path);

        Picasso.get()
                .load(new File(lastImagePostedPath))
                .fit()
                .centerCrop()
                .error(R.drawable.pn)
                .placeholder(R.drawable.pn)
                .into(post_image);

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        OttoBus.bus.register(PictureReport.this);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        OttoBus.bus.unregister(PictureReport.this);
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
