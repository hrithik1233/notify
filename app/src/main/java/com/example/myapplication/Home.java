package com.example.myapplication;
import static com.example.myapplication.SignUpActivity.APP_LOGIN;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Home extends AppCompatActivity implements RecyclerBatchInterface {
    Dialog dialog;


    ImageView NavBarNotificationHighlight;
    final int[] resultNotification = new int[1];
    DatabaseBatch databaseBatch;
    RecyclerView recyclerView;
    ArrayList<Homefiles> arrayList;

    DatabaseReference firebase;

    Dialog navManu;
    String databaseMain = "";
    HomeFilesAdapter homeFilesAdapter;

    @SuppressLint({"MissingInflatedId", "RtlHardcoded"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception ignored){}

     resultNotification[0]=0;

        NavBarNotificationHighlight = findViewById(R.id.notificationHighlightHome);
        NavBarNotificationHighlight.setVisibility(View.INVISIBLE);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> FirebaseDatabase.getInstance().getReference(databaseMain).child("Userdata-1").child("token").setValue(s));

        navManu = new Dialog(this);
        navManu.setContentView(R.layout.navingatiob_bar);
        setPrefrences();
        navManu.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        navManu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        navManu.getWindow().setGravity(Gravity.LEFT);
        navManu.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        navManu.setCancelable(true);
        navManu.setCanceledOnTouchOutside(true);
        SharedPreferences preferences = getSharedPreferences(APP_LOGIN, MODE_PRIVATE);
        databaseMain = preferences.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reff1 = ref.child(databaseMain).child("Userdata-1");
   checkAnyMessageRecieved();



        reff1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SharedPreferences preferences = getSharedPreferences(APP_LOGIN, MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (Objects.equals(data.getKey(), "name")) {
                        String val = data.getValue(String.class);
                        editor.putString("login_name", val);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String Main_database_name;
        Main_database_name = preferences.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
        SharedPreferences pre = getSharedPreferences(APP_LOGIN, MODE_PRIVATE);


            if (!pre.getBoolean("isaccessed", true)) {
                AlertDialog.Builder AcceDialog=new AlertDialog.Builder(new ContextThemeWrapper(Home.this, R.style.CustomAlertDialogTheme));
                AcceDialog.setCancelable(false);
                AcceDialog.setMessage("Allow accessiblity service for notify");
                ImageView imgView=new ImageView(this);
                imgView.setImageResource(R.drawable.accessiblity_serivice_image1g);
                AcceDialog.setView(imgView);
                AcceDialog.setPositiveButton("ok", (dialogInterface, i) -> {
                    Intent acc = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    acc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(acc);
                    SharedPreferences.Editor editor = pre.edit();
                    editor.putBoolean("isaccessed", true);
                    editor.apply();
                });
            }

        recyclerView = findViewById(R.id.batch_recycler);
        arrayList = new ArrayList<>();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_newbatch_diolgue);

        databaseBatch = new DatabaseBatch(this, Main_database_name);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        homeFilesAdapter = new HomeFilesAdapter(Home.this, arrayList, this);
        recyclerView.setAdapter(homeFilesAdapter);

        // fetch the database and load the data of batches into the main page..
        fetch();
        //..


    }

    private void checkAnyMessageRecieved() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference(databaseMain);
          ref.child("messageRecieved").addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if(snapshot.getChildrenCount()>0){
                    resultNotification[0]=1;
                      NavBarNotificationHighlight.setVisibility(View.VISIBLE);
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });


    }

    private void setPrefrences() {
        SharedPreferences pref = getSharedPreferences("applogin", MODE_PRIVATE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(databaseMain + "/" + "Userdata-1/dateAndTime");

        SharedPreferences.Editor editor = pref.edit();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {

                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (Objects.equals(data.getKey(), "date")) {
                            String date = data.getValue(String.class);
                            editor.putString("reg_date", date);
                        } else {
                            String time = data.getValue(String.class);
                            editor.putString("reg_time", time);
                        }
                    }
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }


    @SuppressLint("NotifyDataSetChanged")
    public void addBatch(View view) {
        try {
            EditText btcName, btcYr;
            Button btn1, btn2;
            btcName = dialog.findViewById(R.id.batchname);
            btcYr = dialog.findViewById(R.id.batchyear);
            btn1 = dialog.findViewById(R.id.cancel);
            btn2 = dialog.findViewById(R.id.create);
            btcName.setText("");
            btcYr.setText("");
            dialog.show();
            btn1.setOnClickListener(view1 -> dialog.cancel());
            btn2.setOnClickListener(view12 -> {
                try {
                    String name = btcName.getText().toString();
                    String year = btcYr.getText().toString();
                    if (name.equals("") || !Character.isAlphabetic(name.charAt(0)))
                        throw new Exception("error");
                    if (year.equals("")) year = "unknown";
                    for (char c : name.toCharArray()) {
                        if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                            Toast.makeText(Home.this, "Use only alphabets and digits", Toast.LENGTH_SHORT).show();
                            throw new Exception();
                        }
                    }
                    Homefiles homefiles = new Homefiles(name, year);
                    //inserting the batch details into the database
                    Boolean res = databaseBatch.insert(homefiles);
                    firebase = FirebaseDatabase.getInstance().getReference(databaseMain);
                    firebase.child(homefiles.getTable_Name()).child("batchdata").setValue(homefiles);
                    if (res) {
                        SharedPreferences pref = getSharedPreferences("applogin", MODE_PRIVATE);
                        String databaseMain = pref.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
                        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference(databaseMain).child(homefiles.getTable_Name());
                        firebase.child("batchdata").child("NoOfStudents").setValue("0");
                        arrayList.add(0, homefiles);

                        Toast.makeText(Home.this, "Batch has been created successfully", Toast.LENGTH_SHORT).show();
                        homeFilesAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(Home.this, "Batch already exists", Toast.LENGTH_SHORT).show();
                    }

                    dialog.cancel();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Home.this, "Error creating batch", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception ignored) {
        }
    }

    @Override
    public void itemOnClick(int position) {
        Intent intent = new Intent(this, AddDataMainPage.class);
        String batchname = arrayList.get(position).getBatch();
        String yearof = arrayList.get(position).getYear();
        intent.putExtra("batch_name", batchname);
        intent.putExtra("batchYear", yearof);
        intent.putExtra("TABLE_NAME", arrayList.get(position).getTable_Name());
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetch() {
        //fetches from either internal database or firebase database
        try {

            Cursor cursor = databaseBatch.fetch();
            if (cursor != null && cursor.moveToFirst()) {
                int f1 = cursor.getColumnIndex(DatabaseBatch.BATCH_NAME);
                int f2 = cursor.getColumnIndex(DatabaseBatch.BATCH_YEAR);
                do {
                    arrayList.add(new Homefiles(cursor.getString(f1), cursor.getString(f2)));
                } while (cursor.moveToNext());
                Collections.reverse(arrayList);
                homeFilesAdapter.notifyDataSetChanged();

            }
            if (arrayList.size() == 0) {
                if (!NetworkUtils.isInternetIsConnected(this)) {
                    throw new Exception("offline unable to fetch data");
                }
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(true);
                progressDialog.show();
                firebase = FirebaseDatabase.getInstance().getReference(databaseMain);
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot data : snapshot.getChildren()) {

                            if (!Objects.equals(data.getKey(), "Userdata-1")){
                                DataSnapshot d = data.child("batchdata");
                                Homefiles dt = d.getValue(Homefiles.class);
                                if (dt != null) {
                                    databaseBatch.insert(dt);
                                    arrayList.add(dt);
                                }
                            }
                        }
                        Collections.reverse(arrayList);
                        System.out.println(arrayList);
                        runOnUiThread(progressDialog::dismiss);

                        homeFilesAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        runOnUiThread(progressDialog::dismiss);
                        Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void itemLongPress(int position) {
        Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.batchclickoption);
        dialog1.show();
        Button renameBatch = dialog1.findViewById(R.id.renamebatch);
        Button bt2 = dialog1.findViewById(R.id.deletebatch);
        renameBatch.setOnClickListener(view -> {
            Dialog dlog = new Dialog(Home.this);
            dlog.setContentView(R.layout.rename_batch);
            dlog.show();
            EditText name, year;
            name = dlog.findViewById(R.id.namerename);
            year = dlog.findViewById(R.id.yearrename);
            Button bt11, bt21;
            bt11 = dlog.findViewById(R.id.okrename);
            bt21 = dlog.findViewById(R.id.cancelraname);
            bt11.setOnClickListener(view1 -> {
                String n1 = name.getText().toString();
                String yr = year.getText().toString();
                Homefiles hf = new Homefiles(n1, yr);
                DatabaseReference renamedBatch=FirebaseDatabase.getInstance().getReference(databaseMain);

                Boolean res = databaseBatch.update(arrayList.get(position), hf);
                if (res) {
                    try {
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference(databaseMain).child(arrayList.get(position).getTable_Name());

                       ref.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               for(DataSnapshot data:snapshot.getChildren()){

                                   if(Objects.equals(data.getKey(), "Userdata-1")){
                                       DateAndTime dateAndTime=data.child("dateAndTime").getValue(DateAndTime.class);
                                       String username=data.child("name").getValue(String.class);
                                       String userEmail=data.child("email").getValue(String.class);
                                        renamedBatch.child(hf.getTable_Name()).child("Userdata-1").child("name").setValue(username);
                                       renamedBatch.child(hf.getTable_Name()).child("Userdata-1").child("email").setValue(userEmail);
                                       renamedBatch.child(hf.getTable_Name()).child("Userdata-1").child("dateAndTime").setValue(dateAndTime);

                                   }
                               }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });
                        StudentsDatabase db=new StudentsDatabase(this);
                        db.renameTable( arrayList.get(position).getTable_Name(),n1);
                    }catch (Exception ignored){


                    }

                    arrayList.get(position).setYear(yr);
                    arrayList.get(position).setBatch(n1);


                } else {
                    Toast.makeText(Home.this, "Reanme unsuccesfull", Toast.LENGTH_SHORT).show();
                }

                homeFilesAdapter.notifyDataSetChanged();
                dlog.dismiss();
                dialog1.dismiss();
                Toast.makeText(Home.this, "Batch renamed to " + n1, Toast.LENGTH_SHORT).show();
            });
            bt21.setOnClickListener(view12 -> {

                dlog.dismiss();
                dialog1.dismiss();
                Toast.makeText(Home.this, "renamed cancelled", Toast.LENGTH_SHORT).show();
            });

        });
        bt2.setOnClickListener(view -> {
            AlertDialog.Builder deletealert = new AlertDialog.Builder(new ContextThemeWrapper(Home.this, R.style.CustomAlertDialogTheme));

                deletealert.setTitle("Delete Batch").setMessage("Do you want to delete this Batch").setCancelable(true)
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            try {
                            String temp = arrayList.get(position).getBatch();
                            String tableName = arrayList.get(position).getTable_Name();
                            Boolean res = databaseBatch.delete(arrayList.get(position));
                            StudentsDatabase db = new StudentsDatabase(Home.this);
                            db.dropTable(tableName);

                            firebase = FirebaseDatabase.getInstance().getReference(databaseMain);
                            firebase.child(tableName).removeValue();

                            if (res) {
                                arrayList.remove(position);
                                homeFilesAdapter.notifyDataSetChanged();
                                Toast.makeText(Home.this, temp + " successfully removed", Toast.LENGTH_SHORT).show();
                            }
                            }catch (Exception ignored){

                            }
                            dialog1.cancel();
                        }).setNegativeButton("No", (dialogInterface, i) -> {
                            dialogInterface.cancel();
                            dialog1.cancel();
                        }).show();



        });
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onDestroy() {
        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            for (Homefiles data : arrayList) {
                firebase = FirebaseDatabase.getInstance().getReference(databaseMain);
                firebase.child(data.getTable_Name()).child("batchdata").setValue(data);
            }
            runOnUiThread(() -> {
                // Update UI here
            });
        });
        databaseBatch.close();
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    public void MenuBarClick(View v) {
        LottieAnimationView animationView;
        animationView = findViewById(R.id.animation_view);
        animationView.setAnimation(R.raw.cross_nav);
        animationView.playAnimation();
        new Handler().postDelayed(() -> {
            animationView.clearAnimation();
            animationView.cancelAnimation();
            animationView.setProgress(0f);

        }, 1000);

        navManu.show();
        ImageView close = navManu.findViewById(R.id.animation_close);
        ImageView notificationHighlight = navManu.findViewById(R.id.notificationHighlight);
        notificationHighlight.setVisibility(View.INVISIBLE);
        if(resultNotification[0]>0){
            notificationHighlight.setVisibility(View.VISIBLE);

        }


        Animation ballsquash = AnimationUtils.loadAnimation(this, R.anim.bouns_squash);
        close.startAnimation(ballsquash);
        close.setOnClickListener(view -> {
            navManu.dismiss();
            close.startAnimation(ballsquash);
            new Handler().postDelayed(animationView::playAnimation, 500);

        });
        navManu.setOnCancelListener(dialogInterface -> {
            navManu.dismiss();
            new Handler().postDelayed(animationView::playAnimation, 500);
        });
        TextView settings, help, logout, nav_name, nav_email;

        LinearLayout notification=navManu.findViewById(R.id.nav_messages);
        settings = navManu.findViewById(R.id.nav_settings);
        help = navManu.findViewById(R.id.nav_help);
        logout = navManu.findViewById(R.id.nav_logout);
        nav_name = navManu.findViewById(R.id.nav_name);
        nav_email = navManu.findViewById(R.id.nav_email);
        SharedPreferences preferences = getSharedPreferences(APP_LOGIN, MODE_PRIVATE);
        String name = preferences.getString("login_name", "");
        String email = preferences.getString("login_email", "");
        nav_name.setText("@/:" + name);
        nav_email.setText(email);

        settings.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, MenuSettings.class);
            startActivity(intent);
        });
        help.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, HelpMenuOptionActivity.class);
            startActivity(intent);
        });

        notification.setOnClickListener(view -> {
            Intent intent=new Intent(Home.this,Notifications.class);
            intent.putExtra("databaseMain",databaseMain);

            startActivity(intent);

        });

        logout.setOnClickListener(view -> {
            AlertDialog.Builder logout1 = new AlertDialog.Builder(new ContextThemeWrapper(Home.this, R.style.CustomAlertDialogTheme));
            logout1.setTitle("Log out").setMessage("Do you want to log out").setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("yes", (dialogInterface, i) -> {
                SharedPreferences pre = getSharedPreferences(APP_LOGIN, MODE_PRIVATE);
                SharedPreferences.Editor editor = pre.edit();
                editor.putBoolean("isaccessed", false);
                editor.apply();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(Home.this, LoginActivity.class);
                startActivity(intent);
                finish();
                dialogInterface.dismiss();
                Toast.makeText(Home.this, "your loged out", Toast.LENGTH_SHORT).show();
            }).show();

        });

    }


}