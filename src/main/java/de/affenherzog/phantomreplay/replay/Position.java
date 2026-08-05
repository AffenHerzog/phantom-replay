package de.affenherzog.phantomreplay.replay;

import com.google.gson.annotations.SerializedName;
import de.affenherzog.phantomreplay.util.MathUtils;
import org.bukkit.Location;
import org.bukkit.World;

public record Position(double x, double y, double z, @SerializedName("ry") float yaw, @SerializedName("rp") float pitch,
                       @SerializedName("w") String worldName) {

    public Location toBukkitLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Position fromBukkitLocation(Location loc) {
        return new Position(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), loc.getWorld().getName());
    }

    public static Position fromBukkitLocationRounded(Location loc) {
        return new Position(
                MathUtils.roundPosition(loc.getX()),
                MathUtils.roundPosition(loc.getY()),
                MathUtils.roundPosition(loc.getZ()),
                MathUtils.roundRotation(loc.getYaw()),
                MathUtils.roundRotation(loc.getPitch()),
                loc.getWorld().getName());
    }

}