package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {
   public static final  String APP_LOGIN="applogin";
    public static final  String IS_LOGGED="isloged";
    public static final String MAIN_DATABASE_NAME="databasename";

    EditText name,password,email;
    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        name=findViewById(R.id.signName);
        password=findViewById(R.id.sign_password);
        email=findViewById(R.id.sign_email);
        auth=FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar(); // Obtain a reference to the action bar
        if (actionBar != null) {
            actionBar.hide(); // Hide the action bar
        }
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        LinearLayout layout=findViewById(R.id.mian_layout_anim_login);
        AnimationDrawable drawable= (AnimationDrawable) layout.getBackground();
        drawable.setEnterFadeDuration(0);
        drawable.setExitFadeDuration(1000);
        drawable.start();

    }
    public void login(View v){
        String _name=name.getText().toString();
        String _password=password.getText().toString();
        String _email=email.getText().toString();

        try {
          if(_name.equals("")||_password.equals("")||email.equals(""))
              throw new Exception("empty");
          String dbName="";

           auth.createUserWithEmailAndPassword(_email,_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String _name=name.getText().toString();
                    String _password=password.getText().toString();
                    String _email=email.getText().toString();
                    Intent intent=new Intent(SignUpActivity .this,Home.class);
                    Toast.makeText(SignUpActivity .this, "sign up successfully", Toast.LENGTH_SHORT).show();
                    SharedPreferences preferences=getSharedPreferences(APP_LOGIN,MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    FirebaseUser user=auth.getCurrentUser();
                    String MainDatabase_name="User"+user.getUid();
                    editor.putBoolean(IS_LOGGED,true);
                    editor.putString("login_name",_name);
                    editor.putString("login_password",_password);
                    editor.putString("login_email",_email);
                    editor.putString(MAIN_DATABASE_NAME,MainDatabase_name);
                    editor.commit();
                    firebaseIntailization();
                    startActivity(intent);
                    finish();
                }else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(SignUpActivity .this, "Sign up failed: " + errorMessage, Toast.LENGTH_LONG).show();
                }
               }
           });
        }catch (Exception e){
            if(_name.length()==0){
                name.setError("Blank");
                name.requestFocus();
            }

            if(_password.length()==0){
                password.setError("Blank");
                password.requestFocus();
            }
            if(_email.length()==0){
                email.setError("Blank");
                email.requestFocus();
            }


        }
    }
    private void firebaseIntailization() {
        try {
            DatabaseReference firebase;
            SharedPreferences pref=getSharedPreferences("applogin",MODE_PRIVATE);
            String databaseMain=pref.getString(SignUpActivity.MAIN_DATABASE_NAME,"default");
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            firebase= FirebaseDatabase.getInstance().getReference();
            String name=pref.getString("login_name","unotherized");
            Toast.makeText(this, databaseMain, Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance(); // For Calendar class
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(currentDate);
            String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
            DateAndTime dateAndTime=new DateAndTime(date,time);
            firebase.child(databaseMain).child("Userdata-1").child("email").setValue(pref.getString("login_email",""));
            firebase.child(databaseMain).child("Userdata-1").child("name").setValue(name);
            firebase.child(databaseMain).child("Userdata-1").child("dateAndTime").setValue(dateAndTime);


        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }




}