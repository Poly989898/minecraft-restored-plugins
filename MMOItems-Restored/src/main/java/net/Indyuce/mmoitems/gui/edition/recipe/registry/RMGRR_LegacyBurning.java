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

   public RMGRR_LegacyBurning(CraftingType var1) {
      this.craftingType = var1;
      this.displayListItem = RecipeEditorGUI.rename(
         var1.getItem(), FFPMMOItems.get().getExampleFormat() + capitalizeFirst(this.getRecipeConfigPath()) + " Recipe"
      );
   }

   @NotNull
   public CraftingType getLegacyBurningType() {
      return this.craftingType;
   }

   @NotNull
   public static String capitalizeFirst(@NotNull String var0) {
      return var0.substring(0, 1).toUpperCase() + var0.substring(1);
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
   public void openForPlayer(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      new RMG_BurningLegacy(var1.getPlayer(), var1.getEdited(), var2, this).open(var1);
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
      Validate.isTrue(var4.getValue() != null);
      ConfigurationSection var6 = RecipeEditorGUI.getSection(var2, var3);
      String var7 = var6.getString("item");
      if (var7 == null) {
         throw new IllegalArgumentException("Missing input ingredient");
      }

      WorkbenchIngredient var8 = RecipeManager.getWorkbenchIngredient(var7);
      int var9 = var6.getInt("amount", 1);
      double var10 = var6.getDouble("exp", 0.35);
      int var12 = var6.getInt("time", SilentNumbers.round(200.0));
      boolean var13 = var6.getBoolean("hidden", false);
      BurningRecipeInformation var14 = new BurningRecipeInformation(var8, (float)var10, var12);
      MMOItems.plugin
         .getRecipes()
         .registerBurningRecipe(this.craftingType.getBurningType(), var1.newBuilder(0, null).build(), var14, var9, (NamespacedKey)var4.getValue(), var13);
      throw new IllegalArgumentException("");
   }
}
