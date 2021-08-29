package at.co.netconsulting.wakeonlan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.widget.Toolbar;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.Timestamp;
import java.sql.Date;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import at.co.netconsulting.wakeonlan.database.DBHelper;
import at.co.netconsulting.wakeonlan.general.BaseActivity;
import at.co.netconsulting.wakeonlan.general.SharedPreferenceModel;
import at.co.netconsulting.wakeonlan.general.StaticFields;
import at.co.netconsulting.wakeonlan.poj.EntryPoj;

public class SettingsActivity extends BaseActivity {
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button buttonSave;
    private EditText editTextPort, editTextArpRequest, editTextImportCSV;
    private Toolbar toolbar;
    private CheckBox checkBoxLoadFromCsv;
    private SharedPreferenceModel prefs = new SharedPreferenceModel(SettingsActivity.this);
    private boolean isCheckBox;
    private final String RADIO_BUTTON_GROUP = "Server_Or_Group";
    private DBHelper dbHelper;
    private String dateTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set the toolbar
        toolbar = findViewById(R.id.toolbarSettings);
        toolbar.inflateMenu(R.menu.menu_settings);

        initializeObjects();
        loadPreferencesCsvOrDb();
        loadPreferencesServerOrGroup();
        loadPreferencesPort();
        loadPreferencesFilename();
        loadPreferencesArpRequest();

        RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
                int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);
                prefs.saveIntSharedPreference(RADIO_BUTTON_GROUP, checkedIndex);
            }
        };

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(radioGroupOnCheckedChangeListener);

        checkBoxLoadFromCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This gets the correct item to work with.
                if(checkBoxLoadFromCsv.isChecked()){
                    boolean isChecked = true;
                    String key = "PREFS_CHECKBOX_CSV";
                    prefs.saveBooleanSharedPreference(key, isChecked);
                }else{
                    boolean isChecked = false;
                    String key = "PREFS_CHECKBOX_CSV";
                    prefs.saveBooleanSharedPreference(key, isChecked);
                }
            }
        });
    }

    private void loadPreferencesArpRequest() {
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS_ARP_REQUEST", MODE_PRIVATE);
        int arpRequest = sharedPreferences.getInt("PREFS_ARP_REQUEST", 1);
        editTextArpRequest.setText(String.valueOf(arpRequest));
    }

    private void loadPreferencesFilename() {
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS_FILENAME", MODE_PRIVATE);
        String filename = sharedPreferences.getString("PREFS_FILENAME", "");
        editTextImportCSV.setText(filename);
    }

    private void loadPreferencesPort() {
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS_PORT", MODE_PRIVATE);
        int port = sharedPreferences.getInt("PREFS_PORT", 9);
        editTextPort.setText(String.valueOf(port));
    }

    private void loadPreferencesServerOrGroup() {
        int savedRadioIndex = prefs.getIntSharedPreference(RADIO_BUTTON_GROUP);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        if(savedRadioIndex==0){
            ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
            prefs.saveStringSharedPreference("PREFS_SERVER_CLIENT", "SERVER");
        }else {
            ((RadioButton) radioGroup.getChildAt(savedRadioIndex)).setChecked(true);
            prefs.saveStringSharedPreference("PREFS_SERVER_CLIENT", "CLIENT");
        }
    }

    private void loadPreferencesCsvOrDb() {
        isCheckBox = prefs.getBooleanSharedPreference("PREFS_CHECKBOX_CSV");
        checkBoxLoadFromCsv.setChecked(isCheckBox);
    }

    private void initializeObjects() {
        buttonSave = findViewById(R.id.buttonSave);
        editTextPort = (EditText) findViewById(R.id.editTextPort);
        editTextImportCSV = (EditText) findViewById(R.id.editTextImportCSVFile);
        editTextArpRequest = (EditText) findViewById(R.id.editTextSendArpRequest);

        checkBoxLoadFromCsv = findViewById(R.id.checkBoxLoadFromCsv);
    }

    public void save(View view) {
        savePort();
        saveFilename();
        saveArpRequest();
    }

    private void saveArpRequest() {
        int arpRequest = Integer.parseInt(editTextArpRequest.getText().toString());
        String key = "PREFS_ARP_REQUEST";
        prefs.saveIntSharedPreference(key, arpRequest);
    }

    private void savePort() {
        int port = Integer.parseInt(editTextPort.getText().toString());
        String key = "PREFS_PORT";
        prefs.saveIntSharedPreference(key, port);
    }

    private void saveFilename() {
        String filename = editTextImportCSV.getText().toString();
        String key = "PREFS_FILENAME";
        prefs.saveStringSharedPreference(key, filename);
    }

    public void delete(View view) {
        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.deleteAllEntries();
    }

    public void showMenu(MenuItem item) {
        onOptionsItemSelected(item);
    }

    public void export(View view) {
        dbHelper = new DBHelper(getApplicationContext());
        List<EntryPoj> allEntries = dbHelper.exportAllEntries();

        dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss").format(LocalDateTime.now());

        File backupFile = new File(Environment.getExternalStorageDirectory() + "/" + "export_server.csv." + dateTimeFormat);
        StringBuilder stringbuilder = new StringBuilder();

        try (PrintWriter writer = new PrintWriter(backupFile)) {
            for(int i = 0; i<allEntries.size(); i++) {
                stringbuilder.append(allEntries.get(i).getId()+";");
                stringbuilder.append(allEntries.get(i).getHostname()+";");
                stringbuilder.append(allEntries.get(i).getGroup_name()+";");
                stringbuilder.append(allEntries.get(i).getIp_address()+";");
                stringbuilder.append(allEntries.get(i).getBroadcast()+";");
                stringbuilder.append(allEntries.get(i).getNic_mac()+";");
                stringbuilder.append(allEntries.get(i).getComment());
                stringbuilder.append("\n");
            }
            writer.write(stringbuilder.toString());
            writer.close();
        } catch (FileNotFoundException exception) {
            Log.e(StaticFields.ERROR_TAG, "Could not find file!");
        }
    }
}