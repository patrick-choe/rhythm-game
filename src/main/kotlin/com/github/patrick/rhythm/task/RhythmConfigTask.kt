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

import com.github.patrick.rhythm.*
import com.github.patrick.rhythm.util.RhythmColor
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration.loadConfiguration
import java.io.File

class RhythmConfigTask : Runnable {
    private var lastModified = 0L

    /**
     * Following overridden method 'run' executes every single tick
     * to find whether the 'config.yml' file has changed.
     * If the file has changed, this task saves a changed value
     * to the global variables.
     */
    override fun run() {
        val file = File(instance.dataFolder, "config.yml")
        val last = file.lastModified()
        if (last != lastModified) {
            lastModified = last
            val config = loadConfiguration(file)
            moveSpeed = config.getDouble("move-speed", 1.0)
            rhythmMusic = requireNotNull(config.getString("rhythm-music"))
            rhythmStudioLength = config.getInt("studio-length", 16)
            rhythmModifier = config.getDouble("rhythm-modifier")
            rhythmLength = rhythmStudioLength + rhythmModifier

            val calcPoint = requireNotNull(config.getConfigurationSection("rhythm-points"))
            pointPoop = calcPoint.getDouble("poop")
            pointGood = calcPoint.getDouble("good")
            pointGreat = calcPoint.getDouble("great")
            pointPerfect = calcPoint.getDouble("perfect")
            pointDestroy = calcPoint.getDouble("destroy")

            val loc = requireNotNull(config.getConfigurationSection("studio-center"))
            rhythmStudioCenter = Location(requireNotNull(getWorld(loc.getString("world"))), loc.getDouble("x"), loc.getDouble("y"), loc.getDouble("z"))

            rhythmGiver = config.getString("giver")
            rhythmColorPlayers.clear()
            val players = requireNotNull(config.getConfigurationSection("receivers"))
            players.getString("RED")?.let { rhythmColorPlayers[RhythmColor.RED] = it }
            players.getString("YELLOW")?.let { rhythmColorPlayers[RhythmColor.YELLOW] = it }
            players.getString("GREEN")?.let { rhythmColorPlayers[RhythmColor.GREEN] = it }
            players.getString("BLUE")?.let { rhythmColorPlayers[RhythmColor.BLUE] = it }

            instance.logger.info("RELOAD CONFIG")
        }
    }
}