package com.nexomc.nexo.nms.commons.furniture;

import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.hitbox.BarrierHitbox;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.ranges.RangesKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0016\u0010\u0011\u001a\u0010\u0012\f\b\u0001\u0012\b\u0012\u0004\u0012\u00020\u00020\u00010\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0002H\u0016J\n\u0010\u0016\u001a\u0004\u0018\u00010\u000eH\u0002J&\u0010\u0017\u001a\u0010\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u001a\u0018\u00010\u00182\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\r\u001a\u0004\u0018\u00010\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001b"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/ClientboundSendBarrierHitboxPacket;", "Lnet/minecraft/network/protocol/Packet;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "<init>", "(Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)V", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "wrappedPacket", "Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "getWrappedPacket", "()Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "type", "Lnet/minecraft/network/protocol/PacketType;", "handle", "", "handler", "createFakeSectionPackets", "barrierPositions", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "Lnet/minecraft/core/BlockPos;", "Lnet/minecraft/world/level/block/state/BlockState;", "com.nexomc:commons"}
)
public final class ClientboundSendBarrierHitboxPacket implements Packet<ClientGamePacketListener> {
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureMechanic mechanic;
   @Nullable
   private final ClientboundBundlePacket wrappedPacket;

   public ClientboundSendBarrierHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      super();
      this.baseEntity = baseEntity;
      this.mechanic = mechanic;
      this.wrappedPacket = this.createFakeSectionPackets();
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

   private final ClientboundBundlePacket createFakeSectionPackets() {
      Object2ObjectOpenHashMap object2ObjectOpenHashMap = this.barrierPositions(this.baseEntity, this.mechanic);
      if (object2ObjectOpenHashMap != null) {
         ObjectSet entrySet = (ObjectSet)object2ObjectOpenHashMap.entrySet();
         if (entrySet != null) {
            Iterable iterable = (Iterable)entrySet;
            Map destination = (Map)(new LinkedHashMap());

            for(Object element : iterable) {
               Map.Entry it = (Map.Entry)element;
               int value = 0;
               Object key = SectionPos.of((BlockPos)it.getKey());
               Object obj2 = destination.get(key);
               Object obj;
               if (obj2 == null) {
                  int value2 = 0;
                  Object answer = (List)(new ArrayList());
                  destination.put(key, answer);
                  obj = answer;
               } else {
                  obj = obj2;
               }

               List list = (List)obj;
               list.add(element);
            }

            Collection collection = (Collection)(new ArrayList(destination.size()));

            for(Object obj3 : destination.entrySet()) {
               Map.Entry item = (Map.Entry)obj3;
               int value3 = 0;
               SectionPos sectionPos = (SectionPos)item.getKey();
               List entries = (List)item.getValue();
               Iterable iterable2 = (Iterable)entries;
               Map map = (Map)(new Short2ObjectArrayMap());

               for(Object obj4 : iterable2) {
                  Map.Entry entry = (Map.Entry)obj4;
                  int value4 = 0;
                  BlockPos pos = (BlockPos)entry.getKey();
                  BlockState blockState = (BlockState)entry.getValue();
                  Pair pair = TuplesKt.to(SectionPos.sectionRelativePos(pos), blockState);
                  map.put(pair.getFirst(), pair.getSecond());
               }

               Short2ObjectArrayMap sectionData = (Short2ObjectArrayMap)map;
               collection.add(new ClientboundSectionBlocksUpdatePacket(sectionPos, (Short2ObjectMap)sectionData));
            }

            List blockChanges = (List)collection;
            return new ClientboundBundlePacket((Iterable)blockChanges);
         }
      }

      return null;
   }

   private final Object2ObjectOpenHashMap<BlockPos, BlockState> barrierPositions(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      if (!baseEntity.isValid()) {
         return null;
      } else {
         Location location = baseEntity.getLocation();
         Location loc = location;
         UUID uniqueId = baseEntity.getUniqueId();
         UUID uuid = uniqueId;
         Object[] objs = CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getBarrierHitboxPositionMap(), uuid, (() -> ClientboundSendBarrierHitboxPacket.barrierPositions$lambda$3(mechanic, loc, uuid, baseEntity)));
         int capacity = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(objs.length), 16);
         Object[] objs2 = objs;
         Map destination = (Map)(new Object2ObjectOpenHashMap(capacity));
         int value = 0;

         for(int i = objs.length; value < i; ++value) {
            Object element = objs2[value];
            BarrierHitbox hitbox = (BarrierHitbox)element;
            int value2 = 0;
            Object obj = hitbox.barrierData(mechanic.getWaterloggable());
            if (obj == null) {
               throw new NullPointerException("null cannot be cast to non-null type org.bukkit.craftbukkit.block.data.CraftBlockData");
            }

            BlockState state = ((CraftBlockData)obj).getState();
            Pair pair = TuplesKt.to(new BlockPos(hitbox.getX(), hitbox.getY(), hitbox.getZ()), state);
            destination.put(pair.getFirst(), pair.getSecond());
         }

         return (Object2ObjectOpenHashMap)destination;
      }
   }

   private static final BarrierHitbox[] barrierPositions$computeBarriers(FurnitureMechanic furnitureMechanic, Location loc, UUID uuid, ItemDisplay itemDisplay) {
      Iterable barriers = (Iterable)furnitureMechanic.getHitbox().getBarriers();
      Object[] result = new Location[CollectionsKt.count(barriers)];
      int index = 0;

      for(Object item : barriers) {
         int value = index++;
         BarrierHitbox offset = (BarrierHitbox)item;
         int value2 = value;
         int value3 = 0;
         Location toBlockLocation = offset.groundRotate(loc.getYaw()).add(loc).toBlockLocation();
         int value4 = 0;
         toBlockLocation.setYaw(0.0F);
         toBlockLocation.setPitch(0.0F);
         result[value2] = toBlockLocation;
      }

      Object[] objs2 = result;
      Location[] it = (Location[])objs2;
      int value5 = 0;
      IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
      World world = itemDisplay.getWorld();
      companion.storeBarrierHitboxLocations(uuid, world, it);
      Object[] objs = objs2;
      Object[] objs3 = new BarrierHitbox[objs2.length];
      index = 0;
      int value9 = 0;

      for(int i = objs2.length; value9 < i; ++value9) {
         Object obj = objs[value9];
         int value6 = index++;
         Location it_1 = (Location)obj;
         int value7 = value6;
         int value8 = 0;
         BarrierHitbox barrierHitbox = new BarrierHitbox(it_1, furnitureMechanic.getWaterloggable());
         objs3[value7] = barrierHitbox;
      }

      return (BarrierHitbox[])(objs3);
   }

   private static final BarrierHitbox[] barrierPositions$lambda$3(FurnitureMechanic furnitureMechanic, Location loc, UUID uuid, ItemDisplay itemDisplay) {
      return barrierPositions$computeBarriers(furnitureMechanic, loc, uuid, itemDisplay);
   }
}
