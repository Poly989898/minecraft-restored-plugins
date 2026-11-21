package com.nexomc.nexo.nms.commons.furniture;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.furniture.FurnitureHitboxEntity;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacket;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.hitbox.FurnitureHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.GhastHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.InteractionHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.ShulkerHitbox;
import com.nexomc.nexo.mechanics.furniture.text_entity.FurnitureTextEntity;
import com.nexomc.nexo.nms.commons.EntityUtilsKt;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.LocationUtilsKt;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.optionals.OptionalsKt;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J*\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0012\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0002\b\u0003\u0018\u00010\u000f0\u000eH\u0002J$\u0010\b\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\f2\u0012\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0002\b\u0003\u0018\u00010\u000f0\u000eH\u0002J\u0018\u0010\u0011\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u0011\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u0018\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u0018\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u0019\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u0019\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u001a\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u001a\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u001b\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u001c\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u001c\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u001d\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u001d\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u001e\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u001e\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0018\u0010\u001f\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u001f\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u0012\u0010 \u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0010\u001a\u00020\fH\u0016J.\u0010!\u001a\b\u0012\u0004\u0012\u00020#0\"2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020%2\u0006\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020*H\u0002¨\u0006+"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/FurniturePacketManager;", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacketManager;", "<init>", "()V", "getEntity", "Lorg/bukkit/entity/Entity;", "entityId", "", "sendPacketAsync", "", "players", "", "Lorg/bukkit/entity/Player;", "packetFactory", "Lkotlin/Function0;", "Lnet/minecraft/network/protocol/Packet;", "player", "sendFurnitureMetadataPacket", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "hideFurniture", "showFurniture", "sendHitboxEntityPacket", "removeHitboxEntityPacket", "sendBarrierHitboxPacket", "removeBarrierHitboxPacket", "sendLightMechanicPacket", "removeLightMechanicPacket", "sendTextEntityMechanicPacket", "removeTextEntityMechanicPacket", "findTargetFurnitureHitbox", "generateRaycastPoints", "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", "Lorg/bukkit/Location;", "start", "Lnet/minecraft/world/phys/Vec3;", "direction", "distance", "", "world", "Lorg/bukkit/World;", "com.nexomc:commons"}
)
public final class FurniturePacketManager implements IFurniturePacketManager {
   @Nullable
   public Entity getEntity(int entityId) {
      List worlds = Bukkit.getWorlds();
      Iterable iterable = (Iterable)worlds;

      for(Object element : iterable) {
         World it = (World)element;
         int value = 0;
         net.minecraft.world.entity.Entity entity = ((CraftWorld)it).getHandle().getEntity(entityId);
         if (entity != null) {
            CraftEntity bukkitEntity = entity.getBukkitEntity();
            if (bukkitEntity != null) {
               return (Entity)bukkitEntity;
            }
         }
      }

      return null;
   }

   private final void sendPacketAsync(Collection<? extends Player> players, Function0<? extends Packet<?>> packetFactory) {
      if (!players.isEmpty()) {
         List copiedPlayers = CollectionsKt.toList((Iterable)players);
         FurniturePacketProcessor.INSTANCE.sendAsync((() -> FurniturePacketManager.sendPacketAsync$lambda$0(packetFactory, copiedPlayers)));
      }
   }

   private final void sendPacketAsync(Player player, Function0<? extends Packet<?>> packetFactory) {
      FurniturePacketProcessor.INSTANCE.sendAsync((() -> FurniturePacketManager.sendPacketAsync$lambda$1(packetFactory, player)));
   }

