package de.affenherzog.phantomreplay.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.affenherzog.phantomreplay.cooldown.CooldownManager;
import de.affenherzog.phantomreplay.cooldown.PhantomCooldown;
import de.affenherzog.phantomreplay.cooldown.StartRecordingCooldown;
import de.affenherzog.phantomreplay.record.RecordingManager;
import de.affenherzog.phantomreplay.util.MUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class RecordCommand implements PhantomCommand {

    private final RecordingManager recordingManager;
    private final CooldownManager cooldownManager;

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("record")
                .requires(source -> source.getSender().hasPermission("phantomreplay.record"))
                .then(Commands.literal("start")
                        .executes(ctx -> this.executeStart(ctx.getSource())))
                .then(Commands.literal("stop")
                        .executes(ctx -> this.executeStop(ctx.getSource())))
                .build();
    }

    private int executeStart(CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Nur Spieler können das tun!");
            return 0;
        }

        if (recordingManager.isRecording(player.getUniqueId())) {
            player.sendMessage(MUtil.parse("<red>Die Aufnahme läuft bereits!"));
            return 0;
        }

        PhantomCooldown cooldown = new StartRecordingCooldown(player);
        cooldownManager.startCooldown(player.getUniqueId(), cooldown, () -> {
            recordingManager.startRecording(player.getUniqueId());
            player.sendMessage(MUtil.parse("<green>Du hast eine neue Aufnahme gestartet!"));
        });

        return Command.SINGLE_SUCCESS;
    }

    private int executeStop(CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Nur Spieler können das tun!");
            return 0;
        }

        if (!recordingManager.saveRecording(player.getUniqueId())) {
            player.sendMessage(MUtil.parse("<red>Du hast noch keine Aufnahme zu speichern!"));
            return 0;
        }

        player.sendMessage(MUtil.parse("<green>Aufnahme gespeichert!"));
        return Command.SINGLE_SUCCESS;
    }


}
