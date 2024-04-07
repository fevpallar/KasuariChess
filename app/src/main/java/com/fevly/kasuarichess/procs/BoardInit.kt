/*==========================================
Author : Fevly Pallar
contact : fevly.pallar@gmail.com

board schema :
0,0 0,1 0,2 0,3 0,4 0,5 0,6 0,7
1,0 1,1 1,2 1,3 1,4 1,5 1,6 1,7
2,0 2,1 2,2 2,3 2,4 2,5 2,6 2,7
3,0 3,1 3,2 3,3 3,4 3,5 3,6 3,7
4,0 4,1 4,2 4,3 4,4 4,5 4,6 4,7
5,0 5,1 5,2 5,3 5,4 5,5 5,6 5,7
6,0 6,1 6,2 6,3 6,4 6,5 6,6 6,7
7,0 7,1 7,2 7,3 7,4 7,5 7,6 7,7
=========================================*/
package com.fevly.kasuarichess.procs;

import android.util.Log

class BoardInit() {
    val rows = 8
    val cols = 8


    // Initialize a 2D array with zeros
    val board = Array(rows) { Array(cols) { "" } }

    fun initializeBoard(): Array<Array<String>> {
        // Define the dimensions of the 2D array

        // bishop (mentri)

        board[7][1] = "wk";
        board[7][6] = "wk";
        board[0][1] = "bk";
        board[0][6] = "bk";

        // knight (kuda)

        board[7][2] = "wb";
        board[7][5] = "wb";
        board[0][5] = "bb";
        board[0][2] = "bb";

        // rook (benteng)
        board[7][0] = "wr";
        board[7][7] = "wr";
        board[0][0] = "br";
        board[0][7] = "br";

        // queen
        board[7][3] = "wq";
        board[0][3] = "bq";

        // kings
        board[7][4] = "wg";
        board[0][4] = "bg";

        // pawns
        for (i in 0 until cols) {
            board[6][i] = "wp"
            board[1][i] = "bp"
        }




        return board
    }

    fun printTwoDStringArrayInBox(array: Array<Array<String>>) {
        val rows = array.size
        val cols = if (rows > 0) array[0].size else 0

        val maxCellWidth = array.flatten().maxByOrNull { it.length }?.length ?: 0

        val stringBuilder = StringBuilder()
        stringBuilder.append("┌")
        for (j in 0 until cols) {
            stringBuilder.append("─".repeat(maxCellWidth + 2))
            if (j < cols - 1) stringBuilder.append("┬")
        }
        stringBuilder.append("┐\n")

        for (i in 0 until rows) {
            stringBuilder.append("│ ")
            for (j in 0 until cols) {
                val cellValue = array[i][j].padEnd(maxCellWidth)
                stringBuilder.append(cellValue)
                if (j < cols - 1) stringBuilder.append(" │ ")
            }
            stringBuilder.append(" │\n")

            if (i < rows - 1) {
                stringBuilder.append("├")
                for (j in 0 until cols) {
                    stringBuilder.append("─".repeat(maxCellWidth + 2))
                    if (j < cols - 1) stringBuilder.append("┼")
                }
                stringBuilder.append("┤\n")
            }
        }

        stringBuilder.append("└")
        for (j in 0 until cols) {
            stringBuilder.append("─".repeat(maxCellWidth + 2))
            if (j < cols - 1) stringBuilder.append("┴")
        }
        stringBuilder.append("┘")
    }


}
