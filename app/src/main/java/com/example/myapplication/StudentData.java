package com.example.myapplication;

import android.widget.RelativeLayout;

public class StudentData {
    public static Boolean isSelectionMode=false;
    boolean isSelected=false;
    public  boolean getIsselected(){
        return isSelected;
    }
    public void setIsselected(boolean b){
        isSelected=b;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    int id;
    String stdnt_name;
    String std_gender;
    String std_register_no;
    String aadhar_no;

    @Override
    public String toString() {
        return "StudentData{" +
                "isSelected=" + isSelected +
                ", id=" + id +
                ", stdnt_name='" + stdnt_name + '\'' +
                ", std_gender='" + std_gender + '\'' +
                ", std_register_no='" + std_register_no + '\'' +
                ", aadhar_no='" + aadhar_no + '\'' +
                ", stdnt_age=" + stdnt_age +
                ", number_of_late_comes=" + number_of_late_comes +
                ", stdnt_dprtmnt='" + stdnt_dprtmnt + '\'' +
                ", stdnt_mobileNo='" + stdnt_mobileNo + '\'' +
                ", parent_name='" + parent_name + '\'' +
                ", parent_no='" + parent_no + '\'' +
                ", Address='" + Address + '\'' +
                '}';
    }

    public String getAadhar_no() {
        return aadhar_no;
    }

    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }

    public String getStd_gender() {
        return std_gender;
    }

    public void setStd_gender(String std_gender) {
        this.std_gender = std_gender;
    }

    public int getStdnt_age() {
        return stdnt_age;
    }

    public void setStdnt_age(int stdnt_age) {
        this.stdnt_age = stdnt_age;
    }

    int stdnt_age;
    int number_of_late_comes=0;
    String stdnt_dprtmnt;
    String stdnt_mobileNo;
    String parent_name;
    public void increment_late_comes(){
        number_of_late_comes++;
    }
    public int getNumber_of_late_comes() {
        return number_of_late_comes;
    }

    public void setNumber_of_late_comes(int number_of_late_comes) {
        this.number_of_late_comes = number_of_late_comes;
    }

    String parent_no;
    String Address;

    public StudentData(int id,String stdnt_name, String std_gender, int stdnt_age, String stdnt_dprtmnt,
                       String stdnt_mobileNo, String parent_name, String parent_no, String address,int number_of_late_comes,String adhar) {
        this.stdnt_name = stdnt_name;
        this.std_gender = std_gender;
        this.stdnt_age = stdnt_age;
        this.stdnt_dprtmnt = stdnt_dprtmnt;
        this.stdnt_mobileNo = stdnt_mobileNo;
        this.parent_name = parent_name;
        this.parent_no = parent_no;
        Address = address;
     this.id=id;
        this.number_of_late_comes=number_of_late_comes;
        aadhar_no=adhar;

    }
    public StudentData(String stdnt_name, String std_gender, int stdnt_age, String stdnt_dprtmnt,
                       String stdnt_mobileNo, String parent_name, String parent_no, String address,int number_of_late_comes,String aadhar) {
        this.stdnt_name = stdnt_name;
        this.std_gender = std_gender;
        this.stdnt_age = stdnt_age;
        this.stdnt_dprtmnt = stdnt_dprtmnt;
        this.stdnt_mobileNo = stdnt_mobileNo;
        this.parent_name = parent_name;
        this.parent_no = parent_no;
        Address = address;
        aadhar_no=aadhar;
        this.number_of_late_comes=number_of_late_comes;
    }


    public String getStd_register_no() {
        return std_register_no;
    }

    public void setStd_register_no(String std_register_no) {
        this.std_register_no = std_register_no;
    }

    public String getStdnt_name() {
        return stdnt_name;
    }

    public void setStdnt_name(String stdnt_name) {
        this.stdnt_name = stdnt_name;
    }

    public String getStdnt_dprtmnt() {
        return stdnt_dprtmnt;
    }

    public void setStdnt_dprtmnt(String stdnt_dprtmnt) {
        this.stdnt_dprtmnt = stdnt_dprtmnt;
    }

    public String getStdnt_mobileNo() {
        return stdnt_mobileNo;
    }

    public void setStdnt_mobileNo(String stdnt_mobileNo) {
        this.stdnt_mobileNo = stdnt_mobileNo;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public String getParent_no() {
        return parent_no;
    }

    public void setParent_no(String parent_no) {
        this.parent_no = parent_no;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
