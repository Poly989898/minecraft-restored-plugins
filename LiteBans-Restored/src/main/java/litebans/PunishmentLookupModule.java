package litebans;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PunishmentLookupModule extends PluginModule {
   public MuteChatTask muteChatTask;

   public PunishmentLookupModule(@NotNull Platform platform) {
      super(platform);
   }

   public final MuteChatTask getMuteChatTask() {
      if (this.muteChatTask != null) {
         return this.muteChatTask;
      }

      Intrinsics.throwUninitializedProperty("");
      return null;
   }

   public final void setMuteChatTask(@NotNull MuteChatTask muteChatTask) {
      this.muteChatTask = muteChatTask;
   }

   @Override
   public void enable() {
      this.setMuteChatTask(new MuteChatTask(this.platform));
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession) {
      try {
         LiteSender[] onlineSenders = databaseSession.getPlatform().getOnlineSenders();
         Intrinsics.checkNotNull(onlineSenders);
         if (onlineSenders.length != 0 && !databaseSession.isClosed()) {
            if (databaseSession instanceof DatabaseSessionImpl) {
               DatabaseSessionImpl databaseSession2 = (DatabaseSessionImpl)databaseSession;
               PunishmentTable punishmentTable = PunishmentTable.MUTES;
               List list;
               if (onlineSenders.length == 0) {
                  list = CollectionsKt.emptyList();
               } else {
                  ArrayList arrayList = new ArrayList(Math.max(1, onlineSenders.length / DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.obtainIntValue()));
                  int value = 0;
                  int value2 = 0;
                  HashSet hashSet = new HashSet(onlineSenders.length);
                  ArrayList arrayList2 = new ArrayList(onlineSenders.length * 2);

                  while (true) {
                     StringBuilder builder = new StringBuilder().append("SELECT * FROM ").append(punishmentTable).append(" WHERE(");
                     StringBuilder stringBuilder = new StringBuilder(
                        builder.append("active=" + databaseSession2.getBroadcastModule().getSqlTrue() + " AND(until<1 OR until>?)").append(")AND(").toString()
                     );

                     for (int i = onlineSenders.length; value < i; value2++) {
                        LiteSender liteSender = onlineSenders[value];
                        String ipAddress = liteSender.getIpAddress();
                        stringBuilder.append("uuid=?");
                        arrayList2.add(liteSender.getUuidString());
                        if (ipAddress != null && !hashSet.contains(ipAddress)) {
                           stringBuilder.append(" OR(ipban=").append(databaseSession2.getBroadcastModule().getSqlTrue()).append(" AND ").append("ip=?)");
                           arrayList2.add(ipAddress);
                           hashSet.add(ipAddress);
                        }

                        if (value2 > DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.obtainIntValue()) {
                           break;
                        }

                        if (value != i - 1) {
                           stringBuilder.append(" OR ");
                        }

                        value++;
                     }

                     value2 = 0;
                     stringBuilder.append(")AND(server_scope='*' OR server_scope=? OR server_scope=?)");
                     String string = stringBuilder.toString();
                     ConfigModule configModule = databaseSession2.getConfigModule();
                     if (configModule.isVerbose()) {
                        configModule.logDebug(value + ": " + string);
                     }

                     PreparedQuery prepare2 = databaseSession2.prepare(string);
                     SqlUtil.access$bindLong(prepare2);
                     Iterable iterable = arrayList2;

                     for (Object obj : iterable) {
                        String string2 = (String)obj;
                        prepare2.bindString(string2);
                     }

                     String serverName = onlineSenders[0].getServerName();
                     prepare2.bindString(serverName);
                     String serverName2 = databaseSession2.getDatabaseModule().getServerName();
                     prepare2.bindString(serverName2);
                     arrayList.add(prepare2);
                     if (value >= onlineSenders.length) {
                        list = arrayList;
                        break;
                     }

                     arrayList2.clear();
                  }
               }

               List list2 = list;
               ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
               Iterable iterable2 = list2;

               for (Object obj2 : iterable2) {
                  PreparedQuery preparedQuery = (PreparedQuery)obj2;
                  ResultSet resultSet = SqlUtil.computeResultSet2(preparedQuery);
                  if (resultSet.next()) {
                     while (true) {
                        Punishment punishment = databaseSession2.readPunishment(resultSet, PunishmentType.MUTE);
                        String uuid = punishment.getUuid();
                        if (uuid != null && uuid.length() == 36) {
                           concurrentHashMap.put(UUID.fromString(uuid), punishment);
                        }

                        Object[] onlineSenders2 = ((DatabaseSessionImpl)databaseSession).getPlatform().getOnlineSenders();
                        var arrayList3 = new ArrayList();
                        int value3 = 0;

                        for (int i2 = onlineSenders2.length; value3 < i2; value3++) {
                           Object obj3 = onlineSenders2[value3];
                           LiteSender liteSender2 = (LiteSender)obj3;
                           if (punishment.checkText(liteSender2.getUuidString(), liteSender2.getIpAddress(), DatabaseModule.getCurrentTimeWithDefaults(databaseSession.getDatabaseModule(), false, 1, null))) {
                              arrayList3.add(obj3);
                           }
                        }


                        for (Object obj4 : arrayList3) {
                           LiteSender liteSender3 = (LiteSender)obj4;
                           concurrentHashMap.put(liteSender3.getUuid(), punishment);
                        }

                        if (!resultSet.next()) {
                           break;
                        }
                     }
                  }
               }

               if (this.getMuteChatTask().getMutedPlayers().size() != concurrentHashMap.size()) {
                  this.getMuteChatTask().setMutedPlayers(concurrentHashMap);
               }
            } else {
               StandardKt.notImplemented();
               throw new CommandAbortException();
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final void handleText(@NotNull String text, @NotNull Punishment punishment) {
      try {
         UUID fromString2 = UUID.fromString(TargetParser.normalize(text));
         this.getMuteChatTask().getMutedPlayers().put(fromString2, punishment);
      } catch (IllegalArgumentException e) {
         ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
         ConfigModule.handleConfigModule(module, e, 0, 2, null);
      }
   }

   public final void handleText(@Nullable String text, @Nullable String text2, @Nullable String text3, @Nullable String text4) {
      Platform platform = this.platform;
      Intrinsics.checkNotNull(text2);
      new ActiveMuteNoticeTask(platform, text, text2, text3, text4).run();
   }

   public final Punishment findPunishment(@NotNull UUID uuid, @Nullable String text, @Nullable String text2) {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      DatabaseModule module2 = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
      Punishment punishment = (Punishment)this.getMuteChatTask().getMutedPlayers().get(uuid);
      if (punishment != null) {
         String string = uuid.toString();
         if (punishment.checkText(string, text, DatabaseModule.getCurrentTimeWithDefaults(module2, false, 1, null))) {
            ServerScope serverScope = ServerScopes.SERVER_SCOPE;
            String serverScope2 = punishment.getServerScope();
            if (serverScope2 == null) {
               serverScope2 = ServerScopes.SERVER_SCOPE.getLocalScope();
            }

            String text3 = text2;
            if (text2 == null) {
               text3 = module2.getServerName();
            }

            if (serverScope.scopesMatch(serverScope2, text3)) {
               return punishment;
            }
         } else if (Intrinsics.areEqual(text, punishment.getIp())) {
            this.getMuteChatTask().getMutedPlayers().remove(uuid);
         }

         if (module.isVerbose()) {
            module.logDebug("M! " + uuid);
         }
      }

      return null;
   }

   public final void handleText(@Nullable String text, @Nullable String text2) {
      if (text != null) {
         ActiveMuteNoticeTask liteCommandTaskSub2 = new ActiveMuteNoticeTask(this.platform, null, text, text2, null);
         CommandTask commandTask = liteCommandTaskSub2;
         Object[] onlineSenders = commandTask.getPlatform().getOnlineSenders();
         int value = 0;
         int length2 = onlineSenders.length;

         Object obj;
         while (true) {
            if (value >= length2) {
               obj = null;
               break;
            }

            Object obj2 = onlineSenders[value];
            LiteSender liteSender = (LiteSender)obj2;
            if (Intrinsics.areEqual(liteSender.getUuidString(), text)) {
               obj = obj2;
               break;
            }

            value++;
         }

         LiteSender liteSender2 = (LiteSender)obj;
         if (liteSender2 != null) {
            liteCommandTaskSub2.run();
         }
      }
   }

   public final boolean isMutedInChat(@Nullable Punishment punishment) {
      if ((punishment != null ? punishment.getType() : null) == PunishmentType.MUTE) {
         boolean flag = CollectionsKt.checkIterable((Iterable)this.getMuteChatTask().getMutedPlayers().entrySet(), obj -> {
            Entry entry = (java.util.Map.Entry) obj;
            return punishment.getUuid() != null && Intrinsics.areEqual(punishment.getUuid(), ((Punishment)entry.getValue()).getUuid())
                     || punishment.isIpBan() && punishment.getIp() != null && Intrinsics.areEqual(punishment.getIp(), ((Punishment)entry.getValue()).getIp());
         });
         if (flag) {
            this.handleText(punishment.getUuid(), punishment.getIp());
         }

         return flag;
      } else {
         return false;
      }
   }

   @Override
   public void disable() {
      this.getMuteChatTask().getMutedPlayers().clear();
   }

}
