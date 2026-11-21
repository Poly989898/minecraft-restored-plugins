package litebans;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BungeeLoginHandler extends LoginHandler {
   private final ConcurrentHashMap concurrentHashMap2 = new ConcurrentHashMap();
   private final ConcurrentHashMap concurrentHashMap3 = new ConcurrentHashMap();

   public BungeeLoginHandler(@NotNull Platform platform) {
      super(platform);
   }

   public final ConcurrentHashMap getConcurrentHashMap2() {
      return this.concurrentHashMap2;
   }

   public final ConcurrentHashMap getConcurrentHashMap3() {
      return this.concurrentHashMap3;
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull String text, @NotNull UUID uuid, @Nullable String text2, @NotNull String text3, @NotNull Collection collection) {
      int mostSignificantBits = (int)uuid.getMostSignificantBits();
      Collection collection2 = (Collection)this.concurrentHashMap2.get(mostSignificantBits);
      if (!collection.isEmpty()) {
         Iterable iterable = collection;

         for (Object obj : iterable) {
            Punishment punishment = (Punishment)obj;
            this.concurrentHashMap3.put(punishment, Placeholders.replaceTargetPlaceholders(databaseSession, punishment, text, uuid.toString(), text2, text3, -1));
         }

         Integer boxed = mostSignificantBits;
         this.concurrentHashMap2.put(boxed, this.computeCollection(collection, collection2));
      }
   }

   private final Collection computeCollection(Collection collection, Collection collection2) {
      if (collection2 == null || collection2.isEmpty()) {
         return collection;
      }

      List mutableList = CollectionsKt.toMutableList(collection2);

      for (Punishment punishment : (Iterable<Punishment>) collection) {
         boolean flag;
         if (collection2 instanceof Collection && ((Collection)collection2).isEmpty()) {
            flag = true;
         } else {
            label58: {
               for (Object obj : collection2) {
                  Punishment punishment2 = (Punishment)obj;
                  if (punishment2.getId() == punishment.getId()) {
                     flag = false;
                     break label58;
                  }
               }

               flag = true;
            }
         }

         if (flag) {
            mutableList.add(punishment);
         }
      }

      return mutableList;
   }

   public final void handleLoginEvent(@NotNull LoginEvent loginEvent) {
      try {
         boolean flag = false;
         Object obj = null;
         PendingConnection pendingConnection;
         UUID uuid;
         String text;
         String text2;
         BungeeBridge bungeeBridge;
         String text3;
         label246: {
            PlatformBridge bridge = this.getPlatform().createBridge();
            Intrinsics.checkNotNullExpressionValue(bridge, "");
            bungeeBridge = (BungeeBridge)bridge;
            pendingConnection = loginEvent.getConnection();
            uuid = pendingConnection.getUniqueId();
            text = uuid.toString();
            text2 = pendingConnection.getName();
            InetSocketAddress address = pendingConnection.getAddress();
            if (address != null) {
               InetAddress address2 = address.getAddress();
               if (address2 != null) {
                  text3 = address2.getHostAddress();
                  break label246;
               }
            }

            text3 = null;
         }

         String text4 = text3;
         Intrinsics.checkNotNull(pendingConnection);
         ServerInfo serverInfo = bungeeBridge.computeServerInfo(pendingConnection);
         if (serverInfo == null) {
            throw new AssertionError("No default server");
         }

         KotlinSerializableImpl kotlinSerializable = new KotlinSerializableImpl();
         String name = serverInfo.getName();
         CommandTask commandTask = this;
         DatabaseModule module = (DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               label258: {
                  CommandTask commandTask2 = this;
                  byte usernameMaxLength = ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().getUsernameMaxLength();
                  int value = text2.length();
                  if (Intrinsics.compare(value, usernameMaxLength & 255) > 0) {
                     Intrinsics.checkNotNull(text2);
                     this.warnInvalidUsername(session, text2, usernameMaxLength);
                     if (this.disallowIfPunished(
                        session,
                        loginEvent,
                        text,
                        text2,
                        TextUtil.colorize(
                           LiteColor.RED,
                           "Invalid username.\nMaximum "
                              + LiteColor.GREEN
                              + UByte.toStringImpl(usernameMaxLength)
                              + ' '
                              + LiteColor.RED
                              + "characters, found "
                              + LiteColor.YELLOW
                              + LiteColor.UNDERLINE
                              + text2.length()
                        ),
                        null,
                        "KICK_OTHER",
                        false
                     )) {
                        break label258;
                     }
                  }

                  kotlinSerializable.obj = session.getHistory(text, text4, PunishmentType.BAN, "__ALL__", true, 0);
                  CommandTask commandTask3 = this;
                  if (((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).getSettings().isBanNames()) {
                     Intrinsics.checkNotNull(text2);
                     List punishments = session.findPunishments(text2, text, "__ALL__", PunishmentType.BAN, true);
                     if (!punishments.isEmpty()) {
                        Object obj2 = kotlinSerializable.obj;
                        Intrinsics.checkNotNullExpressionValue(kotlinSerializable.obj, "");
                        KotlinUtil30.asMutableList(obj2).addAll(punishments);
                     }
                  }

                  CommandTask commandTask4 = this;
                  ConfigModule module2 = (ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class);
                  byte b = 3;
                  if (module2.isDebugAtLeast(b)) {
                     module2.logDebug("Found " + ((List)kotlinSerializable.obj).size() + " ban(s) for " + text2 + " (uuid: '" + uuid + "').");
                  }

                  CommandTask commandTask5 = this;
                  if (((ConfigModule)commandTask5.getPlatform().getModule(ConfigModule.class)).isDebugAtLeast(10)) {
                     Iterable iterable = (Iterable)kotlinSerializable.obj;
                     CommandTask commandTask6 = this;
                     ConfigModule module3 = (ConfigModule)commandTask6.getPlatform().getModule(ConfigModule.class);

                     for (Object obj3 : iterable) {
                        module3.logDebug(obj3);
                     }
                  }

                  label210: {
                     if (!((Collection)kotlinSerializable.obj).isEmpty()) {
                        Intrinsics.checkNotNull(uuid);
                        if (session.hasAllowEntry(uuid, PunishmentType.BAN)) {
                           flag = true;
                           break label210;
                        }
                     }

                     flag = false;
                  }

                  boolean flag2 = flag;
                  if (flag2) {
                     Iterable iterable2 = (Iterable)kotlinSerializable.obj;
                     var arrayList = new ArrayList();

                     for (Object obj4 : iterable2) {
                        Punishment punishment = (Punishment)obj4;
                        if (Intrinsics.areEqual(punishment.getUuid(), text)) {
                           arrayList.add(obj4);
                        }
                     }

                     kotlinSerializable.obj = arrayList;
                  }

                  String geoipCountry = this.getGeoipCountry(text4);
                  if (geoipCountry != null && !flag2) {
                     Intrinsics.checkNotNull(name);
                     Intrinsics.checkNotNull(text2);
                     this.checkDatabaseSession(session, loginEvent, geoipCountry, name, text, text2);
                  }

                  Iterable iterable3 = (Iterable)kotlinSerializable.obj;

                  label194: {
                     for (Object obj5 : iterable3) {
                        Punishment punishment2 = (Punishment)obj5;
                        ServerScope serverScope = ServerScopes.SERVER_SCOPE;
                        Intrinsics.checkNotNull(name);
                        String serverScope2 = punishment2.getServerScope();
                        if (serverScope2 == null) {
                           serverScope2 = ServerScopes.SERVER_SCOPE.getLocalScope();
                        }

                        if (serverScope.scopesMatch(name, serverScope2) && this.checkText2(name)) {
                           obj = obj5;
                           break label194;
                        }
                     }

                     obj = null;
                  }

                  Punishment punishment3 = (Punishment)obj;
                  CommandTask commandTask7 = this;
                  ConfigModule module4 = (ConfigModule)commandTask7.getPlatform().getModule(ConfigModule.class);
                  byte b2 = 3;
                  if (module4.isDebugAtLeast(b2)) {
                     module4.logDebug("Ban for " + name + " (uncached): " + punishment3);
                  }

                  if (punishment3 != null) {
                     Intrinsics.checkNotNull(text2);
                     this.checkGeoipBlock(session, loginEvent, text, text2, text4, name, punishment3);
                  } else {
                     Intrinsics.checkNotNull(text2);
                     Intrinsics.checkNotNull(uuid);
                     Intrinsics.checkNotNull(name);
                     this.handleDatabaseSession(session, text2, uuid, text4, name, (Collection)kotlinSerializable.obj);
                  }
               }

            } catch (Throwable e2) {
               error = e2;
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            } finally {
               CloseableKt.handleCloseable(session, error);
            }
         } catch (Throwable e3) {
            if (!(e3 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e3);
            }
            SQLException sqlException = (SQLException) e3;
            if (!module.isConnectionFailure(sqlException)) {
               throw sqlException;
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void handleObj(@NotNull Object obj) {
      boolean flag = false;
      Object obj2 = null;
      try {
         PlayerInfo playerInfo = this.getPlayerInfo(obj);
         String component12 = playerInfo.component1();
         String component22 = playerInfo.component2();
         String component32 = playerInfo.component3();
         String serverName = this.getServerName(obj);
         boolean flag2 = false;
         if (this.checkText2(serverName)) {
            Collection collection = (Collection)this.concurrentHashMap2.get(this.computeIntValue(component22));
            Punishment punishment;
            if (collection != null) {
               Iterable iterable = collection;
               Iterator iterator2 = iterable.iterator();

               while (true) {
                  if (!iterator2.hasNext()) {
                     obj2 = null;
                     break;
                  }

                  Object obj3;
                  label66: {
                     obj3 = iterator2.next();
                     Punishment punishment2 = (Punishment)obj3;
                     if (punishment2.checkText(component22, component32, System.currentTimeMillis())) {
                        ServerScope serverScope = ServerScopes.SERVER_SCOPE;
                        String serverScope2 = punishment2.getServerScope();
                        if (serverScope2 == null) {
                           serverScope2 = ServerScopes.SERVER_SCOPE.getLocalScope();
                        }

                        if (serverScope.scopesMatch(serverName, serverScope2)) {
                           flag = true;
                           break label66;
                        }
                     }

                     flag = false;
                  }

                  if (flag) {
                     obj2 = obj3;
                     break;
                  }
               }

               punishment = (Punishment)obj2;
            } else {
               punishment = null;
            }

            Punishment punishment3 = punishment;
            if (punishment3 != null) {
               CommandTask commandTask = this;
               if (!((BroadcastModule)commandTask.getPlatform().getModule(BroadcastModule.class)).removePendingUnban(punishment3, component12)) {
                  flag2 = this.checkObj(obj, component22, component12, component32, serverName, punishment3);
               }
            }
         }

         CommandTask commandTask2 = this;
         ((BroadcastModule)commandTask2.getPlatform().getModule(BroadcastModule.class)).handleText(component12, component32, component22, serverName, flag2);
      } catch (LiteException e) {
         CommandTask commandTask3 = this;
         ConfigModule.handleConfigModule((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class), e, 0, 2, null);
      } catch (NullPointerException e2) {
         Platform platform = this.getPlatform();
         if (platform.isEnabled() && !platform.obtainFlag3()) {
            throw e2;
         }

         CommandTask commandTask4 = this;
         ConfigModule.handleConfigModule((ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class), e2, 0, 2, null);
      } catch (Throwable e3) {
         if (!(e3 instanceof SQLException)) {
            throw SneakyThrow.<RuntimeException>rethrow(e3);
         }
         SQLException sqlException = (SQLException) e3;
         CommandTask commandTask5 = this;
         ((DatabaseModule)commandTask5.getPlatform().getModule(DatabaseModule.class)).logConnectionError(sqlException);
      }
   }

   public final int computeIntValue(@NotNull String text) {
      UUID fromString2 = UUID.fromString(text);
      return (int)fromString2.getMostSignificantBits();
   }

   public final boolean checkObj(@NotNull Object obj, @NotNull String text, @NotNull String text2, @Nullable String text3, @Nullable String text4, @NotNull Punishment punishment) {
      CharSequence charSequence = (CharSequence)this.concurrentHashMap3.get(punishment);
      if (charSequence == null) {
         charSequence = "null";
      }

      CharSequence message = charSequence;
      CommandTask commandTask = this;
      ConfigModule module = (ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class);
      byte b = 3;
      if (module.isDebugAtLeast(b)) {
         module.logDebug("Ban for " + text4 + " (cached): " + punishment);
      }

      PlayerKicker.disallowLoginWithDefaults(this.getPlatform().createBridge(), obj, message, null, false, null, 28, null);
      CommandTask commandTask2 = this;
      if (((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().isNotifyBannedPlayerJoin()) {
         CommandTask commandTask3 = this;
         ((DatabaseModule)commandTask3.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj2 -> {
            DatabaseSession databaseSession = (DatabaseSession) obj2;
            CharSequence replacePlaceholders2 = databaseSession.replacePlaceholders(PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Message.BANNED_PLAYER_JOIN, punishment, false, 2, null), text2, text2, text, text3, text4);
            CommandTask commandTask4 = this;
            ((BroadcastModule)commandTask4.getPlatform().getModule(BroadcastModule.class)).handleText2(text, replacePlaceholders2, true);
            return Unit.UNIT;
         });
      }

      this.handleObj(obj, obj3 -> this.computeMessage(((DatabaseSession) obj3), punishment, (CharSequence)message));
      return true;
   }

   @Override
   public void handleObj(@NotNull Object obj, @NotNull Function1 callback) {
      if (!(obj instanceof LoginEvent)) {
         super.handleObj(obj, callback);
      }
   }

}
