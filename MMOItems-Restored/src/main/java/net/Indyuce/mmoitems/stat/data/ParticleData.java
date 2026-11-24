package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.version.VParticle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.particle.api.ParticleRunnable;
import net.Indyuce.mmoitems.particle.api.ParticleType;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.configuration.ConfigurationSection;

public class ParticleData implements StatData, RandomStatData<ParticleData> {
   private final ParticleType type;
   private final Particle particle;
   private final Map<String, Double> modifiers = new HashMap<>();
   private final Color color;

   public ParticleData(JsonObject var1) {
      this.particle = Particle.valueOf(var1.get("Particle").getAsString());
      this.type = ParticleType.valueOf(var1.get("Type").getAsString());
      if (var1.has("Color")) {
         JsonObject var2 = var1.getAsJsonObject("Color");
         this.color = Color.fromRGB(var2.get("Red").getAsInt(), var2.get("Green").getAsInt(), var2.get("Blue").getAsInt());
      } else {
         this.color = Color.fromRGB(255, 0, 0);
      }

      var1.getAsJsonObject("Modifiers").entrySet().forEach(var1x -> this.setModifier((String)var1x.getKey(), ((JsonElement)var1x.getValue()).getAsDouble()));
   }

   public ParticleData(ConfigurationSection var1) {
      Validate.isTrue(var1.contains("type") && var1.contains("particle"), "Particle is missing type or selected particle.");
      String var2 = var1.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
      this.type = ParticleType.valueOf(var2);
      var2 = var1.getString("particle").toUpperCase().replace("-", "_").replace(" ", "_");
      this.particle = Particle.valueOf(var2);
      this.color = var1.contains("color")
         ? Color.fromRGB(var1.getInt("color.red"), var1.getInt("color.green"), var1.getInt("color.blue"))
         : Color.fromRGB(255, 0, 0);

      for (String var4 : var1.getKeys(false)) {
         if (!var4.equalsIgnoreCase("particle") && !var4.equalsIgnoreCase("type") && !var4.equalsIgnoreCase("color")) {
            this.setModifier(var4, var1.getDouble(var4));
         }
      }
   }

   public ParticleData(ParticleType var1, Particle var2) {
      this.type = var1;
      this.particle = var2;
      this.color = Color.fromRGB(255, 0, 0);
   }

   public ParticleType getType() {
      return this.type;
   }

   public Particle getParticle() {
      return this.particle;
   }

   public Color getColor() {
      return this.color;
   }

   public double getModifier(String var1) {
      return this.modifiers.containsKey(var1) ? this.modifiers.get(var1) : this.type.getModifier(var1);
   }

   public Set<String> getModifiers() {
      return this.modifiers.keySet();
   }

   public void setModifier(String var1, double var2) {
      this.modifiers.put(var1, var2);
   }

   public void display(Location var1, float var2) {
      this.display(var1, 1, 0.0F, 0.0F, 0.0F, var2);
   }

   public void display(Location var1, int var2, float var3, float var4, float var5, float var6) {
      if (this.particle == VParticle.REDSTONE.get()) {
         var1.getWorld().spawnParticle(this.particle, var1, var2, var3, var4, var5, new DustOptions(this.color, 1.0F));
      } else if (this.particle != VParticle.ENTITY_EFFECT.get() && this.particle != VParticle.ENTITY_EFFECT_AMBIENT.get()) {
         var1.getWorld().spawnParticle(this.particle, var1, var2, var3, var4, var5, var6);
      } else {
         for (int var7 = 0; var7 < var2; var7++) {
            var1.getWorld()
               .spawnParticle(this.particle, var1, 0, this.color.getRed() / 255.0F, this.color.getGreen() / 255.0F, this.color.getBlue() / 255.0F, 1.0);
         }
      }
   }

   public ParticleRunnable start(PlayerData var1) {
      ParticleRunnable var2 = this.type.newRunnable(this, var1);
      var2.runTaskTimer(MMOItems.plugin, 0L, this.type.getTime());
      return var2;
   }

   public JsonObject toJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("Particle", this.getParticle().name());
      var1.addProperty("Type", this.getType().name());
      if (MMOUtils.isColorable(this.particle)) {
         JsonObject var2 = new JsonObject();
         var2.addProperty("Red", this.getColor().getRed());
         var2.addProperty("Green", this.getColor().getGreen());
         var2.addProperty("Blue", this.getColor().getBlue());
         var1.add("Color", var2);
      }

      JsonObject var3 = new JsonObject();
      this.getModifiers().forEach(var2x -> var3.addProperty(var2x, this.getModifier(var2x)));
      var1.add("Modifiers", var3);
      return var1;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   public ParticleData randomize(MMOItemBuilder var1) {
      return this;
   }

   public void spawn(Location var1, Particle var2, int var3, double var4, double var6, double var8, double var10) {
   }
}
