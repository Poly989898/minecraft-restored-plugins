package litebans;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.SkinConfiguration;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.connection.Connection.Unsafe;
import net.md_5.bungee.api.connection.ProxiedPlayer.ChatMode;
import net.md_5.bungee.api.connection.ProxiedPlayer.MainHand;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;
import net.md_5.bungee.api.score.Scoreboard;

public final class FakeProxiedPlayer implements ProxiedPlayer {
   final LiteSender liteSender;
   final ServerInfo serverInfo;

   public FakeProxiedPlayer(LiteSender liteSender, ServerInfo serverInfo) {
      this.liteSender = liteSender;
      this.serverInfo = serverInfo;
   }

   public InetSocketAddress getAddress() {
      try {
         return new InetSocketAddress(InetAddress.getByName(this.liteSender.getIpAddress()), 0);
      } catch (UnknownHostException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public SocketAddress getSocketAddress() {
      return this.getAddress();
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
      return null;
   }

   public String getName() {
      return this.liteSender.getName();
   }

   public void sendMessage(ChatMessageType chatMessageType, BaseComponent[] baseComponents) {
   }

   public void sendMessage(ChatMessageType chatMessageType, BaseComponent baseComponent) {
   }

   public void sendMessage(UUID uuid, BaseComponent[] baseComponents) {
   }

   public void sendMessage(UUID uuid, BaseComponent baseComponent) {
   }

   public void sendMessage(String text) {
   }

   public void sendMessage(BaseComponent[] baseComponents) {
   }

   public void sendMessage(BaseComponent baseComponent) {
   }

   public void sendMessages(String[] texts) {
   }

   public Collection getGroups() {
      return new ArrayList();
   }

   public void addGroups(String[] texts) {
   }

   public void removeGroups(String[] texts) {
   }

   public boolean hasPermission(String text) {
      return false;
   }

   public Void computeVoid(String text, boolean flag) {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public Collection getPermissions() {
      return new ArrayList();
   }

   public String getDisplayName() {
      return this.liteSender.getName();
   }

   public Void computeVoid(String text) {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public void connect(ServerInfo serverInfo) {
   }

   public void connect(ServerInfo serverInfo, Reason reason) {
   }

   public void connect(ServerInfo serverInfo, Callback callback) {
   }

   public void connect(ServerInfo serverInfo, Callback callback, Reason reason) {
   }

   public void connect(ServerConnectRequest serverConnectRequest) {
   }

   public Server getServer() {
      return new FakeServer(this.serverInfo);
   }

   public int getPing() {
      return 0;
   }

   public void sendData(String text, byte[] bytes) {
   }

   public PendingConnection getPendingConnection() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public void chat(String text) {
   }

   public ServerInfo getReconnectServer() {
      return null;
   }

   public void setReconnectServer(ServerInfo serverInfo) {
   }

   public String getUUID() {
      return this.liteSender.getUuidString();
   }

   public UUID getUniqueId() {
      return this.liteSender.getUuid();
   }

   public Locale getLocale() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public byte getViewDistance() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public ChatMode getChatMode() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public boolean hasChatColors() {
      return false;
   }

   public SkinConfiguration getSkinParts() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public MainHand getMainHand() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public void setTabHeader(BaseComponent baseComponent, BaseComponent baseComponent2) {
   }

   public void setTabHeader(BaseComponent[] baseComponents, BaseComponent[] baseComponents2) {
   }

   public void resetTabHeader() {
   }

   public void sendTitle(Title title) {
   }

   public boolean isForgeUser() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public Map getModList() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public Scoreboard getScoreboard() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public CompletableFuture retrieveCookie(String text) {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public Void computeVoid(String text, byte[] bytes) {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public Void computeVoid(String text, int value) {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public void setPermission(String text, boolean flag) {
      this.computeVoid(text, flag);
   }

   public void setDisplayName(String text) {
      this.computeVoid(text);
   }

   public void storeCookie(String text, byte[] bytes) {
      this.computeVoid(text, bytes);
   }

   public void transfer(String text, int value) {
      this.computeVoid(text, value);
   }
}
