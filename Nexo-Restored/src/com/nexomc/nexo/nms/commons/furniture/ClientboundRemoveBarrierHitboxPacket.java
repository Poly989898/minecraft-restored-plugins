package com.nexomc.nexo.nms.commons.furniture;

import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.hitbox.BarrierHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.FurnitureHitbox;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
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
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0016\u0010\u0011\u001a\u0010\u0012\f\b\u0001\u0012\b\u0012\u0004\u0012\u00020\u00020\u00010\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0002H\u0016J\b\u0010\u0016\u001a\u00020\u000eH\u0002J$\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u001a0\u00182\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001b"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/ClientboundRemoveBarrierHitboxPacket;", "Lnet/minecraft/network/protocol/Packet;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "<init>", "(Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)V", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "wrappedPacket", "Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "getWrappedPacket", "()Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "type", "Lnet/minecraft/network/protocol/PacketType;", "handle", "", "handler", "createFakeSectionPackets", "removeBarrierMap", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "Lnet/minecraft/core/BlockPos;", "Lnet/minecraft/world/level/block/state/BlockState;", "com.nexomc:commons"}
)
public final class ClientboundRemoveBarrierHitboxPacket implements Packet<ClientGamePacketListener> {
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureMechanic mechanic;
   @NotNull
   private final ClientboundBundlePacket wrappedPacket;

   public ClientboundRemoveBarrierHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
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

   @NotNull
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
      Set entrySet = this.removeBarrierMap(this.baseEntity, this.mechanic).entrySet();
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

      Map map = destination;
      Collection collection = (Collection)(new ArrayList(destination.size()));

      for(Map.Entry item : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value3 = 0;
         SectionPos sectionPos = (SectionPos)item.getKey();
         List entries = (List)item.getValue();
         Iterable iterable2 = (Iterable)entries;
         Map map2 = (Map)(new Short2ObjectArrayMap());

         for(Object obj3 : iterable2) {
            Map.Entry entry = (Map.Entry)obj3;
            int value4 = 0;
            BlockPos pos = (BlockPos)entry.getKey();
            BlockState blockState = (BlockState)entry.getValue();
            Pair pair = TuplesKt.to(SectionPos.sectionRelativePos(pos), blockState);
            map2.put(pair.getFirst(), pair.getSecond());
         }

         Short2ObjectArrayMap sectionData = (Short2ObjectArrayMap)map2;
         collection.add(new ClientboundSectionBlocksUpdatePacket(sectionPos, (Short2ObjectMap)sectionData));
      }

      List blockChanges = (List)collection;
      return new ClientboundBundlePacket((Iterable)blockChanges);
   }

   private final Object2ObjectOpenHashMap<BlockPos, BlockState> removeBarrierMap(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      BarrierHitbox[] barrierHitboxes = (BarrierHitbox[])IFurniturePacketManager.Companion.getBarrierHitboxPositionMap().get(baseEntity.getUniqueId());
      Object2ObjectOpenHashMap object2ObjectOpenHashMap;
      if (barrierHitboxes != null) {
         Object[] objs = barrierHitboxes;
         int capacity = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(objs.length), 16);
         Object obj = objs;
         Map destination = (Map)(new Object2ObjectOpenHashMap(capacity));
         int value = 0;

         for(int i = objs.length; value < i; ++value) {
            Object element = ((Object[])obj)[value];
            int blockData = 0;
            BlockPos blockPos = new BlockPos(((BarrierHitbox)element).getX(), ((BarrierHitbox)element).getY(), ((BarrierHitbox)element).getZ());
            BlockData wateR_DATA = IFurniturePacketManager.Companion.getWATER_DATA();
            BlockPos blockPos2 = blockPos;
            int value2 = 0;
            BlockData blockData2 = Boolean.valueOf(((BarrierHitbox)element).getBarrierData().isWaterlogged()) ? wateR_DATA : null;
            if (blockData2 == null) {
               blockData2 = IFurniturePacketManager.Companion.getAIR_DATA();
            }

            Object obj2 = blockData2;
            if (obj2 == null) {
               throw new NullPointerException("null cannot be cast to non-null type org.bukkit.craftbukkit.block.data.CraftBlockData");
            }

            Pair barrier = TuplesKt.to(blockPos2, ((CraftBlockData)obj2).getState());
            destination.put(barrier.getFirst(), barrier.getSecond());
         }

         object2ObjectOpenHashMap = (Object2ObjectOpenHashMap)destination;
      } else {
         FurnitureHitbox hitbox = mechanic.getHitbox();
         Location location2 = baseEntity.getLocation();
         Iterable iterable = (Iterable)hitbox.barrierLocations(location2, baseEntity.getYaw());
         int value4 = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10), 16);
         Map map = (Map)(new Object2ObjectOpenHashMap(value4));

         for(Object obj3 : iterable) {
            Location location = (Location)obj3;
            int value3 = 0;
            BlockHelpers blockHelpers = BlockHelpers.INSTANCE;
            BlockData blockData3 = blockHelpers.isLoaded(location) && mechanic.getWaterloggable() ? location.getBlock().getBlockData() : IFurniturePacketManager.Companion.getAIR_DATA();
            BlockPos blockPos3 = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Pair pair = TuplesKt.to(blockPos3, ((CraftBlockData)blockData3).getState());
            map.put(pair.getFirst(), pair.getSecond());
         }

         object2ObjectOpenHashMap = (Object2ObjectOpenHashMap)map;
      }

      return object2ObjectOpenHashMap;
   }
}
