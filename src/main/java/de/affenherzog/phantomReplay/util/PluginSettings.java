package de.affenherzog.phantomReplay.util;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class PluginSettings {

    private int maxTicksRecord;

    public void load(FileConfiguration config) {
        maxTicksRecord = config.getInt("record.max-ticks");
    }

}