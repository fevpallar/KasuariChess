package com.fevly.kasuarichess.stockengine;

/*================================
Author : Fevly Pallar
contact : fevly.pallar@gmail.com
================================== */

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;


import com.fevly.engine.StockfishEngine;
import com.fevly.engine.StringOutListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockfishFeeder implements StringOutListener {
    public static String allMoves;

    StockfishEngine so = new StockfishEngine();

    /*=========================================
 extract moves dikategori depth

 sample

info depth 8 seldepth 6 multipv 1 score cp -21 nodes 4939 nps 65853 hashfull 1 tbhits 0 time 75 pv c7c5 g1f3 b8c6 b1c3 e7e5
info depth 8 seldepth 6 multipv 1 score cp -21 nodes 4939 nps 65853 hashfull 1 tbhits 0 time 75 pv c7c5 g1f3 b8c6 b1c3 e7e5
 =======================================*/
    public static void cariDiKedalaman(String input, int targetDepth) {
        Pattern pattern = Pattern.compile("info depth " + targetDepth + " .*");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            Log.d("kasuarichess", matcher.group(0));
        }
    }

    public StockfishFeeder(Context context) {

        String command = "isready"; //or other command
        command += "\n";
        command += "position startpos moves e2e4";
        command += "\n";
        command += "go infinite";
        command += "\n";

        /*====================================================
        02042024
         di StockfishEngine.kt pakai observer pattern,
         karena disana variabel output diupdate secara continues
         di dalam background thread yg terpisah.
         Kalau tdk ngepass instance dari class ini
         outputnya gak ada.
        ======================================== */
        so.tryIt(context, this, command);
        allMoves = so.getStringOut();
    }


    @Override
    public void onStringOutUpdated(@Nullable String stringOut) {
        cariDiKedalaman(stringOut, 8);
    }
}
