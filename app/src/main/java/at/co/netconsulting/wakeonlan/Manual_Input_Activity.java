package at.co.netconsulting.wakeonlan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import at.co.netconsulting.wakeonlan.database.DBHelper;
import at.co.netconsulting.wakeonlan.general.BaseActivity;

public class Manual_Input_Activity extends BaseActivity {

    private EditText editTextHostname;
    private EditText editTextGroupname;
    private EditText editTextIpaddress;
    private EditText editTextBroadcast;
    private EditText editTextNicMac;
    private EditText editTextComment;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);

        initializeObjects();

        editTextHostname = findViewById(R.id.editTextHostname);
        editTextGroupname = findViewById(R.id.editTextGroupname);
        editTextIpaddress = findViewById(R.id.editTextIpaddress);
        editTextBroadcast = findViewById(R.id.editTextBroadcast);
        editTextNicMac = findViewById(R.id.editTextNicMac);
        editTextComment = findViewById(R.id.editTextComment);
    }

    private void initializeObjects() {
        dbHelper = new DBHelper(getApplicationContext());
    }

    public void save(View view) {
        dbHelper.insertWifi(editTextHostname.getText().toString(),
                editTextGroupname.getText().toString(),
                editTextIpaddress.getText().toString(),
                editTextBroadcast.getText().toString(),
                editTextNicMac.getText().toString(),
                editTextComment.getText().toString());
    }
}