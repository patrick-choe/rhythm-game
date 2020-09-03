package com.github.patrick.rhythm

import com.github.patrick.rhythm.plugin.RhythmPlugin
import com.github.patrick.rhythm.util.RhythmBlock
import com.github.patrick.rhythm.util.RhythmColor
import com.github.patrick.rhythm.util.RhythmPlayer
import com.github.patrick.rhythm.util.RhythmReceiver
import com.github.patrick.rhythm.util.RhythmSender
import com.github.patrick.rhythm.util.RhythmTeam
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.IdentityHashMap
import java.util.LinkedList
import java.util.UUID

/**
 * Plugin-wide Variables
 */
internal lateinit var instance: RhythmPlugin
internal lateinit var rhythmMusic: String
internal var rhythmStudioLength = 0
internal var moveSpeed = 0.0
internal var rhythmModifier = 0.0
internal var pointPoop = 0.0
internal var pointGood = 0.0
internal var pointGreat = 0.0
internal var pointPerfect = 0.0
internal var pointDestroy = 0.0
internal lateinit var rhythmStudioCenter: Location
internal lateinit var rhythmGiver: String
internal val rhythmColorPlayers = HashMap<RhythmColor, String>()

var rhythmStatus = false
var rhythmLength = rhythmStudioLength + rhythmModifier
var totalTicks = 20 / moveSpeed
val rhythmTeams = HashMap<RhythmColor, RhythmTeam>()
val rhythmReceivers = HashMap<UUID, RhythmReceiver>()
val onlineRhythmPlayers = IdentityHashMap<Player, RhythmPlayer>()
lateinit var rhythmSender: RhythmSender
val rhythmBlocks = HashMap<RhythmColor, Array<LinkedList<RhythmBlock>>>()

fun newBlock(block: RhythmBlock, slot: Int) = rhythmBlocks[block.team.color]?.get(slot)?.offer(block)