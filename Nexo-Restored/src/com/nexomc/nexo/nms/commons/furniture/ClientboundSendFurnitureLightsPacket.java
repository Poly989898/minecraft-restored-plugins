package com.nexomc.nexo.nms.commons.furniture;

import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacket;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.light.LightBlock;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.FurnitureHelpers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u001c2\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u0003:\u0001\u001cB\u0017\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0016\u0010\u0012\u001a\u0010\u0012\f\b\u0001\u0012\b\u0012\u0004\u0012\u00020\u00020\u00010\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0002H\u0016J\n\u0010\u0017\u001a\u0004\u0018\u00010\u000fH\u0002J&\u0010\u0018\u001a\u0010\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u001b\u0018\u00010\u00192\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0002R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001d"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/ClientboundSendFurnitureLightsPacket;", "Lnet/minecraft/network/protocol/Packet;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacket;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "<init>", "(Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)V", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "wrappedPacket", "Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "getWrappedPacket", "()Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;", "type", "Lnet/minecraft/network/protocol/PacketType;", "handle", "", "handler", "createFakeSectionPackets", "lightPositions", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "Lnet/minecraft/core/BlockPos;", "Lnet/minecraft/world/level/block/state/BlockState;", "Companion", "com.nexomc:commons"}
)
public final class ClientboundSendFurnitureLightsPacket implements Packet<ClientGamePacketListener>, IFurniturePacket {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureMechanic mechanic;
   @Nullable
   private final ClientboundBundlePacket wrappedPacket;

   public ClientboundSendFurnitureLightsPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
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
      Object2ObjectOpenHashMap object2ObjectOpenHashMap = this.lightPositions(this.baseEntity, this.mechanic);
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

   private final Object2ObjectOpenHashMap<BlockPos, BlockState> lightPositions(ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      if (!baseEntity.isValid()) {
         return null;
      } else {
         Boolean flag = FurnitureHelpers.lightState(baseEntity);
         boolean it = flag;
         int value = 0;
         Boolean flag2 = mechanic.getLight().getToggleable() ? flag : null;
         boolean lightState = flag2 != null ? flag2 : true;
         value = 0;
         Pair pair = TuplesKt.to(baseEntity.getYaw(), baseEntity.getLocation());
         float yaw = ((Number)pair.component1()).floatValue();
         Object component2 = pair.component2();
         Location loc = (Location)component2;
         Pair pair2 = TuplesKt.to(baseEntity.getWorld(), baseEntity.getUniqueId());
         component2 = pair2.component1();
         World world = (World)component2;
         component2 = pair2.component2();
         UUID uuid = (UUID)component2;
         Object[] objs = CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getLightPositionMap(), uuid, (() -> ClientboundSendFurnitureLightsPacket.lightPositions$lambda$4(mechanic, yaw, loc, uuid, world)));
         int capacity = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(objs.length), 16);
         Object[] objs2 = objs;
         Map destination = (Map)(new Object2ObjectOpenHashMap(capacity));
         int value2 = 0;

         for(int i = objs.length; value2 < i; ++value2) {
            Object element = objs2[value2];
            LightBlock it_6 = (LightBlock)element;
            int value3 = 0;
            BlockPos blockPos = new BlockPos(it_6.getX(), it_6.getY(), it_6.getZ());
            Light lightData = it_6.getLightData();
            BlockPos blockPos2 = blockPos;
            int value4 = 0;
            Light light = Boolean.valueOf(lightState) ? lightData : null;
            Object obj = light != null ? (BlockData)light : IFurniturePacketManager.Companion.getAIR_DATA();
            if (obj == null) {
               throw new NullPointerException("null cannot be cast to non-null type org.bukkit.craftbukkit.block.data.CraftBlockData");
            }

            Pair pair3 = TuplesKt.to(blockPos2, ((CraftBlockData)obj).getState());
            destination.put(pair3.getFirst(), pair3.getSecond());
         }

