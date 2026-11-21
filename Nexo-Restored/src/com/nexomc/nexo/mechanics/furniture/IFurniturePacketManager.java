package com.nexomc.nexo.mechanics.furniture;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.furniture.hitbox.BarrierHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.HitboxMap;
import com.nexomc.nexo.mechanics.furniture.text_entity.FurnitureTextEntity;
import com.nexomc.nexo.mechanics.light.LightBlock;
import com.nexomc.nexo.utils.FurnitureHelpersKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.VectorUtilsKt;
import com.nexomc.nexo.utils.VersionUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\bf\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aJ\u0012\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\f\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\r\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J \u0010\r\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\u0010\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH&J \u0010\u0010\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH&J\u0018\u0010\u0011\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH&J \u0010\u0011\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH&J\u0018\u0010\u0012\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH&J \u0010\u0012\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH&J\u0018\u0010\u0013\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH&J \u0010\u0013\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH&J\u0018\u0010\u0014\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J \u0010\u0014\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\u0015\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J \u0010\u0015\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\u0016\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J \u0010\u0016\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\u0017\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J \u0010\u0017\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\b\u0010\u0018\u001a\u00020\u0007H\u0016J\u0012\u0010\u0019\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016¨\u0006\u001bÀ\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacketManager;", "", "getEntity", "Lorg/bukkit/entity/Entity;", "entityId", "", "hideFurniture", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "player", "Lorg/bukkit/entity/Player;", "showFurniture", "sendFurnitureMetadataPacket", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "sendHitboxEntityPacket", "removeHitboxEntityPacket", "sendBarrierHitboxPacket", "removeBarrierHitboxPacket", "sendLightMechanicPacket", "removeLightMechanicPacket", "sendTextEntityMechanicPacket", "removeTextEntityMechanicPacket", "removeAllFurniturePackets", "findTargetFurnitureHitbox", "Companion", "com.nexomc:core"}
)
public interface IFurniturePacketManager {
   @NotNull
   Companion Companion = IFurniturePacketManager.Companion.$$INSTANCE;

   @Nullable
   Entity getEntity(int value);

   default void hideFurniture(@NotNull ItemDisplay baseEntity, @NotNull Player player) {
   }

   default void showFurniture(@NotNull ItemDisplay baseEntity, @NotNull Player player) {
   }

