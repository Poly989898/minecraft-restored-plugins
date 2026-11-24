package me.TechsCode.UltraPermissions.base.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;

public class SpigotNetworkManager {
   private SpigotTechPlugin plugin;
   private NetworkData networkData;
   private String ownIpAddress;

   public SpigotNetworkManager(SpigotTechPlugin spigotTechPlugin) {
      this.plugin = spigotTechPlugin;
      this.ownIpAddress = this.retrieveOwnIpAddress();
      spigotTechPlugin.getMessagingService().register(message -> {
         if (message.getKey().equals("networking")) {
            this.networkData = NetworkData.fromJsonObject(message.getData());
         }
      });
   }

   private String retrieveOwnIpAddress() {
      try {
         return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
      } catch (IOException exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public Optional<NetworkData> getData() {
      return Optional.ofNullable(this.networkData);
   }

   public Optional<NServer> getThisServer() {
      if (this.networkData == null) {
         return Optional.empty();
      }

      int port = this.plugin.getBootstrap().getServer().getPort();
      return this.networkData.getServerList().stream().filter(nServer -> nServer.getIp().equals(this.ownIpAddress) && nServer.getPort() == port).findFirst();
   }

   public Optional<NServer> getServerFromName(String text) {
      return this.networkData == null
         ? Optional.empty()
         : this.networkData.getServerList().stream().filter(nServer -> nServer.getName().equalsIgnoreCase(text)).findFirst();
   }

   public Optional<ServerList> getServersContains(String text) {
      return this.networkData == null
         ? Optional.empty()
         : Optional.of(
            this.networkData.getServerList().stream().filter(nServer -> nServer.getName().contains(text)).collect(Collectors.toCollection(ServerList::new))
         );
   }

   public Optional<NServer> getServerEqualsPort(int count) {
      return this.networkData == null ? Optional.empty() : this.networkData.getServerList().stream().filter(nServer -> nServer.getPort() == count).findFirst();
   }
}
