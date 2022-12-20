package ru.nazarov.fsplayer;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    ExoPlayer player;
    ListStations ls = new ListStations();
    TextView textView;
    TextView genre;
    TextView stationName;
    TextView url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Handle with SELinux
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//Perms for storage
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    {permission}, 123);
        }

        askForPermissions();
        player = new ExoPlayer.Builder(this).build();
        textView = findViewById(R.id.textView);
        updatePlaylists();
        if(ls.isFillStations) {
            play();
        }

        ImageButton updatePlaylists = findViewById(R.id.update_playlists);
        updatePlaylists.setOnClickListener(view -> {
            updatePlaylists();
//            textView.setText(getTime());
        });

        ImageButton genre = findViewById(R.id.genre);
        genre.setOnClickListener(view -> {
                ls.increaseStationsListNumber();
                ls.toZeroStationNumber();
                play();
        });

        ImageButton nextStation = findViewById(R.id.next_station);
        nextStation.setOnClickListener(view -> {
                ls.increaseStationNumber();
                play();
        });

        ImageButton prevStation = findViewById(R.id.prev_station);
        prevStation.setOnClickListener(view -> {
        //    new DownloadFromURL().execute("https://github.com/alexeynovosibirsk/Files_RadioPlayerPlaylists/blob/master/trance.txt");
            ls.decreaseStationNumber();
             play();
        });
    }

    public void play() {
        try {
            MediaItem item = new MediaItem.Builder()
                    .setUri(ls.getStation().getUrl())
                    .build();

            player.setMediaItem(item);
            player.prepare();
            player.play();
            stationInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                startActivity(intent);
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }
    }

   public void updatePlaylists() {

       ls.fillListStations();
       if (ls.getIsFillStations()) {
//           String time = c.getTime().toString().replace("GMT", "");
           textView.setText("LISTS UPDATED AT: " + getTime());
       } else {
           textView.setText("The Playlists are not downloaded! ");
       }
   }

   private String getTime() {
       Calendar c = GregorianCalendar.getInstance();
        return c.getTime().toString();
   }

    private void stationInfo() {
        genre = findViewById(R.id.textView2);
        stationName = findViewById(R.id.textView3);
        url = findViewById(R.id.textView4);
        Station station = ls.getStation();
        genre.setText("Genre: " + station.getGenre().toUpperCase() + " >> " + ls.getStationNumber()
                + "/" +        ls.getStationListSize());
        stationName.setText("Station: " + station.getName());
        url.setText("Url: " + station.getUrl());
    }
    private void releaseResources() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
    }

    @Override
    public void onBackPressed() {
        releaseResources();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        releaseResources();
        super.onDestroy();
    }

class DownloadFromURL extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... fileUrl) {
        int count;
        try {
            URL url = new URL(fileUrl[0]);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            // show progress bar 0-100%
            int fileLength = urlConnection.getContentLength();
            InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
            OutputStream outputStream = new FileOutputStream("/sdcard/downloadedfile1.txt");

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = inputStream.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / fileLength));
                outputStream.write(data, 0, count);
            }
            // flushing output
            outputStream.flush();
            // closing streams
            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }
}}