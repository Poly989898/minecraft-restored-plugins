package net.Indyuce.mmoitems.gui.edition.recipe.registry;

import io.lumine.mythic.lib.api.crafting.ingredients.ShapedIngredient;
import io.lumine.mythic.lib.api.crafting.outputs.MRORecipe;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeBlueprint;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeStation;
import io.lumine.mythic.lib.api.crafting.recipes.ShapedRecipe;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.crafting.MMOItemUIFilter;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RMG_Shaped;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_Shaped;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMGRR_Shaped implements RecipeRegistry {
   @NotNull
   final ItemStack displayListItem = RecipeEditorGUI.rename(new ItemStack(Material.CRAFTING_TABLE), FFPMMOItems.get().getExampleFormat() + "Shaped Recipe");

   @NotNull
   @Override
   public String getRecipeConfigPath() {
      return "shaped";
   }

   @NotNull
   @Override
   public String getRecipeTypeName() {
      return "Shaped";
   }

   @NotNull
   @Override
   public ItemStack getDisplayListItem() {
      return this.displayListItem;
   }

   @Override
   public void openForPlayer(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      new RMG_Shaped(var1.getPlayer(), var1.getEdited(), var2, this).open(var1);
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
      ConfigurationSection var6 = RecipeEditorGUI.moveInput(var2, var3);
      NamespacedKey var7 = (NamespacedKey)var4.getValue();
      if (var7 == null) {
         throw new IllegalArgumentException(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Illegal (Null) Namespace", new String[0]));
      }

      ShapedRecipe var8 = shapedRecipeFromList(var7.getKey(), new ArrayList<>(var6.getStringList("input")), var5);
      if (var8 == null) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Shaped recipe containing only AIR, $fignored$b.", new String[0])
         );
      }

      ShapedRecipe var9 = shapedRecipeFromList(var7.getKey(), new ArrayList<>(var6.getStringList("output")), var5);
      int var10 = var6.getInt("amount", 1);
      boolean var11 = var6.getBoolean("hidden", false);
      ShapedRecipe var12 = ShapedRecipe.single(
         var7.getKey(), new ProvidedUIFilter[]{new ProvidedUIFilter(MMOItemUIFilter.get(), var1.getType().getId(), var1.getId(), Math.max(var10, 1))}
      );
      MRORecipe var13 = new MRORecipe(var12, var9);
      MythicRecipeBlueprint var14 = new MythicRecipeBlueprint(var8, var13, var7);
      RandomStatData var15 = var1.getBaseItemData().get(ItemStats.CRAFT_PERMISSION);
      if (var15 instanceof StringData) {
         String var16 = ((StringData)var15).getString();
         if (var16 != null) {
            var14.addRequiredPermission(var16);
         }
      }

      var14.deploy(MythicRecipeStation.WORKBENCH, var4);
      if (var11) {
         var4.setValue(null);
      }

      return var14;
   }

   @Nullable
   public static ShapedRecipe shapedRecipeFromList(@NotNull String var0, @NotNull ArrayList<String> var1, @NotNull FriendlyFeedbackProvider var2) {
      ArrayList var3 = new ArrayList();
      boolean var4 = false;
      int var5 = 0;

      for (String var7 : var1) {
         String var8 = RMGRI_Shaped.updateRow(var7);
         String[] var9;
         if (var8.contains("|")) {
            var9 = var8.split("\\|");
         } else {
            var9 = var8.split(" ");
         }

         if (var9.length != 3) {
            throw new IllegalArgumentException("Invalid crafting table row $u" + var8 + "$b ($fNot exactly 3 ingredients wide$b).");
         }

         ProvidedUIFilter var10 = RecipeEditorGUI.readIngredientFrom(var9[0], var2);
         ProvidedUIFilter var11 = RecipeEditorGUI.readIngredientFrom(var9[1], var2);
         ProvidedUIFilter var12 = RecipeEditorGUI.readIngredientFrom(var9[2], var2);
         if (!var10.isAir()) {
            var4 = true;
         }

         if (!var11.isAir()) {
            var4 = true;
         }

         if (!var12.isAir()) {
            var4 = true;
         }

         ShapedIngredient var13 = new ShapedIngredient(var10, 0, -var5);
         ShapedIngredient var14 = new ShapedIngredient(var11, 1, -var5);
         ShapedIngredient var15 = new ShapedIngredient(var12, 2, -var5);
         var3.add(var13);
         var3.add(var14);
         var3.add(var15);
         var5++;
      }

      return !var4 ? null : ShapedRecipe.unsharpen(new ShapedRecipe(var0, var3));
   }
}
