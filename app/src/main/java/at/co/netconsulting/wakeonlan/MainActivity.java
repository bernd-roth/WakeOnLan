package at.co.netconsulting.wakeonlan;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import at.co.netconsulting.wakeonlan.database.DBHelper;
import at.co.netconsulting.wakeonlan.general.BaseActivity;
import at.co.netconsulting.wakeonlan.poj.EntryPoj;

public class MainActivity extends BaseActivity {

    private DBHelper dbHelper;
    private List<EntryPoj> allEntries;
    private List<EntryPoj> entries;
//    private ImageView imageView;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);

        initializeObjects();

        // Setup the list view
        final ListView newsEntryListView = (ListView) findViewById(R.id.list);
        final EntryAdapter entryAdapter = new EntryAdapter(this, R.layout.entry_list_item);
        newsEntryListView.setAdapter(entryAdapter);
        
        // Populate the list, through the adapter
        for(final EntryPoj entry : getEntries()) {
        	entryAdapter.add(entry);
        }
    }

    private List<EntryPoj> getEntries() {
    	allEntries = dbHelper.getAllEntries();

        if(dbHelper.numberOfRows()==0) {
            allEntries.add(new EntryPoj("TV", "Server", "192.168.178.23", "00:00", "Nur ein Testeintrag"));
            //allEntries.add(new EntryPoj(res,"TV", "Server", "192.168.178.23", "00:00", "Nur ein Testeintrag"));
        } else {
            for (int i = 0; i <= dbHelper.numberOfRows(); i++) {
                entries.add(allEntries.get(i));
            }
        }
    	return allEntries;
    }

    //Initializing objects
    private void initializeObjects() {
        dbHelper = new DBHelper(getApplicationContext());
        entries = new ArrayList<EntryPoj>();
        //imageView = (ImageView) findViewById(R.id.entry_icon);
    }

    public void showMsgDirectMenuXml(MenuItem item) {
        onOptionsItemSelected(item);
    }
}