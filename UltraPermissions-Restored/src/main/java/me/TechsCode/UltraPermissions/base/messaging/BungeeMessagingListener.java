package me.TechsCode.UltraPermissions.base.messaging;

import net.md_5.bungee.api.config.ServerInfo;

public interface BungeeMessagingListener {
   void onMessage(ServerInfo serverInfo, Message message);
}
