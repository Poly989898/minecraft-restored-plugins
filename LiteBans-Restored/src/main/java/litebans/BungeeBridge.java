package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

@PlatformMarker(obtainByteValue = 1)
public final class BungeeBridge implements PlatformBridge {
   private final Platform platform;
   private boolean flag3;
   private boolean flag4;

   public BungeeBridge(@NotNull Platform platform) {
      try {
         this.platform = platform;
         this.flag3 = true;
         this.flag4 = true;

         boolean flag;
         BungeeBridge bungeeBridge;
         try {
            bungeeBridge = this;
            Class.forName("net.md_5.bungee.api.event.LoginEvent").getMethod("getCancelReasonComponents");
            flag = true;
         } catch (NoSuchMethodException e2) {
            bungeeBridge = this;
            flag = false;
         }

         bungeeBridge.flag3 = flag;

         boolean flag2;
         try {
            bungeeBridge = this;
            Class forName2 = Class.forName("net.md_5.bungee.api.plugin.Command");
            Class[] classArray = new Class[]{String.class};
            forName2.getDeclaredMethod("setPermissionMessage", classArray);
            flag2 = true;
         } catch (NoSuchMethodException e3) {
            bungeeBridge = this;
            flag2 = false;
         }

         bungeeBridge.flag4 = flag2;
      } catch (ClassNotFoundException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Platform getPlatform() {
      return this.platform;
   }

   public final boolean isFlag4() {
      return this.flag4;
   }

   public final ProxyServer getProxy(@NotNull Platform platform) {
      return this.asBungeePlugin(platform).getProxy();
   }

   public final BungeePlugin asBungeePlugin(@NotNull Platform platform) {
      Intrinsics.checkNotNullExpressionValue(platform, "");
      return (BungeePlugin)platform;
   }

   public final boolean checkText(@NotNull String text, @NotNull String text2, @NotNull String text3) {
      try {
         ConfigurationAdapter configurationAdapter = this.getProxy(this.getPlatform()).getConfigurationAdapter();
         var groups = configurationAdapter.getGroups(text);
         boolean flag;
         if (groups instanceof Collection && ((Collection)groups).isEmpty()) {
            flag = false;
         } else {
            Iterator iterator2 = groups.iterator();

            while (true) {
               if (!iterator2.hasNext()) {
                  flag = false;
                  break;
               }

               Object obj = iterator2.next();
               String string = (String)obj;
               if (StringsKt.checkText(string, text3, true)) {
                  flag = true;
                  break;
               }
            }
         }

         if (!flag) {
            var groups2 = configurationAdapter.getGroups(text2);
            if (groups2 instanceof Collection && ((Collection)groups2).isEmpty()) {
               flag = false;
            } else {
               Iterator iterator3 = groups2.iterator();

               while (true) {
                  if (!iterator3.hasNext()) {
                     flag = false;
                     break;
                  }

                  Object obj2 = iterator3.next();
                  String string2 = (String)obj2;
                  if (StringsKt.checkText(string2, text3, true)) {
                     flag = true;
                     break;
                  }
               }
            }

            if (!flag) {
               return false;
            }
         }

         return true;
      } catch (IllegalStateException e) {
         return false;
      }
   }

   public final ThreadFactory computeThreadFactory(@NotNull Platform platform) {
      BungeePlugin bungeePlugin = this.asBungeePlugin(platform);
      ExecutorService executorService = bungeePlugin.getProxy().getScheduler().unsafe().getExecutorService(bungeePlugin);
      Intrinsics.checkNotNullExpressionValue(executorService, "");
      return ((ThreadPoolExecutor)executorService).getThreadFactory();
   }

   public final int getPlayerCount() {
      return this.getProxy(this.getPlatform()).getPlayers().size();
   }

   @Override
   public Object getPlugin(@NotNull String text) {
      return ProxyServer.getInstance().getPluginManager().getPlugin(text);
   }

   @Override
   public List findPlugins(@NotNull String text) {
      Iterable plugins = this.getProxy(this.getPlatform()).getPluginManager().getPlugins();
      var arrayList = new ArrayList();

      for (Object obj : plugins) {
         Plugin plugin;
         boolean flag;
         label65: {
            plugin = (Plugin)obj;
            if (plugin != null) {
               PluginDescription description = plugin.getDescription();
               if (description != null) {
                  Set depends = description.getDepends();
                  if (depends != null) {
                     flag = depends.contains(text);
                     break label65;
                  }
               }
            }

            flag = false;
         }

         label59: {
            if (!flag) {
               label56: {
                  if (plugin != null) {
                     PluginDescription description2 = plugin.getDescription();
                     if (description2 != null) {
                        Set softDepends = description2.getSoftDepends();
                        if (softDepends != null) {
                           flag = softDepends.contains(text);
                           break label56;
                        }
                     }
                  }

                  flag = false;
               }

               if (!flag) {
                  flag = false;
                  break label59;
               }
            }

            flag = true;
         }

         if (flag) {
            arrayList.add(obj);
         }
      }

      Iterable iterable = arrayList;
      var arrayList2 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList, 10));

      for (Object obj2 : iterable) {
         ArrayList list;
         String text2;
         label43: {
            Plugin plugin2 = (Plugin)obj2;
            list = arrayList2;
            if (plugin2 != null) {
               PluginDescription description3 = plugin2.getDescription();
               if (description3 != null) {
                  text2 = description3.getName();
                  if (text2 != null) {
                     break label43;
                  }
               }
            }

            text2 = "";
         }

         list.add(text2);
      }

      return arrayList2;
   }

