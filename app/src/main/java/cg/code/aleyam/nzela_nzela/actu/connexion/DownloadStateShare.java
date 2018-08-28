package cg.code.aleyam.nzela_nzela.actu.connexion;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface DownloadStateShare {

    void notifyDataReady(List<? extends Post_ancestor> new_data);
    void onPostAdded();
    void onPostedRemoved();
    void onPostChanged();
    void onRequestFail();

}