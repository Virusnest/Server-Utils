package net.kyrptonaught.serverutils.syncedKeybinds;

import net.kyrptonaught.serverutils.AbstractConfigFile;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;

public class SyncedKeybindsConfig extends AbstractConfigFile {

    public HashMap<String, KeybindConfigItem> keybinds = new HashMap<>();

    public static class KeybindConfigItem {
        public String triggerCMD;
        public String keybinding;
        public String controllerBind;

        public void writeToPacket(String id, PacketByteBuf packetByteBuf) {
            packetByteBuf.writeString(id);
            packetByteBuf.writeString(keybinding);
            packetByteBuf.writeString(controllerBind);
        }
    }
}
