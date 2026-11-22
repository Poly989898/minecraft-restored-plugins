package litebans;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class LiteLiteSenderImpl implements LiteSender {
   private final BukkitPlugin bukkitPlugin;
   private final WeakReference handle;
   private UUID consoleUuid = ZERO_UUID;
   private String uuidString = null;
   private final String name;
   private String displayName;
   private final boolean player;
   private final boolean console;

   public LiteLiteSenderImpl(BukkitPlugin bukkitPlugin, CommandSender commandSender) {
      this.bukkitPlugin = bukkitPlugin;
      this.handle = new WeakReference<>(commandSender);
      boolean flag = commandSender instanceof Player;
      this.player = flag;
      this.console = !flag && commandSender instanceof ConsoleCommandSender;
      this.name = commandSender.getName();
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj || obj instanceof LiteLiteSenderImpl && ((LiteLiteSenderImpl)obj).getUuid().equals(this.getUuid());
   }

   @Override
   public void dispatchCommand(String text) {
      CommandSender commandSender = (CommandSender)this.handle.get();
      if (commandSender != null && !text.isEmpty()) {
         if (FeatureDetection.obtainFlag2()) {
            if (commandSender instanceof BlockCommandSender) {
               LiteUtil.access$getServer2(this.bukkitPlugin, ((BlockCommandSender)commandSender).getBlock().getLocation(), () -> this.handleCommandSender3(commandSender, text));
            } else if (commandSender instanceof Entity) {
               LiteUtil.handleBukkitPlugin(this.bukkitPlugin, this, () -> this.handleCommandSender2(commandSender, text));
            } else {
               LiteUtil.access$getServer(this.bukkitPlugin, () -> this.handleCommandSender(commandSender, text));
            }
         } else {
            this.bukkitPlugin.getServer().dispatchCommand(commandSender, text);
         }
      }
   }

   @Override
   public String getDisplayName() {
      CommandSender commandSender = (CommandSender)this.handle.get();
      if (commandSender == null) {
         return this.name;
      } else {
         return this.player ? ((Player)commandSender).getDisplayName() : this.getName();
      }
   }

   @Override
   public Object getHandle() {
      return this.handle.get();
   }

   @Override
   public String getIpAddress() {
      String text = this.displayName;
      if (text == null) {
         CommandSender commandSender = (CommandSender)this.handle.get();
         if (commandSender != null) {
            this.displayName = text = this.computeText(commandSender);
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
      return null;
   }

   @Override
   public @NonNull UUID getUuid() {
      UUID uuid = this.consoleUuid;
      if (uuid.equals(ZERO_UUID) && this.player) {
         CommandSender commandSender = (CommandSender)this.handle.get();
         if (commandSender != null) {
            UUID uniqueId = ((Player)commandSender).getUniqueId();
            if (uniqueId != null) {
               this.consoleUuid = uniqueId;
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

   @Override
   public boolean hasPermission(@Nullable String text) {
      if (text == null) {
         return true;
      }

      CommandSender commandSender = (CommandSender)this.handle.get();
      return commandSender != null && commandSender.hasPermission(text);
   }

   @Override
   public boolean isConsole() {
      return this.console;
   }

   @Override
   public boolean isOnline() {
      CommandSender commandSender = (CommandSender)this.handle.get();
      return this.player && commandSender != null && ((Player)commandSender).isOnline();
   }

   @Override
   public boolean isPlayer() {
      return this.player;
   }

   @Override
   public void disconnect(String text) {
      CommandSender commandSender = (CommandSender)this.handle.get();
      if (commandSender != null) {
         if (this.player) {
            Player player = (Player)commandSender;
            if (!FeatureDetection.obtainFlag2() && !this.bukkitPlugin.getServer().isPrimaryThread()) {
               this.bukkitPlugin.runSync(new LiteRunnableImpl3(this, player, text));
            } else {
               player.kickPlayer(text);
            }
         }
      }
   }

   @Override
   public void sendRichMessage(String text) {
      CommandSender commandSender = (CommandSender)this.handle.get();
      if (commandSender != null && text != null) {
         if (this.player) {
            Player player = (Player)commandSender;
            if (FeatureDetection.obtainFlag()) {
               FeatureDetection.handlePlayer(player, text);
            } else {
               this.sendMessage(this.computeText(text));
            }
         } else {
            this.sendMessage(this.computeText(text));
         }
      }
   }

   @Override
   public void sendMessage(String text) {
      CommandSender commandSender = (CommandSender)this.handle.get();
      if (commandSender != null) {
         if (FeatureDetection.obtainFlag2() && commandSender instanceof BlockCommandSender) {
            LiteUtil.access$getServer2(this.bukkitPlugin, ((BlockCommandSender)commandSender).getBlock().getLocation(), () -> {
               commandSender.sendMessage(text);
            });
         } else {
            commandSender.sendMessage(text);
         }
      }
   }

   @Override
   public void sendPluginMessage(String text, byte[] bytes) {
      CommandSender commandSender = (CommandSender)this.handle.get();
      if (commandSender != null) {
         if (this.player) {
            ((Player)commandSender).sendPluginMessage(this.bukkitPlugin, text, bytes);
         }
      }
   }

   private final String computeText(String text) {
      try {
         JSONObject jsonObject = (JSONObject)new JSONParser().computeObj(text);
         return (String)jsonObject.get("text");
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final String computeText(CommandSender commandSender) {
      if (commandSender instanceof Player) {
         InetSocketAddress address = ((Player)commandSender).getAddress();
         if (address != null) {
            InetAddress address2 = address.getAddress();
            if (address2 != null) {
               return address2.getHostAddress();
            }
         }
      }

      return null;
   }

   private final void handleCommandSender(CommandSender commandSender, String text) {
      this.bukkitPlugin.getServer().dispatchCommand(commandSender, text);
   }

   private final void handleCommandSender2(CommandSender commandSender, String text) {
      this.bukkitPlugin.getServer().dispatchCommand(commandSender, text);
   }

   private final void handleCommandSender3(CommandSender commandSender, String text) {
      this.bukkitPlugin.getServer().dispatchCommand(commandSender, text);
   }
}
