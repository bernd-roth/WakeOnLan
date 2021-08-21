package at.co.netconsulting.wakeonlan;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import at.co.netconsulting.wakeonlan.general.BaseActivity;

public class SettingsActivity extends BaseActivity {
    private int radioButtonEvaluation;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedEditor;
    private RadioGroup radioGroup;
    private RadioButton radioButtonHostname,radioButtonGroupname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeObjects();
        loadPreferences();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButtonStartAllFromHostname){
                    sharedEditor.clear();
                    sharedEditor.putInt("PREFS_RADIOGROUP_SETTINGS", checkedId);
                    sharedEditor.apply();
                }else if(checkedId == R.id.radioButtonStartAllFromGroupname){
                    sharedEditor.clear();
                    sharedEditor.putInt("PREFS_RADIOGROUP_SETTINGS", checkedId);
                    sharedEditor.apply();
                }
            }
        });
    }

    private void loadPreferences() {
        sharedEditor = sharedPreferences.edit();
        radioButtonEvaluation = sharedPreferences.getInt("PREFS_RADIOGROUP_SETTINGS", 0);

        if(radioButtonEvaluation == R.id.radioButtonStartAllFromHostname){
            radioButtonHostname.setChecked(true);
        }else if(radioButtonEvaluation == R.id.radioButtonStartAllFromGroupname){
            radioButtonGroupname.setChecked(true);
        }
    }

    private void initializeObjects() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButtonHostname = (RadioButton)findViewById(R.id.radioButtonStartAllFromHostname);
        radioButtonGroupname = (RadioButton)findViewById(R.id.radioButtonStartAllFromGroupname);
        sharedPreferences = getSharedPreferences("PREFS_RADIOGROUP_SETTINGS",0);
    }
}