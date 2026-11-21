package litebans;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;

public final class KotlinServerInfoImpl implements ServerInfo {
   public String getName() {
      return "lobby";
   }

   public InetSocketAddress getAddress() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public SocketAddress getSocketAddress() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public Collection getPlayers() {
      return new ArrayList();
   }

   public String getMotd() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public boolean isRestricted() {
      return false;
   }

   public String getPermission() {
      return "";
   }

   public boolean canAccess(CommandSender commandSender) {
      return true;
   }

   public void sendData(String text, byte[] bytes) {
   }

   public boolean sendData(String text, byte[] bytes, boolean flag) {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public void ping(Callback callback) {
   }
}
