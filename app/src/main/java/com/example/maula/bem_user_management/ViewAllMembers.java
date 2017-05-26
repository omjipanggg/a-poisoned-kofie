package com.example.maula.bem_user_management;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewAllMembers extends AppCompatActivity implements ListView.OnItemClickListener {

    private ListView listView;
    private String JSON_STR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_members);
        listView = (ListView) findViewById(R.id.memberList);
        listView.setOnItemClickListener(this);
        getJSON();
    }

    public void showAllMembers() {
        JSONObject jso = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jso = new JSONObject(JSON_STR);
            JSONArray result = jso.getJSONArray(Config.TAG_JSON_ARRAY);

            for (int a = 0; a < result.length(); a++) {
                JSONObject j = result.getJSONObject(a);
                String sid = j.getString(Config.TAG_SID);
                String name = j.getString(Config.TAG_NAME);

                HashMap<String, String> member = new HashMap<String, String>();
                member.put(Config.TAG_SID, sid);
                member.put(Config.TAG_NAME, name);
                list.add(member);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        ListAdapter la = new SimpleAdapter(
                ViewAllMembers.this, list, R.layout.list_items,
                new String[]{Config.TAG_SID, Config.TAG_NAME},
                new int[]{R.id.showID, R.id.showName}
        );
        listView.setAdapter(la);
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewAllMembers.this, "Fetching data...", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STR = s;
                showAllMembers();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_ALL);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(ViewAllMembers.this, ViewMember.class);
        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
        String memberSID = map.get(Config.TAG_SID).toString();
        i.putExtra(Config.MEMBER_SID, memberSID);
        startActivity(i);
    }
}
