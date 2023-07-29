package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.provider.Settings;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class HelpMenuOptionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       Window window= getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_help_menu_option);
    }

    public void passwordReset(View v) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(HelpMenuOptionActivity.this, R.style.CustomAlertDialogTheme));
        builder.setTitle("Password reset");
        builder.setMessage("request password reset link").setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences preferences = getSharedPreferences(SignUpActivity.APP_LOGIN, MODE_PRIVATE);
                String email = preferences.getString("login_email", "");
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HelpMenuOptionActivity.this, " reset link send to " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HelpMenuOptionActivity.this, "not valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).show();

    }

    public void goToAccessibility(View v) {
        new Handler().postDelayed(() -> {
            Intent acc = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            acc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(acc);
        }, 200);
    }

}