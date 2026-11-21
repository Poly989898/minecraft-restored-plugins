package com.ticxo.modelengine.v1_20_R3.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.BlockPosition.MutableBlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.RayTrace.BlockCollisionOption;
import net.minecraft.world.level.RayTrace.FluidCollisionOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class OcclusionClipContext extends RayTrace {
   public OcclusionClipContext(Vec3D start, Vec3D end) {
      super(start, end, BlockCollisionOption.c, FluidCollisionOption.a, (Entity)null);
   }

   public VoxelShape a(IBlockData state, IBlockAccess world, BlockPosition pos) {
      return occlusionShape(state, world, pos);
   }

   private static VoxelShape occlusionShape(IBlockData state, IBlockAccess world, BlockPosition pos) {
      return state.p() && state.c(world, pos) == VoxelShapes.b() ? VoxelShapes.b() : VoxelShapes.a();
   }

   static boolean isClear(World level, Vec3D from, Vec3D to) {
      if (from.equals(to)) {
         return true;
      }

      double toX = MathHelper.d(-1.0E-7, to.c, from.c);
      double toY = MathHelper.d(-1.0E-7, to.d, from.d);
      double toZ = MathHelper.d(-1.0E-7, to.e, from.e);
      double fromX = MathHelper.d(-1.0E-7, from.c, to.c);
      double fromY = MathHelper.d(-1.0E-7, from.d, to.d);
      double fromZ = MathHelper.d(-1.0E-7, from.e, to.e);
      int x = MathHelper.a(fromX);
      int y = MathHelper.a(fromY);
      int z = MathHelper.a(fromZ);
      double dx = toX - fromX;
      double dy = toY - fromY;
      double dz = toZ - fromZ;
      int signX = MathHelper.j(dx);
      int signY = MathHelper.j(dy);
      int signZ = MathHelper.j(dz);
      double tDeltaX = signX == 0 ? Double.MAX_VALUE : signX / dx;
      double tDeltaY = signY == 0 ? Double.MAX_VALUE : signY / dy;
      double tDeltaZ = signZ == 0 ? Double.MAX_VALUE : signZ / dz;
      double tX = tDeltaX * (signX > 0 ? 1.0 - MathHelper.e(fromX) : MathHelper.e(fromX));
      double tY = tDeltaY * (signY > 0 ? 1.0 - MathHelper.e(fromY) : MathHelper.e(fromY));
      double tZ = tDeltaZ * (signZ > 0 ? 1.0 - MathHelper.e(fromZ) : MathHelper.e(fromZ));
      MutableBlockPosition pos = new MutableBlockPosition(x, y, z);
      IChunkAccess chunk = null;
      int chunkX = 0;
      int chunkZ = 0;

      while (true) {
         pos.d(x, y, z);
         IBlockData state;
         if (level.captureTreeGeneration) {
            state = level.getBlockStateIfLoaded(pos);
         } else if (level.s(pos)) {
            state = Blocks.nb.o();
         } else {
            if (chunk == null || x >> 4 != chunkX || z >> 4 != chunkZ) {
               chunkX = x >> 4;
               chunkZ = z >> 4;
               chunk = level.getChunkIfLoadedImmediately(chunkX, chunkZ);
            }

            state = chunk == null ? null : chunk.a_(pos);
         }

         if (state == null) {
            return true;
         }

         if (!state.i()) {
            VoxelShape shape = occlusionShape(state, level, pos);
            if (!shape.c()) {
               MovingObjectPositionBlock hit = level.a(from, to, pos, shape, state);
               if (hit != null && from.g(hit.e()) <= Double.MAX_VALUE) {
                  return false;
               }
            }
         }

         if (tX > 1.0 && tY > 1.0 && tZ > 1.0) {
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
