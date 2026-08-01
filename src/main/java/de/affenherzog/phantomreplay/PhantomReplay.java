package de.affenherzog.phantomreplay;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.affenherzog.phantomreplay.command.RecordCommand;
import de.affenherzog.phantomreplay.database.DatabaseManager;
import de.affenherzog.phantomreplay.listener.PlayerJoinListener;
import de.affenherzog.phantomreplay.listener.PlayerQuitListener;
import de.affenherzog.phantomreplay.listener.PlayerSwingArmListener;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.player.PlayerRepository;
import de.affenherzog.phantomreplay.record.RecordingManager;
import de.affenherzog.phantomreplay.replay.ReplayRepository;
import de.affenherzog.phantomreplay.util.PluginSettings;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class PhantomReplay extends JavaPlugin {

    private final PluginSettings pluginSettings = new PluginSettings();

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
        pluginSettings.load(getConfig());

        if (!setupDatabase()) {
            return;
        }

        protocolManager = ProtocolLibrary.getProtocolManager();

        replayRepository = new ReplayRepository(databaseManager.getDataSource(), log);
        playerRepository = new PlayerRepository(databaseManager.getDataSource(), log);

        phantomPlayerManager = new PhantomPlayerManager(new HashMap<>());
        recordingManager = new RecordingManager(this, pluginSettings, phantomPlayerManager, replayRepository, new ConcurrentHashMap<>());

        registerListener();
        registerCommands();

        log.info("PhantomReplay wurde erfolgreich gestartet.");
    }

    private void registerCommands() {
        RecordCommand recordCommand = new RecordCommand(recordingManager);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register(recordCommand.build()));
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
            log.error("Konnte keine Verbindung zur Datenbank herstellen!");
            log.error("Bitte überprüfe die Zugangsdaten in der config.yml.");
            log.error("Das Plugin wird nun deaktiviert.");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        if (!this.databaseManager.initSchema()) {
            log.error("Konnte die Datenbank-Tabellen nicht erstellen!");
            log.error("Das Plugin wird nun deaktiviert.");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        log.info("Datenbank-Setup vollständig und erfolgreich.");
        return true;
    }

    private void registerListener() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this, phantomPlayerManager, replayRepository, playerRepository), this);
        pluginManager.registerEvents(new PlayerQuitListener(phantomPlayerManager, recordingManager), this);
        pluginManager.registerEvents(new PlayerSwingArmListener(recordingManager), this);
    }
}
