package me.gserv.fabrikommander.commands

import com.mojang.brigadier.arguments.StringArgumentType
import me.gserv.fabrikommander.data.SpawnDataManager
import me.gserv.fabrikommander.utils.Context
import me.gserv.fabrikommander.utils.Dispatcher
import me.gserv.fabrikommander.utils.aqua
import me.gserv.fabrikommander.utils.green
import me.gserv.fabrikommander.utils.identifierToWorldName
import me.gserv.fabrikommander.utils.plus
import me.gserv.fabrikommander.utils.red
import me.gserv.fabrikommander.utils.yellow
import net.minecraft.server.command.CommandManager
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

class SpawnCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            CommandManager.literal("spawn")
                .executes { spawnCommand(it) }
        )
    }

    fun spawnCommand(context: Context): Int {
        val player = context.source.player
        val spawn = SpawnDataManager.getSpawn()

        val world = player.server.getWorld(RegistryKey.of(Registry.DIMENSION, spawn.pos.world))

        if (world == null) {
            context.source.sendFeedback(
                red("Warp ") +
                        aqua("spawn") +
                        red(" is in a world ") +
                        yellow("(") +
                        aqua(identifierToWorldName(spawn.pos.world)) +
                        yellow(") ") +
                        red("that no longer exists."),
                false
            )
        } else {

            player.teleport(world, spawn.pos.x, spawn.pos.y, spawn.pos.z, spawn.pos.yaw, spawn.pos.pitch)

            context.source.sendFeedback(
                green("Teleported to spawn!"),
                true
            )
        }

        return 1
    }
}
