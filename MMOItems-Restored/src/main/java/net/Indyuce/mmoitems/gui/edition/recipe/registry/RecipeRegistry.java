package net.Indyuce.mmoitems.gui.edition.recipe.registry;

import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeBlueprint;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface RecipeRegistry {
   @NotNull
   String getRecipeConfigPath();

   @NotNull
   String getRecipeTypeName();

   @NotNull
   ItemStack getDisplayListItem();

   void openForPlayer(@NotNull EditionInventory var1, @NotNull String var2, Object... var3);

   @NotNull
   MythicRecipeBlueprint sendToMythicLib(
      @NotNull MMOItemTemplate var1,
      @NotNull ConfigurationSection var2,
      @NotNull String var3,
      @NotNull Ref<NamespacedKey> var4,
      @NotNull FriendlyFeedbackProvider var5
   ) throws IllegalArgumentException;
}
