package de.affenherzog.phantomReplay.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.affenherzog.phantomReplay.record.RecordingManager;
import de.affenherzog.phantomReplay.util.MUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class RecordCommand {

    private final RecordingManager recordingManager;

    public RecordCommand(RecordingManager recordingManager) {
        this.recordingManager = recordingManager;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("replay")
                .requires(source -> source.getSender().hasPermission("phantomreplay.replay"))
                .then(Commands.literal("start")
                        .executes(ctx -> this.executeStart(ctx.getSource())))
                .then(Commands.literal("stop")
                        .executes(ctx -> this.executeStop(ctx.getSource())))
                .build();
    }

    private int executeStart(CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Nur Spieler können das tun!");
            return Command.SINGLE_SUCCESS;
        }

        if (!recordingManager.startRecording(player.getUniqueId())) {
            player.sendMessage(MUtil.parse("<red>Die Aufnahme läuft bereits!"));
            return Command.SINGLE_SUCCESS;
        }
        player.sendMessage(MUtil.parse("<green>Du hast eine neue Aufnahme gestartet!"));

        return Command.SINGLE_SUCCESS;
    }

    private int executeStop(CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Nur Spieler können das tun!");
            return Command.SINGLE_SUCCESS;
        }

        if (!recordingManager.saveRecording(player.getUniqueId())) {
            player.sendMessage(MUtil.parse("<red>Du hast noch keine Aufnahme zu speichern!"));
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(MUtil.parse("<green>Aufnahme gespeichert!"));
        return Command.SINGLE_SUCCESS;
    }


}
