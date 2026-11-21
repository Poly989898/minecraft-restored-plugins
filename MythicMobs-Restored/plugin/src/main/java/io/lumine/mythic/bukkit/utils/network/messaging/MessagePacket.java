package io.lumine.mythic.bukkit.utils.network.messaging;

public abstract class MessagePacket {
   private String originServer = null;

   public static String getChannel() {
      return null;
   }

   public String getOriginServer() {
      return this.originServer;
   }

   public void setOriginServer(String originServer) {
      this.originServer = originServer;
   }
}
