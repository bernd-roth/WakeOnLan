package at.co.netconsulting.wakeonlan.general;

import android.app.Activity;
import android.content.ComponentName;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import at.co.netconsulting.wakeonlan.R;

public class BaseActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ComponentName a = getCallingActivity();
        
        switch (item.getItemId()) {
            case R.id.action_settings:
                // do what you want here
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}