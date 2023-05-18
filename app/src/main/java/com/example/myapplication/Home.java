package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements RecyclerBatchInterface{

    Dialog dialog,dialog_update;
    DatabaseBatch databaseBatch;
    RecyclerView recyclerView;
    ArrayList<Homefiles> arrayList;
    HomeFilesAdapter homeFilesAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_home);
        recyclerView=findViewById(R.id.batch_recycler);
        arrayList=new ArrayList<>();
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.add_newbatch_diolgue);
      databaseBatch=new DatabaseBatch(this);

      //  NavigationBarView nav=findViewById(R.id.nav);


        homeFilesAdapter=new HomeFilesAdapter(Home.this,arrayList, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(homeFilesAdapter);
       // fetch the database and load the data of batches into the main page..
        fetch();
       //..
    }
    public void addBatch(View view){
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
        Cursor cursor=databaseBatch.fetch();
        if(cursor!=null && cursor.moveToFirst()){
            int f1=cursor.getColumnIndex(DatabaseBatch.BATCH_NAME);
            int f2=cursor.getColumnIndex(DatabaseBatch.BATCH_YEAR);
            do{
              arrayList.add(new Homefiles(cursor.getString(f1),cursor.getString(f2)));
            }while (cursor.moveToNext());
            recyclerView.setAdapter(homeFilesAdapter);
        }
    }

    @Override
    public void itemLongPress(int position) {
//        arrayList.remove(position);
//        recyclerView.setAdapter(homeFilesAdapter);
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
                                String year=arrayList.get(position).getYear();
                                String tableName=arrayList.get(position).getTable_Name();
                                Boolean res=databaseBatch.delete(arrayList.get(position));
                                StudentsDatabase db=new StudentsDatabase(Home.this);

                               db.dropTable(tableName);
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


}