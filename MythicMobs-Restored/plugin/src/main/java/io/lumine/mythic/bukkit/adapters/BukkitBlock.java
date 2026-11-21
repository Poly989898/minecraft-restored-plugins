package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock;
import io.lumine.mythic.bukkit.BukkitAdapter;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;

@Getter
public class BukkitBlock extends AbstractBlock {
   private final BlockData blockData;

   public static BukkitBlock get(AbstractLocation location) {
      return new BukkitBlock(BukkitAdapter.adapt(location).getBlock().getBlockData());
   }

   public BukkitBlock(String type) {
      if (!type.contains(":")) {
         type = "minecraft:" + type.toLowerCase();
      }

      this.blockData = (Registry.BLOCK.get(NamespacedKey.fromString(type))).createBlockData();
   }

   public BukkitBlock(Material material) {
      this.blockData = material.createBlockData();
   }

   public BukkitBlock(BlockData blockData) {
      this.blockData = blockData;
   }

   @Override
   public void set(AbstractLocation location) {
      this.set(location, true);
   }

   @Override
   public void set(AbstractLocation location, boolean physics) {
      Location l = BukkitAdapter.adapt(location);
      l.getBlock().setBlockData(this.blockData, physics);
   }

   @Override
   public boolean matches(AbstractLocation location) {
      BlockData tarBlock = BukkitAdapter.adapt(location).getBlock().getBlockData();
      return tarBlock.matches(this.blockData);
   }

   public boolean matches(Block block) {
      BlockData tarBlock = block.getBlockData();
      return tarBlock.matches(this.blockData);
   }

   @Override
   public boolean isAir() {
      return this.blockData.getMaterial().isAir();
   }

   @Override
   public boolean isOccluding() {
      return this.blockData.isOccluding();
   }

   @Override
   public boolean isLiquid() {
      return this.blockData.getMaterial() == Material.WATER || this.blockData.getMaterial() == Material.LAVA;
   }

   @Override
   public boolean isLava() {
      return this.blockData.getMaterial() == Material.LAVA;
   }

   @Override
   public boolean isWater() {
      return this.blockData.getMaterial() == Material.WATER;
   }

   @Override
   public PacketFallingBlock createFakeEntity(AbstractLocation location) {
      return new PacketFallingBlock(location, this.blockData);
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof BukkitBlock block) {
         return block.getBlockData().matches(this.blockData);
      } else {
         return o instanceof String string ? this.blockData.getMaterial().toString().equalsIgnoreCase(string) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.blockData.hashCode();
   }
}
