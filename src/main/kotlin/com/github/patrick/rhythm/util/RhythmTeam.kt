/*
 * Copyright (C) 2020 PatrickKR
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact me on <mailpatrickkr@gmail.com>
 */

package com.github.patrick.rhythm.util

import com.github.noonmaru.tap.ChatType
import com.github.noonmaru.tap.packet.Packet
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Score
import org.bukkit.scoreboard.Team

class RhythmTeam(team: Team, val color: RhythmColor, score: Score) {
    val displayName = team.prefix + team.displayName
    val scoreMap = HashMap<Int, Int>()
    lateinit var rhythmReceiver: RhythmReceiver
    var multiplier = 1
    var connection = 0
    var score = score
        private set
    var maxCombo = 0
        private set

    init {
        score.score = 0
        setOf(0, 1, 2, 3, 4).forEach { scoreMap[it] = 0 }
    }

    fun addScore(amount: Int) {
        score.score += (amount * multiplier)
        if (++connection > 9) {
            connection = 0
            multiplier++
            rhythmReceiver.player.sendMessage("Multiplier: x$multiplier")
        }
        val temp = scoreMap[amount]?: return
        scoreMap[amount] = temp + 1
    }

    fun setPlayer(player: Player) {
        rhythmReceiver = RhythmReceiver(player, this)
    }

    fun miss() {
        Packet.INFO.chat(ChatColor.RED.toString() + "MISS", ChatType.GAME_INFO).sendTo(rhythmReceiver.player)
        var temp = (multiplier - 1) * 10 + connection
        if (temp > maxCombo) maxCombo = temp
        connection = 0
        multiplier = 1
        temp = scoreMap[0]?: return
        scoreMap[0] = temp + 1
    }
}