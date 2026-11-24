package com.comphenix.protocol;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
public interface ProtocolManager {
    void addPacketListener(PacketAdapter adapter);
    PacketContainer createPacket(PacketType type);
    void sendServerPacket(Player player, PacketContainer packet);
}
