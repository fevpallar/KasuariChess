package com.fevly.kasuarichess.net

import java.net.Socket

class KasuariClientEngine (_port: Int) {
    var port =0
    init {
        this.port=_port
    }

    fun getConn (): Socket {
       return Socket("localhost", this.port)
    }
}