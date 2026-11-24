package io.lumine.mythic.lib.player.particle;

import com.google.gson.JsonObject;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.Objects;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public class ParticleInformation {
   private final Particle particle;
   private final int amount;
   private final double rOffset;
   private final double speed;
   @Nullable
   private final Color color;
   private final float size;
   @Nullable
   private final BlockData blockData;

   public ParticleInformation(Particle particle) {
      this(particle, 1, 0.0F, 0.0, null, 1.0F);
   }

   public ParticleInformation(Particle particle, int amount, float speed, double rOffset, Color color, float size) {
      this.particle = particle;
      this.amount = amount;
      this.rOffset = rOffset;
      this.speed = speed;
      this.color = color;
      this.size = size;
      this.blockData = null;
   }

   public ParticleInformation(Particle particle, int amount, float speed, double rOffset, Material mat) {
      this.particle = particle;
      this.amount = amount;
      this.rOffset = rOffset;
      this.speed = speed;
      this.color = null;
      this.size = 1.0F;
      this.blockData = mat.createBlockData();
   }

   public ParticleInformation(ConfigObject obj) {
      this.particle = Particle.valueOf(obj.getString("name"));
      this.color = obj.contains("color") ? this.readColor(obj.getObject("color")) : null;
      this.size = (float)obj.getDouble("size", 1.0);
      this.rOffset = this.speed = 0.0;
      this.amount = 1;
      this.blockData = null;
   }

   public ParticleInformation(JsonObject object) {
      this.particle = Particle.valueOf(object.get("Particle").getAsString());
      this.amount = object.get("Amount").getAsInt();
      this.rOffset = object.get("Offset").getAsDouble();
      boolean colored = object.get("Colored").getAsBoolean();
      this.color = colored ? Color.fromRGB(object.get("Red").getAsInt(), object.get("Green").getAsInt(), object.get("Blue").getAsInt()) : null;
      this.speed = colored ? 0.0 : object.get("Speed").getAsFloat();
      this.size = 1.0F;
      this.blockData = !colored && object.has("Material") ? Material.valueOf(object.get("Material").getAsString()).createBlockData() : null;
   }

   public void display(Location loc) {
      this.display(loc, this.amount, this.rOffset, this.rOffset, this.rOffset, this.speed);
   }

   public void display(Location loc, double speed) {
      this.display(loc, this.amount, this.rOffset, this.rOffset, this.rOffset, speed);
   }

   public void display(Location loc, int amount, double x, double y, double z, double speed) {
      Class<?> dataType = this.particle.getDataType();
      if (dataType == DustOptions.class) {
         loc.getWorld()
            .spawnParticle(this.particle, loc, amount, x, y, z, speed, new DustOptions(Objects.requireNonNull(this.color, "No color provided"), this.size));
      } else if (dataType == BlockData.class) {
         loc.getWorld().spawnParticle(this.particle, loc, amount, x, y, z, speed, Objects.requireNonNull(this.blockData, "No material provided"));
      } else {
         loc.getWorld().spawnParticle(this.particle, loc, amount, x, y, z, speed);
      }
   }

   private Color readColor(ConfigObject obj) {
      return Color.fromRGB(obj.getInt("red"), obj.getInt("green"), obj.getInt("blue"));
   }
}
