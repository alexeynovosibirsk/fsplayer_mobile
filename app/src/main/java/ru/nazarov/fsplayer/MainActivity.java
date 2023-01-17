package ru.nazarov.fsplayer;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
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
    TextView amount;
    TextView stationsAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
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

//        askForPermissions();
// TextViews links
        player = new ExoPlayer.Builder(this).build();
        amount = findViewById(R.id.playlistamountTextView);
        stationsAmount = findViewById(R.id.stationsamountTextView);
        textView = findViewById(R.id.statusTextView);
        updatePlaylists();
        if(ls.isFillStations) {
            play();
        }
        // TODO: FInd if I need to use the notification? Make app in foreground
//        Notification notification = new Notification(R.drawable.icon, getText(R.string.ticker_text),
//                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(this, getText(R.string.notification_title),
//                getText(R.string.notification_message), pendingIntent);
//        startForeground(ONGOING_NOTIFICATION_ID, notification);
        //-----------------
        ImageButton updatePlaylists = findViewById(R.id.update_playlistsButton);
        updatePlaylists.setOnClickListener(view -> {
            updatePlaylists();
        });

//        ImageButton themeChangerBtn = findViewById(R.id.theme_changer);
//        themeChangerBtn.setOnClickListener(view -> {
//                Utils.changeToTheme(MainActivity.this, 1);
//        });

        ImageButton genre = findViewById(R.id.genreButton);
        genre.setOnClickListener(view -> {
                ls.increaseStationsListNumber();
                ls.toZeroStationNumber();
                play();
        });

        ImageButton genreBack = findViewById(R.id.genreButtonBack);
        genreBack.setOnClickListener(view -> {
            ls.decreaseStationsListNumber();
            ls.toZeroStationNumber();
            play();
        });

        ImageButton nextStation = findViewById(R.id.next_stationButton);
        nextStation.setOnClickListener(view -> {
                ls.increaseStationNumber();
                play();
        });

        ImageButton prevStation = findViewById(R.id.prev_stationButton);
        prevStation.setOnClickListener(view -> {
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
           amount.setText("Playlists: " + ls.getPlaylistsAmount());
           stationsAmount.setText("Stations: " + ls.getStationsAmount());
           textView.setText("at: " + getTime());
       } else {
           textView.setText("The Playlists are not downloaded! ");
       }
   }

   private String getTime() {
       Calendar c = GregorianCalendar.getInstance();
        return c.getTime().toString();
   }

    private void stationInfo() {
        genre = findViewById(R.id.genreTextView);
        stationName = findViewById(R.id.stationTextView);
        url = findViewById(R.id.urlTextView);
        Station station = ls.getStation();
        genre.setText(ls.getStationListSize() + "  - " + station.getGenre().toUpperCase() +
                        " -  " + ls.getStationNumber());
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