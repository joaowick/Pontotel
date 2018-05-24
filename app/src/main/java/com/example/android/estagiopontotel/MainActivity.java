package com.example.android.estagiopontotel;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;

    //URL de JSON
    private static String url = "https://s3-sa-east-1.amazonaws.com/pontotel-docs/data.json";

    ArrayList<HashMap<String, String>> datatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datatList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.listView);

        new GetDatas().execute();
    }

    private class GetDatas extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("carregando...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i=0; i < data.length(); i++){
                        JSONObject d = data.getJSONObject(i);

                        String id = d.getString("id");
                        String name = d.getString("name");
                        String pwd = d.getString("pwd");

                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("pwd", pwd);

                        datatList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,
                                    "JSON parsing error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Couldn't get json from server.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, datatList,
                    R.layout.list_item, new String[]{"id", "name", "pwd"},
                    new int[]{R.id.id, R.id.name, R.id.pwd});

            lv.setAdapter(adapter);
        }
    }
}