   public void sendFurnitureMetadataPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      Set trackedBy = baseEntity.getTrackedBy();
      this.sendPacketAsync((Collection)trackedBy, (() -> FurniturePacketManager.sendFurnitureMetadataPacket$lambda$0(baseEntity, mechanic)));
   }

   public void sendFurnitureMetadataPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
      this.sendPacketAsync(player, (() -> FurniturePacketManager.sendFurnitureMetadataPacket$lambda$1(baseEntity, mechanic)));
   }

   public void hideFurniture(@NotNull ItemDisplay baseEntity, @NotNull Player player) {
      List data = CollectionsKt.listOf(new SynchedEntityData.DataValue(17, EntityDataSerializers.FLOAT, 0.0F));
      ((CraftPlayer)player).getHandle().connection.send((Packet)(new ClientboundSetEntityDataPacket(baseEntity.getEntityId(), data)));
   }

   public void showFurniture(@NotNull ItemDisplay baseEntity, @NotNull Player player) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
      if (furnitureMechanic != null) {
         FurnitureMechanic mechanic = furnitureMechanic;
         Float number = mechanic.getProperties().getViewRange();
         float viewRange = number != null ? number : 1.0F;
         List data = CollectionsKt.listOf(new SynchedEntityData.DataValue(17, EntityDataSerializers.FLOAT, viewRange));
         ((CraftPlayer)player).getHandle().connection.send((Packet)(new ClientboundSetEntityDataPacket(baseEntity.getEntityId(), data)));
      }
   }

   public void sendHitboxEntityPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      ClientboundEntityHitboxPacket.Companion.precomputeHitboxPackets(baseEntity, mechanic);
      Set trackedBy = baseEntity.getTrackedBy();
      this.sendPacketAsync((Collection)trackedBy, (() -> FurniturePacketManager.sendHitboxEntityPacket$lambda$0(baseEntity, mechanic)));
   }

   public void sendHitboxEntityPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
      ClientboundEntityHitboxPacket.Companion.precomputeHitboxPackets(baseEntity, mechanic);
      this.sendPacketAsync(player, (() -> FurniturePacketManager.sendHitboxEntityPacket$lambda$1(baseEntity, mechanic)));
   }

   public void removeHitboxEntityPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      UUID uniqueId = baseEntity.getUniqueId();
      UUID uuid = uniqueId;
      IntArrayList entityIds = IntArrayList.of();
      FurnitureHitboxEntity furnitureHitboxEntity = (FurnitureHitboxEntity)IFurniturePacketManager.Companion.getInteractionHitboxIdMap().remove(uuid);
      if (furnitureHitboxEntity != null) {
         IntArrayList intArrayList = furnitureHitboxEntity.getEntityIds();
         if (intArrayList != null) {
            IntArrayList intArrayList2 = intArrayList;
            IntList p0 = (IntList)intArrayList2;
            int value = 0;
            entityIds.addAll(p0);
         }
      }

      furnitureHitboxEntity = (FurnitureHitboxEntity)IFurniturePacketManager.Companion.getShulkerHitboxIdMap().remove(uuid);
      if (furnitureHitboxEntity != null) {
         IntArrayList intArrayList3 = furnitureHitboxEntity.getEntityIds();
         if (intArrayList3 != null) {
            IntArrayList intArrayList4 = intArrayList3;
            IntList p0 = (IntList)intArrayList4;
            int value2 = 0;
            entityIds.addAll(p0);
         }
      }

      furnitureHitboxEntity = (FurnitureHitboxEntity)IFurniturePacketManager.Companion.getGhastHitboxIdMap().remove(uuid);
      if (furnitureHitboxEntity != null) {
         IntArrayList intArrayList5 = furnitureHitboxEntity.getEntityIds();
         if (intArrayList5 != null) {
            IntArrayList intArrayList6 = intArrayList5;
            IntList p0 = (IntList)intArrayList6;
            int value3 = 0;
            entityIds.addAll(p0);
         }
      }

      IFurniturePacketManager.Companion.getInteractionHitboxPacketMap().remove(uuid);
      IFurniturePacketManager.Companion.getShulkerHitboxPacketMap().remove(uuid);
      IFurniturePacketManager.Companion.getGhastHitboxPacketMap().remove(uuid);
      ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket((IntList)entityIds);
      Set trackedBy = baseEntity.getTrackedBy();
      this.sendPacketAsync((Collection)trackedBy, (() -> FurniturePacketManager.removeHitboxEntityPacket$lambda$2(packet)));
   }

   public void removeHitboxEntityPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
      UUID uniqueId = baseEntity.getUniqueId();
      UUID uuid = uniqueId;
      this.sendPacketAsync(player, (() -> FurniturePacketManager.removeHitboxEntityPacket$lambda$3(uuid)));
   }

   public void sendBarrierHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      Set trackedBy = baseEntity.getTrackedBy();
      this.sendPacketAsync((Collection)trackedBy, (() -> FurniturePacketManager.sendBarrierHitboxPacket$lambda$0(baseEntity, mechanic)));
   }

   public void sendBarrierHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
      this.sendPacketAsync(player, (() -> FurniturePacketManager.sendBarrierHitboxPacket$lambda$1(baseEntity, mechanic)));
   }

   public void removeBarrierHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      Collection trackedBy = (Collection)baseEntity.getTrackedBy();
      Collection collection;
      if (trackedBy.isEmpty()) {
         int value = 0;
         List list = baseEntity.getWorld().getPlayers();
         Iterable iterable = (Iterable)list;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            Player it = (Player)element;
            int value2 = 0;
            if (it.canSee((Entity)baseEntity)) {
               destination.add(element);
            }
         }

         collection = (Collection)((List)destination);
      } else {
         collection = trackedBy;
      }

      Collection players = collection;
      UUID uniqueId = baseEntity.getUniqueId();
      UUID uuid = uniqueId;
      ClientboundRemoveBarrierHitboxPacket packet = new ClientboundRemoveBarrierHitboxPacket(baseEntity, mechanic);
      IFurniturePacketManager.Companion.removeBarrierHitboxLocations(uuid);
      this.sendPacketAsync(players, (() -> FurniturePacketManager.removeBarrierHitboxPacket$lambda$1(packet)));
   }

   public void removeBarrierHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
      ClientboundRemoveBarrierHitboxPacket packet = new ClientboundRemoveBarrierHitboxPacket(baseEntity, mechanic);
      this.sendPacketAsync(player, (() -> FurniturePacketManager.removeBarrierHitboxPacket$lambda$2(packet)));
   }

   public void sendLightMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      UUID uniqueId = baseEntity.getUniqueId();
      UUID uuid = uniqueId;
      ClientboundSendFurnitureLightsPacket.Companion.precomputeLightPositions(baseEntity, mechanic);
      Set trackedBy = baseEntity.getTrackedBy();
      this.sendPacketAsync((Collection)trackedBy, (() -> FurniturePacketManager.sendLightMechanicPacket$lambda$0(uuid, baseEntity, mechanic)));
   }

   public void sendLightMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
      UUID uniqueId = baseEntity.getUniqueId();
      UUID uuid = uniqueId;
      ClientboundSendFurnitureLightsPacket.Companion.precomputeLightPositions(baseEntity, mechanic);
      this.sendPacketAsync(player, (() -> FurniturePacketManager.sendLightMechanicPacket$lambda$1(uuid, baseEntity, mechanic)));
   }

   public void removeLightMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      UUID uniqueId = baseEntity.getUniqueId();
      UUID uuid = uniqueId;
      Object obj = CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getLightRemovePacketMap(), uuid, (() -> FurniturePacketManager.removeLightMechanicPacket$lambda$0(baseEntity, mechanic)));
      ClientboundRemoveFurnitureLightsPacket removeLightPacket = (ClientboundRemoveFurnitureLightsPacket)obj;
      IFurniturePacketManager.Companion.getLightPositionMap().remove(uuid);
      IFurniturePacketManager.Companion.removeLightLocations(uuid);
      IFurniturePacketManager.Companion.getLightSendPacketMap().remove(uuid);
      Set trackedBy = baseEntity.getTrackedBy();
      this.sendPacketAsync((Collection)trackedBy, (() -> FurniturePacketManager.removeLightMechanicPacket$lambda$1(removeLightPacket)));
   }

   public void removeLightMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
      UUID uniqueId = baseEntity.getUniqueId();
      UUID uuid = uniqueId;
      Object obj = CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getLightRemovePacketMap(), uuid, (() -> FurniturePacketManager.removeLightMechanicPacket$lambda$2(baseEntity, mechanic)));
      ClientboundRemoveFurnitureLightsPacket removeLightPacket = obj instanceof ClientboundRemoveFurnitureLightsPacket ? (ClientboundRemoveFurnitureLightsPacket)obj : null;
      this.sendPacketAsync(player, (() -> FurniturePacketManager.removeLightMechanicPacket$lambda$3(removeLightPacket)));
   }

   public void sendTextEntityMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      ClientboundFurnitureTextEntityPacket.Companion.precomputeTextEntityPackets(baseEntity, mechanic);
      Set trackedBy = baseEntity.getTrackedBy();
      this.sendPacketAsync((Collection)trackedBy, (() -> FurniturePacketManager.sendTextEntityMechanicPacket$lambda$0(baseEntity, mechanic)));
   }

   public void sendTextEntityMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
      ClientboundFurnitureTextEntityPacket.Companion.precomputeTextEntityPackets(baseEntity, mechanic);
      this.sendPacketAsync(player, (() -> FurniturePacketManager.sendTextEntityMechanicPacket$lambda$1(baseEntity, mechanic)));
   }

   public void removeTextEntityMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      UUID uniqueId = baseEntity.getUniqueId();
      UUID uuid = uniqueId;
      FurnitureTextEntity furnitureTextEntity = (FurnitureTextEntity)IFurniturePacketManager.Companion.getTextEntityIdMap().remove(uuid);
      if (furnitureTextEntity != null) {
         IntArrayList intArrayList = furnitureTextEntity.getEntityIds();
         if (intArrayList != null) {
            IntArrayList entityIds = intArrayList;
            IFurniturePacketManager.Companion.getTextEntityPacketMap().remove(uuid);
            ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket((IntList)entityIds);
            Set trackedBy = baseEntity.getTrackedBy();
            this.sendPacketAsync((Collection)trackedBy, (() -> FurniturePacketManager.removeTextEntityMechanicPacket$lambda$0(packet)));
            return;
         }
      }

   }

   public void removeTextEntityMechanicPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
      UUID uniqueId = baseEntity.getUniqueId();
      UUID uuid = uniqueId;
      this.sendPacketAsync(player, (() -> FurniturePacketManager.removeTextEntityMechanicPacket$lambda$1(uuid)));
   }

   @Nullable
   public ItemDisplay findTargetFurnitureHitbox(@NotNull Player player) {
      java.lang.Object obj;
      java.lang.Object obj2;
      org.bukkit.entity.Entity entity;
      FurniturePacketManager furniturePacketManager = this;

      Object obj7;
      try {
         FurniturePacketManager furniturePacketManager2 = furniturePacketManager;
         int value = 0;
         AttributeInstance attribute = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
         obj7 = ResultShim.ok(attribute != null ? attribute.getValue() : null);
      } catch (Throwable e) {
         obj7 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Double number = (Double)(ResultShim.isFailure(obj7) ? null : obj7);
      double maxReachDistance = number != null ? number : (double)5.0F;
      ServerPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
      Pair pair = TuplesKt.to(nmsPlayer.getEyePosition(1.0F), nmsPlayer.getLookAngle());
      Vec3 start = (Vec3)pair.component1();
      Vec3 direction = (Vec3)pair.component2();
      Vec3 distanceDirection = direction.scale(maxReachDistance);
      Vec3 end = start.add(distanceDirection);
      World world = ((CraftPlayer)player).getWorld();
      ObjectArrayList raycastPoints = this.generateRaycastPoints(start, direction, maxReachDistance, world);
      Collection collection = IFurniturePacketManager.Companion.getBarrierHitboxLocationMap().values();
      List<Location> barrierHitboxes = new ArrayList<>();
      for(Object hitboxes : collection) {
         barrierHitboxes.addAll(java.util.Arrays.asList((Location[])hitboxes));
      }

      Iterable iterable = (Iterable)raycastPoints;
      Iterator iterator = iterable.iterator();

      while(true) {
         if (iterator.hasNext()) {
            Object element = iterator.next();
            Location point = (Location)element;
            int value2 = 0;
            if (!barrierHitboxes.contains(point)) {
               continue;
            }

            obj2 = element;
            break;
         }

         obj2 = null;
         break;
      }

      Location aabb = (Location)obj2;
      if (aabb != null) {
         Location hitblock = aabb;
         int value3 = 0;
         Iterable entrySet = (Iterable)IFurniturePacketManager.Companion.getBarrierHitboxLocationMap().entrySet();
         Iterator iterator2 = entrySet.iterator();

         while(true) {
            if (iterator2.hasNext()) {
               Object obj8 = iterator2.next();
               Map.Entry it = (Map.Entry)obj8;
               int value4 = 0;
               if (!ArraysKt.contains((Location[])it.getValue(), hitblock)) {
                  continue;
               }

               obj = obj8;
               break;
            }

            obj = null;
            break;
         }

         block: {
            Map.Entry entry = (Map.Entry)obj;
            if (entry != null) {
               UUID key = (UUID)entry.getKey();
               if (key != null) {
                  UUID p0 = key;
                  int value17 = 0;
                  entity = Bukkit.getEntity(p0);
                  break block;
               }
            }

            entity = null;
         }

         Entity entity2 = entity;
         return entity2 instanceof ItemDisplay ? (ItemDisplay)entity2 : null;
      } else {
         AABB aabb_13 = nmsPlayer.getBoundingBox().expandTowards(distanceDirection).inflate((double)1.0F);
         List entities = nmsPlayer.level().getEntities((net.minecraft.world.entity.Entity)nmsPlayer, aabb_13, (a0_ -> FurniturePacketManager.findTargetFurnitureHitbox$lambda$3((net.minecraft.world.entity.Entity)a0_)));
         Object closestEntity = null;
         double closestDistance = (double)0.0F;
         closestDistance = Double.MAX_VALUE;
         Sequence sequence = SequencesKt.mapNotNull(CollectionsKt.asSequence((Iterable)entities), FurniturePacketManager::findTargetFurnitureHitbox$lambda$5);

         for(Object obj9 : kotlin.sequences.SequencesKt.asIterable(sequence)) {
            ItemDisplay bukkitEntity = (ItemDisplay)obj9;
            int value5 = 0;
            FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)bukkitEntity);
            if (furnitureMechanic != null) {
               FurnitureMechanic mechanic = furnitureMechanic;
               Iterable interactions = (Iterable)mechanic.getHitbox().getInteractions();
               Object2ObjectOpenHashMap result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((interactions instanceof Collection ? (Collection)interactions : null) != null ? (interactions instanceof Collection ? (Collection)interactions : null).size() : 10), 16));
               Map destination = (Map)result;

               for(Object obj10 : interactions) {
                  InteractionHitbox it = (InteractionHitbox)obj10;
                  int value6 = 0;
                  Location location2 = bukkitEntity.getLocation();
                  Location location3 = LocationUtilsKt.add(location2, it.offset(bukkitEntity.getLocation().getYaw()));
                  destination.put(obj10, location3);
               }

               Map map = (Map)((Object2ObjectOpenHashMap)destination);

               for(Map.Entry entry2 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
                  InteractionHitbox interaction = (InteractionHitbox)entry2.getKey();
                  Location location = (Location)entry2.getValue();
                  Pair it = TuplesKt.to((double)interaction.getWidth(), (double)interaction.getHeight());
                  double width = ((Number)it.component1()).doubleValue();
                  double height = ((Number)it.component2()).doubleValue();
                  Location it_10 = location.add((double)0.0F, (double)interaction.getHeight() / (double)2.0F, (double)0.0F);
                  int value7 = 0;
                  Vec3 hitboxCenter = new Vec3(it_10.getX(), it_10.getY(), it_10.getZ());
                  Optional optional = AABB.ofSize(hitboxCenter, width, height, width).clip(start, end);
                  Vec3 orNull = (Vec3)OptionalsKt.getOrNull(optional);
                  if (orNull != null) {
                     Vec3 rayTrace = orNull;
                     int value8 = 0;
                     double distanceTo = start.distanceToSqr(rayTrace);
                     if (distanceTo < closestDistance) {
                        closestDistance = distanceTo;
                        closestEntity = bukkitEntity;
                     }

                     Unit unit = Unit.INSTANCE;
                     unit = Unit.INSTANCE;
                  } else {
                     Object obj3 = null;
                  }
               }

               if (closestEntity == null) {
                  Iterable shulkers = (Iterable)mechanic.getHitbox().getShulkers();
                  result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((shulkers instanceof Collection ? (Collection)shulkers : null) != null ? (shulkers instanceof Collection ? (Collection)shulkers : null).size() : 10), 16));
                  destination = (Map)result;

                  for(Object obj11 : shulkers) {
                     ShulkerHitbox it = (ShulkerHitbox)obj11;
                     int value9 = 0;
                     Location location4 = bukkitEntity.getLocation();
                     Location location5 = LocationUtilsKt.add(location4, it.offset(bukkitEntity.getLocation().getYaw()));
                     destination.put(obj11, location5);
                  }

                  Map map2 = (Map)((Object2ObjectOpenHashMap)destination);

                  for(Map.Entry entry3 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
                     ShulkerHitbox shulker = (ShulkerHitbox)entry3.getKey();
                     Location location = (Location)entry3.getValue();
                     Pair hitboxCenter = TuplesKt.to(shulker.getScale(), shulker.getLength() * shulker.getScale());
                     double width = ((Number)hitboxCenter.component1()).doubleValue();
                     double height = ((Number)hitboxCenter.component2()).doubleValue();
                     Location it = location.add((double)0.0F, height / (double)2.0F, (double)0.0F);
                     int value10 = 0;
                     Vec3 hitboxCenter_6 = new Vec3(it.getX(), it.getY(), it.getZ());
                     Optional optional2 = AABB.ofSize(hitboxCenter_6, width, height, width).clip(start, end);
                     Vec3 vec3 = (Vec3)OptionalsKt.getOrNull(optional2);
                     if (vec3 != null) {
                        Vec3 rayTrace = vec3;
                        int value11 = 0;
                        double distanceTo = start.distanceToSqr(rayTrace);
                        if (distanceTo < closestDistance) {
                           closestDistance = distanceTo;
                           closestEntity = bukkitEntity;
                        }

                        Unit unit2 = Unit.INSTANCE;
                        unit2 = Unit.INSTANCE;
                     } else {
                        Object obj4 = null;
                     }
                  }
               }

               if (closestEntity == null) {
                  Iterable ghasts = (Iterable)mechanic.getHitbox().getGhasts();
                  result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((ghasts instanceof Collection ? (Collection)ghasts : null) != null ? (ghasts instanceof Collection ? (Collection)ghasts : null).size() : 10), 16));
                  destination = (Map)result;

                  for(Object obj12 : ghasts) {
                     GhastHitbox ghastHitbox = (GhastHitbox)obj12;
                     int value12 = 0;
                     Location location6 = bukkitEntity.getLocation();
                     Location location7 = LocationUtilsKt.add(location6, ghastHitbox.offset(bukkitEntity.getLocation().getYaw()));
                     destination.put(obj12, location7);
                  }

                  Map map3 = (Map)((Object2ObjectOpenHashMap)destination);

                  for(Map.Entry entry4 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map3.entrySet()) {
                     GhastHitbox ghast = (GhastHitbox)entry4.getKey();
                     Location location = (Location)entry4.getValue();
                     double size = ghast.getScale() / (double)4.0F;
                     Location it = location.add((double)0.0F, size / (double)2.0F, (double)0.0F);
                     int value13 = 0;
                     Vec3 hitboxCenter = new Vec3(it.getX(), it.getY(), it.getZ());
                     Optional optional3 = AABB.ofSize(hitboxCenter, size, size, size).clip(start, end);
                     Vec3 vec32 = (Vec3)OptionalsKt.getOrNull(optional3);
                     if (vec32 != null) {
                        Vec3 rayTrace = vec32;
                        int value14 = 0;
                        double distanceTo = start.distanceToSqr(rayTrace);
                        if (distanceTo < closestDistance) {
                           closestDistance = distanceTo;
                           closestEntity = bukkitEntity;
                        }

                        Unit unit3 = Unit.INSTANCE;
                        unit3 = Unit.INSTANCE;
                     } else {
                        Object obj5 = null;
                     }
                  }
               }

               if (closestEntity == null) {
                  FurnitureHitbox hitbox = mechanic.getHitbox();
                  Location location8 = bukkitEntity.getLocation();
                  Iterable iterable2 = (Iterable)hitbox.barrierLocations(location8, bukkitEntity.getLocation().getYaw());

                  for(Object obj13 : iterable2) {
                     Location location = (Location)obj13;
                     Location it = location.add((double)0.0F, (double)0.5F, (double)0.0F);
                     int value15 = 0;
                     Vec3 hitboxCenter = new Vec3(it.getX(), it.getY(), it.getZ());
                     Optional optional4 = AABB.ofSize(hitboxCenter, (double)1.0F, (double)0.5F, (double)1.0F).clip(start, end);
                     Vec3 vec33 = (Vec3)OptionalsKt.getOrNull(optional4);
                     if (vec33 != null) {
                        Vec3 rayTrace = vec33;
                        int value16 = 0;
                        double distanceTo = start.distanceToSqr(rayTrace);
                        if (distanceTo < closestDistance) {
                           closestDistance = distanceTo;
                           closestEntity = bukkitEntity;
                        }

                        Unit unit4 = Unit.INSTANCE;
                        unit4 = Unit.INSTANCE;
                     } else {
                        Object obj6 = null;
                     }
                  }
               }
            }
         }

         return (ItemDisplay)closestEntity;
      }
   }

   private final ObjectArrayList<Location> generateRaycastPoints(Vec3 start, Vec3 direction, double distance, World world) {
      ObjectArrayList objectArrayList = new ObjectArrayList();
      ObjectArrayList objectArrayList2 = objectArrayList;
      int value = 0;
      Vec3 currentPosition = start;
      int steps = (int)(distance / (double)0.5F);

      for(int i = 0; i < steps; ++i) {
         Vec3 vec3 = currentPosition.add(direction.scale((double)0.5F));
         currentPosition = vec3;
         ((Collection)objectArrayList2).add((new Location(world, currentPosition.x, currentPosition.y, currentPosition.z)).toBlockLocation());
      }

      Vec3 loc = start.add(direction.scale(distance));
      ((Collection)objectArrayList2).add((new Location(world, loc.x, loc.y, loc.z)).toBlockLocation());
      return objectArrayList;
   }

   private static final Unit sendPacketAsync$lambda$0(Function0 packetFactory, List copiedPlayers) {
      Packet invoke = (Packet)packetFactory.invoke();
      if (invoke == null) {
         return Unit.INSTANCE;
      } else {
         Packet packet = invoke;
         Iterable iterable = (Iterable)copiedPlayers;

         for(Object element : iterable) {
            Player player = (Player)element;
            int value = 0;
            if (player.isOnline()) {
               ((CraftPlayer)player).getHandle().connection.send(packet);
            }
         }

         return Unit.INSTANCE;
      }
   }

   private static final Unit sendPacketAsync$lambda$1(Function0 packetFactory, Player player) {
      Packet invoke = (Packet)packetFactory.invoke();
      if (invoke == null) {
         return Unit.INSTANCE;
      } else {
         Packet packet = invoke;
         if (player.isOnline()) {
            ((CraftPlayer)player).getHandle().connection.send(packet);
         }

         return Unit.INSTANCE;
      }
   }

   private static final Packet sendFurnitureMetadataPacket$lambda$0(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundFurnitureBasePacket(baseEntity, mechanic);
   }

   private static final Packet sendFurnitureMetadataPacket$lambda$1(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundFurnitureBasePacket(baseEntity, mechanic);
   }

   private static final Packet sendHitboxEntityPacket$lambda$0(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundEntityHitboxPacket(baseEntity, mechanic);
   }

   private static final Packet sendHitboxEntityPacket$lambda$1(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundEntityHitboxPacket(baseEntity, mechanic);
   }

   private static final Packet removeHitboxEntityPacket$lambda$2(ClientboundRemoveEntitiesPacket packet) {
      return (Packet)packet;
   }

   private static final Packet removeHitboxEntityPacket$lambda$3(UUID uuid) {
      IntArrayList entityIds = IntArrayList.of();
      FurnitureHitboxEntity furnitureHitboxEntity = (FurnitureHitboxEntity)IFurniturePacketManager.Companion.getInteractionHitboxIdMap().get(uuid);
      if (furnitureHitboxEntity != null) {
         IntArrayList intArrayList = furnitureHitboxEntity.getEntityIds();
         if (intArrayList != null) {
            IntArrayList intArrayList2 = intArrayList;
            IntList p0 = (IntList)intArrayList2;
            int value = 0;
            entityIds.addAll(p0);
         }
      }

      furnitureHitboxEntity = (FurnitureHitboxEntity)IFurniturePacketManager.Companion.getShulkerHitboxIdMap().get(uuid);
      if (furnitureHitboxEntity != null) {
         IntArrayList intArrayList3 = furnitureHitboxEntity.getEntityIds();
         if (intArrayList3 != null) {
            IntArrayList intArrayList4 = intArrayList3;
            IntList p0 = (IntList)intArrayList4;
            int value2 = 0;
            entityIds.addAll(p0);
         }
      }

      furnitureHitboxEntity = (FurnitureHitboxEntity)IFurniturePacketManager.Companion.getGhastHitboxIdMap().get(uuid);
      if (furnitureHitboxEntity != null) {
         IntArrayList intArrayList5 = furnitureHitboxEntity.getEntityIds();
         if (intArrayList5 != null) {
            IntArrayList intArrayList6 = intArrayList5;
            IntList p0 = (IntList)intArrayList6;
            int value3 = 0;
            entityIds.addAll(p0);
         }
      }

      return (Packet)(new ClientboundRemoveEntitiesPacket((IntList)entityIds));
   }

   private static final Packet sendBarrierHitboxPacket$lambda$0(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundSendBarrierHitboxPacket(baseEntity, mechanic);
   }

   private static final Packet sendBarrierHitboxPacket$lambda$1(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundSendBarrierHitboxPacket(baseEntity, mechanic);
   }

   private static final Packet removeBarrierHitboxPacket$lambda$1(ClientboundRemoveBarrierHitboxPacket packet) {
      return packet;
   }

   private static final Packet removeBarrierHitboxPacket$lambda$2(ClientboundRemoveBarrierHitboxPacket packet) {
      return packet;
   }

   private static final IFurniturePacket sendLightMechanicPacket$lambda$0$0(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundSendFurnitureLightsPacket(baseEntity, mechanic);
   }

   private static final Packet sendLightMechanicPacket$lambda$0(UUID uuid, ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      Object obj = CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getLightSendPacketMap(), uuid, (() -> FurniturePacketManager.sendLightMechanicPacket$lambda$0$0(baseEntity, mechanic)));
      ClientboundSendFurnitureLightsPacket clientboundSendFurnitureLightsPacket = obj instanceof ClientboundSendFurnitureLightsPacket ? (ClientboundSendFurnitureLightsPacket)obj : null;
      if ((obj instanceof ClientboundSendFurnitureLightsPacket ? (ClientboundSendFurnitureLightsPacket)obj : null) == null) {
         return null;
      } else {
         ClientboundSendFurnitureLightsPacket lightPacket = clientboundSendFurnitureLightsPacket;
         IFurniturePacketManager.Companion.getLightRemovePacketMap().remove(uuid);
         return lightPacket;
      }
   }

   private static final IFurniturePacket sendLightMechanicPacket$lambda$1$0(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundSendFurnitureLightsPacket(baseEntity, mechanic);
   }

   private static final Packet sendLightMechanicPacket$lambda$1(UUID uuid, ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      Object obj = CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getLightSendPacketMap(), uuid, (() -> FurniturePacketManager.sendLightMechanicPacket$lambda$1$0(baseEntity, mechanic)));
      return obj instanceof ClientboundSendFurnitureLightsPacket ? (ClientboundSendFurnitureLightsPacket)obj : null;
   }

   private static final IFurniturePacket removeLightMechanicPacket$lambda$0(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundRemoveFurnitureLightsPacket(baseEntity, mechanic);
   }

   private static final Packet removeLightMechanicPacket$lambda$1(ClientboundRemoveFurnitureLightsPacket removeLightPacket) {
      return removeLightPacket;
   }

   private static final IFurniturePacket removeLightMechanicPacket$lambda$2(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundRemoveFurnitureLightsPacket(baseEntity, mechanic);
   }

   private static final Packet removeLightMechanicPacket$lambda$3(ClientboundRemoveFurnitureLightsPacket removeLightPacket) {
      return removeLightPacket;
   }

   private static final Packet sendTextEntityMechanicPacket$lambda$0(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundFurnitureTextEntityPacket(baseEntity, mechanic);
   }

   private static final Packet sendTextEntityMechanicPacket$lambda$1(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      return new ClientboundFurnitureTextEntityPacket(baseEntity, mechanic);
   }

   private static final Packet removeTextEntityMechanicPacket$lambda$0(ClientboundRemoveEntitiesPacket packet) {
      return (Packet)packet;
   }

   private static final Packet removeTextEntityMechanicPacket$lambda$1(UUID uuid) {
      FurnitureTextEntity furnitureTextEntity = (FurnitureTextEntity)IFurniturePacketManager.Companion.getTextEntityIdMap().get(uuid);
      if (furnitureTextEntity != null) {
         IntArrayList intArrayList = furnitureTextEntity.getEntityIds();
         if (intArrayList != null) {
            IntArrayList entityIds = intArrayList;
            return (Packet)(new ClientboundRemoveEntitiesPacket((IntList)entityIds));
         }
      }

      return null;
   }

   private static final boolean findTargetFurnitureHitbox$lambda$3(net.minecraft.world.entity.Entity it) {
      return Intrinsics.areEqual(it.getType(), EntityUtilsKt.getEntityUtils().itemDisplayType());
   }

   private static final boolean findTargetFurnitureHitbox$lambda$4(Function1 tmp0, Object p0) {
      return (Boolean)tmp0.invoke(p0);
   }

   private static final ItemDisplay findTargetFurnitureHitbox$lambda$5(net.minecraft.world.entity.Entity it) {
      CraftEntity bukkitEntity = it.getBukkitEntity();
      return bukkitEntity instanceof ItemDisplay ? (ItemDisplay)bukkitEntity : null;
   }
}
