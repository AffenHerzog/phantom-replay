package de.affenherzog.phantomreplay.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.affenherzog.phantomreplay.application.ReplayManagementService;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.VisibilityScope;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.replay.Position;
import de.affenherzog.phantomreplay.replay.Replay;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Optional;

import static de.affenherzog.phantomreplay.util.MUtil.MM;

@RequiredArgsConstructor
public class ReplayCommand implements PhantomCommand {

    private final PlaybackManager playbackManager;
    private final PhantomPlayerManager phantomPlayerManager;

    private final ReplayManagementService replayManagementService;

    private static final String REPLAY_NAME_ARGUMENT = "replay_name";

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("replay")
                .requires(source -> source.getSender() instanceof Player && source.getSender().hasPermission("phantomreplay.replay"))
                .then(Commands.argument(REPLAY_NAME_ARGUMENT, StringArgumentType.word())
                        .executes(this::executeInfo)
                        .suggests((context, builder) -> {
                            Player player = (Player) context.getSource().getSender();
                            phantomPlayerManager.getPhantomPlayer(player.getUniqueId()).ifPresent(ph ->
                                    ph.getOwnedReplays().forEach(it -> builder.suggest(it.getUniqueName()))
                            );
                            return builder.buildFuture();
                        })

                        .then(Commands.literal("play")
                                .requires(source -> source.getSender().hasPermission("phantomreplay.replay.play"))
                                .then(Commands.argument("state", BoolArgumentType.bool())
                                        .executes(this::executePlay)
                                )
                        )
                        .then(Commands.literal("visibility")
                                .requires(source -> source.getSender().hasPermission("phantomreplay.replay.visibility"))
                                .then(Commands.argument("scope", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            builder.suggest("GLOBAL");
                                            builder.suggest("PRIVAT");
                                            return builder.buildFuture();
                                        })
                                        .executes(this::executeVisibility)
                                )
                        )
                        .then(Commands.literal("rename")
                                .requires(source -> source.getSender().hasPermission("phantomreplay.replay.rename"))
                                .then(Commands.argument("new_name", StringArgumentType.word())
                                        .executes(this::executeRename)
                                )
                        )
                        .then(Commands.literal("delete")
                                .requires(source -> source.getSender().hasPermission("phantomreplay.replay.delete"))
                                .executes(this::executeDelete)
                        )
                ).build();
    }

    private int executePlay(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String replayName = StringArgumentType.getString(context, REPLAY_NAME_ARGUMENT);
        boolean state = BoolArgumentType.getBool(context, "state");

        Replay replay = getReplayOrSendError(player, replayName);
        if (replay == null) return 0;

        boolean success = playbackManager.updateActiveSession(replay.id(), state);

        if (success) {
            player.sendMessage(MM.deserialize("<green>Replay <yellow>" + replayName + "</yellow> Play-Status auf <gold>" + state + "</gold> gesetzt."));
            return Command.SINGLE_SUCCESS;
        } else {
            player.sendMessage(MM.deserialize("<red>Konnte den Play-Status für <yellow>" + replayName + "</yellow> nicht ändern, ist es bereits so eingestellt?"));
            return 0;
        }
    }

    private int executeInfo(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String replayName = StringArgumentType.getString(context, REPLAY_NAME_ARGUMENT);

        Replay replay = getReplayOrSendError(player, replayName);
        if (replay == null) return 0;

        Position startPosition = replay.getStartPosition();
        String startPositionString = startPosition.getBlockX() + " " + startPosition.getBlockY() + " " + startPosition.getBlockZ();

        playbackManager.findSessionModelByReplayId(replay.id()).ifPresentOrElse(session ->
                player.sendMessage(MM.deserialize(
                        "<green>Replay <yellow>" + replayName + "</yellow>:" +
                                " <gray><br> Aktiv:</gray> <gold>" + session.active() + "</gold>" +
                                " <gray><br> Sichtbarkeit:</gray> <gold>" + session.visibilityScope().name() + "</gold>" +
                                " <gray><br> Session-Id:</gray> <gold>" + session.id() + "</gold>" +
                                " <gray><br> Start Position:</gray> <gold>" + startPositionString + "</gold>"
                )), () -> player.sendMessage(MM.deserialize("<red>Für <yellow>" + replayName + "</yellow>  wurde keine Playback-Sitzung gefunden.")));

        return Command.SINGLE_SUCCESS;
    }

    private int executeVisibility(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String replayName = StringArgumentType.getString(context, REPLAY_NAME_ARGUMENT);
        String scopeInput = StringArgumentType.getString(context, "scope").toUpperCase();

        VisibilityScope scopeEnum;
        try {
            scopeEnum = VisibilityScope.valueOf(scopeInput);
        } catch (IllegalArgumentException _) {
            player.sendMessage(MM.deserialize("<red>Ungültige Sichtbarkeit! Bitte nutze GLOBAL oder PRIVAT."));
            return 0;
        }

        Replay replay = getReplayOrSendError(player, replayName);
        if (replay == null) return 0;

        boolean success = playbackManager.updateVisibilitySession(replay.id(), scopeEnum);

        if (success) {
            player.sendMessage(MM.deserialize("<green>Sichtbarkeit für <yellow>" + replayName + "</yellow> ist nun <gold>" + scopeEnum.name() + "</gold>."));
            return Command.SINGLE_SUCCESS;
        } else {
            player.sendMessage(MM.deserialize("<red>Konnte die Sichtbarkeit für <yellow>" + replayName + "</yellow> nicht ändern. Ist die Sichtbarkeit bereits so eingestellt?"));
            return 0;
        }
    }

    private int executeRename(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String oldName = StringArgumentType.getString(context, REPLAY_NAME_ARGUMENT);
        String newName = StringArgumentType.getString(context, "new_name");

        Replay replay = getReplayOrSendError(player, oldName);
        if (replay == null) return 0;

        ReplayManagementService.RenameResult renameResult = replayManagementService.renameReplay(replay, player.getUniqueId(), newName);

        if (renameResult == ReplayManagementService.RenameResult.SUCCESS) {
            player.sendMessage(renameResult.getMessageComponent(oldName, newName));
            return Command.SINGLE_SUCCESS;
        }

        player.sendMessage(renameResult.getMessageComponent());
        return Command.SINGLE_SUCCESS;
    }

    private int executeDelete(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String replayName = StringArgumentType.getString(context, REPLAY_NAME_ARGUMENT);

        Replay replay = getReplayOrSendError(player, replayName);
        if (replay == null) return 0;

        replayManagementService.deleteReplay(replay, player.getUniqueId());

        player.sendMessage(MM.deserialize("<green>Replay <yellow>" + replayName + "</yellow> wurde gelöscht."));

        return Command.SINGLE_SUCCESS;
    }

    private Replay getReplayOrSendError(Player player, String replayName) {
        Optional<Replay> optReplay = phantomPlayerManager.getPhantomPlayer(player.getUniqueId())
                .flatMap(ph -> ph.getOwnedReplays().stream()
                        .filter(it -> it.getUniqueName().equals(replayName))
                        .findFirst());

        if (optReplay.isEmpty()) {
            player.sendMessage(MM.deserialize("<red>Du besitzt kein Replay mit dem Namen <yellow>" + replayName + "</yellow>."));
            return null;
        }

        return optReplay.get();
    }
}
