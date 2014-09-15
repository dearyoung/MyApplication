package com.example.ralph.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class MyActivity extends Activity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // Handle presses on the action bar items
        switch (id) {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSearch() {
        // SharedPreferences 2014.09.12
        SharedPreferences sharedRef = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        EditText editText = (EditText) findViewById(R.id.edit_message);

        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putInt("height", 175);
        editor.putInt("weight", 65);
        editor.putString("name", "Ralph");
        editor.commit();

        // Implicit Intent 2014.09.13
//        Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
//        Intent intent = new Intent(Intent.ACTION_VIEW, location);

//        PackageManager packageManager = getPackageManager();
//        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
//        boolean isIntentState = activities.size() > 0;
//
//        if(isIntentState) {
//            startActivity(intent);
//        }

//        Uri webpage = Uri.parse("http://www.android.com");
//        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//        Intent chooser = Intent.createChooser(intent, "골라보셈");
//
//        if(intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(chooser);
//        }


        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);


    }

    static int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
               Uri contactUri = data.getData();
               String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER };

                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);

                EditText editText = (EditText) findViewById(R.id.edit_message);
                editText.setText("Phone Number is " + number);

            }
        }
    }


    private void openSettings() {
        SharedPreferences sharedRef = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int height = sharedRef.getInt("height", 0);
        int weight = sharedRef.getInt("weight", 0);
        String name = sharedRef.getString("name", "noname");

        EditText editText = (EditText) findViewById(R.id.edit_message);
        editText.setText("name: " + name + ", height: " + height + ", weight: " + weight);

        saveFile();
        File f = getTempFile(this, "/aaa/bbb/ralph_temp.txt");

        editText.setText(editText.getText() + "::" + f.getAbsolutePath());
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    private void saveFile() {
        String filename = "ralph001.txt";
        File file = new File(getFilesDir(), filename);

        String s = "Hello World";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(s.getBytes());
            outputStream.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private File getTempFile(Context context, String url) {
        File file = null;

        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


}
