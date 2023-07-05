package com.example.myapplication;

public class Homefiles {
    String batch;
    String year;
    String Table_Name="";
    String NoOfBatches="";

    public Homefiles(String batch, String year, String table_Name, String noOfBatches) {
        this.batch = batch;
        this.year = year;
        Table_Name = table_Name;
        NoOfBatches = noOfBatches;
    }

    public Homefiles(String batch, String year) {
        this.batch = batch;
        this.year = year;
        setTable_Name();
    }
    public  Homefiles(){}
  public String getTable_Name(){return Table_Name;}
    public void setTable_Name(){
      for(char c:batch.toCharArray()){
          Table_Name+=Character.toUpperCase(c);
      }
    }
    public String getBatch() {
        return batch;
    }

    public String getYear() {
        return year;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
