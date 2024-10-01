package ua.cn.stu.tictactoe.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import ua.cn.stu.tictactoe.R

class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var buttons: List<Button>
    private var board = Array(3) { arrayOfNulls<String>(3) }
    private var isPlayerTurn = true  // Щоб знати, чий зараз хід

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = GameFragmentArgs.fromBundle(requireArguments())
        val playerName = args.login

        val playerNameTextView = view.findViewById<TextView>(R.id.playerNameTextView)
        playerNameTextView.text = getString(R.string.player_name) + playerName

        buttons = listOf(
            view.findViewById(R.id.button1),
            view.findViewById(R.id.button2),
            view.findViewById(R.id.button3),
            view.findViewById(R.id.button4),
            view.findViewById(R.id.button5),
            view.findViewById(R.id.button6),
            view.findViewById(R.id.button7),
            view.findViewById(R.id.button8),
            view.findViewById(R.id.button9)
        )

        if (savedInstanceState != null) {
            isPlayerTurn = savedInstanceState.getBoolean("isPlayerTurn", true)
            val savedBoard = savedInstanceState.getSerializable("board") as Array<Array<String?>>
            for (i in 0..2) {
                for (j in 0..2) {
                    board[i][j] = savedBoard[i][j]
                    buttons[i * 3 + j].text = board[i][j]
                }
            }
        }

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener { onPlayerMove(button, index) }
        }

        view.findViewById<Button>(R.id.resetButton).setOnClickListener { resetGame() }
        view.findViewById<Button>(R.id.exitButton).setOnClickListener { activity?.finish() }
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
        val winningMove = findWinningMove("O")
        if (winningMove != null) {
            buttons[winningMove].text = "O"
            updateBoard(winningMove, "O")
            checkGameEnd("O")
            return
        }

        val blockingMove = findWinningMove("X")
        if (blockingMove != null) {
            buttons[blockingMove].text = "O"
            updateBoard(blockingMove, "O")
            checkGameEnd("O")
            return
        }

        val availableButtons = buttons.filter { it.text.isEmpty() }
        if (availableButtons.isNotEmpty()) {
            val randomButton = availableButtons.random()
            randomButton.text = "O"
            updateBoard(buttons.indexOf(randomButton), "O")
            checkGameEnd("O")
        }
    }

    private fun findWinningMove(player: String): Int? {
        for (i in 0..2) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == null) return i * 3 + 2
            if (board[i][0] == player && board[i][2] == player && board[i][1] == null) return i * 3 + 1
            if (board[i][1] == player && board[i][2] == player && board[i][0] == null) return i * 3
        }

        for (i in 0..2) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == null) return 2 * 3 + i
            if (board[0][i] == player && board[2][i] == player && board[1][i] == null) return 1 * 3 + i
            if (board[1][i] == player && board[2][i] == player && board[0][i] == null) return i
        }

        if (board[0][0] == player && board[1][1] == player && board[2][2] == null) return 8
        if (board[0][0] == player && board[2][2] == player && board[1][1] == null) return 4
        if (board[1][1] == player && board[2][2] == player && board[0][0] == null) return 0

        if (board[0][2] == player && board[1][1] == player && board[2][0] == null) return 6
        if (board[0][2] == player && board[2][0] == player && board[1][1] == null) return 4
        if (board[1][1] == player && board[2][0] == player && board[0][2] == null) return 2

        return null
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
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("Restart") { _, _ -> resetGame() }
            .setNegativeButton("Exit") { _, _ -> activity?.finish() }
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

