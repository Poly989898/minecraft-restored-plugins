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

   public ParticleData(JsonObject jsonObject) {
      this.particle = Particle.valueOf(jsonObject.get("Particle").getAsString());
      this.type = ParticleType.valueOf(jsonObject.get("Type").getAsString());
      if (jsonObject.has("Color")) {
         JsonObject asJsonObject = jsonObject.getAsJsonObject("Color");
         this.color = Color.fromRGB(asJsonObject.get("Red").getAsInt(), asJsonObject.get("Green").getAsInt(), asJsonObject.get("Blue").getAsInt());
      } else {
         this.color = Color.fromRGB(255, 0, 0);
      }

      jsonObject.getAsJsonObject("Modifiers").entrySet().forEach(var1x -> this.setModifier((String)var1x.getKey(), ((JsonElement)var1x.getValue()).getAsDouble()));
   }

   public ParticleData(ConfigurationSection section) {
      Validate.isTrue(section.contains("type") && section.contains("particle"), "Particle is missing type or selected particle.");
      String text = section.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
      this.type = ParticleType.valueOf(text);
      text = section.getString("particle").toUpperCase().replace("-", "_").replace(" ", "_");
      this.particle = Particle.valueOf(text);
      this.color = section.contains("color")
         ? Color.fromRGB(section.getInt("color.red"), section.getInt("color.green"), section.getInt("color.blue"))
         : Color.fromRGB(255, 0, 0);

      for (String key : section.getKeys(false)) {
         if (!key.equalsIgnoreCase("particle") && !key.equalsIgnoreCase("type") && !key.equalsIgnoreCase("color")) {
            this.setModifier(key, section.getDouble(key));
         }
      }
   }

   public ParticleData(ParticleType particleType, Particle particle) {
      this.type = particleType;
      this.particle = particle;
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

   public double getModifier(String text) {
      return this.modifiers.containsKey(text) ? this.modifiers.get(text) : this.type.getModifier(text);
   }

   public Set<String> getModifiers() {
      return this.modifiers.keySet();
   }

   public void setModifier(String text, double value) {
      this.modifiers.put(text, value);
   }

   public void display(Location location, float value) {
      this.display(location, 1, 0.0F, 0.0F, 0.0F, value);
   }

   public void display(Location location, int count, float value, float value2, float value3, float value4) {
      if (this.particle == VParticle.REDSTONE.get()) {
         location.getWorld().spawnParticle(this.particle, location, count, value, value2, value3, new DustOptions(this.color, 1.0F));
      } else if (this.particle != VParticle.ENTITY_EFFECT.get() && this.particle != VParticle.ENTITY_EFFECT_AMBIENT.get()) {
         location.getWorld().spawnParticle(this.particle, location, count, value, value2, value3, value4);
      } else {
         for (int i = 0; i < count; i++) {
            location.getWorld()
               .spawnParticle(this.particle, location, 0, this.color.getRed() / 255.0F, this.color.getGreen() / 255.0F, this.color.getBlue() / 255.0F, 1.0);
         }
      }
   }

   public ParticleRunnable start(PlayerData playerData) {
      ParticleRunnable runnable = this.type.newRunnable(this, playerData);
      runnable.runTaskTimer(MMOItems.plugin, 0L, this.type.getTime());
      return runnable;
   }

   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("Particle", this.getParticle().name());
      jsonObject.addProperty("Type", this.getType().name());
      if (MMOUtils.isColorable(this.particle)) {
         JsonObject colorJson = new JsonObject();
         colorJson.addProperty("Red", this.getColor().getRed());
         colorJson.addProperty("Green", this.getColor().getGreen());
         colorJson.addProperty("Blue", this.getColor().getBlue());
         jsonObject.add("Color", colorJson);
      }

      JsonObject colorJson = new JsonObject();
      this.getModifiers().forEach(var2x -> colorJson.addProperty(var2x, this.getModifier(var2x)));
      jsonObject.add("Modifiers", colorJson);
      return jsonObject;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   public ParticleData randomize(MMOItemBuilder mmoItemBuilder) {
      return this;
   }

   public void spawn(Location location, Particle particle, int count, double value, double value2, double value3, double value4) {
   }
}
