package com.example.calculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.calculator.databinding.ActivityMainBinding
import com.example.calculator.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var listOfOperations: Array<String> = arrayOf("+", "-", "*", "/", "=")
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mAuth = FirebaseAuth.getInstance()
        val username= intent.getStringExtra("Username").toString()
        println(username)
        Toast.makeText(applicationContext, "welcome ${mAuth.currentUser?.email} !", Toast.LENGTH_LONG).show()
        //addButtons()
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        println(mAuth.currentUser?.email)
        if (mAuth.currentUser?.email==null){
            startActivity(intent)
        }
        binding.logout?.setOnClickListener {
            mAuth.signOut()
            startActivity(intent)
        }
    }



    fun handleOnclick(view: View) {
        val buttonText = (view as Button).text
        //println(buttonText)

        if (resultText.text.toString().isNotEmpty() && !(listOfOperations.contains(
                buttonText
            ) && listOfOperations.contains(resultText.text.toString().last().toString()))
        ) {
            resultText.append(buttonText)
        }
        if (resultText.text.toString().isEmpty()) {
            resultText.append(buttonText)
        }
        //binding.resultText.text=binding.resultText.text+buttonText
        //binding.resultText.text="added"
        if (resultText.text.toString()=="5"){
            FirebaseCrashlytics.getInstance().log("number 5 pressed!")
        }

        //resultText.append(buttonText)

    }

    @Suppress("UNUSED_PARAMETER")
    fun onClear(_view: View) {
        resultText.text = ""
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEquals(_view: View) {


        val operatorList: MutableList<String> = ArrayList()
        val operandList: MutableList<String> = ArrayList()
        val st = StringTokenizer(resultText.text.toString(), "+-*/", true)
        while (st.hasMoreTokens()) {
            val token: String = st.nextToken()
            if ("+-/*".contains(token)) {
                operatorList.add(token)
            } else {
                operandList.add(token)
            }
        }
        try {
            var result = operandList.removeAt(0).toDouble()
            for (i in operatorList) {

                when (i) {
                    "+" -> {
                        result += operandList.removeAt(0).toDouble()
                    }
                    "-" -> {
                        result -= operandList.removeAt(0).toDouble()
                    }
                    "*" -> {
                        result *= operandList.removeAt(0).toDouble()
                    }
                    "/" -> {
                        result /= operandList.removeAt(0).toDouble()
                    }
                    else -> { // Note the block
                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                    }
                }


            }
            resultText.text = result.toString()
            val url = "http://numbersapi.com/${resultText.text.toString().toDouble().toInt()}"
            println(url)
            val que = Volley.newRequestQueue(applicationContext)
            que.add(StringRequest(url,
                { response ->
                    Toast.makeText(
                        applicationContext,
                        "Fact: %s".format(response.toString()),
                        Toast.LENGTH_LONG
                    ).show()
                    //resultText.text = "Response: %s".format(response.toString())
                    println(response.toString())
                },
                { error ->
                    println(error)
                }
            ))
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            Log.e("OnEqual",e.toString())
            FirebaseCrashlytics.getInstance().recordException(e)
            //FirebaseCrashlytics.getInstance().sendUnsentReports()

            resultText.text = ""
        }


    }

//private fun addButtons(){
//var listOfButtons: Array<String> = arrayOf("1","2","3","4","5","6","7","8","9","0","+", "-", "*","/","=")
//    var  layout= LinearLayout(this)
//    layout.layoutParams=LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//        ViewGroup.LayoutParams.WRAP_CONTENT,1.0f)
//    for (i in listOfButtons.indices){
//        if(i%4==0 || listOfButtons.size-i<4){
//            buttonPanel.addView(layout)
//            layout= LinearLayout(this)
//            layout.layoutParams=LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,1.0f)
//        }
//        val button = Button(this)
//        button.text= listOfButtons[i]
//        button.layoutParams = LinearLayout.LayoutParams(0,
//            ViewGroup.LayoutParams.WRAP_CONTENT,1.0f)
//        layout.addView(button)
//    }
//}
}