package de.affenherzog.phantomReplay;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.affenherzog.phantomReplay.database.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public final class PhantomReplay extends JavaPlugin {

    private ProtocolManager protocolManager;
    private DatabaseManager databaseManager;

    private Logger log;

    @Override
    public void onEnable() {
        log = getSLF4JLogger();

        saveDefaultConfig();

        if (!setupDatabase()) {
            return;
        }

        protocolManager = ProtocolLibrary.getProtocolManager();

        log.info("PhantomReplay wurde erfolgreich gestartet.");
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
}
