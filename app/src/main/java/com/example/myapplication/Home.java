package com.example.myapplication;

import static com.example.myapplication.SignUpActivity.APP_LOGIN;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;



public class Home extends AppCompatActivity implements RecyclerBatchInterface{

    Dialog dialog;
    DatabaseBatch databaseBatch;
    RecyclerView recyclerView;
    ArrayList<Homefiles> arrayList;
    FirebaseAuth fireAuth;
    DatabaseReference firebase;
    FirebaseUser user;

    HomeFilesAdapter homeFilesAdapter;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        Intent whts = new Intent(Intent.ACTION_VIEW);
//        whts.setPackage("com.whatsapp");
//        whts.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        whts.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + "7994497571" + "&text=" + Uri.encode("hello world")));
//        getApplicationContext().startActivity(whts);

        SharedPreferences preferences=getSharedPreferences(APP_LOGIN,MODE_PRIVATE);
        fireAuth=FirebaseAuth.getInstance();
        user=fireAuth.getCurrentUser();
        String Main_database_name="";
        if(user!=null){
            Main_database_name=preferences.getString(SignUpActivity.MAIN_DATABASE_NAME,"default");
        }
        /*/
        declare the  database such when a new user or already existing user login
        it's check if the persons database exists if not exists it's create a new main database
        that hold the name,and email of the user
        */

        firebaseIntailization();

        //

        String db=preferences.getString(SignUpActivity .MAIN_DATABASE_NAME,"batch.db");
        String name=preferences.getString("login_name","");
        String password=preferences.getString("login_password","");
        SharedPreferences pre=getSharedPreferences(APP_LOGIN,MODE_PRIVATE);
        if(!pre.getBoolean("isaccessed",true)){
            Intent acc=new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            acc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(acc);
            SharedPreferences.Editor editor=pre.edit();
            editor.putBoolean("isaccessed",true);
            editor.commit();
        }

