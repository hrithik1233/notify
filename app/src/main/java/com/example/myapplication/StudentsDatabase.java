package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class StudentsDatabase extends SQLiteOpenHelper {
    Context context;
    public StudentsDatabase(@Nullable Context context) {
        super(context,"studentData.db", null, 1);
       this.context=context;
    }
    public static final String  STUDENT_ID="student_id";
    public static final String STUDENT_AGE = "student_age";
    public static  final   String STUDENT_NAME = "student_name";
    public static  final   String STUDENT_DEPARTMENT = "student_department";
    public static  final   String STUDENT_GENDER = "student_gender";
    public static  final   String STUDENT_MOBILE_NO = "student_mobile_no";
    public static  final   String STUDENT_REGISTER_NO = "student_register_no";
    public static  final   String STUDENT_ADDRESS = "student_address";
    public static  final   String STUDENT_PARENT_NAME = "student_parent_name";
    public static  final   String NO_OF_LATE_COMES = "student_late_comes";
    public static final  String STUDENT_AADHAR_NO="student_aadhar";
    public static  final   String STUDENT_PARENT_MOBILE_NO = "student_parent_mobile_no";
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public Boolean insert(String TABLE_NAME,StudentData sd){
        SQLiteDatabase db=this.getWritableDatabase();
        long res=-1;
        try {
      String table="create table if not exists "+TABLE_NAME+" ( "+STUDENT_ID+" integer primary key autoincrement,"
                +STUDENT_NAME+" TEXT not null,"+STUDENT_AGE+" INTEGER,"
                +STUDENT_DEPARTMENT+" text,"+STUDENT_GENDER+" TEXT,"
                +STUDENT_MOBILE_NO+" text,"+STUDENT_REGISTER_NO+" text,"
                +STUDENT_PARENT_NAME+" text,"+STUDENT_PARENT_MOBILE_NO
                +" text,"+STUDENT_ADDRESS+" text,"+NO_OF_LATE_COMES +" integer,"+STUDENT_AADHAR_NO+" text);";
        db.execSQL(table);
        ContentValues cv=new ContentValues();
        cv.put(STUDENT_NAME,sd.getStdnt_name());
        cv.put(STUDENT_AGE,sd.getStdnt_age());
        cv.put(STUDENT_DEPARTMENT,sd.getStdnt_dprtmnt());
        cv.put(STUDENT_ADDRESS,sd.getAddress());
        cv.put(STUDENT_GENDER,sd.getStd_gender());
        cv.put(STUDENT_PARENT_NAME,sd.getParent_name());
        cv.put(STUDENT_PARENT_MOBILE_NO,sd.getParent_no());
        cv.put(STUDENT_MOBILE_NO,sd.getStdnt_mobileNo());
        cv.put(STUDENT_REGISTER_NO,sd.getStd_register_no());
        cv.put(NO_OF_LATE_COMES,sd.getNumber_of_late_comes());
        cv.put(STUDENT_AADHAR_NO,sd.getAadhar_no());
        res=db.insert(TABLE_NAME,null,cv);
        }catch (Exception e){}

    return res!=-1;

    }
    public Cursor fetch(String TABLE_NAME){
   SQLiteDatabase db=this.getReadableDatabase();
   Cursor cursor=db.query(TABLE_NAME,new String[]{STUDENT_ID,STUDENT_NAME,STUDENT_GENDER,STUDENT_AGE,STUDENT_DEPARTMENT,
           STUDENT_MOBILE_NO,STUDENT_PARENT_NAME,STUDENT_PARENT_MOBILE_NO,STUDENT_ADDRESS,NO_OF_LATE_COMES,STUDENT_AADHAR_NO},
           null,null,null,null,NO_OF_LATE_COMES+" DESC");

   return cursor;
    }
  public Boolean update(String TABLE_NAME,StudentData sd){
      ContentValues cv=new ContentValues();
      cv.put(STUDENT_NAME,sd.getStdnt_name());
      cv.put(STUDENT_AGE,sd.getStdnt_age());
      cv.put(STUDENT_DEPARTMENT,sd.getStdnt_dprtmnt());
      cv.put(STUDENT_ADDRESS,sd.getAddress());
      cv.put(STUDENT_GENDER,sd.getStd_gender());
      cv.put(STUDENT_PARENT_NAME,sd.getParent_name());
      cv.put(STUDENT_PARENT_MOBILE_NO,sd.getParent_no());
      cv.put(STUDENT_MOBILE_NO,sd.getStdnt_mobileNo());
      cv.put(STUDENT_REGISTER_NO,sd.getStd_register_no());
      cv.put(NO_OF_LATE_COMES,sd.getNumber_of_late_comes());
      cv.put(STUDENT_AADHAR_NO,sd.getAadhar_no());
      SQLiteDatabase db=this.getWritableDatabase();
      long res=db.update(TABLE_NAME,cv,STUDENT_ID+"=?",new String[]{Integer.toString(sd.getId())});
      db.close();
      return res!=-1;
  }
  public Boolean delete(String TABLE_NAME,String id){
      SQLiteDatabase db=this.getWritableDatabase();
     long res= db.delete(TABLE_NAME,STUDENT_ID+"=?",new String[]{id});
      db.close();
      return res!=-1;
  }
  public void dropTable(String table){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("drop table if exists "+table);
  }
}
