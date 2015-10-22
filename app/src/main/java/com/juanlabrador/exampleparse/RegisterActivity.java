package com.juanlabrador.exampleparse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by juanlabrador on 21/10/15.
 */
public class RegisterActivity extends Activity {

    private EditText username;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button register;

    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.username);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        register = (Button) findViewById(R.id.signin);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("Creating account, wait...");
        dialog.show();
        user = new ParseUser();
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        user.setEmail(email.getText().toString());
        user.put("name", name.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error in create account! Try later", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }
}
