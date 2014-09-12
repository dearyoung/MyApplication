package com.example.ralph.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


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
        SharedPreferences sharedRef = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        EditText editText = (EditText) findViewById(R.id.edit_message);

        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putInt("height", 175);
        editor.putInt("weight", 65);
        editor.putString("name", "Ralph");
        editor.commit();
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
