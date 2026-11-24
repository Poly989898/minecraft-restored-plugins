package com.comphenix.protocol.events;
import com.comphenix.protocol.PacketType;
import org.bukkit.plugin.Plugin;
public abstract class PacketAdapter {
    public PacketAdapter(Plugin plugin, PacketType... types) {}
    public void onPacketSending(PacketEvent event) {}
}
