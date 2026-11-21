package com.nexomc.nexo.nms.commons.furniture;

import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.FurnitureProperties;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacket;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.text_entity.FurnitureTextEntity;
import com.nexomc.nexo.mechanics.furniture.text_entity.TextEntityMechanic;
import com.nexomc.nexo.nms.EntityMetadatas;
import com.nexomc.nexo.nms.commons.EntityUtils;
import com.nexomc.nexo.nms.commons.EntityUtilsKt;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.LocationUtilsKt;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 (2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001(B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0016\u0010\u0011\u001a\u0010\u0012\f\b\u0001\u0012\b\u0012\u0004\u0012\u00020\u00020\u00010\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0002H\u0016J\u0013\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017H\u0002¢\u0006\u0002\u0010\u0019J\"\u0010\u001a\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u001c0\u001b2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0003\u001a\u00020\u0004H\u0002J;\u0010\u001f\u001a\b\u0012\u0004\u0012\u0002H 0\u001c\"\b\b\u0000\u0010 *\u00020!2\u0006\u0010\"\u001a\u00020#2\f\u0010$\u001a\b\u0012\u0004\u0012\u0002H 0%2\u0006\u0010&\u001a\u0002H H\u0002¢\u0006\u0002\u0010'R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\r\u001a\u0004\u0018\u00010\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006)"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/ClientboundFurnitureTextEntityPacket;", "Lnet/minecraft/network/protocol/Packet;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "<init>", "(Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)V", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "wrappedPacket", "Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "getWrappedPacket", "()Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "type", "Lnet/minecraft/network/protocol/PacketType;", "handle", "", "handler", "textEntityPackets", "", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacket;", "()[Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacket;", "dataValues", "", "Lnet/minecraft/network/syncher/SynchedEntityData$DataValue;", "textEntity", "Lcom/nexomc/nexo/mechanics/furniture/text_entity/TextEntityMechanic;", "dataValue", "T", "", "id", "", "serializer", "Lnet/minecraft/network/syncher/EntityDataSerializer;", "value", "(ILnet/minecraft/network/syncher/EntityDataSerializer;Ljava/lang/Object;)Lnet/minecraft/network/syncher/SynchedEntityData$DataValue;", "Companion", "com.nexomc:commons"}
)
public final class ClientboundFurnitureTextEntityPacket implements Packet<ClientGamePacketListener> {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureMechanic mechanic;
   @Nullable
   private final ClientboundBundlePacket wrappedPacket;

   public ClientboundFurnitureTextEntityPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      super();
      this.baseEntity = baseEntity;
      this.mechanic = mechanic;
      ClientboundFurnitureTextEntityPacket clientboundFurnitureTextEntityPacket = this;
      IFurniturePacket[] textEntityPackets = this.textEntityPackets();
      IFurniturePacket[] iFurniturePackets;
      if (textEntityPackets.length == 0) {
         int value = 0;
         iFurniturePackets = null;
         clientboundFurnitureTextEntityPacket = this;
      } else {
         iFurniturePackets = textEntityPackets;
      }

      ClientboundBundlePacket clientboundBundlePacket;
      if (iFurniturePackets != null) {
         IFurniturePacket[] iFurniturePackets2 = iFurniturePackets;
         ClientboundFurnitureTextEntityPacket clientboundFurnitureTextEntityPacket2 = clientboundFurnitureTextEntityPacket;
         IFurniturePacket[] iFurniturePackets3 = iFurniturePackets2;
         Collection destination = (Collection)(new ArrayList());
         int value2 = 0;

         for(int i = iFurniturePackets2.length; value2 < i; ++value2) {
            Object element = iFurniturePackets3[value2];
            if (element instanceof FurnitureEntityHitboxPacket) {
               destination.add(element);
            }
         }

         List list = (List)destination;
         clientboundFurnitureTextEntityPacket = clientboundFurnitureTextEntityPacket2;
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

   private final IFurniturePacket[] textEntityPackets() {
      List it = this.mechanic.getTextEntities().getTextEntities();
      int value = 0;
      List list = !it.isEmpty() && this.baseEntity.isValid() ? it : null;
      if (list == null) {
         return new IFurniturePacket[0];
      } else {
         List textEntities = list;
         return (IFurniturePacket[])CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getTextEntityPacketMap(), this.baseEntity.getUniqueId(), (() -> ClientboundFurnitureTextEntityPacket.textEntityPackets$lambda$3(this, textEntities)));
      }
   }

   private final List<SynchedEntityData.DataValue<?>> dataValues(TextEntityMechanic textEntity, ItemDisplay baseEntity) {
      List data = (List)(new ArrayList());
      FurnitureProperties properties = textEntity.getProperties();
      Transformation transformation2 = baseEntity.getTransformation();
      Transformation transformation = transformation2;
      Collection collection = (Collection)data;
      EntityDataSerializer entityDataSerializer = EntityDataSerializers.BYTE;
      collection.add(this.dataValue(0, entityDataSerializer, (byte)32));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.OPTIONAL_COMPONENT;
      Optional optional = Optional.of(PaperAdventure.asVanilla((Component)Component.empty()));
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
      Quaternionf thisCollection = EntityMetadatas.INSTANCE.getDEFAULT_ROTATION();
      entityDataSerializer2 = entityDataSerializer;
      b = 13;
      value7 = 0;
      flag = thisCollection.equals(leftRotation);
      Quaternionf quaternionf = !flag ? leftRotation : null;
      if ((!flag ? leftRotation : null) == null) {
         quaternionf = properties.getLeftRotation();
      }

      collection.add(this.dataValue(b, entityDataSerializer2, quaternionf));
      collection = (Collection)data;
      entityDataSerializer = EntityDataSerializers.QUATERNION;
      leftRotation = transformation.getRightRotation();
      thisCollection = EntityMetadatas.INSTANCE.getDEFAULT_ROTATION();
      entityDataSerializer2 = entityDataSerializer;
      b = 14;
      value7 = 0;
      flag = thisCollection.equals(leftRotation);
      quaternionf = !flag ? leftRotation : null;
      if ((!flag ? leftRotation : null) == null) {
         quaternionf = properties.getRightRotation();
      }

      collection.add(this.dataValue(b, entityDataSerializer2, quaternionf));
      Collection collection3 = (Collection)textEntity.getText();
      Component[] components = (Component[])collection3.toArray(new Component[0]);
      TextComponent textComponent = Component.textOfChildren((ComponentLike[])Arrays.copyOf(components, components.length));
      TextComponent text = textComponent;
      Collection collection2 = (Collection)data;
      entityDataSerializer = EntityDataSerializers.COMPONENT;
      net.minecraft.network.chat.Component component = PaperAdventure.asVanilla((Component)text);
      collection2.add(this.dataValue(23, entityDataSerializer, component));
      collection2 = (Collection)data;
      entityDataSerializer = EntityDataSerializers.INT;
      collection2.add(this.dataValue(24, entityDataSerializer, textEntity.getLineWidth()));
      collection2 = (Collection)data;
      entityDataSerializer = EntityDataSerializers.INT;
      collection2.add(this.dataValue(25, entityDataSerializer, textEntity.getBackgroundColor().asARGB()));
      collection2 = (Collection)data;
      entityDataSerializer = EntityDataSerializers.BYTE;
      collection2.add(this.dataValue(26, entityDataSerializer, (byte)textEntity.getTextOpacity()));
      byte flags = 0;
      if (textEntity.getShadow()) {
         flags = (byte)(flags | 1);
      }

      if (textEntity.getSeeThrough()) {
         flags = (byte)(flags | 2);
      }

      byte b2;
      switch (ClientboundFurnitureTextEntityPacket.WhenMappings.$EnumSwitchMapping$0[textEntity.getAlignment().ordinal()]) {
         case 1 -> b2 = 1;
         case 2 -> b2 = 2;
         default -> b2 = 0;
      }

      int alignmentValue = b2 << 3;
      flags = (byte)(flags | (byte)alignmentValue);
      collection3 = (Collection)data;
      entityDataSerializer = EntityDataSerializers.BYTE;
      collection3.add(this.dataValue(27, entityDataSerializer, flags));
      return CollectionsKt.filterNotNull((Iterable)data);
   }

   private final <T> SynchedEntityData.DataValue<T> dataValue(int id, EntityDataSerializer<T> serializer, T value) {
      return new SynchedEntityData.DataValue(id, serializer, value);
   }

   private static final IFurniturePacket[] textEntityPackets$computeTextEntityPackets(ClientboundFurnitureTextEntityPacket clientboundFurnitureTextEntityPacket, List<TextEntityMechanic> textEntities) {
      Location baseLoc;
      Location baseBlockLoc;
      float partialOffset;
      IntArrayList intArrayList;
      block: {
         Location location = clientboundFurnitureTextEntityPacket.baseEntity.getLocation();
         baseLoc = location;
         location = baseLoc.set((double)baseLoc.getBlockX() + (double)0.5F, (double)baseLoc.getBlockY(), (double)baseLoc.getBlockZ() + (double)0.5F);
         baseBlockLoc = location;
         BoundingBox boundingBox = baseBlockLoc.getBlock().getRelative(BlockFace.DOWN).getBoundingBox();
         BoundingBox belowBB = boundingBox;
         double height = belowBB.getHeight();
         partialOffset = (0.01 <= height ? height <= 0.99 : false) ? clientboundFurnitureTextEntityPacket.mechanic.getProperties().getTranslation().y : 0.0F;
         UUID uniqueId = clientboundFurnitureTextEntityPacket.baseEntity.getUniqueId();
         UUID uuid = uniqueId;
         FurnitureTextEntity furnitureTextEntity = (FurnitureTextEntity)IFurniturePacketManager.Companion.getTextEntityIdMap().get(uuid);
         if (furnitureTextEntity != null) {
            intArrayList = furnitureTextEntity.getEntityIds();
            if (intArrayList != null) {
               break block;
            }
         }

         int value = 0;
         IntArrayList newEntityIds = new IntArrayList(textEntities.size());
         int i = 0;

         for(int j = ((Collection)textEntities).size(); i < j; ++i) {
            EntityUtils entityUtils = EntityUtilsKt.getEntityUtils();
            World world = clientboundFurnitureTextEntityPacket.baseEntity.getWorld();
            newEntityIds.add(entityUtils.nextEntityId(world));
         }

         FurnitureTextEntity furnitureTextEntity2 = new FurnitureTextEntity(clientboundFurnitureTextEntityPacket.mechanic, clientboundFurnitureTextEntityPacket.baseEntity, (Collection)newEntityIds);
         int value2 = 0;
         IFurniturePacketManager.Companion.getTextEntityIdMap().put(uuid, furnitureTextEntity2);
         intArrayList = furnitureTextEntity2.getEntityIds();
      }

      IntArrayList entityIds = intArrayList;
      int i = entityIds.size();

      for(int k = textEntities.size(); i < k; ++i) {
         EntityUtils entityUtils2 = EntityUtilsKt.getEntityUtils();
         World world2 = clientboundFurnitureTextEntityPacket.baseEntity.getWorld();
         entityIds.add(entityUtils2.nextEntityId(world2));
      }

      Iterable iterable = (Iterable)textEntities;
      Object[] result = new IFurniturePacket[CollectionsKt.count(iterable)];
      int index = 0;

      for(Object item : iterable) {
         TextEntityMechanic textEntityMechanic = (TextEntityMechanic)item;
         int value3 = 0;
         int entityId = entityIds.getInt(index);
         Location location3 = LocationUtilsKt.plus(baseBlockLoc, textEntityMechanic.offset(baseLoc.getYaw()));
         int value4 = 0;
         Location location2 = location3;
         double y = location3.getY();
         float value5;
         if (clientboundFurnitureTextEntityPacket.mechanic.getProperties().isFixedTransform()) {
            float it = clientboundFurnitureTextEntityPacket.baseEntity.getTransformation().getTranslation().z;
            double value6 = y;
            int value7 = 0;
            float value8 = location3.getPitch() == 90.0F ? -it : it;
            location2 = location3;
            y = value6;
            value5 = value8;
         } else {
            value5 = clientboundFurnitureTextEntityPacket.baseEntity.getTransformation().getTranslation().y;
         }

         location2.setY(y + (double)(value5 - partialOffset));
         ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(entityId, UUID.randomUUID(), location3.x(), location3.y(), location3.z(), location3.getPitch(), location3.getYaw(), EntityUtilsKt.getEntityUtils().textDisplayType(), 1, Vec3.ZERO, (double)0.0F);
         ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(entityId, clientboundFurnitureTextEntityPacket.dataValues(textEntityMechanic, clientboundFurnitureTextEntityPacket.baseEntity));
         IFurniturePacket iFurniturePacket = new FurnitureEntityHitboxPacket(entityId, addEntityPacket, metadataPacket, (ClientboundUpdateAttributesPacket)null);
         int value9 = index;
         int value10 = 0;
         ++index;
         result[value9] = iFurniturePacket;
      }

      return (IFurniturePacket[])(result);
   }

   private static final IFurniturePacket[] textEntityPackets$lambda$3(ClientboundFurnitureTextEntityPacket clientboundFurnitureTextEntityPacket, List textEntities) {
      return textEntityPackets$computeTextEntityPackets(clientboundFurnitureTextEntityPacket, textEntities);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/ClientboundFurnitureTextEntityPacket$Companion;", "", "<init>", "()V", "precomputeTextEntityPackets", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "com.nexomc:commons"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final void precomputeTextEntityPackets(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         if (baseEntity.isValid()) {
            new ClientboundFurnitureTextEntityPacket(baseEntity, mechanic);
         }
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.entity.TextDisplay.TextAlignment.values().length];
         try { a_0[org.bukkit.entity.TextDisplay.TextAlignment.LEFT.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.entity.TextDisplay.TextAlignment.RIGHT.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
