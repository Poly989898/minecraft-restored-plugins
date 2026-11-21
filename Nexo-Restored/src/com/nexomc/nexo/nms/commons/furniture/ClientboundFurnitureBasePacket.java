package com.nexomc.nexo.nms.commons.furniture;

import com.nexomc.nexo.mechanics.furniture.FurnitureBaseEntity;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.FurnitureProperties;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.nms.EntityMetadatas;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.ticxo.modelengine.api.ModelEngineAPI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Color;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0016\u0010\u0016\u001a\u0010\u0012\f\b\u0001\u0012\b\u0012\u0004\u0012\u00020\u00020\u00010\u0017H\u0016J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0002H\u0016J\u001a\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\"\u0010\u000f\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00110\u00102\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0003\u001a\u00020\u0004H\u0002J;\u0010\u001b\u001a\b\u0012\u0004\u0012\u0002H\u001c0\u0011\"\b\b\u0000\u0010\u001c*\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\f\u0010 \u001a\b\u0012\u0004\u0012\u0002H\u001c0!2\u0006\u0010\"\u001a\u0002H\u001cH\u0002¢\u0006\u0002\u0010#R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u000f\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00110\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0012\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015¨\u0006$"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/ClientboundFurnitureBasePacket;", "Lnet/minecraft/network/protocol/Packet;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "<init>", "(Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)V", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "furnitureBase", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureBaseEntity;", "dataValues", "", "Lnet/minecraft/network/syncher/SynchedEntityData$DataValue;", "metadataPacket", "Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;", "getMetadataPacket", "()Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;", "type", "Lnet/minecraft/network/protocol/PacketType;", "handle", "", "handler", "dataValue", "T", "", "id", "", "serializer", "Lnet/minecraft/network/syncher/EntityDataSerializer;", "value", "(ILnet/minecraft/network/syncher/EntityDataSerializer;Ljava/lang/Object;)Lnet/minecraft/network/syncher/SynchedEntityData$DataValue;", "com.nexomc:commons"}
)
public final class ClientboundFurnitureBasePacket implements Packet<ClientGamePacketListener> {
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureMechanic mechanic;
   @Nullable
   private final FurnitureBaseEntity furnitureBase;
   @NotNull
   private final List<SynchedEntityData.DataValue<?>> dataValues;
   @NotNull
   private final ClientboundSetEntityDataPacket metadataPacket;

   public ClientboundFurnitureBasePacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      List list;
      ClientboundFurnitureBasePacket clientboundFurnitureBasePacket;
      block: {
         /* implicit super() */
         this.baseEntity = baseEntity;
         this.mechanic = mechanic;
         this.furnitureBase = this.furnitureBase(this.baseEntity, this.mechanic);
         clientboundFurnitureBasePacket = this;
         FurnitureBaseEntity furnitureBaseEntity = this.furnitureBase;
         if (furnitureBaseEntity != null) {
            FurnitureBaseEntity it = furnitureBaseEntity;
            int value = 0;
            list = this.dataValues(it, this.baseEntity);
            clientboundFurnitureBasePacket = this;
            if (list != null) {
               break block;
            }
         }

         list = CollectionsKt.emptyList();
      }

