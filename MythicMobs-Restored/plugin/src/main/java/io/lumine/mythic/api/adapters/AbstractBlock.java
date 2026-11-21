package io.lumine.mythic.api.adapters;

import io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBlock {
   public abstract boolean matches(AbstractLocation abstractLocation);

   public void set(AbstractLocation location) {
      this.set(location, true);
   }

   public abstract void set(AbstractLocation abstractLocation, boolean flag);

   public boolean isAir() {
      return false;
   }

   public boolean isOccluding() {
      return true;
   }

   public boolean isLiquid() {
      return false;
   }

   public boolean isLava() {
      return false;
   }

   public boolean isWater() {
      return false;
   }

   public abstract PacketFallingBlock createFakeEntity(AbstractLocation abstractLocation);

   @Nullable
   public abstract BlockData getBlockData();

   public static class InvalidBlock extends AbstractBlock {
      @Override
      public boolean matches(AbstractLocation location) {
         return false;
      }

      @Override
      public void set(AbstractLocation location, boolean physics) {
      }

      @Override
      public PacketFallingBlock createFakeEntity(AbstractLocation location) {
         return new PacketFallingBlock(location, Material.STONE.createBlockData());
      }

      @Override
      public BlockData getBlockData() {
         return null;
      }
   }
}
