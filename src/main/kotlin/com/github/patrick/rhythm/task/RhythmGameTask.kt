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

import com.github.patrick.rhythm.plugin.RhythmPlugin
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointDestroy
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointPerfect
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmMusic
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmBlocks
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmLength
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmReceivers
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmSender
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmStatus
import com.github.patrick.rhythm.process.RhythmGame.Companion.totalTicks
import com.github.patrick.rhythm.process.RhythmListener
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.SoundCategory.MASTER
import kotlin.math.roundToInt

class RhythmGameTask : RhythmTask {
    private var ticks = -1
    /**
     * This 'execute' method works like a 'run'
     * method in 'Runnable'
     */
    override fun execute(): RhythmTask? {
        ++ticks
        rhythmBlocks.values.forEach { blocks -> blocks.forEach { it.onUpdate() } }
        if (ticks == 0) {
            Bukkit.getPluginManager().registerEvents(RhythmListener(), RhythmPlugin.instance)
            rhythmSender.player.playSound(rhythmSender.player.location, rhythmMusic, MASTER, 60000000F, 1F)
            getOnlinePlayers().forEach { it.inventory.heldItemSlot = 4 }
        }
        if (ticks == ((totalTicks * rhythmLength) + (pointPerfect + pointDestroy) / 2).roundToInt()) rhythmReceivers.values.forEach {
            it.player.playSound(it.player.location, rhythmMusic, MASTER, 60000000F, 1F)
        }

        return if (rhythmStatus) this else RhythmResultTask()
    }
}