package litebans;

import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult;
import com.velocitypowered.api.event.player.ServerPreConnectEvent.ServerResult;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.meta.PluginDependency;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public final class VelocityBridge implements PlatformBridge {
   private final Platform platform;

   public VelocityBridge(@NotNull Platform platform) {
      this.platform = platform;
   }

   @Override
   public Platform getPlatform() {
      return this.platform;
   }

   public final VelocityPlugin computeVelocityPlugin(@NotNull Platform platform) {
      Intrinsics.checkNotNullExpressionValue(platform, "");
      return (VelocityPlugin)platform;
   }

   public static VelocityPlugin computeVelocityPlugin(VelocityBridge velocityBridge, Platform platform, int value, Object obj) {
      if ((value & 1) != 0) {
         platform = velocityBridge.getPlatform();
      }

      return velocityBridge.computeVelocityPlugin(platform);
   }

   @Override
   public Object getPlugin(@NotNull String text) {
      PluginManager pluginManager = computeVelocityPlugin(this, null, 1, null).proxyServer.getPluginManager();
      String text2 = text;
      Optional plugin = pluginManager.getPlugin(text2.toLowerCase(Locale.ENGLISH));
      return plugin.isPresent() && ((PluginContainer)plugin.get()).getInstance().isPresent() ? ((PluginContainer)plugin.get()).getInstance().get() : null;
   }

   public List obtainList() {
      Iterable allServers = computeVelocityPlugin(this, null, 1, null).proxyServer.getAllServers();
      var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)allServers, 10));

      for (Object obj : allServers) {
         RegisteredServer registeredServer = (RegisteredServer)obj;
         arrayList.add(registeredServer.getServerInfo().getName());
      }

      return arrayList;
   }

   @Override
   public String getServerVersion() {
      return computeVelocityPlugin(this, null, 1, null).proxyServer.getVersion().getVersion();
   }

   @Override
   public List findPlugins(@NotNull String text) {
      Iterable plugins = computeVelocityPlugin(this, null, 1, null).proxyServer.getPluginManager().getPlugins();
      var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)plugins, 10));

      for (Object obj : plugins) {
         PluginContainer pluginContainer = (PluginContainer)obj;
         arrayList.add(pluginContainer.getDescription());
      }

      Iterable iterable = arrayList;
      var arrayList2 = new ArrayList();

      for (Object obj2 : iterable) {
         PluginDescription pluginDescription = (PluginDescription)obj2;
         var dependencies = pluginDescription.getDependencies();
         boolean flag;
         if (dependencies instanceof Collection && ((Collection)dependencies).isEmpty()) {
            flag = false;
         } else {
            Iterator iterator2 = dependencies.iterator();

            while (true) {
               if (!iterator2.hasNext()) {
                  flag = false;
                  break;
               }

               Object obj3 = iterator2.next();
               PluginDependency pluginDependency = (PluginDependency)obj3;
               String id = pluginDependency.getId();
               String text2 = text;
               if (Intrinsics.areEqual(id, text2.toLowerCase(Locale.ENGLISH))) {
                  flag = true;
                  break;
               }
            }
         }

         if (flag) {
            arrayList2.add(obj2);
         }
      }

      Iterable iterable2 = arrayList2;
      var arrayList3 = new ArrayList();

      for (Object obj4 : iterable2) {
         PluginDescription pluginDescription2 = (PluginDescription)obj4;
         if (pluginDescription2.getName().isPresent()) {
            arrayList3.add(obj4);
         }
      }

      Iterable iterable3 = arrayList3;
      var arrayList4 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList3, 10));

      for (Object obj5 : iterable3) {
         PluginDescription pluginDescription3 = (PluginDescription)obj5;
         arrayList4.add((String)pluginDescription3.getName().get());
      }

      return arrayList4;
   }

   @Override
   public void handleObj(@NotNull Object obj, @NotNull CharSequence message) {
      if (obj instanceof ServerPreConnectEvent) {
         ((ServerPreConnectEvent)obj).getPlayer().sendMessage((Component)this.computeTextComponent(message));
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public void disallowLogin(@NotNull Object obj, @NotNull CharSequence message, @NotNull String text, boolean flag, @NotNull String text2) {
      Object obj2 = obj;
      if (obj2 instanceof PlayerChatEvent) {
         ((PlayerChatEvent)obj).setResult(ChatResult.denied());
      } else if (obj2 instanceof CommandExecuteEvent) {
         ((CommandExecuteEvent)obj).setResult(CommandResult.denied());
      } else if (obj2 instanceof ServerPreConnectEvent) {
         ((ServerPreConnectEvent)obj).setResult(ServerResult.denied());
         if (flag && !((ServerPreConnectEvent)obj).getPlayer().getCurrentServer().isPresent()) {
            ((ServerPreConnectEvent)obj).getPlayer().disconnect((Component)this.computeTextComponent(message));
         }
      }
   }

   public final TextComponent computeTextComponent(@NotNull CharSequence message) {
      return LegacyComponentSerializer.legacySection().deserialize(message.toString());
   }

   @Override
   public PlayerInfo getPlayerInfo(@NotNull Object obj, boolean flag) {
      if (obj instanceof ServerPreConnectEvent) {
         Player player = ((ServerPreConnectEvent)obj).getPlayer();
         return new PlayerInfo(player.getUsername(), player.getUniqueId().toString(), player.getRemoteAddress().getAddress().getHostAddress());
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public String getTargetServerName(@NotNull Object obj) {
      if (obj instanceof ServerPreConnectEvent) {
         return ((RegisteredServer)((ServerPreConnectEvent)obj).getResult().getServer().get()).getServerInfo().getName();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public boolean hasPlugin(@NotNull String text) {
      return PlayerKicker.access$getPlugin(this, text);
   }

   @Override
   public Collection getServerNames() {
      return this.obtainList();
   }
}