   default void sendFurnitureMetadataPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
   }

   default void sendFurnitureMetadataPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
   }

   void sendHitboxEntityPacket(@NotNull ItemDisplay itemDisplay, @NotNull FurnitureMechanic furnitureMechanic);

   void sendHitboxEntityPacket(@NotNull ItemDisplay itemDisplay, @NotNull FurnitureMechanic furnitureMechanic, @NotNull Player player);

   void removeHitboxEntityPacket(@NotNull ItemDisplay itemDisplay, @NotNull FurnitureMechanic furnitureMechanic);

   void removeHitboxEntityPacket(@NotNull ItemDisplay itemDisplay, @NotNull FurnitureMechanic furnitureMechanic, @NotNull Player player);

   void sendBarrierHitboxPacket(@NotNull ItemDisplay itemDisplay, @NotNull FurnitureMechanic furnitureMechanic);

   void sendBarrierHitboxPacket(@NotNull ItemDisplay itemDisplay, @NotNull FurnitureMechanic furnitureMechanic, @NotNull Player player);

   void removeBarrierHitboxPacket(@NotNull ItemDisplay itemDisplay, @NotNull FurnitureMechanic furnitureMechanic);

   void removeBarrierHitboxPacket(@NotNull ItemDisplay itemDisplay, @NotNull FurnitureMechanic furnitureMechanic, @NotNull Player player);

   default void sendLightMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
   }

   default void sendLightMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
   }

   default void removeLightMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
   }

   default void removeLightMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
   }

   default void sendTextEntityMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
   }

   default void sendTextEntityMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
   }

   default void removeTextEntityMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
   }

   default void removeTextEntityMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
   }

   default void removeAllFurniturePackets() {
      SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
      Function1 task = (Function1<ItemDisplay, Unit>)(entity -> removeAllFurniturePackets$lambda$0(this, entity));
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
               SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocation(new Location(world, (double)chunk.getX() * (double)16.0F, (double)100.0F, (double)chunk.getZ() * (double)16.0F), new IFurniturePacketManager$removeAllFurniturePackets$$inlined$runAtWorldEntities$1(chunk, task));
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

   }

   @Nullable
   default ItemDisplay findTargetFurnitureHitbox(@NotNull Player player) {
      return null;
   }

   private static Unit removeAllFurniturePackets$lambda$0(IFurniturePacketManager iFurniturePacketManager, ItemDisplay entity) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)entity);
      if (furnitureMechanic == null) {
         return Unit.INSTANCE;
      } else {
         FurnitureMechanic mechanic = furnitureMechanic;
         iFurniturePacketManager.removeHitboxEntityPacket(entity, mechanic);
         iFurniturePacketManager.removeBarrierHitboxPacket(entity, mechanic);
         iFurniturePacketManager.removeLightMechanicPacket(entity, mechanic);
         iFurniturePacketManager.removeTextEntityMechanicPacket(entity, mechanic);
         return Unit.INSTANCE;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      public static void hideFurniture(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull Player player) {
         iFurniturePacketManager.hideFurniture(baseEntity, player);
      }

      /** @deprecated */
      @Deprecated
      public static void showFurniture(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull Player player) {
         iFurniturePacketManager.showFurniture(baseEntity, player);
      }

      /** @deprecated */
      @Deprecated
      public static void sendFurnitureMetadataPacket(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         iFurniturePacketManager.sendFurnitureMetadataPacket(baseEntity, mechanic);
      }

      /** @deprecated */
      @Deprecated
      public static void sendFurnitureMetadataPacket(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
         iFurniturePacketManager.sendFurnitureMetadataPacket(baseEntity, mechanic, player);
      }

      /** @deprecated */
      @Deprecated
      public static void sendLightMechanicPacket(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         iFurniturePacketManager.sendLightMechanicPacket(baseEntity, mechanic);
      }

      /** @deprecated */
      @Deprecated
      public static void sendLightMechanicPacket(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
         iFurniturePacketManager.sendLightMechanicPacket(baseEntity, mechanic, player);
      }

      /** @deprecated */
      @Deprecated
      public static void removeLightMechanicPacket(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         iFurniturePacketManager.removeLightMechanicPacket(baseEntity, mechanic);
      }

      /** @deprecated */
      @Deprecated
      public static void removeLightMechanicPacket(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
         iFurniturePacketManager.removeLightMechanicPacket(baseEntity, mechanic, player);
      }

      /** @deprecated */
      @Deprecated
      public static void sendTextEntityMechanicPacket(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         iFurniturePacketManager.sendTextEntityMechanicPacket(baseEntity, mechanic);
      }

      /** @deprecated */
      @Deprecated
      public static void sendTextEntityMechanicPacket(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
         iFurniturePacketManager.sendTextEntityMechanicPacket(baseEntity, mechanic, player);
      }

      /** @deprecated */
      @Deprecated
      public static void removeTextEntityMechanicPacket(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         iFurniturePacketManager.removeTextEntityMechanicPacket(baseEntity, mechanic);
      }

      /** @deprecated */
      @Deprecated
      public static void removeTextEntityMechanicPacket(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
         iFurniturePacketManager.removeTextEntityMechanicPacket(baseEntity, mechanic, player);
      }

      /** @deprecated */
      @Deprecated
      public static void removeAllFurniturePackets(@NotNull IFurniturePacketManager iFurniturePacketManager) {
         iFurniturePacketManager.removeAllFurniturePackets();
      }

      /** @deprecated */
      @Deprecated
      @Nullable
      public static ItemDisplay findTargetFurnitureHitbox(@NotNull IFurniturePacketManager iFurniturePacketManager, @NotNull Player player) {
         return iFurniturePacketManager.findTargetFurnitureHitbox(player);
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000¸\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010%\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\u0010#\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u001d\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010I\u001a\u00020J2\u0006\u0010K\u001a\u00020L2\u0006\u0010M\u001a\u00020NJ\u0010\u0010O\u001a\u0004\u0018\u00010L2\u0006\u0010P\u001a\u00020\u0013J\u0010\u0010O\u001a\u0004\u0018\u00010L2\u0006\u0010Q\u001a\u00020\u001fJ\u0018\u0010O\u001a\u0004\u0018\u00010L2\u0006\u0010Q\u001a\u00020R2\u0006\u0010S\u001a\u00020TJ\u0018\u0010U\u001a\u0004\u0018\u00010L2\u0006\u0010Q\u001a\u00020R2\u0006\u0010S\u001a\u00020TJ\u0010\u0010V\u001a\u0004\u0018\u00010N2\u0006\u0010P\u001a\u00020\u0013J\u0010\u0010V\u001a\u0004\u0018\u00010N2\u0006\u0010Q\u001a\u00020RJ\u0018\u0010W\u001a\u0004\u0018\u00010\u001f2\u0006\u0010P\u001a\u00020\u00132\u0006\u0010S\u001a\u00020TJ\u000e\u0010X\u001a\u00020Y2\u0006\u0010Z\u001a\u00020[J\"\u0010\\\u001a\u00020Y2\u0006\u0010]\u001a\u00020^2\u0006\u0010S\u001a\u00020T2\n\b\u0002\u0010_\u001a\u0004\u0018\u00010\u0014J$\u0010\\\u001a\u00020Y2\u0006\u0010`\u001a\u00020a2\n\b\u0002\u0010_\u001a\u0004\u0018\u00010\u00142\b\b\u0002\u0010b\u001a\u00020YJ\u001a\u0010\\\u001a\u00020Y2\u0006\u0010Q\u001a\u00020\u001f2\n\b\u0002\u0010_\u001a\u0004\u0018\u00010\u0014J\"\u0010c\u001a\u00020Y2\u0006\u0010]\u001a\u00020^2\u0006\u0010S\u001a\u00020T2\n\b\u0002\u0010_\u001a\u0004\u0018\u00010\u0014J\u001e\u0010d\u001a\u00020,2\u0006\u0010e\u001a\u00020\u00132\u0006\u0010f\u001a\u00020\u00132\u0006\u0010g\u001a\u00020\u0013J\u0016\u0010h\u001a\u00020,2\u0006\u0010e\u001a\u00020\u00132\u0006\u0010g\u001a\u00020\u0013J)\u0010i\u001a\u00020J2\u0006\u0010j\u001a\u00020\u00142\u0006\u0010S\u001a\u00020T2\f\u0010k\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001b¢\u0006\u0002\u0010lJ)\u0010m\u001a\u00020J2\u0006\u0010j\u001a\u00020\u00142\u0006\u0010S\u001a\u00020T2\f\u0010k\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001b¢\u0006\u0002\u0010lJ\u000e\u0010n\u001a\u00020J2\u0006\u0010j\u001a\u00020\u0014J\u000e\u0010o\u001a\u00020J2\u0006\u0010j\u001a\u00020\u0014J\u0091\u0001\u0010p\u001a\u00020J2\u0006\u0010j\u001a\u00020\u00142\u0006\u0010q\u001a\u00020\u00142\f\u0010k\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001b2\u0018\u0010r\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\u001b0\u00122$\u0010s\u001a \u0012\u0004\u0012\u00020\u0014\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020,\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140-0\u00120\u00122$\u0010t\u001a \u0012\u0004\u0012\u00020\u0014\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020,\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140-0\u00120\u0012H\u0002¢\u0006\u0002\u0010uJ\u009a\u0001\u0010v\u001a\u00020J\"\b\b\u0000\u0010w*\u00020R2\u0006\u0010j\u001a\u00020\u00142\u0018\u0010r\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\u001b0\u00122\u0018\u0010x\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u0002Hw0\u001b0\u00122$\u0010s\u001a \u0012\u0004\u0012\u00020\u0014\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020,\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140-0\u00120\u00122$\u0010t\u001a \u0012\u0004\u0012\u00020\u0014\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020,\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140-0\u00120\u0012H\u0002J\u001d\u0010y\u001a\u00020J2\u0006\u0010j\u001a\u00020\u00142\u0006\u0010z\u001a\u00020>H\u0000¢\u0006\u0002\b{J\u001d\u0010|\u001a\u00020J2\u0006\u0010j\u001a\u00020\u00142\u0006\u0010z\u001a\u00020>H\u0000¢\u0006\u0002\b}R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0007R\u0016\u0010\n\u001a\u00070\u000b¢\u0006\u0002\b\f¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u000f\u001a\u00070\u000b¢\u0006\u0002\b\f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u001d\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00140\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u001d\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00180\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0016R#\u0010\u001a\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001c0\u001b0\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0016R#\u0010\u001e\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\u001b0\u0012¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u0016R#\u0010!\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\"0\u001b0\u0012¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0016R#\u0010$\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\u001b0\u0012¢\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0016R\u001d\u0010&\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020'0\u0012¢\u0006\b\n\u0000\u001a\u0004\b(\u0010\u0016R\u001d\u0010)\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020'0\u0012¢\u0006\b\n\u0000\u001a\u0004\b*\u0010\u0016R/\u0010+\u001a \u0012\u0004\u0012\u00020\u0014\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020,\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140-0\u00120\u0012¢\u0006\b\n\u0000\u001a\u0004\b.\u0010\u0016R/\u0010/\u001a \u0012\u0004\u0012\u00020\u0014\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020,\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140-0\u00120\u0012¢\u0006\b\n\u0000\u001a\u0004\b0\u0010\u0016R#\u00101\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020'0\u001b0\u0012¢\u0006\b\n\u0000\u001a\u0004\b2\u0010\u0016R#\u00103\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020'0\u001b0\u0012¢\u0006\b\n\u0000\u001a\u0004\b4\u0010\u0016R#\u00105\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020'0\u001b0\u0012¢\u0006\b\n\u0000\u001a\u0004\b6\u0010\u0016R/\u00107\u001a \u0012\u0004\u0012\u00020\u0014\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020,\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140-0\u00120\u0012¢\u0006\b\n\u0000\u001a\u0004\b8\u0010\u0016R/\u00109\u001a \u0012\u0004\u0012\u00020\u0014\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020,\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140-0\u00120\u0012¢\u0006\b\n\u0000\u001a\u0004\b:\u0010\u0016R/\u0010;\u001a \u0012\u0004\u0012\u00020\u0014\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020,\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140-0\u00120\u0012¢\u0006\b\n\u0000\u001a\u0004\b<\u0010\u0016R\u001d\u0010=\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020>0\u0012¢\u0006\b\n\u0000\u001a\u0004\b?\u0010\u0016R\u001d\u0010@\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020>0\u0012¢\u0006\b\n\u0000\u001a\u0004\bA\u0010\u0016R\u001d\u0010B\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020>0\u0012¢\u0006\b\n\u0000\u001a\u0004\bC\u0010\u0016R#\u0010D\u001a\u0014\u0012\u0004\u0012\u00020\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020'0\u001b0\u0012¢\u0006\b\n\u0000\u001a\u0004\bE\u0010\u0016R\u001d\u0010F\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020G0\u0012¢\u0006\b\n\u0000\u001a\u0004\bH\u0010\u0016¨\u0006~"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacketManager$Companion;", "", "<init>", "()V", "BARRIER_DATA", "Lorg/bukkit/block/data/Waterlogged;", "getBARRIER_DATA", "()Lorg/bukkit/block/data/Waterlogged;", "BARRIER_DATA_WATERLOGGED", "getBARRIER_DATA_WATERLOGGED", "AIR_DATA", "Lorg/bukkit/block/data/BlockData;", "Lorg/jetbrains/annotations/NotNull;", "getAIR_DATA", "()Lorg/bukkit/block/data/BlockData;", "WATER_DATA", "getWATER_DATA", "entityIdMap", "", "", "Ljava/util/UUID;", "getEntityIdMap", "()Ljava/util/Map;", "furnitureBaseMap", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureBaseEntity;", "getFurnitureBaseMap", "barrierHitboxPositionMap", "", "Lcom/nexomc/nexo/mechanics/furniture/hitbox/BarrierHitbox;", "getBarrierHitboxPositionMap", "barrierHitboxLocationMap", "Lorg/bukkit/Location;", "getBarrierHitboxLocationMap", "lightPositionMap", "Lcom/nexomc/nexo/mechanics/light/LightBlock;", "getLightPositionMap", "lightLocationMap", "getLightLocationMap", "lightSendPacketMap", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacket;", "getLightSendPacketMap", "lightRemovePacketMap", "getLightRemovePacketMap", "barrierChunkMap", "", "", "getBarrierChunkMap", "lightChunkMap", "getLightChunkMap", "interactionHitboxPacketMap", "getInteractionHitboxPacketMap", "shulkerHitboxPacketMap", "getShulkerHitboxPacketMap", "ghastHitboxPacketMap", "getGhastHitboxPacketMap", "barrierHitboxReverseMap", "getBarrierHitboxReverseMap", "lightReverseMap", "getLightReverseMap", "entityHitboxReverseMap", "getEntityHitboxReverseMap", "interactionHitboxIdMap", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureHitboxEntity;", "getInteractionHitboxIdMap", "shulkerHitboxIdMap", "getShulkerHitboxIdMap", "ghastHitboxIdMap", "getGhastHitboxIdMap", "textEntityPacketMap", "getTextEntityPacketMap", "textEntityIdMap", "Lcom/nexomc/nexo/mechanics/furniture/text_entity/FurnitureTextEntity;", "getTextEntityIdMap", "updateBaseEntity", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "baseEntityFromHitbox", "entityId", "location", "Lcom/nexomc/nexo/mechanics/furniture/BlockLocation;", "world", "Lorg/bukkit/World;", "baseEntityFromLight", "mechanicFromHitbox", "hitboxLocFromId", "standingOnFurniture", "", "player", "Lorg/bukkit/entity/Player;", "blockIsHitbox", "vec", "Lorg/bukkit/util/Vector;", "excludeUUID", "block", "Lorg/bukkit/block/Block;", "collisionOnly", "blockIsLight", "packBlockPos", "x", "y", "z", "packChunkPos", "storeBarrierHitboxLocations", "uuid", "locations", "(Ljava/util/UUID;Lorg/bukkit/World;[Lorg/bukkit/Location;)V", "storeLightLocations", "removeBarrierHitboxLocations", "removeLightLocations", "storeLocations", "worldUID", "locationMap", "reverseMap", "chunkMap", "(Ljava/util/UUID;Ljava/util/UUID;[Lorg/bukkit/Location;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;)V", "removeLocations", "T", "positionMap", "addHitboxToReverseMap", "hitboxEntity", "addHitboxToReverseMap$com_nexomc_core", "removeHitboxFromReverseMap", "removeHitboxFromReverseMap$com_nexomc_core", "com.nexomc:core"}
   )
   public static final class Companion {
      public static final Companion $$INSTANCE = new Companion();
      @NotNull
      private static final Waterlogged BARRIER_DATA;
      @NotNull
      private static final Waterlogged BARRIER_DATA_WATERLOGGED;
      @NotNull
      private static final BlockData AIR_DATA;
      @NotNull
      private static final BlockData WATER_DATA;
      @NotNull
      private static final Map<Integer, UUID> entityIdMap;
      @NotNull
      private static final Map<UUID, FurnitureBaseEntity> furnitureBaseMap;
      @NotNull
      private static final Map<UUID, BarrierHitbox[]> barrierHitboxPositionMap;
      @NotNull
      private static final Map<UUID, Location[]> barrierHitboxLocationMap;
      @NotNull
      private static final Map<UUID, LightBlock[]> lightPositionMap;
      @NotNull
      private static final Map<UUID, Location[]> lightLocationMap;
      @NotNull
      private static final Map<UUID, IFurniturePacket> lightSendPacketMap;
      @NotNull
      private static final Map<UUID, IFurniturePacket> lightRemovePacketMap;
      @NotNull
      private static final Map<UUID, Map<Long, Set<UUID>>> barrierChunkMap;
      @NotNull
      private static final Map<UUID, Map<Long, Set<UUID>>> lightChunkMap;
      @NotNull
      private static final Map<UUID, IFurniturePacket[]> interactionHitboxPacketMap;
      @NotNull
      private static final Map<UUID, IFurniturePacket[]> shulkerHitboxPacketMap;
      @NotNull
      private static final Map<UUID, IFurniturePacket[]> ghastHitboxPacketMap;
      @NotNull
      private static final Map<UUID, Map<Long, Set<UUID>>> barrierHitboxReverseMap;
      @NotNull
      private static final Map<UUID, Map<Long, Set<UUID>>> lightReverseMap;
      @NotNull
      private static final Map<UUID, Map<Long, Set<UUID>>> entityHitboxReverseMap;
      @NotNull
      private static final Map<UUID, FurnitureHitboxEntity> interactionHitboxIdMap;
      @NotNull
      private static final Map<UUID, FurnitureHitboxEntity> shulkerHitboxIdMap;
      @NotNull
      private static final Map<UUID, FurnitureHitboxEntity> ghastHitboxIdMap;
      @NotNull
      private static final Map<UUID, IFurniturePacket[]> textEntityPacketMap;
      @NotNull
      private static final Map<UUID, FurnitureTextEntity> textEntityIdMap;

      private Companion() {
      }

      @NotNull
      public final Waterlogged getBARRIER_DATA() {
         return BARRIER_DATA;
      }

      @NotNull
      public final Waterlogged getBARRIER_DATA_WATERLOGGED() {
         return BARRIER_DATA_WATERLOGGED;
      }

      @NotNull
      public final BlockData getAIR_DATA() {
         return AIR_DATA;
      }

      @NotNull
      public final BlockData getWATER_DATA() {
         return WATER_DATA;
      }

      @NotNull
      public final Map<Integer, UUID> getEntityIdMap() {
         return entityIdMap;
      }

      @NotNull
      public final Map<UUID, FurnitureBaseEntity> getFurnitureBaseMap() {
         return furnitureBaseMap;
      }

      @NotNull
      public final Map<UUID, BarrierHitbox[]> getBarrierHitboxPositionMap() {
         return barrierHitboxPositionMap;
      }

      @NotNull
      public final Map<UUID, Location[]> getBarrierHitboxLocationMap() {
         return barrierHitboxLocationMap;
      }

      @NotNull
      public final Map<UUID, LightBlock[]> getLightPositionMap() {
         return lightPositionMap;
      }

      @NotNull
      public final Map<UUID, Location[]> getLightLocationMap() {
         return lightLocationMap;
      }

      @NotNull
      public final Map<UUID, IFurniturePacket> getLightSendPacketMap() {
         return lightSendPacketMap;
      }

      @NotNull
      public final Map<UUID, IFurniturePacket> getLightRemovePacketMap() {
         return lightRemovePacketMap;
      }

      @NotNull
      public final Map<UUID, Map<Long, Set<UUID>>> getBarrierChunkMap() {
         return barrierChunkMap;
      }

      @NotNull
      public final Map<UUID, Map<Long, Set<UUID>>> getLightChunkMap() {
         return lightChunkMap;
      }

      @NotNull
      public final Map<UUID, IFurniturePacket[]> getInteractionHitboxPacketMap() {
         return interactionHitboxPacketMap;
      }

      @NotNull
      public final Map<UUID, IFurniturePacket[]> getShulkerHitboxPacketMap() {
         return shulkerHitboxPacketMap;
      }

      @NotNull
      public final Map<UUID, IFurniturePacket[]> getGhastHitboxPacketMap() {
         return ghastHitboxPacketMap;
      }

      @NotNull
      public final Map<UUID, Map<Long, Set<UUID>>> getBarrierHitboxReverseMap() {
         return barrierHitboxReverseMap;
      }

      @NotNull
      public final Map<UUID, Map<Long, Set<UUID>>> getLightReverseMap() {
         return lightReverseMap;
      }

      @NotNull
      public final Map<UUID, Map<Long, Set<UUID>>> getEntityHitboxReverseMap() {
         return entityHitboxReverseMap;
      }

      @NotNull
      public final Map<UUID, FurnitureHitboxEntity> getInteractionHitboxIdMap() {
         return interactionHitboxIdMap;
      }

      @NotNull
      public final Map<UUID, FurnitureHitboxEntity> getShulkerHitboxIdMap() {
         return shulkerHitboxIdMap;
      }

      @NotNull
      public final Map<UUID, FurnitureHitboxEntity> getGhastHitboxIdMap() {
         return ghastHitboxIdMap;
      }

      @NotNull
      public final Map<UUID, IFurniturePacket[]> getTextEntityPacketMap() {
         return textEntityPacketMap;
      }

      @NotNull
      public final Map<UUID, FurnitureTextEntity> getTextEntityIdMap() {
         return textEntityIdMap;
      }

      public final void updateBaseEntity(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         FurnitureBaseEntity furnitureBaseEntity = furnitureBaseMap.get(baseEntity.getUniqueId());
         if (furnitureBaseEntity != null) {
            furnitureBaseEntity.setMechanic$com_nexomc_core(mechanic);
         }

      }

      @Nullable
      public final ItemDisplay baseEntityFromHitbox(int entityId) {
         UUID uuid = entityIdMap.get(entityId);
         Entity entity;
         if (uuid != null) {
            UUID it = uuid;
            int value = 0;
            entity = Bukkit.getEntity(it);
         } else {
            entity = null;
         }

         Entity entity2 = entity;
         return entity2 instanceof ItemDisplay ? (ItemDisplay)entity2 : null;
      }

      @Nullable
      public final ItemDisplay baseEntityFromHitbox(@NotNull Location location) {
         BlockLocation blockLocation = new BlockLocation(location);
         World world = location.getWorld();
         return this.baseEntityFromHitbox(blockLocation, world);
      }

      @Nullable
      public final ItemDisplay baseEntityFromHitbox(@NotNull BlockLocation location, @NotNull World world) {
         long packed = this.packBlockPos(location.getX(), location.getY(), location.getZ());
         Map map = (Map)barrierHitboxReverseMap.get(world.getUID());
         ItemDisplay itemDisplay;
         if (map != null) {
            Set set = (Set)map.get(packed);
            if (set != null) {
               Iterator iterator = ((Iterable)set).iterator();

               while(true) {
                  if (!iterator.hasNext()) {
                     itemDisplay = null;
                     break;
                  }

                  UUID uuid = (UUID)iterator.next();
                  int value = 0;
                  int value2 = 0;
                  BarrierHitbox[] barrierHitboxes = barrierHitboxPositionMap.get(uuid);
                  boolean flag;
                  if (barrierHitboxes == null) {
                     flag = false;
                  } else {
                     Object[] objs = barrierHitboxes;
                     int value3 = 0;
                     int value4 = objs.length;

                     while(true) {
                        if (value3 >= value4) {
                           flag = false;
                           break;
                        }

                        Object element = objs[value3];
                        int value5 = 0;
                        if (Intrinsics.areEqual(element, location)) {
                           flag = true;
                           break;
                        }

                        ++value3;
                     }

                     flag = flag;
                  }

                  Entity entity = (flag ? world : null) != null ? (flag ? world : null).getEntity(uuid) : null;
                  ItemDisplay itemDisplay2 = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
                  if (itemDisplay2 != null) {
                     itemDisplay = itemDisplay2;
                     break;
                  }
               }

               if (itemDisplay != null) {
                  return itemDisplay;
               }
            }
         }

         Map map2 = (Map)entityHitboxReverseMap.get(world.getUID());
         if (map2 != null) {
            Set set2 = (Set)map2.get(packed);
            if (set2 != null) {
               for(UUID uuid : (java.lang.Iterable<UUID>)(Iterable)set2) {
                  int it = 0;
                  FurnitureHitboxEntity furnitureHitboxEntity = interactionHitboxIdMap.get(uuid);
                  if (furnitureHitboxEntity == null) {
                     furnitureHitboxEntity = shulkerHitboxIdMap.get(uuid);
                     if (furnitureHitboxEntity == null) {
                        furnitureHitboxEntity = ghastHitboxIdMap.get(uuid);
                     }
                  }

                  FurnitureHitboxEntity hitboxEntity = furnitureHitboxEntity;
                  ItemDisplay itemDisplay3;
                  if (hitboxEntity != null && hitboxEntity.overlaps(location)) {
                     Entity entity2 = world.getEntity(hitboxEntity.getBaseUuid());
                     itemDisplay3 = entity2 instanceof ItemDisplay ? (ItemDisplay)entity2 : null;
                  } else {
                     itemDisplay3 = null;
                  }

                  ItemDisplay itemDisplay4 = itemDisplay3;
                  if (itemDisplay4 != null) {
                     itemDisplay = itemDisplay4;
                     return itemDisplay;
                  }
               }

               itemDisplay = null;
               return itemDisplay;
            }
         }

         itemDisplay = null;
         return itemDisplay;
      }

      @Nullable
      public final ItemDisplay baseEntityFromLight(@NotNull BlockLocation location, @NotNull World world) {
         long packed = this.packBlockPos(location.getX(), location.getY(), location.getZ());
         Map map = (Map)lightReverseMap.get(world.getUID());
         ItemDisplay itemDisplay;
         if (map != null) {
            Set set = (Set)map.get(packed);
            if (set != null) {
               for(UUID uuid : (java.lang.Iterable<UUID>)(Iterable)set) {
                  int value = 0;
                  int value2 = 0;
                  LightBlock[] lightBlocks = lightPositionMap.get(uuid);
                  boolean flag;
                  if (lightBlocks == null) {
                     flag = false;
                  } else {
                     Object[] objs = lightBlocks;
                     int value3 = 0;
                     int value4 = objs.length;

                     while(true) {
                        if (value3 >= value4) {
                           flag = false;
                           break;
                        }

                        Object element = objs[value3];
                        int value5 = 0;
                        if (location.equals(element)) {
                           flag = true;
                           break;
                        }

                        ++value3;
                     }

                     flag = flag;
                  }

                  Entity entity = (flag ? world : null) != null ? (flag ? world : null).getEntity(uuid) : null;
                  ItemDisplay itemDisplay2 = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
                  if (itemDisplay2 != null) {
                     itemDisplay = itemDisplay2;
                     return itemDisplay;
                  }
               }

               itemDisplay = null;
               return itemDisplay;
            }
         }

         itemDisplay = null;
         return itemDisplay;
      }

      @Nullable
      public final FurnitureMechanic mechanicFromHitbox(int entityId) {
         UUID uuid2 = entityIdMap.get(entityId);
         if (uuid2 == null) {
            return null;
         } else {
            UUID uuid = uuid2;
            FurnitureHitboxEntity furnitureHitboxEntity = interactionHitboxIdMap.get(uuid);
            if (furnitureHitboxEntity == null) {
               furnitureHitboxEntity = shulkerHitboxIdMap.get(uuid);
               if (furnitureHitboxEntity == null) {
                  furnitureHitboxEntity = ghastHitboxIdMap.get(uuid);
               }
            }

            return furnitureHitboxEntity != null ? furnitureHitboxEntity.mechanic() : null;
         }
      }

      @Nullable
      public final FurnitureMechanic mechanicFromHitbox(@NotNull BlockLocation location) {
         long packed = this.packBlockPos(location.getX(), location.getY(), location.getZ());
         Iterable iterable = (Iterable)barrierHitboxReverseMap.values();

         for(Object element : iterable) {
            Map worldMap = (Map)element;
            int value = 0;
            Set set = (Set)worldMap.get(packed);
            if (set != null) {
               Iterator iterator = ((Iterable)set).iterator();

               FurnitureMechanic furnitureMechanic;
               while(true) {
                  if (iterator.hasNext()) {
                     UUID uuid = (UUID)iterator.next();
                     int value2 = 0;
                     FurnitureBaseEntity furnitureBaseEntity = furnitureBaseMap.get(uuid);
                     FurnitureMechanic furnitureMechanic2 = furnitureBaseEntity != null ? furnitureBaseEntity.mechanic() : null;
                     if (furnitureMechanic2 == null) {
                        continue;
                     }

                     furnitureMechanic = furnitureMechanic2;
                     break;
                  }

                  furnitureMechanic = null;
                  break;
               }

               FurnitureMechanic it = furnitureMechanic;
               if (it != null) {
                  int uuid = 0;
                  return it;
               }
            }
         }

         iterable = (Iterable)entityHitboxReverseMap.values();

         for(Object obj : iterable) {
            Map worldMap = (Map)obj;
            int value3 = 0;
            Set set2 = (Set)worldMap.get(packed);
            if (set2 != null) {
               Iterator it = ((Iterable)set2).iterator();

               FurnitureMechanic furnitureMechanic3;
               while(true) {
                  if (it.hasNext()) {
                     UUID uuid = (UUID)it.next();
                     int value4 = 0;
                     FurnitureHitboxEntity furnitureHitboxEntity = interactionHitboxIdMap.get(uuid);
                     if (furnitureHitboxEntity == null) {
                        furnitureHitboxEntity = shulkerHitboxIdMap.get(uuid);
                        if (furnitureHitboxEntity == null) {
                           furnitureHitboxEntity = ghastHitboxIdMap.get(uuid);
                        }
                     }

                     FurnitureMechanic furnitureMechanic4 = furnitureHitboxEntity != null ? furnitureHitboxEntity.mechanic() : null;
                     if (furnitureMechanic4 == null) {
                        continue;
                     }

                     furnitureMechanic3 = furnitureMechanic4;
                     break;
                  }

                  furnitureMechanic3 = null;
                  break;
               }

               FurnitureMechanic it_17 = furnitureMechanic3;
               if (it_17 != null) {
                  int value5 = 0;
                  return it_17;
               }
            }
         }

         return null;
      }

      @Nullable
      public final Location hitboxLocFromId(int entityId, @NotNull World world) {
         UUID uuid2 = entityIdMap.get(entityId);
         if (uuid2 == null) {
            return null;
         } else {
            UUID uuid = uuid2;
            FurnitureHitboxEntity furnitureHitboxEntity = interactionHitboxIdMap.get(uuid);
            if (furnitureHitboxEntity == null) {
               furnitureHitboxEntity = shulkerHitboxIdMap.get(uuid);
               if (furnitureHitboxEntity == null) {
                  furnitureHitboxEntity = ghastHitboxIdMap.get(uuid);
                  if (furnitureHitboxEntity == null) {
                     return null;
                  }
               }
            }

            FurnitureHitboxEntity hitboxEntity = furnitureHitboxEntity;
            Vector vector = hitboxEntity.hitboxLocation(entityId);
            return vector != null ? vector.toLocation(world) : null;
         }
      }

      public final boolean standingOnFurniture(@NotNull Player player) {
         Location location = player.getLocation();
         BlockLocation playerLoc = new BlockLocation(location);
         Iterable iterable = (Iterable)barrierHitboxPositionMap.values();
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
               BarrierHitbox[] hitboxes = (BarrierHitbox[])element;
               int value = 0;
               Object[] objs = hitboxes;
               int value2 = 0;

               block: {
                  for(int i = hitboxes.length; value2 < i; ++value2) {
                     Object obj = objs[value2];
                     int value3 = 0;
                     if (((BarrierHitbox)obj).distanceTo(playerLoc) <= (double)3.0F) {
                        flag = true;
                        break block;
                     }
                  }

                  flag = false;
               }

               if (flag) {
                  flag = true;
                  break;
               }
            }
         }

         return flag;
      }

      public final boolean blockIsHitbox(@NotNull Vector vec, @NotNull World world, @Nullable UUID excludeUUID) {
         long packed = this.packBlockPos(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
         Map map = (Map)barrierHitboxReverseMap.get(world.getUID());
         boolean flag;
         if (map != null) {
            Set set = (Set)map.get(packed);
            if (set != null) {
               Iterable iterable = (Iterable)set;
               boolean flag2;
               if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
                  flag2 = false;
               } else {
                  block: {
                     for(Object element : iterable) {
                        UUID it = (UUID)element;
                        int value = 0;
                        if (!Intrinsics.areEqual(it, excludeUUID)) {
                           flag2 = true;
                           break block;
                        }
                     }

                     flag2 = false;
                  }
               }

               flag = flag2;
               return flag;
            }
         }

         flag = false;
         return flag;
      }

      public final boolean blockIsHitbox(@NotNull Block block, @Nullable UUID excludeUUID, boolean collisionOnly) {
         long packed;
         UUID worldUid;
         boolean flag;
         block: {
            packed = this.packBlockPos(block.getX(), block.getY(), block.getZ());
            UUID uid = block.getWorld().getUID();
            worldUid = uid;
            Map map = (Map)barrierHitboxReverseMap.get(worldUid);
            if (map != null) {
               Set set = (Set)map.get(packed);
               if (set != null) {
                  Iterable iterable = (Iterable)set;
                  boolean flag2;
                  if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
                     flag2 = false;
                  } else {
                     block2: {
                        for(Object element : iterable) {
                           UUID it = (UUID)element;
                           int value = 0;
                           if (!Intrinsics.areEqual(it, excludeUUID)) {
                              flag2 = true;
                              break block2;
                           }
                        }

                        flag2 = false;
                     }
                  }

                  flag = flag2;
                  break block;
               }
            }

            flag = false;
         }

         boolean flag3;
         if (flag) {
            return true;
         } else if (collisionOnly) {
            return false;
         } else {
            Map map2 = (Map)entityHitboxReverseMap.get(worldUid);
            if (map2 != null) {
               Set set2 = (Set)map2.get(packed);
               if (set2 != null) {
                  Collection collection = (Collection)set2;
                  Collection collection2;
                  if (collection.isEmpty()) {
                     int value2 = 0;
                     collection2 = null;
                  } else {
                     collection2 = collection;
                  }

                  Set set3 = (Set)collection2;
                  if (set3 != null) {
                     Set candidates = set3;
                     BoundingBox boundingBox = BoundingBox.of(block).expand(-0.05);
                     BoundingBox blockBox = boundingBox;
                     Iterable iterable2 = (Iterable)candidates;
                     boolean flag4;
                     if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
                        flag4 = false;
                     } else {
                        Iterator iterator = iterable2.iterator();

                        while(true) {
                           if (!iterator.hasNext()) {
                              flag4 = false;
                              break;
                           }

                           block: {
                              Object obj = iterator.next();
                              UUID uuid = (UUID)obj;
                              int value3 = 0;
                              if (!Intrinsics.areEqual(uuid, excludeUUID)) {
                                 FurnitureHitboxEntity furnitureHitboxEntity = interactionHitboxIdMap.get(uuid);
                                 if (furnitureHitboxEntity == null) {
                                    furnitureHitboxEntity = shulkerHitboxIdMap.get(uuid);
                                    if (furnitureHitboxEntity == null) {
                                       furnitureHitboxEntity = ghastHitboxIdMap.get(uuid);
                                    }
                                 }

                                 block2: {
                                    FurnitureHitboxEntity furnitureHitboxEntity2 = furnitureHitboxEntity;
                                    if (furnitureHitboxEntity2 != null) {
                                       ObjectArrayList boundingBoxes = furnitureHitboxEntity2.getBoundingBoxes();
                                       if (boundingBoxes != null) {
                                          Iterable iterable3 = (Iterable)boundingBoxes;
                                          boolean flag5;
                                          if (iterable3 instanceof Collection && ((Collection)iterable3).isEmpty()) {
                                             flag5 = false;
                                          } else {
                                             Iterator iterator2 = iterable3.iterator();

                                             while(true) {
                                                if (!iterator2.hasNext()) {
                                                   flag5 = false;
                                                   break;
                                                }

                                                Object obj2 = iterator2.next();
                                                BoundingBox p0 = (BoundingBox)obj2;
                                                int value4 = 0;
                                                if (blockBox.overlaps(p0)) {
                                                   flag5 = true;
                                                   break;
                                                }
                                             }
                                          }

                                          flag3 = flag5;
                                          break block2;
                                       }
                                    }

                                    flag3 = false;
                                 }

                                 if (flag3) {
                                    flag4 = true;
                                    break block;
                                 }
                              }

                              flag4 = false;
                           }

                           if (flag4) {
                              flag4 = true;
                              break;
                           }
                        }
                     }

                     return flag4;
                  }
               }
            }

            return false;
         }
      }

      public final boolean blockIsHitbox(@NotNull Location location, @Nullable UUID excludeUUID) {
         UUID worldUid;
         long packed;
         boolean flag;
         block: {
            UUID uid = location.getWorld().getUID();
            worldUid = uid;
            packed = this.packBlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Map map = (Map)barrierHitboxReverseMap.get(worldUid);
            if (map != null) {
               Set set = (Set)map.get(packed);
               if (set != null) {
                  Iterable iterable = (Iterable)set;
                  boolean flag2;
                  if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
                     flag2 = false;
                  } else {
                     block2: {
                        for(Object element : iterable) {
                           UUID it = (UUID)element;
                           int value = 0;
                           if (!Intrinsics.areEqual(it, excludeUUID)) {
                              flag2 = true;
                              break block2;
                           }
                        }

                        flag2 = false;
                     }
                  }

                  flag = flag2;
                  break block;
               }
            }

            flag = false;
         }

         if (!flag) {
            block: {
               Map map2 = (Map)entityHitboxReverseMap.get(worldUid);
               if (map2 != null) {
                  Set set2 = (Set)map2.get(packed);
                  if (set2 != null) {
                     Iterable iterable2 = (Iterable)set2;
                     boolean flag3;
                     if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
                        flag3 = false;
                     } else {
                        block2: {
                           for(Object obj : iterable2) {
                              UUID it = (UUID)obj;
                              int value2 = 0;
                              if (!Intrinsics.areEqual(it, excludeUUID)) {
                                 flag3 = true;
                                 break block2;
                              }
                           }

                           flag3 = false;
                        }
                     }

                     flag = flag3;
                     break block;
                  }
               }

               flag = false;
            }

            if (!flag) {
               flag = false;
               return flag;
            }
         }

         flag = true;
         return flag;
      }

      public final boolean blockIsLight(@NotNull Vector vec, @NotNull World world, @Nullable UUID excludeUUID) {
         Companion companion = this;

         Object obj2;
         try {
            Companion companion2 = companion;
            int value = 0;
            Map map = lightLocationMap;
            boolean flag;
            if (map.isEmpty()) {
               flag = false;
            } else {
               Iterator iterator = map.entrySet().iterator();

               while(true) {
                  if (!iterator.hasNext()) {
                     flag = false;
                     break;
                  }

                  block: {
                     Map.Entry element = (Map.Entry)iterator.next();
                     int value2 = 0;
                     UUID uuid = (UUID)element.getKey();
                     Location[] locations = (Location[])element.getValue();
                     if (!Intrinsics.areEqual(uuid, excludeUUID)) {
                        Object[] objs = locations;
                        int value3 = 0;

                        block2: {
                           for(int i = locations.length; value3 < i; ++value3) {
                              Object obj3 = objs[value3];
                              int value4 = 0;
                              if (((Location)obj3).getX() == vec.getX() && ((Location)obj3).getY() == vec.getY() && ((Location)obj3).getZ() == vec.getZ() && Intrinsics.areEqual(((Location)obj3).getWorld(), world)) {
                                 flag = true;
                                 break block2;
                              }
                           }

                           flag = false;
                        }

                        if (flag) {
                           flag = true;
                           break block;
                        }
                     }

                     flag = false;
                  }

                  if (flag) {
                     flag = true;
                     break;
                  }
               }
            }

            obj2 = ResultShim.ok(flag);
         } catch (Throwable e) {
            obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         Object obj = UtilsKt.printOnFailure(obj2, false);
         Boolean flag2 = false;
         return (Boolean)(ResultShim.isFailure(obj) ? flag2 : obj);
      }

      public final long packBlockPos(int x, int y, int z) {
         return ((long)x & 67108863L) << 38 | ((long)z & 67108863L) << 12 | (long)y & 4095L;
      }

      public final long packChunkPos(int x, int z) {
         return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
      }

      public final void storeBarrierHitboxLocations(@NotNull UUID uuid, @NotNull World world, @NotNull Location[] locations) {
         UUID uid = world.getUID();
         this.storeLocations(uuid, uid, locations, barrierHitboxLocationMap, barrierHitboxReverseMap, barrierChunkMap);
      }

      public final void storeLightLocations(@NotNull UUID uuid, @NotNull World world, @NotNull Location[] locations) {
         UUID uid = world.getUID();
         this.storeLocations(uuid, uid, locations, lightLocationMap, lightReverseMap, lightChunkMap);
      }

      public final void removeBarrierHitboxLocations(@NotNull UUID uuid) {
         this.removeLocations(uuid, barrierHitboxLocationMap, barrierHitboxPositionMap, barrierHitboxReverseMap, barrierChunkMap);
      }

      public final void removeLightLocations(@NotNull UUID uuid) {
         this.removeLocations(uuid, lightLocationMap, lightPositionMap, lightReverseMap, lightChunkMap);
      }

      private final void storeLocations(UUID uuid, UUID worldUID, Location[] locations, Map<UUID, Location[]> locationMap, Map<UUID, Map<Long, Set<UUID>>> reverseMap, Map<UUID, Map<Long, Set<UUID>>> chunkMap) {
         locationMap.put(uuid, locations);
         Object obj = reverseMap.computeIfAbsent(worldUID, (a0_ -> Companion.storeLocations$lambda$0(a0_)));
         Map worldMap = (Map)obj;
         obj = chunkMap.computeIfAbsent(worldUID, (a0_ -> Companion.storeLocations$lambda$2(a0_)));
         Map chunkMap_6 = (Map)obj;
         Object[] objs = locations;
         int value = 0;

         for(int i = locations.length; value < i; ++value) {
            Object element = objs[value];
            int value2 = 0;
            long packed = $$INSTANCE.packBlockPos(((Location)element).getBlockX(), ((Location)element).getBlockY(), ((Location)element).getBlockZ());
            ((Set)worldMap.computeIfAbsent(packed, (a0_ -> Companion.storeLocations$lambda$4$0((Long)a0_)))).add(uuid);
            long chunkKey = $$INSTANCE.packChunkPos(((Location)element).getBlockX() >> 4, ((Location)element).getBlockZ() >> 4);
            ((Set)chunkMap_6.computeIfAbsent(chunkKey, (a0_ -> Companion.storeLocations$lambda$4$2((Long)a0_)))).add(uuid);
         }

      }

      private final <T extends BlockLocation> void removeLocations(UUID uuid, Map<UUID, Location[]> locationMap, Map<UUID, T[]> positionMap, Map<UUID, Map<Long, Set<UUID>>> reverseMap, Map<UUID, Map<Long, Set<UUID>>> chunkMap) {
         Location[] locations = locationMap.get(uuid);
         if (locations != null) {
            Location location = ArraysKt.firstOrNull(locations);
            if (location != null) {
               World world = location.getWorld();
               if (world != null) {
                  UUID uid = world.getUID();
                  if (uid != null) {
                     UUID worldUid = uid;
                     Map map = (Map)reverseMap.get(worldUid);
                     if (map == null) {
                        return;
                     }

                     Map worldMap = map;
                     Map chunkMap_5 = (Map)chunkMap.get(worldUid);
                     Object[] objs = locations;
                     int value = 0;

                     for(int i = locations.length; value < i; ++value) {
                        Object element = objs[value];
                        int value2 = 0;
                        long packed = $$INSTANCE.packBlockPos(((Location)element).getBlockX(), ((Location)element).getBlockY(), ((Location)element).getBlockZ());
                        Set set = (Set)worldMap.get(packed);
                        if (set != null) {
                           set.remove(uuid);
                        }

                        set = (Set)worldMap.get(packed);
                        if (set != null ? set.isEmpty() : false) {
                           worldMap.remove(packed);
                        }

                        if (chunkMap_5 != null) {
                           long chunkKey = $$INSTANCE.packChunkPos(((Location)element).getBlockX() >> 4, ((Location)element).getBlockZ() >> 4);
                           set = (Set)chunkMap_5.get(chunkKey);
                           if (set != null) {
                              set.remove(uuid);
                           }

                           set = (Set)chunkMap_5.get(chunkKey);
                           if (set != null ? set.isEmpty() : false) {
                              chunkMap_5.remove(chunkKey);
                           }
                        }
                     }

                     positionMap.remove(uuid);
                     locationMap.remove(uuid);
                     return;
                  }
               }
            }

         }
      }

      public final void addHitboxToReverseMap$com_nexomc_core(@NotNull UUID uuid, @NotNull FurnitureHitboxEntity hitboxEntity) {
         Iterable entityIds = (Iterable)hitboxEntity.getEntityIds();

         for(Object element : entityIds) {
            Integer id = (Integer)element;
            int value = 0;
            entityIdMap.put(id, uuid);
         }

         Object obj = entityHitboxReverseMap.computeIfAbsent(hitboxEntity.getWorldUID(), (a0_ -> Companion.addHitboxToReverseMap$lambda$1(a0_)));
         Map worldMap = (Map)obj;
         Iterable boundingBoxes = (Iterable)hitboxEntity.getBoundingBoxes();

         for(Object obj2 : boundingBoxes) {
            BoundingBox box = (BoundingBox)obj2;
            int value2 = 0;
            FurnitureHelpersKt.forEachBlock(box, ((a0_, a1_, a2_) -> Companion.addHitboxToReverseMap$lambda$3$0(box, worldMap, uuid, a0_, a1_, a2_)));
         }

      }

      public final void removeHitboxFromReverseMap$com_nexomc_core(@NotNull UUID uuid, @NotNull FurnitureHitboxEntity hitboxEntity) {
         Iterable entityIds = (Iterable)hitboxEntity.getEntityIds();

         for(Object element : entityIds) {
            Integer id = (Integer)element;
            int value = 0;
            entityIdMap.remove(id);
         }

         Map map = (Map)entityHitboxReverseMap.get(hitboxEntity.getWorldUID());
         if (map != null) {
            Map worldMap = map;
            Iterable boundingBoxes = (Iterable)hitboxEntity.getBoundingBoxes();

            for(Object obj : boundingBoxes) {
               BoundingBox box = (BoundingBox)obj;
               int value2 = 0;
               FurnitureHelpersKt.forEachBlock(box, ((a0_, a1_, a2_) -> Companion.removeHitboxFromReverseMap$lambda$1$0(box, worldMap, uuid, a0_, a1_, a2_)));
            }

         }
      }

      private static final Map storeLocations$lambda$0(UUID it) {
         return (Map)(new ConcurrentHashMap());
      }

      private static final Map storeLocations$lambda$1(Function1 tmp0, Object p0) {
         return (Map)tmp0.invoke(p0);
      }

      private static final Map storeLocations$lambda$2(UUID it) {
         return (Map)(new ConcurrentHashMap());
      }

      private static final Map storeLocations$lambda$3(Function1 tmp0, Object p0) {
         return (Map)tmp0.invoke(p0);
      }

      private static final Set storeLocations$lambda$4$0(Long it) {
         return (Set)ConcurrentHashMap.newKeySet();
      }

      private static final Set storeLocations$lambda$4$1(Function1 tmp0, Object p0) {
         return (Set)tmp0.invoke(p0);
      }

      private static final Set storeLocations$lambda$4$2(Long it) {
         return (Set)ConcurrentHashMap.newKeySet();
      }

      private static final Set storeLocations$lambda$4$3(Function1 tmp0, Object p0) {
         return (Set)tmp0.invoke(p0);
      }

      private static final Map addHitboxToReverseMap$lambda$1(UUID it) {
         return (Map)(new ConcurrentHashMap());
      }

      private static final Map addHitboxToReverseMap$lambda$2(Function1 tmp0, Object p0) {
         return (Map)tmp0.invoke(p0);
      }

      private static final Set addHitboxToReverseMap$lambda$3$0$0(Long it) {
         return (Set)ConcurrentHashMap.newKeySet();
      }

      private static final Set addHitboxToReverseMap$lambda$3$0$1(Function1 tmp0, Object p0) {
         return (Set)tmp0.invoke(p0);
      }

      private static final Unit addHitboxToReverseMap$lambda$3$0(BoundingBox box, Map worldMap, UUID uuid, int x, int y, int z) {
         Vector corner = new Vector(x, y, z);
         if (!box.overlaps(corner, VectorUtilsKt.plus(corner, 1, 1, 1))) {
            return Unit.INSTANCE;
         } else {
            long packed = $$INSTANCE.packBlockPos(x, y, z);
            ((Set)worldMap.computeIfAbsent(packed, (a0_ -> Companion.addHitboxToReverseMap$lambda$3$0$0((Long)a0_)))).add(uuid);
            return Unit.INSTANCE;
         }
      }

      private static final Unit removeHitboxFromReverseMap$lambda$1$0(BoundingBox box, Map worldMap, UUID uuid, int x, int y, int z) {
         Vector corner = new Vector(x, y, z);
         if (!box.overlaps(corner, VectorUtilsKt.plus(corner, 1, 1, 1))) {
            return Unit.INSTANCE;
         } else {
            long packed = $$INSTANCE.packBlockPos(x, y, z);
            Set set = (Set)worldMap.get(packed);
            boolean flag;
            if (set != null) {
               Set set2 = set;
               int value = 0;
               set2.remove(uuid);
               flag = set2.isEmpty();
            } else {
               flag = false;
            }

            if (flag) {
               worldMap.remove(packed);
            }

            return Unit.INSTANCE;
         }
      }

      static {
         BlockData createBlockData = Material.BARRIER.createBlockData();
         BARRIER_DATA = (Waterlogged)createBlockData;
         createBlockData = Material.BARRIER.createBlockData();
         Waterlogged waterlogged = (Waterlogged)createBlockData;
         int value = 0;
         waterlogged.setWaterlogged(true);
         BARRIER_DATA_WATERLOGGED = waterlogged;
         createBlockData = Material.AIR.createBlockData();
         AIR_DATA = createBlockData;
         createBlockData = Material.WATER.createBlockData();
         WATER_DATA = createBlockData;
         entityIdMap = (Map)(new ConcurrentHashMap());
         furnitureBaseMap = (Map)(new ConcurrentHashMap());
         barrierHitboxPositionMap = (Map)(new ConcurrentHashMap());
         barrierHitboxLocationMap = (Map)(new ConcurrentHashMap());
         lightPositionMap = (Map)(new ConcurrentHashMap());
         lightLocationMap = (Map)(new ConcurrentHashMap());
         lightSendPacketMap = (Map)(new ConcurrentHashMap());
         lightRemovePacketMap = (Map)(new ConcurrentHashMap());
         barrierChunkMap = (Map)(new ConcurrentHashMap());
         lightChunkMap = (Map)(new ConcurrentHashMap());
         interactionHitboxPacketMap = (Map)(new ConcurrentHashMap());
         shulkerHitboxPacketMap = (Map)(new ConcurrentHashMap());
         ghastHitboxPacketMap = (Map)(new ConcurrentHashMap());
         barrierHitboxReverseMap = (Map)(new ConcurrentHashMap());
         lightReverseMap = (Map)(new ConcurrentHashMap());
         entityHitboxReverseMap = (Map)(new ConcurrentHashMap());
         interactionHitboxIdMap = (Map)(new HitboxMap());
         shulkerHitboxIdMap = (Map)(new HitboxMap());
         ghastHitboxIdMap = (Map)(new HitboxMap());
         textEntityPacketMap = (Map)(new ConcurrentHashMap());
         textEntityIdMap = (Map)(new ConcurrentHashMap());
      }
   }
}
