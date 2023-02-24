package net.kyrptonaught.serverutils;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class VelocityProxyHelper {
    private static final Identifier BUNGEECORD_ID = new Identifier("bungeecord", "main");

    public static void switchServer(ServerPlayerEntity player, String servername) {
        sendVelocityCommand(player, "Connect", servername);
    }


    public static void kickVelocity(ServerPlayerEntity player, Text msg) {
        kickVelocity(player, msg.getString());
    }

    public static void kickVelocity(ServerPlayerEntity player, String msg) {
        sendVelocityCommand(player, "KickPlayer", player.getEntityName(), msg);
    }

    private static void sendVelocityCommand(ServerPlayerEntity player, String command, String... args) {
        try (ByteBufDataOutput output = new ByteBufDataOutput(new PacketByteBuf(Unpooled.buffer()))) {
            output.writeUTF(command);
            for (String arg : args)
                output.writeUTF(arg);

            ServerPlayNetworking.send(player, BUNGEECORD_ID, output.getBuf());
        } catch (Exception e) {
            System.out.println("Failed to send comamnd to Velocity Proxy: ");
            e.printStackTrace();
        }
    }

    public static class ByteBufDataOutput extends OutputStream {
        private final PacketByteBuf packetByteBuf;
        private final DataOutputStream dataOutputStream;

        public ByteBufDataOutput(PacketByteBuf buf) {
            this.packetByteBuf = buf;
            this.dataOutputStream = new DataOutputStream(this);
        }

        public PacketByteBuf getBuf() {
            return packetByteBuf;
        }

        @Override
        public void write(int b) {
            packetByteBuf.writeByte(b);
        }

        public void writeUTF(String s) {
            try {
                this.dataOutputStream.writeUTF(s);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public void close() throws IOException {
            super.close();
            dataOutputStream.close();
        }
    }
}