package com.nexomc.nexo.mechanics.furniture.listeners;

import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import io.papermc.paper.event.entity.EntityMoveEvent;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerKickEvent.Cause;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J&\u0010\t\u001a\u00020\n*\u00020\u000bH\u0007b\u0018\b\f\u0012\n\b\r\u0012\u0006\b\n0\u000e8\u000f\u0012\b\b\u0010\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0011\u001a\u00020\n*\u00020\u0012H\u0007b\u0018\b\f\u0012\n\b\r\u0012\u0006\b\n0\u000e8\u0013\u0012\b\b\u0010\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0014\u001a\u00020\n*\u00020\u0015H\u0007b\u0018\b\f\u0012\n\b\r\u0012\u0006\b\n0\u000e8\u0013\u0012\b\b\u0010\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0016\u001a\u00020\n*\u00020\u0017H\u0007b\u0018\b\f\u0012\n\b\r\u0012\u0006\b\n0\u000e8\u000f\u0012\b\b\u0010\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010\u0018\u001a\u00020\n*\u00020\u0019H\u0007b\u0002\b\fJ\u0010\u0010\u0018\u001a\u00020\n*\u00020\u001aH\u0007b\u0002\b\fJ&\u0010\u001b\u001a\u00020\n*\u00020\u001cH\u0007b\u0018\b\f\u0012\n\b\r\u0012\u0006\b\n0\u000e8\u000f\u0012\b\b\u0010\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001d\u001a\u00020\n*\u00020\u001eH\u0007b\u0018\b\f\u0012\n\b\r\u0012\u0006\b\n0\u000e8\u000f\u0012\b\b\u0010\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001f\u001a\u00020\n*\u00020 H\u0007b\u0018\b\f\u0012\n\b\r\u0012\u0006\b\n0\u000e8\u000f\u0012\b\b\u0010\u0012\u0004\b\u0007\u0010\u0002R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006!"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/listeners/FurnitureBarrierHitboxListener;", "Lorg/bukkit/event/Listener;", "factory", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureFactory;", "<init>", "(Lcom/nexomc/nexo/mechanics/furniture/FurnitureFactory;)V", "flightCache", "Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "Ljava/util/UUID;", "onHangingBreak", "", "Lorg/bukkit/event/hanging/HangingBreakEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "onPlace", "Lorg/bukkit/event/entity/EntityPlaceEvent;", "HIGH", "onBreak", "Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureBreakEvent;", "onFlowThroughBarrier", "Lorg/bukkit/event/block/BlockFromToEvent;", "onGrow", "Lorg/bukkit/event/block/BlockGrowEvent;", "Lorg/bukkit/event/world/StructureGrowEvent;", "onSnow", "Lorg/bukkit/event/block/BlockFormEvent;", "onPiston", "Lorg/bukkit/event/block/BlockPistonExtendEvent;", "onPistonRetract", "Lorg/bukkit/event/block/BlockPistonRetractEvent;", "com.nexomc:core"}
)
public final class FurnitureBarrierHitboxListener implements Listener {
   @NotNull
   private final ObjectOpenHashSet<UUID> flightCache;

