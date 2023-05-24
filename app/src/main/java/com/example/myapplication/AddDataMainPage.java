package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AddDataMainPage extends AppCompatActivity implements  StudentResponseInterface{
  TextView ed1,ed2,numberOfStudentsTextView;
   ArrayList<StudentData> selected;
   EditText name,deptmnt,age,stdnt_contctno,gender,parentName,parentMobile,address,aadhar;
   TextView create,cancel,title;
  ImageView back,selectdelete;
  FloatingActionButton send,edit,addStudent;
  RecyclerView recyclerView;

  StudentDataAdapter adapter;
  ArrayList<StudentData> arrayList;
    StudentsDatabase db;

  String dataBaseName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data_main_page);
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        numberOfStudentsTextView=findViewById(R.id.numberofstudents);
        selected=new ArrayList<>();
        db=new StudentsDatabase(this);

        edit=findViewById(R.id.floatingActionButtonedit);
        addStudent=findViewById(R.id.floatingActionButtonadd);
        send=findViewById(R.id.floatingActionButtonsend);
        recyclerView=findViewById(R.id.recyclerView);
        selectdelete=findViewById(R.id.deleteSelect);
        arrayList=new ArrayList<StudentData>();
  ed1=findViewById(R.id.batch_top);
  ed2=findViewById(R.id.year_top);
  back=findViewById(R.id.back);
        Intent intent=getIntent();
        String bt=intent.getStringExtra("batch_name");
        String yr=intent.getStringExtra("batchYear");
        dataBaseName=intent.getStringExtra("TABLE_NAME");
        db.createtabel(dataBaseName);

        setMultiDeletesetup();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new StudentDataAdapter(AddDataMainPage.this,arrayList,AddDataMainPage.this);
        recyclerView.setAdapter(adapter);
  ed1.setText(bt);
  ed2.setText(yr);

  addStudent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          Dialog  dialog=new Dialog(AddDataMainPage.this);
          dialog.setContentView(R.layout.addstudentdialogbox);
          dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
          dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          try {
              dialog.show();
          title=dialog.findViewById(R.id.title);
          name=dialog.findViewById(R.id.student_name);
          deptmnt=dialog.findViewById(R.id.department);
          age=dialog.findViewById(R.id.age);
          stdnt_contctno=dialog.findViewById(R.id.student_mobil);
          gender=dialog.findViewById(R.id.student_gender);
          parentMobile=dialog.findViewById(R.id.parent_number);
          parentName=dialog.findViewById(R.id.parent_name);
          address=dialog.findViewById(R.id.address);
          aadhar=dialog.findViewById(R.id.Aadhar);
              create=dialog.findViewById(R.id.create1);
              cancel=dialog.findViewById(R.id.cancel1);
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int age2 = age.getText().toString().equals("") ? 1 : Integer.parseInt(age.getText().toString());
                    char gder='M';
                    if(gender.getText().toString().length()>=1){
                        gder = Character.toUpperCase(gender.getText().toString().charAt(0));
                    }
                    if(!valiGender(gder)){
                        gender.setError("m/f");
                        gender.requestFocus();
                    }else
                    if(!validAge(age2)){
                        age.setError("invalid");
                        age.requestFocus();
                    }else
                    if (name.getText().toString().equals("")) {
                       name.setError("blank");
                       name.requestFocus();
                    } else {
                        StudentData data = new StudentData(name.getText().toString(), gender.getText().toString()
                                , age2, deptmnt.getText().toString(),
                                stdnt_contctno.getText().toString(), parentName.getText().toString()
                                , parentMobile.getText().toString()
                                , address.getText().toString(),
                                0, aadhar.getText().toString());

                        Boolean res = db.insert(dataBaseName, data);
                        if (res) {
                         /*   System.out.println(name.getText().toString()+" "+gender.getText().toString()
                                    +" "+stdnt_contctno.getText().toString()+" "+parentMobile.getText().toString()+" "+parentName.getText().toString()
                                    +" "+address.getText().toString()+" "+age2 ); */
                            arrayList.add(data);
                            intialise();
                            dialog.dismiss();
                        }
                        dialog.dismiss();
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

          }catch (Exception e){
    e.printStackTrace();
          }


      }
  });

  send.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          Toast.makeText(AddDataMainPage.this, "send", Toast.LENGTH_SHORT).show();


      }
  });
  edit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          Toast.makeText(AddDataMainPage.this, "edit", Toast.LENGTH_SHORT).show();
      }
  });
  intialise();
  back.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          finish();
      }
  });
    }

    @Override
    public void onTouchDelete(int pos) {
        AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.CustomAlertDialogTheme));

        builder.setTitle("Delete student").setMessage("Do you want to delete?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Boolean res=db.delete(dataBaseName,Integer.toString(arrayList.get(pos).getId()));
                if(res){
                    String tmp=arrayList.get(pos).getStdnt_name();
                    arrayList.remove(pos);
                    intialise();
                    Toast.makeText(AddDataMainPage.this, tmp+" deleted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddDataMainPage.this, "item not deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @Override
    public void onTouchEdit(int pos) {
        Dialog  dialog=new Dialog(AddDataMainPage.this);
        dialog.setContentView(R.layout.addstudentdialogbox);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        title=dialog.findViewById(R.id.title);
        title.setText("Student details update");
        name=dialog.findViewById(R.id.student_name);
        deptmnt=dialog.findViewById(R.id.department);
        age=dialog.findViewById(R.id.age);
        stdnt_contctno=dialog.findViewById(R.id.student_mobil);
        gender=dialog.findViewById(R.id.student_gender);
        parentMobile=dialog.findViewById(R.id.parent_number);
        parentName=dialog.findViewById(R.id.parent_name);
        address=dialog.findViewById(R.id.address);
        aadhar=dialog.findViewById(R.id.Aadhar);
        create=dialog.findViewById(R.id.create1);
        cancel=dialog.findViewById(R.id.cancel1);
        name.setText(arrayList.get(pos).getStdnt_name());
       deptmnt.setText(arrayList.get(pos).getStdnt_dprtmnt());
        age.setText(Integer.toString(arrayList.get(pos).getStdnt_age()));
        gender.setText(arrayList.get(pos).getStd_gender());
        stdnt_contctno.setText(arrayList.get(pos).getStdnt_mobileNo());
        parentName.setText(arrayList.get(pos).getParent_name());
        parentMobile.setText(arrayList.get(pos).getParent_no());
        address.setText(arrayList.get(pos).getAddress());
        aadhar.setText(arrayList.get(pos).getAadhar_no());
        create.setText("UPDATE");

       // gender.setText(arrayList.get(pos).getStd_gender());

        try {
            create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int age2 = age.getText().toString() == "" ? null: arrayList.get(pos).getStdnt_age();
                int id = arrayList.get(pos).getId();
               char gder=' ';
               if(gender.getText().toString().length()>=1)
                gder= Character.toUpperCase(gender.getText().toString().charAt(0));

                if(!validAge(age2)||!valiGender(gder)){
                    Toast.makeText(AddDataMainPage.this, "Enter valid data", Toast.LENGTH_SHORT).show();
                } else  {
                    StudentData data = new StudentData(id, name.getText().toString(), gender.getText().toString(), age2
                            , deptmnt.getText().toString()
                            , stdnt_contctno.getText().toString()
                            , parentName.getText().toString(), parentMobile.getText().toString()
                            , address.getText().toString(),
                            0, aadhar.getText().toString());

                    Boolean res = db.update(dataBaseName, data);
                    if (res) {
                        dialog.dismiss();
                        intialise();
                        Toast.makeText(AddDataMainPage.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(AddDataMainPage.this, "update cancelled", Toast.LENGTH_SHORT).show();
            }
        });
 dialog.show();

        }catch (Exception e){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onLongpress(int pos,ArrayList<StudentData> studentData) {
     if(studentData.size()>=1){
         selectdelete.setVisibility(View.VISIBLE);
     }
        selected=studentData;
    }
    @Override
    public void onClick(int pos, ArrayList<StudentData> studentData) {
        selected=studentData;
       if(studentData.size()==0)
           selectdelete.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("ResourceAsColor")

    private void intialise(){

        try {
            selectdelete.setVisibility(View.INVISIBLE);
            selected.clear();
            arrayList.clear();
            try{
                Cursor cursor=db.fetch(dataBaseName);
                if(cursor!=null&&cursor.moveToNext()){
                    do{
                        StudentData data=new StudentData(cursor.getInt(0),
                                cursor.getString(1),cursor.getString(2)
                                ,cursor.getInt(3),cursor.getString(4)
                                ,cursor.getString(5)
                                ,cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getInt(9),cursor.getString(10));
                        arrayList.add(data);
                    }while (cursor.moveToNext());

                }
            }catch (Exception t){}
    if(arrayList.size()==0){
        SharedPreferences pref=getSharedPreferences("applogin",MODE_PRIVATE);
        String databaseMain=pref.getString(SignUpActivity.MAIN_DATABASE_NAME,"default");
        DatabaseReference firebase=FirebaseDatabase.getInstance().getReference(databaseMain).child(dataBaseName).child("studentdata");

        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        boolean isInternetConnected=NetworkUtils.isInternetIsConnected(this);
        if(isInternetConnected){
            progressDialog.show();
        }

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                  progressDialog.dismiss();
                Toast.makeText(AddDataMainPage.this, snapshot.getChildrenCount()+"", Toast.LENGTH_SHORT).show();
                for(DataSnapshot data:snapshot.getChildren()){
                    StudentData dt=data.getValue(StudentData.class);
                    if(dt!=null){
                        Toast.makeText(AddDataMainPage.this, dt.getStdnt_name(), Toast.LENGTH_SHORT).show();
                        arrayList.add(dt);
                        db.insert(dataBaseName,dt);
                    }
                }
                adapter=new StudentDataAdapter(AddDataMainPage.this,arrayList,AddDataMainPage.this);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }

        });


    }
          //  Toast.makeText(this, arrayList.size()+"", Toast.LENGTH_SHORT).show();
            adapter=new StudentDataAdapter(AddDataMainPage.this,arrayList,this);
            recyclerView.setAdapter(adapter);
        numberOfStudentsTextView.setText("No of students : "+Integer.toString(arrayList.size()));

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }
    public static boolean validAge(int age){
        return age>=1&&age<100;
    }
    private static  boolean valiGender(char m){
        return m=='f'||m=='F'||m=='M'||m=='m'||m==' ';
    }

    void setMultiDeletesetup(){
        selectdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(AddDataMainPage.this,R.style.CustomAlertDialogTheme));
                builder.setTitle("Delete items").setMessage("Do you want to delete records ("+selected.size()+")")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int count=0;
                                for(int j=0;j<selected.size();j++){
                                    boolean res=db.delete(dataBaseName,Integer.toString(selected.get(j).getId()));
                                    System.out.println(selected.get(j));
                                    if(res){
                                        count++;
                                    }

                                }
                                Toast.makeText(AddDataMainPage.this, ""+count+" items deleted", Toast.LENGTH_SHORT).show();

                                intialise();
                                selectdelete.setVisibility(View.INVISIBLE);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();


            }
        });
    }

    public void uploadToFireBase(View v){


    }

    public void downloadFromFireBase(View v){

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences pref=getSharedPreferences("applogin",MODE_PRIVATE);
        String databaseMain=pref.getString(SignUpActivity.MAIN_DATABASE_NAME,"default");
        DatabaseReference firebase=FirebaseDatabase.getInstance().getReference(databaseMain).child(dataBaseName);
        for(int i=0;i<arrayList.size();i++){
            firebase.child("studentdata").child(arrayList.get(i).getId()+"").setValue(arrayList.get(i));
        }
    }
}