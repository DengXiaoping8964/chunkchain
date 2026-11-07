package dev.fish.chunkchain;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class ChunkChainClient implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static KeyBinding cycleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.chunkchain.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.chunkchain"));
        cycleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.chunkchain.cycle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category.chunkchain"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                sendControl((byte) 0);
            }
            while (cycleKey.wasPressed()) {
                sendControl((byte) 1);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ChainNetworking.SYNC, (client, handler, buf, responseSender) -> {
            boolean enabled = buf.readBoolean();
            int size = buf.readVarInt();
            client.execute(() -> {
                if (client.player != null) {
                    Text msg = enabled
                        ? new LiteralText("§b区块连锁 §f开启 §7(" + size + "×" + size + " 区块·同位置连锁)")
                        : new LiteralText("§7区块连锁 关闭 §7(范围 " + size + "×" + size + ")");
                    client.player.sendMessage(msg, true);
                }
            });
        });
    }

    private static void sendControl(byte action) {
        if (!ClientPlayNetworking.canSend(ChainNetworking.CONTROL)) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(action);
        ClientPlayNetworking.send(ChainNetworking.CONTROL, buf);
    }
}