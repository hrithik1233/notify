package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.BoringLayout;

public class DatabaseBatch extends SQLiteOpenHelper {

    final static  public String BATCH_NAME="batchName";
    final static  public String BATCH_YEAR="batchYear";
    final static  public String TABLE_NAME="BatchTable";
    public DatabaseBatch(Context context,String db) {
        super(context,db+".db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    String tbQry="create table "+TABLE_NAME+"( "+BATCH_NAME
            +"   TEXT primary key unique, "+BATCH_YEAR+" TEXT );";
    db.execSQL(tbQry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
       db.execSQL("drop table if exists "+TABLE_NAME);
    }
    public Boolean insert(Homefiles hf){
        long res=-1;
        try{


        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(BATCH_NAME,hf.getBatch());
        cv.put(BATCH_YEAR,hf.getYear());
        Cursor cursor=database.query(TABLE_NAME,new String[]{BATCH_NAME},BATCH_NAME+"=? AND "+BATCH_YEAR +"=?",
                new String[]{hf.getBatch(), hf.getYear()},null,null,null);
        if(cursor.moveToNext()) return false;
        res=database.insert(TABLE_NAME,null,cv);

        }catch (Exception e){}
        return res!=-1;
    }


    public Cursor fetch(){
        SQLiteDatabase database=this.getReadableDatabase();
        return database.query(TABLE_NAME,new String[]{BATCH_NAME,BATCH_YEAR},null, null,null,null,null);
    }
    public  Boolean delete(Homefiles hf){
        long res = 0;
        SQLiteDatabase database=this.getWritableDatabase();
        try{
             res=database.delete(TABLE_NAME,BATCH_NAME+"=? AND "+BATCH_YEAR+"=?",new String[]{hf.getBatch(), hf.getYear()});

        }catch(Exception e){
    System.out.println(e);
        }

        return res!=-1;
    }
    public Boolean update(Homefiles hf,Homefiles hf2){
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(BATCH_NAME,hf2.getBatch());
        cv.put(BATCH_YEAR,hf2.getYear());
        long res=database.update(TABLE_NAME,cv,BATCH_NAME+"=? AND "+BATCH_YEAR+" =?"
                ,new String[]{hf.getBatch(),hf.getYear()});
        return res!=-1;
    }

}
