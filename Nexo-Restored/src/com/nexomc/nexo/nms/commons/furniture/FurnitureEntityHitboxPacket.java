package com.nexomc.nexo.nms.commons.furniture;

import com.nexomc.nexo.mechanics.furniture.IFurniturePacket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0014\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\n\u0010\u000bR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001c\u0010\b\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001b¨\u0006\u001d"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/FurnitureEntityHitboxPacket;", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacket;", "entityId", "", "addEntity", "Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;", "metadata", "Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;", "attributesPacket", "Lnet/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket;", "<init>", "(ILnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;Lnet/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket;)V", "getEntityId", "()I", "setEntityId", "(I)V", "getAddEntity", "()Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;", "setAddEntity", "(Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;)V", "getMetadata", "()Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;", "setMetadata", "(Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;)V", "getAttributesPacket", "()Lnet/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket;", "setAttributesPacket", "(Lnet/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket;)V", "Companion", "com.nexomc:commons"}
)
public final class FurnitureEntityHitboxPacket implements IFurniturePacket {
   @NotNull
   public static final Companion Companion = new Companion();
   private int entityId;
   @NotNull
   private ClientboundAddEntityPacket addEntity;
   @NotNull
   private ClientboundSetEntityDataPacket metadata;
   @Nullable
   private ClientboundUpdateAttributesPacket attributesPacket;

   public FurnitureEntityHitboxPacket(int entityId, @NotNull ClientboundAddEntityPacket addEntity, @NotNull ClientboundSetEntityDataPacket metadata, @Nullable ClientboundUpdateAttributesPacket attributesPacket) {
      super();
      this.entityId = entityId;
      this.addEntity = addEntity;
      this.metadata = metadata;
      this.attributesPacket = attributesPacket;
   }

   public final int getEntityId() {
      return this.entityId;
   }

   public final void setEntityId(int value) {
      this.entityId = value;
   }

   @NotNull
   public final ClientboundAddEntityPacket getAddEntity() {
      return this.addEntity;
   }

   public final void setAddEntity(@NotNull ClientboundAddEntityPacket clientboundAddEntityPacket) {
      this.addEntity = clientboundAddEntityPacket;
   }

   @NotNull
   public final ClientboundSetEntityDataPacket getMetadata() {
      return this.metadata;
   }

   public final void setMetadata(@NotNull ClientboundSetEntityDataPacket clientboundSetEntityDataPacket) {
      this.metadata = clientboundSetEntityDataPacket;
   }

   @Nullable
   public final ClientboundUpdateAttributesPacket getAttributesPacket() {
      return this.attributesPacket;
   }

   public final void setAttributesPacket(@Nullable ClientboundUpdateAttributesPacket clientboundUpdateAttributesPacket) {
      this.attributesPacket = clientboundUpdateAttributesPacket;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\b\u0012\u0004\u0012\u00020\u00070\u0006¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/FurnitureEntityHitboxPacket$Companion;", "", "<init>", "()V", "bundlePackets", "Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "", "Lcom/nexomc/nexo/nms/commons/furniture/FurnitureEntityHitboxPacket;", "com.nexomc:commons"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final ClientboundBundlePacket bundlePackets(@NotNull List<FurnitureEntityHitboxPacket> furnitureEntityHitboxPackets) {
         Iterable iterable2 = (Iterable)furnitureEntityHitboxPackets;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable2) {
            FurnitureEntityHitboxPacket it = (FurnitureEntityHitboxPacket)element;
            int value = 0;
            Packet[] packets = new Packet[4];
            int[] ints = new int[]{it.getEntityId()};
            packets[0] = new ClientboundRemoveEntitiesPacket(ints);
            packets[1] = it.getAddEntity();
            packets[2] = it.getMetadata();
            packets[3] = it.getAttributesPacket();
            Iterable list = (Iterable)CollectionsKt.listOfNotNull(packets);
            CollectionsKt.addAll(destination, list);
         }

         Iterable iterable = (Iterable)((List)destination);
         return new ClientboundBundlePacket(iterable);
      }
   }
}
