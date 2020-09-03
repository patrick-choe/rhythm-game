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

import com.github.noonmaru.tap.Tap
import com.github.noonmaru.tap.entity.TapArmorStand
import com.github.noonmaru.tap.item.TapItemStack
import com.github.noonmaru.tap.packet.Packet
import com.github.patrick.rhythm.*
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

class RhythmBlock(loc: Location, val team: RhythmTeam, private val bulletItem: TapItemStack) {
    private val tapArmorStand: TapArmorStand? = Tap.ENTITY.createEntity(ArmorStand::class.java)
    private val armorStand: ArmorStand?
    private var removed = false
    var dead = false
        private set
    var ticks = 0
        private set

    init {
        tapArmorStand?.setUp()
        armorStand = tapArmorStand?.bukkitEntity
        spawnTo(getOnlinePlayers())
        tapArmorStand?.setPositionAndRotation(loc.x, loc.y, loc.z, 45F, 0F)
    }

    fun destroy() {
        tapArmorStand?.let { Packet.ENTITY.destroy(it.id).sendAll() }
        dead = true
    }

    fun spawnTo(players: Collection<Player?>?) = players?.let {
        it.updateEquipmentTo()
        Packet.ENTITY.metadata(armorStand).sendTo(it)
        tapArmorStand?.apply { Packet.ENTITY.teleport(armorStand, posX, posY, posZ, 45F, 0F, false).sendTo(players) }
        if (ticks < 1) Packet.ENTITY.spawnMob(armorStand).sendTo(it)
    }

    private fun TapArmorStand.setUp() = apply {
        isInvisible = true
        isMarker = true
        setGravity(false)
        setBasePlate(false)
    }

    private fun Collection<Player?>.updateEquipmentTo() = apply {
        tapArmorStand?.id?.let { Packet.ENTITY.equipment(it, EquipmentSlot.HEAD, bulletItem).sendTo(this) }
    }

    fun onUpdate() {
        armorStand?.location?.apply {
            spawnTo(getOnlinePlayers())
            if (dead) return

            if (++ticks == 2) getOnlinePlayers().updateEquipmentTo()

            if (!removed) {
                if ((ticks * moveSpeed / 20) - rhythmLength > pointDestroy) {
                    removed = true
                    return
                }
                tapArmorStand?.setPositionAndRotation(x + moveSpeed * team.color.direction.dx / 20, y, z + moveSpeed * team.color.direction.dz / 20, 45F, 0F)
            } else {
                destroy()
                team.miss()
            }
        }
    }
}