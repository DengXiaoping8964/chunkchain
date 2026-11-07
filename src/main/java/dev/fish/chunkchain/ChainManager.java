package dev.fish.chunkchain;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChainManager {
    private static final Map<UUID, ChainState> STATES = new ConcurrentHashMap<>();

    private ChainManager() {}

    public static ChainState get(ServerPlayerEntity player) {
        return STATES.computeIfAbsent(player.getUuid(), k -> new ChainState());
    }

    public static void remove(UUID uuid) {
        STATES.remove(uuid);
    }
}