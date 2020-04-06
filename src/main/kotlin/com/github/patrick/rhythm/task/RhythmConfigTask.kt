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

import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.instance
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.lastModified
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.moveSpeed
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointDestroy
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointGood
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointGreat
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointPerfect
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointPoop
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmGiver
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmModifier
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmMusic
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmReceivers
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmStudioCenter
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmStudioLength
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmLength
import com.github.patrick.rhythm.util.RhythmColor
import org.bukkit.Bukkit.getLogger
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration.loadConfiguration
import java.io.File

class RhythmConfigTask : Runnable {
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
            rhythmMusic = config.getString("rhythm-music")?: throw NullPointerException("Song cannot be null")
            rhythmStudioLength = config.getInt("studio-length", 16)
            rhythmModifier = config.getDouble("rhythm-modifier")
            rhythmLength = rhythmStudioLength + rhythmModifier

            val calcPoint = config.getConfigurationSection("rhythm-points")
                ?: throw NullPointerException("Points not found")
            pointPoop = calcPoint.getDouble("poop")
            pointGood = calcPoint.getDouble("good")
            pointGreat = calcPoint.getDouble("great")
            pointPerfect = calcPoint.getDouble("perfect")
            pointDestroy = calcPoint.getDouble("destroy")

            val loc = config.getConfigurationSection("studio-center")
                ?: throw NullPointerException("Center location not found")
            getWorld(loc.getString("world"))?.let {
                rhythmStudioCenter = Location(it, loc.getDouble("x"), loc.getDouble("y"), loc.getDouble("z"))
            }

            val players = config.getConfigurationSection("receivers")
                ?: throw NullPointerException("Rhythm players not found")
            rhythmGiver = config.getString("giver")
            rhythmReceivers.clear()
            players.getString("RED")?.let { rhythmReceivers[RhythmColor.RED] = it }
            players.getString("YELLOW")?.let { rhythmReceivers[RhythmColor.YELLOW] = it }
            players.getString("GREEN")?.let { rhythmReceivers[RhythmColor.GREEN] = it }
            players.getString("BLUE")?.let { rhythmReceivers[RhythmColor.BLUE] = it }

            getLogger().info("RELOAD CONFIG")
        }
    }
}