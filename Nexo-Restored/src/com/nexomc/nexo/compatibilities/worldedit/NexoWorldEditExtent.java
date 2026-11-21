package com.nexomc.nexo.compatibilities.worldedit;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.entity.EntityType;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTagType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u001a\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J/\u0010\u0010\u001a\u00020\u0011\"\u000e\b\u0000\u0010\u0012*\b\u0012\u0004\u0012\u0002H\u00120\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u0002H\u0012H\u0016¢\u0006\u0002\u0010\u0017J\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0019H\u0016JC\u0010\u001a\u001a\u00020\u001b\"\u0012\b\u0000\u0010\u001c*\f\u0012\u0006\u0012\u0004\u0018\u0001H\u001c\u0018\u00010\u00132\u0006\u0010\u001d\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\u00112\u0006\u0010\u001f\u001a\u00020\u00112\b\u0010\u0016\u001a\u0004\u0018\u0001H\u001cH\u0016¢\u0006\u0002\u0010 J-\u0010\u001a\u001a\u00020\u001b\"\u000e\b\u0000\u0010\u001c*\b\u0012\u0004\u0012\u0002H\u001c0\u00132\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u0002H\u001cH\u0016¢\u0006\u0002\u0010$J\u0012\u0010(\u001a\u0004\u0018\u00010)2\u0006\u0010*\u001a\u00020\u000fH\u0002R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010%\u001a\n '*\u0004\u0018\u00010&0&X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006+"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/worldedit/NexoWorldEditExtent;", "Lcom/sk89q/worldedit/extent/AbstractDelegateExtent;", "extent", "Lcom/sk89q/worldedit/extent/Extent;", "world", "Lorg/bukkit/World;", "<init>", "(Lcom/sk89q/worldedit/extent/Extent;Lorg/bukkit/World;)V", "getWorld", "()Lorg/bukkit/World;", "createEntity", "Lcom/sk89q/worldedit/entity/Entity;", "location", "Lcom/sk89q/worldedit/util/Location;", "baseEntity", "Lcom/sk89q/worldedit/entity/BaseEntity;", "setBlocks", "", "B", "Lcom/sk89q/worldedit/world/block/BlockStateHolder;", "region", "Lcom/sk89q/worldedit/regions/Region;", "block", "(Lcom/sk89q/worldedit/regions/Region;Lcom/sk89q/worldedit/world/block/BlockStateHolder;)I", "pattern", "Lcom/sk89q/worldedit/function/pattern/Pattern;", "setBlock", "", "T", "x", "y", "z", "(IIILcom/sk89q/worldedit/world/block/BlockStateHolder;)Z", "pos", "Lcom/sk89q/worldedit/math/BlockVector3;", "stateToSet", "(Lcom/sk89q/worldedit/math/BlockVector3;Lcom/sk89q/worldedit/world/block/BlockStateHolder;)Z", "FAWE_DISPLAY", "Lcom/sk89q/worldedit/world/entity/EntityType;", "kotlin.jvm.PlatformType", "furnitureMechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "entity", "com.nexomc:core"}
)
public final class NexoWorldEditExtent extends AbstractDelegateExtent {
   @NotNull
   private final World world;
   private final EntityType FAWE_DISPLAY;

   public NexoWorldEditExtent(@NotNull Extent extent, @NotNull World world) {
      Intrinsics.checkNotNullParameter(extent, "extent");
      Intrinsics.checkNotNullParameter(world, "world");
      super(extent);
      this.world = world;
      this.FAWE_DISPLAY = BukkitAdapter.adapt(org.bukkit.entity.EntityType.ITEM_DISPLAY);
   }

   @NotNull
   public final World getWorld() {
      return this.world;
   }

   @Nullable
   public Entity createEntity(@NotNull Location location, @NotNull BaseEntity baseEntity) {
      Intrinsics.checkNotNullParameter(location, "location");
      Intrinsics.checkNotNullParameter(baseEntity, "baseEntity");
      if (!Settings.WORLDEDIT_FURNITURE.toBool()) {
         return super.createEntity(location, baseEntity);
      } else if (!Intrinsics.areEqual(baseEntity.getType(), BukkitAdapter.adapt(org.bukkit.entity.EntityType.INTERACTION))) {
         if (baseEntity.hasNbtData() && baseEntity.getType() == BukkitAdapter.adapt(org.bukkit.entity.EntityType.ITEM_DISPLAY)) {
            FurnitureMechanic var13 = this.furnitureMechanic(baseEntity);
            if (var13 == null) {
               return super.createEntity(location, baseEntity);
            } else {
               FurnitureMechanic mechanic = var13;
               CompoundTag var14 = baseEntity.getNbtData();
               Intrinsics.checkNotNull(var14);
               int[] intArray = var14.getIntArray("UUID");
               int var7 = 0;
               UUID originalUUID = new UUID((long)intArray[0] << 32 | (long)intArray[1] & 4294967295L, (long)intArray[2] << 32 | (long)intArray[3] & 4294967295L);
               SchedulerUtils.INSTANCE.syncDelayedTask(2L, (() -> NexoWorldEditExtent.createEntity$lambda$1(originalUUID, this, location, mechanic)));
               return null;
            }
         } else {
            return super.createEntity(location, baseEntity);
         }
      } else {
         Object var11;
         block: {
            CompoundTag var10000 = baseEntity.getNbtData();
            if (var10000 != null) {
               Map var9 = var10000.getValue();
               if (var9 != null) {
                  Tag var10 = (Tag)var9.get("BukkitValues");
                  if (var10 != null) {
                     var11 = var10.getValue();
                     break block;
                  }
               }
            }

            var11 = null;
         }

         Object $this$safeCast$iv = var11;
         var11 = $this$safeCast$iv;
         if (!($this$safeCast$iv instanceof Map)) {
            var11 = null;
         }

         return ((Map)var11 != null ? ((Map)var11).containsKey("nexo:seat") : false) ? null : super.createEntity(location, baseEntity);
      }
   }

