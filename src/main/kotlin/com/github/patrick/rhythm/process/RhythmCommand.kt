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

import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmStatus
import com.github.patrick.rhythm.process.RhythmProcess.startProcess
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class RhythmCommand : CommandExecutor, TabCompleter {
    /**
     * Executes the given command, returning its success
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("command.rhythm")) return false
        when (args[0]) {
            "start" -> startProcess()
            "stop" -> rhythmStatus = false
            else -> helpCommand(sender)
        }
        return true
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender Source of the command.
     * @param command Command which was executed
     * @param alias The alias used
     * @param args The arguments passed to the command, including final
     * partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>) =
        when (args.size) {
            1 -> listOf("help", "start", "stop").filter(args[0])
            else -> emptyList()
        }

    private fun helpCommand(sender: CommandSender) {
        sender.sendMessage("##### Rhythm Help #####")
        sender.sendMessage("/rhythm start: Starts the rhythm game")
        sender.sendMessage("/rhythm stop: Stops the rhythm game")
    }

    private fun Iterable<String>.filter(predicate: String) = filter { it.startsWith(predicate) }
}

