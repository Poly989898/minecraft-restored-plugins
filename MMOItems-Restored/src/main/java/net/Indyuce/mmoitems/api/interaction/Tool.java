package net.Indyuce.mmoitems.api.interaction;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import io.lumine.mythic.lib.math3.geometry.euclidean.threed.Line;
import io.lumine.mythic.lib.math3.geometry.euclidean.threed.Vector3D;
import io.lumine.mythic.lib.version.OreDrops;
import io.lumine.mythic.lib.version.VEnchantment;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.event.BouncingCrackBlockBreakEvent;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Tool extends Weapon {
   private static final BlockFace[] NEIGHBORS = new BlockFace[]{BlockFace.NORTH, BlockFace.DOWN, BlockFace.EAST, BlockFace.UP, BlockFace.WEST, BlockFace.SOUTH};

   public Tool(PlayerData playerData, NBTItem nbtItem) {
      super(playerData, nbtItem);
   }

   @Deprecated
   public Tool(Player player, NBTItem nbtItem) {
      super(player, nbtItem);
   }

   @Override
   public CustomFlag getUseFlag() {
      return CustomFlag.MI_TOOLS;
   }

   public boolean miningEffects(@NotNull final Block block) {
      boolean flag = false;
      if (this.getNBTItem().getBoolean("MMOITEMS_AUTOSMELT")) {
         OreDrops oreDrops = MythicLib.plugin.getVersion().getWrapper().getOreDrops(block.getType());
         if (oreDrops != null) {
            int level = MMOUtils.getLevel(this.getItem(), VEnchantment.FORTUNE.get());
            UtilityMethods.dropItemNaturally(block.getLocation(), oreDrops.generate(level));
            block.getWorld().spawnParticle(Particle.LAVA, block.getLocation().add(0.5, 0.5, 0.5), 4);
            block.setType(Material.AIR);
            flag = true;
         }
      }

      if (this.getNBTItem().getBoolean("MMOITEMS_BOUNCING_CRACK") && !this.getPlayerData().isOnCooldown(PlayerData.CooldownType.BOUNCING_CRACK)) {
         this.getPlayerData().applyCooldown(PlayerData.CooldownType.BOUNCING_CRACK, 1.0);
         (new BukkitRunnable() {
            final Vector globalDirection = Tool.this.player.getEyeLocation().getDirection();
            final Vector3D sourcePoint = Tool.this.toJava(block.getLocation().add(0.5, 0.5, 0.5).toVector());
            final Line line = new Line(this.sourcePoint, this.sourcePoint.add(Tool.this.toJava(this.globalDirection)), 1.0E-10);
            final double[] products = new double[Tool.NEIGHBORS.length];
            Block curr;
            int j;
            private static final int BLOCKS_BROKEN = 4;

            {
               for (BlockFace blockFace : Tool.NEIGHBORS) {
                  this.products[blockFace.ordinal()] = blockFace.getDirection().dot(this.globalDirection);
               }

               this.curr = block;
               this.j = 0;
            }

            public void run() {
               if (++this.j >= 4) {
                  this.cancel();
               }

               this.curr = this.findBestBlock();
               if (this.curr.getType() != Material.AIR && !MMOItems.plugin.getLanguage().isBlacklisted(this.curr.getType())) {
                  BouncingCrackBlockBreakEvent breakEvent = new BouncingCrackBlockBreakEvent(this.curr, Tool.this.player);
                  Bukkit.getPluginManager().callEvent(breakEvent);
                  if (breakEvent.isCancelled()) {
                     this.cancel();
                  } else {
                     this.curr.breakNaturally(Tool.this.getItem());
                     this.curr.getWorld().playSound(this.curr.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 1.0F, 1.0F);
                  }
               }
            }

            @NotNull
            private Block findBestBlock() {
               Block best = null;
               double bestCost = Double.MAX_VALUE;

               for (BlockFace blockFace : Tool.NEIGHBORS) {
                  Block relative = this.curr.getRelative(blockFace);
                  double cost = this.findCost(blockFace, relative);
                  if (cost < bestCost) {
                     bestCost = cost;
                     best = relative;
                  }
               }

               return best;
            }

            private double findCost(BlockFace face, Block neighbor) {
               Vector3D vector3D = Tool.this.toJava(neighbor.getLocation().add(0.5, 0.5, 0.5).toVector());
               return this.line.distance(vector3D) - this.products[face.ordinal()];
            }
         }).runTaskTimer(MMOItems.plugin, 0L, 1L);
      }

      return flag;
   }

   private Vector3D toJava(Vector vector) {
      return new Vector3D(vector.getX(), vector.getY(), vector.getZ());
   }
}
