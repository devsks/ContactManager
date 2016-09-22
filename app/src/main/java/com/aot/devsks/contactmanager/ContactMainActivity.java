package com.aot.devsks.contactmanager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactMainActivity extends AppCompatActivity {
    private ArrayList<HashMap<String,String>> arr;
    private DBHelp contactsDB;
    private ListView contacts;
    AlertDialog.Builder builder;
    private String cName,cNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        contactsDB = new DBHelp();
        contactsDB.myDb = openOrCreateDatabase("devsks",MODE_PRIVATE,null);
        contactsDB.myDb.execSQL("CREATE TABLE IF NOT EXISTS contacts(name string,number string);");
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to place a call ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Toast.makeText(getApplicationContext(),"Placing the Call",Toast.LENGTH_SHORT).show();
                        placeCall();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.addcontact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(getApplicationContext(),AddContactActivity.class);
            startActivityForResult(i,0);
            }
        });

         contacts = (ListView) findViewById(R.id.contacts);
        contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = contacts.getItemAtPosition(i);
                HashMap<String,String> cData = (HashMap<String,String>) o;
                cNumber = cData.get("Number");
                cName = cData.get("Name");
                AlertDialog alert = builder.create();
                alert.setTitle(cName);
                alert.show();
            }
        });

        //contactsDB.myDb.execSQL("DROP TABLE contacts ");
        //contactsDB.execSQL("INSERT INTO contacts VALUES('Santosh','8583055132');");
        updateContacts();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            updateContacts();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public  void updateContacts(){
        ListView contacts = (ListView) findViewById(R.id.contacts);
        arr = new ArrayList<HashMap<String, String>>();
        Cursor resultSet = contactsDB.myDb.rawQuery("Select * from contacts ORDER BY name",null);
        resultSet.moveToFirst();
        while (resultSet.isAfterLast() == false) {
            HashMap<String,String> tmp = new HashMap<String, String>();
            String Uname = resultSet.getString(0);
            String Unumber = resultSet.getString(1);
            tmp.put("Name", Uname);
            tmp.put("Number", Unumber);
            arr.add(tmp);
            resultSet.moveToNext();
        };
        SimpleAdapter adapt = new SimpleAdapter(
                this,arr,android.R.layout.simple_list_item_2,new String[]{"Name","Number"},
                new int[]{android.R.id.text1,android.R.id.text2});
        contacts.setAdapter(adapt);

    }
    void placeCall()
    {
        Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+cNumber.trim()));
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CALL_PHONE);
            startActivity(intent);
        }
        catch(ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),"Unable to place the call"+e,Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
       if(resultCode==RESULT_OK) {
           super.onActivityResult(requestCode, resultCode, data);
           updateContacts();
       }
    }
}
