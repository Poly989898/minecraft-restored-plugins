package litebans;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(obtainByteValue = 2)
public class VelocityConsoleSender implements LiteSender {
   private final VelocityPlugin velocityPlugin;
   private final WeakReference weakReference;
   private final String name;
   private final boolean player;
   private UUID uuid2 = uuid3;
   private String uuidString = null;
   private String text3;

   public VelocityConsoleSender(VelocityPlugin velocityPlugin, CommandSource commandSource) {
      this.velocityPlugin = velocityPlugin;
      this.weakReference = new WeakReference<>(commandSource);
      this.player = commandSource instanceof Player;
      if (this.player) {
         this.name = ((Player)commandSource).getUsername();
      } else {
         this.name = "[Console]";
      }
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj || obj instanceof VelocityConsoleSender && ((VelocityConsoleSender)obj).getUuid().equals(this.getUuid());
   }

   @Override
   public void dispatchCommand(String text) {
      CommandSource commandSource = (CommandSource)this.weakReference.get();
      if (commandSource != null && !text.isEmpty()) {
         this.velocityPlugin.proxyServer.getCommandManager().executeAsync(commandSource, text);
      }
   }

   @Override
   public String getDisplayName() {
      return this.getName();
   }

   @Override
   public Object getHandle() {
      return this.weakReference.get();
   }

   @Override
   public @Nullable String getIpAddress() {
      String text = this.text3;
      if (text == null) {
         CommandSource commandSource = (CommandSource)this.weakReference.get();
         if (commandSource != null) {
            this.text3 = text = this.computeText(commandSource);
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
      CommandSource commandSource = (CommandSource)this.weakReference.get();
      if (commandSource == null) {
         return null;
      } else if (this.isPlayer()) {
         Player player = (Player)commandSource;
         Optional currentServer = player.getCurrentServer();
         return !currentServer.isPresent() ? null : ((ServerConnection)currentServer.get()).getServerInfo().getName();
      } else {
         return null;
      }
   }

   @Override
   public @NonNull UUID getUuid() {
      UUID uuid = this.uuid2;
      if (uuid.equals(uuid3) && this.player) {
         CommandSource commandSource = (CommandSource)this.weakReference.get();
         if (commandSource != null) {
            UUID uniqueId = ((Player)commandSource).getUniqueId();
            if (uniqueId != null) {
               this.uuid2 = uniqueId;
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

   public int obtainIntValue() {
      Object handle = this.getHandle();
      if (handle instanceof Player) {
         Player player = (Player)handle;
         return player.getProtocolVersion().getProtocol();
      } else {
         return 0;
      }
   }

   @Override
   public boolean hasPermission(@Nullable String text) {
      if (text == null) {
         return true;
      }

      CommandSource commandSource = (CommandSource)this.weakReference.get();
      return commandSource != null && commandSource.hasPermission(text);
   }

   @Override
   public boolean isConsole() {
      return !this.player;
   }

   @Override
   public boolean isOnline() {
      if (this.player) {
         CommandSource commandSource = (CommandSource)this.weakReference.get();
         if (commandSource != null) {
            return ((Player)commandSource).isActive();
         }
      }

      return this.weakReference.get() != null;
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

      CommandSource commandSource = (CommandSource)this.weakReference.get();
      if (commandSource != null) {
         if (this.isPlayer()) {
            ((Player)commandSource).disconnect(AdventureConversion.computeTextComponent(text));
         }
      }
   }

   @Override
   public void sendRichMessage(String text) {
      CommandSource commandSource = (CommandSource)this.weakReference.get();
      if (commandSource != null) {
         commandSource.sendMessage(AdventureConversion.computeComponent(text));
      }
   }

   @Override
   public void sendMessage(String text) {
      CommandSource commandSource = (CommandSource)this.weakReference.get();
      if (commandSource != null) {
         commandSource.sendMessage(AdventureConversion.computeTextComponent(text));
      }
   }

   @Override
   public void sendPluginMessage(String text, byte[] bytes) {
      CommandSource commandSource = (CommandSource)this.weakReference.get();
      if (commandSource != null) {
         if (this.isPlayer()) {
            ((Player)commandSource).sendPluginMessage(new LegacyChannelIdentifier(text), bytes);
         }
      }
   }

   private final String computeText(CommandSource commandSource) {
      if (commandSource instanceof Player) {
         InetSocketAddress remoteAddress = ((Player)commandSource).getRemoteAddress();
         if (remoteAddress != null) {
            InetAddress address = remoteAddress.getAddress();
            if (address != null) {
               return address.getHostAddress();
            }
         }
      }

      return null;
   }
}
