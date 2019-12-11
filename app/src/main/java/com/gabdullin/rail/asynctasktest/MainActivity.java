package com.gabdullin.rail.asynctasktest;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String url = "https://yandex.ru/pogoda/";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPage();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadPage() {
        new AsyncTask<String, Integer, String>(){

            @Override
            protected String doInBackground(String... strings) {
                try {
                    URL uri = new URL(url);

                    //Усыпляем потоки на 2 секунды просто чтобы проверить как работает прогресс бар
                    Thread.sleep(2000);
                    publishProgress(40);
                    HttpsURLConnection connection = (HttpsURLConnection) uri.openConnection();
                    Thread.sleep(2000);
                    publishProgress(60);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.connect();
                    Thread.sleep(2000);
                    publishProgress(100);
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        return in.lines().collect(Collectors.joining("\n"));
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                //Поменял только адрес сайта
                if (result != null) ((WebView)findViewById(R.id.page)).loadData(result, "text/html; charset=utf-8", "utf-8" );
                super.onPostExecute(result);
            }

            @Override
            protected void onProgressUpdate(Integer[] progress) {
                progressBar.setProgress(progress[0]);
                super.onProgressUpdate(progress);
            }
        }.execute(url);
    }
}
