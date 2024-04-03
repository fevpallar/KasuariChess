package kususkasuari
/*==========================================
Author : Fevly Pallar
contact : fevly.pallar@gmail.com
=========================================*/
import java.io.*
import java.net.Socket


fun main() {

    var player = Player()
    Thread(
        player.connect(9999)
    ).start()

    /*==================================================
    Sample proper response :

        Server started. Waiting for client...
        Client connected: 127.0.0.1
        got it
        [[Ljava.lang.String;@4815fa4e
        +---+
        | 0 | 0 | 0 | 0 |
        | 0 | 1 | 2 | 3 |
        | 0 | 2 | 4 | 6 |
        | 0 | 3 | 6 | 9 |
        tm_w
      =================================================

       03042024
       implementasi sementara

       cast --w---> server--w--> opponent
            <--w---

        bedainn??

       nilai toMove diplayer ini saat 'w' balik, mmg ttp sama tapi ttp
       invalid kalau white mau gerak lagi..(sampai 'b' respon )

      gimana kalau 'b' overlap??

      well, Java is SUPER strong in API implementation,
      stream di java re-send kalau streamnya issue (unless fatal exception).
       ===============================================*/


    val kasuariClientEngine = KasuariClientEngine(9999)
    var connClient = kasuariClientEngine.getConn()

    /*=====================================================
    note 30032023
     thread-flag cukup sulit (kalau nanti playernya sdh banyak).
     jadi, instead of boolean flagging,
     dari sini hint saja ke internal serving pakai mark 'pull'
     untuk proses generate datanya (1 player saja)
    ======================================================== */

    // board tdk usah pakai indicator
    player.sendBoard(connClient, player.dummyDataUntukTransmisi())

//    player.getMessage(connClient)
    //StreamCorruptedException
    // 'tm' to move
    player.sendToMove(connClient, "tm_w")
//    player.getMessage(connClient)
    while (true) {
        player.getMessage(connClient)

    }


}

class Player {
    var toMove = "0"
    var cliendIdentificator = System.currentTimeMillis()
    var board = Array(4) { Array(4) { "" } }


    fun dummyDataUntukTransmisi(): Array<Array<String>> {
        // Define the dimensions of the 2D array
        val rows = 4
        val cols = 4

        val array2D = Array(rows) { Array(cols) { "" } }

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                array2D[i][j] = (i * j).toString()
            }
        }

        return array2D
    }


    fun getMessage(sock: Socket) {
        val inputStream = ObjectInputStream(sock.getInputStream())

        val receivedData = inputStream.readObject()

        if (receivedData is Array<*>) {
            val temp = receivedData as Array<Array<String>>
            board = temp
            printBoxedArray(board)
        } else {

            /*========================
            note 03242024
            server sdh trim karakter "_" jdi tinggal proses,
            otherwise, index out of bound.
            ========================== */
            toMove = receivedData.toString()
            println(toMove)
        }

    }

    fun sendBoard(sock: Socket, message: Array<Array<String>>) {

        val outputStream = sock.getOutputStream()
        val objectOutputStream = ObjectOutputStream(outputStream)
        objectOutputStream.writeObject(message)

    }

    fun sendToMove(sock: Socket, message: String) {
        val outputStream = sock.getOutputStream()
        val objectOutputStream = ObjectOutputStream(outputStream)
        objectOutputStream.writeObject(message)
    }

    fun connect(port: Int): Runnable {
        return Runnable {
            var gameEngine = GameEngine()
            gameEngine.start(port)
        }

    }

    fun printBoxedArray(array: Array<Array<String>>) {
        val maxStringLength = array.flatten().maxByOrNull { it.length }?.length ?: 0

        val horizontalBorder = "+${"-".repeat(maxStringLength + 2)}+"
        println(horizontalBorder)

        for (row in array) {
            print("| ")
            for (element in row) {
                val padding = " ".repeat(maxStringLength - element.length)
                print("$element$padding | ")
            }
            println()
        }
    }
}

