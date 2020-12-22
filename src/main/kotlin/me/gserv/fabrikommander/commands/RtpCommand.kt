package me.gserv.fabrikommander.commands

import me.gserv.fabrikommander.data.PlayerDataManager
import me.gserv.fabrikommander.data.spec.Pos
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.gold
import me.gserv.fabrikommander.utils.gray
import me.gserv.fabrikommander.utils.yellow
import me.gserv.fabrikommander.utils.plus
import me.gserv.fabrikommander.utils.aqua
import net.minecraft.server.command.CommandManager
import net.minecraft.util.Identifier
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.math.BlockPos

class RtpCommand(val dispatcher: Dispatcher) {
    private val rtpRange = -5000..5000

    fun register() {
        dispatcher.register(
            CommandManager.literal("rtp")
                .executes { rtpCommand(it) }
        )
        dispatcher.register(
            CommandManager.literal("wild")
                .executes { rtpCommand(it) }
        )
    }
    
    private fun generateCoordinates(world: ServerWorld?): List<Int> {
        val x = rtpRange.random()
        val z = rtpRange.random()
        val y = getHighestBlock(world, x, z)

        if (world?.isWater(BlockPos(x, y, z)) == true) {
            return generateCoordinates(world)
        }

        return listOf(x, y + 1, z)
    }

    private fun getHighestBlock(world: ServerWorld?, x: Int, z: Int): Int {
        val heightLimit = world!!.heightLimit

        for (height in heightLimit downTo 0)
            if (!world.isAir(BlockPos(x, height, z))) {
                return height
            }

        return 0
    }

    fun rtpCommand(context: Context): Int {
        val player = context.source.player
        val overworld = player.server.getWorld(RegistryKey.of(Registry.DIMENSION, Identifier("minecraft:overworld")))
        val coordinates = generateCoordinates(overworld)

        PlayerDataManager.setBackPos(
            player.uuid,
            Pos(
                x = player.x,
                y = player.y,
                z = player.z,
                world = player.world.registryKey.value,
                yaw = player.yaw,
                pitch = player.pitch
            )
        )

        player.teleport(
            overworld,
            coordinates[0].toDouble(),
            coordinates[1].toDouble(),
            coordinates[2].toDouble(),
            player.yaw,
            player.pitch
        )
        context.source.sendFeedback(
            gray("[") +
            yellow("RTP") +
            gray("] ") +
            gold("Randomly teleported to ") +
            aqua("[${player.x.toInt()}, ${player.y.toInt()}, ${player.z.toInt()}]"),
            true
        )
        

        return 1
    }
}
