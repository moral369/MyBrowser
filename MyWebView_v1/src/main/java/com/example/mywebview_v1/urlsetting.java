package com.example.mywebview_v1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

public class urlsetting extends Activity {

    EditText url_address;
    public static String current_url;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.url_input);
        url_address = (EditText)findViewById(R.id.url_input);
    };

    public void onOkClick(View v)throws IOException
    {
        current_url = "http://" + url_address.getText().toString();

        Intent intent = new Intent(this, FullscreenActivity.class);
        startActivity(intent);
    }
}
