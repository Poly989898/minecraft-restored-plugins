package io.lumine.mythic.lib.util.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectAdapter implements JsonSerializer<PotionEffect>, JsonDeserializer<PotionEffect> {
   public JsonElement serialize(PotionEffect effect, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject object = new JsonObject();
      object.addProperty("Type", effect.getType().getName());
      object.addProperty("Duration", effect.getDuration());
      object.addProperty("Amplifier", effect.getAmplifier());
      object.addProperty("Ambient", effect.isAmbient());
      object.addProperty("Particles", effect.hasParticles());
      object.addProperty("Icon", effect.hasIcon());
      return object;
   }

   public PotionEffect deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject object = element.getAsJsonObject();
      return new PotionEffect(
         PotionEffectType.getByName(object.get("Type").getAsString()),
         object.get("Duration").getAsInt(),
         object.get("Amplifier").getAsInt(),
         object.get("Ambient").getAsBoolean(),
         object.get("Particles").getAsBoolean(),
         object.get("Icon").getAsBoolean()
      );
   }
}
