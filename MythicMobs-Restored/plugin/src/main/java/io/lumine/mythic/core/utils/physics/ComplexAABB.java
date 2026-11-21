package io.lumine.mythic.core.utils.physics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ComplexAABB {
   @NotNull
   public static Collection<BoundingBox> get(@NotNull Block block) {
      Collection<BoundingBox> AABBs = new ArrayList<>();
      Material type = block.getType();
      if (Tag.STAIRS.isTagged(type)) {
         AABBs.addAll(ComplexAABB.Stair.get(block));
      } else if (type == Material.CAULDRON) {
         AABBs.addAll(ComplexAABB.Cauldron.get(block));
      } else {
         AABBs.add(block.getBoundingBox());
      }

      return AABBs;
   }

   @NotNull
   public static Collection<BoundingBox> getOverlaps(@NotNull BoundingBox a, @NotNull Collection<BoundingBox> b) {
      Collection<BoundingBox> AABBs = new ArrayList<>(Collections.emptyList());

      for (BoundingBox _b : b) {
         if (a.overlaps(_b)) {
            AABBs.add(_b);
         }
      }

      return AABBs;
   }

   public static boolean anyOverlap(@NotNull BoundingBox a, @NotNull Collection<BoundingBox> b) {
      for (BoundingBox _b : b) {
         if (a.overlaps(_b)) {
            return true;
         }
      }

      return false;
   }

   public static class Cauldron {
      private static final BoundingBox BOTTOM_SLAB = new BoundingBox(0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
      private static final BoundingBox FACE_WEST = new BoundingBox(0.0, 0.25, 0.0, 0.125, 1.0, 1.0);
      private static final BoundingBox FACE_SOUTH = new BoundingBox(0.0, 0.25, 1.0, 1.0, 1.0, 0.875);
      private static final BoundingBox FACE_EAST = new BoundingBox(1.0, 0.25, 1.0, 0.875, 1.0, 0.0);
      private static final BoundingBox FACE_NORTH = new BoundingBox(1.0, 0.25, 0.0, 0.0, 1.0, 0.125);

      @NotNull
      public static Collection<BoundingBox> get(@NotNull Block block) {
         Collection<BoundingBox> AABBs = new ArrayList<>(Arrays.asList(BOTTOM_SLAB, FACE_NORTH, FACE_EAST, FACE_SOUTH, FACE_WEST));
         Vector v = block.getLocation().toVector();

         for (BoundingBox o : AABBs) {
            o.shift(v);
         }

         return AABBs;
      }
   }

   public static class Stair {
      private static final BoundingBox STRIP_NORTH = new BoundingBox(0.0, 0.5, 0.5, 1.0, 1.0, 1.0);
      private static final BoundingBox STRIP_EAST = new BoundingBox(0.0, 0.5, 0.0, 0.5, 1.0, 1.0);
      private static final BoundingBox STRIP_SOUTH = new BoundingBox(0.0, 0.5, 0.0, 1.0, 1.0, 0.5);
      private static final BoundingBox STRIP_WEST = new BoundingBox(0.5, 0.5, 0.0, 1.0, 1.0, 1.0);
      private static final BoundingBox CORNER_NORTH_EAST = new BoundingBox(0.5, 0.5, 0.0, 1.0, 1.0, 0.5);
      private static final BoundingBox CORNER_NORTH_WEST = new BoundingBox(0.0, 0.5, 0.0, 0.5, 1.0, 0.5);
      private static final BoundingBox CORNER_SOUTH_EAST = new BoundingBox(0.5, 0.5, 0.5, 1.0, 1.0, 1.0);
      private static final BoundingBox CORNER_SOUTH_WEST = new BoundingBox(0.0, 0.5, 0.5, 0.5, 1.0, 1.0);
      private static final BoundingBox SLAB_BOTTOM = new BoundingBox(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
      private static final BoundingBox SLAB_TOP = new BoundingBox(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);

      @NotNull
      public static Collection<BoundingBox> get(@NotNull Block block) {
         Collection<BoundingBox> AABBs;
         BlockData data;
         AABBs = new ArrayList<>(Collections.emptyList());
         data = block.getBlockData();
         Shape shape = ((Stairs)data).getShape();
         switchBlock:
         switch (((Stairs)data).getFacing().getOppositeFace()) {
            case NORTH:
               switch (shape) {
                  case STRAIGHT:
                     AABBs.add(STRIP_NORTH.clone());
                     break switchBlock;
                  case INNER_LEFT:
                     AABBs.add(STRIP_NORTH.clone());
                     AABBs.add(CORNER_NORTH_EAST.clone());
                     break switchBlock;
                  case INNER_RIGHT:
                     AABBs.add(STRIP_NORTH.clone());
                     AABBs.add(CORNER_NORTH_WEST.clone());
                     break switchBlock;
                  case OUTER_LEFT:
                     AABBs.add(CORNER_SOUTH_EAST.clone());
                     break switchBlock;
                  case OUTER_RIGHT:
                     AABBs.add(CORNER_SOUTH_WEST.clone());
                  default:
                     break switchBlock;
               }
            case EAST:
               switch (shape) {
                  case STRAIGHT:
                     AABBs.add(STRIP_EAST.clone());
                     break switchBlock;
                  case INNER_LEFT:
                     AABBs.add(STRIP_EAST.clone());
                     AABBs.add(CORNER_SOUTH_EAST.clone());
                     break switchBlock;
                  case INNER_RIGHT:
                     AABBs.add(STRIP_EAST.clone());
                     AABBs.add(CORNER_NORTH_EAST.clone());
                     break switchBlock;
                  case OUTER_LEFT:
                     AABBs.add(CORNER_SOUTH_WEST.clone());
                     break switchBlock;
                  case OUTER_RIGHT:
                     AABBs.add(CORNER_NORTH_WEST.clone());
                  default:
                     break switchBlock;
               }
            case SOUTH:
               switch (shape) {
                  case STRAIGHT:
                     AABBs.add(STRIP_SOUTH.clone());
                     break switchBlock;
                  case INNER_LEFT:
                     AABBs.add(STRIP_SOUTH.clone());
                     AABBs.add(CORNER_SOUTH_WEST.clone());
                     break switchBlock;
                  case INNER_RIGHT:
                     AABBs.add(STRIP_SOUTH.clone());
                     AABBs.add(CORNER_SOUTH_EAST.clone());
                     break switchBlock;
                  case OUTER_LEFT:
                     AABBs.add(CORNER_NORTH_WEST.clone());
                     break switchBlock;
                  case OUTER_RIGHT:
                     AABBs.add(CORNER_NORTH_EAST.clone());
                  default:
                     break switchBlock;
               }
            case WEST:
               switch (shape) {
                  case STRAIGHT:
                     AABBs.add(STRIP_WEST.clone());
                     break;
                  case INNER_LEFT:
                     AABBs.add(STRIP_WEST.clone());
                     AABBs.add(CORNER_NORTH_WEST.clone());
                     break;
                  case INNER_RIGHT:
                     AABBs.add(STRIP_WEST.clone());
                     AABBs.add(CORNER_SOUTH_WEST.clone());
                     break;
                  case OUTER_LEFT:
                     AABBs.add(CORNER_NORTH_EAST.clone());
                     break;
                  case OUTER_RIGHT:
                     AABBs.add(CORNER_SOUTH_EAST.clone());
               }
         }

         if (((Stairs)data).getHalf() == Half.TOP) {
            for (BoundingBox o : AABBs) {
               o.shift(0.0, -0.5, 0.0);
            }

            AABBs.add(SLAB_TOP.clone());
         } else {
            AABBs.add(SLAB_BOTTOM.clone());
         }

         Vector v = block.getLocation().toVector();

         for (BoundingBox o : AABBs) {
            o.shift(v);
         }

         return AABBs;
      }
   }
}
