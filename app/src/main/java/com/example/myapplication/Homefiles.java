package com.example.myapplication;

public class Homefiles {
    String batch;
    String year;
    String Table_Name="null";
    String NoOfStudents="null";

    public Homefiles(String batch, String year, String table_Name, String noOfBatches) {
        this.batch = batch;
        this.year = year;
        Table_Name = table_Name;
        NoOfStudents = noOfBatches;
    }

    public Homefiles(String batch, String year) {
        this.batch = batch;
        this.year = year;
         Table_Name=batch.toUpperCase();
    }
    public  Homefiles(){}

    public void setTable_Name(String table_Name) {
        Table_Name = table_Name;
    }

    public String getNoOfStudents() {
        return NoOfStudents;
    }

    public void setNoOfStudents(String noOfStudents) {
        NoOfStudents = noOfStudents;
    }

    public String getTable_Name(){return Table_Name;}

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
