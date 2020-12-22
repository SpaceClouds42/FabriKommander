package me.gserv.fabrikommander.data

import com.charleskorn.kaml.Yaml
import me.gserv.fabrikommander.data.spec.Player
import me.gserv.fabrikommander.data.spec.Pos
import me.gserv.fabrikommander.data.spec.Spawn
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.util.Identifier
import net.minecraft.util.WorldSavePath
import org.apache.logging.log4j.LogManager
import java.nio.file.Path
import kotlin.NoSuchElementException

object SpawnDataManager {
    private val logger = LogManager.getLogger(this::class.java)

    private lateinit var dataDir: Path

    fun setup() {
        ServerLifecycleEvents.SERVER_STARTING.register {
            dataDir = it.getSavePath(WorldSavePath.ROOT).resolve("FabriKommander")

            dataDir.toFile().mkdir()

            logger.info("Data directory: $dataDir")
        }
    }

    fun loadData(): Spawn {
        val spawnFile = dataDir.resolve("spawn.yaml").toFile()

        if (!spawnFile.exists()) {
            spawnFile.createNewFile()

            val data = Spawn(pos = Pos(
                0.0,
                75.0,
                0.0,

                0F,
                0F,

                Identifier("minecraft:overworld")
            ))

            spawnFile.writeText(
                Yaml.default.encodeToString(Spawn.serializer(), data)
            )

            return data
        }

        val string = spawnFile.readText()
        return Yaml.default.decodeFromString(Spawn.serializer(), string)
    }

    fun setSpawn(spawn: Spawn): Boolean? {
        saveData(spawn)

        return true
    }


    fun getSpawn(): Spawn {
        return loadData()
    }

    fun saveData(spawn: Spawn) {
        val spawnFile = dataDir.resolve("spawn.yaml").toFile()
            ?: throw NoSuchElementException("No spawn config found")

        if (!spawnFile.exists()) {
            spawnFile.createNewFile()
        }

        spawnFile.writeText(
            Yaml.default.encodeToString(Spawn.serializer(), spawn)
        )
    }
}
