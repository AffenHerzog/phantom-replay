package de.affenherzog.phantomReplay.replay;

import org.bukkit.Location;
import org.bukkit.World;

public record Position(double x, double y, double z, float yaw, float pitch) {

    public Location toBukkitLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

}