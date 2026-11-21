package com.nexomc.nexo.mechanics.furniture.bed;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\f\u001a\u00020\r*\u00020\u000eH\u0007b\u0002\b\u000fJ&\u0010\u0010\u001a\u00020\r*\u00020\u0011H\u0007b\u0018\b\u000f\u0012\n\b\u0012\u0012\u0006\b\n0\u00138\u0014\u0012\b\b\u0015\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0016\u001a\u00020\r*\u00020\u0017H\u0007b\u0018\b\u000f\u0012\n\b\u0012\u0012\u0006\b\n0\u00138\u0014\u0012\b\b\u0015\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0018\u001a\u00020\r*\u00020\u0019H\u0007b\u0018\b\u000f\u0012\n\b\u0012\u0012\u0006\b\n0\u00138\u0014\u0012\b\b\u0015\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001a\u001a\u00020\r*\u00020\u001bH\u0007b\u0018\b\u000f\u0012\n\b\u0012\u0012\u0006\b\n0\u00138\u0014\u0012\b\b\u0015\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001c\u001a\u00020\r*\u00020\u001dH\u0007b\u0018\b\u000f\u0012\n\b\u0012\u0012\u0006\b\n0\u00138\u001e\u0012\b\b\u0015\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001f\u001a\u00020\r*\u00020 H\u0007b\u0018\b\u000f\u0012\n\b\u0012\u0012\u0006\b\n0\u00138\u001e\u0012\b\b\u0015\u0012\u0004\b\u0007\u0010\u0002J&\u0010!\u001a\u00020\r*\u00020\"H\u0007b\u0018\b\u000f\u0012\n\b\u0012\u0012\u0006\b\n0\u00138\u0014\u0012\b\b\u0015\u0012\u0004\b\u0007\u0010\u0002J&\u0010#\u001a\u00020\r*\u00020$H\u0007b\u0018\b\u000f\u0012\n\b\u0012\u0012\u0006\b\n0\u00138\u0014\u0012\b\b\u0015\u0012\u0004\b\u0007\u0010\u0002J&\u0010%\u001a\u00020\r*\u00020&H\u0007b\u0018\b\u000f\u0012\n\b\u0012\u0012\u0006\b\n0\u00138\u0014\u0012\b\b\u0015\u0012\u0004\b\u0007\u0010\u0002J&\u0010'\u001a\u00020\r*\u00020(H\u0007b\u0018\b\u000f\u0012\n\b\u0012\u0012\u0006\b\n0\u00138\u0014\u0012\b\b\u0015\u0012\u0004\b\u0007\u0010\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006)"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBedListener;", "Lorg/bukkit/event/Listener;", "bedPlayer", "Lorg/bukkit/entity/Player;", "bed", "Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "<init>", "(Lorg/bukkit/entity/Player;Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;)V", "getBedPlayer", "()Lorg/bukkit/entity/Player;", "getBed", "()Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "onSleep", "", "Lorg/bukkit/event/world/TimeSkipEvent;", "Lorg/bukkit/event/EventHandler;", "interact", "Lorg/bukkit/event/player/PlayerInteractEvent;", "priority", "Lorg/bukkit/event/EventPriority;", "LOWEST", "ignoreCancelled", "onHotbarSwap", "Lorg/bukkit/event/player/PlayerItemHeldEvent;", "onInteractEntity", "Lorg/bukkit/event/player/PlayerInteractEntityEvent;", "onDamageEntity", "Lorg/bukkit/event/entity/EntityDamageByEntityEvent;", "onEntityDamage", "Lorg/bukkit/event/entity/EntityDamageEvent;", "HIGHEST", "onAnimation", "Lorg/bukkit/event/player/PlayerAnimationEvent;", "onProjectile", "Lorg/bukkit/event/entity/ProjectileLaunchEvent;", "onClick", "Lorg/bukkit/event/inventory/InventoryClickEvent;", "dropItem", "Lorg/bukkit/event/player/PlayerDropItemEvent;", "onEffect", "Lorg/bukkit/event/entity/EntityPotionEffectEvent;", "com.nexomc:core"}
)
public final class FurnitureBedListener implements Listener {
   @NotNull
   private final Player bedPlayer;
   @NotNull
   private final FurnitureBed bed;

   public FurnitureBedListener(@NotNull Player bedPlayer, @NotNull FurnitureBed bed) {
      super();
      this.bedPlayer = bedPlayer;
      this.bed = bed;
   }

   @NotNull
   public final Player getBedPlayer() {
      return this.bedPlayer;
   }

   @NotNull
   public final FurnitureBed getBed() {
      return this.bed;
   }

   @EventHandler
   public final void onSleep(@NotNull TimeSkipEvent timeSkipEvent) {
      if (this.bed.getSkipNight() && Intrinsics.areEqual(timeSkipEvent.getSkipReason().name(), "NIGHT_SKIP")) {
         this.bedPlayer.leaveVehicle();
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void interact(@NotNull PlayerInteractEvent playerInteractEvent) {
      if (Intrinsics.areEqual(playerInteractEvent.getPlayer(), this.bedPlayer)) {
         playerInteractEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onHotbarSwap(@NotNull PlayerItemHeldEvent playerItemHeldEvent) {
      if (Intrinsics.areEqual(playerItemHeldEvent.getPlayer(), this.bedPlayer)) {
         playerItemHeldEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onInteractEntity(@NotNull PlayerInteractEntityEvent playerInteractEntityEvent) {
      if (playerInteractEntityEvent.getPlayer() == this.bedPlayer) {
         playerInteractEntityEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onDamageEntity(@NotNull EntityDamageByEntityEvent entityDamageByEntityEvent) {
      if (entityDamageByEntityEvent.getDamager() == this.bedPlayer) {
         entityDamageByEntityEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onEntityDamage(@NotNull EntityDamageEvent entityDamageEvent) {
      if (entityDamageEvent.getEntity() == this.bedPlayer) {
         this.bedPlayer.swingMainHand();
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onAnimation(@NotNull PlayerAnimationEvent playerAnimationEvent) {
      if (playerAnimationEvent.getPlayer() == this.bedPlayer && playerAnimationEvent.getAnimationType() == PlayerAnimationType.ARM_SWING) {
         this.bedPlayer.swingMainHand();
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onProjectile(@NotNull ProjectileLaunchEvent projectileLaunchEvent) {
      if (projectileLaunchEvent.getEntity().getShooter() == this.bedPlayer) {
         projectileLaunchEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onClick(@NotNull InventoryClickEvent inventoryClickEvent) {
      if (inventoryClickEvent.getWhoClicked() == this.bedPlayer && this.bedPlayer.getGameMode() == GameMode.CREATIVE) {
         inventoryClickEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void dropItem(@NotNull PlayerDropItemEvent playerDropItemEvent) {
      if (playerDropItemEvent.getPlayer() == this.bedPlayer && this.bedPlayer.getGameMode() == GameMode.CREATIVE) {
         playerDropItemEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onEffect(@NotNull EntityPotionEffectEvent entityPotionEffectEvent) {
      if (entityPotionEffectEvent.getEntity() == this.bedPlayer) {
         this.bedPlayer.setInvisible(true);
      }

   }
}
