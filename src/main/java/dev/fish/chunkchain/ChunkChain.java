package dev.fish.chunkchain;

import net.fabricmc.api.ModInitializer;

public final class ChunkChain implements ModInitializer {
    @Override
    public void onInitialize() {
        // 本鱼写的，厉害吧 (´｡• ᵕ •｡`)
        ChainHandler.register();
        ChainCommand.register();
        ChainNetworking.registerServer();
    }
}