   @Override
   public Collection getServerNames() {
      return this.getProxy(this.getPlatform()).getServers().keySet();
   }

   @Override
   public String getServerVersion() {
      return this.getProxy(this.getPlatform()).getVersion();
   }

   public final boolean checkText2(@NotNull String text, @NotNull String text2, @NotNull String text3) {
      try {
         ConfigurationAdapter configurationAdapter = this.getProxy(this.getPlatform()).getConfigurationAdapter();
         var groups = configurationAdapter.getGroups(text);
         boolean flag;
         if (groups instanceof Collection && ((Collection)groups).isEmpty()) {
            flag = false;
         } else {
            Iterator iterator2 = groups.iterator();

            while (true) {
               if (!iterator2.hasNext()) {
                  flag = false;
                  break;
               }

               Object obj = iterator2.next();
               String string = (String)obj;
               if (configurationAdapter.getPermissions(string).contains(text3)) {
                  flag = true;
                  break;
               }
            }
         }

         if (!flag) {
            var groups2 = configurationAdapter.getGroups(text2);
            if (groups2 instanceof Collection && ((Collection)groups2).isEmpty()) {
               flag = false;
            } else {
               Iterator iterator3 = groups2.iterator();

               while (true) {
                  if (!iterator3.hasNext()) {
                     flag = false;
                     break;
                  }

                  Object obj2 = iterator3.next();
                  String string2 = (String)obj2;
                  if (configurationAdapter.getPermissions(string2).contains(text3)) {
                     flag = true;
                     break;
                  }
               }
            }

            if (!flag) {
               return false;
            }
         }

         return true;
      } catch (Exception e) {
         boolean flag2 = e instanceof ClassCastException;
         if (flag2) {
            this.getPlatform().getLogger().warning("Bungee permission config (in Bungee config.yml) is invalid, ignoring. No players will be exempt.");
         }

         if (!(e instanceof IllegalStateException) && !flag2) {
            throw e;
         } else {
            return false;
         }
      }
   }

   public final void handleLiteSender(@NotNull LiteSender liteSender, @NotNull String text) {
      ProxiedPlayer handle = (ProxiedPlayer)liteSender.getHandle();
      Intrinsics.checkNotNullExpressionValue(handle, "");
      handle = handle;
      BaseComponent[] parse2 = ComponentSerializer.parse(text);
      handle.disconnect(Arrays.copyOf(parse2, parse2.length));
   }

