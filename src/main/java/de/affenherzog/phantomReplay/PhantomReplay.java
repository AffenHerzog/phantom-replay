package de.affenherzog.phantomReplay;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.affenherzog.phantomReplay.command.RecordCommand;
import de.affenherzog.phantomReplay.database.DatabaseManager;
import de.affenherzog.phantomReplay.listener.PlayerJoinListener;
import de.affenherzog.phantomReplay.listener.PlayerQuitListener;
import de.affenherzog.phantomReplay.player.PhantomPlayerManager;
import de.affenherzog.phantomReplay.player.PlayerRepository;
import de.affenherzog.phantomReplay.record.RecordingManager;
import de.affenherzog.phantomReplay.replay.ReplayRepository;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.HashMap;

public final class PhantomReplay extends JavaPlugin {

    private ProtocolManager protocolManager;
    private DatabaseManager databaseManager;
    private ReplayRepository replayRepository;
    private PlayerRepository playerRepository;
    private RecordingManager recordingManager;
    private PhantomPlayerManager phantomPlayerManager;

    private Logger log;

    @Override
    public void onEnable() {
        log = getSLF4JLogger();

        saveDefaultConfig();

        if (!setupDatabase()) {
            return;
        }

        protocolManager = ProtocolLibrary.getProtocolManager();

        replayRepository = new ReplayRepository(databaseManager.getDataSource(), log);
        playerRepository = new PlayerRepository(databaseManager.getDataSource(), log);

        phantomPlayerManager = new PhantomPlayerManager(new HashMap<>());
        recordingManager = new RecordingManager(this, phantomPlayerManager, replayRepository, new HashMap<>());

        registerListener();
        registerCommands();

        log.info("PhantomReplay wurde erfolgreich gestartet.");
    }

    private void registerCommands() {
        RecordCommand recordCommand = new RecordCommand(recordingManager);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                (event) -> event.registrar().register(recordCommand.build()));
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
    }

    private boolean setupDatabase() {
        this.databaseManager = new DatabaseManager(this, log);

        if (!this.databaseManager.connect()) {
            log.error("=========================================");
            log.error("Konnte keine Verbindung zur Datenbank herstellen!");
            log.error("Bitte überprüfe die Zugangsdaten in der config.yml.");
            log.error("Das Plugin wird nun deaktiviert.");
            log.error("=========================================");

            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        if (!this.databaseManager.initSchema()) {
            log.error("=========================================");
            log.error("Konnte die Datenbank-Tabellen nicht erstellen!");
            log.error("Das Plugin wird nun deaktiviert.");
            log.error("=========================================");

            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        log.info("Datenbank-Setup vollständig und erfolgreich.");
        return true;
    }

    private void registerListener() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this, phantomPlayerManager, replayRepository, playerRepository), this);
        pluginManager.registerEvents(new PlayerQuitListener(phantomPlayerManager), this);
    }
}
