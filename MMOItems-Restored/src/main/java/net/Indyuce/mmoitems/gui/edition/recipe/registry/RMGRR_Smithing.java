package net.Indyuce.mmoitems.gui.edition.recipe.registry;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeIngredient;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeBlueprint;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeStation;
import io.lumine.mythic.lib.api.crafting.recipes.ShapedRecipe;
import io.lumine.mythic.lib.api.crafting.recipes.ShapelessRecipe;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import net.Indyuce.mmoitems.api.crafting.recipe.CustomSmithingRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.SmithingCombinationType;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RMG_Smithing;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_Smithing;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMGRR_Smithing implements RecipeRegistry {
   @NotNull
   final ItemStack displayListItem = RecipeEditorGUI.rename(new ItemStack(Material.SMITHING_TABLE), FFPMMOItems.get().getExampleFormat() + "Smithing Recipe");

   @NotNull
   @Override
   public String getRecipeTypeName() {
      return "Smithing";
   }

   @NotNull
   @Override
   public String getRecipeConfigPath() {
      return "smithing";
   }

   @NotNull
   @Override
   public ItemStack getDisplayListItem() {
      return this.displayListItem;
   }

   @Override
   public void openForPlayer(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      new RMG_Smithing(editionInventory.getPlayer(), editionInventory.getEdited(), text, this).open(editionInventory);
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
      RecipeEditorGUI.moveInput(section, text);
      ConfigurationSection inputSection = RecipeEditorGUI.getSection(section, text);
      NamespacedKey namespacedKey = (NamespacedKey)ref.getValue();
      if (namespacedKey == null) {
         throw new IllegalArgumentException(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Illegal (Null) Namespace", new String[0]));
      } else {
         String updatedInput = RMGRI_Smithing.updateIngredients(inputSection.getString("input"));
         String[] texts = updatedInput.split("\\|");
         ProvidedUIFilter ingredientFrom = RecipeEditorGUI.readIngredientFrom(texts[0], friendlyFeedbackProvider);
         ProvidedUIFilter ingotFilter = RecipeEditorGUI.readIngredientFrom(texts[1], friendlyFeedbackProvider);
         if (!ingredientFrom.isAir() && !ingotFilter.isAir()) {
            MythicRecipeIngredient mythicRecipeIngredient = new MythicRecipeIngredient(ingredientFrom);
            MythicRecipeIngredient ingotIngredient = new MythicRecipeIngredient(ingotFilter);
            ShapelessRecipe shapelessRecipe = new ShapelessRecipe(namespacedKey.getKey(), new MythicRecipeIngredient[]{mythicRecipeIngredient});
            ShapelessRecipe inputRecipe = new ShapelessRecipe(namespacedKey.getKey(), new MythicRecipeIngredient[]{ingotIngredient});
            String updatedOutput = RMGRI_Smithing.updateIngredients(inputSection.getString("output"));
            String[] outputParts = updatedOutput.split("\\|");
            ProvidedUIFilter outputMainFilter = RecipeEditorGUI.readIngredientFrom(outputParts[0], friendlyFeedbackProvider);
            ProvidedUIFilter outputIngotFilter = RecipeEditorGUI.readIngredientFrom(outputParts[1], friendlyFeedbackProvider);
            ShapedRecipe shapedRecipe = outputMainFilter.isAir() ? null : ShapedRecipe.single(namespacedKey.getKey(), new ProvidedUIFilter[]{outputMainFilter});
            ShapedRecipe outputRecipe = outputIngotFilter.isAir() ? null : ShapedRecipe.single(namespacedKey.getKey(), new ProvidedUIFilter[]{outputIngotFilter});
            int amount = inputSection.getInt("amount", 1);
            boolean dropGems = inputSection.getBoolean("drop-gems", false);
            SmithingCombinationType sCT = this.readSCT(inputSection.getString("enchantments"));
            SmithingCombinationType combinationType = this.readSCT(inputSection.getString("upgrades"));
            CustomSmithingRecipe customSmithingRecipe = new CustomSmithingRecipe(mmoItemTemplate, dropGems, sCT, combinationType, amount);
            customSmithingRecipe.setMainInputConsumption(shapedRecipe);
            customSmithingRecipe.setIngotInputConsumption(outputRecipe);
            MythicRecipeBlueprint mythicRecipeBlueprint = new MythicRecipeBlueprint(shapelessRecipe, customSmithingRecipe, namespacedKey);
            mythicRecipeBlueprint.addSideCheck("ingot", inputRecipe);
            mythicRecipeBlueprint.deploy(MythicRecipeStation.SMITHING, ref);
            return mythicRecipeBlueprint;
         } else {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Smithing recipe containing AIR, $fignored$b.", new String[0])
            );
         }
      }
   }

   @NotNull
   SmithingCombinationType readSCT(@Nullable String text) {
      if (text == null) {
         return SmithingCombinationType.MAXIMUM;
      }

      try {
         return SmithingCombinationType.valueOf(text);
      } catch (IllegalArgumentException exception) {
         return SmithingCombinationType.MAXIMUM;
      }
   }
}
