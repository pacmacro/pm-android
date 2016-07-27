package ca.sfu.pacmacro;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class PlayerActivity extends AppCompatActivity {


    private static final int PERMISSION_RESPONSE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_RESPONSE_CODE);
        }
        else {
            startLocationService();
        }

        Button stopButton = (Button) findViewById(R.id.player_stop_service);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                stopService(intent);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_RESPONSE_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Really?");
                builder.create().show();
                // TODO: real shit here
            }
        }
    }

    private void startLocationService() {
        Intent intent = new Intent(getApplicationContext(), PlayerService.class);
        startService(intent);
    }


}
