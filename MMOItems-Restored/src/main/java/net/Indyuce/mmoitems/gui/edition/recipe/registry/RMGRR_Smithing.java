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
   public void openForPlayer(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      new RMG_Smithing(var1.getPlayer(), var1.getEdited(), var2, this).open(var1);
   }

   @NotNull
   @Override
   public MythicRecipeBlueprint sendToMythicLib(
      @NotNull MMOItemTemplate var1,
      @NotNull ConfigurationSection var2,
      @NotNull String var3,
      @NotNull Ref<NamespacedKey> var4,
      @NotNull FriendlyFeedbackProvider var5
   ) {
      RecipeEditorGUI.moveInput(var2, var3);
      ConfigurationSection var6 = RecipeEditorGUI.getSection(var2, var3);
      NamespacedKey var7 = (NamespacedKey)var4.getValue();
      if (var7 == null) {
         throw new IllegalArgumentException(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Illegal (Null) Namespace", new String[0]));
      } else {
         String var8 = RMGRI_Smithing.updateIngredients(var6.getString("input"));
         String[] var9 = var8.split("\\|");
         ProvidedUIFilter var10 = RecipeEditorGUI.readIngredientFrom(var9[0], var5);
         ProvidedUIFilter var11 = RecipeEditorGUI.readIngredientFrom(var9[1], var5);
         if (!var10.isAir() && !var11.isAir()) {
            MythicRecipeIngredient var12 = new MythicRecipeIngredient(var10);
            MythicRecipeIngredient var13 = new MythicRecipeIngredient(var11);
            ShapelessRecipe var14 = new ShapelessRecipe(var7.getKey(), new MythicRecipeIngredient[]{var12});
            ShapelessRecipe var15 = new ShapelessRecipe(var7.getKey(), new MythicRecipeIngredient[]{var13});
            String var16 = RMGRI_Smithing.updateIngredients(var6.getString("output"));
            String[] var17 = var16.split("\\|");
            ProvidedUIFilter var18 = RecipeEditorGUI.readIngredientFrom(var17[0], var5);
            ProvidedUIFilter var19 = RecipeEditorGUI.readIngredientFrom(var17[1], var5);
            ShapedRecipe var20 = var18.isAir() ? null : ShapedRecipe.single(var7.getKey(), new ProvidedUIFilter[]{var18});
            ShapedRecipe var21 = var19.isAir() ? null : ShapedRecipe.single(var7.getKey(), new ProvidedUIFilter[]{var19});
            int var22 = var6.getInt("amount", 1);
            boolean var23 = var6.getBoolean("drop-gems", false);
            SmithingCombinationType var24 = this.readSCT(var6.getString("enchantments"));
            SmithingCombinationType var25 = this.readSCT(var6.getString("upgrades"));
            CustomSmithingRecipe var26 = new CustomSmithingRecipe(var1, var23, var24, var25, var22);
            var26.setMainInputConsumption(var20);
            var26.setIngotInputConsumption(var21);
            MythicRecipeBlueprint var27 = new MythicRecipeBlueprint(var14, var26, var7);
            var27.addSideCheck("ingot", var15);
            var27.deploy(MythicRecipeStation.SMITHING, var4);
            return var27;
         } else {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Smithing recipe containing AIR, $fignored$b.", new String[0])
            );
         }
      }
   }

   @NotNull
   SmithingCombinationType readSCT(@Nullable String var1) {
      if (var1 == null) {
         return SmithingCombinationType.MAXIMUM;
      }

      try {
         return SmithingCombinationType.valueOf(var1);
      } catch (IllegalArgumentException var3) {
         return SmithingCombinationType.MAXIMUM;
      }
   }
}
