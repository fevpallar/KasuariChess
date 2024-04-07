package com.fevly.kasuarichess.util;

import java.util.HashSet;

/*================================
Author : Fevly Pallar
contact : fevly.pallar@gmail.com
================================== */
public class EngineOutputFilter {
    public HashSet<String>  movesSet ;

    public  HashSet<String> filterOutputMoves(String input) {

        movesSet = new HashSet<>();
        String[] splittedMoves = input.trim().split("\n");

        for (String sp : splittedMoves) {
            if (sp.contains("pv")) {
                int tempIndex = sp.lastIndexOf("pv");

                /*===========070424====================
                Dua move dibawah ini itu persepsinya sama.
                tapi kelanjutannya linenya yg bikin beda

              1.  d7d5 d2d4 g8f6 c1f4
              2.  d7d5 d2d4 g8f6 c1f4 a3 a6

              Nah ini bagaimana cara supaya extract line yg nomor 2 saja.
                * ====================================*/
                    // +2 untuk exclude 'pv' itu sendiri
                    movesSet.add(sp.substring(tempIndex + 2).trim());

            }
        }
        return movesSet;
    }
}
