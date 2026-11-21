package com.nexomc.nexo.mechanics.furniture.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockHelpers;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.furniture.BlockLocation;
import com.nexomc.nexo.mechanics.furniture.FurnitureBaseEntity;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.seats.FurnitureSeat;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.protectionlib.ProtectionLib;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007J\u0010\u0010\b\u001a\u00020\u0005*\u00020\tH\u0007b\u0002\b\u0007J\u0010\u0010\n\u001a\u00020\u0005*\u00020\u000bH\u0007b\u0002\b\u0007J\u0010\u0010\f\u001a\u00020\u0005*\u00020\rH\u0007b\u0002\b\u0007J\u0010\u0010\f\u001a\u00020\u0005*\u00020\u000eH\u0007b\u0002\b\u0007J\u001c\u0010\u000f\u001a\u00020\u0005*\u00020\u0010H\u0007b\u000e\b\u0007\u0012\n\b\u0011\u0012\u0006\b\n0\u00128\u0013J\u0010\u0010\u0014\u001a\u00020\u0005*\u00020\u0015H\u0007b\u0002\b\u0007J\u0010\u0010\u0016\u001a\u00020\u0005*\u00020\u0017H\u0007b\u0002\b\u0007J&\u0010\u0018\u001a\u00020\u0005*\u00020\u0019H\u0007b\u0018\b\u0007\u0012\n\b\u0011\u0012\u0006\b\n0\u00128\u001a\u0012\b\b\u001b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001c\u001a\u00020\u0005*\u00020\u001dH\u0007b\u0018\b\u0007\u0012\b\b\u001b\u0012\u0004\b\u0007\u0010\u0002\u0012\n\b\u0011\u0012\u0006\b\n0\u00128\u001eJ&\u0010\u001f\u001a\u00020\u0005*\u00020\u001dH\u0007b\u0018\b\u0007\u0012\b\b\u001b\u0012\u0004\b\u0007\u0010\u0002\u0012\n\b\u0011\u0012\u0006\b\n0\u00128\u001e¨\u0006 "},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/listeners/FurniturePacketListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onPlayerTrackFurniture", "", "Lio/papermc/paper/event/player/PlayerTrackEntityEvent;", "Lorg/bukkit/event/EventHandler;", "onPlayerUntrackFurniture", "Lio/papermc/paper/event/player/PlayerUntrackEntityEvent;", "onLoad", "Lcom/destroystokyo/paper/event/entity/EntityAddToWorldEvent;", "onUnload", "Lcom/destroystokyo/paper/event/entity/EntityRemoveFromWorldEvent;", "Lorg/bukkit/event/world/WorldUnloadEvent;", "onTeleportFurniture", "Lorg/bukkit/event/entity/EntityTeleportEvent;", "priority", "Lorg/bukkit/event/EventPriority;", "MONITOR", "onFurnitureFactory", "Lcom/nexomc/nexo/api/events/NexoItemsLoadedEvent;", "onUseUnknownEntity", "Lcom/destroystokyo/paper/event/player/PlayerUseUnknownEntityEvent;", "onPlayerInteractBarrierHitbox", "Lorg/bukkit/event/player/PlayerInteractEvent;", "HIGH", "ignoreCancelled", "onPlaceInBarrier", "Lorg/bukkit/event/block/BlockPlaceEvent;", "HIGHEST", "onPlaceInLight", "com.nexomc:core"}
)
public final class FurniturePacketListener implements Listener {
   @EventHandler
   public final void onPlayerTrackFurniture(@NotNull PlayerTrackEntityEvent playerTrackEntityEvent) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator p0 = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (p0.hasNext()) {
            MechanicFactory it = (MechanicFactory)p0.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager = iFurniturePacketManager;
            Entity p0_10 = playerTrackEntityEvent.getEntity();
            int value2 = 0;
            Entity entity = p0_10.isValid() ? p0_10 : null;
            ItemDisplay itemDisplay2 = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
            if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) == null) {
               return;
            }

            ItemDisplay itemDisplay = itemDisplay2;
            FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)itemDisplay);
            if (furnitureMechanic == null) {
               furnitureMechanic = FurnitureMechanic.Companion.invalidFurniture(itemDisplay);
               if (furnitureMechanic == null) {
                  return;
               }
            }

            FurnitureMechanic mechanic = furnitureMechanic;
            SchedulerUtils.INSTANCE.runAtEntityLater((Entity)itemDisplay, 4L, (a0_ -> FurniturePacketListener.onPlayerTrackFurniture$lambda$0(packetManager, itemDisplay, mechanic, playerTrackEntityEvent, (Entity)a0_)));
            return;
         }
      }

   }

   @EventHandler
   public final void onPlayerUntrackFurniture(@NotNull PlayerUntrackEntityEvent playerUntrackEntityEvent) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator p0 = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (p0.hasNext()) {
            MechanicFactory it = (MechanicFactory)p0.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager = iFurniturePacketManager;
            Entity p0_9 = playerUntrackEntityEvent.getEntity();
            int value2 = 0;
            Entity entity = p0_9.isValid() ? p0_9 : null;
            ItemDisplay itemDisplay2 = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
            if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) == null) {
               return;
            }

            ItemDisplay itemDisplay = itemDisplay2;
            FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)itemDisplay);
            if (furnitureMechanic == null) {
               furnitureMechanic = FurnitureMechanic.Companion.invalidFurniture(itemDisplay);
               if (furnitureMechanic == null) {
                  return;
               }
            }

            FurnitureMechanic mechanic = furnitureMechanic;
            Player player = playerUntrackEntityEvent.getPlayer();
            packetManager.removeHitboxEntityPacket(itemDisplay, mechanic, player);
            player = playerUntrackEntityEvent.getPlayer();
            packetManager.removeBarrierHitboxPacket(itemDisplay, mechanic, player);
            player = playerUntrackEntityEvent.getPlayer();
            packetManager.removeLightMechanicPacket(itemDisplay, mechanic, player);
            player = playerUntrackEntityEvent.getPlayer();
            packetManager.removeTextEntityMechanicPacket(itemDisplay, mechanic, player);
            return;
         }
      }

   }

   @EventHandler
   public final void onLoad(@NotNull EntityAddToWorldEvent entityAddToWorldEvent) {
      Entity entity = entityAddToWorldEvent.getEntity();
      ItemDisplay itemDisplay2 = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
      if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
         ItemDisplay itemDisplay = itemDisplay2;
         FurnitureBaseEntity furnitureBaseEntity = (FurnitureBaseEntity)IFurniturePacketManager.Companion.getFurnitureBaseMap().get(itemDisplay.getUniqueId());
         if (furnitureBaseEntity != null) {
            FurnitureBaseEntity it = furnitureBaseEntity;
            int value = 0;
            furnitureBaseEntity = it.getBaseId() != itemDisplay.getEntityId() ? it : null;
            if (furnitureBaseEntity != null) {
               FurnitureBaseEntity it_8 = furnitureBaseEntity;
               value = 0;
               Map furnitureBaseMap = IFurniturePacketManager.Companion.getFurnitureBaseMap();
               UUID uniqueId = itemDisplay.getUniqueId();
               furnitureBaseMap.remove(uniqueId, it_8);
            }
         }

         SchedulerUtils.INSTANCE.runAtEntityLater((Entity)itemDisplay, 2L, (a0_ -> FurniturePacketListener.onLoad$lambda$2(itemDisplay, (Entity)a0_)));
      }
   }

   @EventHandler
   public final void onUnload(@NotNull EntityRemoveFromWorldEvent entityRemoveFromWorldEvent) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator it = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (it.hasNext()) {
            MechanicFactory mechanicFactory2 = (MechanicFactory)it.next();
            int value = 0;
            mechanicFactory = mechanicFactory2;
            if (!(mechanicFactory2 instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            mechanicFactory2 = (FurnitureFactory)mechanicFactory;
            if (mechanicFactory2 == null) {
               continue;
            }

            mechanicFactory = mechanicFactory2;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager = iFurniturePacketManager;
            Entity it_7 = entityRemoveFromWorldEvent.getEntity();
            int value2 = 0;
            BlockHelpers blockHelpers = BlockHelpers.INSTANCE;
            Location location = it_7.getLocation();
            Entity entity = blockHelpers.isLoaded(location) ? it_7 : null;
            ItemDisplay itemDisplay2 = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
            if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) == null) {
               return;
            }

            ItemDisplay itemDisplay = itemDisplay2;
            FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)itemDisplay);
            if (furnitureMechanic == null) {
               furnitureMechanic = FurnitureMechanic.Companion.invalidFurniture(itemDisplay);
               if (furnitureMechanic == null) {
                  return;
               }
            }

            FurnitureMechanic mechanic = furnitureMechanic;
            SchedulerUtils.INSTANCE.runAtEntityLater((Entity)itemDisplay, 1L, (a0_ -> FurniturePacketListener.onUnload$lambda$1(itemDisplay, (Entity)a0_)));
            IFurniturePacketManager.Companion.getFurnitureBaseMap().remove(itemDisplay.getUniqueId());
            packetManager.removeHitboxEntityPacket(itemDisplay, mechanic);
            packetManager.removeBarrierHitboxPacket(itemDisplay, mechanic);
            packetManager.removeLightMechanicPacket(itemDisplay, mechanic);
            packetManager.removeTextEntityMechanicPacket(itemDisplay, mechanic);
            return;
         }
      }

   }

   @EventHandler
   public final void onUnload(@NotNull WorldUnloadEvent worldUnloadEvent) {
      List entities = worldUnloadEvent.getWorld().getEntities();
      Iterable iterable = (Iterable)entities;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         if (element instanceof ItemDisplay) {
            destination.add(element);
         }
      }

      iterable = (Iterable)((List)destination);

      for(Object obj : iterable) {
         ItemDisplay itemDisplay = (ItemDisplay)obj;
         int value = 0;
         UUID uniqueId = itemDisplay.getUniqueId();
         UUID uuid = uniqueId;
         IFurniturePacketManager.Companion.getFurnitureBaseMap().remove(uuid);
         IFurniturePacketManager.Companion.getInteractionHitboxPacketMap().remove(uuid);
         IFurniturePacketManager.Companion.getShulkerHitboxPacketMap().remove(uuid);
         IFurniturePacketManager.Companion.getGhastHitboxPacketMap().remove(uuid);
         IFurniturePacketManager.Companion.removeBarrierHitboxLocations(uuid);
         IFurniturePacketManager.Companion.getBarrierHitboxPositionMap().remove(uuid);
         IFurniturePacketManager.Companion.getBarrierHitboxLocationMap().remove(uuid);
         IFurniturePacketManager.Companion.getLightPositionMap().remove(uuid);
         IFurniturePacketManager.Companion.removeLightLocations(uuid);
         IFurniturePacketManager.Companion.getLightSendPacketMap().remove(uuid);
         IFurniturePacketManager.Companion.getLightRemovePacketMap().remove(uuid);
         IFurniturePacketManager.Companion.getTextEntityPacketMap().remove(uuid);
         IFurniturePacketManager.Companion.getTextEntityIdMap().remove(uuid);
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public final void onTeleportFurniture(@NotNull EntityTeleportEvent entityTeleportEvent) {
      Entity entity = entityTeleportEvent.getEntity();
      ItemDisplay itemDisplay = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
      if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
         ItemDisplay baseEntity = itemDisplay;
         FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
         if (furnitureMechanic == null) {
            furnitureMechanic = FurnitureMechanic.Companion.invalidFurniture(baseEntity);
            if (furnitureMechanic == null) {
               return;
            }
         }

         FurnitureMechanic mechanic = furnitureMechanic;
         mechanic.getHitbox().refreshHitboxes(baseEntity, mechanic);
         mechanic.getLight().refreshLights(baseEntity, mechanic);
         mechanic.getTextEntities().refreshTextEntities(baseEntity, mechanic);
         FurnitureSeat.Companion.updateSeats(baseEntity, mechanic);
         FurnitureBed.Companion.updateBeds(baseEntity, mechanic);
      }
   }

   @EventHandler
   public final void onFurnitureFactory(@NotNull NexoItemsLoadedEvent nexoItemsLoadedEvent) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if ((FurnitureFactory)mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager = iFurniturePacketManager;
            SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
            Function1 task = (a0_ -> FurniturePacketListener.onFurnitureFactory$lambda$0(packetManager, (ItemDisplay)a0_));
            List worlds = Bukkit.getWorlds();
            Iterable iterable = (Iterable)worlds;

            for(Object element : iterable) {
               World world = (World)element;
               int value2 = 0;
               if (VersionUtil.INSTANCE.isFoliaServer()) {
                  Chunk[] loadedChunks = world.getLoadedChunks();
                  Object[] objs = loadedChunks;
                  int value3 = 0;

                  for(int i = objs.length; value3 < i; ++value3) {
                     Object obj = objs[value3];
                     Chunk chunk = (Chunk)obj;
                     int value4 = 0;
                     SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocation(new Location(world, (double)chunk.getX() * (double)16.0F, (double)100.0F, (double)chunk.getZ() * (double)16.0F), new FurniturePacketListener$onFurnitureFactory$$inlined$runAtWorldEntities$1(chunk, task));
                  }
               } else {
                  Collection entitiesByClass = world.getEntitiesByClass(ItemDisplay.class);
                  Iterable iterable2 = (Iterable)entitiesByClass;

                  for(Object obj2 : iterable2) {
                     Entity p0 = (Entity)obj2;
                     int value5 = 0;
                     task.invoke(p0);
                  }
               }
            }

            return;
         }
      }

   }

   @EventHandler
   public final void onUseUnknownEntity(@NotNull PlayerUseUnknownEntityEvent playerUseUnknownEntityEvent) {
      ItemStack itemStack2 = FurniturePacketListener.WhenMappings.$EnumSwitchMapping$0[playerUseUnknownEntityEvent.getHand().ordinal()] == 1 ? playerUseUnknownEntityEvent.getPlayer().getInventory().getItemInMainHand() : playerUseUnknownEntityEvent.getPlayer().getInventory().getItemInOffHand();
      ItemStack itemStack = itemStack2;
      ItemDisplay itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(playerUseUnknownEntityEvent.getEntityId());
      if (itemDisplay != null) {
         ItemDisplay baseEntity = itemDisplay;
         Vector clickedRelativePosition = playerUseUnknownEntityEvent.getClickedRelativePosition();
         if (clickedRelativePosition == null) {
            clickedRelativePosition = new Vector();
         }

         Vector vector = clickedRelativePosition;
         int value = 0;
         clickedRelativePosition = playerUseUnknownEntityEvent.isAttack() || playerUseUnknownEntityEvent.getClickedRelativePosition() != null ? vector : null;
         if (clickedRelativePosition != null) {
            Vector relativePos = clickedRelativePosition;
            IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
            int entityId = playerUseUnknownEntityEvent.getEntityId();
            World world = baseEntity.getWorld();
            Location location = companion.hitboxLocFromId(entityId, world);
            if (location != null) {
               location = location.add(relativePos);
               if (location != null) {
                  Location interactionPoint = location;
                  FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
                  if (furnitureMechanic == null) {
                     furnitureMechanic = FurnitureMechanic.Companion.invalidFurniture(baseEntity);
                     if (furnitureMechanic == null) {
                        return;
                     }
                  }

                  FurnitureMechanic mechanic = furnitureMechanic;
                  if (playerUseUnknownEntityEvent.isAttack() && playerUseUnknownEntityEvent.getPlayer().getGameMode() != GameMode.ADVENTURE) {
                     Player player = playerUseUnknownEntityEvent.getPlayer();
                     Location location2 = baseEntity.getLocation();
                     if (ProtectionLib.canBreak(player, location2)) {
                        EventUtils.INSTANCE.call((Event)(new BlockBreakEvent(baseEntity.getLocation().getBlock(), playerUseUnknownEntityEvent.getPlayer())), (a0_ -> FurniturePacketListener.onUseUnknownEntity$lambda$1(mechanic, baseEntity, (BlockBreakEvent)a0_)));
                        return;
                     }
                  }

                  if (!playerUseUnknownEntityEvent.isAttack() && playerUseUnknownEntityEvent.getClickedRelativePosition() != null) {
                     Player player2 = playerUseUnknownEntityEvent.getPlayer();
                     EquipmentSlot hand = playerUseUnknownEntityEvent.getHand();
                     NexoFurnitureInteractEvent interactEvent = new NexoFurnitureInteractEvent(mechanic, baseEntity, player2, itemStack, hand, interactionPoint, org.bukkit.event.Event.Result.DEFAULT, org.bukkit.event.Event.Result.DEFAULT, null);
                     Player player3 = playerUseUnknownEntityEvent.getPlayer();
                     Location location3 = baseEntity.getLocation();
                     if (!ProtectionLib.canInteract(player3, location3)) {
                        interactEvent.setUseFurniture(Result.DENY);
                     }

                     EventUtils.INSTANCE.call(interactEvent);
                  }

                  return;
               }
            }

         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public final void onPlayerInteractBarrierHitbox(@NotNull PlayerInteractEvent playerInteractEvent) {
      boolean flag;
      IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock == null) {
         Location location = playerInteractEvent.getInteractionPoint();
         clickedBlock = location != null ? location.getBlock() : null;
      }

      if (clickedBlock != null) {
         Block block = clickedBlock;
         IFurniturePacketManager.Companion companion2 = companion;
         int value = 0;
         boolean empty = block.isEmpty();
         clickedBlock = empty ? block : null;
         if ((empty ? block : null) != null) {
            if (!companion2.blockIsHitbox(clickedBlock, (UUID)null, true)) {
               return;
            }

            FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic(playerInteractEvent.getClickedBlock());
            if (furnitureMechanic == null) {
               furnitureMechanic = NexoFurniture.furnitureMechanic(playerInteractEvent.getInteractionPoint());
               if (furnitureMechanic == null) {
                  return;
               }
            }

            FurnitureMechanic mechanic = furnitureMechanic;
            ItemDisplay itemDisplay = FurnitureMechanic.Companion.baseEntity(playerInteractEvent.getClickedBlock());
            if (itemDisplay == null) {
               itemDisplay = FurnitureMechanic.Companion.baseEntity(playerInteractEvent.getInteractionPoint());
               if (itemDisplay == null) {
                  return;
               }
            }

            ItemDisplay baseEntity = itemDisplay;
            Location location2 = playerInteractEvent.getInteractionPoint();
            if (location2 == null) {
               block: {
                  Block block2 = playerInteractEvent.getClickedBlock();
                  if (block2 != null) {
                     Location location3 = block2.getLocation();
                     if (location3 != null) {
                        location2 = location3.toCenterLocation();
                        break block;
                     }
                  }

                  location2 = null;
               }
            }

            Location interactionPoint = location2;
            value = FurniturePacketListener.WhenMappings.$EnumSwitchMapping$1[playerInteractEvent.getAction().ordinal()];
            if (value == 1) {
               Player player = playerInteractEvent.getPlayer();
               Location location4 = baseEntity.getLocation();
               if (!ProtectionLib.canBuild(player, location4)) {
                  playerInteractEvent.setUseItemInHand(Result.DENY);
               }

               block: {
                  if (playerInteractEvent.getItem() != null && !NexoFurniture.isFurniture(playerInteractEvent.getItem())) {
                     ItemStack item = playerInteractEvent.getItem();
                     Material it = item.getType();
                     int value2 = 0;
                     if (it.isBlock() && it != Material.LILY_PAD && it != Material.FROGSPAWN) {
                        flag = true;
                        break block;
                     }
                  }

                  flag = false;
               }

               boolean validBlockItem = flag;
               if (playerInteractEvent.useItemInHand() != Result.DENY && validBlockItem && (!mechanic.isInteractable(playerInteractEvent.getPlayer(), playerInteractEvent.getClickedBlock()) || playerInteractEvent.getPlayer().isSneaking())) {
                  playerInteractEvent.setUseItemInHand(Result.DENY);
                  playerInteractEvent.setUseInteractedBlock(Result.DENY);
                  Block block3 = playerInteractEvent.getClickedBlock();
                  if (block3 != null) {
                     block3.setType(Material.BARRIER);
                  }

                  CustomBlockHelpers customBlockHelpers = CustomBlockHelpers.INSTANCE;
                  Player player2 = playerInteractEvent.getPlayer();
                  EquipmentSlot hand = playerInteractEvent.getHand();
                  ItemStack itemStack = playerInteractEvent.getItem();
                  Block block4 = playerInteractEvent.getClickedBlock();
                  BlockFace blockFace = playerInteractEvent.getBlockFace();
                  customBlockHelpers.makePlayerPlaceBlock(player2, hand, itemStack, block4, blockFace, (CustomBlockMechanic)null, (BlockData)null);
                  Block block5 = playerInteractEvent.getClickedBlock();
                  if (block5 != null) {
                     block5.setType(Material.AIR);
                  }
               }

               player = playerInteractEvent.getPlayer();
               location4 = baseEntity.getLocation();
               if (!ProtectionLib.canInteract(player, location4)) {
                  playerInteractEvent.setUseInteractedBlock(Result.DENY);
               }

               Player player3 = playerInteractEvent.getPlayer();
               ItemStack itemStack2 = playerInteractEvent.getItem();
               EquipmentSlot equipmentSlot = playerInteractEvent.getHand();
               Event.Result useInteractedBlock = playerInteractEvent.useInteractedBlock();
               Event.Result useItemInHand = playerInteractEvent.useItemInHand();
               NexoFurnitureInteractEvent interactEvent = new NexoFurnitureInteractEvent(mechanic, baseEntity, player3, itemStack2, equipmentSlot, interactionPoint, useInteractedBlock, useItemInHand, playerInteractEvent.getBlockFace());
               Event.Result result = playerInteractEvent.useInteractedBlock();
               interactEvent.setUseFurniture(result);
               EventUtils.INSTANCE.call(interactEvent, (a0_ -> FurniturePacketListener.onPlayerInteractBarrierHitbox$lambda$2(playerInteractEvent, (NexoFurnitureInteractEvent)a0_)));
            } else if (value == 2) {
               Player player4 = playerInteractEvent.getPlayer();
               Location location5 = baseEntity.getLocation();
               if (ProtectionLib.canBreak(player4, location5) && (mechanic.getBreakable().getHardness() == (double)0.0F || playerInteractEvent.getPlayer().getGameMode() == GameMode.CREATIVE)) {
                  EventUtils.INSTANCE.call((Event)(new BlockBreakEvent(baseEntity.getLocation().getBlock(), playerInteractEvent.getPlayer())), (a0_ -> FurniturePacketListener.onPlayerInteractBarrierHitbox$lambda$3(mechanic, baseEntity, (BlockBreakEvent)a0_)));
               }
            }

            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPlaceInBarrier(@NotNull BlockPlaceEvent blockPlaceEvent) {
      IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
      Block blockPlaced = blockPlaceEvent.getBlockPlaced();
      if (companion.blockIsHitbox(blockPlaced, (UUID)null, true)) {
         blockPlaceEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPlaceInLight(@NotNull BlockPlaceEvent blockPlaceEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
      Location location = blockPlaceEvent.getBlock().getLocation();
      BlockLocation blockLocation = new BlockLocation(location);
      World world = blockPlaceEvent.getBlock().getWorld();
      ItemDisplay itemDisplay = companion.baseEntityFromLight(blockLocation, world);
      if (itemDisplay != null) {
         ItemDisplay baseEntity = itemDisplay;
         FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
         if (furnitureMechanic != null) {
            FurnitureMechanic mechanic = furnitureMechanic;
            UUID uniqueId = baseEntity.getUniqueId();
            UUID baseUuid = uniqueId;
            IFurniturePacketManager.Companion.getLightSendPacketMap().remove(baseUuid);
            IFurniturePacketManager.Companion.getLightRemovePacketMap().remove(baseUuid);
            MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
            Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

            while(true) {
               if (iterator.hasNext()) {
                  MechanicFactory it = (MechanicFactory)iterator.next();
                  int value = 0;
                  MechanicFactory mechanicFactory2 = it;
                  if (!(it instanceof FurnitureFactory)) {
                     mechanicFactory2 = null;
                  }

                  it = (FurnitureFactory)mechanicFactory2;
                  if (it == null) {
                     continue;
                  }

                  mechanicFactory = it;
                  break;
               }

               mechanicFactory = null;
               break;
            }

            FurnitureFactory furnitureFactory = (FurnitureFactory)mechanicFactory;
            if (furnitureFactory != null) {
               IFurniturePacketManager packetManager = furnitureFactory.packetManager();
               if (packetManager != null) {
                  packetManager.sendLightMechanicPacket(baseEntity, mechanic);
               }
            }

         }
      }
   }

   private static final Unit onPlayerTrackFurniture$lambda$0(IFurniturePacketManager packetManager, ItemDisplay itemDisplay, FurnitureMechanic mechanic, PlayerTrackEntityEvent playerTrackEntityEvent, Entity it) {
      Player player = playerTrackEntityEvent.getPlayer();
      packetManager.sendFurnitureMetadataPacket(itemDisplay, mechanic, player);
      player = playerTrackEntityEvent.getPlayer();
      packetManager.sendHitboxEntityPacket(itemDisplay, mechanic, player);
      player = playerTrackEntityEvent.getPlayer();
      packetManager.sendBarrierHitboxPacket(itemDisplay, mechanic, player);
      player = playerTrackEntityEvent.getPlayer();
      packetManager.sendLightMechanicPacket(itemDisplay, mechanic, player);
      player = playerTrackEntityEvent.getPlayer();
      packetManager.sendTextEntityMechanicPacket(itemDisplay, mechanic, player);
      return Unit.INSTANCE;
   }

   private static final Unit onLoad$lambda$2(ItemDisplay itemDisplay, Entity it) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory mechanicFactory2 = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = mechanicFactory2;
            if (!(mechanicFactory2 instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            mechanicFactory2 = (FurnitureFactory)mechanicFactory;
            if (mechanicFactory2 == null) {
               continue;
            }

            mechanicFactory = mechanicFactory2;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager = iFurniturePacketManager;
            FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)itemDisplay);
            if (furnitureMechanic == null) {
               furnitureMechanic = FurnitureMechanic.Companion.invalidFurniture(itemDisplay);
               if (furnitureMechanic == null) {
                  return Unit.INSTANCE;
               }
            }

            FurnitureMechanic mechanic = furnitureMechanic;
            packetManager.sendFurnitureMetadataPacket(itemDisplay, mechanic);
            packetManager.sendHitboxEntityPacket(itemDisplay, mechanic);
            packetManager.sendBarrierHitboxPacket(itemDisplay, mechanic);
            packetManager.sendLightMechanicPacket(itemDisplay, mechanic);
            packetManager.sendTextEntityMechanicPacket(itemDisplay, mechanic);
            return Unit.INSTANCE;
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit onUnload$lambda$1(ItemDisplay itemDisplay, Entity it) {
      FurnitureBed.Companion.removeBeds(itemDisplay);
      return Unit.INSTANCE;
   }

   private static final Unit onFurnitureFactory$lambda$0(IFurniturePacketManager packetManager, ItemDisplay entity) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)entity);
      if (furnitureMechanic == null) {
         furnitureMechanic = FurnitureMechanic.Companion.invalidFurniture(entity);
         if (furnitureMechanic == null) {
            return Unit.INSTANCE;
         }
      }

      FurnitureMechanic mechanic = furnitureMechanic;
      if (!FurnitureSeat.Companion.isSeat((Entity)entity) && !FurnitureBed.Companion.isBed((Entity)entity)) {
         packetManager.sendFurnitureMetadataPacket(entity, mechanic);
         packetManager.sendHitboxEntityPacket(entity, mechanic);
         packetManager.sendBarrierHitboxPacket(entity, mechanic);
         packetManager.sendLightMechanicPacket(entity, mechanic);
         IFurniturePacketManager.Companion.getLightRemovePacketMap().remove(entity.getUniqueId());
         packetManager.sendTextEntityMechanicPacket(entity, mechanic);
         return Unit.INSTANCE;
      } else {
         return Unit.INSTANCE;
      }
   }

   private static final Unit onUseUnknownEntity$lambda$1$0(ItemDisplay baseEntity, NexoFurnitureBreakEvent nexoFurnitureBreakEvent) {
      NexoFurniture.remove((Entity)baseEntity, nexoFurnitureBreakEvent.getPlayer(), nexoFurnitureBreakEvent.getDrop());
      return Unit.INSTANCE;
   }

   private static final Unit onUseUnknownEntity$lambda$1(FurnitureMechanic mechanic, ItemDisplay baseEntity, BlockBreakEvent blockBreakEvent) {
      EventUtils eventUtils = EventUtils.INSTANCE;
      Player player = blockBreakEvent.getPlayer();
      eventUtils.call(new NexoFurnitureBreakEvent(mechanic, baseEntity, player), (a0_ -> FurniturePacketListener.onUseUnknownEntity$lambda$1$0(baseEntity, (NexoFurnitureBreakEvent)a0_)));
      return Unit.INSTANCE;
   }

   private static final Unit onPlayerInteractBarrierHitbox$lambda$2(PlayerInteractEvent playerInteractEvent, NexoFurnitureInteractEvent nexoFurnitureInteractEvent) {
      playerInteractEvent.setUseInteractedBlock(nexoFurnitureInteractEvent.getUseFurniture());
      playerInteractEvent.setUseItemInHand(nexoFurnitureInteractEvent.getUseItemInHand());
      return Unit.INSTANCE;
   }

   private static final Unit onPlayerInteractBarrierHitbox$lambda$3$0(ItemDisplay baseEntity, NexoFurnitureBreakEvent nexoFurnitureBreakEvent) {
      NexoFurniture.remove((Entity)baseEntity, nexoFurnitureBreakEvent.getPlayer(), nexoFurnitureBreakEvent.getDrop());
      return Unit.INSTANCE;
   }

   private static final Unit onPlayerInteractBarrierHitbox$lambda$3(FurnitureMechanic mechanic, ItemDisplay baseEntity, BlockBreakEvent blockBreakEvent) {
      EventUtils eventUtils = EventUtils.INSTANCE;
      Player player = blockBreakEvent.getPlayer();
      eventUtils.call(new NexoFurnitureBreakEvent(mechanic, baseEntity, player), (a0_ -> FurniturePacketListener.onPlayerInteractBarrierHitbox$lambda$3$0(baseEntity, (NexoFurnitureBreakEvent)a0_)));
      return Unit.INSTANCE;
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      public static final int[] $EnumSwitchMapping$1;
      static {
         int[] a_0 = new int[org.bukkit.inventory.EquipmentSlot.values().length];
         try { a_0[org.bukkit.inventory.EquipmentSlot.HAND.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
         int[] a_1 = new int[org.bukkit.event.block.Action.values().length];
         try { a_1[org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_1[org.bukkit.event.block.Action.LEFT_CLICK_BLOCK.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$1 = a_1;
      }
   }
}
