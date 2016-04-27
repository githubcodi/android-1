package plus.prix.prix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        //JASON Data
        Button getData = (Button) findViewById(R.id.getservicedata);
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RestOperation().execute();
            }
        });

    }

    public class RestOperation extends AsyncTask<String,Void,Void> {

        String content;
        String error;
        ProgressDialog progressDialog = new ProgressDialog(StartActivity.this);
        String data = "";
        TextView serverDataReceived = (TextView) findViewById(R.id.serverDataReceived);
        TextView showParsedJSON = (TextView) findViewById(R.id.showParsedJSON);
        EditText userinput = (EditText) findViewById(R.id.userinput);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Please wait...");
            progressDialog.show();

            try {
                data += "&" + URLEncoder.encode("data","UTF-8") + "=" + userinput.getText();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            BufferedReader br = null;
            URL url;
            try {

                url = new URL("http://prix.plus/api/ping/");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                System.out.println(connection.getResponseCode());
                //connection.setDoOutput(true);
                //OutputStreamWriter outputStreamWr = new OutputStreamWriter(connection.getOutputStream());
                //outputStreamWr.write(data);
                //outputStreamWr.flush();

                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line = null;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                }

                content = sb.toString();

            } catch (MalformedURLException e) {
                error = e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                error = e.getMessage();
                e.printStackTrace();

            } /* finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            */

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            if(error!=null) {
                serverDataReceived.setText("Error "+error);
            } else {
                serverDataReceived.setText(content);

                String output = "";

                JSONObject jsonResponse;

                try {
                    jsonResponse = new JSONObject(content);
                    //JSONArray jsonArray = jsonResponse.optJSONArray("Beatles");
                    output = jsonResponse.getString("message");

                    /*
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        String name = child.getString("name");
                        String number = child.getString("number");
                        String time = child.getString("date_birth");

                        output = "Name =" + name + System.getProperty("line.separator") + number + System.getProperty("line.separator") + time + System.getProperty("line.separator");

                    }
                    */

                    showParsedJSON.setText(output);

                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        }
    }


}
