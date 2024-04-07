package com.fevly.kasuarichess.util;/*================================
Author : Fevly Pallar
contact : fevly.pallar@gmail.com
================================== */
import com.fevly.kasuarichess.procs.BoardInit;

import java.util.ArrayList;
import java.util.List;

public class FenGen {


    /*====================07042024===================
    Uji coba dengan :

    ┌────┬────┬────┬────┬────┬────┬────┬────┐
    │ br │    │ bb │ bg │    │    │    │ br │
    ├────┼────┼────┼────┼────┼────┼────┼────┤
    │ bp │    │    │ bp │ wb │ bp │ wk │ bp │
    ├────┼────┼────┼────┼────┼────┼────┼────┤
    │ bk │    │    │    │    │ bk │    │    │
    ├────┼────┼────┼────┼────┼────┼────┼────┤
    │    │ bp │    │ wk │ wp │    │    │ wp │
    ├────┼────┼────┼────┼────┼────┼────┼────┤
    │    │    │    │    │    │    │ wp │    │
    ├────┼────┼────┼────┼────┼────┼────┼────┤
    │    │    │    │ wp │    │    │    │    │
    ├────┼────┼────┼────┼────┼────┼────┼────┤
    │ wp │    │ wp │    │ wg │    │    │    │
    ├────┼────┼────┼────┼────┼────┼────┼────┤
    │ bq │    │    │    │    │    │ bb │    │
    └────┴────┴────┴────┴────┴────┴────┴────┘
 parameter:

        board[0][0] = "br";
        board[0][2] = "bb";
        board[0][3] = "bg";
        board[0][7] = "br";
        board[1][0] = "bp";
        board[1][3] = "bp";
        board[1][4] = "wb";
        board[1][5] = "bp";
        board[1][6] = "wk";
        board[1][7] = "bp";
        board[2][0] = "bk";
        board[2][5] = "bk";
        board[3][1] = "bp";
        board[3][3] = "wk";
        board[3][4] = "wp";
        board[3][7] = "wp";
        board[4][6] = "wp";
        board[5][3] = "wp";
        board[6][0] = "wp";
        board[6][2] = "wp";
        board[6][4] = "wg";
        board[7][0] = "bq";
        board[7][6] = "bb";

        hasilnya = r1bk3r/p2pBpNp/n4n2/1p1NP2P/6P1/3P4/P1P1K3/q5b1
     =================================================*/
    public static String transform(String[][] input) {
        String separator = "";
        String fen = "";
        List<String> movesPerRow;

        for (int i = 0; i < 8; i++) {
            movesPerRow = new ArrayList<String>();
            for (int j = 0; j < 8; j++) {
                String currPiece = input[i][j];

                // kalau putih
                if (currPiece.length() > 0 && currPiece.charAt(0) == 'w') {
                    switch (currPiece.charAt(1)) {
                        case 'r':
                            movesPerRow.add("R");
                            break;
                        case 'b':
                            movesPerRow.add("B");
                            break;
                        case 'k':
                            movesPerRow.add("N");
                            break;
                        case 'q':
                            movesPerRow.add("Q");
                            break;
                        case 'g':
                            movesPerRow.add("K");
                            break;
                        case 'p':
                            movesPerRow.add("P");
                            break;

                    }
                }
                // kalau hitam
                else if (currPiece.length() > 0 && currPiece.charAt(0) == 'b') {
                    switch (currPiece.charAt(1)) {
                        case 'r':
                            movesPerRow.add("r");
                            break;
                        case 'b':
                            movesPerRow.add("b");
                            break;
                        case 'k':
                            movesPerRow.add("n");
                            break;
                        case 'q':
                            movesPerRow.add("q");
                            break;
                        case 'g':
                            movesPerRow.add("k");
                            break;
                        case 'p':
                            movesPerRow.add("p");
                            break;
                    }
                } else movesPerRow.add(""); // cell kosong

            }// j


            for (int x = 0; x < movesPerRow.size(); x++) {
                if (movesPerRow.get(x).length() > 0){
                    fen += movesPerRow.get(x) .trim();
                }

                if (movesPerRow.get(x) .length() == 0){
                    fen += "*";
                }
            }


            if (i != 7) {  // trim '/' yg terakhir
                fen += separator;
            }

    } // i


        return filterFenPerRow(fen);
}
    public static String filterFenPerRow(String fen) {
        StringBuilder transformedFen = new StringBuilder();
        int count = 0;

        for (char c : fen.toCharArray()) {
            if (c == '*') {
                count++;
            } else {
                if (count > 0) {
                    transformedFen.append(count);
                    count = 0;
                }
                transformedFen.append(c);
            }
        }

        if (count > 0) {
            transformedFen.append(count);
        }

        return transformedFen.toString();
    }

}
