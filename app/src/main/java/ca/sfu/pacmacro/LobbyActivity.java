package ca.sfu.pacmacro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import ca.sfu.pacmacro.Model.HomeFragment;

public class LobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        if(savedInstanceState == null) {
            changeFrame(new HomeFragment());
        }
    }

    public void changeFrame(Fragment nextFrame){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, nextFrame)
                .addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
