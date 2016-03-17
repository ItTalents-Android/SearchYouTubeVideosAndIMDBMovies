package com.youtubeimdb;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ImdbActivity extends AppCompatActivity {

    EditText search;
    TextView title;
    TextView descr;
    TextView link;
    ImageView poster;
    Button btnSearch;
    Button goToYoutube;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imdb);


        search = (EditText) findViewById(R.id.search_movie_name);

        title = (TextView) findViewById(R.id.movie_title);
        descr = (TextView) findViewById(R.id.movie_descr);
        link = (TextView) findViewById(R.id.link_movie);

        poster = (ImageView) findViewById(R.id.movie_poster);

        btnSearch = (Button) findViewById(R.id.btn_search_movie);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = search.getText().toString();
                new ImdbTask().execute(searchString);
            }
        });

        goToYoutube = (Button) findViewById(R.id.go_to_youtube);
        goToYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ImdbActivity.this,MainActivity.class);
                startActivity(i);

            }
        });

    }

    class ImdbTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            String address = "http://www.omdbapi.com/?t="+params[0]+"&y=&plot=short&r=json";
            String response = "";
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                Scanner sc = new Scanner(connection.getInputStream());
                StringBuilder body = new StringBuilder();
                while(sc.hasNextLine()){
                    body.append(sc.nextLine());
                }
                response = String.valueOf(body);
                //return the data
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
                title.setText(new JSONObject(s).getString("Title"));
                descr.setText(new JSONObject(s).getString("Plot"));
                link.setText("http://www.imdb.com/title/"+new JSONObject(s).getString("imdbID"));
                String imageUrl = new JSONObject(s).getString("Poster");
                if (!(imageUrl.equals("N/A"))) {
                    new ImageLoaderTask().execute(imageUrl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ImageLoaderTask extends AsyncTask<String,Void,Drawable >{

        @Override
        protected Drawable doInBackground(String... params) {
            URL posterUrl = null;
            Drawable posterStreamed = null;
            try {
                posterUrl = new URL(params[0]);
                posterStreamed = Drawable.createFromStream(posterUrl.openStream(),"src");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return posterStreamed;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            poster.setImageDrawable(drawable);
        }
    }
}
