package dev.fish.chunkchain;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public final class ChainCommand {
    private ChainCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(build("chunkchain"));
            dispatcher.register(build("cc"));
        });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> build(String name) {
        return CommandManager.literal(name)
            .executes(ctx -> status(ctx.getSource()))
            .then(CommandManager.literal("on").executes(ctx -> set(ctx.getSource(), true)))
            .then(CommandManager.literal("off").executes(ctx -> set(ctx.getSource(), false)))
            .then(CommandManager.literal("toggle").executes(ctx -> toggle(ctx.getSource())))
            .then(CommandManager.literal("cycle").executes(ctx -> cycle(ctx.getSource())))
            .then(CommandManager.literal("status").executes(ctx -> status(ctx.getSource())))
            .then(CommandManager.literal("size")
                .then(CommandManager.argument("n", IntegerArgumentType.integer(3, 11))
                    .executes(ctx -> setSize(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "n")))));
    }

    private static int set(ServerCommandSource source, boolean on) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        ChainManager.get(player).enabled = on;
        ChainNetworking.sync(player);
        return 1;
    }

    private static int toggle(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        ChainState st = ChainManager.get(player);
        st.enabled = !st.enabled;
        ChainNetworking.sync(player);
        return 1;
    }

    private static int cycle(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        ChainManager.get(player).cycleSize();
        ChainNetworking.sync(player);
        return 1;
    }

    private static int status(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        source.sendFeedback(ChainNetworking.statusText(ChainManager.get(player)), false);
        return 1;
    }

    private static int setSize(ServerCommandSource source, int n) throws CommandSyntaxException {
        if (!ChainState.isValidSize(n)) {
            source.sendError(new LiteralText("范围必须是 3 / 5 / 7 / 9 / 11"));
            return 0;
        }
        ServerPlayerEntity player = source.getPlayer();
        ChainManager.get(player).size = n;
        ChainNetworking.sync(player);
        return 1;
    }
}