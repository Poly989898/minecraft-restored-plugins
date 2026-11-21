package litebans;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

@PlatformMarker(obtainByteValue = 0)
public final class BukkitBridge implements PlatformBridge {
   private final Platform platform;
   private final Lazy vault;

   public BukkitBridge(@NotNull Platform platform) {
      this.platform = platform;
      this.vault = KotlinStub5.computeLazy(() -> new VaultBridge(this));
   }

   @Override
   public Platform getPlatform() {
      return this.platform;
   }

   public final VaultBridge getVault() {
      Lazy lazy = this.vault;
      return (VaultBridge)lazy.getValue();
   }

   public final Plugin asPlugin(@NotNull Platform platform) {
      Intrinsics.checkNotNullExpressionValue(platform, "");
      return (Plugin)platform;
   }

   public static Plugin asPluginWithDefaults(BukkitBridge bukkitBridge, Platform platform, int value, Object obj) {
      if ((value & 1) != 0) {
         platform = bukkitBridge.getPlatform();
      }

      return bukkitBridge.asPlugin(platform);
   }

   public final void connectToServer(@NotNull LiteSender liteSender, @NotNull String text) {
      if (!this.isOutgoingChannelRegistered("BungeeCord")) {
         this.registerOutgoingChannel("BungeeCord");
      }

      this.getPlatform().runAsyncLater(() -> {
         if (liteSender.isOnline()) {
            ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
            dataOutput.writeUTF("KickPlayer");
            dataOutput.writeUTF(liteSender.getName());
            dataOutput.writeUTF(text);
            liteSender.sendPluginMessage("BungeeCord", dataOutput.toByteArray());
         }
      }, 8L);
      if (liteSender.isOnline()) {
         byte[] pluginMessage = this.createPluginMessage(liteSender, text);
         liteSender.sendPluginMessage("BungeeCord", pluginMessage);
      }

      this.getPlatform().getLogger().info("Kicking " + liteSender.getName() + " from BungeeCord.");
   }

   public final byte[] createPluginMessage(@NotNull LiteSender liteSender, @NotNull String text) {
      LiteData liteData = LiteHelper5.computeLiteData(LiteData.LITE_HELPER, this.getServerVersion(), null, 2, null);
      ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
      dataOutput.writeUTF("KickPlayerRaw");
      dataOutput.writeUTF(liteSender.getName());
      GameVersion gameVersion = liteData.checkIntValue(1, 21, 5) ? GameVersion.V1_21_5 : GameVersion.V1_16;
      dataOutput.writeUTF(LiteHelper6.computeText(text, gameVersion));
      byte[] byteArray = dataOutput.toByteArray();
      Intrinsics.checkNotNull(byteArray);
      return byteArray;
   }

   public final Object getOfflinePlayer(@NotNull UUID uuid) {
      asPluginWithDefaults(this, null, 1, null).getServer().getVersion();
      return asPluginWithDefaults(this, null, 1, null).getServer().getOfflinePlayer(uuid);
   }

   public final String getOfflinePlayerName(@NotNull Object obj) {
      Intrinsics.checkNotNullExpressionValue(obj, "");
      String name = ((OfflinePlayer)obj).getName();
      Intrinsics.checkNotNull(name);
      return name;
   }

   @Override
   public Object getPlugin(@NotNull String text) {
      return Bukkit.getServer().getPluginManager().getPlugin(text);
   }

