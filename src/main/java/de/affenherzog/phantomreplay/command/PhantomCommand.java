package de.affenherzog.phantomreplay.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public interface PhantomCommand {

    LiteralCommandNode<CommandSourceStack> build();

}
