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
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.moveSpeed
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointGood
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointGreat
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointPerfect
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointPoop
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.rhythmStudioCenter
import com.github.patrick.rhythm.process.RhythmGame.Companion.newBlock
import com.github.patrick.rhythm.process.RhythmGame.Companion.onlineRhythmPlayers
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmBlocks
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmLength
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmReceivers
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmSender
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmTeams
import com.github.patrick.rhythm.util.RhythmBlock
import com.github.patrick.rhythm.util.RhythmDirection
import com.github.patrick.rhythm.util.RhythmReceiver
import org.bukkit.ChatColor
import org.bukkit.GameMode.SPECTATOR
import org.bukkit.Material
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.Collections.singleton

class RhythmListener : Listener {
    private val senderID = rhythmSender.uniqueId

    @EventHandler fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val rhythmReceiver = rhythmReceivers[player.uniqueId]
        if (rhythmReceiver == null && senderID != player.uniqueId) player.gameMode = SPECTATOR else {
            onlineRhythmPlayers[player] = rhythmReceiver
            rhythmReceiver?.prepare()
            rhythmBlocks.values.forEach { blocks -> blocks.forEach { it.spawnTo(singleton(player)) } }
        }
    }

    @EventHandler fun onPlayerQuit(event: PlayerQuitEvent) {
        onlineRhythmPlayers.remove(event.player)
    }

    @EventHandler fun onPlayerSwap(event: PlayerItemHeldEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val slot = event.newSlot
        if (slot == 4) return

        if (senderID == uuid) rhythmTeams.forEach {
            newBlock(RhythmBlock(rhythmStudioCenter.clone().add(getVector(slot, it.key.direction)), it.value, Tap.ITEM.fromItemStack(ItemStack(Material.WOOL, 1, getShort(slot))), slot))
        }
        if (rhythmReceivers.keys.contains(uuid)) {
            val team = ((onlineRhythmPlayers[player]?: throw NullPointerException("Receiver cannot be null")) as RhythmReceiver).team
            var highestScore = -rhythmLength
            var highestBlock: RhythmBlock? = null
            var score: Int
            rhythmBlocks[team.color]?.forEach {
                if (it.slot == slot) {
                    val progress = (it.ticks * moveSpeed / 20) - rhythmLength
                    if (progress > highestScore) {
                        highestBlock = it
                        highestScore = progress
                    }
                }
            }
            highestBlock?.let {
                println(highestScore)
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
                        Packet.INFO.chat(ChatColor.RED.toString() + "MISS", GAME_INFO).sendTo(player)
                        team.connection = 0
                        team.multiplier = 1
                        return@let
                    }
                }
                team.addScore(score)
                it.destroy()
            }
        }
        player.inventory.heldItemSlot = 4
    }

    @EventHandler fun onEvent(event: BlockBreakEvent) = cancel(event)
    @EventHandler fun onEvent(event: BlockDamageEvent) = cancel(event)
    @EventHandler fun onEvent(event: BlockPlaceEvent) = cancel(event)
    @EventHandler fun onEvent(event: EntityDamageEvent) = cancel(event)
    @EventHandler fun onEvent(event: EntityInteractEvent) = cancel(event)
    @EventHandler fun onEvent(event: EntitySpawnEvent) = cancel(event)
    @EventHandler fun onEvent(event: InventoryInteractEvent) = cancel(event)
    @EventHandler fun onEvent(event: InventoryOpenEvent) = cancel(event)
    @EventHandler fun onEvent(event: PlayerDropItemEvent) = cancel(event)
    @EventHandler fun onEvent(event: PlayerGameModeChangeEvent) = cancel(event)
    @EventHandler fun onEvent(event: PlayerInteractEvent) = cancel(event)

    private fun cancel(event: Cancellable) {
        event.isCancelled = true
    }

    private fun getVector(slot: Int, direction: RhythmDirection) = when (direction) {
        RhythmDirection.NORTH -> Vector((slot - 4) * -1.0, -1.0, -5.0)
        RhythmDirection.EAST -> Vector(5.0, -1.0, (slot - 4) * -1.0)
        RhythmDirection.SOUTH -> Vector((slot - 4) * 1.0, -1.0, 5.0)
        RhythmDirection.WEST -> Vector(-5.0, -1.0, (slot - 4) * 1.0)
    }

    private fun getShort(slot: Int) = when (slot) {
        0, 8 -> 14
        1, 7 -> 4
        2, 6 -> 5
        3, 5 -> 9
        else -> 0
    }.toShort()
}