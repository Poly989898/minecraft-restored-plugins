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
      ConfigurationSection section2 = RecipeEditorGUI.getSection(section, text);
      NamespacedKey namespacedKey = (NamespacedKey)ref.getValue();
      if (namespacedKey == null) {
         throw new IllegalArgumentException(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Illegal (Null) Namespace", new String[0]));
      } else {
         String text2 = RMGRI_Smithing.updateIngredients(section2.getString("input"));
         String[] texts = text2.split("\\|");
         ProvidedUIFilter ingredientFrom = RecipeEditorGUI.readIngredientFrom(texts[0], friendlyFeedbackProvider);
         ProvidedUIFilter ingredientFrom2 = RecipeEditorGUI.readIngredientFrom(texts[1], friendlyFeedbackProvider);
         if (!ingredientFrom.isAir() && !ingredientFrom2.isAir()) {
            MythicRecipeIngredient mythicRecipeIngredient = new MythicRecipeIngredient(ingredientFrom);
            MythicRecipeIngredient mythicRecipeIngredient2 = new MythicRecipeIngredient(ingredientFrom2);
            ShapelessRecipe shapelessRecipe = new ShapelessRecipe(namespacedKey.getKey(), new MythicRecipeIngredient[]{mythicRecipeIngredient});
            ShapelessRecipe shapelessRecipe2 = new ShapelessRecipe(namespacedKey.getKey(), new MythicRecipeIngredient[]{mythicRecipeIngredient2});
            String text3 = RMGRI_Smithing.updateIngredients(section2.getString("output"));
            String[] texts2 = text3.split("\\|");
            ProvidedUIFilter ingredientFrom3 = RecipeEditorGUI.readIngredientFrom(texts2[0], friendlyFeedbackProvider);
            ProvidedUIFilter ingredientFrom4 = RecipeEditorGUI.readIngredientFrom(texts2[1], friendlyFeedbackProvider);
            ShapedRecipe shapedRecipe = ingredientFrom3.isAir() ? null : ShapedRecipe.single(namespacedKey.getKey(), new ProvidedUIFilter[]{ingredientFrom3});
            ShapedRecipe shapedRecipe2 = ingredientFrom4.isAir() ? null : ShapedRecipe.single(namespacedKey.getKey(), new ProvidedUIFilter[]{ingredientFrom4});
            int amount = section2.getInt("amount", 1);
            boolean dropGems = section2.getBoolean("drop-gems", false);
            SmithingCombinationType sCT = this.readSCT(section2.getString("enchantments"));
            SmithingCombinationType sCT2 = this.readSCT(section2.getString("upgrades"));
            CustomSmithingRecipe customSmithingRecipe = new CustomSmithingRecipe(mmoItemTemplate, dropGems, sCT, sCT2, amount);
            customSmithingRecipe.setMainInputConsumption(shapedRecipe);
            customSmithingRecipe.setIngotInputConsumption(shapedRecipe2);
            MythicRecipeBlueprint mythicRecipeBlueprint = new MythicRecipeBlueprint(shapelessRecipe, customSmithingRecipe, namespacedKey);
            mythicRecipeBlueprint.addSideCheck("ingot", shapelessRecipe2);
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
