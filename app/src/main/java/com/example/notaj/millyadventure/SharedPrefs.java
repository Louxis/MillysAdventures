package com.example.notaj.millyadventure;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by notaj on 10/12/2017.
 */

public class SharedPrefs {

    public static final String MY_PREFS_NAME = "Information";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public SharedPrefs(AppCompatActivity activity){

        settings = activity.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();


    }

    public void initScoresAndPositions(){


        if(settings.getInt("BunnyScore", 0) == 0){

            editor.putInt("BunnyScore", 0);
            editor.putInt("SquirrelScore", 0);
            editor.putInt("SheepScore", 0);
            editor.putInt("FlowerScore", 0);
            editor.putInt("BirdScore", 0);

        }

        String[] list;
        list = settings.getString("userPositionInfo", "0.0,0.0,ERROR").split(",");




        if(list[0].equals("0.0") && list[1].equals("0.0")){
            editor.putString("userPositionInfo","38.5223503,-8.8383939,Player");
        }


        /*editor.putString("bunnyPositionInfo","0.0,0.0,Bunny");
        editor.putString("squirrelPositionInfo","0.0,0.0,Squirrel");
        editor.putString("sheepPositionInfo","0.0,0.0,Sheep");
        editor.putString("flowerPositionInfo","0.0,0.0,Flower");
        editor.putString("birdPositionInfo","0.0,0.0,Bird");*/



        if (settings.getString("GPS", "") == "") {
            editor.putString("GPS", "true");
        }



        editor.commit();
    }

    public SharedPreferences getSharedPrefs(){
        return settings;
    }

    public SharedPreferences.Editor getSharedPrefsEditor(){
        return editor;
    }


}
