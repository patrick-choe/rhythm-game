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

package com.github.patrick.rhythm.process

import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmReceivers
import com.github.patrick.rhythm.util.RhythmColor
import org.bukkit.Bukkit.broadcast
import org.bukkit.Bukkit.getPlayerExact
import org.bukkit.Bukkit.getScoreboardManager
import org.bukkit.scoreboard.Team

object RhythmProcess {
    private var game: RhythmGame? = null

    fun startProcess() {
        if (game != null) broadcast("게임이 이미 진행중입니다.", "command.rhythm").also { return }
        val scoreboard = getScoreboardManager().mainScoreboard
        scoreboard.teams.forEach { it.unregister() }

        val teams = HashMap<Team, RhythmColor>()
        rhythmReceivers.forEach {
            if (getPlayerExact(it.value) != null) {
                val team = scoreboard.registerNewTeam(it.value)
                team.prefix = it.key.color.toString()
                team.addEntry(it.value)
                teams[team] = it.key
            }
        }

        if (teams.isEmpty()) broadcast("게임 참가자가 없습니다. config.yml 을 확인하세요", "command.rhythm").also { return }
        game = RhythmGame(teams)
    }

    fun stopProcess() {
        RhythmGame.rhythmBlocks.values.forEach { blocks -> blocks.forEach { it.destroy() } }
        if (game == null) broadcast("진행중인 게임이 없습니다.", "command.rhythm").also { return }
        game?.unregister()
        game = null
    }
}