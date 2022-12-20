package ru.nazarov.fsplayer;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ListStations {

    private List<String> genres = new ArrayList<>(
            Arrays.asList("synth", "trance", "jazz", "easy", "dance", "ambient"));

    private int numberListByGenre = 0;
    private int stationNumber = 0;
    public boolean isFillStations = false;
    private int stationListSize = 0;

            File dir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS + "/playlists")));
    public List<List<Station>> listByGenre = new ArrayList<>();


    public void increaseStationsListNumber() {
        numberListByGenre++;
        numberListByGenre = checkSize(numberListByGenre, listByGenre.size());
    }
    public void increaseStationNumber() {
        stationNumber++;
        stationNumber = checkSize(stationNumber, stationListSize);
    }
    public void decreaseStationNumber() {
        if(stationNumber == 0) {
            stationNumber = listByGenre.get(numberListByGenre).size() - 1;
        } else {
            stationNumber--;
        }
    }
    public void toZeroStationNumber() {
        stationNumber=0;
    }
    private int checkSize(int arrayIndex, int listSize) {
        if (arrayIndex > listSize - 1) {
            arrayIndex = 0;
        }
        return arrayIndex;
    }

    public Station getStation() {
        stationListSize = listByGenre.get(numberListByGenre).size();
        return listByGenre.get(numberListByGenre).get(stationNumber);
    }

    public int getStationListSize() {
        return stationListSize;
    }

    public int getStationNumber() {
        return stationNumber + 1;
    }

    public boolean getIsFillStations() {
        return isFillStations;
    }

    public void fillListStations() {
        toZeroStationNumber();
        for(String genre : genres) {
            File fileWithPath = new File(dir + "/" + genre + ".txt");
            if (fileWithPath.exists()) {
                List<Station> listGenre = fileToPlaylist(fileWithPath, genre);
                isFillStations = true;
                listByGenre.add(listGenre);
            }
        }
    }

    public List<Station> fileToPlaylist(File fileWithPath, String fileName) {
        List<String> urlList = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(fileWithPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            urlList.add(scanner.nextLine());
        }
            List<Station> stationList = new ArrayList<>();
            for (String s : urlList) {
                s = s + " "; // if there is no words after url in file
                String[] str = s.split("\\s", 2);
                String ifIsNothingAfterUrl = " " + str[1];
                    stationList.add(new Station(str[0], ifIsNothingAfterUrl, fileName));
            }
        return stationList;
    }
}
