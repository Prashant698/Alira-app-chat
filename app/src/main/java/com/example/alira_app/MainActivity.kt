package com.example.alira_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var sendBtn: ImageView
    private val USER = 0
    private val BOT = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val chatScrollView = findViewById<NestedScrollView>(R.id.chatScrollView)

        editText = findViewById(R.id.edittext_chatbox)
        chatScrollView.post { chatScrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        sendBtn = findViewById(R.id.btn_send)
        sendBtn.setOnClickListener{
            sendMessage()
        }


    }

    fun sendMessage(){
        val msg:String = editText.text.toString().trim()
        val date = Date(System.currentTimeMillis())


       val okHttpClient = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.43.9:5005/webhooks/rest/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val userMessage = UserMessage()
        if(msg.trim().isEmpty())
            Toast.makeText(this,"Please enter your query",Toast.LENGTH_SHORT).show()
        else {
            Log.e("MSg","msssage: $msg")
            editText.setText("")
            userMessage.UserMessage("User",msg)
            showTextView(msg,USER,date.toString())

        }
       val messageSender = retrofit.create(MessageSender::class.java)
        val response =
            messageSender.sendMessage(userMessage)

        response.enqueue(object : Callback<List<BotResponse>> {
            override fun onResponse(call: Call<List<BotResponse>>, response: Response<List<BotResponse>>) {
                if (response.body() == null || response.body()!!.size == 0) {
                    val botMessage = "Sorry didn't understand"
                    showTextView(botMessage,BOT,date.toString())
                } else {
                    val botResponse = response.body()!![0]
                    showTextView(botResponse.text,BOT,date.toString())
                    if(botResponse.buttons != null) {
                        Log.e("Button c", "${botResponse.buttons.size}")
                    }
                }
            }

            override fun onFailure(call: Call<List<BotResponse>>, t: Throwable) {
                val botMessage = "Check your network connection"
                showTextView(botMessage,BOT,date.toString())
                t.printStackTrace()
                Toast.makeText(this@MainActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }
    fun showTextView(message:String,type:Int,date:String){
        var frameLayout: FrameLayout? = null
        val linearLayout = findViewById<LinearLayout>(R.id.chat_layout)
        when(type){
            USER -> { frameLayout = getUserLayout()
            }
            BOT ->{frameLayout = getBotLayout()
            }
            else->{
                frameLayout = getBotLayout()
            }
        }
        frameLayout?.isFocusableInTouchMode = true
        linearLayout.addView(frameLayout)
        val messageTextView = frameLayout?.findViewById<TextView>(R.id.chat_msg)
        messageTextView?.setText(message)
        frameLayout?.requestFocus()
        editText.requestFocus()
       /* val currentDateTime = Date(System.currentTimeMillis())
        val dateNew = Date(date)
        val dateFormat = SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH)
        val currentDate = dateFormat.format(currentDateTime)
        val providedDate = dateFormat.format(dateNew)
        var time = ""
        if(currentDate.equals(providedDate)) {
            val timeFormat = SimpleDateFormat(
                "hh:mm aa",
                Locale.ENGLISH
            )
            time = timeFormat.format(dateNew)
        }else{
            val dateTimeFormat = SimpleDateFormat(
                "dd-MM-yy hh:mm aa",
                Locale.ENGLISH
            )
            time = dateTimeFormat.format(dateNew)
        }
        val timeTextView = frameLayout?.findViewById<TextView>(R.id.message_time)
        timeTextView?.setText(time.toString())*/


    }
    fun getUserLayout(): FrameLayout? {
        val inflater: LayoutInflater = LayoutInflater.from(this)
        return inflater.inflate(R.layout.user_message_box, null) as FrameLayout?
    }

    fun getBotLayout(): FrameLayout? {
        val inflater: LayoutInflater = LayoutInflater.from(this)
        return inflater.inflate(R.layout.bot_message_box, null) as FrameLayout?
    }

    fun onClick(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        title = "KotlinApp"
    }

    fun setText(view: View) {
        val newText = editText.text.toString()
        closeKeyBoard()
        layout_chatbox.setVisibility(View.INVISIBLE)
        bottom_nav_bar.setVisibility(View.VISIBLE)

    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    fun Keyboard(view: View) {
        showSoftKeyboard()
        layout_chatbox.setVisibility(View.VISIBLE)
        bottom_nav_bar.setVisibility(View.INVISIBLE)

    }



    private fun showSoftKeyboard() {
        println("s")
        val view = this.currentFocus

        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }



}
