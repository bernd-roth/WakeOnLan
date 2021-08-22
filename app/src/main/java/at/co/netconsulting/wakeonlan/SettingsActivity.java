package at.co.netconsulting.wakeonlan;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.widget.Toolbar;
import at.co.netconsulting.wakeonlan.general.BaseActivity;

public class SettingsActivity extends BaseActivity {
    private int radioButtonEvaluation;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedEditor;
    private RadioGroup radioGroup;
    private RadioButton radioButtonHostname,radioButtonGroupname;
    private Button buttonSave;
    private EditText editTextPort;
    private int port;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set the toolbar
        toolbar = findViewById(R.id.toolbarSettings);
        toolbar.inflateMenu(R.menu.menu_settings);

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
        sharedPreferences = getSharedPreferences("PREFS_RADIOGROUP_SETTINGS",0);
        sharedEditor = sharedPreferences.edit();
        radioButtonEvaluation = sharedPreferences.getInt("PREFS_RADIOGROUP_SETTINGS", 0);

        if(radioButtonEvaluation == R.id.radioButtonStartAllFromHostname){
            radioButtonHostname.setChecked(true);
        }else if(radioButtonEvaluation == R.id.radioButtonStartAllFromGroupname){
            radioButtonGroupname.setChecked(true);
        }

        sharedPreferences = getSharedPreferences("PREFS_PORT", 0);
        int port = sharedPreferences.getInt("PREFS_PORT", 9);
        editTextPort.setText(String.valueOf(port));
    }

    private void initializeObjects() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButtonHostname = (RadioButton)findViewById(R.id.radioButtonStartAllFromHostname);
        radioButtonGroupname = (RadioButton)findViewById(R.id.radioButtonStartAllFromGroupname);
        buttonSave = findViewById(R.id.buttonSave);
        editTextPort = (EditText) findViewById(R.id.editTextPort);
    }

    public void save(View view) {
        sharedPreferences = getSharedPreferences("PREFS_PORT", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        port = Integer.valueOf(String.valueOf(editTextPort.getText()));
        editor.putInt("PREFS_PORT", port);
        editor.commit();
    }

    public void showMenu(MenuItem item) {
        onOptionsItemSelected(item);
    }
}