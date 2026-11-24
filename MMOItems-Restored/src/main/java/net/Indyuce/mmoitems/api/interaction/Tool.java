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

   public Tool(PlayerData var1, NBTItem var2) {
      super(var1, var2);
   }

   @Deprecated
   public Tool(Player var1, NBTItem var2) {
      super(var1, var2);
   }

   @Override
   public CustomFlag getUseFlag() {
      return CustomFlag.MI_TOOLS;
   }

   public boolean miningEffects(@NotNull final Block var1) {
      boolean var2 = false;
      if (this.getNBTItem().getBoolean("MMOITEMS_AUTOSMELT")) {
         OreDrops var3 = MythicLib.plugin.getVersion().getWrapper().getOreDrops(var1.getType());
         if (var3 != null) {
            int var4 = MMOUtils.getLevel(this.getItem(), VEnchantment.FORTUNE.get());
            UtilityMethods.dropItemNaturally(var1.getLocation(), var3.generate(var4));
            var1.getWorld().spawnParticle(Particle.LAVA, var1.getLocation().add(0.5, 0.5, 0.5), 4);
            var1.setType(Material.AIR);
            var2 = true;
         }
      }

      if (this.getNBTItem().getBoolean("MMOITEMS_BOUNCING_CRACK") && !this.getPlayerData().isOnCooldown(PlayerData.CooldownType.BOUNCING_CRACK)) {
         this.getPlayerData().applyCooldown(PlayerData.CooldownType.BOUNCING_CRACK, 1.0);
         (new BukkitRunnable() {
            final Vector globalDirection = Tool.this.player.getEyeLocation().getDirection();
            final Vector3D sourcePoint = Tool.this.toJava(var1.getLocation().add(0.5, 0.5, 0.5).toVector());
            final Line line = new Line(this.sourcePoint, this.sourcePoint.add(Tool.this.toJava(this.globalDirection)), 1.0E-10);
            final double[] products = new double[Tool.NEIGHBORS.length];
            Block curr;
            int j;
            private static final int BLOCKS_BROKEN = 4;

            {
               for (BlockFace var6 : Tool.NEIGHBORS) {
                  this.products[var6.ordinal()] = var6.getDirection().dot(this.globalDirection);
               }

               this.curr = var1;
               this.j = 0;
            }

            public void run() {
               if (++this.j >= 4) {
                  this.cancel();
               }

               this.curr = this.findBestBlock();
               if (this.curr.getType() != Material.AIR && !MMOItems.plugin.getLanguage().isBlacklisted(this.curr.getType())) {
                  BouncingCrackBlockBreakEvent var1x = new BouncingCrackBlockBreakEvent(this.curr, Tool.this.player);
                  Bukkit.getPluginManager().callEvent(var1x);
                  if (var1x.isCancelled()) {
                     this.cancel();
                  } else {
                     this.curr.breakNaturally(Tool.this.getItem());
                     this.curr.getWorld().playSound(this.curr.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 1.0F, 1.0F);
                  }
               }
            }

            @NotNull
            private Block findBestBlock() {
               Block var1x = null;
               double var2x = Double.MAX_VALUE;

               for (BlockFace var7 : Tool.NEIGHBORS) {
                  Block var8 = this.curr.getRelative(var7);
                  double var9 = this.findCost(var7, var8);
                  if (var9 < var2x) {
                     var2x = var9;
                     var1x = var8;
                  }
               }

               return var1x;
            }

            private double findCost(BlockFace var1x, Block var2x) {
               Vector3D var3 = Tool.this.toJava(var2x.getLocation().add(0.5, 0.5, 0.5).toVector());
               return this.line.distance(var3) - this.products[var1x.ordinal()];
            }
         }).runTaskTimer(MMOItems.plugin, 0L, 1L);
      }

      return var2;
   }

   private Vector3D toJava(Vector var1) {
      return new Vector3D(var1.getX(), var1.getY(), var1.getZ());
   }
}
