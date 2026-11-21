package litebans;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.chat.ChatVersion;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(obtainByteValue = 1)
public class BungeeConsoleSender implements LiteSender {
   private final BungeePlugin bungeePlugin;
   private final WeakReference weakReference2;
   private UUID uuid6 = uuid3;
   private String uuidString = null;
   private String text6;
   private final String name;
   private final boolean player;

   public BungeeConsoleSender(BungeePlugin bungeePlugin, CommandSender commandSender) {
      this.bungeePlugin = bungeePlugin;
      this.weakReference2 = new WeakReference<>(commandSender);
      this.player = commandSender instanceof ProxiedPlayer;
      this.name = commandSender.getName();
   }

   @Override
   public void dispatchCommand(String text) {
      CommandSender commandSender = (CommandSender)this.weakReference2.get();
      if (commandSender != null && !text.isEmpty()) {
         this.bungeePlugin.getProxy().getPluginManager().dispatchCommand(commandSender, text);
      }
   }

   @Override
   public String getDisplayName() {
      CommandSender commandSender = (CommandSender)this.weakReference2.get();
      if (commandSender == null) {
         return this.name;
      } else {
         return this.isPlayer() ? ((ProxiedPlayer)commandSender).getDisplayName() : this.getName();
      }
   }

   @Override
   public Object getHandle() {
      return this.weakReference2.get();
   }

   @Override
   public @Nullable String getIpAddress() {
      String text = this.text6;
      if (text == null) {
         CommandSender commandSender = (CommandSender)this.weakReference2.get();
         if (commandSender != null) {
            this.text6 = text = this.computeText(commandSender);
         }
      }

      return text;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public @Nullable String getServerName() {
      CommandSender commandSender = (CommandSender)this.weakReference2.get();
      if (commandSender == null) {
         return null;
      } else if (this.isPlayer()) {
         ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
         Server server = proxiedPlayer.getServer();
         return server == null ? null : server.getInfo().getName();
      } else {
         return null;
      }
   }

   @Override
   public @NonNull UUID getUuid() {
      UUID uuid = this.uuid6;
      if (uuid.equals(uuid3) && this.player) {
         CommandSender commandSender = (CommandSender)this.weakReference2.get();
         if (commandSender != null) {
            UUID uniqueId = ((ProxiedPlayer)commandSender).getUniqueId();
            if (uniqueId != null) {
               this.uuid6 = uniqueId;
               return uniqueId;
            }
         }
      }

      return uuid;
   }

   @Override
   public String getUuidString() {
      String text = this.uuidString;
      return text != null && !text.equals("00000000-0000-0000-0000-000000000000") ? text : (this.uuidString = this.getUuid().toString());
   }

   public int obtainIntValue2() {
      Object handle = this.getHandle();
      if (handle instanceof ProxiedPlayer) {
         PendingConnection pendingConnection = ((ProxiedPlayer)handle).getPendingConnection();
         return pendingConnection.getVersion();
      } else {
         return 0;
      }
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj || obj instanceof BungeeConsoleSender && ((BungeeConsoleSender)obj).getUuid().equals(this.getUuid());
   }

   @Override
   public boolean hasPermission(@Nullable String text) {
      if (text == null) {
         return true;
      }

      CommandSender commandSender = (CommandSender)this.weakReference2.get();
      return commandSender != null && commandSender.hasPermission(text);
   }

   @Override
   public boolean isConsole() {
      return !this.player;
   }

   @Override
   public boolean isOnline() {
      if (this.player) {
         CommandSender commandSender = (CommandSender)this.weakReference2.get();
         if (commandSender != null) {
            return ((ProxiedPlayer)commandSender).isConnected();
         }
      }

      return this.weakReference2.get() != null;
   }

   @Override
   public boolean isPlayer() {
      return this.player;
   }

   @Override
   public void disconnect(@NonNull String text) {
      if (text == null) {
         throw new NullPointerException("reason is marked non-null but is null");
      }

      CommandSender commandSender = (CommandSender)this.weakReference2.get();
      if (commandSender != null) {
         if (this.isPlayer()) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer)commandSender;
            proxiedPlayer.disconnect(BungeeChatSupport.computeBaseComponentArray(text));
         }
      }
   }

   @Override
   public void sendRichMessage(String text) {
      CommandSender commandSender = (CommandSender)this.weakReference2.get();
      if (commandSender != null) {
         if (BungeeChatSupport.obtainFlag()) {
            if (this.obtainIntValue2() >= 770) {
               commandSender.sendMessage(BungeeChatConversion.computeBaseComponentArray(text, ChatVersion.V1_21_5));
            } else {
               commandSender.sendMessage(BungeeChatConversion.computeBaseComponentArray(text, ChatVersion.V1_16));
            }
         } else {
            commandSender.sendMessage(BungeeChatSupport.computeBaseComponentArray2(text));
         }
      }
   }

   @Override
   public void sendMessage(String text) {
      CommandSender commandSender = (CommandSender)this.weakReference2.get();
      if (commandSender != null && (!this.player || this.isOnline())) {
         commandSender.sendMessage(BungeeChatSupport.computeBaseComponentArray(text));
      }
   }

   @Override
   public void sendPluginMessage(String text, byte[] bytes) {
      CommandSender commandSender = (CommandSender)this.weakReference2.get();
      if (commandSender != null) {
         if (this.isPlayer()) {
            ((ProxiedPlayer)commandSender).sendData(text, bytes);
         }
      }
   }

   private final String computeText(CommandSender commandSender) {
      if (commandSender instanceof ProxiedPlayer) {
         InetSocketAddress address = ((ProxiedPlayer)commandSender).getAddress();
         if (address != null) {
            InetAddress address2 = address.getAddress();
            if (address2 != null) {
               return address2.getHostAddress();
            }
         }
      }

      return null;
   }
}
