package me.gserv.fabrikommander.commands

import me.gserv.fabrikommander.data.SpawnDataManager
import me.gserv.fabrikommander.data.spec.Spawn
import me.gserv.fabrikommander.data.spec.Pos
import me.gserv.fabrikommander.utils.*
import net.minecraft.server.command.CommandManager

class SetSpawnCommand(val dispatcher: Dispatcher) {
    fun register() {
        dispatcher.register(
            CommandManager.literal("setspawn")
                .executes { setSpawnCommand(it) }
                .requires { it.hasPermissionLevel(2) }
        )
    }

    fun setSpawnCommand(context: Context): Int {
        val player = context.source.player

        val spawn = Spawn(
            pos = Pos(
                x = player.x,
                y = player.y,
                z = player.z,
                yaw = player.yaw,
                pitch = player.pitch,
                world = player.world.registryKey.value
            ),
        )

        SpawnDataManager.setSpawn(spawn)

        context.source.sendFeedback(
            green("Spawn created at ") +
                    aqua("[${player.x.toInt()}, ${player.y.toInt()}, ${player.z.toInt()}]") +
                    green(" in the ") +
                    aqua(identifierToWorldName(player.world.registryKey.value)),
            true
        )

        return 1
    }
}
