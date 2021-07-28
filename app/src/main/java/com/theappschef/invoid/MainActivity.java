package com.theappschef.invoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.theappschef.mylibrary.InvoidMain;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id=getRandomNumberString();
        findViewById(R.id.auth).setOnClickListener(v -> {
            Intent intent=new Intent(this, InvoidMain.class);
            intent.putExtra("id",id);
            startActivityForResult(intent,100);
        });
        TextView textView=findViewById(R.id.uid);
        textView.setText(id);
    }
    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(99999999);
        return String.format("%08d", number);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {

            if (resultCode == RESULT_OK
                    && null != data) {
                Button auth=findViewById(R.id.auth);
                auth.setText("Done");
                auth.setEnabled(false);
            }
        }

    }
}