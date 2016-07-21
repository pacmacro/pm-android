package ca.sfu.pacmacro;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import ca.sfu.pacmacro.Model.Character;



public class PlayerFragment extends Fragment {

//    private OnFragmentInteractionListener mListener;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        RadioGroup characterSelection = (RadioGroup) view.findViewById(R.id.character_selection);

        Button startButton = (Button) view.findViewById(R.id.player_start);
        startButton.setOnClickListener(getStartButtonListener(characterSelection));

        return view;
    }

    public View.OnClickListener getStartButtonListener(final RadioGroup characterSelection) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Character.CharacterType selectedCharacter = null;
                int selectedCharacterId = characterSelection.getCheckedRadioButtonId();
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
                }

                Toast.makeText(getContext(), "Character selected: " + selectedCharacter, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
