package cg.code.aleyam.nzela_nzela;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    public interface SheetCallback {
        void setupDialog(Dialog dialog, int style);
    }
    private SheetCallback scb = null;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        scb.setupDialog(dialog , style);
    }

    public void setSheetCallBack(SheetCallback scb) {
        if(scb != null) {
            this.scb = scb;
        }
    }

}
