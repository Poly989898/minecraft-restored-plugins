package net.Indyuce.mmoitems.api.recipe.workbench.ingredients;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;

@Deprecated
public class MMOItemIngredient extends WorkbenchIngredient {
   private final Type type;
   private final String id;

   public MMOItemIngredient(Type type, String text, int count) {
      super(count);
      this.type = type;
      this.id = text;
   }

   @Override
   public boolean corresponds(ItemStack item) {
      NBTItem nbtItem = NBTItem.get(item);
      return this.type.equals(Type.get(nbtItem.getType())) && nbtItem.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(this.id);
   }

   @Override
   public ItemStack generateItem() {
      return MMOItems.plugin.getItem(this.type, this.id);
   }

   @Override
   public RecipeChoice toBukkit() {
      return new ExactChoice(this.generateItem());
   }
}
