package io.lumine.mythic.lib.util.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
   public Location deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject object = element.getAsJsonObject();
      return new Location(
         Bukkit.getWorld(object.get("World").getAsString()),
         object.get("X").getAsDouble(),
         object.get("Y").getAsDouble(),
         object.get("Z").getAsDouble(),
         object.get("Yaw").getAsFloat(),
         object.get("Pitch").getAsFloat()
      );
   }

   public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject object = new JsonObject();
      object.addProperty("World", location.getWorld().getName());
      object.addProperty("X", location.getX());
      object.addProperty("Y", location.getY());
      object.addProperty("Z", location.getZ());
      object.addProperty("Yaw", location.getYaw());
      object.addProperty("Pitch", location.getPitch());
      return object;
   }
}
