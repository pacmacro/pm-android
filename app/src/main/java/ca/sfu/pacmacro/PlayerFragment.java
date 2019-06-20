package ca.sfu.pacmacro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

public class PlayerFragment extends Fragment implements View.OnClickListener{
    private ImageView mPacman, mBlinky, mPinky, mInky, mClyde, mGo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        mPacman = view.findViewById(R.id.player_pacman);
        mPacman.setOnClickListener(this);
        mBlinky = view.findViewById(R.id.player_blinky);
        mBlinky.setOnClickListener(this);
        mPinky = view.findViewById(R.id.player_pinky);
        mPinky.setOnClickListener(this);
        mInky = view.findViewById(R.id.player_inky);
        mInky.setOnClickListener(this);
        mClyde = view.findViewById(R.id.player_clyde);
        mClyde.setOnClickListener(this);
        mGo = view.findViewById(R.id.player_go);
        mGo.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Character.CharacterType selectedCharacter = null;
        int selectedCharacterId = characterSelection.getCheckedRadioButtonId();
        boolean isPlayerSelected = true;
        switch (selectedCharacterId) {
            case R.id.character_pacman:
                selectedCharacter = Character.CharacterType.PACMAN;
                break;
            case R.id.character_inky:
                selectedCharacter = Character.CharacterType.INKY;
                break;
            case R.id.character_blinky:
                selectedCharacter = Character.CharacterType.BLINKY;
                break;
            case R.id.character_pinky:
                selectedCharacter = Character.CharacterType.PINKY;
                break;
            case R.id.character_clyde:
                selectedCharacter = Character.CharacterType.CLYDE;
                break;
            default:
                isPlayerSelected = false;

                AlertDialog.Builder makeSelectionBuilder = new AlertDialog.Builder(PlayerFragment.this.getContext());
                makeSelectionBuilder.setTitle(R.string.dialog_title_select_player);
                makeSelectionBuilder.setPositiveButton(R.string.dialog_button_ok, null);
                makeSelectionBuilder.show();
        }

        if (isPlayerSelected) {
            Toast.makeText(getContext(), "Character selected: " + selectedCharacter, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), PlayerActivity.class);
            intent.putExtra("Character", selectedCharacter);
            startActivity(intent);
        }
    }


    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        MaterialRadioGroup characterSelection = (MaterialRadioGroup) view.findViewById(R.id.character_selection);

        Button startButton = (Button) view.findViewById(R.id.player_start);
        startButton.setOnClickListener(getStartButtonListener(characterSelection));

        return view;
    }

    public View.OnClickListener getStartButtonListener(final MaterialRadioGroup characterSelection) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Character.CharacterType selectedCharacter = null;
                int selectedCharacterId = characterSelection.getCheckedRadioButtonId();
                boolean isPlayerSelected = true;
                switch (selectedCharacterId) {
                    case R.id.character_pacman:
                        selectedCharacter = Character.CharacterType.PACMAN;
                        break;
                    case R.id.character_inky:
                        selectedCharacter = Character.CharacterType.INKY;
                        break;
                    case R.id.character_blinky:
                        selectedCharacter = Character.CharacterType.BLINKY;
                        break;
                    case R.id.character_pinky:
                        selectedCharacter = Character.CharacterType.PINKY;
                        break;
                    case R.id.character_clyde:
                        selectedCharacter = Character.CharacterType.CLYDE;
                        break;
                    default:
                        isPlayerSelected = false;

                        AlertDialog.Builder makeSelectionBuilder = new AlertDialog.Builder(PlayerFragment.this.getContext());
                        makeSelectionBuilder.setTitle(R.string.dialog_title_select_player);
                        makeSelectionBuilder.setPositiveButton(R.string.dialog_button_ok, null);
                        makeSelectionBuilder.show();
                }

                if (isPlayerSelected) {
                    Toast.makeText(getContext(), "Character selected: " + selectedCharacter, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), PlayerActivity.class);
                    intent.putExtra("Character", selectedCharacter);
                    startActivity(intent);
                }
            }
        };
    }*/
}
