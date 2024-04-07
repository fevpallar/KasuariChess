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
package com.fevly.kasuarichess.fragments


import PieceMoves
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.fevly.kasuarichess.R
import com.fevly.kasuarichess.fragments.AnalysisFragment.StaticImageViewHolder.imageViewArray
import com.fevly.kasuarichess.procs.BoardInit
import com.fevly.kasuarichess.stockengine.StockfishFeeder
import com.fevly.kasuarichess.util.Timing
import com.google.android.material.tabs.TabLayout


class AnalysisFragment : Fragment() {
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    var currentPiece = ""
    var currentRow = -1
    var currentCol = -1

    // ini nanti diupdate juga oleh lawan
    var colorHasMoveFlag: Char = 'x'; // w -> putih (white), b -> hitam (bleeki..)
    var colorToMoveFlag: Char = 'x'; // w -> putih (white), b -> hitam (bleeki..)

    //  07042024 : ini usahakan reuse dimana2
    // intepret ke notasi
    var lastMoveX = -1
    var lastMoveY = -1
    var lastMovePiece = ""

    lateinit var uiThreadHandler: Handler

    lateinit var board: Array<Array<String>>

    lateinit var chessboardLayout: GridLayout

    lateinit var trackedClickedPiece: MutableSet<Pair<Int, Int>>

    //    lateinit var imageViewArray: Array<Array<ImageView>>
    object StaticImageViewHolder {
        lateinit var imageViewArray: Array<Array<ImageView>>
    }

    lateinit var pm: PieceMoves

    lateinit var latestBoard: Array<Array<String>>
    lateinit var snapshotMoves: MutableList<Array<Array<String>>>

    lateinit var engineout: EditText
    lateinit var stockfishFeeder: StockfishFeeder

    var labelHuruf = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")
    var labelAngka = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")


