package ua.cn.stu.tictactoe.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ua.cn.stu.tictactoe.R
import ua.cn.stu.tictactoe.services.GameService

class GameActivity : AppCompatActivity() {

    private lateinit var buttons: List<Button>
    private var board = Array(3) { arrayOfNulls<String>(3) }
    private var isPlayerTurn = true
    private var gameService: GameService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as GameService.LocalBinder
            gameService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_game)

        val playerName = intent.getStringExtra("login")

        val playerNameTextView = findViewById<TextView>(R.id.playerNameTextView)
        playerNameTextView.text = getString(R.string.player_name) + playerName

        buttons = listOf(
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6),
            findViewById(R.id.button7),
            findViewById(R.id.button8),
            findViewById(R.id.button9)
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener { onPlayerMove(button, index) }
        }

        findViewById<Button>(R.id.resetButton).setOnClickListener { resetGame() }
        findViewById<Button>(R.id.exitButton).setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, GameService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    private fun onPlayerMove(button: Button, index: Int) {
        if (button.text.isEmpty()) {
            button.text = "X"
            updateBoard(index, "X")
            if (!checkGameEnd("X")) {
                onAiMove()
            }
        }
    }

    private fun onAiMove() {
        gameService?.let {
            val aiMove = it.calculateNextMove(board)
            aiMove?.let { move ->
                buttons[move].text = "O"
                updateBoard(move, "O")
                checkGameEnd("O")
            }
        }
    }

    private fun updateBoard(index: Int, player: String) {
        val row = index / 3
        val col = index % 3
        board[row][col] = player
    }

    private fun checkGameEnd(player: String): Boolean {
        if ((0..2).any { i -> (board[i][0] == player && board[i][1] == player && board[i][2] == player) } ||
            (0..2).any { i -> (board[0][i] == player && board[1][i] == player && board[2][i] == player) } ||
            (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
            (board[0][2] == player && board[1][1] == player && board[2][0] == player)) {
            showGameEndDialog("$player won!")
            return true
        }

        if (buttons.all { it.text.isNotEmpty() }) {
            showGameEndDialog("It's a draw!")
            return true
        }

        return false
    }

    private fun showGameEndDialog(message: String) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("Restart") { _, _ -> resetGame() }
            .setNegativeButton("Exit") { _, _ -> finish() }
            .create()
        dialog.show()
    }

    private fun resetGame() {
        buttons.forEach { it.text = "" }
        board = Array(3) { arrayOfNulls<String>(3) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isPlayerTurn", isPlayerTurn)
        outState.putSerializable("board", board)
    }
}