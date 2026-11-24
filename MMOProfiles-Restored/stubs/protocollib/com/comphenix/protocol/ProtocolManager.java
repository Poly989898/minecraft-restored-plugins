package com.comphenix.protocol;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
public interface ProtocolManager {
    void addPacketListener(PacketListener listener);
    PacketContainer createPacket(PacketType type);
    void sendServerPacket(Player player, PacketContainer packet);
}
