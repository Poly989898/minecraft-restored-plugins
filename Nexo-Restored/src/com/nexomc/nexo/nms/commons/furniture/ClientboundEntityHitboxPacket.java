package com.nexomc.nexo.nms.commons.furniture;

import com.nexomc.nexo.mechanics.furniture.FurnitureHitboxEntity;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacket;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.hitbox.GhastHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.InteractionHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.ShulkerHitbox;
import com.nexomc.nexo.nms.NMSHandler;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.nms.commons.CommonNMSHandler;
import com.nexomc.nexo.nms.commons.EntityUtils;
import com.nexomc.nexo.nms.commons.EntityUtilsKt;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.LocationUtilsKt;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \u001c2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001cB\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0016\u0010\u0011\u001a\u0010\u0012\f\b\u0001\u0012\b\u0012\u0004\u0012\u00020\u00020\u00010\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0002H\u0016J\u0013\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017H\u0002¢\u0006\u0002\u0010\u0019J\u0013\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017H\u0002¢\u0006\u0002\u0010\u0019J\u0013\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017H\u0002¢\u0006\u0002\u0010\u0019R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\r\u001a\u0004\u0018\u00010\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001d"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/ClientboundEntityHitboxPacket;", "Lnet/minecraft/network/protocol/Packet;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "<init>", "(Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)V", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "wrappedPacket", "Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "getWrappedPacket", "()Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "type", "Lnet/minecraft/network/protocol/PacketType;", "handle", "", "handler", "interactionPackets", "", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacket;", "()[Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacket;", "shulkerPackets", "ghastPackets", "Companion", "com.nexomc:commons"}
)
public final class ClientboundEntityHitboxPacket implements Packet<ClientGamePacketListener> {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureMechanic mechanic;
   @Nullable
   private final ClientboundBundlePacket wrappedPacket;

   public ClientboundEntityHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      super();
      this.baseEntity = baseEntity;
      this.mechanic = mechanic;
      ClientboundEntityHitboxPacket clientboundEntityHitboxPacket = this;
      Object[] objs = ArraysKt.plus(ArraysKt.plus(this.interactionPackets(), this.shulkerPackets()), this.ghastPackets());
      Object[] objs2;
      if (objs.length == 0) {
         int value = 0;
         objs2 = null;
         clientboundEntityHitboxPacket = this;
      } else {
         objs2 = objs;
      }

      objs2 = objs2;
      ClientboundBundlePacket clientboundBundlePacket;
      if ((IFurniturePacket[])objs2 != null) {
         IFurniturePacket[] iFurniturePackets = (IFurniturePacket[])objs2;
         ClientboundEntityHitboxPacket clientboundEntityHitboxPacket2 = clientboundEntityHitboxPacket;
         IFurniturePacket[] iFurniturePackets2 = iFurniturePackets;
         Collection destination = (Collection)(new ArrayList());
         int value2 = 0;

         for(int i = iFurniturePackets.length; value2 < i; ++value2) {
            Object element = iFurniturePackets2[value2];
            if (element instanceof FurnitureEntityHitboxPacket) {
               destination.add(element);
            }
         }

         List list = (List)destination;
         clientboundEntityHitboxPacket = clientboundEntityHitboxPacket2;
         clientboundBundlePacket = FurnitureEntityHitboxPacket.Companion.bundlePackets(list);
      } else {
         clientboundBundlePacket = null;
      }

