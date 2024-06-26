package com.fevly.kasuarichess.stockengine;

/*================================
Author : Fevly Pallar
contact : fevly.pallar@gmail.com
================================== */

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fevly.kasuarichess.R;
import com.fevly.kasuarichess.depend.StockfishEngine;
import com.fevly.kasuarichess.depend.StringOutListener;
import com.fevly.kasuarichess.fragments.AnalysisFragment;
import com.fevly.kasuarichess.util.EngineOutputFilter;
import com.fevly.kasuarichess.util.FenGen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StockfishFeeder implements StringOutListener {
    public static String[][] board;
    public static HashSet<String> filteredOutptMoves = new HashSet<>(); // lempar ke fragment analisis
    private static Handler uiThreadHandler;
    static Set holdmove = new HashSet<String>();
    static HashSet allMoves = new HashSet<String>();
    String allLines = "";
    static int counter = 0;

    @Override
    public void onStringOutUpdated(@Nullable String stringOut) {
        counter++;// ini ikut moves, per 1 piece move
        /*==============================
        Note: 08042024

       Sementara:

       parameter engine berhenti scan setelah 10 lines per move

       Kedalaman yg didapt dari 15 lines yaitu

        - max. (+-depth 11 dan variasi +-3) untuk Snapdragon 845.

      sampel setelah  1.Nc3 Nc6 :

                        info string NNUE evaluation using nn-b1a57edbea57.nnue
                        info string NNUE evaluation using nn-baff1ede1f90.nnue
                        info depth 1 seldepth 2 multipv 1 score cp 0 nodes 20 nps 6666 hashfull 0 tbhits 0 time 3 pv d2d4
                        info depth 2 seldepth 2 multipv 1 score cp 12 nodes 44 nps 14666 hashfull 0 tbhits 0 time 3 pv a2a3
                        info depth 3 seldepth 2 multipv 1 score cp 50 nodes 69 nps 13800 hashfull 0 tbhits 0 time 5 pv d2d4
                        info depth 4 seldepth 3 multipv 1 score cp 50 nodes 92 nps 18400 hashfull 0 tbhits 0 time 5 pv d2d4
                        info depth 5 seldepth 3 multipv 1 score cp 50 nodes 123 nps 17571 hashfull 0 tbhits 0 time 7 pv d2d4 a7a6
                        info depth 6 seldepth 8 multipv 1 score cp 31 nodes 1795 nps 19725 hashfull 0 tbhits 0 time 91 pv d2d4 d7d5 b1c3 c8f5
                        info depth 7 seldepth 8 multipv 1 score cp 28 nodes 2051 nps 20107 hashfull 0 tbhits 0 time 102 pv d2d4 d7d5 c2c4 d5c4
                        info depth 8 seldepth 8 multipv 1 score cp 31 nodes 4080 nps 20400 hashfull 1 tbhits 0 time 200 pv e2e4 e7e6 b1c3 c7c5 g1f3
                        info depth 9 seldepth 9 multipv 1 score cp 35 nodes 5727 nps 21289 hashfull 1 tbhits 0 time 269 pv e2e4 e7e5 g1f3 d7d5 e4d5 e5e4
                        info depth 10 seldepth 12 multipv 1 score cp 37 nodes 8227 nps 21707 hashfull 2 tbhits 0 time 379 pv e2e4 e7e5 d2d4 e5d4 d1d4 b8c6 d4e3
                        info depth 11 seldepth 14 multipv 1 score cp 41 nodes 16493 nps 21672 hashfull 5 tbhits 0 time 761 pv e2e4 c7c5 g1f3 d7d6 b1c3 g8f6 d2d4

     ==================================*/
        allLines = stringOut;
        allMoves = new EngineOutputFilter().filterOutputMoves(allLines);

        mapMoveAndItsConseq = new LinkedHashMap<String, String>();// map response-move-pertama & konsekutif movesnya
        String tempLine = "";
        for (Object line : allMoves) {
            String splitOfEachLineMove[] = line.toString().split(" ");

            if (mapMoveAndItsConseq.containsKey(splitOfEachLineMove[0])) {
                String theValue = mapMoveAndItsConseq.get(splitOfEachLineMove[0]);
                if (line.toString().length() > theValue.length()) {
                    mapMoveAndItsConseq.put(splitOfEachLineMove[0], line.toString());
                }
            } else mapMoveAndItsConseq.put(splitOfEachLineMove[0], line.toString());


        }


        moveKeys = new ArrayList<String>();

        for (Map.Entry<String, String> entry : mapMoveAndItsConseq.entrySet()) {
            moveKeys.add(entry.getKey().trim());
            String tValue = entry.getValue();
            tempLine += tValue + "\n";
        }

        String finalTempLine = tempLine;
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                text.setText(finalTempLine);
            }
        });
        allMoves.clear();

        for (String mk : moveKeys) {
            int bSCol = convertToColumnNumber(mk.charAt(mk.length() - 4));
            int bSRow = (8) - Integer.valueOf(String.valueOf(mk.charAt(mk.length() - 3)));

            int bDCol = convertToColumnNumber(mk.charAt(mk.length() - 2));
            int bDRow = (8) - Integer.valueOf(String.valueOf(mk.charAt(mk.length() - 1)));

            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (AnalysisFragment.BoardHolder.board[bDRow][bDCol] == "") {
                        AnalysisFragment.StaticImageViewHolder.imageViewArray[bDRow][bDCol].setImageResource(R.drawable.herearrow);
                        AnalysisFragment.StaticImageViewHolder.imageViewArray[bDRow][bDCol].setBackgroundColor(Color.parseColor("#dffc4c"));
                        AnalysisFragment.StaticImageViewHolder.imageViewArray[bSRow][bSCol].setBackgroundColor(Color.parseColor("#dffc4c"));
                    }
                    else
                        AnalysisFragment.StaticImageViewHolder.imageViewArray[bDRow][bDCol].setBackgroundColor(Color.parseColor("#80F79123"));
                }
            });
        }
    }
    public void setParams(Context context, Handler uiThreadHandler, String board[][],
                          Character toMove, EditText text) {

        Long start = System.currentTimeMillis();
        String boardToFen = FenGen.transform(board) + " " + toMove;
        Log.d("duration", "FenGen.transform " + (System.currentTimeMillis() - start));
        System.out.println("FEN: " + boardToFen);

        String command = "ucinewgame";
        command += "\n";
        command += "isready";
        command += "\n";
//        command += "setoption name Clear Hash";
//        command += "\n";
        command += "position fen " + boardToFen.trim();
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
        Long start2 = System.currentTimeMillis();
        so.tryIt(context, this, command);
        Log.d("duration", "so.tryIt " + (System.currentTimeMillis() - start2));
    }

    public StockfishFeeder(Context context, Handler uiThreadHandler, String board[][],
                           Character toMove, EditText text) {
        this.board = board;
        this.text = text;
        this.uiThreadHandler = uiThreadHandler;
        this.context = context;
        so = new StockfishEngine();
        setParams(context, uiThreadHandler, board, toMove, text);

        generateNotasi();
    }


    public static void generateNotasi() {
        String[][] chessBoard = new String[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                chessBoard[row][col] = (char) ('a' + col) + Integer.toString(8 - row);
            }
        }

    /*    for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                System.out.print("| " + chessBoard[row][col] + " ");
            }
            System.out.println("|");
            System.out.println("-------------------------------");
        }*/
        arrayNotasi = chessBoard;


    }

    static int convertToColumnNumber(char columnLetter) {
        return columnLetter - 'a';
    }

    public static EditText text;
    private static StockfishEngine so;
    private static String arrayNotasi[][] = new String[8][8];
    private Map<String, String> mapMoveAndItsConseq = new LinkedHashMap<>();

    private List<String> moveKeys = new ArrayList<>(); // pakai untuk warnai board sesuai engine output
    private Context context;
}
