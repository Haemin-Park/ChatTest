package com.example.testchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testchat.adapter.ChatAdapter
import com.example.testchat.model.Chat
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    val cAdapter: ChatAdapter = ChatAdapter(this)

    val logger = Logger.getLogger("Main")

    lateinit var stompConnection: Disposable
    lateinit var topic: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler.adapter = cAdapter
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

        //1. init

        // url: ws://[도메인]/엔드포인트
        val url = "ws://10.0.3.2:8080/im/websocket"
        val intervalMillis = 5000L
        val client = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()

        val stomp = StompClient(client, intervalMillis).apply { this@apply.url = url }

        val constant = Constant()
        send.setOnClickListener {
            cAdapter.addItem(Chat(constant.MESSAGE_TYPE_MY, message.text.toString()))
        }

        // 2. connect
        stompConnection = stomp.connect().subscribe {
            when (it.type) {
                Event.Type.OPENED -> {

                    // subscribe 채널구독
                    topic = stomp.join("/chatting")
                            .subscribe { logger.log(Level.INFO, it) }

                    // unsubscribe
                    //topic.dispose()

                    // send
                    stomp.send("/app/[destination]", message.text.toString())
                    message.text = null
                    /*

                    stomp.send("/destination", "dummy message").subscribe {
                        if (it) {
                        }
                    }

                    */

                }
                Event.Type.CLOSED -> {

                }
                Event.Type.ERROR -> {

                }
            }
        }


    }
}