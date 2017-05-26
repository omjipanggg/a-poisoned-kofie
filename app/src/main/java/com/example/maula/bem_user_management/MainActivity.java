package com.example.maula.bem_user_management;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Button btnAdd, btnReset;
    private EditText name, sex, birthDate, email, phone, sid;
    private RadioGroup radioSex;
    private RadioButton rdM, rdF;
    private TextView viewAll;
    private int y, m, d;
    private static final int DIALOG_ID = 0;
    private String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sid = (EditText) findViewById(R.id.editID);
        name = (EditText) findViewById(R.id.editName);
        radioSex = (RadioGroup) findViewById(R.id.radioSex);
        rdM = (RadioButton) findViewById(R.id.radioMale);
        rdF = (RadioButton) findViewById(R.id.radioFemale);
        birthDate = (EditText) findViewById(R.id.editBday);
        email = (EditText) findViewById(R.id.editEmail);
        phone = (EditText) findViewById(R.id.editPhone);
        viewAll = (TextView) findViewById(R.id.btnView);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnReset = (Button) findViewById(R.id.btnReset);

        final Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);

        radioSex.setOnCheckedChangeListener(this);
        viewAll.setOnClickListener(this);
        birthDate.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

     @Override
     protected Dialog onCreateDialog(int id) {
         if (id == DIALOG_ID) {
             return new DatePickerDialog(this, dpl, y, m, d);
         } else { return null; }
     }

     private DatePickerDialog.OnDateSetListener dpl = new DatePickerDialog.OnDateSetListener() {
         @Override
         public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
         y = year;
         m = monthOfYear;
         d = dayOfMonth;
         String dateStr = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
//       Toast.makeText(MainActivity.this, y + "/" + (m+1) + "/" + d, Toast.LENGTH_LONG).show();
         birthDate.setText(dateStr);
         }
     };

     private void addMember() {
         final String memberSID = sid.getText().toString();
         final String memberName = name.getText().toString();
         final String memberJK = temp;
         final String memberTTL = birthDate.getText().toString();
         final String memberEmail = email.getText().toString();
         final String memberPhone = phone.getText().toString();

         class AddMember extends AsyncTask<Void, Void, String> {
             ProgressDialog loading;

             @Override
             protected void onPreExecute() {
                 super.onPreExecute();
                 loading = ProgressDialog.show(MainActivity.this, "Saving data...", "Please wait...", false, false);
             }

             @Override
             protected void onPostExecute(String s) {
                 super.onPostExecute(s);
                 loading.dismiss();
                 Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
             }

             @Override
             protected String doInBackground(Void... v) {
                 HashMap<String, String> params = new HashMap<>();
                 params.put(Config.KEY_MEMBER_SID, memberSID);
                 params.put(Config.KEY_MEMBER_NAME, memberName);
                 params.put(Config.KEY_MEMBER_GENDER, memberJK);
                 params.put(Config.KEY_MEMBER_BDAY, memberTTL);
                 params.put(Config.KEY_MEMBER_EMAIL, memberEmail);
                 params.put(Config.KEY_MEMBER_PHONE, memberPhone);

                 RequestHandler rh = new RequestHandler();
                 String res = rh.sendPostRequest(Config.URL_ADD, params);
                 return res;
             }
         }
         AddMember am = new AddMember();
         am.execute();
     }

     @Override
     public void onClick(View v) {
         if (v == birthDate) {
             Log.d("Date", "Picking a date");
             showDialog(DIALOG_ID);
         } else if (v == viewAll) {
             Log.d("Show", "Showing the rest of members");
             Intent i = new Intent(MainActivity.this, ViewAllMembers.class);
             startActivity(i);
         } else if (v == btnAdd) {
             Log.d("Add", "Adding a new member");
             addMember();
             clearText();
         } else if (v == btnReset) {
             Log.d("Reset", "Clearing the fields");
             clearText();
         } else {}
     }

     private void clearText() {
         sid.setText("");
         name.setText("");
         rdM.setChecked(true);
         birthDate.setText("");
         email.setText("");
         phone.setText("");
     }

     @Override
     public void onCheckedChanged(RadioGroup group, int checkedId) {
         int a = group.getChildCount();
         String gender = null;
         for (int x = 0; x < a; x++) {
             RadioButton btn = (RadioButton) group.getChildAt(x);

             if (btn.getId() == R.id.radioMale) {
                 temp = "L";
             } else
             if (btn.getId() == R.id.radioFemale)
             {
                 temp = "P";
             } else {}

             if (btn.getId() == checkedId) {
                 gender = temp;
             }
         }
         Log.e("Gender", gender);
     }
 }