      this.dataValues = list;
      this.metadataPacket = new ClientboundSetEntityDataPacket(this.baseEntity.getEntityId(), this.dataValues);
   }

   @NotNull
   public final ItemDisplay getBaseEntity() {
      return this.baseEntity;
   }

   @NotNull
   public final FurnitureMechanic getMechanic() {
      return this.mechanic;
   }

   @NotNull
   public final ClientboundSetEntityDataPacket getMetadataPacket() {
      return this.metadataPacket;
   }

   @NotNull
   public PacketType<? extends Packet<ClientGamePacketListener>> type() {
      PacketType packetType = GamePacketTypes.CLIENTBOUND_SET_ENTITY_DATA;
      return packetType;
   }

   public void handle(@NotNull ClientGamePacketListener handler) {
   }

   private final FurnitureBaseEntity furnitureBase(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      if (!baseEntity.isValid()) {
         return null;
      } else {
         return mechanic.isModelEngine() && ModelEngineAPI.getBlueprint(mechanic.getModelEngineID()) != null ? null : (FurnitureBaseEntity)CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getFurnitureBaseMap(), baseEntity.getUniqueId(), (() -> ClientboundFurnitureBasePacket.furnitureBase$lambda$0(baseEntity, mechanic)));
      }
   }

   private final List<SynchedEntityData.DataValue<?>> dataValues(FurnitureBaseEntity furnitureBase, ItemDisplay baseEntity) {
      List data = (List)(new ArrayList());
      FurnitureProperties properties = this.mechanic.getProperties();
      Transformation transformation2 = baseEntity.getTransformation();
      Transformation transformation = transformation2;
      Collection collection = (Collection)data;
      EntityDataSerializer entityDataSerializer = EntityDataSerializers.BYTE;
      collection.add(this.dataValue(0, entityDataSerializer, (byte)32));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.OPTIONAL_COMPONENT;
      Optional optional = Optional.of(Component.empty());
      collection.add(this.dataValue(2, entityDataSerializer, optional));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.BOOLEAN;
      collection.add(this.dataValue(3, entityDataSerializer, false));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.INT;
      collection.add(this.dataValue(8, entityDataSerializer, 0));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.INT;
      collection.add(this.dataValue(9, entityDataSerializer, DurationUtilsKt.getTicks_LRDsOJo(properties.getDelay_UwyO8pc())));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.FLOAT;
      collection.add(this.dataValue(20, entityDataSerializer, properties.getDisplayWidth()));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.FLOAT;
      collection.add(this.dataValue(21, entityDataSerializer, properties.getDisplayHeight()));
      collection = (Collection)data;
      Float number = properties.getViewRange();
      SynchedEntityData.DataValue dataValue;
      if (number != null) {
         float viewRange = ((Number)number).floatValue();
         int value = 0;
         entityDataSerializer = EntityDataSerializers.FLOAT;
         dataValue = this.dataValue(17, entityDataSerializer, viewRange);
      } else {
         dataValue = null;
      }

      collection.add(dataValue);
      collection = (Collection)data;
      Float number2 = properties.getShadowRadius();
      SynchedEntityData.DataValue dataValue2;
      if (number2 != null) {
         float shadowRadius = ((Number)number2).floatValue();
         int value2 = 0;
         entityDataSerializer = EntityDataSerializers.FLOAT;
         dataValue2 = this.dataValue(18, entityDataSerializer, shadowRadius);
      } else {
         dataValue2 = null;
      }

      collection.add(dataValue2);
      collection = (Collection)data;
      Float number3 = properties.getShadowStrength();
      SynchedEntityData.DataValue dataValue3;
      if (number3 != null) {
         float shadowStrength = ((Number)number3).floatValue();
         int value3 = 0;
         entityDataSerializer = EntityDataSerializers.FLOAT;
         dataValue3 = this.dataValue(19, entityDataSerializer, shadowStrength);
      } else {
         dataValue3 = null;
      }

      collection.add(dataValue3);
      collection = (Collection)data;
      Display.Billboard trackingRotation = properties.getTrackingRotation();
      SynchedEntityData.DataValue dataValue4;
      if (trackingRotation != null) {
         Display.Billboard tracking = trackingRotation;
         int value4 = 0;
         entityDataSerializer = EntityDataSerializers.BYTE;
         dataValue4 = this.dataValue(15, entityDataSerializer, (byte)tracking.ordinal());
      } else {
         dataValue4 = null;
      }

      collection.add(dataValue4);
      collection = (Collection)data;
      Display.Brightness brightness2 = properties.getBrightness();
      SynchedEntityData.DataValue dataValue5;
      if (brightness2 != null) {
         Display.Brightness brightness = brightness2;
         int value5 = 0;
         entityDataSerializer = EntityDataSerializers.INT;
         dataValue5 = this.dataValue(16, entityDataSerializer, brightness.getBlockLight() << 4 | brightness.getSkyLight() << 20);
      } else {
         dataValue5 = null;
      }

      collection.add(dataValue5);
      collection = (Collection)data;
      Color glowColor = properties.getGlowColor();
      SynchedEntityData.DataValue dataValue6;
      if (glowColor != null) {
         Color glow = glowColor;
         int value6 = 0;
         entityDataSerializer = EntityDataSerializers.INT;
         dataValue6 = this.dataValue(22, entityDataSerializer, glow.asRGB());
      } else {
         dataValue6 = null;
      }

      collection.add(dataValue6);
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.VECTOR3;
      Vector3f translation = transformation.getTranslation();
      Vector3f defaulT_TRANSLATION = EntityMetadatas.INSTANCE.getDEFAULT_TRANSLATION();
      EntityDataSerializer entityDataSerializer2 = entityDataSerializer;
      byte b = 11;
      int value7 = 0;
      boolean flag = defaulT_TRANSLATION.equals(translation);
      Vector3f vector3f = !flag ? translation : null;
      if ((!flag ? translation : null) == null) {
         vector3f = properties.getTranslation();
      }

      collection.add(this.dataValue(b, entityDataSerializer2, vector3f));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.VECTOR3;
      translation = transformation.getScale();
      defaulT_TRANSLATION = EntityMetadatas.INSTANCE.getDEFAULT_SCALE();
      entityDataSerializer2 = entityDataSerializer;
      b = 12;
      value7 = 0;
      flag = defaulT_TRANSLATION.equals(translation);
      vector3f = !flag ? translation : null;
      if ((!flag ? translation : null) == null) {
         vector3f = properties.getScale();
      }

      collection.add(this.dataValue(b, entityDataSerializer2, vector3f));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.QUATERNION;
      Quaternionf leftRotation = transformation.getLeftRotation();
      Quaternionf defaulT_ROTATION = EntityMetadatas.INSTANCE.getDEFAULT_ROTATION();
      entityDataSerializer2 = entityDataSerializer;
      b = 13;
      value7 = 0;
      flag = defaulT_ROTATION.equals(leftRotation);
      Quaternionf quaternionf = !flag ? leftRotation : null;
      if ((!flag ? leftRotation : null) == null) {
         quaternionf = properties.getLeftRotation();
      }

      collection.add(this.dataValue(b, entityDataSerializer2, quaternionf));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.QUATERNION;
      leftRotation = transformation.getRightRotation();
      defaulT_ROTATION = EntityMetadatas.INSTANCE.getDEFAULT_ROTATION();
      entityDataSerializer2 = entityDataSerializer;
      b = 14;
      value7 = 0;
      flag = defaulT_ROTATION.equals(leftRotation);
      quaternionf = !flag ? leftRotation : null;
      if ((!flag ? leftRotation : null) == null) {
         quaternionf = properties.getRightRotation();
      }

      collection.add(this.dataValue(b, entityDataSerializer2, quaternionf));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.ITEM_STACK;
      ItemStack itemStack = CraftItemStack.asNMSCopy(furnitureBase.getItemStack());
      collection.add(this.dataValue(23, entityDataSerializer, itemStack));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.BYTE;
      collection.add(this.dataValue(24, entityDataSerializer, (byte)properties.getDisplayTransform().ordinal()));
      return CollectionsKt.filterNotNull((Iterable)data);
   }

   private final <T> SynchedEntityData.DataValue<T> dataValue(int id, EntityDataSerializer<T> serializer, T value) {
      return new SynchedEntityData.DataValue(id, serializer, value);
   }

   private static final FurnitureBaseEntity furnitureBase$lambda$0(ItemDisplay itemDisplay, FurnitureMechanic furnitureMechanic) {
      return new FurnitureBaseEntity(itemDisplay, furnitureMechanic);
   }
}
