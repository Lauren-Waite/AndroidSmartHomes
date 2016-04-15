package smarthomes.group07.smart_homes_application;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class MainActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        if (preferences.contains("weight")) {
            // Profile exists, launch BAC activity
        }
        else {
            //Intent login = new Intent(this, LoginActivity.class);
            //startActivity(login);

        }
    }

    public void toBacActivity(View view) {

    }
    public void editProfile(View view) {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

}
