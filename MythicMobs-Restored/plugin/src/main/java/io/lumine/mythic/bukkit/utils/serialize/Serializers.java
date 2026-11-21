package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class Serializers {
   public static JsonPrimitive serializeItemstack(ItemStack item) {
      return JsonBuilder.primitiveNonNull(InventorySerialization.encodeItemStackToString(item));
   }

   public static ItemStack deserializeItemstack(JsonElement data) {
      Preconditions.checkArgument(data.isJsonPrimitive());
      return InventorySerialization.decodeItemStack(data.getAsString());
   }

   public static JsonPrimitive serializeItemstacks(ItemStack[] items) {
      return JsonBuilder.primitiveNonNull(InventorySerialization.encodeItemStacksToString(items));
   }

   public static JsonPrimitive serializeInventory(Inventory inventory) {
      return JsonBuilder.primitiveNonNull(InventorySerialization.encodeInventoryToString(inventory));
   }

   public static ItemStack[] deserializeItemstacks(JsonElement data) {
      Preconditions.checkArgument(data.isJsonPrimitive());
      return InventorySerialization.decodeItemStacks(data.getAsString());
   }

   public static Inventory deserializeInventory(JsonElement data, String title) {
      Preconditions.checkArgument(data.isJsonPrimitive());
      return InventorySerialization.decodeInventory(data.getAsString(), title);
   }

   private Serializers() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