   @Override
   public PlayerInfo getPlayerInfo(@NotNull Object obj, boolean flag) {
      if (obj instanceof ServerConnectEvent) {
         ProxiedPlayer player = ((ServerConnectEvent)obj).getPlayer();
         return new PlayerInfo(player.getName(), player.getUniqueId().toString(), player.getAddress().getAddress().getHostAddress());
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public void handleObj(@NotNull Object obj, @NotNull CharSequence message) {
      Object obj2 = obj;
      if (obj2 instanceof ServerConnectEvent) {
         if (((ServerConnectEvent)obj).getPlayer().isConnected()) {
            ProxiedPlayer player = ((ServerConnectEvent)obj).getPlayer();
            BaseComponent[] fromLegacyText2 = TextComponent.fromLegacyText(message.toString());
            player.sendMessage(Arrays.copyOf(fromLegacyText2, fromLegacyText2.length));
         }
      } else if (!(obj2 instanceof LoginEvent)) {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public String getTargetServerName(@NotNull Object obj) {
      if (obj instanceof ServerConnectEvent) {
         return ((ServerConnectEvent)obj).getTarget().getName();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public void disallowLogin(@NotNull Object obj, @NotNull CharSequence message, @NotNull String text, boolean flag, @NotNull String text2) {
      if (obj instanceof Cancellable) {
         ((Cancellable)obj).setCancelled(true);
      }

      Object obj2 = obj;
      if (obj2 instanceof ChatEvent) {
         boolean startsWith = StringsKt.startsWithWithDefaults(((ChatEvent)obj).getMessage(), '/', false, 2, null);
         ((ChatEvent)obj).setMessage((startsWith ? "/" : "") + text + ((ChatEvent)obj).getMessage());
      } else if (obj2 instanceof LoginEvent) {
         if (this.flag3) {
            LoginEvent loginEvent = (LoginEvent)obj;
            BaseComponent[] fromLegacyText2 = TextComponent.fromLegacyText(message.toString());
            loginEvent.setCancelReason(Arrays.copyOf(fromLegacyText2, fromLegacyText2.length));
         } else {
            BridgeMarker.MESSAGE_SEND_HELPER.handleObj(obj, message);
         }
      } else if (obj2 instanceof ServerConnectEvent && flag && ((ServerConnectEvent)obj).getPlayer().getServer() == null) {
         ProxiedPlayer player = ((ServerConnectEvent)obj).getPlayer();
         BaseComponent[] fromLegacyText3 = TextComponent.fromLegacyText(message.toString());
         player.disconnect(Arrays.copyOf(fromLegacyText3, fromLegacyText3.length));
      }
   }

   public final ServerInfo computeServerInfo(@NotNull Object obj) {
      ProxyServer proxy = this.getProxy(this.getPlatform());
      ServerInfo serverInfo;
      if (obj instanceof PendingConnection) {
         ReconnectHandler reconnectHandler = proxy.getReconnectHandler();
         if (reconnectHandler == null) {
            return this.computeServerInfo2(obj);
         }

         serverInfo = reconnectHandler.getServer(new BungeeProxiedPlayerWrapper(this.getPlatform(), (PendingConnection)obj));
         if (serverInfo == null) {
            return this.computeServerInfo2(obj);
         }
      } else {
         serverInfo = null;
      }

      return serverInfo;
   }

   public final ServerInfo computeServerInfo2(@NotNull Object obj) {
      ProxyServer proxy = this.getProxy(this.getPlatform());
      ServerInfo serverInfo;
      if (obj instanceof PendingConnection) {
         ListenerInfo listener = ((PendingConnection)obj).getListener();
         serverInfo = proxy.getServerInfo(listener.getDefaultServer());
         if (serverInfo == null) {
            serverInfo = AbstractReconnectHandler.getForcedHost((PendingConnection)obj);
         }
      } else {
         serverInfo = null;
      }

      return serverInfo;
   }

   @Override
   public boolean hasPlugin(@NotNull String text) {
      return PlayerKicker.access$getPlugin(this, text);
   }
}
