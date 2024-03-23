package com.fevly.kasuarichess


import PieceMoves
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.fevly.kasuarichess.procs.BoardInit
import com.google.android.material.tabs.TabLayout


class BoardFragment : Fragment() {
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    var currentPiece = ""
    var currentRow = -1
    var currentCol = -1

    var colorToMoveFlag=0; // 0 -> putih (white), 1 -> hitam (bleeki..)

    lateinit var board: Array<Array<String>>

    lateinit var chessboardLayout: GridLayout

    lateinit var trackedClickedPiece: MutableSet<Pair<Int, Int>>
    lateinit var imageViewArray: Array<Array<ImageView>>

    lateinit var pm: PieceMoves

    lateinit var latestBoard: Array<Array<String>>
    lateinit var snapshotMoves: MutableList<Array<Array<String>>>

    var labelHuruf = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")
    var labelAngka = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_one, container, false)


        imageViewArray = Array(8) { Array(8) { ImageView(requireContext()) } }
        chessboardLayout = view.findViewById<GridLayout>(R.id.chessboard)
        chessboardLayout.setBackgroundColor(Color.RED)


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
            Color.parseColor("#F57C00")
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

                    if (trackedClickedPiece.size > 1) {

                        var previousX =
                            trackedClickedPiece.toTypedArray()[trackedClickedPiece.size - 2].first.toInt();
                        var previousY =
                            trackedClickedPiece.toTypedArray()[trackedClickedPiece.size - 2].second.toInt();

                        Log.d("chess", "previous ($previousX , $previousY) ")


                        if (currentRow != previousX && currentCol != previousY) {
                            // 22/03/2024 move dari cell kosong ke cell bidak throw exception
                            // , sehingga perlu board[previousX][previousY].length>0
                            if (board[previousX][previousY].length > 0 && board[previousX][previousY][1] == 'k') {

                                latestBoard = pm.moveKnight(
                                    previousX,
                                    previousY,
                                    currentRow,
                                    currentCol,
                                    board[previousX][previousY],
                                    board,
                                    false
                                )
//                                 boardInit.
                                BoardInit().printTwoDStringArrayInBox(latestBoard)

                                // update layout square image view disini
                                drawUpdatedPieces(latestBoard, imageViewArray)

                                if (snapshotMoves.size==0)
                                snapshotMoves.add(latestBoard)

                               else {
                                    // jika kontent latestboard identik dengan element terakhir dari
                                    // array snapshot maka interpretasinya adalah tdk ada pergerakan
                                    if (!latestBoard.contentDeepEquals(
                                            snapshotMoves.toTypedArray()[snapshotMoves.size - 1]
                                        )
                                    )
                                        snapshotMoves.add(latestBoard)
                               }



                            }
                        }
                        trackedClickedPiece.clear()

                    }

                    if(snapshotMoves.size>0) {
                        Log.d("chess","snapshot terakhir: \n")
                        BoardInit().printTwoDStringArrayInBox(snapshotMoves[snapshotMoves.size - 1])

                    }
                }
            }
        }

    }
}