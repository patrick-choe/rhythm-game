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

package com.github.patrick.rhythm.task

import com.github.noonmaru.tap.packet.Packet
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmTeams
import com.github.patrick.rhythm.util.RhythmTeam
import org.bukkit.Bukkit.broadcastMessage
import org.bukkit.ChatColor

class RhythmResultTask : RhythmTask {
    /**
     * This 'execute' method works like a 'run'
     * method in 'Runnable'
     */
    override fun execute(): RhythmTask? {
        val teams = ArrayList(rhythmTeams.values).apply {
            sortWith(Comparator { team1: RhythmTeam, team2: RhythmTeam -> team2.score.score.compareTo(team1.score.score) } )
        }
        Packet.TITLE.compound(ChatColor.RED.toString() + "게임종료!", "우승: ${teams.first().displayName}", 5, 60, 10).sendAll()
        teams.forEachIndexed { index, team -> broadcastMessage(
            "${index + 1}. ${team.displayName}(${team.score.score})${ChatColor.WHITE} ->" +
                " ${ChatColor.AQUA}PERFECT: ${team.scoreMap[4]}" +
                " ${ChatColor.GREEN}GREAT: ${team.scoreMap[3]}" +
                " ${ChatColor.GOLD}GOOD: ${team.scoreMap[2]}" +
                " ${ChatColor.LIGHT_PURPLE}POOP: ${team.scoreMap[1]}" +
                " ${ChatColor.RED}MISS: ${team.scoreMap[0]}" +
                " ${ChatColor.GRAY}MAX COMBO: ${team.maxCombo}") }
        return null
    }
}