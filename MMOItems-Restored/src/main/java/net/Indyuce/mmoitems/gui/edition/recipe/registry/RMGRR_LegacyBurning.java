package net.Indyuce.mmoitems.gui.edition.recipe.registry;

import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeBlueprint;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.recipe.workbench.ingredients.WorkbenchIngredient;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RMG_BurningLegacy;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.burninglegacy.BurningRecipeInformation;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.burninglegacy.CraftingType;
import net.Indyuce.mmoitems.manager.RecipeManager;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class RMGRR_LegacyBurning implements RecipeRegistry {
   private final CraftingType craftingType;
   @NotNull
   private final ItemStack displayListItem;

   public RMGRR_LegacyBurning(CraftingType craftingType) {
      this.craftingType = craftingType;
      this.displayListItem = RecipeEditorGUI.rename(
         craftingType.getItem(), FFPMMOItems.get().getExampleFormat() + capitalizeFirst(this.getRecipeConfigPath()) + " Recipe"
      );
   }

   @NotNull
   public CraftingType getLegacyBurningType() {
      return this.craftingType;
   }

   @NotNull
   public static String capitalizeFirst(@NotNull String text) {
      return text.substring(0, 1).toUpperCase() + text.substring(1);
   }

   @NotNull
   @Override
   public String getRecipeConfigPath() {
      return this.craftingType.name().toLowerCase();
   }

   @NotNull
   @Override
   public String getRecipeTypeName() {
      return "§8{§4§oL§8} " + capitalizeFirst(this.getRecipeConfigPath());
   }

   @NotNull
   @Override
   public ItemStack getDisplayListItem() {
      return this.displayListItem;
   }

   @Override
   public void openForPlayer(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      new RMG_BurningLegacy(editionInventory.getPlayer(), editionInventory.getEdited(), text, this).open(editionInventory);
   }

   @NotNull
   @Override
   public MythicRecipeBlueprint sendToMythicLib(
      @NotNull MMOItemTemplate mmoItemTemplate,
      @NotNull ConfigurationSection section,
      @NotNull String text,
      @NotNull Ref<NamespacedKey> ref,
      @NotNull FriendlyFeedbackProvider friendlyFeedbackProvider
   ) {
      Validate.isTrue(ref.getValue() != null);
      ConfigurationSection recipeSection = RecipeEditorGUI.getSection(section, text);
      String item = recipeSection.getString("item");
      if (item == null) {
         throw new IllegalArgumentException("Missing input ingredient");
      }

      WorkbenchIngredient workbenchIngredient = RecipeManager.getWorkbenchIngredient(item);
      int amount = recipeSection.getInt("amount", 1);
      double exp = recipeSection.getDouble("exp", 0.35);
      int time = recipeSection.getInt("time", SilentNumbers.round(200.0));
      boolean hidden = recipeSection.getBoolean("hidden", false);
      BurningRecipeInformation burningRecipeInformation = new BurningRecipeInformation(workbenchIngredient, (float)exp, time);
      MMOItems.plugin
         .getRecipes()
         .registerBurningRecipe(this.craftingType.getBurningType(), mmoItemTemplate.newBuilder(0, null).build(), burningRecipeInformation, amount, (NamespacedKey)ref.getValue(), hidden);
      throw new IllegalArgumentException("");
   }
}
