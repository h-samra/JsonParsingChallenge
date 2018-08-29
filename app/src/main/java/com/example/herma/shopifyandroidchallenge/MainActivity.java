package com.example.herma.shopifyandroidchallenge;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        textView.setText("");
        textView.setMovementMethod(new ScrollingMovementMethod());




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTask().execute("https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6");

            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    public class JSONTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return buffer.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //textView.setText(s);

            try {
                catagorizeByProvince(s);
                catagorizeByYear(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        void catagorizeByYear(String data) throws JSONException {
            JSONObject parentObject = new JSONObject(data);
            JSONArray parentArray = parentObject.getJSONArray("orders");
            StringBuilder output = new StringBuilder();
            ArrayList<Integer> years;
            years = new ArrayList<>();

            for (int i=0; i<parentArray.length(); i++) {
                JSONObject currentOrder = parentArray.getJSONObject(i);
                if (currentOrder.has("created_at")) {
                    String dates = (currentOrder.getString("created_at"));
                    String yearStr = dates.substring(0,4);
                    int year = Integer.parseInt(yearStr);
                    years.add(year);
                }
            }

            Collections.sort(years);

            int count = 0;
            for (int j = 0; j<years.size(); j++) {
                count++;
                if ((j+1) < years.size()) {
                    if (!(years.get(j).equals(years.get(j+1)))) {
                        output.append(count).append(" number of orders from ").append(years.get(j)).append("\n");
                        count = 0;
                    }
                } else {
                    output.append(count).append(" number of orders from ").append(years.get(j)).append("\n");
                }

            }

            textView.append(output.toString());

        }

        @SuppressLint("SetTextI18n")
        void catagorizeByProvince(String data) throws JSONException {

            JSONObject parentObject = new JSONObject(data);
            JSONArray parentArray = parentObject.getJSONArray("orders");
            StringBuilder output = new StringBuilder();
            ArrayList<String> provinces = new ArrayList<>();

            for (int i=0; i<parentArray.length(); i++) {
                JSONObject currentOrder = parentArray.getJSONObject(i);
                if (currentOrder.has("shipping_address")) {
                    JSONObject AddressObject = currentOrder.getJSONObject("shipping_address");
                    provinces.add(AddressObject.getString("province"));
                }
            }

            Collections.sort(provinces);

            int count = 0;
            for (int j = 0; j<provinces.size(); j++) {
                count++;
                if ((j+1) < provinces.size()) {
                    if (!(provinces.get(j).equals(provinces.get(j+1)))) {
                        output.append(count).append(" number of orders from ").append(provinces.get(j)).append("\n");
                        count = 0;
                    }
                } else {
                    output.append(count).append(" number of orders from ").append(provinces.get(j)).append("\n");
                }

            }



            textView.setText(output + "\n\n\n");



        }

    }



}
