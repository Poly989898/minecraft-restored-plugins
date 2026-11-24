package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.version.VParticle;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public class ProjectileParticlesData implements StatData, RandomStatData<ProjectileParticlesData> {
   private final Particle particle;
   private final int red;
   private final int green;
   private final int blue;
   private final boolean colored;
   public static final ProjectileParticlesData DEFAULT = new ProjectileParticlesData(Particle.NOTE);

   public ProjectileParticlesData(Particle var1) {
      this.particle = var1;
      this.red = 0;
      this.green = 0;
      this.blue = 0;
      this.colored = false;
   }

   public ProjectileParticlesData(Particle var1, int var2, int var3, int var4) {
      this.particle = var1;
      this.red = var2;
      this.green = var3;
      this.blue = var4;
      this.colored = true;
   }

   public ProjectileParticlesData(String var1) {
      JsonObject var2 = (JsonObject)MythicLib.plugin.getGson().fromJson(var1, JsonObject.class);
      this.particle = Particle.valueOf(var2.get("Particle").getAsString());
      if (MMOUtils.isColorable(this.particle)) {
         this.colored = true;
         this.red = var2.get("Red").getAsInt();
         this.green = var2.get("Green").getAsInt();
         this.blue = var2.get("Blue").getAsInt();
      } else {
         this.colored = false;
         this.red = 0;
         this.green = 0;
         this.blue = 0;
      }
   }

   public Particle getParticle() {
      return this.particle;
   }

   public int getRed() {
      return this.red;
   }

   public int getGreen() {
      return this.green;
   }

   public int getBlue() {
      return this.blue;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Deprecated
   public static boolean isColorable(Particle var0) {
      return MMOUtils.isColorable(var0);
   }

   @Override
   public String toString() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("Particle", this.particle.name());
      if (this.colored) {
         var1.addProperty("Red", this.red);
         var1.addProperty("Green", this.green);
         var1.addProperty("Blue", this.blue);
      }

      return var1.toString();
   }

   public void shootParticle(Location var1) {
      this.shootParticle(var1, 1, 0.0);
   }

   public void shootParticle(Location var1, int var2, double var3) {
      if (MMOUtils.isColorable(this.particle)) {
         if (this.particle == VParticle.REDSTONE.get()) {
            DustOptions var5 = new DustOptions(Color.fromRGB(this.red, this.green, this.blue), 1.0F);
            var1.getWorld().spawnParticle(this.particle, var1, var2, var3, var3, var3, 0.0, var5);
         } else if (this.particle == Particle.NOTE) {
            double var11 = this.red / 24.0;
            var1.getWorld().spawnParticle(Particle.NOTE, var1, 0, var11, 0.0, 0.0, 1.0);
         } else {
            double var12 = this.red / 255.0;
            double var7 = this.green / 255.0;
            double var9 = this.blue / 255.0;
            var1.getWorld().spawnParticle(this.particle, var1, 0, var12, var7, var9, 1.0);
         }
      } else if (this.particle == VParticle.ITEM.get()) {
         ItemStack var13 = new ItemStack(Material.STONE);
         var1.getWorld().spawnParticle(this.particle, var1, var2, var3, var3, var3, 0.0, var13);
      } else if (this.particle != VParticle.BLOCK.get() && this.particle != VParticle.BLOCK_DUST.get() && this.particle != Particle.FALLING_DUST) {
         var1.getWorld().spawnParticle(this.particle, var1, var2, var3, var3, var3, 0.0);
      } else {
         BlockData var14 = Material.STONE.createBlockData();
         var1.getWorld().spawnParticle(this.particle, var1, var2, var3, var3, var3, 0.0, var14);
      }
   }

   public ProjectileParticlesData randomize(MMOItemBuilder var1) {
      return this;
   }
}
