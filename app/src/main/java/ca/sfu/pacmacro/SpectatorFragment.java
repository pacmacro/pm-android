package ca.sfu.pacmacro;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import ca.sfu.pacmacro.Controller.CharacterDisplayCriteria;

public class SpectatorFragment extends Fragment implements View.OnClickListener{

    private ImageView mPacman;
    private ImageView mGhost;
    private ImageView mGo;
    private int mTeam = 0;
    private boolean mSelected = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spectator, container, false);
        mPacman = view.findViewById(R.id.team_pacman);
        mPacman.setOnClickListener(this);
        mGhost = view.findViewById(R.id.team_ghost);
        mGhost.setOnClickListener(this);
        mGo = view.findViewById(R.id.team_go);
        mGo.setOnClickListener(this);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        mGo.setColorFilter(cf);
        mGo.setImageAlpha(128);
        mGo.setClickable(false);
        return view;
    }

    @Override
    public void onClick(View v) {
        int teamId = v.getId();
        if(mSelected && teamId != R.id.team_go){
            mPacman.setImageResource(R.drawable.pacman_button_long);
            mGhost.setImageResource(R.drawable.ghost_button);
        }
        switch(teamId) {
            case R.id.team_ghost:
                mTeam=CharacterDisplayCriteria.CRITERIA_GHOST_TEAM;
                mGhost.setImageResource(R.drawable.ghosts_selected);
                break;
            case R.id.team_pacman:
                mTeam=CharacterDisplayCriteria.CRITERIA_PACMAN_TEAM;
                mPacman.setImageResource(R.drawable.pacman_selected_long);
                break;
            case R.id.team_go:
                if(mTeam==CharacterDisplayCriteria.CRITERIA_GHOST_TEAM){
                    Toast.makeText(getContext(),"You are on the ghosts' team, Have fun!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"You are on the Pacman's team, good luck!", Toast.LENGTH_SHORT).show();
                }
                goToSpectatorActivity(mTeam);
                break;
        }
        if(!mSelected){
            mGo.setColorFilter(null);
            mGo.setImageAlpha(255);
            mGo.setClickable(true);
            mSelected=true;
        }
    }

    private void goToSpectatorActivity(int selectedTeam) {
        Intent intent = new Intent(getContext(), SpectatorActivity.class);
        intent.putExtra(CharacterDisplayCriteria.EXTRA_KEY, selectedTeam);
        startActivity(intent);
    }

}
