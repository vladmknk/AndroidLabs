package ua.cn.stu.tictactoe.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class GameService : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): GameService = this@GameService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun calculateNextMoveAsync(board: Array<Array<String?>>, callback: (Int?) -> Unit) {
        Thread {
            val aiMove = calculateNextMove(board)
            callback(aiMove)
        }.start()
    }

    fun calculateNextMove(board: Array<Array<String?>>): Int? {
        return findWinningMove("O", board) ?: findWinningMove("X", board) ?: getRandomMove(board)
    }

    private fun findWinningMove(player: String, board: Array<Array<String?>>): Int? {
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

    private fun getRandomMove(board: Array<Array<String?>>): Int? {
        val availableMoves = board.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell == null) rowIndex * 3 + colIndex else null
            }
        }
        return if (availableMoves.isNotEmpty()) availableMoves.random() else null
    }
}