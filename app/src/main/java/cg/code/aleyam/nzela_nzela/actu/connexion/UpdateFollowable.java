package cg.code.aleyam.nzela_nzela.actu.connexion;

import android.app.Dialog;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public interface UpdateFollowable {
     void notifyDialogCreation();
     void begin(Button cancel, Dialog uploadDialog);
     void onSucces(Dialog uploadDialog);
     void onFailure(Dialog uploadDialog);
     void onProgress(Long sent, Long total, ProgressBar uploadState, TextView percent);
}