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

import com.github.noonmaru.tap.packet.Packet
import org.bukkit.Bukkit.broadcastMessage
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Score
import org.bukkit.scoreboard.Team

class RhythmTeam(team: Team, val color: RhythmColor, score: Score) {
    val displayName = team.prefix + team.displayName
    lateinit var rhythmReceiver: RhythmReceiver
    var multiplier = 1
    var connection = 0
    var score = score
        private set

    init {
        score.score = 0
    }

    fun addScore(amount: Int) {
        score.score += (amount * multiplier)
        if (++connection > 9) {
            connection = 0
            if (++multiplier % 4 == 0) broadcastMessage("$displayName just got x$multiplier!")
            Packet.TITLE.compound("Multiplier: x$multiplier", null, 5, 10, 5)
        }
    }

    fun setPlayer(player: Player) {
        rhythmReceiver = RhythmReceiver(player, this)
    }
}