   @Override
   public List findPlugins(@NotNull String text) {
      Object[] plugins = Bukkit.getServer().getPluginManager().getPlugins();
      var arrayList = new ArrayList(plugins.length);
      int value = 0;

      for (int i = plugins.length; value < i; value++) {
         Object obj = plugins[value];
         Plugin plugin = (Plugin)obj;
         arrayList.add(plugin.getDescription());
      }

      Iterable iterable = arrayList;
      var arrayList2 = new ArrayList();

      for (Object obj2 : iterable) {
         PluginDescriptionFile pluginDescriptionFile = (PluginDescriptionFile)obj2;
         if (pluginDescriptionFile.getDepend().contains(text) || pluginDescriptionFile.getSoftDepend().contains(text)) {
            arrayList2.add(obj2);
         }
      }

      Iterable iterable2 = arrayList2;
      var arrayList3 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList2, 10));

      for (Object obj3 : iterable2) {
         PluginDescriptionFile pluginDescriptionFile2 = (PluginDescriptionFile)obj3;
         arrayList3.add(pluginDescriptionFile2.getName());
      }

      return arrayList3;
   }

   @Override
   public Collection getServerNames() {
      return CollectionsKt.emptyList();
   }

   @Override
   public String getServerVersion() {
      return asPluginWithDefaults(this, null, 1, null).getServer().getBukkitVersion();
   }

   public final boolean isOutgoingChannelRegistered(@NotNull String text) {
      return asPluginWithDefaults(this, null, 1, null).getServer().getMessenger().isOutgoingChannelRegistered(asPluginWithDefaults(this, null, 1, null), text);
   }

   public final void registerOutgoingChannel(@NotNull String text) {
      asPluginWithDefaults(this, null, 1, null).getServer().getMessenger().registerOutgoingPluginChannel(asPluginWithDefaults(this, null, 1, null), text);
   }

   public final String getEventMessage(@NotNull Object obj) {
      Object obj2 = obj;
      String text;
      if (obj2 instanceof AsyncPlayerChatEvent) {
         text = ((AsyncPlayerChatEvent)obj).getMessage();
      } else {
         if (!(obj2 instanceof PlayerCommandPreprocessEvent)) {
            this.computeVoid(obj);
            throw new CommandAbortException();
         }

         text = ((PlayerCommandPreprocessEvent)obj).getMessage();
      }

      String text2 = text;
      Intrinsics.checkNotNull(text2);
      return text2;
   }

   @Override
   public PlayerInfo getPlayerInfo(@NotNull Object obj, boolean flag) {
      if (obj instanceof AsyncPlayerPreLoginEvent) {
         String name = ((AsyncPlayerPreLoginEvent)obj).getName();
         UUID uniqueId = ((AsyncPlayerPreLoginEvent)obj).getUniqueId();
         if (uniqueId == null) {
            throw new NullPointerException(obj.getClass().getSimpleName() + ".getUniqueId() == null (" + name + ')');
         }

         InetAddress address = ((AsyncPlayerPreLoginEvent)obj).getAddress();
         String text;
         if (address == null) {
            if (!flag) {
               this.getPlatform().getLogger().warning(obj.getClass().getSimpleName() + ".getAddress() == null (" + name + ')');
            }

            text = null;
         } else {
            text = address.getHostAddress();
         }

         String text2 = text;
         return new PlayerInfo(((AsyncPlayerPreLoginEvent)obj).getName(), uniqueId.toString(), text2);
      } else {
         this.computeVoid(obj);
         throw new CommandAbortException();
      }
   }

   public Void computeVoid(@NotNull Object obj, @NotNull CharSequence message) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getTargetServerName(@NotNull Object obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void disallowLogin(@NotNull Object obj, @NotNull CharSequence message, @NotNull String text, boolean flag, @NotNull String text2) {
      if (obj instanceof AsyncPlayerPreLoginEvent) {
         ((AsyncPlayerPreLoginEvent)obj).disallow(Result.valueOf(text2), message.toString());
      } else if (obj instanceof PlayerLoginEvent) {
         ((PlayerLoginEvent)obj).disallow(org.bukkit.event.player.PlayerLoginEvent.Result.valueOf(text2), message.toString());
      } else if (obj instanceof Cancellable) {
         if (flag) {
            ((Cancellable)obj).setCancelled(true);
         }

         if (obj instanceof AsyncPlayerChatEvent) {
            AsyncPlayerChatEvent asyncPlayerChatEvent = (AsyncPlayerChatEvent)obj;

            try {
               asyncPlayerChatEvent.getRecipients().clear();
            } catch (UnsupportedOperationException e) {
            }

            ((AsyncPlayerChatEvent)obj).setMessage(text + ((AsyncPlayerChatEvent)obj).getMessage());
         } else if (obj instanceof PlayerCommandPreprocessEvent) {
            ((PlayerCommandPreprocessEvent)obj).setMessage('/' + text + ((PlayerCommandPreprocessEvent)obj).getMessage());
         }
      } else {
         this.computeVoid(obj);
         throw new CommandAbortException();
      }
   }

   private final Void computeVoid(Object obj) {
      throw new NotImplementedErrorImpl(obj.getClass().getName());
   }

   @Override
   public boolean hasPlugin(@NotNull String text) {
      return PlayerKicker.access$getPlugin(this, text);
   }

   @Override
   public void handleObj(Object obj, CharSequence message) {
      this.computeVoid(obj, message);
   }
}
