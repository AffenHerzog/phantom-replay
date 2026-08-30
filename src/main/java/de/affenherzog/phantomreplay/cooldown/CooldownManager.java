package de.affenherzog.phantomreplay.cooldown;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class CooldownManager {

    private final Plugin plugin;

    private static final long TICKS_PER_SECOND = 20;

    private final HashMap<UUID, PhantomCooldown> cooldowns = new HashMap<>();

    public void startCooldown(UUID uuid, PhantomCooldown phantomCooldown, Runnable onFinish) {
        if (cooldowns.containsKey(uuid)) {
            return;
        }
        cooldowns.put(uuid, phantomCooldown);

        int seconds = phantomCooldown.getSeconds();
        if (seconds < 0) {
            executeImmediately(uuid, phantomCooldown, onFinish);
            return;
        }

        BukkitTask task = startSecondsTimer(seconds, phantomCooldown);
        startFinishTask(seconds, uuid, phantomCooldown, onFinish, task);
    }

    private void executeImmediately(UUID uuid, PhantomCooldown phantomCooldown, Runnable onFinish) {
        phantomCooldown.onCooldownFinished();
        onFinish.run();
        cooldowns.remove(uuid);
    }

    private BukkitTask startSecondsTimer(int seconds, PhantomCooldown phantomCooldown) {
        AtomicInteger i = new AtomicInteger(seconds);
        return Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> phantomCooldown.onCooldownTick(i.getAndDecrement()),
                1L,
                TICKS_PER_SECOND
        );
    }

    private void startFinishTask(int seconds, UUID uuid, PhantomCooldown phantomCooldown, Runnable onFinish, BukkitTask task) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            phantomCooldown.onCooldownFinished();
            task.cancel();
            onFinish.run();
            cooldowns.remove(uuid);
        }, seconds * TICKS_PER_SECOND);
    }

    public void cancelCooldown(UUID uuid) {
        cooldowns.remove(uuid);
    }


}
