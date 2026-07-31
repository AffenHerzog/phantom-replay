package de.affenherzog.phantomReplay.replay;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.UUID;

public record Replay(int id, String name, UUID uuid, List<Frame> frames) {

    public Component getComponentName() {
        return Component.text(name);
    }

}
