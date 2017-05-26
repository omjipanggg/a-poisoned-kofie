package com.example.maula.bem_user_management;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ViewMember extends AppCompatActivity implements View.OnClickListener {

    private static final int DIALOG_ID = 0;
    private TextView btnHome;
    private EditText m_id, m_name, m_gender, m_birthdate, m_email, m_phone;
    private Button btnUpdate, btnDelete;
    private String id;
    private int y, m, d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_member);

        Intent i = getIntent();
        id = i.getStringExtra(Config.MEMBER_SID);

        m_id = (EditText) findViewById(R.id.memberID);
        m_name = (EditText) findViewById(R.id.memberName);
        m_gender = (EditText) findViewById(R.id.memberJK);
        m_birthdate = (EditText) findViewById(R.id.memberBday);
        m_email = (EditText) findViewById(R.id.memberEmail);
        m_phone = (EditText) findViewById(R.id.memberPhone);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnHome = (TextView) findViewById(R.id.btnHome);

        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        m_birthdate.setOnClickListener(this);

        m_id.setText(id);
        getMember();
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
            m_birthdate.setText(dateStr);
        }
    };

    private void getMember() {
        class GetMember extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewMember.this, "Fetching data...", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showMember(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_MEMBER, id);
                return s;
            }
        }
        GetMember gm = new GetMember();
        gm.execute();
    }

    private void updMember() {
        final String name = m_name.getText().toString();
        final String gender = m_gender.getText().toString();
        final String birthdate = m_birthdate.getText().toString();
        final String email = m_email.getText().toString();
        final String phone = m_phone.getText().toString();

        class UpdateMember extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewMember.this, "Updating data...", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ViewMember.this, s, Toast.LENGTH_LONG).show();
                startActivity(new Intent(ViewMember.this, ViewAllMembers.class));
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_MEMBER_SID,id);
                hashMap.put(Config.KEY_MEMBER_NAME,name);
                hashMap.put(Config.KEY_MEMBER_GENDER,gender);
                hashMap.put(Config.KEY_MEMBER_BDAY,birthdate);
                hashMap.put(Config.KEY_MEMBER_EMAIL,email);
                hashMap.put(Config.KEY_MEMBER_PHONE,phone);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(Config.URL_UPDATE_MEMBER, hashMap);
                return s;
            }
        }
        UpdateMember um = new UpdateMember();
        um.execute();
    }

    private void delMember() {
        class DelMember extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewMember.this, "Deleting data...", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ViewMember.this, "Deleted", Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_DELETE_MEMBER, id);
                return s;
            }
        }
        DelMember dm = new DelMember();
        dm.execute();
    }

    private void showMember(String json) {
        try {
            JSONObject jso = new JSONObject(json);
            JSONArray result = jso.getJSONArray(Config.TAG_JSON_ARRAY);
            JSONObject cde = result.getJSONObject(0);
            String name = cde.getString(Config.TAG_NAME);
            String gender = cde.getString(Config.TAG_GENDER);
            String birthdate = cde.getString(Config.TAG_BDAY);
            String email = cde.getString(Config.TAG_EMAIL);
            String phone = cde.getString(Config.TAG_PHONE);

            m_name.setText(name);
            m_gender.setText(gender);
            m_birthdate.setText(birthdate);
            m_email.setText(email);
            m_phone.setText(phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void askDelete(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure?");

        alertDialogBuilder.setPositiveButton("Yes",
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                delMember();
                startActivity(new Intent(ViewMember.this, ViewAllMembers.class));
            }
        });

        alertDialogBuilder.setNegativeButton("No",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // logic goes here //
                }
            });
        AlertDialog ad = alertDialogBuilder.create();
        ad.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.memberBday:
                Log.d("Clicked the button", "Date");
                showDialog(DIALOG_ID);
                break;
            case R.id.btnHome:
                Log.d("Clicked the button","Home");
                startActivity(new Intent(ViewMember.this, MainActivity.class));
                break;
            case R.id.btnUpdate:
                Log.d("Clicked the button","Update");
                updMember();
                startActivity(new Intent(ViewMember.this, ViewAllMembers.class));
                break;
            case R.id.btnDelete:
                Log.d("Clicked the button","Delete");
                askDelete();
                break;
            default:
                // Do nothing
                break;
        }
    }
}
