package com.aot.devsks.contactmanager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddContactActivity extends AppCompatActivity {
    private EditText name;
    private EditText number;
    private Button add;
    private DBHelp myDB = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Object obj = getIntent().getSerializableExtra("myContacts");
        myDB = new DBHelp();
        myDB.myDb = openOrCreateDatabase("devsks",MODE_PRIVATE,null);
        name = (EditText) findViewById(R.id.name);
        name.requestFocus();
        number = (EditText) findViewById(R.id.number);
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = name.getText().toString();
                String newNumber = number.getText().toString();
                TextView error = (TextView) findViewById(R.id.hint);
                error.setText("");
                if(myDB!=null && newName.length() > 0 && newNumber.length() == 10) {

                    myDB.myDb.execSQL("INSERT INTO contacts VALUES('" + newName + "','" + newNumber + "')");
                    Toast.makeText(getApplicationContext(), "New Contact Added", Toast.LENGTH_SHORT).show();

                    name.setText("");
                    number.setText("");
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    error.setError("");
                    if(newName.equalsIgnoreCase(""))
                        error.setText("Enter the Name of the Contact.");
                    else
                        error.setText("Mobile Number should be of 10 digit.");
                }


            }
        });
    }
}
