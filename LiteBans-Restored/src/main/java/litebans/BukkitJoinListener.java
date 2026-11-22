package litebans;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public final class BukkitJoinListener implements BukkitListener {
   private final Platform platform;

   public BukkitJoinListener(@NotNull Platform platform) {
      this.platform = platform;
   }

   @EventHandler
   public final void handlePlayerJoinEvent(@NotNull PlayerJoinEvent playerJoinEvent) {
      DatabaseModule databaseModule;
      BroadcastModule broadcastModule;
      ConfigModule configModule;
      String text;
      Player player;
      String text2;
      label12: {
         player = playerJoinEvent.getPlayer();
         databaseModule = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
         broadcastModule = (BroadcastModule)this.platform.getModule(BroadcastModule.class);
         configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
         PlatformBridge bridge = this.platform.createBridge();
         Intrinsics.checkNotNullExpressionValue(bridge, "");
         text = ((BukkitBridge)bridge).getOfflinePlayerName(player);
         InetSocketAddress address = player.getAddress();
         if (address != null) {
            InetAddress address2 = address.getAddress();
            if (address2 != null) {
               text2 = address2.getHostAddress();
               break label12;
            }
         }

         text2 = null;
      }

      String text3 = text2;
      String string = player.getUniqueId().toString();
      BooleanFlag booleanFlag = new BooleanFlag();
      databaseModule.handleFunction(obj -> {
         DatabaseSession databaseSession = (DatabaseSession) obj;
         LiteSender wrapSender2 = databaseSession.getPlatform().wrapSender(player);
         Punishment punishment = PunishmentQueries.findPunishmentWithDefaults(databaseSession, string, text3, PunishmentType.BAN, false, false, 24, null);
         if (configModule.isDebugAtLeast(4)) {
            configModule.logDebug("Ban: " + punishment + " (valid: " + (punishment != null) + ')');
         }

         if (punishment != null) {
            if (broadcastModule.removePendingUnban(punishment, text)) {
               return Unit.UNIT;
            }

            Intrinsics.checkNotNull(wrapSender2);
            Intrinsics.checkNotNull(broadcastModule);
            Intrinsics.checkNotNull(configModule);
            this.handleDatabaseSession(databaseSession, wrapSender2, string, text, text3, punishment, broadcastModule, configModule, databaseSession);
            booleanFlag.flag = true;
         }

         if (!booleanFlag.flag && configModule.getSettings().isBanNames()) {
            Punishment punishment2 = PunishmentQueries.findPunishmentWithDefaults(databaseSession, text, string, databaseModule.getServerName(), null, 8, null);
            if (punishment2 != null) {
               Intrinsics.checkNotNull(wrapSender2);
               Intrinsics.checkNotNull(broadcastModule);
               Intrinsics.checkNotNull(configModule);
               this.handleDatabaseSession(databaseSession, wrapSender2, string, text, text3, punishment2, broadcastModule, configModule, databaseSession);
               booleanFlag.flag = true;
            }
         }

         return Unit.UNIT;
      });
      broadcastModule.handleText(text, text3, string, databaseModule.getServerName(), booleanFlag.flag);
   }

   private final void handleDatabaseSession(DatabaseSession databaseSession, LiteSender liteSender, String text, String text2, String text3, Punishment punishment, BroadcastModule broadcastModule, ConfigModule configModule, DatabaseSession databaseSession2) {
      try {
         if (configModule.getSettings().isNotifyBannedPlayerJoin()) {
            CharSequence replacePlaceholders2 = databaseSession.replacePlaceholders(PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Message.BANNED_PLAYER_JOIN, punishment, false, 2, null), liteSender);
            broadcastModule.handleText2(text, replacePlaceholders2, true);
         }
      } finally {
         Intrinsics.checkNotNull(punishment);
         broadcastModule.sendMessageSync(liteSender, Placeholders.replaceTargetPlaceholders(databaseSession, punishment, text2, text, text3, null, -1));
      }
   }

   public BukkitJoinListener obtainLiteBukkitListenerImpl2() {
      Platform platform = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainLiteBukkitListenerImpl2();
   }
}
