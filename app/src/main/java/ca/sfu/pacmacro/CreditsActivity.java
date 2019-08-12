package ca.sfu.pacmacro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class CreditsActivity extends BaseActivity {
    SharedPreferences preferences;
    int clickCount = 0;

    public static final String SUDO_KEY = "sudo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_credits);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void onLogoClick(View view) {
        clickCount++;

        boolean currentSudoStatus = preferences.getBoolean(SUDO_KEY, false);

        if (clickCount == 10) {
            boolean newSudoStatus = !currentSudoStatus;
            preferences.edit().putBoolean(SUDO_KEY, newSudoStatus).apply();
            Toast.makeText(CreditsActivity.this, "Sudo mode enabled: " + newSudoStatus, Toast.LENGTH_SHORT).show();
            clickCount = 0;
        }
    }
}
