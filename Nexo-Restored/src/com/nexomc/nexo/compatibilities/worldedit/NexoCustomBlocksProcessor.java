package com.nexomc.nexo.compatibilities.worldedit;

import com.fastasyncworldedit.core.extent.processor.ProcessorScope;
import com.fastasyncworldedit.core.queue.IBatchProcessor;
import com.fastasyncworldedit.core.queue.IChunk;
import com.fastasyncworldedit.core.queue.IChunkGet;
import com.fastasyncworldedit.core.queue.IChunkSet;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypesCache;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J(\u0010\b\u001a\u0004\u0018\u00010\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\tH\u0016J \u0010\u000f\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\tH\u0016J\u0014\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0012H\u0017b\u0002\b\u0014J\b\u0010\u0015\u001a\u00020\u0016H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0017"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/worldedit/NexoCustomBlocksProcessor;", "Lcom/fastasyncworldedit/core/queue/IBatchProcessor;", "world", "Lorg/bukkit/World;", "<init>", "(Lorg/bukkit/World;)V", "getWorld", "()Lorg/bukkit/World;", "processSet", "Lcom/fastasyncworldedit/core/queue/IChunkSet;", "chunk", "Lcom/fastasyncworldedit/core/queue/IChunk;", "get", "Lcom/fastasyncworldedit/core/queue/IChunkGet;", "set", "postProcess", "", "construct", "Lcom/sk89q/worldedit/extent/Extent;", "child", "Ljavax/annotation/Nullable;", "getScope", "Lcom/fastasyncworldedit/core/extent/processor/ProcessorScope;", "com.nexomc:core"}
)
public final class NexoCustomBlocksProcessor implements IBatchProcessor {
   @NotNull
   private final World world;

   public NexoCustomBlocksProcessor(@NotNull World world) {
      Intrinsics.checkNotNullParameter(world, "world");
      super();
      this.world = world;
   }

   @NotNull
   public final World getWorld() {
      return this.world;
   }

   @Nullable
   public IChunkSet processSet(@Nullable IChunk chunk, @Nullable IChunkGet get, @Nullable IChunkSet set) {
      return null;
   }

   public void postProcess(@NotNull IChunk chunk, @NotNull IChunkGet get, @NotNull IChunkSet set) {
      Intrinsics.checkNotNullParameter(chunk, "chunk");
      Intrinsics.checkNotNullParameter(get, "get");
      Intrinsics.checkNotNullParameter(set, "set");
      Pair var4 = TuplesKt.to(chunk.getX() << 4, chunk.getZ() << 4);
      int bx = ((Number)var4.component1()).intValue();
      int bz = ((Number)var4.component2()).intValue();
      int layer = get.getMinSectionPosition();
      int var8 = get.getMaxSectionPosition();
      if (layer <= var8) {
         while(true) {
            if (set.hasSection(layer)) {
               char[] var10000 = set.loadIfPresent(layer);
               Intrinsics.checkNotNull(var10000);
               char[] var10001 = get.loadIfPresent(layer);
               Intrinsics.checkNotNull(var10001);
               Pair var9 = TuplesKt.to(var10000, var10001);
               char[] blocksSet = (char[])var9.component1();
               char[] blocksGet = (char[])var9.component2();
               int by = layer << 4;
               int y = 0;

               for(int index = 0; y < 16; ++y) {
                  int yy = y + by;

                  for(int z = 0; z < 16; ++z) {
                     int zz = z + bz;
                     int x = 0;

                     while(x < 16) {
                        int rawStateSet = blocksSet[index];
                        if (rawStateSet == 0) {
                           ++x;
                           ++index;
                        } else {
                           int rawStateGet = blocksGet[index];
                           if (rawStateSet == rawStateGet) {
                              ++x;
                              ++index;
                           } else {
                              int xx = bx + x;
                              Pair var22 = TuplesKt.to(BlockTypesCache.states[rawStateSet], BlockTypesCache.states[rawStateGet]);
                              BlockState stateSet = (BlockState)var22.component1();
                              BlockState stateGet = (BlockState)var22.component2();
                              Location location = new Location(this.world, (double)xx, (double)yy, (double)zz);
                              CustomBlocksWorldEditUtils var26 = CustomBlocksWorldEditUtils.INSTANCE;
                              Intrinsics.checkNotNull(stateSet);
                              BlockStateHolder var10002 = (BlockStateHolder)stateSet;
                              Intrinsics.checkNotNull(stateGet);
                              var26.processBlock(location, var10002, (BlockStateHolder)stateGet);
                              ++x;
                              ++index;
                           }
                        }
                     }
                  }
               }
            }

            if (layer == var8) {
               break;
            }

            ++layer;
         }
      }

   }

   @javax.annotation.Nullable
   @NotNull
   public Extent construct(@NotNull Extent child) {
      Intrinsics.checkNotNullParameter(child, "child");
      return (Extent)(new NexoWorldEditExtent(child, this.world));
   }

   @NotNull
   public ProcessorScope getScope() {
      return ProcessorScope.READING_SET_BLOCKS;
   }
}
