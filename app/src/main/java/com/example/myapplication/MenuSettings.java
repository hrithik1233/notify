package com.example.myapplication;

import static com.example.myapplication.SignUpActivity.APP_LOGIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MenuSettings extends AppCompatActivity {
    String password;
    MaterialButton setPasswordBotton;
    MaterialButton btn;
    SharedPreferences settings;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences("Setting", MODE_PRIVATE);

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
        }

        View decor = getWindow().getDecorView();
        int flags = decor.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decor.setSystemUiVisibility(flags);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.BLUE);
        setContentView(R.layout.activity_settings);
        btn=findViewById(R.id.offline_mode_btn);
        setPasswordBotton = findViewById(R.id.setpassword_btn);

        setpassword();
    }

    boolean imgSet = false;

    private void setpassword() {

        setPasswordBotton.setOnClickListener(view -> {
            try {
            SharedPreferences settings = getSharedPreferences("Setting", MODE_PRIVATE);
            Dialog setpasswordDialog = new Dialog(this);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = settings.edit();

            setpasswordDialog.setContentView(R.layout.setpassword_settings_layout);
            setpasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            setpasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            setpasswordDialog.show();
            ImageView img = setpasswordDialog.findViewById(R.id.setpasswordimage);
            EditText pass = setpasswordDialog.findViewById(R.id.setpasswordtext);
            Button ok = setpasswordDialog.findViewById(R.id.btn_setpassword);
            @SuppressLint("UseSwitchCompatOrMaterialCode")
            Switch switchbtn = setpasswordDialog.findViewById(R.id.setpasswordswitch);
            switchbtn.setChecked(settings.getBoolean("isPasswordEnable", false));
            switchbtn.setOnClickListener(view12 -> {
                editor.putBoolean("isPasswordEnable", switchbtn.isChecked());
                editor.apply();
            });
            pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            img.setOnClickListener(view1 -> {
                imgSet = !imgSet;
                if (!imgSet) {
                    img.setImageResource(R.drawable.eye_24);
                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    img.setImageResource(R.drawable.eye_close);
                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                }

            });
            ok.setEnabled(false);
            ok.setAlpha(0.5F);
            String password;
            pass.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() < 6) {
                        ok.setEnabled(false);
                        ok.setAlpha(0.5F);
                    } else {
                        ok.setEnabled(true);
                        editor.putString("passwordM", editable.toString());
                        editor.apply();
                        ok.setAlpha(1F);
                    }
                }
            });

            ok.setOnClickListener(view1 -> {

                if (pass.length() >= 6 && switchbtn.isChecked()) {
                    editor.putBoolean("isPasswordEnable", switchbtn.isChecked());
                    Toast.makeText(getApplicationContext(), "password set successful", Toast.LENGTH_SHORT).show();
                    editor.apply();
                }
            });
            }catch (Exception e){

            }

        });

    }


    public void offlineMode(View v) {

         Dialog dialog=new Dialog(this);
       boolean set= settings.getBoolean("isOfflineMode",false);
        dialog.setContentView(R.layout.offline_mode_layout);
         dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
          dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
         dialog.show();
        SharedPreferences.Editor editor=settings.edit();
        TextView txt=dialog.findViewById(R.id.offline_current_mode);
        Switch s=dialog.findViewById(R.id.offline_mode_switch);
        s.setChecked(set);
        if(set){
            txt.setText("Current mode: offline");

        }else{
            txt.setText("Current mode: online");
        }

        s.setOnClickListener(view -> {
         if(s.isChecked()){
             editor.putBoolean("isOfflineMode",true);
             txt.setText("Current mode: offline");

             editor.apply();
         }else{
             editor.putBoolean("isOfflineMode",false);
             txt.setText("Current mode: online");
             editor.apply();
         }
        });


    }

    public void onSingleMode(View v) {
        if(NetworkUtils.isInternetIsConnected(this)){

        SharedPreferences preferences = getSharedPreferences(APP_LOGIN, MODE_PRIVATE);
        String maindatabase = preferences.getString(SignUpActivity.MAIN_DATABASE_NAME, "null");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(maindatabase).child("Userdata-1");
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.single_mode_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
        ProgressDialog pr = new ProgressDialog(this);
        pr.show();
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch sw = dialog.findViewById(R.id.single_mode_btn);
        ref.child("isSecuredMode").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean res = snapshot.getValue(Boolean.class);
                sw.setChecked(res);
                runOnUiThread(pr::dismiss);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sw.setOnClickListener(view ->{
            ref.child("isSecuredMode").setValue(sw.isChecked()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ref.child("NoLogedDevice").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                if(sw.isChecked()){
                                    Toast.makeText(MenuSettings.this, "Secured Mode activated", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(MenuSettings.this, "Secured Mode Deactivated", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });

                }
            }).addOnFailureListener(e -> Toast.makeText(MenuSettings.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        }else{
            Toast.makeText(this, "No connnection", Toast.LENGTH_SHORT).show();
        }



    }

    @SuppressLint("Range")
    public void resetAllData(View v) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
            builder.setMessage("Do you want to reset all the data");
            builder.setPositiveButton("No", null);
            builder.setNegativeButton("Yes", (dialogInterface, i) -> {

                String MainDatabse = getIntent().getStringExtra("databaseName");
                DatabaseBatch maindb = new DatabaseBatch(this, MainDatabse);
                StudentsDatabase db = new StudentsDatabase(this);
                Cursor cur = maindb.fetch();

                if (cur != null) {
                    while (cur.moveToNext()) {
                        String tableName = cur.getString(0).toUpperCase();
                        FirebaseDatabase.getInstance().getReference(MainDatabse).child(tableName).removeValue();
                        db.dropTable(tableName);
                    }

                }
                maindb.deleteAll();
                Toast.makeText(this, "all data is erased", Toast.LENGTH_SHORT).show();
            }).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}