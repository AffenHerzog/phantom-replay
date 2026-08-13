package de.affenherzog.phantomreplay.replay;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.UUID;

public record Replay(int id, String name, UUID uuid, List<KeyFrame> keyFrames) {

    public static final int DEFAULT_ID = -1;
    public static final String DEFAULT_NAME = "Neues-Replay";

    public Component getComponentName() {
        return Component.text(name);
    }

    public String getUniqueName() {
        if (name.equals(DEFAULT_NAME)) return id + "-" + name;
        return name;
    }

    public Replay withName(String newName) {
        return new Replay(this.id, newName, this.uuid, this.keyFrames);
    }

    public Position getStartPosition() {
        return keyFrames.getFirst().frame().position();
    }

}
