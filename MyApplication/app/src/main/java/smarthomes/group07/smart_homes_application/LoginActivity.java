package smarthomes.group07.smart_homes_application;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.view.View;

public class LoginActivity extends Activity implements AdapterView.OnItemSelectedListener {
    SharedPreferences preferences;
    private int genderSelection = -1;
    private int weightSelection = 0;
    private Spinner mWeightSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_login);

        preferences = this.getSharedPreferences("PROFILE_PREF", Context.MODE_PRIVATE);
        mWeightSpinner = (Spinner) findViewById(R.id.weight_spinner);
        mWeightSpinner.setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        weightSelection = (10 * pos) + 60;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Blank interface method
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

        SharedPreferences.Editor prefEditor = preferences.edit();

        if(weightSelection != 0) {
            prefEditor.putInt("weight", weightSelection);
            prefEditor.apply();
        }

        if (genderSelection != -1) {
            prefEditor.putInt("gender", genderSelection);
            prefEditor.apply();
        }

        if(weightSelection != 0 && genderSelection != -1) {
            finish();
        }
    }
}
