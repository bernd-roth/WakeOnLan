package at.co.netconsulting.wakeonlan;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.CheckBox;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import at.co.netconsulting.wakeonlan.databinding.ActivitySettingsBinding;
import at.co.netconsulting.wakeonlan.general.BaseActivity;

public class SettingsActivity extends BaseActivity {

    private CheckBox startAllFromHostname;
    private CheckBox startAllFromGroupname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeObjects();

    }

    private void initializeObjects() {
        startAllFromHostname = (CheckBox)findViewById(R.id.startAllFromHostname);
        startAllFromGroupname = (CheckBox)findViewById(R.id.startAllFromGroupname);
    }
}