   public <B extends BlockStateHolder<B>> int setBlocks(@Nullable Region region, @NotNull B block) {
      Intrinsics.checkNotNullParameter(block, "block");
      return super.setBlocks(region, block);
   }

   public int setBlocks(@NotNull Region region, @NotNull Pattern pattern) {
      Intrinsics.checkNotNullParameter(region, "region");
      Intrinsics.checkNotNullParameter(pattern, "pattern");
      Iterable $this$forEach$iv = (Iterable)region;

      for(Object element$iv : $this$forEach$iv) {
         BlockVector3 block = (BlockVector3)element$iv;
         int var8 = 0;
         org.bukkit.Location location = BukkitAdapter.adapt(this.world, block);
         BlockState stateBefore = BukkitAdapter.adapt(location.getBlock().getBlockData());
         BaseBlock stateToSet = pattern.applyBlock(block);
         CustomBlocksWorldEditUtils var10000 = CustomBlocksWorldEditUtils.INSTANCE;
         Intrinsics.checkNotNull(location);
         Intrinsics.checkNotNull(stateToSet);
         BlockStateHolder var10002 = (BlockStateHolder)stateToSet;
         Intrinsics.checkNotNull(stateBefore);
         var10000.processBlock(location, var10002, (BlockStateHolder)stateBefore);
      }

      return super.setBlocks(region, pattern);
   }

   public <T extends BlockStateHolder<T>> boolean setBlock(int x, int y, int z, @Nullable T block) {
      return super.setBlock(x, y, z, block);
   }

   public <T extends BlockStateHolder<T>> boolean setBlock(@NotNull BlockVector3 pos, @NotNull T stateToSet) throws WorldEditException {
      Intrinsics.checkNotNullParameter(pos, "pos");
      Intrinsics.checkNotNullParameter(stateToSet, "stateToSet");
      if (!Settings.WORLDEDIT_CUSTOM_BLOCKS.toBool()) {
         return super.setBlock(pos, stateToSet);
      } else {
         org.bukkit.Location location = BukkitAdapter.adapt(this.world, pos);
         BlockState stateBefore = BukkitAdapter.adapt(location.getBlock().getBlockData());
         CustomBlocksWorldEditUtils var10000 = CustomBlocksWorldEditUtils.INSTANCE;
         Intrinsics.checkNotNull(location);
         Intrinsics.checkNotNull(stateBefore);
         var10000.processBlock(location, stateToSet, (BlockStateHolder)stateBefore);
         return super.setBlock(pos, stateToSet);
      }
   }

   private final FurnitureMechanic furnitureMechanic(BaseEntity entity) {
      LazyReference var10000 = entity.getNbtReference();
      if (var10000 != null) {
         LazyReference var5 = var10000;
         int var7 = 0;
         var10000 = entity.getType() != this.FAWE_DISPLAY ? var5 : null;
         if (var10000 != null) {
            LazyReference tag = var10000;
            LinCompoundTag var9 = (LinCompoundTag)((LinCompoundTag)tag.getValue()).findTag("BukkitValues", LinTagType.compoundTag());
            if (var9 == null) {
               return null;
            }

            LinCompoundTag bukkitValues = var9;
            LinStringTag var10 = (LinStringTag)bukkitValues.findTag("nexo:furniture", LinTagType.stringTag());
            if (var10 == null) {
               return null;
            }

            LinStringTag furnitureTag = var10;
            return NexoFurniture.furnitureMechanic(furnitureTag.value());
         }
      }

      return null;
   }

   private static final Unit createEntity$lambda$1(UUID $originalUUID, NexoWorldEditExtent this$0, Location $location, FurnitureMechanic $mechanic) {
      org.bukkit.entity.Entity var10000 = Bukkit.getEntity($originalUUID);
      if (var10000 != null) {
         Object $this$safeCast$iv = var10000;
         Object var10 = $this$safeCast$iv;
         if (!($this$safeCast$iv instanceof ItemDisplay)) {
            var10 = null;
         }

         ItemDisplay var11 = (ItemDisplay)var10;
         if (var11 != null) {
            ItemDisplay p0 = var11;
            NexoFurniture var12 = NexoFurniture.INSTANCE;
            int var8 = 0;
            NexoFurniture.updateFurniture(p0);
         }
      }

      org.bukkit.Location location = BukkitAdapter.adapt(this$0.world, $location);
      Intrinsics.checkNotNull(location);
      $mechanic.place(location, location.getYaw(), BlockFace.UP, false);
      return Unit.INSTANCE;
   }
}
