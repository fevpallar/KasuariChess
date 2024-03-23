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
class ConditionalPieceMoves {

    fun isKingInChecked(currXKing: Int, currYKing: Int, piece: String, board: Array<Array<String>>): Boolean {
        val directions = listOf(
            Pair(-1, 0),  // Up
            Pair(1, 0), // bawah
            Pair(0, 1),   // kanan (->)
            Pair(0, -1),  // kiri (<-)
            Pair(-1, 1),  // Atas-kanan
            Pair(-1, -1), // Atas-kiri
            Pair(1, 1),   // Bawah-kanan
            Pair(1, -1)   // Bawah-kiri
        )
        // untuk putih periksa cell atas-kiri dan atas-kanan kalau ada piece berwarna berlawanan maka skak
        if (piece[0] == 'w') {
            //check board[currXKing-1][currYKing-1]!="" untuk prevent index of out range excep.
            if (board[currXKing - 1][currYKing - 1] != "" && board[currXKing - 1][currYKing - 1][0] == 'b') {
                return true
            }
            if (board[currXKing - 1][currYKing - 1] != "" && board[currXKing - 1][currYKing + 1][0] == 'b') {
                return true
            }
        }
        // untuk hitam check cell bawah-kanan dan bawah-kiri kalau ada piece berwarna berlawanan maka skak
        if (piece[0] == 'b') {
            if (board[currXKing + 1][currYKing - 1][0] == 'w') {
                return true
            }
            if (board[currXKing + 1][currYKing + 1][0] == 'w') {
                return true
            }
        }

        // Cari semua moves sepanjang jalur King
        var indexOfCurrentPair = 0

            /*=================================================================================
              kalau disepanjang jalur King ada piece berlawanan warna yang bukan Pawn atau
              King maka kondisi sedang skak

              sedangkan untuk kasus Rook dan Bishop, agak sulit. Kalau rook lawan ada di jalur diagonal
              maka tidak skak, begitupun jika Bishop ada dijalur horizontal/vertical maka tidak skak

              Contoh dibawah ini kondisi tidak skak meskipun piece lawan (Rook atau Bishop) ada dijalur:
               ├────┼────┼────┼────┼────┼────┼────┼────┤
               │    │    │ br │    │    │    │    │    │
               ├────┼────┼────┼────┼────┼────┼────┼────┤
               │ wp │ wp │ wp │    │ wp │ wp │ wp │ wp │
               ├────┼────┼────┼────┼────┼────┼────┼────┤
               │ wr │ wk │ wb │ wq │ wg │ wb │ wk │ wr │
               └────┴────┴────┴────┴────┴────┴────┴────┘
                 ├────┼────┼────┼────┼────┼────┼────┼────┤
                 │    │    │    │    │ bb │    │    │    │
                 ├────┼────┼────┼────┼────┼────┼────┼────┤
                 │ wp │ wp │ wp │ wp │    │ wp │ wp │ wp │
                 ├────┼────┼────┼────┼────┼────┼────┼────┤
                 │ wr │ wk │ wb │ wq │ wg │ wb │ wk │ wr │
                 └────┴────┴────┴────┴────┴────┴────┴────┘
         Tapi dibawah ini skak :
                   ├────┼────┼────┼────┼────┼────┼────┼────┤
                   │    │    │    │ br │    │    │    │    │
                   ├────┼────┼────┼────┼────┼────┼────┼────┤
                   │ wp │ wp │ wp │    │ wp │ wp │ wp │ wp │
                   ├────┼────┼────┼────┼────┼────┼────┼────┤
                   │ wr │ wk │ wb │ wq │ wg │ wb │ wk │ wr │
                   └────┴────┴────┴────┴────┴────┴────┴────┘
                     ├────┼────┼────┼────┼────┼────┼────┼────┤
                     │    │    │ bb │    │    │    │    │    │
                     ├────┼────┼────┼────┼────┼────┼────┼────┤
                     │ wp │ wp │ wp │    │    │ wp │ wp │ wp │
                     ├────┼────┼────┼────┼────┼────┼────┼────┤
                     │ wr │ wk │ wb │ wq │ wg │ wb │ wk │ wr │
                     └────┴────┴────┴────┴────┴────┴────┴────┘
               Jadi harus detect jalur yg sedang ditelusuri ini diagonal atau tdk.
             ======================================================== */
        for ((di, dj) in directions) {
            if (indexOfCurrentPair > 3) {// jalur diagonal
                var row = currXKing + di
                var col = currYKing + dj
                var stopHere = false
                // untuk masing2 cell (yg possible move)
                println("$di $dj")
                while (row in 0..7 && col in 0..7 && !stopHere) {
                    // println("poss move is $row, $col")

                    // sewaktu dapat penghalang ( piece lain )
                    if (board[row][col] != "") {
                        //println("penghalang di $row $col")
                        if (piece[0] != board[row][col][0]
                            && (!board[row][col].contains("g") // King tidak bisa skak King
                                    && !board[row][col].contains("p")
                                    && !board[row][col].contains("r")
                                    )
                        ) {
                            return true // langsung berhenti, sudah pasti skak
                        }
                        else stopHere = true
                    } else {
                        row += di
                        col += dj
                    }
                }
                println("bla")
            } else { // jalur vertical/horizontal
                var row = currXKing + di
                var col = currYKing + dj
                var stopHere = false
                // untuk masing2 cell (yg possible move)
                println("$di $dj")
                while (row in 0..7 && col in 0..7 && !stopHere) {
                    // println("poss move is $row, $col")
                    // sewaktu dapat penghalang ( piece lain )
                    if (board[row][col] != "") {
                        //println("penghalang di $row $col")
                        if (piece[0] != board[row][col][0]
                            && (!board[row][col].contains("g") // King tidak bisa skak King
                                    && !board[row][col].contains("p")
                                    && !board[row][col].contains("b")
                                    )
                        ) {
                            return true // langsung berhenti, sudah pasti skak
                        }
                        else stopHere = true
                    } else {
                        row += di
                        col += dj
                    }
                }
                println("bla")
            }
            indexOfCurrentPair++
        }
        return false
    }