   public FurnitureBarrierHitboxListener(@NotNull FurnitureFactory factory) {
      super();
      this.flightCache = new ObjectOpenHashSet();
      if (factory.getTryPreventingBarrierKick() && !Bukkit.getServer().getAllowFlight()) {
         Listener[] listeners = new Listener[]{new Listener() {
            @EventHandler(
               priority = EventPriority.HIGHEST,
               ignoreCancelled = true
            )
            public final void onMove(PlayerMoveEvent playerMoveEvent) {
               if (playerMoveEvent.hasExplicitlyChangedBlock()) {
                  ((Collection)FurnitureBarrierHitboxListener.this.flightCache).remove(playerMoveEvent.getPlayer().getUniqueId());
               }
            }

            @EventHandler(
               priority = EventPriority.HIGHEST,
               ignoreCancelled = true
            )
            public final void onKick(PlayerKickEvent playerKickEvent) {
               if (playerKickEvent.getCause() == Cause.FLYING_PLAYER) {
                  if (!FurnitureBarrierHitboxListener.this.flightCache.contains(playerKickEvent.getPlayer().getUniqueId())) {
                     IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
                     Player player = playerKickEvent.getPlayer();
                     if (!companion.standingOnFurniture(player)) {
                        return;
                     }
                  }

                  ((Collection)FurnitureBarrierHitboxListener.this.flightCache).add(playerKickEvent.getPlayer().getUniqueId());
                  playerKickEvent.setCancelled(true);
               }
            }
         }};
         factory.registerListeners(listeners);
      }

      if (factory.getHandleNonPlayerBarrierCollision()) {
         Listener[] listeners2 = new Listener[]{new Listener() {
            @EventHandler(
               priority = EventPriority.HIGHEST,
               ignoreCancelled = true
            )
            public final void onMobMove(EntityMoveEvent entityMoveEvent) {
               if (entityMoveEvent.hasExplicitlyChangedBlock() && !(entityMoveEvent.getTo().getY() < entityMoveEvent.getFrom().getY())) {
                  IFurniturePacketManager.Companion companion2 = IFurniturePacketManager.Companion;
                  Location to = entityMoveEvent.getTo();
                  if (!companion2.blockIsHitbox(to, (UUID)null)) {
                     companion2 = IFurniturePacketManager.Companion;
                     to = entityMoveEvent.getFrom();
                     if (!companion2.blockIsHitbox(to, (UUID)null)) {
                        return;
                     }
                  }

                  entityMoveEvent.setCancelled(true);
               }
            }
         }};
         factory.registerListeners(listeners2);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onHangingBreak(@NotNull HangingBreakEvent hangingBreakEvent) {
      if (hangingBreakEvent.getCause() == RemoveCause.PHYSICS) {
         Block block = hangingBreakEvent.getEntity().getLocation().getBlock().getRelative(hangingBreakEvent.getEntity().getAttachedFace());
         Block relative = block;
         if (IFurniturePacketManager.Companion.blockIsHitbox(relative, (UUID)null, false)) {
            hangingBreakEvent.setCancelled(true);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public final void onPlace(@NotNull EntityPlaceEvent entityPlaceEvent) {
      if (entityPlaceEvent.getEntity() instanceof ArmorStand) {
         IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
         Block block = entityPlaceEvent.getBlock();
         if (companion.blockIsHitbox(block, (UUID)null, false)) {
            entityPlaceEvent.getEntity().setGravity(false);
            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public final void onBreak(@NotNull NexoFurnitureBreakEvent nexoFurnitureBreakEvent) {
      Location[] locations = (Location[])IFurniturePacketManager.Companion.getBarrierHitboxLocationMap().get(nexoFurnitureBreakEvent.getBaseEntity().getUniqueId());
      if (locations != null) {
         Location[] hitboxLocs = locations;
         Collection nearbyEntitiesByType = nexoFurnitureBreakEvent.getBaseEntity().getWorld().getNearbyEntitiesByType(ArmorStand.class, nexoFurnitureBreakEvent.getBaseEntity().getLocation(), (double)8.0F);
         Iterable iterable = (Iterable)nearbyEntitiesByType;

         for(Object element : iterable) {
            ArmorStand it = (ArmorStand)element;
            int value = 0;
            if (ArraysKt.contains(hitboxLocs, it.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation())) {
               it.setGravity(true);
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onFlowThroughBarrier(@NotNull BlockFromToEvent blockFromToEvent) {
      IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
      Block toBlock = blockFromToEvent.getToBlock();
      if (companion.blockIsHitbox(toBlock, (UUID)null, true)) {
         blockFromToEvent.setCancelled(true);
      }

   }

   @EventHandler
   public final void onGrow(@NotNull BlockGrowEvent blockGrowEvent) {
      IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
      Block block = blockGrowEvent.getBlock();
      if (companion.blockIsHitbox(block, (UUID)null, true)) {
         blockGrowEvent.setCancelled(true);
      }

   }

   @EventHandler
   public final void onGrow(@NotNull StructureGrowEvent structureGrowEvent) {
      List blocks = structureGrowEvent.getBlocks();
      Iterable iterable = (Iterable)blocks;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            BlockState it = (BlockState)element;
            int value = 0;
            IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
            Block block = it.getBlock();
            if (companion.blockIsHitbox(block, (UUID)null, true)) {
               flag = true;
               break;
            }
         }
      }

      if (flag) {
         structureGrowEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onSnow(@NotNull BlockFormEvent blockFormEvent) {
      IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
      Block block = blockFormEvent.getBlock();
      if (companion.blockIsHitbox(block, (UUID)null, true)) {
         blockFormEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPiston(@NotNull BlockPistonExtendEvent blockPistonExtendEvent) {
      Vector direction = blockPistonExtendEvent.getDirection().getDirection();
      Vector dir = direction;
      IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
      Location location = blockPistonExtendEvent.getBlock().getLocation().add(dir);
      if (companion.blockIsHitbox(location, (UUID)null)) {
         blockPistonExtendEvent.setCancelled(true);
      } else {
         Iterable blocks = (Iterable)blockPistonExtendEvent.getBlocks();
         boolean flag;
         if (blocks instanceof Collection && ((Collection)blocks).isEmpty()) {
            flag = false;
         } else {
            Iterator iterator = blocks.iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  flag = false;
                  break;
               }

               Object element = iterator.next();
               Block it = (Block)element;
               int value = 0;
               IFurniturePacketManager.Companion companion2 = IFurniturePacketManager.Companion;
               location = it.getLocation().add(dir);
               if (companion2.blockIsHitbox(location, (UUID)null)) {
                  flag = true;
                  break;
               }
            }
         }

         if (flag) {
            blockPistonExtendEvent.setCancelled(true);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPistonRetract(@NotNull BlockPistonRetractEvent blockPistonRetractEvent) {
      Vector direction = blockPistonRetractEvent.getDirection().getDirection();
      Vector dir = direction;
      Iterable blocks = (Iterable)blockPistonRetractEvent.getBlocks();
      boolean flag;
      if (blocks instanceof Collection && ((Collection)blocks).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = blocks.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            Block it = (Block)element;
            int value = 0;
            IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
            Location location = it.getLocation().add(dir);
            if (companion.blockIsHitbox(location, (UUID)null)) {
               flag = true;
               break;
            }
         }
      }

      if (flag) {
         blockPistonRetractEvent.setCancelled(true);
      }

   }
}
