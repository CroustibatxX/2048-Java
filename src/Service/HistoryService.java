package Service;

import modele.Case;

import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import com.google.gson.Gson;
import modele.HistoryData;

public class HistoryService {
    private Preferences prefs;
    private int playIndex = 0;

    private Gson gson;


    public HistoryService() {
        prefs = Preferences.userRoot().node(this.getClass().getName());
        gson = new Gson();
    }


    public void savePlay(Case[][] tabCases, int score){
        playIndex++;
        String tab = getStringArray(tabCases);
        tab += score;
        prefs.put(Integer.toString(playIndex), tab);
    }

    public HistoryData back(){
        if(playIndex > 1){
            String tab = prefs.get(Integer.toString(playIndex-1),"");
            prefs.remove(Integer.toString(playIndex));
            playIndex --;
            return getArrayFromString(tab);
        }
        return null;
    }


    private String getStringArray(Case[][] tabCases){
        String tab = "";
        for (int i = 0; i < tabCases.length; i++) {
            int[] row = new int[tabCases.length];
            for (int j = 0; j < tabCases.length; j++) {
                row[j] = tabCases[i][j].getValeur();
            }
            tab += gson.toJson(row) + "-";
        }
        return tab;
    }

    public void clear() {
        try {
            prefs.clear();
            playIndex = 0;
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }


    private HistoryData getArrayFromString(String tab){
        String[] stringTab = tab.split("-");
        int score = Integer.parseInt(stringTab[stringTab.length-1]);
        stringTab = Arrays.copyOf(stringTab, stringTab.length-1);

        int[][] tabCases = new int[stringTab.length][stringTab.length];
        for (int i = 0; i < stringTab.length; i++) {
            tabCases[i] = gson.fromJson(stringTab[i],int[].class);
        }

        return new HistoryData(tabCases, score);
    }



}
