package litebans;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.connection.Connection.Unsafe;

public final class FakeServer implements Server {
   final ServerInfo serverInfo;

   public FakeServer(ServerInfo serverInfo) {
      this.serverInfo = serverInfo;
   }

   public InetSocketAddress getAddress() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public SocketAddress getSocketAddress() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public void disconnect(String text) {
   }

   public void disconnect(BaseComponent[] baseComponents) {
   }

   public void disconnect(BaseComponent baseComponent) {
   }

   public boolean isConnected() {
      return false;
   }

   public Unsafe unsafe() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public ServerInfo getInfo() {
      return this.serverInfo;
   }

   public void sendData(String text, byte[] bytes) {
   }
}