      this.wrappedPacket = clientboundBundlePacket;
   }

   @NotNull
   public final ItemDisplay getBaseEntity() {
      return this.baseEntity;
   }

   @NotNull
   public final FurnitureMechanic getMechanic() {
      return this.mechanic;
   }

   @Nullable
   public final ClientboundBundlePacket getWrappedPacket() {
      return this.wrappedPacket;
   }

   @NotNull
   public PacketType<? extends Packet<ClientGamePacketListener>> type() {
      PacketType packetType = GamePacketTypes.CLIENTBOUND_BUNDLE;
      return packetType;
   }

   public void handle(@NotNull ClientGamePacketListener handler) {
   }

   private final IFurniturePacket[] interactionPackets() {
      ObjectOpenHashSet it = this.mechanic.getHitbox().getInteractions();
      int value = 0;
      ObjectOpenHashSet objectOpenHashSet = !it.isEmpty() && this.baseEntity.isValid() ? it : null;
      if (objectOpenHashSet == null) {
         return new IFurniturePacket[0];
      } else {
         ObjectOpenHashSet interactionHitboxes = objectOpenHashSet;
         if (this.mechanic.isModelEngine()) {
            ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(this.mechanic.getModelEngineID());
            if ((blueprint != null ? blueprint.getMainHitbox() : null) != null) {
               return new IFurniturePacket[0];
            }
         }

         return (IFurniturePacket[])CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getInteractionHitboxPacketMap(), this.baseEntity.getUniqueId(), (() -> ClientboundEntityHitboxPacket.interactionPackets$lambda$4(this, interactionHitboxes)));
      }
   }

   private final IFurniturePacket[] shulkerPackets() {
      ObjectOpenHashSet it = this.mechanic.getHitbox().getShulkers();
      int value = 0;
      ObjectOpenHashSet objectOpenHashSet = !it.isEmpty() && this.baseEntity.isValid() ? it : null;
      if (objectOpenHashSet == null) {
         return new IFurniturePacket[0];
      } else {
         ObjectOpenHashSet shulkerHitboxes = objectOpenHashSet;
         if (this.mechanic.isModelEngine()) {
            ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(this.mechanic.getModelEngineID());
            if ((blueprint != null ? blueprint.getMainHitbox() : null) != null) {
               return new IFurniturePacket[0];
            }
         }

         return (IFurniturePacket[])CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getShulkerHitboxPacketMap(), this.baseEntity.getUniqueId(), (() -> ClientboundEntityHitboxPacket.shulkerPackets$lambda$3(this, shulkerHitboxes)));
      }
   }

   private final IFurniturePacket[] ghastPackets() {
      ObjectOpenHashSet it = this.mechanic.getHitbox().getGhasts();
      int value = 0;
      ObjectOpenHashSet objectOpenHashSet = !it.isEmpty() && this.baseEntity.isValid() ? it : null;
      if (objectOpenHashSet == null) {
         return new IFurniturePacket[0];
      } else {
         ObjectOpenHashSet ghastHitboxes = objectOpenHashSet;
         if (this.mechanic.isModelEngine()) {
            ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(this.mechanic.getModelEngineID());
            if (blueprint != null && blueprint.getMainHitbox() != null) {
               return new IFurniturePacket[0];
            }
         }

         return (IFurniturePacket[])CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getGhastHitboxPacketMap(), this.baseEntity.getUniqueId(), (() -> ClientboundEntityHitboxPacket.ghastPackets$lambda$3(this, ghastHitboxes)));
      }
   }

   private static final IFurniturePacket[] interactionPackets$computeInteractionPackets(ClientboundEntityHitboxPacket clientboundEntityHitboxPacket, ObjectOpenHashSet<InteractionHitbox> interactionHitboxes) {
      Location baseLoc;
      float partialOffset;
      IntArrayList intArrayList;
      block: {
         Location location3 = clientboundEntityHitboxPacket.baseEntity.getLocation();
         int value = 0;
         location3.setY(location3.getY() - (double)0.5F);
         baseLoc = location3;
         Location location = clientboundEntityHitboxPacket.baseEntity.getLocation();
         BoundingBox boundingBox = BlockHelpers.toCenterBlockLocation(location).getBlock().getRelative(BlockFace.DOWN).getBoundingBox();
         BoundingBox belowBB = boundingBox;
         double height = belowBB.getHeight();
         partialOffset = (0.01 <= height ? height <= 0.99 : false) ? clientboundEntityHitboxPacket.mechanic.getProperties().getTranslation().y : 0.0F;
         UUID uniqueId = clientboundEntityHitboxPacket.baseEntity.getUniqueId();
         UUID uuid = uniqueId;
         FurnitureHitboxEntity furnitureHitboxEntity = (FurnitureHitboxEntity)IFurniturePacketManager.Companion.getInteractionHitboxIdMap().get(uuid);
         if (furnitureHitboxEntity != null) {
            intArrayList = furnitureHitboxEntity.getEntityIds();
            if (intArrayList != null) {
               break block;
            }
         }

         int value2 = 0;
         IntArrayList newEntityIds = new IntArrayList(interactionHitboxes.size());
         int i = 0;

         for(int j = ((Collection)interactionHitboxes).size(); i < j; ++i) {
            Collection collection = (Collection)newEntityIds;
            EntityUtils entityUtils = EntityUtilsKt.getEntityUtils();
            World world = clientboundEntityHitboxPacket.baseEntity.getWorld();
            collection.add(entityUtils.nextEntityId(world));
         }

         Collection collection2 = (Collection)newEntityIds;
         Iterable hitbox = (Iterable)interactionHitboxes;
         Collection collection3 = collection2;
         ItemDisplay itemDisplay = clientboundEntityHitboxPacket.baseEntity;
         FurnitureMechanic furnitureMechanic = clientboundEntityHitboxPacket.mechanic;
         Collection destination = (Collection)(new ObjectArrayList((hitbox instanceof Collection ? (Collection)hitbox : null) != null ? (hitbox instanceof Collection ? (Collection)hitbox : null).size() : 10));

         for(Object item : hitbox) {
            InteractionHitbox it = (InteractionHitbox)item;
            int value3 = 0;
            Location toCenterLocation = baseLoc.toCenterLocation();
            destination.add(it.boundingBox(LocationUtilsKt.add(toCenterLocation, it.offset(clientboundEntityHitboxPacket.baseEntity.getYaw()))));
         }

         ObjectArrayList objectArrayList = (ObjectArrayList)destination;
         List list = (List)objectArrayList;
         FurnitureHitboxEntity furnitureHitboxEntity2 = new FurnitureHitboxEntity(furnitureMechanic, itemDisplay, collection3, list);
         int value4 = 0;
         IFurniturePacketManager.Companion.getInteractionHitboxIdMap().put(uuid, furnitureHitboxEntity2);
         intArrayList = furnitureHitboxEntity2.getEntityIds();
      }

      IntArrayList entityIds = intArrayList;
      int i = entityIds.size();

      for(int k = interactionHitboxes.size(); i < k; ++i) {
         EntityUtils entityUtils2 = EntityUtilsKt.getEntityUtils();
         World world2 = clientboundEntityHitboxPacket.baseEntity.getWorld();
         entityIds.add(entityUtils2.nextEntityId(world2));
      }

      Iterable iterable = (Iterable)interactionHitboxes;
      Object[] result = new IFurniturePacket[CollectionsKt.count(iterable)];
      int index = 0;

      for(Object obj : iterable) {
         InteractionHitbox interactionHitbox = (InteractionHitbox)obj;
         int value5 = 0;
         int entityId = entityIds.getInt(index);
         Location location4 = LocationUtilsKt.plus(baseLoc, interactionHitbox.offset(clientboundEntityHitboxPacket.baseEntity.getYaw()));
         int value6 = 0;
         Location location2 = location4;
         double y = location4.getY();
         float value7;
         if (clientboundEntityHitboxPacket.mechanic.getProperties().isFixedTransform()) {
            float it = clientboundEntityHitboxPacket.baseEntity.getTransformation().getTranslation().z;
            double value8 = y;
            int value9 = 0;
            float value10 = location4.getPitch() == 90.0F ? -it : it;
            location2 = location4;
            y = value8;
            value7 = value10;
         } else {
            value7 = clientboundEntityHitboxPacket.baseEntity.getTransformation().getTranslation().y;
         }

         location2.setY(y + (double)(value7 - partialOffset));
         ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(entityId, UUID.randomUUID(), location4.x(), location4.y(), location4.z(), location4.getPitch(), location4.getYaw(), EntityUtilsKt.getEntityUtils().interactionType(), 1, Vec3.ZERO, (double)0.0F);
         SynchedEntityData.DataValue[] dataValues = new SynchedEntityData.DataValue[]{new SynchedEntityData.DataValue(8, EntityDataSerializers.FLOAT, interactionHitbox.getWidth()), new SynchedEntityData.DataValue(9, EntityDataSerializers.FLOAT, interactionHitbox.getHeight())};
         ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(entityId, (List)ObjectArrayList.of(dataValues));
         IFurniturePacket iFurniturePacket = new FurnitureEntityHitboxPacket(entityId, addEntityPacket, metadataPacket, (ClientboundUpdateAttributesPacket)null);
         int value11 = index;
         int value12 = 0;
         ++index;
         result[value11] = iFurniturePacket;
      }

      return (IFurniturePacket[])(result);
   }

   private static final IFurniturePacket[] shulkerPackets$computeShulkerPackets(ClientboundEntityHitboxPacket clientboundEntityHitboxPacket, ObjectOpenHashSet<ShulkerHitbox> shulkerHitboxes) {
      CommonNMSHandler nmsHandler;
      Location baseLoc;
      IntArrayList intArrayList;
      block: {
         UUID uniqueId = clientboundEntityHitboxPacket.baseEntity.getUniqueId();
         UUID uuid = uniqueId;
         NMSHandler handler = NMSHandlers.handler();
         nmsHandler = (CommonNMSHandler)handler;
         Location location = clientboundEntityHitboxPacket.baseEntity.getLocation();
         baseLoc = location;
         FurnitureHitboxEntity furnitureHitboxEntity = (FurnitureHitboxEntity)IFurniturePacketManager.Companion.getShulkerHitboxIdMap().get(uuid);
         if (furnitureHitboxEntity != null) {
            intArrayList = furnitureHitboxEntity.getEntityIds();
            if (intArrayList != null) {
               break block;
            }
         }

         int value = 0;
         IntArrayList newEntityIds = new IntArrayList(shulkerHitboxes.size());
         int i = 0;

         for(int k = ((Collection)shulkerHitboxes).size(); i < k; ++i) {
            Collection collection = (Collection)newEntityIds;
            EntityUtils entityUtils = EntityUtilsKt.getEntityUtils();
            World world = clientboundEntityHitboxPacket.baseEntity.getWorld();
            collection.add(entityUtils.nextEntityId(world));
         }

         Iterable iterable = (Iterable)shulkerHitboxes;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            ShulkerHitbox it = (ShulkerHitbox)item;
            int value2 = 0;
            Location location2 = baseLoc.clone();
            destination.add(it.boundingBox(LocationUtilsKt.add(location2, it.offset(clientboundEntityHitboxPacket.baseEntity.getYaw()))));
         }

         List boundingBoxes = (List)destination;
         FurnitureHitboxEntity furnitureHitboxEntity2 = new FurnitureHitboxEntity(clientboundEntityHitboxPacket.mechanic, clientboundEntityHitboxPacket.baseEntity, (Collection)newEntityIds, boundingBoxes);
         int value12 = 0;
         IFurniturePacketManager.Companion.getShulkerHitboxIdMap().put(uuid, furnitureHitboxEntity2);
         intArrayList = furnitureHitboxEntity2.getEntityIds();
      }

      IntArrayList entityIds = intArrayList;
      int i = entityIds.size();

      for(int j = shulkerHitboxes.size(); i < j; ++i) {
         EntityUtils entityUtils2 = EntityUtilsKt.getEntityUtils();
         World world2 = clientboundEntityHitboxPacket.baseEntity.getWorld();
         entityIds.add(entityUtils2.nextEntityId(world2));
      }

      Iterable iterable2 = (Iterable)shulkerHitboxes;
      Object[] result = new IFurniturePacket[CollectionsKt.count(iterable2)];
      int index = 0;

      for(Object obj : iterable2) {
         ShulkerHitbox shulkerHitbox = (ShulkerHitbox)obj;
         int value3 = 0;
         int entityId = entityIds.getInt(index);
         Direction direction = nmsHandler.nearestDirection(shulkerHitbox.direction(clientboundEntityHitboxPacket.baseEntity.getYaw())).getOpposite();
         Location metadataPacket = baseLoc.toCenterLocation().subtract((double)0.0F, (double)0.5F, (double)0.0F);
         int value4 = 0;
         Location location3 = metadataPacket;
         double y = metadataPacket.getY();
         float value5;
         if (clientboundEntityHitboxPacket.mechanic.getProperties().isFixedTransform()) {
            float it = clientboundEntityHitboxPacket.baseEntity.getTransformation().getTranslation().z;
            double value6 = y;
            int value7 = 0;
            float value8 = metadataPacket.getPitch() == 90.0F ? it : -it;
            location3 = metadataPacket;
            y = value6;
            value5 = value8;
         } else {
            value5 = Math.abs(clientboundEntityHitboxPacket.baseEntity.getTransformation().getTranslation().y);
         }

         location3.setY(y - (double)value5);
         Location loc = LocationUtilsKt.add(metadataPacket, shulkerHitbox.offset(clientboundEntityHitboxPacket.baseEntity.getYaw()));
         ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(entityId, UUID.randomUUID(), loc.x(), loc.y(), loc.z(), loc.getPitch(), loc.getYaw(), EntityUtilsKt.getEntityUtils().shulkerType(), 1, Vec3.ZERO, (double)0.0F);
         SynchedEntityData.DataValue[] attribute = new SynchedEntityData.DataValue[]{new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, shulkerHitbox.getVisible() ? 0 : 32), new SynchedEntityData.DataValue(15, EntityDataSerializers.BYTE, (byte)2), new SynchedEntityData.DataValue(16, EntityDataSerializers.DIRECTION, direction), new SynchedEntityData.DataValue(17, EntityDataSerializers.BYTE, (byte)((int)(shulkerHitbox.getLength() * (double)100) - 100))};
         ClientboundSetEntityDataPacket metadataPacket_4 = new ClientboundSetEntityDataPacket(entityId, (List)ObjectArrayList.of(attribute));
         AttributeInstance attributesPacket = new AttributeInstance(Attributes.SCALE, ClientboundEntityHitboxPacket::shulkerPackets$computeShulkerPackets$lambda$2$1);
         int value9 = 0;
         attributesPacket.setBaseValue(shulkerHitbox.getScale());
         ClientboundUpdateAttributesPacket attributesPacket_3 = new ClientboundUpdateAttributesPacket(entityId, (Collection)CollectionsKt.listOf(attributesPacket));
         IFurniturePacket iFurniturePacket = new FurnitureEntityHitboxPacket(entityId, addEntityPacket, metadataPacket_4, attributesPacket_3);
         int value10 = index;
         int value11 = 0;
         ++index;
         result[value10] = iFurniturePacket;
      }

      return (IFurniturePacket[])(result);
   }

   private static final IFurniturePacket[] ghastPackets$computeGhastPackets(ClientboundEntityHitboxPacket clientboundEntityHitboxPacket, ObjectOpenHashSet<GhastHitbox> ghastHitboxes) {
      Location baseLoc;
      IntArrayList intArrayList;
      block: {
         UUID uniqueId = clientboundEntityHitboxPacket.baseEntity.getUniqueId();
         UUID uuid = uniqueId;
         Location location = clientboundEntityHitboxPacket.baseEntity.getLocation();
         baseLoc = location;
         FurnitureHitboxEntity furnitureHitboxEntity = (FurnitureHitboxEntity)IFurniturePacketManager.Companion.getGhastHitboxIdMap().get(uuid);
         if (furnitureHitboxEntity != null) {
            intArrayList = furnitureHitboxEntity.getEntityIds();
            if (intArrayList != null) {
               break block;
            }
         }

         int value = 0;
         IntArrayList newEntityIds = new IntArrayList(ghastHitboxes.size());
         int i = 0;

         for(int k = ((Collection)ghastHitboxes).size(); i < k; ++i) {
            Collection collection = (Collection)newEntityIds;
            EntityUtils entityUtils = EntityUtilsKt.getEntityUtils();
            World world = clientboundEntityHitboxPacket.baseEntity.getWorld();
            collection.add(entityUtils.nextEntityId(world));
         }

         Iterable iterable = (Iterable)ghastHitboxes;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            GhastHitbox it = (GhastHitbox)item;
            int value2 = 0;
            Location location2 = baseLoc.clone();
            destination.add(it.boundingBox(LocationUtilsKt.add(location2, it.offset(clientboundEntityHitboxPacket.baseEntity.getYaw()))));
         }

         List boundingBoxes = (List)destination;
         FurnitureHitboxEntity furnitureHitboxEntity2 = new FurnitureHitboxEntity(clientboundEntityHitboxPacket.mechanic, clientboundEntityHitboxPacket.baseEntity, (Collection)newEntityIds, boundingBoxes);
         int value11 = 0;
         IFurniturePacketManager.Companion.getGhastHitboxIdMap().put(uuid, furnitureHitboxEntity2);
         intArrayList = furnitureHitboxEntity2.getEntityIds();
      }

      IntArrayList entityIds = intArrayList;
      int i = entityIds.size();

      for(int j = ghastHitboxes.size(); i < j; ++i) {
         EntityUtils entityUtils2 = EntityUtilsKt.getEntityUtils();
         World world2 = clientboundEntityHitboxPacket.baseEntity.getWorld();
         entityIds.add(entityUtils2.nextEntityId(world2));
      }

      Iterable iterable2 = (Iterable)ghastHitboxes;
      Object[] result = new IFurniturePacket[CollectionsKt.count(iterable2)];
      int index = 0;

      for(Object obj : iterable2) {
         GhastHitbox ghastHitbox = (GhastHitbox)obj;
         int value3 = 0;
         int entityId = entityIds.getInt(index);
         Location metadataPacket = baseLoc.toCenterLocation().subtract((double)0.0F, (double)0.5F, (double)0.0F);
         int attributesPacket = 0;
         Location location3 = metadataPacket;
         double y = metadataPacket.getY();
         float value4;
         if (clientboundEntityHitboxPacket.mechanic.getProperties().isFixedTransform()) {
            float it = clientboundEntityHitboxPacket.baseEntity.getTransformation().getTranslation().z;
            double value5 = y;
            int value6 = 0;
            float value7 = metadataPacket.getPitch() == 90.0F ? it : -it;
            location3 = metadataPacket;
            y = value5;
            value4 = value7;
         } else {
            value4 = Math.abs(clientboundEntityHitboxPacket.baseEntity.getTransformation().getTranslation().y);
         }

         location3.setY(y - (double)value4);
         Location loc = LocationUtilsKt.add(metadataPacket, ghastHitbox.offset(baseLoc.getYaw()));
         ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(entityId, UUID.randomUUID(), loc.x(), loc.y(), loc.z(), loc.getPitch(), loc.getYaw() + ghastHitbox.getRotation(), EntityUtilsKt.getEntityUtils().happyGhastType(), 1, Vec3.ZERO, (double)0.0F);
         SynchedEntityData.DataValue[] dataValues = new SynchedEntityData.DataValue[]{new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, ghastHitbox.getVisible() ? 0 : 32), new SynchedEntityData.DataValue(15, EntityDataSerializers.BYTE, (byte)2)};
         ClientboundSetEntityDataPacket metadataPacket_2 = new ClientboundSetEntityDataPacket(entityId, (List)ObjectArrayList.of(dataValues));
         AttributeInstance attributeInstance = new AttributeInstance(Attributes.SCALE, ClientboundEntityHitboxPacket::ghastPackets$computeGhastPackets$lambda$2$1);
         int value8 = 0;
         attributeInstance.setBaseValue(ghastHitbox.getScale());
         ClientboundUpdateAttributesPacket attributesPacket_1 = new ClientboundUpdateAttributesPacket(entityId, (Collection)CollectionsKt.listOf(attributeInstance));
         IFurniturePacket iFurniturePacket = new FurnitureEntityHitboxPacket(entityId, addEntityPacket, metadataPacket_2, attributesPacket_1);
         int value9 = index;
         int value10 = 0;
         ++index;
         result[value9] = iFurniturePacket;
      }

      return (IFurniturePacket[])(result);
   }

   private static final IFurniturePacket[] interactionPackets$lambda$4(ClientboundEntityHitboxPacket clientboundEntityHitboxPacket, ObjectOpenHashSet interactionHitboxes) {
      return interactionPackets$computeInteractionPackets(clientboundEntityHitboxPacket, interactionHitboxes);
   }

   private static final IFurniturePacket[] shulkerPackets$lambda$3(ClientboundEntityHitboxPacket clientboundEntityHitboxPacket, ObjectOpenHashSet shulkerHitboxes) {
      return shulkerPackets$computeShulkerPackets(clientboundEntityHitboxPacket, shulkerHitboxes);
   }

   private static final IFurniturePacket[] ghastPackets$lambda$3(ClientboundEntityHitboxPacket clientboundEntityHitboxPacket, ObjectOpenHashSet ghastHitboxes) {
      return ghastPackets$computeGhastPackets(clientboundEntityHitboxPacket, ghastHitboxes);
   }

   private static final void shulkerPackets$computeShulkerPackets$lambda$2$1(AttributeInstance it) {
   }

   private static final void ghastPackets$computeGhastPackets$lambda$2$1(AttributeInstance it) {
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/ClientboundEntityHitboxPacket$Companion;", "", "<init>", "()V", "precomputeHitboxPackets", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "com.nexomc:commons"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final void precomputeHitboxPackets(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         if (baseEntity.isValid()) {
            new ClientboundEntityHitboxPacket(baseEntity, mechanic);
         }
      }
   }
}
