package me.TechsCode.UltraPermissions.base.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.BungeeTechPlugin;
import me.TechsCode.UltraPermissions.base.messaging.Message;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class BungeeNetworkManager {
   private static List<String> LOCALHOST_IPS = Arrays.asList("localhost", "127.0.0.1", "0.0.0.0", "172.17.0.1", "172.18.0.1");
   private BungeeTechPlugin plugin;
   private String ownIpAddress;

   public BungeeNetworkManager(BungeeTechPlugin bungeeTechPlugin) {
      this.plugin = bungeeTechPlugin;
      this.ownIpAddress = this.retrieveOwnIpAddress();
      bungeeTechPlugin.getScheduler().runTaskTimer(this::SendServerListInfoTask, 10L, 100L);
   }

   private String retrieveOwnIpAddress() {
      try {
         return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
      } catch (IOException exception) {
         exception.printStackTrace();
         return null;
      }
   }

   private void SendServerListInfoTask() {
      String version = this.plugin.getVersion();
      ServerList serverList = this.fetchServerList();
      NetworkData networkData = new NetworkData(version, serverList);
      this.plugin.getMessagingService().send(new Message("networking", networkData.toJsonObject()));
   }

   private ServerList fetchServerList() {
      ProxyServer proxy = this.plugin.getBootstrap().getProxy();
      return proxy.getServers()
         .entrySet()
         .stream()
         .map(
            entry -> {
               String name = ((ServerInfo)entry.getValue()).getName();
               InetSocketAddress address = ((ServerInfo)entry.getValue()).getAddress();
               if (address == null) {
                  return null;
               }

               InetAddress inetAddress = address.getAddress();
               if (inetAddress == null) {
                  return null;
               }

               String hostAddress = inetAddress.getHostAddress();
               if (LOCALHOST_IPS.contains(hostAddress) || hostAddress.startsWith("192.168")) {
                  hostAddress = this.ownIpAddress;
               }

               int port = ((ServerInfo)entry.getValue()).getAddress().getPort();
               NServer nServer = new NServer(name, hostAddress, port, new ArrayList<>());
               ((ServerInfo)entry.getValue())
                  .getPlayers()
                  .stream()
                  .map(proxiedPlayer -> new NPlayer(proxiedPlayer.getUniqueId(), proxiedPlayer.getName(), nServer))
                  .forEach(nPlayer -> nServer.getPlayers().add(nPlayer));
               return nServer;
            }
         )
         .filter(Objects::nonNull)
         .collect(Collectors.toCollection(ServerList::new));
   }
}
