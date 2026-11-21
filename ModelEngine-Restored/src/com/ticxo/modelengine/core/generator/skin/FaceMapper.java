package com.ticxo.modelengine.core.generator.skin;

import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public record FaceMapper(String sourceTexture, float[] from, FaceMapper.Face to) {
   public float[] getSourcePixel(float x, float y) {
      float fx1 = this.from[0];
      float fy1 = this.from[1];
      float fx2 = this.from[2];
      float fy2 = this.from[3];
      float tx1 = this.to.uv[0];
      float ty1 = this.to.uv[1];
      float tx2 = this.to.uv[2];
      float ty2 = this.to.uv[3];
      float txr = TMath.invLerp(tx1, tx2, x);
      float tyr = TMath.invLerp(ty1, ty2, y);
      return new float[]{TMath.lerp(fx1, fx2, txr), TMath.lerp(fy1, fy2, tyr)};
   }

   @AllArgsConstructor(access = AccessLevel.PACKAGE)
   public enum Face {
      NORTH(new int[]{8, 8, 16, 16}),
      EAST(new int[]{0, 8, 8, 16}),
      SOUTH(new int[]{24, 8, 32, 16}),
      WEST(new int[]{16, 8, 24, 16}),
      UP(new int[]{16, 8, 8, 0}),
      DOWN(new int[]{24, 0, 16, 8}),
      NORTH_HAT(new int[]{40, 8, 48, 16}),
      EAST_HAT(new int[]{32, 8, 40, 16}),
      SOUTH_HAT(new int[]{56, 8, 64, 16}),
      WEST_HAT(new int[]{48, 8, 56, 16}),
      UP_HAT(new int[]{48, 8, 40, 0}),
      DOWN_HAT(new int[]{56, 0, 48, 8});

      private final int[] uv;

      public void forPixel(BiConsumer<Integer, Integer> consumer) {
         for (int x = Math.min(this.uv[0], this.uv[2]); x < Math.max(this.uv[0], this.uv[2]); x++) {
            for (int y = Math.min(this.uv[1], this.uv[3]); y < Math.max(this.uv[1], this.uv[3]); y++) {
               consumer.accept(x, y);
            }
         }
      }

      public static FaceMapper.Face getFace(String val, boolean hat) {
         return switch (val) {
            case "north" -> hat ? NORTH_HAT : NORTH;
            case "east" -> hat ? EAST_HAT : EAST;
            case "south" -> hat ? SOUTH_HAT : SOUTH;
            case "west" -> hat ? WEST_HAT : WEST;
            case "up" -> hat ? UP_HAT : UP;
            case "down" -> hat ? DOWN_HAT : DOWN;
            default -> throw new IllegalStateException("Unexpected value: " + val);
         };
      }
   }
}
