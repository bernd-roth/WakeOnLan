package at.co.netconsulting.wakeonlan;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import at.co.netconsulting.wakeonlan.database.DBHelper;
import at.co.netconsulting.wakeonlan.general.BaseActivity;

public class MainActivity extends BaseActivity {

    DBHelper dbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeObjects();

        // Setup the list view
        final ListView newsEntryListView = (ListView) findViewById(R.id.list);
        final EntryAdapter entryAdapter = new EntryAdapter(this, R.layout.entry_list_item);
        newsEntryListView.setAdapter(entryAdapter);
        
        // Populate the list, through the adapter
        for(final EntryPoj entry : getNewsEntries()) {
        	entryAdapter.add(entry);
        }
    }

    private List<EntryPoj> getNewsEntries() {
    	// Let's setup some test data.
    	// Normally this would come from some asynchronous fetch into a data source
    	// such as a sqlite database, or an HTTP request
    	
    	final List<EntryPoj> entries = new ArrayList<EntryPoj>();
    	
    	for(int i = 1; i < dbHelper.numberOfRows(); i++) {
    		entries.add(
	    		new EntryPoj(
	    				"Test Entry " + i,
	    				"Anonymous Author " + i,
	    				new GregorianCalendar(2011, 11, i).getTime(),
	    				i % 2 == 0 ? R.drawable.news_icon_1 : R.drawable.news_icon_2
	    		)
	    	);
    	}
    	
    	return entries;
    }

    //Initializing objects
    private void initializeObjects() {
        dbHelper = new DBHelper(getApplicationContext());
    }
}