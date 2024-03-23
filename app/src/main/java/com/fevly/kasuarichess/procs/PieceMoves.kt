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
class PieceMoves(_board: Array<Array<String>>) {
    var board = Array(8) { Array(8) { "" } }

    init {
        board = _board
    }

    var previousPiece = ""

    fun getPossiblePawnMoves(
        i: Int,
        j: Int,
        piece: String,
        previousPiece: String
    ): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        val conditionalPieceMove = ConditionalPieceMoves()
        val inPassingMoves =
            conditionalPieceMove.getInPassingMoves(i, j, piece, previousPiece, board)
        /*===========================================================v
            Note:
          - Hanya pawn satu-satunya yg tidak bisa melangkah mundur dalam catur,
           alhasil ada 2 vektor untuk masing2 pawn hitam dan putih
          - Pawn bisa melangkah 2 box kalau starting cell nya ada di row 1 (untuk hitam) dan 6 (untuk putih)
            │    │    │    │    │    │    │    │    │
            ├────┼────┼────┼────┼────┼────┼────┼────┤
            │    │    │    │ wp │    │    │    │    │
            ├────┼────┼────┼────┼────┼────┼────┼────┤
            │    │    │    │    │    │    │    │    │
            ├────┼────┼────┼────┼────┼────┼────┼────┤
            │ wp │ wp │ wp │    │ wp │ wp │ wp │ wp │
            ├────┼────┼────┼────┼────┼────┼────┼────┤
            │ wr │ wk │ wb │ wq │ wg │ wb │ wk │ wr │
            └────┴────┴────┴────┴────┴────┴────┴────┘
          ============================================================*/
        var directions = emptyList<Pair<Int, Int>>()
        if (piece[0] == 'w') {
            directions = listOf(
                Pair(-1, 0), // atas (up)
            );
        }
        if (piece[0] == 'b') { // pawn hitam
            directions = listOf(
                Pair(1, 0), // turun (down)
            )
        }
        if (i != 6 && i != 1)
            possibleMoves.add(Pair((i + directions[0].first), (j + directions[0].second)))
        else {
            if (i == 6) {
                possibleMoves.add(Pair((i + directions[0].first), (j + directions[0].second)))
                // melangkah 2 box (cell) diikutkan ke possible moves
                possibleMoves.add(Pair((i + (-2)), (j + 0)))
            }
            if (i == 1) {
                possibleMoves.add(Pair((i + directions[0].first), (j + directions[0].second)))
                // melangkah 2 box (cell) diikutkan ke possible moves
                possibleMoves.add(Pair((i + (2)), (j + 0)))
            }
        }
        // check kalau ada list inpassing moves yg bisa dilakukan
        // , maka join listnya (possible moves + possible inpassing moves)
        if (inPassingMoves.size > 0)
            possibleMoves.addAll(inPassingMoves)
        return possibleMoves
    }

    fun movePawn(
        currX: Int,
        currY: Int,
        destX: Int,
        destY: Int,
        piece: String,
        previousPiece: String,
        board: Array<Array<String>>,
        ischeck: Boolean
    ): Array<Array<String>> {
        val possMoves: List<Pair<Int, Int>> =
            getPossiblePawnMoves(currX, currY, piece, previousPiece)
        possMoves.forEachIndexed { index, pair ->
//            val i = index / 8
//            val j = index % 8
            // Access the elements of the pair
            val firstElement: Int = pair.first
            val secondElement: Int = pair.second
            println("test $firstElement $secondElement")
            /*    - memenuhi kalau cell tujuan:
                  - ada piece warna berlawanan namun bukan king
                  - tidak dalam keadaan check/skak */
            if (firstElement == destX && secondElement == destY) {
                // for white piece
                if (ischeck == false && piece[0] == 'w'
                    || board[destX][destY].contains("b")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
                // for black piece
                if (ischeck == false && piece[0] == 'b'
                    || board[destX][destY].contains("w")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
            }
//        println("Pair at index ($i, $j): ($firstElement, $secondElement)")
        }
        this.previousPiece = piece // update previous piece to current piece (setelah move)
        return board
    }

    // belum sy test
    fun getPossibleKingMoves(i: Int, j: Int, piece: String): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
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
        // Untuk masing2 arah
        for ((di, dj) in directions) {
            var row = i + di
            var col = j + dj
            var stopHere = false
            println("current cell is $row, $col")
            // untuk masing2 cell (yg possible move)
            while (row in 0..7 && col in 0..7 && !stopHere) {
                println("poss move is $row, $col")
                // sewaktu dapat penghalang ( piece lain )
                if (board[row][col] != "") {
                    println("penghalang di $row $col")
                    // kalau piece putih dan cell mengandung piece hitam yg bukan king
                    if (piece[0] == 'w' && board[row][col][0] == 'b' && !board[row][col].contains("g")) {
                        // masih nampung sekali lagi untuk piece beda warna ini,
                        // karena persepsinya piece tersebut ditangkap
                        possibleMoves.add(Pair(row, col))
                    }
                    // kalau piece hitam dan cell mengandung piece putih yg bukan king
                    if (piece[0] == 'b' && board[row][col][0] == 'w' && !board[row][col].contains("g")) {
                        // masih nampung sekali lagi untuk piece beda warna ini,
                        // karena persepsinya piece tersebut ditangkap
                        possibleMoves.add(Pair(row, col))
                        stopHere = true
                    } else stopHere = true
                } else {
                    possibleMoves.add(Pair(row, col))
                    row += di
                    col += dj
                }
            }
        }
        return possibleMoves
    }

    fun moveKing(
        currX: Int,
        currY: Int,
        destX: Int,
        destY: Int,
        piece: String,
        previousPiece: String,
        board: Array<Array<String>>,
        ischeck: Boolean
    ): Array<Array<String>> {
        val possMoves: List<Pair<Int, Int>> =
            getPossiblePawnMoves(currX, currY, piece, previousPiece)
        possMoves.forEachIndexed { index, pair ->
//            val i = index / 8
//            val j = index % 8
            // Access the elements of the pair
            val firstElement: Int = pair.first
            val secondElement: Int = pair.second
            println("test $firstElement $secondElement")
            /*    - memenuhi kalau cell tujuan:
                  - ada piece warna berlawanan namun bukan king
            */
            if (firstElement == destX && secondElement == destY) {
                // for white piece
                if (piece[0] == 'w'
                    || board[destX][destY].contains("b")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
                // for black piece
                if (piece[0] == 'b'
                    || board[destX][destY].contains("w")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
            }
//        println("Pair at index ($i, $j): ($firstElement, $secondElement)")
        }
        this.previousPiece = piece // update previous piece to current piece (setelah move)
        return board
    }

    fun getPossibleQueenMoves(i: Int, j: Int, piece: String): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        // Queen (ratu) = Bishop + Rook moves
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
        // Untuk masing2 arah
        for ((di, dj) in directions) {
            var row = i + di
            var col = j + dj
            var stopHere = false
            println("current cell is $row, $col")
            // untuk masing2 cell (yg possible move)
            while (row in 0..7 && col in 0..7 && !stopHere) {
                println("poss move is $row, $col")
                // sewaktu dapat penghalang ( piece lain )
                if (board[row][col] != "") {
                    /*  ======================================================================
                            Prekondisi
                                board [6][3] ="" // hapus biar ratu bisa lewat
                                board [6][2] =""
                                board [6][4] =""
                                board [4][6] ="br"
                                board [3][3] ="bb"
                             Note: Rook putih start di 7,3 . Lalu ada penghalang (bishop hitam)
                             di 3,3 dan (rook hitam) di 4,6. Sehingga ratu tidak boleh melangkai ke 2,3
                             maupun ke 3,7, alhasil valid moves adalah :
                          [(6, 3), (5, 3), (4, 3), (3, 3), (6, 4), (5, 5), (4, 6), (6, 2), (5, 1), (4, 0)]
                                        ┌────┬────┬────┬────┬────┬────┬────┬────┐
                                        │ 0,0│ bk │ bb │ bq │ bg │ bb │ bk │ br │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │ bp │ bp │ bp │ bp │ bp │ bp │ bp │ bp │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │    │    │    │    │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │ bb │    │    │    │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │    │    │    │ br │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │    │    │    │    │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │ wp │ wp │    │    │    │ wp │ wp │ wp │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │ wr │ wk │ wb │ wq │ wg │ wb │ wk │ wr │
                                        └────┴────┴────┴────┴────┴────┴────┴────┘
                      ===============================================================================================*/
                    println("penghalang di $row $col")
                    // kalau piece putih dan cell mengandung piece hitam yg bukan king
                    if (piece[0] == 'w' && board[row][col][0] == 'b' && !board[row][col].contains("g")) {
                        // masih nampung sekali lagi untuk piece beda warna ini,
                        // karena persepsinya piece tersebut ditangkap
                        possibleMoves.add(Pair(row, col))
                    }
                    // kalau piece hitam dan cell mengandung piece putih yg bukan king
                    if (piece[0] == 'b' && board[row][col][0] == 'w' && !board[row][col].contains("g")) {
                        // masih nampung sekali lagi untuk piece beda warna ini,
                        // karena persepsinya piece tersebut ditangkap
                        possibleMoves.add(Pair(row, col))
                        stopHere = true
                    } else stopHere = true
                } else {
                    possibleMoves.add(Pair(row, col))
                    row += di
                    col += dj
                }
            }
        }
        return possibleMoves
    }

    fun moveQueen(
        currX: Int,
        currY: Int,
        destX: Int,
        destY: Int,
        piece: String,
        board: Array<Array<String>>,
        ischeck: Boolean
    ): Array<Array<String>> {
        var possMoves: List<Pair<Int, Int>> = getPossibleQueenMoves(currX, currY, piece)
        possMoves.forEachIndexed { index, pair ->
//            val i = index / 8
//            val j = index % 8
            // Access the elements of the pair
            val firstElement: Int = pair.first
            val secondElement: Int = pair.second
            println("test $firstElement $secondElement")
            /*    - memenuhi kalau cell tujuan:
                  - ada piece warna berlawanan namun bukan king
                  - tidak dalam keadaan check/skak */
            if (firstElement == destX && secondElement == destY) {
                // for white piece
                if (ischeck == false && piece[0] == 'w'
                    || board[destX][destY].contains("b")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
                // for black piece
                if (ischeck == false && piece[0] == 'b'
                    || board[destX][destY].contains("w")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
            }
//        println("Pair at index ($i, $j): ($firstElement, $secondElement)")
        }
        this.previousPiece = piece // update previous piece to current piece (setelah move)
        return board
    }

    fun getPossibleRookMoves(i: Int, j: Int, piece: String): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        // Define directional vectors for diagonals
        val directions = listOf(
            Pair(-1, 0),  // Up
            Pair(1, 0), // bawah
            Pair(0, 1),   // kanan (->)
            Pair(0, -1)   // kiri (<-)
        )
        // Untuk masing2 arah
        for ((di, dj) in directions) {
            var row = i + di
            var col = j + dj
            var stopHere = false
            println("current cell is $row, $col")
            // untuk masing2 cell (yg possible move)
            while (row in 0..7 && col in 0..7 && !stopHere) {
                println("poss move is $row, $col")
                // sewaktu dapat penghalang ( piece lain )
                if (board[row][col] != "") {
                    /*  ======================================================================
                            Prekondisi
                            board [7][1] =""
                            board [7][2] =""
                            board [7][3] =""
                            board [6][0] =""
                            board[3][0]="bp" // tes hapus pawn supaya rook bisa lewat
                            Note: Rook putih start di 7,0 . Lalu ada penghalang (pawn hitam)
                             di 3,0. Sehingga cell 2,0 tidak dapat diinclude lagi sebagai valid cell
                             alhasil valid moves adalah :
                            [(6, 0), (5, 0), (4, 0), (3, 0), (7, 1), (7, 2), (7, 3)]
                                        ┌────┬────┬────┬────┬────┬────┬────┬────┐
                                        │ 0.0│ bk │ bb │ bq │ bg │ bb │ bk │ br │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │ bp │ bp │ bp │ bp │ bp │ bp │ bp │ bp │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │    │    │    │    │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │ bp │    │    │    │    │    │    │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │    │    │    │    │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │    │    │    │    │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │ wp │ wp │ wp │ wp │ wp │ wp │ wp │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │ wr │    │    │    │ wg │ wb │ wk │ wr │
                                        └────┴────┴────┴────┴────┴────┴────┴────┘
                      ===============================================================================================*/
                    println("penghalang di $row $col")
                    // kalau piece putih dan cell mengandung piece hitam yg bukan king
                    if (piece[0] == 'w' && board[row][col][0] == 'b' && !board[row][col].contains("g")) {
                        // masih nampung sekali lagi untuk piece beda warna ini,
                        // karena persepsinya piece tersebut ditangkap
                        possibleMoves.add(Pair(row, col))
                    }
                    // kalau piece hitam dan cell mengandung piece putih yg bukan king
                    if (piece[0] == 'b' && board[row][col][0] == 'w' && !board[row][col].contains("g")) {
                        // masih nampung sekali lagi untuk piece beda warna ini,
                        // karena persepsinya piece tersebut ditangkap
                        possibleMoves.add(Pair(row, col))
                        stopHere = true
                    } else stopHere = true
                } else {
                    possibleMoves.add(Pair(row, col))
                    row += di
                    col += dj
                }
            }
        }
        return possibleMoves
    }

    fun moveRook(
        currX: Int,
        currY: Int,
        destX: Int,
        destY: Int,
        piece: String,
        board: Array<Array<String>>,
        ischeck: Boolean
    ): Array<Array<String>> {
        val possMoves: List<Pair<Int, Int>> = getPossibleRookMoves(currX, currY, piece)
        possMoves.forEachIndexed { index, pair ->
//            val i = index / 8
//            val j = index % 8
            // Access the elements of the pair
            val firstElement: Int = pair.first
            val secondElement: Int = pair.second
            println("test $firstElement $secondElement")
            /*    - memenuhi kalau cell tujuan:
                  - ada piece warna berlawanan namun bukan king
                  - tidak dalam keadaan check/skak */
            if (firstElement == destX && secondElement == destY) {
                // for white piece
                if (ischeck == false && piece[0] == 'w'
                    || board[destX][destY].contains("b")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
                // for black piece
                if (ischeck == false && piece[0] == 'b'
                    || board[destX][destY].contains("w")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
            }
//        println("Pair at index ($i, $j): ($firstElement, $secondElement)")
        }
        this.previousPiece = piece // update previous piece to current piece (setelah move)
        return board
    }

    fun getPossibleBishopMoves(i: Int, j: Int, piece: String): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        // Define directional vectors for diagonals
        val directions = listOf(
            Pair(-1, 1),  // Atas-kanan
            Pair(-1, -1), // Atas-kiri
            Pair(1, 1),   // Bawah-kanan
            Pair(1, -1)   // Bawah-kiri
        )
        // Untuk masing2 arah
        for ((di, dj) in directions) {
            var row = i + di
            var col = j + dj
            var stopHere = false
            println("current cell is $row, $col")
            // untuk masing2 cell (yg possible move)
            while (row in 0..7 && col in 0..7 && !stopHere) {
                println("poss move is $row, $col")
                // sewaktu dapat penghalang ( piece lain )
                if (board[row][col] != "") {
                    /*        ======================================================================
                            Prekondisi
                                board [3][6] ="bb"
                                board [6][1] =""
                                board [5][0] =""
                                board[6][3]  ="" // tes hapus pawn supaya bishop bisa lewat
                             contoh bishop putih start di 7,2 . Lalu ada penghalang (bishop hitam)
                             di 3,6. Sehingga cell 2,7 tidak dapat diinclude lagi sebagai valid cell
                             alhasil valid moves adalah :
                             [(6, 3), (5, 4), (4, 5), (3, 6), (6, 1), (5, 0)]
                                        ┌────┬────┬────┬────┬────┬────┬────┬────┐
                                        │0,0 │ bk │ bb │ bq │ bg │ bb │ bk │ br │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │ bp │ bp │ bp │ bp │ bp │ bp │ bp │ bp │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │    │    │    │    │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │    │    │    │ bb │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │    │    │    │    │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │    │    │    │    │    │    │    │    │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │ wp │    │ wp │    │ wp │ wp │ wp │ wp │
                                        ├────┼────┼────┼────┼────┼────┼────┼────┤
                                        │ wr │ wk │ wb │ wq │ wg │ wb │ wk │ wr │
                                        └────┴────┴────┴────┴────┴────┴────┴────┘
                      ===============================================================================================*/
                    println("penghalang di $row $col")
                    // kalau piece putih dan cell mengandung piece hitam yg bukan king
                    if (piece[0] == 'w' && board[row][col][0] == 'b' && !board[row][col].contains("g")) {
                        // masih nampung sekali lagi untuk piece beda warna ini,
                        // karena persepsinya piece tersebut ditangkap
                        possibleMoves.add(Pair(row, col))
                    }
                    // kalau piece hitam dan cell mengandung piece putih yg bukan king
                    if (piece[0] == 'b' && board[row][col][0] == 'w' && !board[row][col].contains("g")) {
                        // masih nampung sekali lagi untuk piece beda warna ini,
                        // karena persepsinya piece tersebut ditangkap
                        possibleMoves.add(Pair(row, col))
                        stopHere = true
                    } else stopHere = true
                } else {
                    possibleMoves.add(Pair(row, col))
                    row += di
                    col += dj
                }
            }
        }
        return possibleMoves
    }

    fun moveBishop(
        currX: Int,
        currY: Int,
        destX: Int,
        destY: Int,
        piece: String,
        board: Array<Array<String>>,
        ischeck: Boolean
    ): Array<Array<String>> {
        var possMoves: List<Pair<Int, Int>> = getPossibleBishopMoves(currX, currY, piece)
        possMoves.forEachIndexed { index, pair ->
//            val i = index / 8
//            val j = index % 8
            // Access the elements of the pair
            val firstElement: Int = pair.first
            val secondElement: Int = pair.second
            println("test $firstElement $secondElement")
            /*    - memenuhi kalau cell tujuan:
                  - ada piece warna berlawanan namun bukan king
                  - tidak dalam keadaan check/skak */
            if (firstElement == destX && secondElement == destY) {
                // for white piece
                if (ischeck == false && piece[0] == 'w'
                    || board[destX][destY].contains("b")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
                // for black piece
                if (ischeck == false && piece[0] == 'b'
                    || board[destX][destY].contains("w")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
            }
//        println("Pair at index ($i, $j): ($firstElement, $secondElement)")
        }
        this.previousPiece = piece // update previous piece to current piece (setelah move)
        return board
    }

    fun getPossibleKnightMoves(i: Int, j: Int): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        // Define the eight possible directions a knight can move
        val directions = listOf(
            // Two squares horizontally and one square vertically
            // Move up and right
            Pair(i - 2, j + 1),
            // Move up and left
            Pair(i - 2, j - 1),
            // Move down and right
            Pair(i + 2, j + 1),
            // Move down and left
            Pair(i + 2, j - 1),
            // Two squares vertically and one square horizontally
            // Move left and up
            Pair(i - 1, j - 2),
            // Move left and down
            Pair(i + 1, j - 2),
            // Move right and up
            Pair(i - 1, j + 2),
            // Move right and down
            Pair(i + 1, j + 2)
        )
        // Check each direction and add valid moves to the list of possible moves
        for ((newRow, newCol) in directions) {
            if (newRow in 0 until 8 && newCol in 0 until 8) {
                possibleMoves.add(newRow to newCol)
            }
        }
        return possibleMoves
    }

    fun moveKnight(
        currX: Int,
        currY: Int,
        destX: Int,
        destY: Int,
        piece: String,
        board: Array<Array<String>>,
        ischeck: Boolean
    ): Array<Array<String>> {
        val possMoves: List<Pair<Int, Int>> = getPossibleKnightMoves(currX, currY)
        possMoves.forEachIndexed { index, pair ->
//            val i = index / 8
//            val j = index % 8
            // Access the elements of the pair
            val firstElement: Int = pair.first
            val secondElement: Int = pair.second
            println("test $firstElement $secondElement")
            /*    - memenuhi kalau cell tujuan:
                  - kosong atau warna berlawanan
                  - dan bukan king dan
                  - tidak dalam keadaan check/skak */
            if (firstElement == destX && secondElement == destY
                && board[firstElement][secondElement] == ""
            ) {
                // for white piece
                if (ischeck == false && piece[0] == 'w'
                    || board[destX][destY].contains("b")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
                // for black piece
                if (ischeck == false && piece[0] == 'b'
                    || board[destX][destY].contains("w")
                    && !board[destX][destY].contains("g")
                ) {
                    board[destX][destY] = piece
                    board[currX][currY] = ""
                }
            }
//        println("Pair at index ($i, $j): ($firstElement, $secondElement)")
        }
        this.previousPiece = piece // update previous piece to current piece (setelah move)
        return board
    }
}