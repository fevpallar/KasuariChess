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

        Log.e("chess","latest: /n"+stringBuilder.toString())
    }


}