    fun getInPassingMoves( // enpassant (in-passing, Pawn nangkap secara diagonal)
        currRow: Int,
        currCol: Int,
        piece: String,
        previousPiece: String,
        board: Array<Array<String>>

    ): MutableList<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()

        // diagonal kanan (relative ke piece putih/hitam)
        var kordinatXKanan = 0;
        var kordinatYKanan = 0;

        // diagonal kiri (relative ke piece putih/hitam)
        var kordinatXKiri = 0;
        var kordinatYKiri = 0;

        /*============================================================================
          Note :
           enpassant (in-passing, Pawn nangkap secara diagonal) hanya boleh sekali, setelah
           Pawn(hanya boleh Pawn) dari sisi berlawanan move.
        ====================================================================================*/

        // kalau piece yg mau maju warna putih dan piece dari hitam adalah Pawn
        if (piece[0] == 'w' && previousPiece.contains("p")) {

            kordinatXKanan = currRow - 1;
            kordinatYKanan = currCol + 1;
            kordinatXKiri = currRow - 1;
            kordinatYKiri = currCol - 1;

            /*===============================================================
             Note: saat enpassent terjadi bisa terdapat dua cell (di 2 diagonal) untuk possible movenya
             misalkan di 'xl' maupun di 'xr', dimana 0 adalah Pawn yg mau gerak

                     ────┼────┼────┼────┼────┼────┼────┼────┤
                    │    │    │ xl │    │ xr │    │    │    │
                    ├────┼────┼────┼────┼────┼────┼────┼────┤
                    │    │    │    │  0 │    │    │    │    │
                    ├────┼────┼────┼────┼────┼────┼────┼────┤
                    │    │    │    │    │    │    │    │    │
                    ├────┼────┼────┼────┼────┼────┼────┼────┤
             =================================================================*/
            if (board[kordinatXKanan][kordinatYKanan] == "" && board[kordinatXKiri][kordinatYKiri] != "")
                possibleMoves.add(Pair(kordinatXKanan, kordinatYKanan))
            if (board[kordinatXKanan][kordinatYKanan] != "" && board[kordinatXKiri][kordinatYKiri] == "")
                possibleMoves.add(Pair(kordinatXKiri, kordinatYKiri))
            if (board[kordinatXKanan][kordinatYKanan] == "" && board[kordinatXKiri][kordinatYKiri] == "") {
                possibleMoves.add(Pair(kordinatXKiri, kordinatYKiri))
                possibleMoves.add(Pair(kordinatXKanan, kordinatYKiri))
            }

        }
        // kalau piece yg mau maju warna hitam dan piece dari putih  adalah Pawn
        if (piece[0] == 'b' && previousPiece.contains("p")) {

            kordinatXKanan = currRow + 1;
            kordinatYKanan = currCol + 1;
            kordinatXKiri = currRow + 1;
            kordinatYKiri = currCol - 1;

            if (board[kordinatXKanan][kordinatYKanan] == "")
                possibleMoves.add(Pair(kordinatXKanan, kordinatYKanan))
            if (board[kordinatXKiri][kordinatYKiri] == "")
                possibleMoves.add(Pair(kordinatXKiri, kordinatYKiri))

        }

        return possibleMoves

    }
}