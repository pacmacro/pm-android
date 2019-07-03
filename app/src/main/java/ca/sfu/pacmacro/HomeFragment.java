package ca.sfu.pacmacro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ca.sfu.pacmacro.CreditsActivity;
import ca.sfu.pacmacro.LobbyActivity;
import ca.sfu.pacmacro.PlayerFragment;
import ca.sfu.pacmacro.R;
import ca.sfu.pacmacro.SpectatorFragment;

public class HomeFragment extends Fragment {

    private ImageView mPlayer;
    private ImageView mSpectator;
    private ImageView mCredit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lobby, container, false);
        mPlayer = view.findViewById(R.id.homePlayer);
        mPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LobbyActivity)getActivity()).changeFrame(new PlayerFragment());
            }
        });
        mSpectator = view.findViewById(R.id.homeSpectator);
        mSpectator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LobbyActivity)getActivity()).changeFrame(new SpectatorFragment());
            }
        });
        mCredit = view.findViewById(R.id.homeCredit);
        mCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreditsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