         return (Object2ObjectOpenHashMap)destination;
      }
   }

   private static final LightBlock[] lightPositions$computeLights(FurnitureMechanic furnitureMechanic, float yaw, Location loc, UUID uuid, World world) {
      Iterable iterable = (Iterable)furnitureMechanic.getLight().getLightBlocks();
      Object result = null;
      result = new LightBlock[CollectionsKt.count(iterable)];
      int index = 0;

      for(Object item : iterable) {
         LightBlock lightBlock = (LightBlock)item;
         int value = 0;
         Location location = lightBlock.groundRotate(yaw).add(loc);
         LightBlock lightBlock2;
         if (IFurniturePacketManager.Companion.blockIsHitbox(location, uuid)) {
            lightBlock2 = null;
         } else {
            Block block2 = location.getBlock();
            Block block = block2;
            lightBlock2 = !block.isEmpty() && !BlockHelpers.REPLACEABLE_BLOCKS.contains(block.getType()) ? null : new LightBlock(location, lightBlock.getLightData(), block.getType() == Material.WATER);
         }

         if (lightBlock2 != null) {
            Object it = lightBlock2;
            int value2 = 0;
            if (index >= ((Object[])result).length) {
               Object[] objs = Arrays.copyOf((Object[])result, Math.max(((Object[])result).length * 2, CollectionsKt.count(iterable)));
               result = objs;
            }

            int value3 = index++;
            ((Object[])result)[value3] = it;
         }
      }

      Object[] objs2 = Arrays.copyOf((Object[])result, index);
      Object[] objs3 = objs2;
      if (objs3 == null) {
         throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<com.nexomc.nexo.mechanics.light.LightBlock>");
      } else {
         Object[] objs4 = (LightBlock[])objs3;
         LightBlock[] lightBlocks2 = (LightBlock[])objs4;
         int value4 = 0;
         LightBlock[] lightBlocks = lightBlocks2;
         IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
         Object[] objs6 = new Location[lightBlocks2.length];
         int value7 = 0;
         int value8 = 0;

         for(int i = lightBlocks2.length; value8 < i; ++value8) {
            Object obj = lightBlocks[value8];
            int value5 = value7++;
            int value6 = 0;
            Location location2 = ((LightBlock)obj).toLocation(world);
            objs6[value5] = location2;
         }

         Object[] objs5 = objs6;
         companion.storeLightLocations(uuid, world, (Location[])objs5);
         return (LightBlock[])objs4;
      }
   }

   private static final LightBlock[] lightPositions$lambda$4(FurnitureMechanic furnitureMechanic, float yaw, Location loc, UUID uuid, World world) {
      return lightPositions$computeLights(furnitureMechanic, yaw, loc, uuid, world);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/ClientboundSendFurnitureLightsPacket$Companion;", "", "<init>", "()V", "precomputeLightPositions", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "com.nexomc:commons"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final void precomputeLightPositions(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         if (baseEntity.isValid()) {
            UUID uniqueId = baseEntity.getUniqueId();
            UUID uuid = uniqueId;
            if (!IFurniturePacketManager.Companion.getLightPositionMap().containsKey(uuid)) {
               float yaw = baseEntity.getYaw();
               Location location2 = baseEntity.getLocation();
               Location loc = location2;
               World world2 = baseEntity.getWorld();
               World world = world2;
               Iterable lightBlocks = (Iterable)mechanic.getLight().getLightBlocks();
               Object result = null;
               result = new LightBlock[CollectionsKt.count(lightBlocks)];
               int index = 0;

               for(Object item : lightBlocks) {
                  LightBlock lightBlock = (LightBlock)item;
                  int value = 0;
                  Location location = lightBlock.groundRotate(yaw).add(loc);
                  LightBlock lightBlock2;
                  if (IFurniturePacketManager.Companion.blockIsHitbox(location, uuid)) {
                     lightBlock2 = null;
                  } else {
                     Block block2 = location.getBlock();
                     Block block = block2;
                     lightBlock2 = !block.isEmpty() && !BlockHelpers.REPLACEABLE_BLOCKS.contains(block.getType()) ? null : new LightBlock(location, lightBlock.getLightData(), block.getType() == Material.WATER);
                  }

                  if (lightBlock2 != null) {
                     Object it = lightBlock2;
                     int value2 = 0;
                     if (index >= ((Object[])result).length) {
                        Object[] objs = Arrays.copyOf((Object[])result, Math.max(((Object[])result).length * 2, CollectionsKt.count(lightBlocks)));
                        result = objs;
                     }

                     int value3 = index++;
                     ((Object[])result)[value3] = it;
                  }
               }

               Object[] objs2 = Arrays.copyOf((Object[])result, index);
               Object[] objs4 = objs2;
               if (objs4 == null) {
                  throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<com.nexomc.nexo.mechanics.light.LightBlock>");
               } else {
                  LightBlock[] lights = (LightBlock[])((LightBlock[])objs4);
                  LightBlock[] lightBlocks2 = lights;
                  IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
                  result = new Location[lights.length];
                  index = 0;
                  int value4 = 0;

                  for(int i = lights.length; value4 < i; ++value4) {
                     Object obj = lightBlocks2[value4];
                     int value5 = index++;
                     int value6 = 0;
                     Location location3 = ((LightBlock)obj).toLocation(world);
                     ((Object[])result)[value5] = location3;
                  }

                  Object[] objs3 = (Object[])(result);
                  companion.storeLightLocations(uuid, world, (Location[])objs3);
                  CollectionUtilsKt.foliaGetOrPut(IFurniturePacketManager.Companion.getLightPositionMap(), uuid, (() -> Companion.precomputeLightPositions$lambda$2(lights)));
               }
            }
         }
      }

      private static final LightBlock[] precomputeLightPositions$lambda$2(LightBlock[] lights) {
         return lights;
      }
   }
}
