package at.co.netconsulting.wakeonlan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {

               swipeRefreshLayout.setRefreshing(false);
           }
       });
        //imageView = (ImageView) findViewById(R.id.entry_icon);

        initializeObjects();

        // Setup the list view
        final ListView newsEntryListView = (ListView) findViewById(R.id.list);
        final EntryAdapter entryAdapter = new EntryAdapter(this, R.layout.entry_list_item);
        newsEntryListView.setAdapter(entryAdapter);
        
        // Populate the list, through the adapter
        for(final EntryPoj entry : getEntries()) {
        	entryAdapter.add(entry);
        }

        newsEntryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                openDialog();
                return true;
            }
        });
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_hosts, null))
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private List<EntryPoj> getEntries() {
    	allEntries = dbHelper.getAllEntries();

        if(dbHelper.numberOfRows()==0) {
            allEntries.add(new EntryPoj("TV-Server", "Server", "192.168.178.23", "00:00", "Nur ein Testeintrag"));
            allEntries.add(new EntryPoj("TV-Client", "Server", "192.168.178.23", "00:00", "Nur ein Testeintrag"));
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
    }

    public void showMenu(MenuItem item) {
        onOptionsItemSelected(item);
    }
}