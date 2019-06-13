package ca.sfu.pacmacro.Model;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ca.sfu.pacmacro.R;

public class HomeFragment extends Fragment {

    private ImageView mPlayer;
    private ImageView mSpectator;
    private ImageView mCredit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lobby, container, false);
        mPlayer = view.findViewById(R.id.homePlayer);
        mSpectator = view.findViewById(R.id.homeSpectator);
        mCredit = view.findViewById(R.id.homeCredit);

        return view;
    }
}
