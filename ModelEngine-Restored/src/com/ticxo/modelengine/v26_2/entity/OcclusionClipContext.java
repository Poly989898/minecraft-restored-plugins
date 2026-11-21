package com.ticxo.modelengine.v26_2.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OcclusionClipContext extends ClipContext {
   public OcclusionClipContext(Vec3 start, Vec3 end) {
      super(start, end, Block.VISUAL, Fluid.NONE, (Entity)null);
   }

   public VoxelShape getBlockShape(BlockState state, BlockGetter world, BlockPos pos) {
      return occlusionShape(state, world, pos);
   }

   private static VoxelShape occlusionShape(BlockState state, BlockGetter world, BlockPos pos) {
      return state.canOcclude() && state.getOcclusionShape() == Shapes.block() ? Shapes.block() : Shapes.empty();
   }

   static boolean isClear(Level level, Vec3 from, Vec3 to) {
      if (from.equals(to)) {
         return true;
      }

      double toX = Mth.lerp(-1.0E-7, to.x, from.x);
      double toY = Mth.lerp(-1.0E-7, to.y, from.y);
      double toZ = Mth.lerp(-1.0E-7, to.z, from.z);
      double fromX = Mth.lerp(-1.0E-7, from.x, to.x);
      double fromY = Mth.lerp(-1.0E-7, from.y, to.y);
      double fromZ = Mth.lerp(-1.0E-7, from.z, to.z);
      int x = Mth.floor(fromX);
      int y = Mth.floor(fromY);
      int z = Mth.floor(fromZ);
      double dx = toX - fromX;
      double dy = toY - fromY;
      double dz = toZ - fromZ;
      int signX = Mth.sign(dx);
      int signY = Mth.sign(dy);
      int signZ = Mth.sign(dz);
      double tDeltaX = signX == 0 ? Double.MAX_VALUE : signX / dx;
      double tDeltaY = signY == 0 ? Double.MAX_VALUE : signY / dy;
      double tDeltaZ = signZ == 0 ? Double.MAX_VALUE : signZ / dz;
      double tX = tDeltaX * (signX > 0 ? 1.0 - Mth.frac(fromX) : Mth.frac(fromX));
      double tY = tDeltaY * (signY > 0 ? 1.0 - Mth.frac(fromY) : Mth.frac(fromY));
      double tZ = tDeltaZ * (signZ > 0 ? 1.0 - Mth.frac(fromZ) : Mth.frac(fromZ));
      MutableBlockPos pos = new MutableBlockPos(x, y, z);
      ChunkAccess chunk = null;
      int chunkX = 0;
      int chunkZ = 0;

      while (true) {
         pos.set(x, y, z);
         BlockState state;
         if (level.captureTreeGeneration) {
            state = level.getBlockStateIfLoaded(pos);
         } else if (level.isOutsideBuildHeight(pos)) {
            state = Blocks.VOID_AIR.defaultBlockState();
         } else {
            if (chunk == null || x >> 4 != chunkX || z >> 4 != chunkZ) {
               chunkX = x >> 4;
               chunkZ = z >> 4;
               chunk = level.getChunkIfLoadedImmediately(chunkX, chunkZ);
            }

            state = chunk == null ? null : chunk.getBlockState(pos);
         }

         if (state == null) {
            return true;
         }

         if (!state.isAir()) {
            VoxelShape shape = occlusionShape(state, level, pos);
            if (!shape.isEmpty()) {
               BlockHitResult hit = level.clipWithInteractionOverride(from, to, pos, shape, state);
               if (hit != null && from.distanceToSqr(hit.getLocation()) <= Double.MAX_VALUE) {
                  return false;
               }
            }
         }

         if (!(tX <= 1.0) && !(tY <= 1.0) && !(tZ <= 1.0)) {
            return true;
         }

         if (tX < tY) {
            if (tX < tZ) {
               x += signX;
               tX += tDeltaX;
            } else {
               z += signZ;
               tZ += tDeltaZ;
            }
         } else if (tY < tZ) {
            y += signY;
            tY += tDeltaY;
         } else {
            z += signZ;
            tZ += tDeltaZ;
         }
      }
   }
}
