package io.lumine.mythic.lib.util.gson;

import io.lumine.mythic.lib.MythicLib;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;

public class AttributeModifierAdapter implements JsonSerializer<AttributeModifier>, JsonDeserializer<AttributeModifier> {
   public AttributeModifier deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject object = element.getAsJsonObject();
      NamespacedKey key;
      EquipmentSlotGroup group;
      if (object.has("Name")) {
         key = this.convertToNamespacedKey(object.get("UUID").getAsString(), object.get("Name").getAsString());
         group = object.has("Slot") ? EquipmentSlot.valueOf(object.get("Slot").getAsString()).getGroup() : EquipmentSlotGroup.ANY;
      } else {
         key = NamespacedKey.fromString(object.get("Key").getAsString());
         group = Objects.requireNonNullElse(EquipmentSlotGroup.getByName(object.get("Group").getAsString()), EquipmentSlotGroup.ANY);
      }

      return new AttributeModifier(key, object.get("Amount").getAsDouble(), Operation.valueOf(object.get("Operation").getAsString()), group);
   }

   private NamespacedKey convertToNamespacedKey(String uuid, String name) {
      return !name.startsWith("mythiclib.") && !name.startsWith("mmoitems.") && !name.startsWith("mmolib.")
         ? new NamespacedKey(MythicLib.plugin, uuid)
         : new NamespacedKey(MythicLib.plugin, name.split("\\.", 2)[1]);
   }

   public JsonElement serialize(AttributeModifier modifier, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject object = new JsonObject();
      object.addProperty("Key", modifier.getKey().toString());
      object.addProperty("Amount", modifier.getAmount());
      object.addProperty("Operation", modifier.getOperation().name());
      object.addProperty("Group", modifier.getSlotGroup().toString());
      return object;
   }
}
