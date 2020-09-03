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

import com.github.patrick.rhythm.*
import com.github.patrick.rhythm.task.RhythmScheduler
import com.github.patrick.rhythm.util.RhythmBlock
import com.github.patrick.rhythm.util.RhythmColor
import com.github.patrick.rhythm.util.RhythmSender
import com.github.patrick.rhythm.util.RhythmTeam
import org.bukkit.Bukkit.getPlayerExact
import org.bukkit.Bukkit.getScheduler
import org.bukkit.Bukkit.getScoreboardManager
import org.bukkit.ChatColor
import org.bukkit.Material.WOOL
import org.bukkit.event.HandlerList.unregisterAll
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Team
import java.util.LinkedList

/**
 * Rhythm game instance
 */
class RhythmGame(teams: HashMap<Team, RhythmColor>) {
    private var task: BukkitTask

    /**
     * Called on initial process
     */
    init {
        val scoreboard = getScoreboardManager().mainScoreboard
        scoreboard.getObjective("rhythm")?.apply { this.unregister() }
        val objective = scoreboard.registerNewObjective("rhythm", "dummy")
        objective.displayName = "   ${ChatColor.DARK_BLUE}RhythmCraft   "
        objective.displaySlot = DisplaySlot.SIDEBAR

        rhythmStatus = true
        val rhythmSenderPlayer = getPlayerExact(rhythmGiver)?: throw NullPointerException("Giver cannot be empty")
        rhythmSender = RhythmSender(rhythmSenderPlayer)
        onlineRhythmPlayers[rhythmSenderPlayer] = rhythmSender

        teams.forEach {
            val rhythmTeam = RhythmTeam(it.key, it.value, objective.getScore(it.key.prefix + it.key.name))

            it.key.entries.forEach { entry ->
                getPlayerExact(entry)?.let { player ->
                    rhythmTeam.setPlayer(player)
                }
            }

            rhythmBlocks[it.value] = Array(9) {
                LinkedList()
            }
            val rhythmReceiver = rhythmTeam.rhythmReceiver
            rhythmTeams[it.value] = rhythmTeam
            rhythmReceivers[rhythmReceiver.uniqueId] = rhythmReceiver
            onlineRhythmPlayers[rhythmReceiver.player] = rhythmReceiver
        }
        if (teams.isEmpty()) throw IllegalArgumentException("Teams cannot be empty")

        onlineRhythmPlayers.keys.forEach {
            it.inventory?.apply {
                for (i in 0..8) setItem(i, ItemStack(WOOL, 1, when (i) {
                    0, 8 -> 14
                    1, 7 -> 4
                    2, 6 -> 5
                    3, 5 -> 9
                    else -> 0
                }.toShort()))
            }
        }

        task = getScheduler().runTaskTimer(instance, RhythmScheduler(), 0, 1)
    }

    /**
     * Unregisters listeners and tasks
     */
    fun unregister() {
        unregisterAll(instance)
        task.cancel()
    }
}