package net.Indyuce.mmocore.loot.chest;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.SoundEvent;
import net.Indyuce.mmocore.util.HashableLocation;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

public class LootChest {
   private final ChestTier tier;
   private final LootChestRegion region;
   private final LootChest.ReplacedBlock block;
   @Nullable
   private final BukkitRunnable effectRunnable;
   private final BukkitRunnable closeRunnable;
   private boolean active = true;

   public LootChest(ChestTier tier, LootChestRegion region, Block block) {
      this.tier = tier;
      this.region = region;
      this.block = new LootChest.ReplacedBlock(block);
      this.effectRunnable = tier.hasEffect() ? tier.getEffect().startNewRunnable(block.getLocation().add(0.5, 0.5, 0.5)) : null;
      this.closeRunnable = new BukkitRunnable() {
         public void run() {
            LootChest.this.expire(false);
         }
      };
      this.closeRunnable.runTaskLater(MMOCore.plugin, MMOCore.plugin.configManager.lootChestExpireTime);
   }

   public ChestTier getTier() {
      return this.tier;
   }

   public LootChest.ReplacedBlock getBlock() {
      return this.block;
   }

   public LootChestRegion getRegion() {
      return this.region;
   }

   public boolean isActive() {
      return this.active;
   }

   public void expire(boolean player) {
      Validate.isTrue(this.active, "Chest has already expired");
      this.active = false;
      MMOCore.plugin.lootChests.unregister(this);
      if (!this.closeRunnable.isCancelled()) {
         this.closeRunnable.cancel();
      }

      if (player) {
         MMOCore.plugin.soundManager.getSound(SoundEvent.CLOSE_LOOT_CHEST).playAt(this.block.loc.bukkit());
         this.block.loc.getWorld().spawnParticle(Particle.CRIT, this.block.loc.bukkit().add(0.5, 0.5, 0.5), 16, 0.0, 0.0, 0.0, 0.5);
         Inventory chestInv = ((Chest)this.block.loc.bukkit().getBlock().getState()).getBlockInventory();
         Location centerLoc = this.block.findCenterLocation();

         for (ItemStack drop : chestInv.getContents()) {
            if (drop != null && drop.getType() != Material.AIR) {
               this.block.getLocation().getWorld().dropItem(centerLoc, drop);
            }
         }

         chestInv.clear();
      }

      this.block.restore();
      if (this.effectRunnable != null) {
         this.effectRunnable.cancel();
      }
   }

   public static class ReplacedBlock {
      private final Material material;
      private final BlockData data;
      private final HashableLocation loc;

      public ReplacedBlock(Block block) {
         this.material = block.getType();
         this.data = block.getBlockData();
         this.loc = new HashableLocation(block.getLocation());
      }

      public HashableLocation getLocation() {
         return this.loc;
      }

      public Location findCenterLocation() {
         return new Location(this.getLocation().getWorld(), this.loc.getX() + 0.5, this.loc.getY() + 0.5, this.loc.getZ() + 0.5);
      }

      @Deprecated
      public boolean matches(Location loc) {
         return this.loc.getWorld().equals(loc.getWorld())
            && this.loc.getX() == loc.getBlockX()
            && this.loc.getY() == loc.getBlockY()
            && this.loc.getZ() == loc.getBlockZ();
      }

      public void restore() {
         Block block = this.loc.bukkit().getBlock();
         block.setType(this.material);
         block.setBlockData(this.data);
      }
   }
}
