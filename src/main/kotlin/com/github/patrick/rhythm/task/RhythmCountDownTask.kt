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
import com.github.patrick.rhythm.onlineRhythmPlayers
import org.bukkit.ChatColor

class RhythmCountDownTask : RhythmTask {
    private var ticks = 100
    private val period = 20
    private var countdown = -1
    override fun execute(): RhythmTask? {
        --ticks
        if (ticks > 0) {
            val count = (ticks + period) / period
            if (countdown != count) try { Packet.TITLE.compound(getColorByCount(count).toString() + count.toString(), null, 0, period + 20, 0).sendAll() } finally { countdown = count }
            return this
        }
        Packet.TITLE.compound("§6START", null, 0, 24, 6).sendAll()
        onlineRhythmPlayers.values.forEach { it.prepare() }
        return RhythmGameTask()
    }

    private fun getColorByCount(count: Int) = when (count) {
        1 -> ChatColor.RED
        2 -> ChatColor.YELLOW
        3 -> ChatColor.GREEN
        4 -> ChatColor.AQUA
        5 -> ChatColor.LIGHT_PURPLE
        else -> ChatColor.WHITE
    }
}