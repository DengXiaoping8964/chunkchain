package dev.fish.chunkchain;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ChainNetworking {
    public static final Identifier CONTROL = new Identifier("chunkchain", "control");
    public static final Identifier SYNC = new Identifier("chunkchain", "sync");

    private ChainNetworking() {}

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(CONTROL, (server, player, handler, buf, responseSender) -> {
            byte action = buf.readByte();
            server.execute(() -> {
                ChainState st = ChainManager.get(player);
                if (action == 0) {
                    st.enabled = !st.enabled;
                } else if (action == 1) {
                    st.cycleSize();
                }
                sync(player);
            });
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
            ChainManager.remove(handler.getPlayer().getUuid()));
    }

    public static void sync(ServerPlayerEntity player) {
        ChainState st = ChainManager.get(player);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(st.enabled);
        buf.writeVarInt(st.size);
        ServerPlayNetworking.send(player, SYNC, buf);
        player.sendMessage(statusText(st), true);
    }

    public static Text statusText(ChainState st) {
        String range = st.size + "×" + st.size + " 区块";
        return st.enabled
            ? new LiteralText("§b区块连锁 §f开启 §7(" + range + "·同位置连锁)")
            : new LiteralText("§7区块连锁 关闭 §7(范围 " + range + ")");
    }
}