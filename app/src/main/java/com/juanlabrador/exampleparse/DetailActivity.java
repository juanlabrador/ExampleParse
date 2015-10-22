package com.juanlabrador.exampleparse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by juanlabrador on 19/10/15.
 */
public class DetailActivity extends Activity {

    private ImageView imageView;
    private TextView id;
    private TextView tvuuid;
    private EditText name;
    private EditText score;
    private String uuid;
    private EditText model1;
    private EditText model2;

    private Button edit;
    private Button delete;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        uuid = getIntent().getStringExtra("ID");

        imageView = (ImageView) findViewById(R.id.photo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), 2);
            }
        });

        id = (TextView) findViewById(R.id.id);
        tvuuid = (TextView) findViewById(R.id.uuid);
        name = (EditText) findViewById(R.id.etName);
        score = (EditText) findViewById(R.id.etScore);

        model1 = (EditText) findViewById(R.id.model1);
        model2 = (EditText) findViewById(R.id.model2);

        edit = (Button) findViewById(R.id.update);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }
        });
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        ParseQuery<Player> query = Player.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("uuid", uuid);
        query.getFirstInBackground(new GetCallback<Player>() {
            @Override
            public void done(Player object, ParseException e) {
                if (e == null) {
                    player = object;
                    id.setText("ID: " + object.getObjectId());
                    tvuuid.setText("UUID: " + object.getUUID());
                    name.setText(object.getName());
                    score.setText(String.valueOf(object.getScore()));

                    JSONArray array = object.getCars();
                    Log.i("TAG array", array.toString());
                    for (int i = 0; i < array.length(); i++) {
                        Gson gson = new Gson();
                        if (i == 0) {
                            try {
                                Car car = gson.fromJson(array.get(i).toString(), Car.class);
                                model1.setText(car.getModel());
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        } else if (i == 1) {
                            try {
                                Car car = gson.fromJson(array.get(i).toString(), Car.class);
                                model2.setText(car.getModel());
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }

                    }

                    ParseFile file = object.getPhoto();
                    if (file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap bmp = BitmapFactory
                                            .decodeByteArray(
                                                    data, 0,
                                                    data.length);

                                    imageView.setImageBitmap(bmp);
                                } else {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void edit() {
        player.setName(name.getText().toString());
        player.setScore(Integer.parseInt(score.getText().toString()));
        JSONArray array = new JSONArray();
        Car car1 = new Car(model1.getText().toString(), true);
        Car car2 = new Car(model2.getText().toString(), true);

        Gson gson = new Gson();
        array.put(gson.toJson(car1));
        array.put(gson.toJson(car2));

        player.setCars(array);
        player.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void delete() {
        player.deleteEventually(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    player.unpinInBackground();
                    Toast.makeText(getApplicationContext(), "Delete!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                if (data != null) {
                    getBytes(data.getData());
                }
            }
        }
    }

    private void getBytes(final Uri uri) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("upload image...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        Uri data = uri;
        Bitmap bitmap = null;
        try {
           bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] image = new byte[0];
        try {
            image = readBytes(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ParseFile parseFile = new ParseFile("photo.png", image);
        final Bitmap finalBitmap = bitmap;
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {


                    imageView.setImageBitmap(finalBitmap);

                    player.put("photo", parseFile);
                    player.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                setResult(RESULT_OK);
                                Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Toast.makeText(getApplicationContext(), "Photo update!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer percentDone) {
                progressDialog.setProgress(percentDone);
            }
        });
    }

    public byte[] readBytes(Uri uri) throws IOException {
       /* // this dynamically extends to take the bytes you read
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();

        */
        byte[] data = null;
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;

            options.outHeight = 50;
            options.outWidth = 50;
            options.inSampleSize = 8;
            bitmap= BitmapFactory.decodeStream(inputStream,null,options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            data = baos.toByteArray();
            bitmap.recycle();
            System.gc();
            Runtime.getRuntime().gc();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }
}
