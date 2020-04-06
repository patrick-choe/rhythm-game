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

import com.github.noonmaru.tap.ChatType.GAME_INFO
import com.github.noonmaru.tap.Tap
import com.github.noonmaru.tap.entity.TapArmorStand
import com.github.noonmaru.tap.item.TapItemStack
import com.github.noonmaru.tap.packet.Packet
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.moveSpeed
import com.github.patrick.rhythm.plugin.RhythmPlugin.Companion.pointDestroy
import com.github.patrick.rhythm.process.RhythmGame.Companion.rhythmLength
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.ChatColor.RED
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

class RhythmBlock(loc: Location, val team: RhythmTeam, private val bulletItem: TapItemStack, val slot: Int) {
    private val tapArmorStand: TapArmorStand? = Tap.ENTITY.createEntity(ArmorStand::class.java)
    private val armorStand: ArmorStand?
    private var removed = false
    var dead = false
        private set
    var ticks = 0
        private set

    init {
        tapArmorStand?.apply { setUp(this) }
        armorStand = tapArmorStand?.bukkitEntity
        spawnTo(getOnlinePlayers())
        tapArmorStand?.setPosition(loc.x, loc.y, loc.z)
    }

    fun destroy() {
        tapArmorStand?.let { Packet.ENTITY.destroy(it.id).sendAll() }
        dead = true

    }

    fun spawnTo(players: Collection<Player?>?) = players?.let { collection ->
        updateEquipmentTo(collection)
        Packet.ENTITY.metadata(armorStand).sendTo(collection)
        tapArmorStand?.let { Packet.ENTITY.teleport(armorStand, it.posX, it.posY, it.posZ, it.yaw, it.pitch, false).sendTo(players) }
        if (ticks < 1) Packet.ENTITY.spawnMob(armorStand).sendTo(collection)
    }

    private fun setUp(stand: TapArmorStand) {
        stand.let {
            it.isInvisible = true
            it.isMarker = true
            it.setGravity(false)
            it.setBasePlate(false)
        }
    }

    private fun updateEquipmentTo(players: Collection<Player?>?) = players?.let { collection ->
        tapArmorStand?.id?.let { Packet.ENTITY.equipment(it, EquipmentSlot.HEAD, bulletItem).sendTo(collection) }
    }

    fun onUpdate() {
        armorStand?.location?.let {
            spawnTo(getOnlinePlayers())
            if (dead) return

            if (++ticks == 2) updateEquipmentTo(getOnlinePlayers())

            if (!removed) {
                if ((ticks * moveSpeed / 20) - rhythmLength > pointDestroy) {
                    removed = true
                    return
                }
                tapArmorStand?.setPositionAndRotation(it.x + moveSpeed * team.color.direction.dx / 20, it.y, it.z + moveSpeed * team.color.direction.dz / 20, 0F, 0F)
            } else {
                destroy()
                Packet.INFO.chat(RED.toString() + "MISS", GAME_INFO).sendTo(team.rhythmReceiver.player)
                team.connection = 0
                team.multiplier = 1
            }
        }
    }
}