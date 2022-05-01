package com.mju.csmoa.common.util

object Constants {
    const val TAG = "로그"

    /**
     * Emulator에서 할 때는 컴퓨터 localhost 서버에 접속하려면 10.0.0.2를 사용해야 한다(127.0.0.1는 Emulator device 자체 루프백)
     * 실제 모바일 device가 컴퓨터 localhost 서버에 접속하려면 로컬 서버의 private ip를 사용하면 된다.
     */
    var API_BASE_URL = "http://192.168.45.75:5000"
}