    lateinit var timing: Timing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.analysis_fragment, container, false)


        imageViewArray = Array(8) { Array(8) { ImageView(requireContext()) } }
        chessboardLayout = view.findViewById<GridLayout>(R.id.chessboard)
        chessboardLayout.setBackgroundColor(Color.RED)

        engineout = view.findViewById<EditText>(R.id.engineoutput)

        //main board
        board = Array(8) { Array(8) { "" } }

        // var2 kusus
        snapshotMoves =
            mutableListOf<Array<Array<String>>>()  // snapshot untuk setiap langkah diseluruh game (untuk nanti fitur 'rewind'. Ya.. klo ada waktu..)
        trackedClickedPiece =
            mutableSetOf<Pair<Int, Int>>() // untuk rekam piece2 yg sedang diklik (*catatan case ada dibawah)
        latestBoard =
            Array(8) { Array(8) { "" } } // kontent array yg akan dipassing di setiap proses, inilah update-an yg jadi snapshot


        // inisialisasi board dan pergerakan bidak2
        var boardInit = BoardInit()
        board = boardInit.initializeBoard()
        boardInit.printTwoDStringArrayInBox(board)
        pm = PieceMoves(board)
        drawLayoutAndBoard()

        // engine
        uiThreadHandler = Handler(Looper.getMainLooper())
        stockfishFeeder = StockfishFeeder(requireContext(), uiThreadHandler, board, 'w', engineout)




        return view
    }

    fun drawLayoutAndBoard() {

        chessboardLayout.post {
            val cellWidth = chessboardLayout.width / 8

            /*=================================================================
            Untuk cellHeight mesti dibagi dari lebar (dan bukan tinggi) agar seluruh
            cell punya ukuran yg sama
             ===================================================================*/
            val cellHeight = chessboardLayout.width / 8
            chessboardLayout.removeAllViews()// reset viewnya

            for (i in 0 until 9) {
                for (j in 0 until 8) {
//                    Log.d("CHESS", "$i $j")
                    if (i < 8 && j < 8) {
                        val imageView = ImageView(requireContext()).apply {
                            val params = GridLayout.LayoutParams().apply {
                                width = cellWidth
                                height = cellHeight
                                rowSpec = GridLayout.spec(i)
                                columnSpec = GridLayout.spec(j)
                            }
                            layoutParams = params
                            setBackgroundColor(getSquareColor(i, j))

                        }

                        drawPiecesInitially(i, j, imageView)
                        imageViewArray[i][j] = imageView
                        onClickOperation();
                        chessboardLayout.addView(imageView) // Add each ImageView to the GridLayout

                    }

                    // row 9 untuk label huruf
                    if (i == 8) {

                        val labelNumber = TextView(requireContext()).apply {
                            val params = GridLayout.LayoutParams().apply {
                                width = cellWidth
                                rowSpec = GridLayout.spec(i)
                                columnSpec = GridLayout.spec(j)
                            }
                            layoutParams = params
                            setBackgroundColor(Color.YELLOW)
                            gravity = Gravity.CENTER

                        };
                        labelNumber.setText(labelHuruf[j])
                        chessboardLayout.addView(labelNumber) // Add each ImageView to the GridLayout
                    }
                }
            }
        }
    }

    fun drawPiecesInitially(row: Int, col: Int, imageView: ImageView) {


        if (board[row][col] == "wp") {
            imageView.setImageResource(R.drawable.whitepawn)

        }
        if (board[row][col] == "bp") {
            imageView.setImageResource(R.drawable.blackpawn)
        }
        if (board[row][col] == "wb") {
            imageView.setImageResource(R.drawable.whitebishop)
        }
        if (board[row][col] == "bb") {
            imageView.setImageResource(R.drawable.blackbishop)
        }
        if (board[row][col] == "wk") {
            imageView.setImageResource(R.drawable.whiteknight)
        }
        if (board[row][col] == "bk") {
            imageView.setImageResource(R.drawable.blackknight)
        }
        if (board[row][col] == "wr") {
            imageView.setImageResource(R.drawable.whiterook)
        }
        if (board[row][col] == "br") {
            imageView.setImageResource(R.drawable.blackrook)
        }
        if (board[row][col] == "wq") {
            imageView.setImageResource(R.drawable.whitequeen)
        }
        if (board[row][col] == "bq") {
            imageView.setImageResource(R.drawable.blackqueen)
        }
        if (board[row][col] == "wg") {
            imageView.setImageResource(R.drawable.whiteking)
        }
        if (board[row][col] == "bg") {
            imageView.setImageResource(R.drawable.blackking)
        }
    }


    // issue 22/03/2024 layout blinking saat re-draw
    // butuh remodel
    // fixed 23/03/2024 jgn hapus seluruh view di GridLayout saat re-draw
    fun drawUpdatedPieces(board: Array<Array<String>>, imageView: Array<Array<ImageView>>) {

        val cellWidth = chessboardLayout.width / 8

        /*=================================================================
        Untuk cellHeight mesti dibagi dari lebar (dan bukan tinggi) agar seluruh
        cell punya ukuran yg sama
         ===================================================================*/
        val cellHeight = chessboardLayout.width / 8

        chessboardLayout.post {
            for (i in 0 until 9) {
                for (j in 0 until 8) {

                    if (i < 8 && j < 8) {
                        imageView[i][j] = ImageView(requireContext())

                        if (board[i][j] == "wp") {
                            imageView[i][j].setImageResource(R.drawable.whitepawn)
                        }
                        if (board[i][j] == "bp") {
                            imageView[i][j].setImageResource(R.drawable.blackpawn)

                        }
                        if (board[i][j] == "wb") {
                            imageView[i][j].setImageResource(R.drawable.whitebishop)

                        }
                        if (board[i][j] == "bb") {
                            imageView[i][j].setImageResource(R.drawable.blackbishop)

                        }
                        if (board[i][j] == "wk") {
                            imageView[i][j].setImageResource(R.drawable.whiteknight)

                        }
                        if (board[i][j] == "bk") {
                            imageView[i][j].setImageResource(R.drawable.blackknight)

                        }
                        if (board[i][j] == "wr") {
                            imageView[i][j].setImageResource(R.drawable.whiterook)

                        }
                        if (board[i][j] == "br") {
                            imageView[i][j].setImageResource(R.drawable.blackrook)

                        }
                        if (board[i][j] == "wq") {
                            imageView[i][j].setImageResource(R.drawable.whitequeen)

                        }
                        if (board[i][j] == "bq") {
                            imageView[i][j].setImageResource(R.drawable.blackqueen)

                        }
                        if (board[i][j] == "wg") {
                            imageView[i][j].setImageResource(R.drawable.whiteking)

                        }
                        if (board[i][j] == "bg") {
                            imageView[i][j].setImageResource(R.drawable.blackking)

                        }

                        imageView[i][j].apply {
                            val params = GridLayout.LayoutParams().apply {
                                width = cellWidth
                                height = cellHeight
                                rowSpec = GridLayout.spec(i)
                                columnSpec = GridLayout.spec(j)
                            }
                            layoutParams = params
                            setBackgroundColor(getSquareColor(i, j))
                        }

                        onClickOperation()
                        chessboardLayout.addView(imageView[i][j]) // Add each ImageView to the GridLayout

                    }
                    // row 9 untuk label huruf
                    if (i == 8) {

                        val labelNumber = TextView(requireContext()).apply {
                            val params = GridLayout.LayoutParams().apply {
                                width = cellWidth
                                rowSpec = GridLayout.spec(i)
                                columnSpec = GridLayout.spec(j)
                            }
                            layoutParams = params
                            setBackgroundColor(Color.YELLOW)
                            gravity = Gravity.CENTER

                        };
                        labelNumber.setText(labelHuruf[j])
                        chessboardLayout.addView(labelNumber) // Add each ImageView to the GridLayout
                    }
                }
            }
        }
    }

    private fun getSquareColor(row: Int, col: Int): Int {
        // Determine the background color of each square based on its position
        return if ((row + col) % 2 == 0) {
            ContextCompat.getColor(requireContext(), R.color.white)
        } else {
            Color.parseColor("#0acf0e")
        }
    }

    fun onClickOperation() {
        for (i in 0 until 8) {
            for (j in 0 until 8) {
                imageViewArray[i][j].setOnClickListener {


                    currentPiece = board[i][j]
                    currentRow = i
                    currentCol = j

                    /*========================================================================
                           Note:

                           Untuk setiap piece(bidak) yg dipilih.
                           Misal click pawn, click kuda. Yang maju tentu Kuda, bukan Pawn.

                          Implementasi sekarang (22/03/2024).

                            Buat list untuk merekam piece yg sedang diklik.
                            Sewaktu piece lain diklik, list sudah mengandung 2 element
                            Sehingga elemen terakhir dari list itu yg boleh bergerak.

                            Tanpa implementasi ini, Pawn yg maju ke kotaknya Kuda (seharus kuda yg maju ke kotak tujuannya)
                    ============================================================================*/
                    trackedClickedPiece.add(Pair(currentRow, currentCol))


                    Log.d("CHESS", trackedClickedPiece.toString())
                    Log.d("CHESS", "current = ( $i , $j ) = " + board[i][j])

                    if (trackedClickedPiece.size > 1) {// list sdh 2 element, element terakhir itu bidak yg mau gerak/pindah/move

                        // blok ini ketrigger saat piece sudah move ke tujuan cell, dari piece yg paling awal

                        var previousX =
                            trackedClickedPiece.toTypedArray()[trackedClickedPiece.size - 2].first.toInt();
                        var previousY =
                            trackedClickedPiece.toTypedArray()[trackedClickedPiece.size - 2].second.toInt();

                        //  Log.d("chess", "previous ($previousX , $previousY) ")


                        if (
                            board[previousX][previousY].length > 0 // ini prevent excep. "gerak dari cell kosong ke" cell tdk kosong
                            &&
                            board[previousX][previousY][0] != colorHasMoveFlag // wrn yg move <> wara yg baru move
                            &&
                            currentRow != previousX && currentCol != previousY //cell tujuan <> cell awal

                        ) {

                            // block if ini tdk ketrigger saat pertama kali click bidak

                            // 22/03/2024 move dari cell kosong ke cell bidak throw exception
                            // , sehingga perlu board[previousX][previousY].length>0
                            if (board[previousX][previousY].length > 0
                                && board[previousX][previousY][1] == 'k'
                            ) {

                                // ketrigger setelah piece coba dipindakan

                                latestBoard = pm.moveKnight(
                                    previousX,
                                    previousY,
                                    currentRow,
                                    currentCol,
                                    board[previousX][previousY],
                                    board,
                                    false
                                )

                                /*    println("latest board\n")
                                    BoardInit().printTwoDStringArrayInBox(latestBoard)
    */

                                var isMoved = !latestBoard.contentDeepEquals(
                                    board
                                )
                                if (!isMoved) {
                                    snapshotMoves.add(latestBoard)
                                    /*     BoardInit().printTwoDStringArrayInBox(
                                             snapshotMoves[snapshotMoves.size
                                                     - 1]
                                         )*/
                                    lastMoveX = currentRow
                                    lastMoveY = currentCol
                                    lastMovePiece = board[previousX][previousY]

                                    /*========================================================================
                                      070424 : ini yg board[lastMoveX][lastMoveY]!="".
                                      cegah  issue NPE di titik manapun/posisi apapun di dalam game.

                                      -Issue sebelumnya adalah: Kalau pieces (diposisi manapun)
                                      dipindah-pindahkan /diclik-click dengan terlalu cepat
                                      board[lastMoveX][lastMoveY][0] nyebapin index out of bond.
                                      _____________________________________________-

                                      waktu issue ini belum dibenarkan...debugnya sulit .

                                      Karena, contoh:

                                      [Kuda di cell b1 di pindahkan ke --->  cell a3.
                                      Nah sekarang pindah2 kan secara cepat (bolak-balik dengan cepat beberapa kali)]

                                     Nanti itu cell board[lastMoveX][lastMoveY] bisa  kosong.
                                      Kenapa bisa kosong ??  Tidak tahu.....

                                      asumsinya itu karena :

                                       waktu yg dipakai untuk pindahin kuda OVERLAPP sama
                                       proses2 lain

                                       (entahkah itu saat perbandingan lsnapshot ataukah
                                       sewaktu trackedPiece diisi ataukah sewaktu validasi move)

                                     dengan kata lain, proses pindahin kuda terlalu cepat sebelum
                                     hasil sebelum selesai diproses
                                     ===========================================================================*/

                                    if (board[lastMoveX][lastMoveY] != "" && board[lastMoveX][lastMoveY][0] == 'w') {
                                        colorHasMoveFlag = 'w'
                                        colorToMoveFlag = 'b'

                                    }

                                    if (board[lastMoveX][lastMoveY] != "" && board[lastMoveX][lastMoveY][0] == 'b') {
                                        colorHasMoveFlag = 'b'
                                        colorToMoveFlag = 'w'

                                    }

                                    println(colorHasMoveFlag)

                                    stockfishFeeder.setParams(
                                        requireContext(),
                                        uiThreadHandler,
                                        snapshotMoves[snapshotMoves.size - 1],
                                        colorToMoveFlag,
                                        engineout
                                    )
                                }


                                // 070424 jangan ganti ke latestboard, usahakan tetap snapshot
                                // ada issue, tapi  lupa persisnya bgm
                                drawUpdatedPieces(
                                    snapshotMoves[snapshotMoves.size - 1],
                                    imageViewArray
                                )

                                /*====================================
                                note 31032024
                                currentRow & currentCol dititik ini sudah menjadi
                                destination row dan destination col,
                                karena bidak sudah berhasil pindah
                                ======================================= */
                            }
                        }
                        // ini itu fix issue untuk case "gerak2 piece terlalu cepat" dpt di 070424
                        //sebelumnya trackedClickedPiece.clear() yg sebapkan
                        trackedClickedPiece = mutableSetOf<Pair<Int, Int>>()

                    }

                    /*      if (snapshotMoves.size > 0) {
                              Log.d("chess", "snapshot terakhir: \n")
                              BoardInit().printTwoDStringArrayInBox(snapshotMoves[snapshotMoves.size - 1])

                          }*/
                }
            }
        }

    }
}