package com.nexomc.nexo.mechanics.furniture.door;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.mechanics.furniture.FurnitureBaseEntity;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.VersionUtil;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007J\u0010\u0010\b\u001a\u00020\u0005*\u00020\tH\u0007b\u0002\b\u0007J\u0010\u0010\n\u001a\u00020\u0005*\u00020\u000bH\u0007b\u0002\b\u0007J&\u0010\f\u001a\u00020\u0005*\u00020\rH\u0007b\u0018\b\u0007\u0012\n\b\u000e\u0012\u0006\b\n0\u000f8\u0010\u0012\b\b\u0011\u0012\u0004\b\u0007\u0010\u0002¨\u0006\u0012"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/door/FurnitureDoorListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onTrackDoor", "", "Lio/papermc/paper/event/player/PlayerTrackEntityEvent;", "Lorg/bukkit/event/EventHandler;", "onLoad", "Lcom/destroystokyo/paper/event/entity/EntityAddToWorldEvent;", "onFurnitureFactory", "Lcom/nexomc/nexo/api/events/NexoItemsLoadedEvent;", "onDoorOpen", "Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureInteractEvent;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGH", "ignoreCancelled", "com.nexomc:core"}
)
public final class FurnitureDoorListener implements Listener {
   @EventHandler
   public final void onTrackDoor(@NotNull PlayerTrackEntityEvent playerTrackEntityEvent) {
      Entity p0 = playerTrackEntityEvent.getEntity();
      int value = 0;
      Entity entity = p0.isValid() ? p0 : null;
      ItemDisplay itemDisplay = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
      if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
         ItemDisplay baseEntity = itemDisplay;
         FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
         if (furnitureMechanic != null) {
            FurnitureMechanic mechanic = furnitureMechanic;
            SchedulerUtils.INSTANCE.runAtEntityLater((Entity)baseEntity, 6L, (a0_ -> FurnitureDoorListener.onTrackDoor$lambda$0(mechanic, baseEntity, playerTrackEntityEvent, (Entity)a0_)));
         }
      }
   }

   @EventHandler
   public final void onLoad(@NotNull EntityAddToWorldEvent entityAddToWorldEvent) {
      Entity entity = entityAddToWorldEvent.getEntity();
      ItemDisplay itemDisplay = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
      if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
         ItemDisplay baseEntity = itemDisplay;
         Map furnitureBaseMap = IFurniturePacketManager.Companion.getFurnitureBaseMap();
         UUID uniqueId = baseEntity.getUniqueId();
         FurnitureBaseEntity furnitureBaseEntity = (FurnitureBaseEntity)IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
         if (furnitureBaseEntity != null) {
            FurnitureBaseEntity it = furnitureBaseEntity;
            int value = 0;
            furnitureBaseEntity = it.getBaseId() != baseEntity.getEntityId() ? it : null;
         } else {
            furnitureBaseEntity = null;
         }

         FurnitureBaseEntity furnitureBaseEntity2 = furnitureBaseEntity;
         TypeIntrinsics.asMutableMap(furnitureBaseMap).remove(uniqueId, furnitureBaseEntity2);
         SchedulerUtils.INSTANCE.runAtEntityLater((Entity)baseEntity, 4L, (a0_ -> FurnitureDoorListener.onLoad$lambda$1(baseEntity, (Entity)a0_)));
      }
   }

   @EventHandler
   public final void onFurnitureFactory(@NotNull NexoItemsLoadedEvent nexoItemsLoadedEvent) {
      SchedulerUtils.INSTANCE.runTaskLater(2L, FurnitureDoorListener::onFurnitureFactory$lambda$0);
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public final void onDoorOpen(@NotNull NexoFurnitureInteractEvent nexoFurnitureInteractEvent) {
      if (nexoFurnitureInteractEvent.getUseFurniture() != Result.DENY && nexoFurnitureInteractEvent.getHand() == EquipmentSlot.HAND) {
         FurnitureDoor door = nexoFurnitureInteractEvent.getMechanic().getDoor();
         if (door != null) {
            door.toggleState(nexoFurnitureInteractEvent.getBaseEntity(), nexoFurnitureInteractEvent.getMechanic());
         }
      }

   }

   private static final Unit onTrackDoor$lambda$0(FurnitureMechanic mechanic, ItemDisplay baseEntity, PlayerTrackEntityEvent playerTrackEntityEvent, Entity it) {
      FurnitureDoor door = mechanic.getDoor();
      if (door != null) {
         door.ensureHitboxState(baseEntity, mechanic, playerTrackEntityEvent.getPlayer());
      }

      return Unit.INSTANCE;
   }

   private static final Unit onLoad$lambda$1(ItemDisplay baseEntity, Entity it) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
      if (furnitureMechanic == null) {
         return Unit.INSTANCE;
      } else {
         FurnitureMechanic mechanic = furnitureMechanic;
         FurnitureDoor door = mechanic.getDoor();
         if (door != null) {
            door.ensureHitboxState(baseEntity, mechanic, (Player)null);
         }

         return Unit.INSTANCE;
      }
   }

   private static final Unit onFurnitureFactory$lambda$0$0(ItemDisplay baseEntity) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
      if (furnitureMechanic == null) {
         return Unit.INSTANCE;
      } else {
         FurnitureMechanic mechanic = furnitureMechanic;
         FurnitureDoor door = mechanic.getDoor();
         if (door != null) {
            door.ensureHitboxState(baseEntity, mechanic, (Player)null);
         }

         return Unit.INSTANCE;
      }
   }

   private static final Unit onFurnitureFactory$lambda$0() {
      SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
      Function1 task = (a0_ -> FurnitureDoorListener.onFurnitureFactory$lambda$0$0((ItemDisplay)a0_));
      List worlds = Bukkit.getWorlds();
      Iterable iterable = (Iterable)worlds;

      for(Object element : iterable) {
         World world = (World)element;
         int value = 0;
         if (VersionUtil.INSTANCE.isFoliaServer()) {
            Chunk[] loadedChunks = world.getLoadedChunks();
            Object[] objs = loadedChunks;
            int value2 = 0;

            for(int i = objs.length; value2 < i; ++value2) {
               Object obj = objs[value2];
               Chunk chunk = (Chunk)obj;
               int value3 = 0;
               SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocation(new Location(world, (double)chunk.getX() * (double)16.0F, (double)100.0F, (double)chunk.getZ() * (double)16.0F), new FurnitureDoorListener$onFurnitureFactory$lambda$0$$inlined$runAtWorldEntities$1(chunk, task));
            }
         } else {
            Collection entitiesByClass = world.getEntitiesByClass(ItemDisplay.class);
            Iterable iterable2 = (Iterable)entitiesByClass;

            for(Object obj2 : iterable2) {
               Entity p0 = (Entity)obj2;
               int value4 = 0;
               task.invoke(p0);
            }
         }
      }

      return Unit.INSTANCE;
   }
}
