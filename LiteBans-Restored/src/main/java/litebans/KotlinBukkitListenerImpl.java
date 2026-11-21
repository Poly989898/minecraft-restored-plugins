package litebans;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public final class KotlinBukkitListenerImpl implements BukkitListener {
   private final Platform platform;
   private final ConfigModule configModule;
   private final PunishmentLookupModule punishmentLookupModule;
   private final DatabaseModule databaseModule;

   public KotlinBukkitListenerImpl(@NotNull Platform platform2) {
      this.platform = platform2;
      this.configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
      this.punishmentLookupModule = (PunishmentLookupModule)this.platform.getModule(PunishmentLookupModule.class);
      this.databaseModule = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
   }

   public final Platform getPlatform() {
      return this.platform;
   }

   public final ConfigModule getConfigModule() {
      return this.configModule;
   }

   public final PunishmentLookupModule getPunishmentLookupModule() {
      return this.punishmentLookupModule;
   }

   public final DatabaseModule getDatabaseModule() {
      return this.databaseModule;
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public final void handleAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent asyncPlayerChatEvent) {
      try {
         LiteSender wrapSender2 = this.platform.wrapSender(asyncPlayerChatEvent.getPlayer());
         ConfigModule configModule2 = this.configModule;
         Intrinsics.checkNotNull(wrapSender2);
         String senderAddress = configModule2.getSenderAddress(wrapSender2, asyncPlayerChatEvent);
         Punishment punishment = this.punishmentLookupModule.findPunishment(wrapSender2.getUuid(), senderAddress, this.databaseModule.getServerName());
         if (punishment != null) {
            this.handleObj(asyncPlayerChatEvent, wrapSender2, punishment);
            return;
         }
      } catch (NullPointerException e) {
         Platform platform2 = this.platform;
         if (platform2.isEnabled() && !platform2.obtainFlag3()) {
            throw e;
         }

         ConfigModule.handleConfigModule(this.configModule, e, 0, 2, null);
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public final void handlePlayerCommandPreprocessEvent(@NotNull PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
      LiteSender wrapSender2 = this.platform.wrapSender(playerCommandPreprocessEvent.getPlayer());
      ConfigModule configModule2 = this.configModule;
      Intrinsics.checkNotNull(wrapSender2);
      String senderAddress = configModule2.getSenderAddress(wrapSender2, playerCommandPreprocessEvent);
      Punishment punishment = this.punishmentLookupModule.findPunishment(wrapSender2.getUuid(), senderAddress, this.databaseModule.getServerName());
      if (punishment != null) {
         CharSequence message = playerCommandPreprocessEvent.getMessage();
         char[] chars = new char[]{' '};
         String string = (String)StringsKt.splitWithDefaults(message, chars, false, 0, 6, null).get(0);
         Settings settings = this.configModule.getSettings();
         String message2 = playerCommandPreprocessEvent.getMessage();
         if (TextUtil.hasDoubleSpace(message2)) {
            message2 = TextUtil.collapseWhitespace(message2);
         }

         if (StringsKt.containsWithDefaults(string, ':', false, 2, null) && settings.isMutesPreventBypass()) {
            CharSequence message3 = message2;
            Regex regex = new Regex("^/[^ ]+:");
            String text = "/";
            message2 = regex.replaceFirst(message3, text);
         }

         List list = settings.getMutesCommandPrefixes();
         boolean flag;
         if (list instanceof Collection && ((Collection)list).isEmpty()) {
            flag = false;
         } else {
            Iterator iterator2 = list.iterator();

            while (true) {
               if (!iterator2.hasNext()) {
                  flag = false;
                  break;
               }

               Object obj = iterator2.next();
               String string2 = (String)obj;
               if (StringsKt.checkText2(message2, string2, true)) {
                  flag = true;
                  break;
               }
            }
         }

         if (flag || settings.getMutesCommandBlacklist().contains(message2)) {
            this.handleObj(playerCommandPreprocessEvent, wrapSender2, punishment);
            return;
         }
      }
   }

   private final void handleObj(Object obj, LiteSender liteSender, Punishment punishment) {
      MuteChatTask muteChatTask = this.punishmentLookupModule.getMuteChatTask();
      PlatformBridge bridge = this.platform.createBridge();
      Intrinsics.checkNotNullExpressionValue(bridge, "");
      muteChatTask.handleObj(obj, liteSender, ((BukkitBridge)bridge).getEventMessage(obj), punishment);
   }

   static void handleKotlinBukkitListenerImpl(KotlinBukkitListenerImpl kotlinBukkitListener, Object obj, LiteSender liteSender, Punishment punishment, int value, Object obj2) {
      if ((value & 4) != 0) {
         punishment = null;
      }

      kotlinBukkitListener.handleObj(obj, liteSender, punishment);
   }

   public KotlinBukkitListenerImpl obtainKotlinBukkitListenerImpl() {
      Platform platform2 = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform2.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainKotlinBukkitListenerImpl();
   }
}
