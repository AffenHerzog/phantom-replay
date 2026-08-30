package de.affenherzog.phantomreplay.util;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class PluginSettings {

    private static PluginSettings instance;

    private int maxTicksRecord;
    private int recordingCooldownInSeconds;

    public PluginSettings(FileConfiguration config) {
        load(config);
        instance = this;
    }

    public void load(FileConfiguration config) {
        this.maxTicksRecord = config.getInt("record.max-ticks");
        this.recordingCooldownInSeconds = config.getInt("record.cooldown-in-seconds");
    }

    public static PluginSettings get() {
        if (instance == null) {
            throw new IllegalStateException("PluginSettings wurden noch nicht geladen!");
        }
        return instance;
    }
}