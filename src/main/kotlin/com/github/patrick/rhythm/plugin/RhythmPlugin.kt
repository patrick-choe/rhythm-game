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

package com.github.patrick.rhythm.plugin

import com.github.patrick.rhythm.instance
import com.github.patrick.rhythm.process.RhythmCommand
import com.github.patrick.rhythm.process.RhythmProcess.stopProcess
import com.github.patrick.rhythm.task.RhythmConfigTask
import org.bukkit.Bukkit.getScheduler
import org.bukkit.plugin.java.JavaPlugin

/**
 * This is the main class of this 'Rhythm Plugin'.
 * It stores global variables, and initial process of this plugin.
 */
class RhythmPlugin : JavaPlugin() {

    /**
     * Following overridden method executes when the plugin is initializing.
     * It saves default configuration if the 'config.yml' does not exist,
     * sets command executor and tab completer for '/rhythm' command,
     * and registers 'RhythmConfigTask'.
     */
    @Suppress("UsePropertyAccessSyntax")
    override fun onEnable() {
        saveDefaultConfig()
        getCommand("rhythm")?.setExecutor(RhythmCommand())
        getCommand("rhythm")?.setTabCompleter(RhythmCommand())
        getScheduler().runTaskTimer(this, RhythmConfigTask(), 0, 1)
        instance = this
    }

    override fun onDisable() = stopProcess()
}