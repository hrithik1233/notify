package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class AddDataMainPage extends AppCompatActivity implements StudentResponseInterface {
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;
    TextView ed1, ed2, numberOfStudentsTextView, no_ofselected;
    String Main_message;
    ArrayList<StudentData> selected;

    ArrayList<StudentData> temp;
    EditText name, deptmnt, age, stdnt_contctno, gender, parentName, parentMobile, address, aadhar;
    TextView create, cancel, title;
    ImageView back, selectdelete;
    FloatingActionButton send, edit, addStudent;
    RecyclerView recyclerView;

    StudentDataAdapter adapter;
    ArrayList<StudentData> arrayList;
    StudentsDatabase db;
    ProgressDialog progressDialog;
    String dataBaseName;
    EditText search;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data_main_page);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(AddDataMainPage.this, FloatingLayoutService.class));
                } else {
                    startService(new Intent(AddDataMainPage.this, FloatingLayoutService.class));
                }
            } else {
                Toast.makeText(this, "Please Grant Overlay Permission", Toast.LENGTH_SHORT).show();
            }
        });

        search=findViewById(R.id.student_search);
       search.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void afterTextChanged(Editable editable) {
              filter(editable.toString());
           }
       });

        numberOfStudentsTextView = findViewById(R.id.numberofstudents);
        selected = new ArrayList<>();
        temp = new ArrayList<>();
        db = new StudentsDatabase(this);
        no_ofselected = findViewById(R.id.numberofselectedstd);
        edit = findViewById(R.id.floatingActionButtonedit);
        addStudent = findViewById(R.id.floatingActionButtonadd);
        send = findViewById(R.id.floatingActionButtonsend);
        recyclerView = findViewById(R.id.recyclerView);
        selectdelete = findViewById(R.id.deleteSelect);
        arrayList = new ArrayList<StudentData>();
        ed1 = findViewById(R.id.batch_top);
        ed2 = findViewById(R.id.year_top);
        back = findViewById(R.id.back);
        Intent intent = getIntent();
        String bt = intent.getStringExtra("batch_name");
        String yr = intent.getStringExtra("batchYear");
        dataBaseName = intent.getStringExtra("TABLE_NAME");
        db.createtabel(dataBaseName);
        setMultiDeletesetup();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new StudentDataAdapter(AddDataMainPage.this, arrayList, AddDataMainPage.this);
        recyclerView.setAdapter(adapter);
        ed1.setText(bt);
        ed2.setText(yr);

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(AddDataMainPage.this);
                dialog.setContentView(R.layout.addstudentdialogbox);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                try {
                    dialog.show();
                    title = dialog.findViewById(R.id.title);
                    name = dialog.findViewById(R.id.student_name);
                    deptmnt = dialog.findViewById(R.id.department);
                    age = dialog.findViewById(R.id.age);
                    stdnt_contctno = dialog.findViewById(R.id.student_mobil);
                    gender = dialog.findViewById(R.id.student_gender);
                    parentMobile = dialog.findViewById(R.id.parent_number);
                    parentName = dialog.findViewById(R.id.parent_name);
                    address = dialog.findViewById(R.id.address);
                    aadhar = dialog.findViewById(R.id.Aadhar);
                    create = dialog.findViewById(R.id.create1);
                    cancel = dialog.findViewById(R.id.cancel1);
                    create.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int age2 = age.getText().toString().equals("") ? 1 : Integer.parseInt(age.getText().toString());
                            char gder = 'M';
                            if (gender.getText().toString().length() >= 1) {
                                gder = Character.toUpperCase(gender.getText().toString().charAt(0));
                            }
                            if (!valiGender(gder)) {
                                gender.setError("m/f");
                                gender.requestFocus();
                            } else if (!validAge(age2)) {
                                age.setError("invalid");
                                age.requestFocus();
                            } else if (name.getText().toString().equals("")) {
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

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        //****************************************************************
        // send button logic
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog sendBottom = new Dialog(AddDataMainPage.this);
                sendBottom.setContentView(R.layout.send_mess_parent_dialog);
                sendBottom.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                sendBottom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                sendBottom.getWindow().setGravity(Gravity.BOTTOM);
                sendBottom.getWindow().getAttributes().windowAnimations = (R.style.SlideDialogAnimationBottomUp);
                Toast.makeText(AddDataMainPage.this, "send", Toast.LENGTH_SHORT).show();
                Button cancel, sendMes;
                cancel = sendBottom.findViewById(R.id.cancel_message_btn);
                sendMes = sendBottom.findViewById(R.id.send_message_btn);
                ChipGroup chipGroup = sendBottom.findViewById(R.id.chipgroup);
                CheckBox isWhts, issms, isBothMedium, isparent, isstudent, isBothPar_and_stud;
                // checkbox initialization
                isWhts = sendBottom.findViewById(R.id.whtsappSelection);
                issms = sendBottom.findViewById(R.id.smsSelection);
                isBothMedium = sendBottom.findViewById(R.id.bothwhtsAndSMSSelection);
                isstudent = sendBottom.findViewById(R.id.isStudentSelected);
                isparent = sendBottom.findViewById(R.id.isParentSelected);
                isBothPar_and_stud = sendBottom.findViewById(R.id.isBothParentAndStudentSelected);


                // checkBox configuration logic

                isBothMedium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isWhts.setChecked(false);
                        issms.setChecked(false);
                    }
                });

                isBothPar_and_stud.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isparent.setChecked(false);
                        isstudent.setChecked(false);
                    }
                });

                // *********************************************************

                //********************************************************

                // adding all the selected list into a temp array
                for (int i = 0; i < selected.size(); i++) {
                    temp.add(selected.get(i));
                }
                //**********************************************
                //**************************************************
                // setting chips
                if (temp.size() == 0) {
                    Chip chip = new Chip(AddDataMainPage.this);
                    chip.setBackgroundColor(Color.BLUE);
                    chip.setText("No selection is added");
                    chipGroup.addView(chip);
                } else {
                    for (int i = 0; i < temp.size(); i++) {
                        Chip chip = new Chip(AddDataMainPage.this);
                        int id = temp.get(i).getId();
                        String name = temp.get(i).getStdnt_name();
                        String text = id + ":" + " " + name;
                        chip.setText(text);
                        chip.setBackgroundColor(Color.BLUE);
                        chip.setCloseIconVisible(true);
                        chip.setCloseIcon(getDrawable(R.drawable.cancel_24));
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Chip ch = (Chip) view;
                                String id = ch.getText().toString();
                                int m = 0;
                                String res = "";
                                while (id.charAt(m) != ':') {
                                    res += id.charAt(m);
                                    m++;
                                }
                                int res_num = Integer.parseInt(res);
                                for (int i = 0; i < temp.size(); i++) {
                                    if (temp.get(i).getId() == res_num) {
                                        temp.remove(temp.get(i));
                                    }
                                }
                                if (temp.size() == 0) {
                                    Chip chip = new Chip(AddDataMainPage.this);
                                    chip.setBackgroundColor(Color.BLUE);
                                    chip.setText("No selection is added");
                                    chipGroup.addView(chip);
                                }
                                Toast.makeText(AddDataMainPage.this, temp.size() + "", Toast.LENGTH_SHORT).show();
                                chipGroup.removeView(view);
                            }
                        });
                        chipGroup.addView(chip);
                    }

                }
                //***********************************************************

                //**********************************************************

                // when send dialog button pressed
                sendMes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean isWhatsapp, isSMS, isBothWhatsappAndSMS, isParent, isStudent, isBothParentAndStudent;
                        isWhatsapp = isWhts.isChecked();
                        isSMS = issms.isChecked();
                        isBothParentAndStudent = isBothPar_and_stud.isChecked();
                        isParent = isparent.isChecked();
                        isStudent = isstudent.isChecked();
                        isBothWhatsappAndSMS = isBothMedium.isChecked();
                        EditText edittext = sendBottom.findViewById(R.id.editTextTextMultiLine);
                        Main_message = edittext.getText().toString();
                        if (isBothWhatsappAndSMS) {
                            isWhatsapp = true;
                            isSMS = true;
                        }
                        //************************************************************
                        Dexter.withContext(AddDataMainPage.this)
                                .withPermissions(
                                        Manifest.permission.SEND_SMS,
                                        Manifest.permission.READ_CONTACTS

                                ).withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                                }).check();
                        //************************************************************

                        if (isWhatsapp) {
                            try {
                                List<String> t = new ArrayList<>();
                                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                                for (int i = 0; i < temp.size(); i++) {
                                    if (isParent) {
                                        String numberParent = temp.get(i).getParent_no();
                                        boolean isValidNumber = numberParent.length() >=10;
                                        if (isValidNumber) {
                                            t.add(numberParent);
                                            temp.get(i).increment_late_comes();
                                                 boolean res = db.update(dataBaseName,temp.get(i));
                                                 if(res)
                                                     Log.d("isupdated",res+"");


                                        }




                                    }
                                    if (isStudent) {
                                        String numberStudent = temp.get(i).getStdnt_mobileNo();

                                        boolean isValidNumber = numberStudent.length()>=10;
                                        if (isValidNumber) {

                                            t.add(numberStudent);
                                            if(!isParent){
                                                temp.get(i).increment_late_comes();
                                                boolean res = db.update(dataBaseName,temp.get(i));
                                                if(res)
                                                    Log.d("isupdated",res+"");
                                            }
                                        }
                                    }

                                }
                                Log.d("sendsize",t.size()+"");
                                if (t.size() >= 1) {
                                    String ar[] = (String[]) t.toArray(new String[0]);
                                    senWhtsappMessage(ar, Main_message);
                                } else {
                                    Toast.makeText(AddDataMainPage.this, "No number is selected", Toast.LENGTH_SHORT).show();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (isSMS) {
                            //**************************************************
                            //if Sms is selected

                        }


                    }

                });

                sendBottom.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        temp.clear();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendBottom.dismiss();
                        Toast.makeText(AddDataMainPage.this, "cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                sendBottom.show();

            }
        });

        //**************************************************************************************
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

    private void filter(String srchname) {
        ArrayList<StudentData> searchList =new ArrayList<>();
        for(StudentData data:arrayList){
            if(data.getStdnt_name().toLowerCase().contains(srchname.toLowerCase())){
                searchList.add(data);
            }
        }

        adapter.filter(searchList);

    }

    @Override
    public void onTouchDelete(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));

        builder.setTitle("Delete student").setMessage("Do you want to delete?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences pref = getSharedPreferences("applogin", MODE_PRIVATE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                String databaseMain = pref.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
                DatabaseReference reff = ref.child(databaseMain).child(dataBaseName)
                        .child("studentdata").child(Integer.toString(arrayList.get(pos).getId()));
                Toast.makeText(AddDataMainPage.this, Integer.toString(arrayList.get(pos).getId()), Toast.LENGTH_SHORT).show();
                boolean r = reff.removeValue().isSuccessful();

                Boolean res = db.delete(dataBaseName, Integer.toString(arrayList.get(pos).getId()));
                if (res) {

                    String tmp = arrayList.get(pos).getStdnt_name();
                    arrayList.remove(pos);
                    intialise();
                    Toast.makeText(AddDataMainPage.this, tmp + " deleted", Toast.LENGTH_SHORT).show();
                } else {
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
        Dialog dialog = new Dialog(AddDataMainPage.this);
        dialog.setContentView(R.layout.addstudentdialogbox);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        title = dialog.findViewById(R.id.title);
        title.setText("Student details update");
        name = dialog.findViewById(R.id.student_name);
        deptmnt = dialog.findViewById(R.id.department);
        age = dialog.findViewById(R.id.age);
        stdnt_contctno = dialog.findViewById(R.id.student_mobil);
        gender = dialog.findViewById(R.id.student_gender);
        parentMobile = dialog.findViewById(R.id.parent_number);
        parentName = dialog.findViewById(R.id.parent_name);
        address = dialog.findViewById(R.id.address);
        aadhar = dialog.findViewById(R.id.Aadhar);
        create = dialog.findViewById(R.id.create1);
        cancel = dialog.findViewById(R.id.cancel1);
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
                    int age2 = age.getText().toString() == "" ? null : arrayList.get(pos).getStdnt_age();
                    int id = arrayList.get(pos).getId();
                    char gder = ' ';
                    if (gender.getText().toString().length() >= 1)
                        gder = Character.toUpperCase(gender.getText().toString().charAt(0));

                    if (!validAge(age2) || !valiGender(gder)) {
                        Toast.makeText(AddDataMainPage.this, "Enter valid data", Toast.LENGTH_SHORT).show();
                    } else {
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

        } catch (Exception e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onLongpress(int pos, ArrayList<StudentData> studentData) {
        if (studentData.size() >= 1) {
            no_ofselected.setVisibility(View.VISIBLE);
            no_ofselected.setText("No of selected: " + studentData.size());
            selectdelete.setVisibility(View.VISIBLE);
        }
        selected = studentData;
    }

    @Override
    public void onClick(int pos, ArrayList<StudentData> studentData) {

        selected = studentData;
        no_ofselected.setText("No of selected: " + studentData.size());
        if (studentData.size() == 0) {
            selectdelete.setVisibility(View.INVISIBLE);
            no_ofselected.setVisibility(View.INVISIBLE);
        } else no_ofselected.setVisibility(View.VISIBLE);


    }

    @SuppressLint("ResourceAsColor")

    private void intialise() {

        try {
            selectdelete.setVisibility(View.INVISIBLE);
            selected.clear();
            arrayList.clear();
            try {
                Cursor cursor = db.fetch(dataBaseName);
                if (cursor != null && cursor.moveToNext()) {
                    do {
                        StudentData data = new StudentData(cursor.getInt(0),
                                cursor.getString(1), cursor.getString(2)
                                , cursor.getInt(3), cursor.getString(4)
                                , cursor.getString(5)
                                , cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9), cursor.getString(10));
                        arrayList.add(data);
                    } while (cursor.moveToNext());

                }
            } catch (Exception t) {
            }
            if (arrayList.size() == 0) {
                SharedPreferences pref = getSharedPreferences("applogin", MODE_PRIVATE);
                String databaseMain = pref.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
                DatabaseReference firebase = FirebaseDatabase.getInstance().getReference(databaseMain).child(dataBaseName).child("studentdata");

                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                boolean isInternetConnected = NetworkUtils.isInternetIsConnected(this);
                if (isInternetConnected) {
                    progressDialog.show();
                }

                firebase.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        progressDialog.dismiss();
                        Toast.makeText(AddDataMainPage.this, snapshot.getChildrenCount() + "", Toast.LENGTH_SHORT).show();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            StudentData dt = data.getValue(StudentData.class);
                            if (dt != null) {
                                Toast.makeText(AddDataMainPage.this, dt.getStdnt_name(), Toast.LENGTH_SHORT).show();
                                arrayList.add(dt);
                                db.insert(dataBaseName, dt);
                            }
                        }
                        //   adapter = new StudentDataAdapter(AddDataMainPage.this, arrayList, AddDataMainPage.this);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }

                });


            }
            //  Toast.makeText(this, arrayList.size()+"", Toast.LENGTH_SHORT).show();
            // adapter = new StudentDataAdapter(AddDataMainPage.this, arrayList, this);
            adapter.notifyDataSetChanged();
            numberOfStudentsTextView.setText("No of std : " + Integer.toString(arrayList.size()));
            if (selected.size() == 0) {
                no_ofselected.setVisibility(View.INVISIBLE);
            } else {
                no_ofselected.setVisibility(View.VISIBLE);
                no_ofselected.setText("No of selected: " + selected.size());
            }


        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    public static boolean validAge(int age) {
        return age >= 1 && age < 100;
    }

    private static boolean valiGender(char m) {
        return m == 'f' || m == 'F' || m == 'M' || m == 'm' || m == ' ';
    }

    void setMultiDeletesetup() {
        selectdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddDataMainPage.this, R.style.CustomAlertDialogTheme));
                builder.setTitle("Delete items").setMessage("Do you want to delete records (" + selected.size() + ")")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int count = 0;
                                SharedPreferences pref = getSharedPreferences("applogin", MODE_PRIVATE);
                                String databaseMain = pref.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                for (int j = 0; j < selected.size(); j++) {
                                    boolean res = db.delete(dataBaseName, Integer.toString(selected.get(j).getId()));
                                    DatabaseReference reff = ref.child(databaseMain).child(dataBaseName)
                                            .child("studentdata").child(Integer.toString(selected.get(j).getId()));
                                    reff.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(AddDataMainPage.this, "successful", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    if (res) {
                                        count++;
                                    }

                                }
                                Toast.makeText(AddDataMainPage.this, "" + count + " items deleted", Toast.LENGTH_SHORT).show();

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

    public void uploadToFireBase(View v) {


    }

    public void downloadFromFireBase(View v) {

    }


    @Override
    public void onDestroy() {
        SharedPreferences pref = getSharedPreferences("applogin", MODE_PRIVATE);
        String databaseMain = pref.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference(databaseMain).child(dataBaseName);
        firebase.child("batchdata").child("NoOfStudents").setValue(Integer.toString(arrayList.size()));
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).setIsselected(false);
            firebase.child("studentdata").child(arrayList.get(i).getId() + "").setValue(arrayList.get(i));
        }
        super.onDestroy();
    }

    public void senWhtsappMessage(String[] numbers, String message) {

        if (!Settings.canDrawOverlays(AddDataMainPage.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            activityResultLauncher.launch(intent);
        } else {
            Intent intent = new Intent(AddDataMainPage.this, FloatingLayoutService.class);
            intent.putExtra("message", message);
            intent.putExtra("Numbers", numbers);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(new Intent(intent));
            }
        }


        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    String contact = "7306198858";
                    String message = "Your message here";
                    Intent whts = new Intent(Intent.ACTION_VIEW);
                    whts.setPackage("com.whatsapp");
                    whts.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    whts.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + "7994497571" + "&text=" + Uri.encode("hello 3world")));
                    getApplicationContext().startActivity(whts);

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }



            }
        },100);

*/
    }

    private void sendSms(String message, String number) {
        SmsManager smsManager = SmsManager.getDefault();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            smsManager.sendTextMessage(number,
                    null, message, null, null, 0);
        } else {
            smsManager.sendTextMessage(number, null, message, null, null);
        }
    }


}