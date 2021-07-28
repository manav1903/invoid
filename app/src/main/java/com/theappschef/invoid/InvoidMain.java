package com.theappschef.invoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InvoidMain extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoidmain);
        editText = findViewById(R.id.title);
        findViewById(R.id.gallery).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1002);
        });
        findViewById(R.id.upload).setOnClickListener(v -> {
            if(encodedImage!=""){
            try {
                upload(encodedImage);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            }
            else {
                Toast.makeText(this,"Select an image",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    void upload(String encoded) throws MalformedURLException {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", "+917042460662");
            jsonObject.put("text", editText.getText().toString());
            jsonObject.put("img", encoded);
            postguy("https://theappschef.in/manav/saveInvoid.php", jsonObject.toString(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(InvoidMain.this, "error" + e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject jsonObject1 = new JSONObject(response.body().string());
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            );
        } catch (JSONException ex) {
            Log.d("Exception", "JSON exception", ex);
        }
    }

    void postguy(String url, String json, okhttp3.Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(callback);
    }
    String encodedImage;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002) {

            if (resultCode == RESULT_OK
                    && null != data) {
                Uri se=data.getData();
                Bitmap bm = BitmapFactory.decodeFile(se.getPath());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            }
        }
    }
}


















