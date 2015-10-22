package com.juanlabrador.exampleparse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

/**
 * Created by juanlabrador on 22/10/15.
 */
public class NewActivity extends Activity {

    private EditText name;
    private EditText model;
    CheckBox haveNitro;
    Button create;

    Player player;
    Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        name = (EditText) findViewById(R.id.name);
        model = (EditText) findViewById(R.id.modelCar);
        haveNitro = (CheckBox) findViewById(R.id.haveNitro);
        create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPlayer();
            }
        });

    }

    private void newPlayer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Creating player, wait...");
        dialog.show();
        player = new Player();
        player.setName(name.getText().toString());
        player.setScore(100);
        player.setUUID();
        player.setUser(ParseUser.getCurrentUser());

        JSONArray array = new JSONArray();
        Gson gson = new Gson();

        Car car1 = new Car(model.getText().toString(), haveNitro.isChecked());
        Car car2 = new Car("Renault", haveNitro.isChecked());

        array.put(gson.toJson(car1));
        array.put(gson.toJson(car2));

        player.setCars(array);

        player.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                player.pinInBackground();
                setResult(RESULT_OK);
                finish();
            }
        });
        /*player.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    car = new Car();
                    car.setModel(model.getText().toString());
                    car.setUUID();
                    car.haveNitro(haveNitro.isChecked());
                    car.setPlayer(player);
                    car.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                player.pinInBackground();
                                car.pinInBackground();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error save car!", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
                } else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error create player!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
}