        recyclerView=findViewById(R.id.batch_recycler);
        arrayList=new ArrayList<>();
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.add_newbatch_diolgue);
         databaseBatch=new DatabaseBatch(this,Main_database_name);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        homeFilesAdapter=new HomeFilesAdapter(Home.this,arrayList, this);
        recyclerView.setAdapter(homeFilesAdapter);
       // fetch the database and load the data of batches into the main page..
        fetch();
       //..
    }

    private void firebaseIntailization() {
        try {

       SharedPreferences pref=getSharedPreferences("applogin",MODE_PRIVATE);
        String databaseMain=pref.getString(SignUpActivity.MAIN_DATABASE_NAME,"default");
       FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            firebase=FirebaseDatabase.getInstance().getReference();
       String name=pref.getString("login_name","unotherized");
            Toast.makeText(this, databaseMain, Toast.LENGTH_SHORT).show();

           firebase.child(databaseMain).child("Userdata-1").child("email").setValue(pref.getString("login_email",""));
          firebase.child(databaseMain).child("Userdata-1").child("name").setValue(pref.getString("login_name",""));



        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public void addBatch(View view){
        try {
        EditText btcName,btcYr;
        Button btn1,btn2;
        btcName=dialog.findViewById(R.id.batchname);
        btcYr=dialog.findViewById(R.id.batchyear);
        btn1=dialog.findViewById(R.id.cancel);
        btn2=dialog.findViewById(R.id.create);
        btcName.setText("");
        btcYr.setText("");
        dialog.show();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                try {
                    String name=btcName.getText().toString();
                    String year=btcYr.getText().toString();
                    if(name.equals("")||!Character.isAlphabetic(name.charAt(0)) ) throw new Exception("error");
                    if(year.equals("")) year="unknown";
                    for(char c:name.toCharArray()){
                     if(!Character.isAlphabetic(c)&&!Character.isDigit(c)){
                         Toast.makeText(Home.this, "Use only alphabets and digits", Toast.LENGTH_SHORT).show();
                         throw new Exception();
                     }
                    }
                    Homefiles homefiles=new Homefiles(name,year);
                    //inserting the batch details into the database
                    Boolean res=databaseBatch.insert(homefiles);
                    SharedPreferences pref=getSharedPreferences("applogin",MODE_PRIVATE);
                    String databaseMain=pref.getString(SignUpActivity.MAIN_DATABASE_NAME,"default");
                    firebase=FirebaseDatabase.getInstance().getReference(databaseMain);
                    firebase.child(homefiles.getTable_Name()).child("batchdata").setValue(homefiles);
                   if(res){
                       arrayList.add(homefiles);

                       Toast.makeText(Home.this, "Batch has been created successfully", Toast.LENGTH_SHORT).show();

                       recyclerView.setAdapter(homeFilesAdapter);
                   }else{
                       Toast.makeText(Home.this, "Batch already exists", Toast.LENGTH_SHORT).show();
                   }

                    dialog.cancel();

                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(Home.this, "Error creating batch", Toast.LENGTH_SHORT).show();
                }

            }
        });
        }catch(Exception e){}
    }
    @Override
    public void itemOnClick(int position) {
      Intent intent=new Intent(this,AddDataMainPage.class);
        String batchname=arrayList.get(position).getBatch();
        String yearof=arrayList.get(position).getYear();
        intent.putExtra("batch_name",batchname);
        intent.putExtra("batchYear",yearof);
        intent.putExtra("TABLE_NAME",arrayList.get(position).getTable_Name());
        startActivity(intent);
    }
    private void fetch(){
        //fetches from either internal database or firebase database
        try{

        Cursor cursor=databaseBatch.fetch();
        if(cursor!=null && cursor.moveToFirst()){
            int f1=cursor.getColumnIndex(DatabaseBatch.BATCH_NAME);
            int f2=cursor.getColumnIndex(DatabaseBatch.BATCH_YEAR);
            do{
              arrayList.add(new Homefiles(cursor.getString(f1),cursor.getString(f2)));
            }while (cursor.moveToNext());

            recyclerView.setAdapter(homeFilesAdapter);

        }
            if(arrayList.size()==0){
                ProgressDialog progressDialog=new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.show();
                SharedPreferences pref=getSharedPreferences("applogin",MODE_PRIVATE);
                String databaseMain=pref.getString(SignUpActivity.MAIN_DATABASE_NAME,"default");
                firebase=FirebaseDatabase.getInstance().getReference(databaseMain);
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot data :snapshot.getChildren()){

                            if(data.getKey()=="Userdata-1"){

                            }else{
                                DataSnapshot d=data.child("batchdata");
                                Homefiles dt=(Homefiles) d.getValue(Homefiles.class);
                                if(dt!=null){
                                    Boolean res=databaseBatch.insert(dt);
                                    arrayList.add(dt);
                                }



                            }

                        }
                        progressDialog.dismiss();
                        recyclerView.setAdapter(homeFilesAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void itemLongPress(int position) {
        Dialog dialog1=new Dialog(this);
        dialog1.setContentView(R.layout.batchclickoption);
        dialog1.show();
        Button bt1=dialog1.findViewById(R.id.renamebatch);
        Button bt2=dialog1.findViewById(R.id.deletebatch);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dlog =new Dialog(Home.this);
                dlog.setContentView(R.layout.rename_batch);
                dlog.show();
                EditText name, year;
                name = dlog.findViewById(R.id.namerename);
                year = dlog.findViewById(R.id.yearrename);
                Button bt1, bt2;
                bt1 = dlog.findViewById(R.id.okrename);
                bt2 = dlog.findViewById(R.id.cancelraname);
                bt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String n1=name.getText().toString();
                        String yr=year.getText().toString();
                        Homefiles hf=new Homefiles(n1,yr);
                        Boolean res=databaseBatch.update(arrayList.get(position),hf);
                        SharedPreferences pref=getSharedPreferences("applogin",MODE_PRIVATE);
                        String databaseMain=pref.getString(SignUpActivity.MAIN_DATABASE_NAME,"default");
                        DatabaseReference fr=FirebaseDatabase.getInstance().getReference(databaseMain);


                        if(res){
                            arrayList.get(position).setYear(yr);
                            arrayList.get(position).setBatch(n1);
                        }
                        else{
                            Toast.makeText(Home.this, "Reanme unsuccesfull", Toast.LENGTH_SHORT).show();
                        }

                        recyclerView.setAdapter(homeFilesAdapter);
                        dlog.dismiss();
                        dialog1.dismiss();
                       System.out.println(name.toString());
                       Toast.makeText(Home.this, "Batch renamed to "+n1, Toast.LENGTH_SHORT).show();
                    }
                });
                bt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dlog.dismiss();
                        dialog1.dismiss();
                        Toast.makeText(Home.this, "renamed cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder deletealert= new AlertDialog.Builder(new ContextThemeWrapper(Home.this,R.style.CustomAlertDialogTheme));
                deletealert.setTitle("Delete Batch").setMessage("Do you want to delete this Batch").setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String temp=arrayList.get(position).getBatch();
                                String tableName=arrayList.get(position).getTable_Name();
                                Boolean res=databaseBatch.delete(arrayList.get(position));
                                StudentsDatabase db=new StudentsDatabase(Home.this);
                               db.dropTable(tableName);
                                SharedPreferences pref=getSharedPreferences("applogin",MODE_PRIVATE);
                                String databaseMain=pref.getString(SignUpActivity.MAIN_DATABASE_NAME,"default");
                               DatabaseReference firebase=FirebaseDatabase.getInstance().getReference(databaseMain);
                                Toast.makeText(Home.this, databaseMain, Toast.LENGTH_SHORT).show();
                              boolean res2= firebase.child(tableName).removeValue().isSuccessful();
                               firebase=FirebaseDatabase.getInstance().getReference(databaseMain);
                               firebase.child(tableName).removeValue();
                                if(res){
                                    arrayList.remove(position);
                                    recyclerView.setAdapter(homeFilesAdapter);
                                    Toast.makeText(Home.this, temp+" successfully removed", Toast.LENGTH_SHORT).show();
                                }
                                dialog1.cancel();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dialog1.cancel();
                            }
                        }).show();

            }
        });
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onBackPressed() {
            super.onBackPressed();

        SharedPreferences pref=getSharedPreferences("applogin",MODE_PRIVATE);
        String databaseMain=pref.getString(SignUpActivity.MAIN_DATABASE_NAME,"default");

        for(Homefiles data:arrayList){
            firebase=FirebaseDatabase.getInstance().getReference(databaseMain);
            firebase.child(data.getTable_Name()).child("batchdata").setValue(data);
        }

    }

    Boolean click=true;

    public void MenuBarClick(View v){
        ImageView img=findViewById(R.id.main_menu);


        if(click){
            Animation animation= AnimationUtils.loadAnimation(this,R.anim.rotate_start_dase);
            animation.setFillAfter(true);
            img.startAnimation(animation);
            click=false;
        }else{
            Animation animation= AnimationUtils.loadAnimation(this,R.anim.rotate_end_dase);
            animation.setFillAfter(true);
            img.startAnimation(animation);
            click=true;
        }






        Toast.makeText(this, "menu", Toast.LENGTH_SHORT).show();
    }
}