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

import com.github.noonmaru.tap.ChatType.GAME_INFO
import com.github.noonmaru.tap.Tap
import com.github.noonmaru.tap.packet.Packet
import com.github.patrick.rhythm.*
import com.github.patrick.rhythm.util.RhythmBlock
import com.github.patrick.rhythm.util.RhythmDirection
import com.github.patrick.rhythm.util.RhythmReceiver
import org.bukkit.ChatColor
import org.bukkit.GameMode.SPECTATOR
import org.bukkit.Material
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class RhythmListener : Listener {
    private val senderID = rhythmSender.uniqueId

    /**
     * Called when player joins the game.
     */
    @EventHandler fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val rhythmReceiver = rhythmReceivers[player.uniqueId]
        if (rhythmReceiver == null && senderID != player.uniqueId) player.gameMode = SPECTATOR else {
            onlineRhythmPlayers[player] = rhythmReceiver
            rhythmReceiver?.prepare()
            rhythmBlocks.values.forEach { slots ->
                slots.forEach { blocks ->
                    blocks.forEach {
                        it.spawnTo(setOf(player))
                    }
                }
            }
        }
    }

    /**
     * Called when player leaves the game.
     */
    @EventHandler fun onPlayerQuit(event: PlayerQuitEvent) {
        onlineRhythmPlayers.remove(event.player)
    }

    /**
     * Called when player changes the inventory slot.
     */
    @EventHandler fun onPlayerSwap(event: PlayerItemHeldEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val slot = event.newSlot
        if (slot == 4) return
        player.inventory.heldItemSlot = 4

        if (senderID == uuid) rhythmTeams.forEach {
            newBlock(RhythmBlock(rhythmStudioCenter.clone().add(when (it.key.direction) {
                RhythmDirection.NORTH -> Vector((slot - 4) * -1.0, -1.0, -5.0)
                RhythmDirection.EAST -> Vector(5.0, -1.0, (slot - 4) * -1.0)
                RhythmDirection.SOUTH -> Vector((slot - 4) * 1.0, -1.0, 5.0)
                RhythmDirection.WEST -> Vector(-5.0, -1.0, (slot - 4) * 1.0)
            }), it.value, Tap.ITEM.fromItemStack(ItemStack(Material.WOOL, 1, when (slot) {
                0, 8 -> 14
                1, 7 -> 4
                2, 6 -> 5
                3, 5 -> 9
                else -> 0
            }.toShort()))), slot)
        }
        if (rhythmReceivers.keys.contains(uuid)) {
            val team = (onlineRhythmPlayers[player] as RhythmReceiver).team
            val highestBlock = rhythmBlocks[team.color]?.get(slot)?.peek()
            highestBlock?.let { it ->
                val highestScore = highestBlock.ticks * moveSpeed / 20 - rhythmLength
                val score: Int
                when {
                    highestScore > pointPerfect -> {
                        score = 4
                        Packet.INFO.chat(ChatColor.AQUA.toString() + "PERFECT", GAME_INFO).sendTo(player)
                    }
                    highestScore > pointGreat -> {
                        score = 3
                        Packet.INFO.chat(ChatColor.GREEN.toString() + "GREAT", GAME_INFO).sendTo(player)
                    }
                    highestScore > pointGood -> {
                        score = 2
                        Packet.INFO.chat(ChatColor.GOLD.toString() + "GOOD", GAME_INFO).sendTo(player)
                    }
                    highestScore > pointPoop -> {
                        score = 1
                        Packet.INFO.chat(ChatColor.LIGHT_PURPLE.toString() + "POOP", GAME_INFO).sendTo(player)
                    }
                    else -> {
                        team.miss()
                        return@let
                    }
                }
                team.addScore(score)
                rhythmBlocks[team.color]?.get(slot)?.poll()
                it.destroy()
                return
            }
            team.miss()
        }
    }

    /** Blocking actions */
    @EventHandler fun onEvent(event: InventoryInteractEvent) = cancel(event)
    /** Blocking actions */
    @EventHandler fun onEvent(event: InventoryOpenEvent) = cancel(event)
    /** Blocking actions */
    @EventHandler fun onEvent(event: PlayerDropItemEvent) = cancel(event)
    /** Blocking actions */
    @EventHandler fun onEvent(event: PlayerInteractEvent) = cancel(event)

    private fun cancel(event: Cancellable) {
        event.isCancelled = true
    }
}