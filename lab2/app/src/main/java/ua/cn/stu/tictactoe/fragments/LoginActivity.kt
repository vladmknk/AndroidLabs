package ua.cn.stu.tictactoe.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ua.cn.stu.tictactoe.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

        val loginEditText = findViewById<EditText>(R.id.loginEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val login = loginEditText.text.toString()
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("login", login)
            startActivity(intent)
        }
    }
}