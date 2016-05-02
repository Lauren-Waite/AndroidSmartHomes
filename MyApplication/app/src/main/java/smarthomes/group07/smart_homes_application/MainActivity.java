package smarthomes.group07.smart_homes_application;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class MainActivity extends Activity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getSharedPreferences("PROFILE_PREF", Context.MODE_PRIVATE);
        setContentView(R.layout.rect_activity_main);

    }

    public void toBacActivity(View view) {
        if(preferences.contains("weight")) {
            Intent bacAct = new Intent(this, BacActivity.class);
            startActivity(bacAct);
        }
    }
    public void editProfile(View view) {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

}
