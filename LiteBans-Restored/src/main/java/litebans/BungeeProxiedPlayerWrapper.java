package litebans;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(obtainByteValue = 1)
public final class BungeeProxiedPlayerWrapper implements ProxiedPlayer {
   private final Platform platform;
   private final PendingConnection pendingConnection;

   public BungeeProxiedPlayerWrapper(@NotNull Platform platform, @Nullable PendingConnection pendingConnection) {
      this.platform = platform;
      this.pendingConnection = pendingConnection;
   }

   public final Void obtainVoid() {
      throw new UnsupportedOperationException();
   }

   public void connect(@Nullable ServerConnectRequest serverConnectRequest) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void connect(@Nullable ServerInfo serverInfo, @Nullable Reason reason) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void connect(@Nullable ServerInfo serverInfo, @Nullable Callback callback, @Nullable Reason reason) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public SkinConfiguration getSkinParts() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public boolean hasChatColors() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public byte getViewDistance() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public MainHand getMainHand() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public ChatMode getChatMode() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public Scoreboard getScoreboard() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void addGroups(@NotNull String[] texts) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void chat(@NotNull String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void connect(@NotNull ServerInfo serverInfo) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void connect(@NotNull ServerInfo serverInfo, @NotNull Callback callback) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void disconnect(@NotNull String text) {
      if (this.pendingConnection != null) {
         this.pendingConnection.disconnect(text);
      }
   }

   public void disconnect(@NotNull BaseComponent[] baseComponents) {
      if (this.pendingConnection != null) {
         this.pendingConnection.disconnect(Arrays.copyOf(baseComponents, baseComponents.length));
      }
   }

   public void disconnect(@NotNull BaseComponent baseComponent) {
      if (this.pendingConnection != null) {
         this.pendingConnection.disconnect(baseComponent);
      }
   }

   public InetSocketAddress getAddress() {
      return this.pendingConnection != null ? this.pendingConnection.getAddress() : null;
   }

   public String getDisplayName() {
      return this.getName();
   }

   public void setDisplayName(@NotNull String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public Collection getGroups() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public Locale getLocale() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public Map getModList() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public String getName() {
      if (this.pendingConnection != null) {
         String name = this.pendingConnection.getName();
         if (name != null) {
            return name;
         }
      }

      return "null";
   }

   public PendingConnection getPendingConnection() {
      PendingConnection pendingConnection = this.pendingConnection;
      Intrinsics.checkNotNull(this.pendingConnection);
      return pendingConnection;
   }

   public Collection getPermissions() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public int getPing() {
      return 0;
   }

   public ServerInfo getReconnectServer() {
      BungeeBridge bridge = (BungeeBridge) this.platform.createBridge();
      Intrinsics.checkNotNullExpressionValue(bridge, "");
      bridge = bridge;
      PendingConnection pendingConnection = this.pendingConnection;
      Intrinsics.checkNotNull(this.pendingConnection);
      return bridge.computeServerInfo2(pendingConnection);
   }

   public void setReconnectServer(@NotNull ServerInfo serverInfo) {
   }

   public Server getServer() {
      return null;
   }

   public String getUUID() {
      return this.getUniqueId().toString();
   }

   public UUID getUniqueId() {
      if (this.pendingConnection != null) {
         UUID uniqueId = this.pendingConnection.getUniqueId();
         if (uniqueId != null) {
            return uniqueId;
         }
      }

      return new UUID(0L, 0L);
   }

   public boolean hasPermission(@NotNull String text) {
      return false;
   }

   public boolean isConnected() {
      return false;
   }

   public boolean isForgeUser() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void removeGroups(@NotNull String[] texts) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void resetTabHeader() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public SocketAddress getSocketAddress() {
      return this.pendingConnection != null ? this.pendingConnection.getSocketAddress() : null;
   }

   public void sendData(@NotNull String text, @NotNull byte[] bytes) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void sendMessage(@NotNull String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void sendMessage(@NotNull BaseComponent[] baseComponents) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void sendMessage(@NotNull BaseComponent baseComponent) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void sendMessage(@NotNull ChatMessageType chatMessageType, @NotNull BaseComponent[] baseComponents) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void sendMessage(@NotNull ChatMessageType chatMessageType, @NotNull BaseComponent baseComponent) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void sendMessage(@Nullable UUID uuid, @NotNull BaseComponent[] baseComponents) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void sendMessage(@Nullable UUID uuid, @Nullable BaseComponent baseComponent) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void sendMessages(@NotNull String[] texts) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void sendTitle(@NotNull Title title) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void setPermission(@NotNull String text, boolean flag) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void setTabHeader(@NotNull BaseComponent baseComponent, @NotNull BaseComponent baseComponent2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void setTabHeader(@NotNull BaseComponent[] baseComponents, @NotNull BaseComponent[] baseComponents2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public Unsafe unsafe() {
      PendingConnection pendingConnection = this.pendingConnection;
      Intrinsics.checkNotNull(this.pendingConnection);
      return pendingConnection.unsafe();
   }

   public Void computeVoid(@Nullable String text, @NotNull byte[] bytes) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void transfer(@Nullable String text, int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public CompletableFuture retrieveCookie(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   public void storeCookie(String text, byte[] bytes) {
      this.computeVoid(text, bytes);
   }
}
