package com.example.myapplication;

import static android.content.SharedPreferences.*;
import static com.example.myapplication.SignUpActivity.APP_LOGIN;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddDataMainPage extends AppCompatActivity implements StudentResponseInterface {
    EditText gender_EditText, date_EditText, time_EditText;
    EditText sendMessageBox;
    CountryCodePicker countryCodePickerParent, countryCodePickerStudent;
    TextView ed1, ed2, numberOfStudentsTextView, no_ofselected;
    ArrayList<DataIntentShare> share;
    String Main_message;
    ArrayList<StudentData> selected;

    ArrayList<StudentData> temp;
    EditText name, deptmnt, age, stdnt_contctno, gender, parentName, parentMobile, address, aadhar, regno;
    TextView create, cancel, title;
    ImageView back, selectdelete;
    FloatingActionButton send, edit, addStudent;
    RecyclerView recyclerView;

    StudentDataAdapter adapter;
    ArrayList<StudentData> arrayList;
    StudentsDatabase db;
    String dataBaseName;
    String RESULT_LINK = "";
    Boolean RESULT_MODE = false;
    boolean  isOnline=true;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText search;
    Dialog editDialog;
    String userDatabase = "";
    DatabaseReference reference;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @SuppressLint({"MissingInflatedId", "RestrictedApi", "NotifyDataSetChanged", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.i("test", e.getMessage());
        }
        // ** FOR CHECKING IF THE USER IS SET ONLINE OR OFFLINE MODE
        SharedPreferences settings = getSharedPreferences("Setting", MODE_PRIVATE);
        isOnline=!settings.getBoolean("isOfflineMode",false);
        //****************************************************************
        setContentView(R.layout.activity_add_data_main_page);
        swipeRefreshLayout = findViewById(R.id.addDatarefreshLayout);

        SharedPreferences preferences = getSharedPreferences(APP_LOGIN, MODE_PRIVATE);
        userDatabase = preferences.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
        adapter = new StudentDataAdapter(AddDataMainPage.this, arrayList, this);

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

        search = findViewById(R.id.student_search);
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
        arrayList = new ArrayList<>();
        ed1 = findViewById(R.id.batch_top);
        ed2 = findViewById(R.id.year_top);
        back = findViewById(R.id.back);
        //back press toolbar image
        back.setOnClickListener(view -> finish());
        //******************************************************************************
        Intent intent = getIntent();
        String bt = intent.getStringExtra("batch_name");
        String yr = intent.getStringExtra("batchYear");
        dataBaseName = intent.getStringExtra("TABLE_NAME");
        reference = FirebaseDatabase.getInstance().getReference(userDatabase + "/" + dataBaseName);
        Log.i("test",dataBaseName);
        db.createtabel(dataBaseName);
        setMultiDeletesetup();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new StudentDataAdapter(AddDataMainPage.this, arrayList, AddDataMainPage.this);
        recyclerView.setAdapter(adapter);
        ed1.setText(bt);
        ed2.setText(yr);
        intialiseWithdatabase();
        addStudent.setOnClickListener(view -> {
            Dialog dialog = new Dialog(AddDataMainPage.this);
            dialog.setContentView(R.layout.addstudentdialogbox);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            try {
                dialog.show();
                title = dialog.findViewById(R.id.title);
                name = dialog.findViewById(R.id.student_name);
                countryCodePickerParent = dialog.findViewById(R.id.coutryCodeParent);
                countryCodePickerStudent = dialog.findViewById(R.id.coutryCodeStudent);
                countryCodePickerStudent.setDefaultCountryUsingNameCode("+91");
                countryCodePickerParent.setDefaultCountryUsingNameCode("+91");
                deptmnt = dialog.findViewById(R.id.department);
                age = dialog.findViewById(R.id.age);
                stdnt_contctno = dialog.findViewById(R.id.student_mobil);
                gender = dialog.findViewById(R.id.student_gender);
                parentMobile = dialog.findViewById(R.id.parent_number);
                regno = dialog.findViewById(R.id.Studentregno);
                parentName = dialog.findViewById(R.id.parent_name);
                address = dialog.findViewById(R.id.address);
                aadhar = dialog.findViewById(R.id.Aadhar);
                create = dialog.findViewById(R.id.create1);
                cancel = dialog.findViewById(R.id.cancel1);
                create.setOnClickListener(view18 -> {
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

                        String parentNo = "";
                        String studentNo = "";
                        if (parentMobile.getText().toString().length() != 0) {
                            parentNo = countryCodePickerStudent.getSelectedCountryCodeWithPlus() + parentMobile.getText().toString();
                        }

                        if (stdnt_contctno.getText().toString().length() != 0) {
                            studentNo = countryCodePickerStudent.getSelectedCountryCodeWithPlus() + stdnt_contctno.getText().toString();
                        }
                        StudentData data = new StudentData(name.getText().toString(), gender.getText().toString()
                                , age2, deptmnt.getText().toString(),
                                studentNo, parentName.getText().toString()
                                , parentNo
                                , address.getText().toString(),
                                0, aadhar.getText().toString(), regno.getText().toString());

                        Boolean res = db.insert(dataBaseName, data);
                        int id = db.getIDofStudent(dataBaseName);
                        data.setId(id);

                        Log.i("test", reference.getPath().toString());
                        if (res) {

                            arrayList.add(data);
                            adapter.notifyDataSetChanged();
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                        dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(view17 -> dialog.dismiss());

            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        //****************************************************************
        // send button logic
        send.setOnClickListener(view -> {
            Dialog sendBottom = new Dialog(AddDataMainPage.this);
            sendBottom.setContentView(R.layout.send_mess_parent_dialog);
            sendBottom.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            sendBottom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            sendBottom.getWindow().setGravity(Gravity.BOTTOM);
            sendBottom.getWindow().getAttributes().windowAnimations = (R.style.SlideDialogAnimationBottomUp);
            Button cancel, sendMes;
            //*****************EditText
            sendMessageBox = sendBottom.findViewById(R.id.editTextTextMultiLine);
            gender_EditText = sendBottom.findViewById(R.id.mesEditGender);
            date_EditText = sendBottom.findViewById(R.id.mesDatetime);
            time_EditText = sendBottom.findViewById(R.id.mesEditime);
            //***************
            SharedPreferences savaSendBundle = getSharedPreferences("savesend", MODE_PRIVATE);
            String gn = savaSendBundle.getString("genderPronounce", "son/daughter");
            String MainTxt = savaSendBundle.getString("messageBody", getResources().getString(R.string.parent_message));
            gender_EditText.setText(gn);
            sendMessageBox.setText(MainTxt);
            //*********************************************************************************************************
            date_EditText.setText(DateAndTime.getCurrentDate());
            time_EditText.setText(DateAndTime.getCurrentTime());
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
            CardView exapandView = sendBottom.findViewById(R.id.expandcard);
            ImageView img = sendBottom.findViewById(R.id.imgDrop_send);
            img.setOnClickListener(view19 -> {
                if (exapandView.getVisibility() == View.GONE) {
                    exapandView.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.arrow_up_24);
                } else {
                    exapandView.setVisibility(View.GONE);
                    img.setImageResource(R.drawable.arrow_down_24);
                }
            });

            // checkBox configuration logic

            isBothMedium.setOnClickListener(view16 -> {
                isWhts.setChecked(false);
                issms.setChecked(false);
            });

            isBothPar_and_stud.setOnClickListener(view15 -> {
                isparent.setChecked(false);
                isstudent.setChecked(false);
            });

            // *********************************************************

            //********************************************************

            // adding all the selected list into a temp array
            temp.addAll(selected);
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
                    chip.setOnCloseIconClickListener(view14 -> {
                        Chip ch = (Chip) view14;
                        String id1 = ch.getText().toString();
                        int m1 = 0;
                        StringBuilder res = new StringBuilder();
                        while (id1.charAt(m1) != ':') {
                            res.append(id1.charAt(m1));
                            m1++;
                        }
                        int res_num = Integer.parseInt(res.toString());
                        for (int i12 = 0; i12 < temp.size(); i12++) {
                            if (temp.get(i12).getId() == res_num) {
                                temp.remove(temp.get(i12));
                            }
                        }
                        if (temp.size() == 0) {
                            Chip chip1 = new Chip(AddDataMainPage.this);
                            chip1.setBackgroundColor(Color.BLUE);
                            chip1.setText("No selection is added");
                            chipGroup.addView(chip1);
                        }
                        chipGroup.removeView(view14);
                    });
                    chipGroup.addView(chip);
                }

            }
            //***********************************************************

            //**********************************************************

            // when send dialog button pressed
            sendMes.setOnClickListener(view13 -> {
                share = new ArrayList<>();
                boolean isWhatsapp, isSMS, isBothWhatsappAndSMS, isParent, isStudent, isBothParentAndStudent;
                isWhatsapp = isWhts.isChecked();
                isSMS = issms.isChecked();
                isBothParentAndStudent = isBothPar_and_stud.isChecked();
                isParent = isparent.isChecked();
                isStudent = isstudent.isChecked();
                isBothWhatsappAndSMS = isBothMedium.isChecked();
                Main_message = sendMessageBox.getText().toString();
                @SuppressLint("UseSwitchCompatOrMaterialCode")
                Switch saveprefrence = sendBottom.findViewById(R.id.saveprefrencesend);
                if (saveprefrence.isChecked()) {
                    @SuppressLint("CommitPrefEdits")
                    Editor editSave = savaSendBundle.edit();
                    editSave.putString("genderPronounce", gender_EditText.getText().toString());
                    editSave.putString("messageBody", Main_message);
                    editSave.apply();
                }
                if (isBothWhatsappAndSMS) {
                    isWhatsapp = true;
                    isSMS = true;
                }
                if (isBothParentAndStudent) {
                    isParent = isStudent = true;
                }
                //************************************************************
                Dexter.withContext(AddDataMainPage.this)
                        .withPermissions(
                                Manifest.permission.SEND_SMS
                        ).withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                        }).check();
                //************************************************************

                if (isWhatsapp) {
                    try {
                        for (int i = 0; i < temp.size(); i++) {

                            if (isParent) {
                                String numberParent = temp.get(i).getParent_no();
                                boolean isValidNumber = numberParent.length() >= 10;
                                if (isValidNumber) {
                                    String mess = makeValidMessage(temp.get(i), Main_message, false, true);
                                    DataIntentShare dt = new DataIntentShare(numberParent, mess);
                                    share.add(dt);
                                    temp.get(i).increment_late_comes();
                                    boolean res = db.update(dataBaseName, temp.get(i));
                                    if (res)
                                        Log.d("isupdated", res + "");
                                }
                            }

                            if (isStudent) {
                                String numberStudent = temp.get(i).getStdnt_mobileNo();

                                boolean isValidNumber = numberStudent.length() >= 10;
                                if (isValidNumber) {
                                    String mess = makeValidMessage(temp.get(i), Main_message, true, false);
                                    DataIntentShare dt = new DataIntentShare(numberStudent, mess);
                                    share.add(dt);
                                    if (!isParent) {
                                        temp.get(i).increment_late_comes();
                                        db.update(dataBaseName, temp.get(i));
                                    }
                                }
                            }

                        }

                        if (share.size() >= 1) {
                            senWhtsappMessage();
                            adapter.notifyDataSetChanged();
                            adapter.setSelctionMode(false);
                        } else {
                            Toast.makeText(AddDataMainPage.this, "No number is selected", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (isSMS) {
                    for (DataIntentShare data : share) {
                        sendSms(data.getMessage(), data.getNumber());

                    }
                    Toast.makeText(this, "sms", Toast.LENGTH_SHORT).show();
                }

            });

            sendBottom.setOnCancelListener(dialogInterface -> temp.clear());

            cancel.setOnClickListener(view12 -> {
                sendBottom.dismiss();
                Toast.makeText(AddDataMainPage.this, "cancelled", Toast.LENGTH_SHORT).show();
            });
            sendBottom.show();

        });

        //**************************************************************************************
        edit.setOnClickListener(view -> {
            editDialog = new Dialog(AddDataMainPage.this);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            editDialog.setContentView(R.layout.edit_dage_dialog);
            editDialog.getWindow().setGravity(Gravity.CENTER);
            editDialog.show();
            Button ok = editDialog.findViewById(R.id.setokResult);
            EditText text = editDialog.findViewById(R.id.resultLink);
            @SuppressLint("UseSwitchCompatOrMaterialCode")
            Switch mode = editDialog.findViewById(R.id.switch1);
            text.setText(RESULT_LINK);
            mode.setChecked(RESULT_MODE);
            ok.setOnClickListener(view1 -> {
                RESULT_LINK = text.getText().toString();
                RESULT_MODE = mode.isChecked();
                editDialog.dismiss();
            });
        });
        intialise();
    }

    private String makeValidMessage(StudentData studentData, String main_message, boolean st, boolean pr) {
        String res = main_message;
        String gender = gender_EditText.getText().toString();
        String date = date_EditText.getText().toString();
        String time = time_EditText.getText().toString();
        String resGender = "";
        StringBuilder male = new StringBuilder();
        StringBuilder female = new StringBuilder();
        boolean b = true;
        for (int i = 0; i < gender.length(); i++) {
            if (gender.charAt(i) == '/') {
                b = false;
                continue;
            }
            if (b) {
                male.append(gender.charAt(i));
            } else {
                female.append(gender.charAt(i));
            }
        }
        if (studentData.getStd_gender().equalsIgnoreCase("f")) {
            resGender = female.toString();
        } else if (studentData.getStd_gender().equalsIgnoreCase("m")) {
            resGender = male.toString();
        }
        res = res.replace("{", "");
        res = res.replace("}", "");
        res = res.replaceAll("(?i)Gender", resGender);
        res = res.replaceAll("(?i)StudentName", studentData.getStdnt_name());
        res = res.replaceAll("(?i)parentName", studentData.getParent_name());
        res = res.replaceAll("(?i)dateset", date);
        res = res.replaceAll("(?i)timeset", time);

        if (st) {
            res = res.replaceAll("(?i)p/s", studentData.getStdnt_name());
        }
        if (pr) {
            res = res.replaceAll("(?i)p/s", studentData.getParent_name());
        }

        return res;
    }


    private void filter(String srchname) {
        ArrayList<StudentData> searchList = new ArrayList<>();
        for (StudentData data : arrayList) {
            if (data.getStdnt_name().toLowerCase().contains(srchname.toLowerCase())) {
                searchList.add(data);
            }
        }

        adapter.filter(searchList);

    }

    @Override
    public void onTouchDelete(int pos) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
            builder.setTitle("Delete student").setMessage("Do you want to delete?").setPositiveButton("Yes", (dialogInterface, i) -> {
                Boolean res = db.delete(dataBaseName, Integer.toString(arrayList.get(pos).getId()));
                if (res) {
                    String tmp = arrayList.get(pos).getStdnt_name();
                    if(isOnline){
                        reference.child("studentdata").child(arrayList.get(pos).getId() + "").removeValue();
                    }

                     arrayList.remove(pos);
                    intialise();

                    Toast.makeText(AddDataMainPage.this, tmp + " deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddDataMainPage.this, "item not deleted", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).show();
        } catch (Exception e) {
            Log.i("test", e.getMessage());
        }
    }


    @SuppressLint("SetTextI18n")
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
        countryCodePickerParent = dialog.findViewById(R.id.coutryCodeParent);
        countryCodePickerStudent = dialog.findViewById(R.id.coutryCodeStudent);
        countryCodePickerStudent.setDefaultCountryUsingNameCode("+91");
        countryCodePickerParent.setDefaultCountryUsingNameCode("+91");
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
        regno = dialog.findViewById(R.id.Studentregno);
        deptmnt.setText(arrayList.get(pos).getStdnt_dprtmnt());
        age.setText(Integer.toString(arrayList.get(pos).getStdnt_age()));
        gender.setText(arrayList.get(pos).getStd_gender());
        stdnt_contctno.setText(arrayList.get(pos).getStdnt_mobileNo());
        parentName.setText(arrayList.get(pos).getParent_name());
        parentMobile.setText(arrayList.get(pos).getParent_no());
        address.setText(arrayList.get(pos).getAddress());
        regno.setText(arrayList.get(pos).getStd_register_no());
        aadhar.setText(arrayList.get(pos).getAadhar_no());
        create.setText("UPDATE");

        // gender.setText(arrayList.get(pos).getStd_gender());

        try {
            create.setOnClickListener(view -> {
                int age2 = age.getText().toString().equals("") ? 0 : arrayList.get(pos).getStdnt_age();
                int id = arrayList.get(pos).getId();
                char gder = ' ';
                if (gender.getText().toString().length() >= 1)
                    gder = Character.toUpperCase(gender.getText().toString().charAt(0));

                if (!validAge(age2) || !valiGender(gder)) {
                    Toast.makeText(AddDataMainPage.this, "Enter valid data", Toast.LENGTH_SHORT).show();
                } else {
                    String parentNo = "";
                    String studentNo = "";
                    if (parentMobile.getText().toString().length() != 0) {
                        if (!parentMobile.getText().toString().startsWith(countryCodePickerParent.getSelectedCountryCodeWithPlus())) {
                            parentNo += countryCodePickerParent.getSelectedCountryCodeWithPlus();
                        }

                        parentNo += parentMobile.getText().toString();
                    }

                    if (stdnt_contctno.getText().toString().length() != 0) {
                        if (!stdnt_contctno.getText().toString().startsWith(countryCodePickerStudent.getSelectedCountryCodeWithPlus())) {
                            studentNo += countryCodePickerStudent.getSelectedCountryCodeWithPlus();
                        }
                        Log.i("start", countryCodePickerStudent.getSelectedCountryCodeWithPlus());
                        studentNo += stdnt_contctno.getText().toString();
                    }
                    StudentData data = new StudentData(id, name.getText().toString(), gender.getText().toString(), age2
                            , deptmnt.getText().toString()
                            , studentNo
                            , parentName.getText().toString(), parentNo
                            , address.getText().toString(),
                            arrayList.get(pos).getNumber_of_late_comes(), aadhar.getText().toString(), regno.getText().toString());

                    Boolean res = db.update(dataBaseName, data);
                    if (res&&isOnline) {
                        dialog.dismiss();
                        reference.child("studentdata").child(arrayList.get(pos).getId() + "").setValue(data);
                        intialise();
                        Toast.makeText(AddDataMainPage.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            cancel.setOnClickListener(view -> {
                dialog.dismiss();
                Toast.makeText(AddDataMainPage.this, "update cancelled", Toast.LENGTH_SHORT).show();
            });
            dialog.show();

        } catch (Exception e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onLongpress(int pos, ArrayList<StudentData> studentData) {
        if (studentData.size() >= 1) {
            no_ofselected.setVisibility(View.VISIBLE);
            no_ofselected.setText("No of selected: " + studentData.size());
            selectdelete.setVisibility(View.VISIBLE);
        }
        selected = studentData;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(int pos, ArrayList<StudentData> studentData) {

        selected = studentData;
        no_ofselected.setText("No of selected: " + studentData.size());
        if (studentData.size() == 0) {
            selectdelete.setVisibility(View.INVISIBLE);
            no_ofselected.setVisibility(View.INVISIBLE);
            if (RESULT_MODE) {

                Intent intent = new Intent(AddDataMainPage.this, ResultView.class);
                intent.putExtra("MainUrl", RESULT_LINK);
                intent.putExtra("regno", arrayList.get(pos).getStd_register_no());
                intent.putExtra("aadhar", arrayList.get(pos).getAadhar_no());
                startActivity(intent);

            }

        } else no_ofselected.setVisibility(View.VISIBLE);


    }

    @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged", "SetTextI18n"})

    private void intialise() {
        try {
            adapter.notifyDataSetChanged();
            selectdelete.setVisibility(View.INVISIBLE);
            selected.clear();
            arrayList.clear();

            arrayList.size();
                localDatabaseFetch();



            if (selected.size() == 0) {
                no_ofselected.setVisibility(View.INVISIBLE);
            } else {
                no_ofselected.setVisibility(View.VISIBLE);
                no_ofselected.setText("No of selected: " + selected.size());
            }
        } catch (Exception e) {

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void intialiseWithdatabase() {

        swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                if(!isOnline) throw  new Exception("offline mode");
                if (!NetworkUtils.isInternetIsConnected(AddDataMainPage.this)) {
                    throw new Exception("offline unable to connect");
                } else {
                    Toast.makeText(AddDataMainPage.this, "checking database", Toast.LENGTH_SHORT).show();
                    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference(userDatabase + "/" + dataBaseName + "/" + "studentdata");


                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.i("dubugg", "count is " + snapshot.getChildrenCount());

                            arrayList.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                StudentData dt = data.getValue(StudentData.class);
                                if (dt != null) {
                                    arrayList.add(dt);
                                    db.insert(dataBaseName, dt);
                                }
                            }


                            adapter.notifyDataSetChanged();
                            numberOfStudentsTextView.setText("No of std : " + arrayList.size());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            arrayList.clear();
                            localDatabaseFetch();
                        }
                    });

                }


            } catch (Exception e) {
            }
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            }, 800);
        });


    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    void localDatabaseFetch() {
        Cursor cursor = db.fetch(dataBaseName);
        if (cursor != null && cursor.moveToNext()) {
            Log.i("test", cursor.getCount() + "number of otems");
            do {
                StudentData data = new StudentData(cursor.getInt(0),
                        cursor.getString(1), cursor.getString(2)
                        , cursor.getInt(3), cursor.getString(4)
                        , cursor.getString(5)
                        , cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11));
                arrayList.add(data);
            } while (cursor.moveToNext());
            numberOfStudentsTextView.setText("No of std : " + arrayList.size());
            adapter.notifyDataSetChanged();
        }
    }

    public static boolean validAge(int age) {
        return age >= 1 && age < 100;
    }

    private static boolean valiGender(char m) {
        return m == 'f' || m == 'F' || m == 'M' || m == 'm' || m == ' ';
    }

    void setMultiDeletesetup() {
        selectdelete.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddDataMainPage.this, R.style.CustomAlertDialogTheme));
            builder.setTitle("Delete items").setMessage("Do you want to delete records (" + selected.size() + ")")
                    .setPositiveButton("yes", (dialogInterface, i) -> {
                        for (int j = 0; j < selected.size(); j++) {
                            boolean res = db.delete(dataBaseName, Integer.toString(selected.get(j).getId()));
                            if (res&&isOnline) {
                                reference.child("studentdata").child(Integer.toString(selected.get(j).getId())).
                                        removeValue();

                            }
                        }

                        intialise();
                        selectdelete.setVisibility(View.INVISIBLE);
                    }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).show();


        });

    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public void uploadToFireBase(View v) {
        if (arrayList.size() != 0&&isOnline) {

            Dialog upload = new Dialog(AddDataMainPage.this);
            upload.setContentView(R.layout.upload_stdnt_batch);
            upload.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            upload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ProgressBar progressBar = upload.findViewById(R.id.progressBar6);
            progressBar.setVisibility(View.VISIBLE);
            Button cancel, uploadbt;
            EditText Name = upload.findViewById(R.id.uploadName);
            EditText Pin = upload.findViewById(R.id.uploadPin);
            cancel = upload.findViewById(R.id.cancelUpload);
            uploadbt = upload.findViewById(R.id.setupload);
            AtomicInteger clicks = new AtomicInteger();
            Switch isaccessibile = upload.findViewById(R.id.isaccssible);
            DatabaseReference getRef = FirebaseDatabase.getInstance().getReference(userDatabase + "/" + dataBaseName + "/batchPin");
            getRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    BatchPinConfigure configure = snapshot.getValue(BatchPinConfigure.class);
                    if (configure != null) {
                        Name.setText(configure.getName());
                        Pin.setText(configure.getPin());
                        isaccessibile.setChecked(configure.isaccessible);
                    }
                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                }
            });
            new Handler().postDelayed(() -> {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    Toast.makeText(this, "check network", Toast.LENGTH_SHORT).show();
                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                }
            }, 3000);


            uploadbt.setOnClickListener(view -> {
                clicks.getAndIncrement();
                try {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.show();
                    boolean isaccess = isaccessibile.isChecked();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userDatabase + "/" + dataBaseName);
                    String name1 = Name.getText().toString();
                    String pin1 = Pin.getText().toString();
                    if (name1.equals("") || pin1.equals(""))
                        throw new Exception("Enter a valid entry");
                    BatchPinConfigure configure = new BatchPinConfigure(name1, pin1, isaccess);
                    if (!NetworkUtils.isInternetIsConnected(getApplicationContext())) {
                        Toast.makeText(this, "check network", Toast.LENGTH_SHORT).show();
                    }
                    ref.child("batchPin").setValue(configure).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!isaccessibile.isChecked()) {
                                Toast.makeText(this, "Make it accessible to others", Toast.LENGTH_SHORT).show();
                                isaccessibile.setError("");
                                isaccessibile.requestFocus();
                                new Handler().postDelayed(() -> {
                                    upload.dismiss();
                                    Toast.makeText(AddDataMainPage.this, "configured successfully", Toast.LENGTH_SHORT).show();
                                }, 500);
                            } else {
                                upload.dismiss();
                                Toast.makeText(AddDataMainPage.this, "configured successfully", Toast.LENGTH_SHORT).show();
                            }
                            runOnUiThread(progressDialog::dismiss);


                        } else {
                            runOnUiThread(progressDialog::dismiss);
                            Toast.makeText(AddDataMainPage.this, "configured failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {

                    Toast.makeText(AddDataMainPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            cancel.setOnClickListener(view -> upload.dismiss());
            upload.show();
        } else if(arrayList.size()==0){
            Toast.makeText(this, "No selection", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "offline mode", Toast.LENGTH_SHORT).show();

        }
    }

    public void downloadFromFireBase(View v) {
        if (!NetworkUtils.isInternetIsConnected(this)) {
            Toast.makeText(this, "check network", Toast.LENGTH_SHORT).show();
        } else if(isOnline){
            Dialog download = new Dialog(this);
            ProgressDialog reqDialog = new ProgressDialog(this);
            download.setContentView(R.layout.request_batch_add_dialog);
            download.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            download.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            download.show();
            long id = (int) (Math.random() * 1000000000);
            SharedPreferences preferences = getSharedPreferences(APP_LOGIN, MODE_PRIVATE);
            String name = preferences.getString("login_name", "");
            String email = preferences.getString("login_email", "");
            String databaseMain = preferences.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
            EditText reqBatch, reqname, reqpin;
            reqBatch = download.findViewById(R.id.downloadbatchName);
            reqname = download.findViewById(R.id.downloadName);
            reqpin = download.findViewById(R.id.downloadPin);
            Button cancel, request;
            cancel = download.findViewById(R.id.cancelrequest);
            request = download.findViewById(R.id.requestupload);
            cancel.setOnClickListener(view -> {
                Toast.makeText(AddDataMainPage.this, "request cancelled", Toast.LENGTH_SHORT).show();
                download.dismiss();
            });
            request.setOnClickListener(view -> {
                reqDialog.show();
                BatchPinConfigure confi = new BatchPinConfigure(reqname.getText().toString(), reqpin.getText().toString());
                String batchofowner = reqBatch.getText().toString();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (data.child(batchofowner.toUpperCase()).child("batchPin").exists()) {
                                String token = (String) data.child("Userdata-1").child("token").getValue();
                                String ownerName="owner";
                                ownerName= (String) data.child("Userdata-1").child("name").getValue();

                                 BatchPinConfigure bt = data.child(batchofowner.toUpperCase()).child("batchPin").getValue(BatchPinConfigure.class);
                                if (bt != null && bt.getName().equalsIgnoreCase(confi.getName()) && bt.getPin().equals(confi.getPin()) && bt.isIsaccessible()) {
                                    MessagesReceiver message = new MessagesReceiver(id, new DateAndTime(), name, email, dataBaseName,
                                            confi, databaseMain, batchofowner);
                                    message.receiverToken=token;
                                    message.setOwnerName(ownerName);
                                    SharedPreferences preferences=getSharedPreferences("token",MODE_PRIVATE);
                                    message.setRequesterToken(preferences.getString("mytoken","null"));
                                    sendNotifiaction(token, message);
                                    data.getRef().child("messageRecieved").child(message.getMessageId() + "").setValue(message).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AddDataMainPage.this, "Request send successfully", Toast.LENGTH_SHORT).show();
                                            DatabaseReference myref = FirebaseDatabase.getInstance().getReference(databaseMain);
                                            myref.child("MessageSend").child(id + "").setValue(message);
                                        }
                                    }).addOnFailureListener(e ->
                                            Toast.makeText(AddDataMainPage.this, e.getMessage(), Toast.LENGTH_SHORT).show());

                                    download.dismiss();
                                } else if (!bt.isIsaccessible()) {
                                    Toast.makeText(AddDataMainPage.this, "Access Denied", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }
                        runOnUiThread(reqDialog::dismiss);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddDataMainPage.this, "No user found", Toast.LENGTH_SHORT).show();
                    }
                });

            });
        }else{
            Toast.makeText(this, "offline mode", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotifiaction(String token, MessagesReceiver mr) {
        try {

            JSONObject object = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("title", "Data accessing request");
            notification.put("body", mr.getRequesterName() + " requesting for batch access " + mr.getReqeustedBatchOFowner());

            JSONObject dataObj = new JSONObject();

            object.put("data", dataObj);
            object.put("notification", notification);
            object.put("to", token);


            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            String url = "https://fcm.googleapis.com/fcm/send";
            RequestBody reqestBody = RequestBody.create(object.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(reqestBody)
                    .addHeader("Authorization", "key=AAAAkEPqclM:APA91bF0Xmf1i-ZSE1K0tcBIb47cWRDSngDS_WZdP9l3SQ4rRIWfR1uAa6M0iQsDlfhyaht1ollp40wpieu_OWGSSgNS40VoqISEb9dGRTbAQR3lcOhUJNjbWX51wJD3C3yg50FAnpmo")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        if(isOnline) {
            SharedPreferences pref = getSharedPreferences("applogin", MODE_PRIVATE);
            String databaseMain = pref.getString(SignUpActivity.MAIN_DATABASE_NAME, "default");
            DatabaseReference firebase = FirebaseDatabase.getInstance().getReference(databaseMain).child(dataBaseName);
            Intent intent = getIntent();
            String bt = intent.getStringExtra("batch_name");
            String yr = intent.getStringExtra("batchYear");
            Homefiles hm=new Homefiles(bt,yr,bt.toUpperCase(),Integer.toString(arrayList.size()));
            firebase.child("batchdata"). setValue(hm);


            for (int i = 0; i < arrayList.size(); i++) {
                arrayList.get(i).setIsselected(false);
                firebase.child("studentdata").child(arrayList.get(i).getId() + "").setValue(arrayList.get(i));
            }
            db.close();
        }
        super.onDestroy();
    }


    public void senWhtsappMessage() {
        if (!Settings.canDrawOverlays(AddDataMainPage.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));


            activityResultLauncher.launch(intent);
        } else {
            Intent intent = new Intent(AddDataMainPage.this, FloatingLayoutService.class);
            intent.putParcelableArrayListExtra("array", share);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(new Intent(intent));
            }
        }

    }

    private void sendSms(String message, String number) {
        SmsManager smsManager = SmsManager.getDefault();

        // Divide the message into parts if it exceeds the maximum length
        ArrayList<String> parts = smsManager.divideMessage(message);

        // Send the SMS
        smsManager.sendMultipartTextMessage(number, null, parts, null, null);
    }


}