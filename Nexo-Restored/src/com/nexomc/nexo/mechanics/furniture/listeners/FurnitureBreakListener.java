package com.nexomc.nexo.mechanics.furniture.listeners;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.breaker.FurnitureBreakerManager;
import kotlin.Metadata;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
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
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010\f\u001a\u00020\u0005*\u00020\rH\u0007b\u0002\b\u0007J\u0010\u0010\u000e\u001a\u00020\u0005*\u00020\u000fH\u0007b\u0002\b\u0007J\u0010\u0010\u0010\u001a\u00020\u0005*\u00020\u0011H\u0007b\u0002\b\u0007J\u0010\u0010\u0012\u001a\u00020\u0005*\u00020\u0013H\u0007b\u0002\b\u0007J\u0010\u0010\u0014\u001a\u00020\u0005*\u00020\u0015H\u0007b\u0002\b\u0007¨\u0006\u0016"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/listeners/FurnitureBreakListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onDamageFurniture", "", "Lorg/bukkit/event/block/BlockDamageEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "onAbort", "Lorg/bukkit/event/block/BlockDamageAbortEvent;", "onBlockBreak", "Lorg/bukkit/event/block/BlockBreakEvent;", "onDisconnect", "Lorg/bukkit/event/player/PlayerQuitEvent;", "onSwapHand", "Lorg/bukkit/event/player/PlayerSwapHandItemsEvent;", "onDropHand", "Lorg/bukkit/event/player/PlayerDropItemEvent;", "com.nexomc:core"}
)
public final class FurnitureBreakListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onDamageFurniture(@NotNull BlockDamageEvent blockDamageEvent) {
      if (blockDamageEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
         if (blockDamageEvent.getBlock().getType() == Material.BARRIER || blockDamageEvent.getBlock().isEmpty()) {
            ItemDisplay itemDisplay = NexoFurniture.baseEntity(blockDamageEvent.getBlock());
            if (itemDisplay != null) {
               ItemDisplay baseEntity = itemDisplay;
               FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
               if (furnitureMechanic != null) {
                  FurnitureMechanic it = furnitureMechanic;
                  int value = 0;
                  furnitureMechanic = it.getBreakable().getHardness() != (double)0.0F ? it : null;
                  if (furnitureMechanic != null) {
                     FurnitureMechanic mechanic = furnitureMechanic;
                     FurnitureBreakerManager furnitureBreakerManager = FurnitureBreakerManager.INSTANCE;
                     Player player = blockDamageEvent.getPlayer();
                     Block block = blockDamageEvent.getBlock();
                     furnitureBreakerManager.startFurnitureBreak(player, baseEntity, mechanic, block);
                     return;
                  }
               }

            }
         }
      }
   }

   @EventHandler
   public final void onAbort(@NotNull BlockDamageAbortEvent blockDamageAbortEvent) {
      FurnitureBreakerManager furnitureBreakerManager = FurnitureBreakerManager.INSTANCE;
      Player player = blockDamageAbortEvent.getPlayer();
      furnitureBreakerManager.stopFurnitureBreak(player);
   }

   @EventHandler
   public final void onBlockBreak(@NotNull BlockBreakEvent blockBreakEvent) {
      FurnitureBreakerManager furnitureBreakerManager = FurnitureBreakerManager.INSTANCE;
      Player player = blockBreakEvent.getPlayer();
      furnitureBreakerManager.stopFurnitureBreak(player);
   }

   @EventHandler
   public final void onDisconnect(@NotNull PlayerQuitEvent playerQuitEvent) {
      FurnitureBreakerManager furnitureBreakerManager = FurnitureBreakerManager.INSTANCE;
      Player player = playerQuitEvent.getPlayer();
      furnitureBreakerManager.stopFurnitureBreak(player);
   }

   @EventHandler
   public final void onSwapHand(@NotNull PlayerSwapHandItemsEvent playerSwapHandItemsEvent) {
      FurnitureBreakerManager furnitureBreakerManager = FurnitureBreakerManager.INSTANCE;
      Player player = playerSwapHandItemsEvent.getPlayer();
      furnitureBreakerManager.stopFurnitureBreak(player);
   }

   @EventHandler
   public final void onDropHand(@NotNull PlayerDropItemEvent playerDropItemEvent) {
      FurnitureBreakerManager furnitureBreakerManager = FurnitureBreakerManager.INSTANCE;
      Player player = playerDropItemEvent.getPlayer();
      furnitureBreakerManager.stopFurnitureBreak(player);
   }
}
