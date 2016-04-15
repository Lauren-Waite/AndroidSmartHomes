package smarthomes.group07.smart_homes_application;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View;

public class LoginActivity extends Activity {
    SharedPreferences preferences;
    private int genderSelection = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = this.getPreferences(Context.MODE_PRIVATE);

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_female:
                if(checked)
                    genderSelection = 1;
                break;
            case R.id.radio_male:
                if(checked)
                    genderSelection = 2;
                break;
        }

    }


    public void submitData(View view) {
        RadioGroup genderGroup = ((RadioGroup) findViewById(R.id.gender_radio_group));
        EditText weightView = ((EditText) findViewById(R.id.weight_value));
        String weightString = weightView.getText().toString();

        SharedPreferences.Editor prefEditor = preferences.edit();

        if(weightString.equals("")) {
            weightView.setError("Please enter a number");
        } else {
            int weightVal = Integer.parseInt(weightString);
            prefEditor.putInt("weight", weightVal);
        }

        if (genderSelection != -1) {
            prefEditor.putInt("gender", genderSelection);
        }

        if(!weightString.equals("") && genderSelection != -1) {
            finish();
        }
    }
}
