package com.nexomc.nexo.pack;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.pack.multipack.PlayerPackData;
import com.nexomc.nexo.pack.server.NexoPackServer;
import com.nexomc.nexo.proxy.ProxyHelpers;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.VersionUtil;
import kotlin.Metadata;
import kotlin.Unit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\nJ\u001c\u0010\u000b\u001a\u00020\u0005*\u00020\u0006H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\fJ\u001c\u0010\r\u001a\u00020\u0005*\u00020\u0006H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u000e¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/pack/PackListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onSyncMultipackSelections", "", "Lorg/bukkit/event/player/PlayerJoinEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGH", "onPlayerConnect", "NORMAL", "firstJoinProxy", "HIGHEST", "com.nexomc:core"}
)
public final class PackListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGH
   )
   public final void onSyncMultipackSelections(@NotNull PlayerJoinEvent playerJoinEvent) {
      PlayerPackData playerPackData = PlayerPackData.INSTANCE;
      Player player = playerJoinEvent.getPlayer();
      playerPackData.syncSelections(player);
   }

   @EventHandler(
      priority = EventPriority.NORMAL
   )
   public final void onPlayerConnect(@NotNull PlayerJoinEvent playerJoinEvent) {
      if (Settings.PACK_SEND_ON_JOIN.toBool() || Settings.PACK_SEND_PRE_JOIN.toBool()) {
         if (!Settings.PACK_SEND_PRE_JOIN.toBool() || !VersionUtil.atleast("1.21.7")) {
            if (Settings.PACK_SEND_ON_JOIN.toBool() || !Settings.PACK_SEND_PRE_JOIN.toBool() || !playerJoinEvent.getPlayer().hasResourcePack()) {
               int delay = Settings.PACK_SEND_DELAY.toInt(-1);
               if (delay <= 0) {
                  NexoPackServer packServer = NexoPlugin.Companion.instance().packServer();
                  Player player = playerJoinEvent.getPlayer();
                  packServer.sendPack(player);
               } else {
                  SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
                  Player player2 = playerJoinEvent.getPlayer();
                  schedulerUtils.runAtEntityLater((Entity)player2, (long)delay * 20L, (a0_ -> PackListener.onPlayerConnect$lambda$0(playerJoinEvent, a0_)));
               }

            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void firstJoinProxy(@NotNull PlayerJoinEvent playerJoinEvent) {
      if (Bukkit.getOnlinePlayers().size() == 1) {
         SchedulerUtils.INSTANCE.runTaskLater(2L, PackListener::firstJoinProxy$lambda$0);
      }

   }

   private static final Unit onPlayerConnect$lambda$0(PlayerJoinEvent playerJoinEvent, Entity it) {
      NexoPackServer packServer = NexoPlugin.Companion.instance().packServer();
      Player player = playerJoinEvent.getPlayer();
      packServer.sendPack(player);
      return Unit.INSTANCE;
   }

   private static final Unit firstJoinProxy$lambda$0() {
      ProxyHelpers.INSTANCE.sendProxyPackData();
      ProxyHelpers.INSTANCE.sendProxyGlyphData();
      return Unit.INSTANCE;
   }
}
