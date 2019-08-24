package ca.sfu.pacmacro;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

public class LobbyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment())
                    .commit();
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
            showExitDialog();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }


    private void showExitDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LobbyActivity.this);
        dialogBuilder.setTitle(getString(R.string.app_name));
        dialogBuilder.setMessage(getString(R.string.are_you_sure_you_want_to_exit));
        dialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LobbyActivity.super.onBackPressed();
            }
        });

        dialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
