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

import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmStudioCenter
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmStudioLength
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

class RhythmReceiver(val player: Player, val team: RhythmTeam) : RhythmPlayer(player) {
    override fun prepare() {
        val length = 6.0 + rhythmStudioLength
        val direction = team.color.direction
        val location = rhythmStudioCenter.clone().add(direction.dx * length, 0.0, direction.dz * length)
        location.pitch = when (direction) {
            RhythmDirection.NORTH -> 180F
            RhythmDirection.EAST -> 270F
            RhythmDirection.SOUTH -> 0F
            RhythmDirection.WEST -> 90F
        }
        player.teleport(location)
        player.gameMode = GameMode.ADVENTURE
        player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue
        player.allowFlight = true
    }
}