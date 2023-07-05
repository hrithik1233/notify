package com.example.myapplication;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
      EditText password,email;
      FirebaseAuth auth;
    String psswd,eml;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ActionBar actionBar = getSupportActionBar(); // Obtain a reference to the action bar
        if (actionBar != null) {
            actionBar.hide(); // Hide the action bar
        }
        auth=FirebaseAuth.getInstance();
        password=findViewById(R.id.login_password);
        email=findViewById(R.id.login_email);
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
        if(NetworkUtils.isInternetIsConnected(this)==false){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
        psswd=password.getText().toString();
         eml=email.getText().toString();
        if(psswd.equals("")||eml.equals("")||eml.length()<8)
            throw new Exception();
         auth.signInWithEmailAndPassword(eml,psswd).addOnCompleteListener(task -> {

             if(task.isSuccessful()){
                 Intent intent=new Intent(LoginActivity .this,Home.class);
                 Toast.makeText(LoginActivity.this, "login successfully", Toast.LENGTH_SHORT).show();
                 SharedPreferences preferences=getSharedPreferences(SignUpActivity.APP_LOGIN,MODE_PRIVATE);
                 SharedPreferences.Editor editor=preferences.edit();
                 FirebaseUser user=auth.getCurrentUser();
                 String MainDatabase_name="User"+user.getUid();
                 DatabaseReference ref=FirebaseDatabase.getInstance().getReference()
                         .child(MainDatabase_name).child("Userdata-1").child("name");
                 ref.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name=snapshot.getValue(String.class);
                         SharedPreferences preferences=getSharedPreferences(SignUpActivity.APP_LOGIN,MODE_PRIVATE);
                         SharedPreferences.Editor editor=preferences.edit();
                         editor.putString("login_name",name);
                         editor.commit();
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 });

                 editor.putBoolean(SignUpActivity.IS_LOGGED,true);
                 editor.putString("login_password",psswd);
                 editor.putString("login_email",eml);
                 editor.putString(SignUpActivity.MAIN_DATABASE_NAME,MainDatabase_name);
                 editor.commit();
                 startActivity(intent);
                 finish();
             }else{
                 String errorMessage = task.getException().getMessage();
                 Toast.makeText(LoginActivity.this, "login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
             }
         });
        }catch (Exception e){
            if(eml.equals("")){
                email.setError("blank");
                email.requestFocus();
            }
            if(psswd.equals("")){
                password.setError("blank");
                password.requestFocus();
            }else{
                email.setError("invalid");
                email.requestFocus();
            }
            //e.printStackTrace();
        }

    }
    public void signUp(View v){
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(this, "sign up", Toast.LENGTH_SHORT).show();

    }
    public void forgotpassword(View v){
        try {


      FirebaseAuth auth=FirebaseAuth.getInstance();
      eml=email.getText().toString();
      if(eml.equals("")){
          email.setError("blank");
          email.requestFocus();
      }
      auth.sendPasswordResetEmail(eml).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
         if(task.isSuccessful()){
             Toast.makeText(LoginActivity.this, "Message has be send to: "+eml, Toast.LENGTH_SHORT).show();
         }else{
             Toast.makeText(LoginActivity.this, "unable to send request", Toast.LENGTH_SHORT).show();
         }
          }
      });
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}