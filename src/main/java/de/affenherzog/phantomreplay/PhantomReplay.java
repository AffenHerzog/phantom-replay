package de.affenherzog.phantomreplay;

import de.affenherzog.phantomreplay.command.PhantomCommand;
import de.affenherzog.phantomreplay.command.RecordCommand;
import de.affenherzog.phantomreplay.command.ReplayCommand;
import de.affenherzog.phantomreplay.database.DatabaseManager;
import de.affenherzog.phantomreplay.playback.*;
import de.affenherzog.phantomreplay.record.RecordingScheduler;
import de.affenherzog.phantomreplay.session.PlayerJoinListener;
import de.affenherzog.phantomreplay.session.PlayerLoginService;
import de.affenherzog.phantomreplay.session.PlayerLogoutService;
import de.affenherzog.phantomreplay.session.PlayerQuitListener;
import de.affenherzog.phantomreplay.record.RecordingArmSwingListener;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.player.PlayerRepository;
import de.affenherzog.phantomreplay.record.RecordingManager;
import de.affenherzog.phantomreplay.replay.ReplayRepository;
import de.affenherzog.phantomreplay.util.PluginSettings;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class PhantomReplay extends JavaPlugin {

    private final PluginSettings pluginSettings = new PluginSettings();

    private DatabaseManager databaseManager;

    private ReplayRepository replayRepository;

    private RecordingScheduler recordingScheduler;
    private RecordingManager recordingManager;

    private PlaybackScheduler playbackScheduler;
    private PlaybackManager playbackManager;
    private PlaybackRepository playbackRepository;
    private PlaybackSessionService playbackSessionService;

    private PlayerRepository playerRepository;
    private PhantomPlayerManager phantomPlayerManager;

    private Logger log;
    private ComponentLogger componentLogger;

    @Override
    public void onEnable() {
        log = getSLF4JLogger();
        componentLogger = getComponentLogger();

        saveDefaultConfig();
        pluginSettings.load(getConfig());

        if (!setupDatabase()) {
            return;
        }

        replayRepository = new ReplayRepository(databaseManager.getDataSource(), componentLogger);
        playerRepository = new PlayerRepository(databaseManager.getDataSource(), componentLogger);

        phantomPlayerManager = new PhantomPlayerManager(new HashMap<>());
        recordingScheduler = new RecordingScheduler(new ConcurrentHashMap<>());
        recordingManager = new RecordingManager(this, pluginSettings, phantomPlayerManager, replayRepository, recordingScheduler);

        playbackScheduler = new PlaybackScheduler(new ConcurrentHashMap<>());
        playbackRepository = new PlaybackRepository(databaseManager.getDataSource(), componentLogger);
        playbackManager = new PlaybackManager(playbackScheduler, playbackRepository);
        playbackSessionService = new PlaybackSessionService(this, playbackRepository, playbackManager);

        registerListener();
        registerCommands();
        registerScheduler();

        log.info("PhantomReplay wurde erfolgreich gestartet.");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.disconnect();
        unregisterScheduler();
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
        final PlayerLoginService playerLoginService = new PlayerLoginService(playerRepository, replayRepository, playbackRepository, phantomPlayerManager, playbackManager, this);
        final PlayerLogoutService playerLogoutService = new PlayerLogoutService(phantomPlayerManager, recordingManager, playbackManager);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(playerLoginService), this);
        pluginManager.registerEvents(new PlayerQuitListener(playerLogoutService), this);
        pluginManager.registerEvents(new RecordingArmSwingListener(recordingManager), this);
        pluginManager.registerEvents(new PlaybackReplaySavedListener(playbackSessionService), this);
    }

    private void registerCommands() {
        List<PhantomCommand> commands = List.of(
                new RecordCommand(recordingManager),
                new ReplayCommand(playbackManager, replayRepository, phantomPlayerManager)
        );

        commands.forEach(it ->
                this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                        event -> event.registrar().register(it.build())));
    }

    private void registerScheduler() {
        recordingScheduler.runTaskTimer(this, 0, 1);
        playbackScheduler.runTaskTimer(this, 0,1);
    }

    private void unregisterScheduler() {
        if (recordingScheduler != null) recordingScheduler.cancel();
        if (playbackScheduler != null) playbackScheduler.cancel();
    }
}
