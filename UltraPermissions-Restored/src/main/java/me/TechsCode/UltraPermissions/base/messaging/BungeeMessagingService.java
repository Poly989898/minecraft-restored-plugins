package me.TechsCode.UltraPermissions.base.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.TechsCode.UltraPermissions.base.BungeeTechPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeMessagingService implements Listener {
   private BungeeTechPlugin plugin;
   private List<BungeeMessagingListener> listeners;

   public BungeeMessagingService(BungeeTechPlugin bungeeTechPlugin) {
      this.plugin = bungeeTechPlugin;
      this.listeners = new ArrayList<>();
      ProxyServer.getInstance().getPluginManager().registerListener(bungeeTechPlugin.getBootstrap(), this);
      ProxyServer.getInstance().registerChannel("BungeeCord");
   }

   public void register(BungeeMessagingListener bungeeMessagingListener) {
      this.listeners.add(bungeeMessagingListener);
   }

   @EventHandler
   public void message(PluginMessageEvent event) {
      if (event.getSender() instanceof Server) {
         try {
            ByteArrayDataInput dataInput = ByteStreams.newDataInput(event.getData());
            Server sender = (Server)event.getSender();
            ServerInfo info = sender.getInfo();
            String uTF = dataInput.readUTF();
            if (uTF.startsWith(this.plugin.getName() + "//")) {
               Message message = Message.decode(uTF.replace(this.plugin.getName() + "//", ""));
               this.listeners.forEach(bungeeMessagingListener -> bungeeMessagingListener.onMessage(info, message));
            }
         } catch (IllegalStateException exception) {
         }
      }
   }

   public void send(Message message, ServerInfo... serverInfos) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

      try {
         dataOutputStream.writeUTF(this.plugin.getName() + "//" + message.encode());
      } catch (IOException exception) {
         exception.printStackTrace();
      }

      if (serverInfos.length == 0) {
         serverInfos = ProxyServer.getInstance().getServers().values().toArray(new ServerInfo[0]);
      }

      for (ServerInfo serverInfo : serverInfos) {
         serverInfo.sendData("BungeeCord", byteArrayOutputStream.toByteArray());
      }
   }
}
