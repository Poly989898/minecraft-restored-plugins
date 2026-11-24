package fr.phoenixdevt.mmoprofiles.bukkit.profile.data.json;

import fr.phoenixdevt.profiles.util.WrappedLocation;
import io.lumine.mythic.lib.gson.JsonDeserializationContext;
import io.lumine.mythic.lib.gson.JsonDeserializer;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParseException;
import io.lumine.mythic.lib.gson.JsonSerializationContext;
import io.lumine.mythic.lib.gson.JsonSerializer;
import java.lang.reflect.Type;

public class WrappedLocationAdapter implements JsonSerializer<WrappedLocation>, JsonDeserializer<WrappedLocation> {
   public WrappedLocation deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject object = element.getAsJsonObject();
      return new WrappedLocation(
         object.get("World").getAsString(),
         object.get("X").getAsDouble(),
         object.get("Y").getAsDouble(),
         object.get("Z").getAsDouble(),
         object.get("Yaw").getAsFloat(),
         object.get("Pitch").getAsFloat()
      );
   }

   public JsonElement serialize(WrappedLocation location, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject object = new JsonObject();
      object.addProperty("World", location.getWorldName());
      object.addProperty("X", location.getX());
      object.addProperty("Y", location.getY());
      object.addProperty("Z", location.getZ());
      object.addProperty("Yaw", location.getYaw());
      object.addProperty("Pitch", location.getPitch());
      return object;
   }
}
