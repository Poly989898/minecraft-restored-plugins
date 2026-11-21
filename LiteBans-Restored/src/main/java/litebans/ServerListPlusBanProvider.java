package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import net.minecrell.serverlistplus.core.plugin.ServerListPlusPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ServerListPlusBanProvider implements BanProvider {
   private final Platform platform;
   private final ThreadLocal threadLocal;
   private final AtomicBoolean atomicBoolean;

   public ServerListPlusBanProvider(@NotNull Platform platform) {
      this.platform = platform;
      this.threadLocal = new PunishmentCacheLocal();
      this.atomicBoolean = new AtomicBoolean();
   }

   public final ThreadLocal getThreadLocal() {
      return this.threadLocal;
   }

   public final AtomicBoolean getAtomicBoolean() {
      return this.atomicBoolean;
   }

   public final Punishment computePunishment(@NotNull UUID uuid) {
      try {
         WeakHashMap weakHashMap = (WeakHashMap)this.getThreadLocal().get();
         DatabaseModule module = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
         Punishment punishment = null;

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               punishment = PunishmentQueries.findPunishmentWithDefaults(session, uuid.toString(), null, PunishmentType.BAN, module.getServerName(), false, false, 48, null);
               if (punishment != null ? punishment.checkText(uuid.toString(), null, DatabaseModule.getCurrentTimeWithDefaults(module, false, 1, null)) : false) {
                  Intrinsics.checkNotNull(weakHashMap);
                  Map map = weakHashMap;
                  Intrinsics.checkNotNull(punishment);
                  map.put(uuid, punishment);
               } else {
                  punishment = null;
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

         return punishment;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public String getBanReason(@NotNull PlayerIdentity playerIdentity) {
      UUID uuid = playerIdentity.getUuid();
      Punishment punishment = (Punishment)((WeakHashMap)this.getThreadLocal().get()).get(uuid);
      if (punishment != null) {
         CharSequence reason = punishment.getReason();
         if (reason != null) {
            return reason.toString();
         }
      }

      Punishment punishment2 = this.computePunishment(playerIdentity.getUuid());
      return (punishment2 != null ? punishment2.getReason() : "null").toString();
   }

   public boolean isBanned(@NotNull PlayerIdentity playerIdentity) {
      Punishment punishment = this.computePunishment(playerIdentity.getUuid());
      return punishment != null;
   }

   public String getBanOperator(@NotNull PlayerIdentity playerIdentity) {
      UUID uuid = playerIdentity.getUuid();
      Punishment punishment = (Punishment)((WeakHashMap)this.getThreadLocal().get()).get(uuid);
      if (punishment != null) {
         return punishment.getBannedByName();
      }

      Punishment punishment2 = this.computePunishment(playerIdentity.getUuid());
      return punishment2 != null ? punishment2.getBannedByName() : null;
   }

   public Date getBanExpiration(@NotNull PlayerIdentity playerIdentity) {
      UUID uuid = playerIdentity.getUuid();
      Punishment punishment = (Punishment)((WeakHashMap)this.getThreadLocal().get()).get(uuid);
      if (punishment != null) {
         return punishment.getUntil() <= 0L ? null : new Date(punishment.getUntil());
      } else {
         Punishment punishment2 = this.computePunishment(playerIdentity.getUuid());
         if (punishment2 != null) {
            return punishment2.isPermanent() ? null : new Date(punishment2.getUntil());
         } else {
            return null;
         }
      }
   }

   public final void handleObj(@Nullable Object obj) {
      Intrinsics.checkNotNullExpressionValue(obj, "");
      ServerListPlusPlugin serverListPlusPlugin = (ServerListPlusPlugin)obj;
      ServerListPlusCore core = serverListPlusPlugin.getCore();
      if (core == null) {
         throw new NullPointerException("core");
      }

      core.setBanProvider(this);
      this.platform.getLogger().info("Registered as ServerListPlus ban provider.");
   }
}
