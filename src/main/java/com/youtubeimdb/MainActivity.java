package com.youtubeimdb;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    EditText search;
    TextView title;
    TextView descr;
    TextView link;
    ImageView thumb;
    Button btnSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        search = (EditText) findViewById(R.id.search_name);

        title = (TextView) findViewById(R.id.video_title);
        descr = (TextView) findViewById(R.id.descr);
        link = (TextView) findViewById(R.id.link);

        thumb = (ImageView) findViewById(R.id.thumb);

        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = search.getText().toString();
                new YoutubeTask().execute(searchString);
            }
        });
    }

    class YoutubeTask extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... params) {
            String address = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="+params[0]+"&type=video&key=AIzaSyCHvVGds446ASZgXZFCM_yRdJkQdlmNVgI";
            String response = "";
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                Scanner sc = new Scanner(connection.getInputStream());
                StringBuilder body = new StringBuilder();
                while (sc.hasNextLine()){
                    body.append(sc.nextLine());
                }
                response = String.valueOf(body);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject response= new JSONObject(s);
                JSONArray items = response.getJSONArray("items");
                JSONObject clipStats = items.getJSONObject(0);

                JSONObject clipId = clipStats.getJSONObject("id");

                JSONObject clipInfos =  clipStats.getJSONObject("snippet");

                title.setText(clipInfos.getString("title"));
                descr.setText(clipInfos.getString("description"));
                link.setText("https://www.youtube.com/watch?v=" + clipId.getString("videoId"));

                JSONObject thumbs = clipInfos.getJSONObject("thumbnails");

                JSONObject thumbHigh = thumbs.getJSONObject("high");

                String thumbUrl =thumbHigh.getString("url");

                new ImageLoaderTask().execute(thumbUrl);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
    class ImageLoaderTask extends AsyncTask<String,Void,Drawable>{

        @Override
        protected Drawable doInBackground(String... params) {
            URL posterUrl = null;
            Drawable posterStreamed = null;
            try {
                posterUrl = new URL(params[0]);
                posterStreamed = Drawable.createFromStream(posterUrl.openStream(), "src");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return posterStreamed;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            thumb.setImageDrawable(drawable);
        }
    }

}
