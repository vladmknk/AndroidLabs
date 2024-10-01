package ua.cn.stu.tictactoe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ua.cn.stu.tictactoe.R

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val loginEditText = view.findViewById<EditText>(R.id.loginEditText)

        loginButton.setOnClickListener {
            val login = loginEditText.text.toString()

            // Передаємо логін у GameFragment через Bundle
            val action = LoginFragmentDirections.actionLoginFragmentToGameFragment(login)
            findNavController().navigate(action)
        }
    }
}