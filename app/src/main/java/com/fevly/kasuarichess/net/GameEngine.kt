package kususkasuari
/*==========================================
Author : Fevly Pallar
contact : fevly.pallar@gmail.com
=========================================*/
import com.fevly.kasuarinet.KasuariServerEngine
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket


// note 30032023 : sluruh bagian disini coupled sama thread server (jadi tdk bisa client disini)
class GameEngine {
    var kasuariServerEngine: KasuariServerEngine
    init {
        kasuariServerEngine = KasuariServerEngine()
    }

    fun start(port : Int) {
        kasuariServerEngine.startServer(port)
    }
}

