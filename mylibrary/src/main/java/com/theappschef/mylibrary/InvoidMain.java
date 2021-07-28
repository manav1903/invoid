package com.theappschef.mylibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InvoidMain extends AppCompatActivity {
    EditText editText;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoidmain);
        editText = findViewById(R.id.title);
        result = findViewById(R.id.result);
        findViewById(R.id.gallery).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1002);
        });
        findViewById(R.id.camera).setOnClickListener(v -> {
          dispatchTakePictureIntent();
        });
        findViewById(R.id.upload).setOnClickListener(v -> {
            if (!encodedImage.equals("")) {
                findViewById(R.id.progress).setVisibility(View.VISIBLE);
                try {
                    upload(encodedImage);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Select an image", Toast.LENGTH_SHORT).show();
            }
        });

    }
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    void upload(String encoded) throws MalformedURLException {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", getIntent().getStringExtra("id"));
            jsonObject.put("text", editText.getText().toString());
            jsonObject.put("img", encoded);

            Handler mHandler = new Handler(Looper.getMainLooper());
            postguy("https://theappschef.in/manav/saveInvoid.php", jsonObject.toString(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            mHandler.post(() -> result.setText("Please check your internet and Try Again"));
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            mHandler.post(() ->
                                    findViewById(R.id.progress).setVisibility(View.GONE));
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject jsonObject1 = new JSONObject(response.body().string());
                                    String res=jsonObject1.getString("res");
                                    String s = jsonObject1.getString("res") + " " + jsonObject1.getString("error");
                                    mHandler.post(() -> result.setText(s));
                                    mHandler.postDelayed(() -> {
                                            if(res.toLowerCase().equals("success")){
                                                Intent data = new Intent();
                                                String text = "Result to be returned....";
                                                data.setData(Uri.parse(text));
                                                setResult(RESULT_OK, data);
                                                finish();
                                            }
                                            else {
                                                Intent data = new Intent();
                                                String text = "Result to be returned....";
                                                data.setData(Uri.parse(text));
                                                setResult(RESULT_CANCELED, data);
                                                finish();
                                            }
                                    }, 3000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//
//                                    finish();
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

    String encodedImage = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002) {

            if (resultCode == RESULT_OK
                    && null != data) {
                Uri se = data.getData();
                ImageView imageView = findViewById(R.id.image);
                imageView.setImageURI(se);
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bm = drawable.getBitmap();
//                Bitmap bm = BitmapFactory.decodeFile(se.getPath());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            }
        }
        else   if (requestCode == 1) {

            if (resultCode == RESULT_OK
                    && null != data) {
//                Uri se = data.getData();
                ImageView imageView = findViewById(R.id.image);
//                imageView.setImageURI(se);
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bm = drawable.getBitmap();
//                Bitmap bm = BitmapFactory.decodeFile(se.getPath());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            }
        }
    }

}


















