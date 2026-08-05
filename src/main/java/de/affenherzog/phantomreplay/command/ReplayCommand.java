package de.affenherzog.phantomreplay.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.PlaybackSessionModel;
import de.affenherzog.phantomreplay.playback.VisibilityScope;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.replay.Replay;
import de.affenherzog.phantomreplay.replay.ReplayRepository;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class ReplayCommand implements PhantomCommand {

    private final PlaybackManager playbackManager;
    private final ReplayRepository replayRepository;
    private final PhantomPlayerManager phantomPlayerManager;

    private static final String REPLAY_NAME_ARGUMENT = "replay_name";

    public ReplayCommand(PlaybackManager playbackManager, ReplayRepository replayRepository, PhantomPlayerManager phantomPlayerManager) {
        this.playbackManager = playbackManager;
        this.replayRepository = replayRepository;
        this.phantomPlayerManager = phantomPlayerManager;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("replay")
                .requires(source -> source.getSender().hasPermission("phantomreplay.replay"))
                .then(Commands.argument(REPLAY_NAME_ARGUMENT, StringArgumentType.word())
                        .suggests((context, builder) -> {
                            if (context.getSource().getSender() instanceof Player player) {
                                phantomPlayerManager.getPhantomPlayer(player.getUniqueId()).ifPresent(ph ->
                                        ph.getPlaybackManager().getUniqueSessionNames().forEach(it -> {
                                            Component coloredTooltip = MiniMessage.miniMessage().deserialize("<gold>Klicke für Replay: " + it);
                                            builder.suggest(it, MessageComponentSerializer.message().serialize(coloredTooltip));
                                        }));
                            }
                            return builder.buildFuture();
                        })

                        .then(Commands.literal("play")
                                .then(Commands.argument("state", BoolArgumentType.bool())
                                        .executes(context -> {
                                            String replayName = StringArgumentType.getString(context, REPLAY_NAME_ARGUMENT);
                                            boolean state = BoolArgumentType.getBool(context, "state");

                                            playbackManager.getSessions().values().stream().filter(it ->
                                                    replayName.equals(it.getModel().replay().getUniqueName())).findFirst().ifPresent(it ->
                                                    playbackManager.updateActiveSession(it.getModel().id(), state));

                                            context.getSource().getSender().sendMessage("Replay " + replayName + " play: " + state);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )

                        .then(Commands.literal("visibility")
                                .then(Commands.argument("scope", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            builder.suggest("GLOBAL");
                                            builder.suggest("PRIVAT");
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> {
                                            String replayName = StringArgumentType.getString(context, REPLAY_NAME_ARGUMENT);
                                            String scope = StringArgumentType.getString(context, "scope");

                                            VisibilityScope scopeEnum = VisibilityScope.valueOf(scope.toUpperCase());

                                            playbackManager.getSessions().values().stream().filter(it ->
                                                    replayName.equals(it.getModel().replay().getUniqueName())).findFirst().ifPresent(it ->
                                                    playbackManager.updateVisibilitySession(it.getModel().id(), scopeEnum));

                                            context.getSource().getSender().sendMessage("Replay " + replayName + " visibility: " + scope);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )

                        .then(Commands.literal("rename")
                                .then(Commands.argument("new_name", StringArgumentType.word())
                                        .executes(context -> {
                                            String replayName = StringArgumentType.getString(context, REPLAY_NAME_ARGUMENT);
                                            String newName = StringArgumentType.getString(context, "new_name");

                                            playbackManager.getSessions().values().stream().filter(it ->
                                                    replayName.equals(it.getModel().replay().getUniqueName())).findFirst().ifPresent(it -> {

                                                Replay newReplay = it.getModel().replay().withName(newName);
                                                PlaybackSessionModel model = it.getModel().withReplay(newReplay);
                                                it.setModel(model);
                                                replayRepository.updateReplayName(newReplay.id(), newReplay.name());
                                            });

                                            context.getSource().getSender().sendMessage("Replay " + replayName + " renamed to " + newName);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .build();
    }

}
