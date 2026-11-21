package com.nexomc.nexo.mechanics.custom_block;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.utils.breaker.BreakerManager;
import kotlin.Metadata;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0006\u001a\u00020\u0007*\u00020\bH\u0007b\u0018\b\t\u0012\n\b\n\u0012\u0006\b\n0\u000b8\f\u0012\b\b\r\u0012\u0004\b\u0007\u0010\u0002J\u001c\u0010\u000e\u001a\u00020\u0007*\u00020\u000fH\u0007b\u000e\b\t\u0012\n\b\n\u0012\u0006\b\n0\u000b8\u0010J\u0010\u0010\u0011\u001a\u00020\u0007*\u00020\u0012H\u0007b\u0002\b\tJ\u0010\u0010\u0013\u001a\u00020\u0007*\u00020\u0014H\u0007b\u0002\b\tJ\u0010\u0010\u0015\u001a\u00020\u0007*\u00020\u0016H\u0007b\u0002\b\tJ\u0010\u0010\u0017\u001a\u00020\u0007*\u00020\u0018H\u0007b\u0002\b\tR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMiningListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "breakerManager", "Lcom/nexomc/nexo/utils/breaker/BreakerManager;", "onDamageCustomBlock", "", "Lorg/bukkit/event/block/BlockDamageEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "onDamageAbort", "Lorg/bukkit/event/block/BlockDamageAbortEvent;", "LOWEST", "onBlockBreak", "Lorg/bukkit/event/block/BlockBreakEvent;", "onDisconnect", "Lorg/bukkit/event/player/PlayerQuitEvent;", "onSwapHand", "Lorg/bukkit/event/player/PlayerSwapHandItemsEvent;", "onDropHand", "Lorg/bukkit/event/player/PlayerDropItemEvent;", "com.nexomc:core"}
)
public final class CustomBlockMiningListener implements Listener {
   @NotNull
   private final BreakerManager breakerManager;

   public CustomBlockMiningListener() {
      this.breakerManager = NexoPlugin.Companion.instance().breakerManager();
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onDamageCustomBlock(@NotNull BlockDamageEvent blockDamageEvent) {
      Block block = blockDamageEvent.getBlock();
      CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(block);
      if (customBlockMechanic != null) {
         CustomBlockMechanic mechanic = customBlockMechanic;
         if (blockDamageEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
            BreakerManager breakerManager2 = this.breakerManager;
            Player player = blockDamageEvent.getPlayer();
            Block block2 = blockDamageEvent.getBlock();
            breakerManager2.startBlockBreak(player, block2, mechanic);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public final void onDamageAbort(@NotNull BlockDamageAbortEvent blockDamageAbortEvent) {
      BreakerManager breakerManager2 = this.breakerManager;
      Player player = blockDamageAbortEvent.getPlayer();
      breakerManager2.stopBlockBreak(player);
   }

   @EventHandler
   public final void onBlockBreak(@NotNull BlockBreakEvent blockBreakEvent) {
      BreakerManager breakerManager2 = this.breakerManager;
      Player player = blockBreakEvent.getPlayer();
      breakerManager2.stopBlockBreak(player);
   }

   @EventHandler
   public final void onDisconnect(@NotNull PlayerQuitEvent playerQuitEvent) {
      BreakerManager breakerManager2 = this.breakerManager;
      Player player = playerQuitEvent.getPlayer();
      breakerManager2.stopBlockBreak(player);
   }

   @EventHandler
   public final void onSwapHand(@NotNull PlayerSwapHandItemsEvent playerSwapHandItemsEvent) {
      BreakerManager breakerManager2 = this.breakerManager;
      Player player = playerSwapHandItemsEvent.getPlayer();
      breakerManager2.stopBlockBreak(player);
   }

   @EventHandler
   public final void onDropHand(@NotNull PlayerDropItemEvent playerDropItemEvent) {
      BreakerManager breakerManager2 = this.breakerManager;
      Player player = playerDropItemEvent.getPlayer();
      breakerManager2.stopBlockBreak(player);
   }
}
