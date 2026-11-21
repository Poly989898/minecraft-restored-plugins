package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoginHandler extends CommandTask {
   public LoginHandler(@NotNull Platform platform) {
      super(platform);
   }

   public void handleObj(@NotNull Object obj) {
      try {
         CommandTask commandTask = this;
         ConfigModule requireEnabled2 = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).requireEnabled();
         PlayerInfo playerInfo = this.getPlayerInfo(obj);
         String component12 = playerInfo.component1();
         String component22 = playerInfo.component2();
         String component32 = playerInfo.component3();
         byte usernameMaxLength = requireEnabled2.getSettings().getUsernameMaxLength();
         String serverName = this.getServerName(obj);
         boolean flag = false;
         boolean flag2 = false;
         CommandTask commandTask2 = this;
         DatabaseModule module = (DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               label165: {
                  int value = component12.length();
                  if (Intrinsics.compare(value, usernameMaxLength & 255) > 0) {
                     this.warnInvalidUsername(session, component12, usernameMaxLength);
                     if (this.disallowIfPunished(
                        session,
                        obj,
                        component22,
                        component12,
                        TextUtil.colorize(
                           LiteColor.RED,
                           "Invalid username.\nMaximum "
                              + LiteColor.GREEN
                              + UByte.toStringImpl(usernameMaxLength)
                              + ' '
                              + LiteColor.red("characters")
                              + ", found "
                              + LiteColor.yellow(LiteColor.underline(String.valueOf(component12.length())))
                        ),
                        null,
                        "KICK_OTHER",
                        false
                     )) {
                        flag2 = true;
                        break label165;
                     }
                  }

                  label145:
                  if (this.checkText2(serverName)) {
                     Punishment punishment = PunishmentQueries.findPunishmentWithDefaults(session, component22, component32, null, serverName, false, false, 52, null);
                     byte b = 3;
                     if (requireEnabled2.isDebugAtLeast(b)) {
                        requireEnabled2.logDebug("Ban for " + component12 + " (uuid: '" + component22 + "') on " + serverName + ": " + punishment);
                     }

                     String geoipCountry = this.getGeoipCountry(component32);
                     if (geoipCountry != null) {
                        flag = this.checkDatabaseSession(session, obj, geoipCountry, serverName, component22, component12);
                     }

                     if (!flag && punishment != null) {
                        CommandTask commandTask3 = this;
                        if (!((BroadcastModule)commandTask3.getPlatform().getModule(BroadcastModule.class)).removePendingUnban(punishment, component12)) {
                           flag = this.checkGeoipBlock(session, obj, component22, component12, component32, serverName, punishment);
                           break label145;
                        }
                     }

                     if (!flag && requireEnabled2.getSettings().isBanNames()) {
                        Punishment punishment2 = PunishmentQueries.findPunishmentWithDefaults(session, component12, component22, serverName, null, 8, null);
                        if (punishment2 != null) {
                           CommandTask commandTask4 = this;
                           if (!((BroadcastModule)commandTask4.getPlatform().getModule(BroadcastModule.class)).removePendingUnban(punishment2, component12)) {
                              flag = this.checkGeoipBlock(session, obj, component22, component12, component32, serverName, punishment2);
                              if (flag) {
                                 flag2 = true;
                              }
                           }
                        }
                     }
                  }
               }

            } catch (Throwable e) {
               error = e;
               throw SneakyThrow.<RuntimeException>rethrow(e);
            } finally {
               CloseableKt.handleCloseable(session, error);
            }
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            if (!module.isConnectionFailure(sqlException)) {
               throw sqlException;
            }
         }

         if (flag2) {
            return;
         }

         CommandTask commandTask5 = this;
         ((BroadcastModule)commandTask5.getPlatform().getModule(BroadcastModule.class)).handleText(component12, component32, component22, serverName, flag);
      } catch (LiteException e3) {
         CommandTask commandTask6 = this;
         ConfigModule.handleConfigModule((ConfigModule)commandTask6.getPlatform().getModule(ConfigModule.class), e3, 0, 2, null);
      } catch (NullPointerException e4) {
         Platform platform = this.getPlatform();
         if (platform.isEnabled() && !platform.obtainFlag3()) {
            throw e4;
         }

         CommandTask commandTask7 = this;
         ConfigModule.handleConfigModule((ConfigModule)commandTask7.getPlatform().getModule(ConfigModule.class), e4, 0, 2, null);
      } catch (Throwable e5) {
         if (!(e5 instanceof SQLException)) {
            throw SneakyThrow.<RuntimeException>rethrow(e5);
         }
         SQLException sqlException2 = (SQLException) e5;
         CommandTask commandTask8 = this;
         ((DatabaseModule)commandTask8.getPlatform().getModule(DatabaseModule.class)).logConnectionError(sqlException2);
      }
   }

   public final void warnInvalidUsername(@NotNull DatabaseSession databaseSession, @NotNull String text, byte b) {
      databaseSession.getPlatform().getLogger().warning("User \"" + text + "\" length > " + UByte.toStringImpl(b) + " (invalid username); blocking login.");
   }

   public final String getGeoipCountry(@Nullable String text) {
      CommandTask commandTask = this;
      GeoIpLookup geoIp = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getGeoIp();
      return geoIp != null ? geoIp.computeText2(text) : null;
   }

   public final boolean checkText2(@NotNull String text) {
      boolean flag;
      if (this.getPlatform().obtainFlag4()) {
         CommandTask commandTask = this;
         Set disableLoginBanCheckServers = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().getDisableLoginBanCheckServers();
         String text2 = text;
         flag = !disableLoginBanCheckServers.contains(text2.toLowerCase(Locale.ENGLISH));
      } else {
         CommandTask commandTask2 = this;
         flag = !((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().isDisableLoginBanCheck();
      }

      return flag;
   }

   public final boolean disallowIfPunished(
      @NotNull DatabaseSession databaseSession,
   @NotNull Object obj,
   @NotNull String text,
   @NotNull String text2,
   @NotNull CharSequence message,
   @Nullable Punishment punishment,
   @NotNull String text3,
   boolean flag
) {
      UUID uuid = TargetParser.TARGET_PARSER.parseUuid(text);
      if ((punishment == null || !Intrinsics.areEqual(punishment.getUuid(), text)) && uuid != null && databaseSession.hasAllowEntry(uuid, PunishmentType.BAN) && flag) {
         databaseSession.getPlatform().getLogger().info(text2 + " was permitted to join the server.");
         return false;
      } else {
         this.disallowLogin(obj, message, text3);
         return true;
      }
   }

   public static boolean disallowIfPunishedWithDefaults(
      LoginHandler loginHandler, DatabaseSession databaseSession, Object obj, String text, String text2, CharSequence message, Punishment punishment, String text3, boolean flag, int value, Object obj2
   ) {
      if (obj2 != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 32) != 0) {
         text3 = "KICK_BANNED";
      }

      if ((value & 64) != 0) {
         flag = true;
      }

      return loginHandler.disallowIfPunished(databaseSession, obj, text, text2, message, punishment, text3, flag);
   }

   public final PlayerInfo getPlayerInfo(@NotNull Object obj) {
      PlatformBridge bridge = this.getPlatform().createBridge();
      CommandTask commandTask = this;
      return bridge.getPlayerInfo(obj, ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isSuppressGetAddressWarning());
   }

   public final void disallowLogin(@NotNull Object obj, @NotNull CharSequence message, @NotNull String text) {
      PlayerKicker.disallowLoginWithDefaults(this.getPlatform().createBridge(), obj, message, null, false, text, 12, null);
   }

   public String getServerName(@NotNull Object obj) {
      return this.getPlatform().createBridge().getTargetServerName(obj);
   }

   public void handleObj(@NotNull Object obj, @NotNull Function1 callback) {
      CommandTask commandTask = this;
      ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj2 -> {
         DatabaseSession databaseSession = (DatabaseSession) obj2;
         databaseSession.getPlatform().createBridge().handleObj(obj, (CharSequence)callback.computeObj(databaseSession));
         return Unit.UNIT;
      });
   }

   public final boolean checkDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Object obj, @NotNull String text, @NotNull String text2, @NotNull String text3, @NotNull String text4) {
      if (disallowIfPunishedWithDefaults(this, databaseSession, obj, text3, text4, Placeholders.getGeoipBlockMessage(databaseSession, text, text2), null, null, false, 96, null)) {
         CommandTask commandTask = this;
         if (((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isNotifyBannedPlayerJoin()) {
            CommandTask commandTask2 = this;
            BroadcastModule module = (BroadcastModule)commandTask2.getPlatform().getModule(BroadcastModule.class);
            CharSequence message = Message.BANNED_GEOIP_BLACKLIST;
            CharSequence[] messages = new CharSequence[]{"geoip", text, "player", text4};
            module.handleText2(text3, TextUtil.replacePairs(message, messages), true);
            return true;
         }
      }

      return false;
   }

   public final boolean checkGeoipBlock(
      @NotNull DatabaseSession databaseSession, @NotNull Object obj, @NotNull String text, @NotNull String text2, @Nullable String text3, @Nullable String text4, @NotNull Punishment punishment
   ) {
      CharSequence message = Placeholders.replaceTargetPlaceholders(databaseSession, punishment, text2, text, text3, text4, -1);
      if (disallowIfPunishedWithDefaults(this, databaseSession, obj, text, text2, message, punishment, null, false, 96, null)) {
         CommandTask commandTask = this;
         if (((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isNotifyBannedPlayerJoin()) {
            databaseSession.getDatabaseModule().handleFunction(obj2 -> {
               DatabaseSession databaseSession2 = (DatabaseSession) obj2;
               CharSequence replacePlaceholders2 = databaseSession2.replacePlaceholders(PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession2, Message.BANNED_PLAYER_JOIN, punishment, false, 2, null), text2, text2, text, text3, text4);
               CommandTask commandTask2 = this;
               ((BroadcastModule)commandTask2.getPlatform().getModule(BroadcastModule.class)).handleText2(text, replacePlaceholders2, true);
               return Unit.UNIT;
            });
         }

         if (databaseSession.getPlatform().obtainFlag4()) {
            this.handleObj(obj, obj3 -> this.computeMessage(((DatabaseSession) obj3), punishment, message));
         }

         return true;
      } else {
         return false;
      }
   }

   public final CharSequence computeMessage(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @NotNull CharSequence message) {
      CharSequence message2 = message;
      Punishment punishment2 = punishment;
      if (punishment2 == null || punishment2.getTemplate() == -1) {
         message2 = punishment.isPermanent() ? Message.PROXY_SWITCH_BANNED : Message.PROXY_SWITCH_TEMPBANNED;
      }

      return PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, message2, punishment, false, 2, null);
   }

   public Void abort() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   @Override
   public void run() {
      this.abort();
   }
}
