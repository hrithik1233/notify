package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {
    public static final String APP_LOGIN = "applogin";
    public static final String IS_LOGGED = "isloged";
    RelativeLayout signuup;
    public static final String MAIN_DATABASE_NAME = "databasename";

    EditText name, password, email;
    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
        }
        setContentView(R.layout.activity_signup_page);
       signuup=findViewById(R.id.signupbtn);
       signuup.setOnClickListener(view -> {

           signUp();
       });
        name = findViewById(R.id.signName);
        password = findViewById(R.id.sign_password);
        email = findViewById(R.id.sign_email);
        auth = FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar(); // Obtain a reference to the action bar
        if (actionBar != null) {
            actionBar.hide(); // Hide the action bar
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        LinearLayout layout = findViewById(R.id.mian_layout_anim_login);
        AnimationDrawable drawable = (AnimationDrawable) layout.getBackground();
        drawable.setEnterFadeDuration(0);
        drawable.setExitFadeDuration(1000);
        drawable.start();

    }

    public void signUp() {
        String _name = name.getText().toString();
        String _password = password.getText().toString();
        String _email = email.getText().toString();
        if(!NetworkUtils.isInternetIsConnected(this)){
            Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (_name.equals("") || _password.equals("") || email.equals("")) {
                throw new Exception("empty");
            }
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();


            auth.createUserWithEmailAndPassword(_email, _password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String _name1 = name.getText().toString();
                    String _password1 = password.getText().toString();
                    String _email1 = email.getText().toString();
                    Intent intent = new Intent(SignUpActivity.this, Home.class);
                    SharedPreferences settings=getSharedPreferences("Setting",MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor2=settings.edit();
                    editor2.putBoolean("isPasswordEnable",false);
                    editor2.apply();
                    Toast.makeText(SignUpActivity.this, "sign up successfully", Toast.LENGTH_SHORT).show();
                    SharedPreferences preferences = getSharedPreferences(APP_LOGIN, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    FirebaseUser user = auth.getCurrentUser();
                    String MainDatabase_name = "User" + user.getUid();
                    editor.putBoolean(IS_LOGGED, true);
                    editor.putString("login_name", _name1);
                    editor.putString("login_password", _password1);
                    editor.putString("login_email", _email1);
                    editor.putString(MAIN_DATABASE_NAME, MainDatabase_name);
                    editor.commit();
                    runOnUiThread(progressDialog::dismiss);
                    firebaseIntailization();
                    startActivity(intent);
                    finish();

                } else {
                    String errorMessage = task.getException().getMessage();
                    runOnUiThread(progressDialog::dismiss);
                    Toast.makeText(SignUpActivity.this, "Sign up failed: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> {
                runOnUiThread(progressDialog::dismiss);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            if (_name.length() == 0) {
                name.setError("Blank");
                name.requestFocus();
            }

            if (_password.length() == 0) {
                password.setError("Blank");
                password.requestFocus();
            }
            if (_email.length() == 0) {
                email.setError("Blank");
                email.requestFocus();
            }


        }
    }

    private void firebaseIntailization() {
        try {
            DatabaseReference firebase;
            SharedPreferences pref = getSharedPreferences("applogin", MODE_PRIVATE);
            String databaseMain = pref.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            firebase = FirebaseDatabase.getInstance().getReference();
            String name = pref.getString("login_name", "unotherized");
             Calendar calendar = Calendar.getInstance(); // For Calendar class
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(currentDate);
            String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            DateAndTime dateAndTime = new DateAndTime(date, time);
            firebase.child(databaseMain).child("Userdata-1").child("email").setValue(pref.getString("login_email", ""));
            firebase.child(databaseMain).child("Userdata-1").child("name").setValue(name);
            firebase.child(databaseMain).child("Userdata-1").child("dateAndTime").setValue(dateAndTime);
            firebase.child(databaseMain).child("Userdata-1").child("isSecuredMode").setValue(true);
            firebase.child(databaseMain).child("Userdata-1").child("NoLogedDevice").setValue(1);
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener((s) -> {
                SharedPreferences preferences=getSharedPreferences("token",MODE_PRIVATE);
                preferences.edit().putString("mytoken",s).apply();

                FirebaseDatabase.getInstance().getReference(databaseMain).child("Userdata-1").child("token").setValue(s);
            } );


        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        super.onBackPressed();
    }
}