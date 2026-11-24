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

   public ProjectileParticlesData(Particle particle) {
      this.particle = particle;
      this.red = 0;
      this.green = 0;
      this.blue = 0;
      this.colored = false;
   }

   public ProjectileParticlesData(Particle particle, int count, int green, int blue) {
      this.particle = particle;
      this.red = count;
      this.green = green;
      this.blue = blue;
      this.colored = true;
   }

   public ProjectileParticlesData(String text) {
      JsonObject jsonObject = (JsonObject)MythicLib.plugin.getGson().fromJson(text, JsonObject.class);
      this.particle = Particle.valueOf(jsonObject.get("Particle").getAsString());
      if (MMOUtils.isColorable(this.particle)) {
         this.colored = true;
         this.red = jsonObject.get("Red").getAsInt();
         this.green = jsonObject.get("Green").getAsInt();
         this.blue = jsonObject.get("Blue").getAsInt();
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
   public static boolean isColorable(Particle particle) {
      return MMOUtils.isColorable(particle);
   }

   @Override
   public String toString() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("Particle", this.particle.name());
      if (this.colored) {
         jsonObject.addProperty("Red", this.red);
         jsonObject.addProperty("Green", this.green);
         jsonObject.addProperty("Blue", this.blue);
      }

      return jsonObject.toString();
   }

   public void shootParticle(Location location) {
      this.shootParticle(location, 1, 0.0);
   }

   public void shootParticle(Location location, int count, double value) {
      if (MMOUtils.isColorable(this.particle)) {
         if (this.particle == VParticle.REDSTONE.get()) {
            DustOptions dustOptions = new DustOptions(Color.fromRGB(this.red, this.green, this.blue), 1.0F);
            location.getWorld().spawnParticle(this.particle, location, count, value, value, value, 0.0, dustOptions);
         } else if (this.particle == Particle.NOTE) {
            double redScaled = this.red / 24.0;
            location.getWorld().spawnParticle(Particle.NOTE, location, 0, redScaled, 0.0, 0.0, 1.0);
         } else {
            double redScaled = this.red / 255.0;
            double greenScaled = this.green / 255.0;
            double blueScaled = this.blue / 255.0;
            location.getWorld().spawnParticle(this.particle, location, 0, redScaled, greenScaled, blueScaled, 1.0);
         }
      } else if (this.particle == VParticle.ITEM.get()) {
         ItemStack item = new ItemStack(Material.STONE);
         location.getWorld().spawnParticle(this.particle, location, count, value, value, value, 0.0, item);
      } else if (this.particle != VParticle.BLOCK.get() && this.particle != VParticle.BLOCK_DUST.get() && this.particle != Particle.FALLING_DUST) {
         location.getWorld().spawnParticle(this.particle, location, count, value, value, value, 0.0);
      } else {
         BlockData blockData = Material.STONE.createBlockData();
         location.getWorld().spawnParticle(this.particle, location, count, value, value, value, 0.0, blockData);
      }
   }

   public ProjectileParticlesData randomize(MMOItemBuilder mmoItemBuilder) {
      return this;
   }
}
