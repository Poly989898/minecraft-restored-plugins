package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import io.lumine.mythic.core.skills.variables.types.ItemVariable;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "item"
)
public class ItemSerializer extends PlaceholderClassSerializer<BukkitItemStack> {
   @NotNull
   public String serialize(@NotNull BukkitItemStack object) {
      return ItemVariable.Utils.serializeToString(object);
   }

   @NotNull
   public BukkitItemStack deserialize(@NotNull String object) {
      return ItemVariable.Utils.deserializeFromString(object);
   }
}
