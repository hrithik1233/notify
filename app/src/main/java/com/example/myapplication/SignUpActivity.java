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
        if (Build.VERSION.SDK_INT >=   Build.VERSION_CODES.KITKAT) {
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



}