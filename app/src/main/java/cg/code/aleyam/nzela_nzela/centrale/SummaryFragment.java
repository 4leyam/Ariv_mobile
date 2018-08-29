package cg.code.aleyam.nzela_nzela.centrale;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cg.code.aleyam.nzela_nzela.R;

public class SummaryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View contentView = inflater.inflate(R.layout.fragment_menu_acceuil , container ,  false);
        return contentView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Centrale_activity.getLoader_indicator().setVisibility(View.GONE);
    }
}
