package io.lumine.mythic.lib.util.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.UUID;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;

public class LegacyAttributeModifierAdapter implements JsonSerializer<AttributeModifier>, JsonDeserializer<AttributeModifier> {
   public AttributeModifier deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject object = element.getAsJsonObject();
      return new AttributeModifier(
         UUID.fromString(object.get("UUID").getAsString()),
         object.get("Name").getAsString(),
         object.get("Amount").getAsDouble(),
         Operation.valueOf(object.get("Operation").getAsString()),
         object.has("Slot") ? EquipmentSlot.valueOf(object.get("Slot").getAsString()) : null
      );
   }

   public JsonElement serialize(AttributeModifier modifier, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject object = new JsonObject();
      object.addProperty("UUID", modifier.getUniqueId().toString());
      object.addProperty("Name", modifier.getName());
      object.addProperty("Amount", modifier.getAmount());
      object.addProperty("Operation", modifier.getOperation().name());
      if (modifier.getSlot() != null) {
         object.addProperty("Slot", modifier.getSlot().name());
      }

      return object;
   }
}
