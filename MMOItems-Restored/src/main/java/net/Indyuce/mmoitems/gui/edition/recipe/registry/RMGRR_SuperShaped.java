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
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RMG_SuperShaped;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_SuperShaped;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RMGRR_SuperShaped implements RecipeRegistry {
   @NotNull
   final ItemStack displayListItem = RecipeEditorGUI.rename(new ItemStack(Material.NOTE_BLOCK), FFPMMOItems.get().getExampleFormat() + "Super Shaped Recipe");

   @NotNull
   @Override
   public String getRecipeConfigPath() {
      return "supershaped";
   }

   @NotNull
   @Override
   public String getRecipeTypeName() {
      return "Super Shaped";
   }

   @NotNull
   @Override
   public ItemStack getDisplayListItem() {
      return this.displayListItem;
   }

   @Override
   public void openForPlayer(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      new RMG_SuperShaped(var1.getPlayer(), var1.getEdited(), var2, this).open(var1);
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

      ShapedRecipe var8 = superShapedRecipeFromList(var7.getKey(), new ArrayList<>(var6.getStringList("input")), var5);
      if (var8 == null) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Shaped recipe containing only AIR, $fignored$b.", new String[0])
         );
      }

      ShapedRecipe var9 = superShapedRecipeFromList(var7.getKey(), new ArrayList<>(var6.getStringList("output")), var5);
      int var10 = var6.getInt("amount", 1);
      ShapedRecipe var11 = ShapedRecipe.single(
         var7.getKey(), new ProvidedUIFilter[]{new ProvidedUIFilter(MMOItemUIFilter.get(), var1.getType().getId(), var1.getId(), Math.max(var10, 1))}
      );
      MRORecipe var12 = new MRORecipe(var11, var9);
      MythicRecipeBlueprint var13 = new MythicRecipeBlueprint(var8, var12, var7);
      RandomStatData var14 = var1.getBaseItemData().get(ItemStats.CRAFT_PERMISSION);
      if (var14 instanceof StringData) {
         String var15 = ((StringData)var14).getString();
         if (var15 != null) {
            var13.addRequiredPermission(var15);
         }
      }

      var13.deploy(MythicRecipeStation.WORKBENCH, null);
      var4.setValue(null);
      return var13;
   }

   @Nullable
   public static ShapedRecipe superShapedRecipeFromList(@NotNull String var0, @NotNull ArrayList<String> var1, @NotNull FriendlyFeedbackProvider var2) {
      ArrayList var3 = new ArrayList();
      boolean var4 = false;
      int var5 = 0;

      for (String var7 : var1) {
         String var8 = RMGRI_SuperShaped.updateRow(var7);
         String[] var9;
         if (var8.contains("|")) {
            var9 = var8.split("\\|");
         } else {
            var9 = var8.split(" ");
         }

         if (var9.length != 5) {
            throw new IllegalArgumentException("Invalid super crafting table row $u" + var8 + "$b ($fNot exactly 5 ingredients wide$b).");
         }

         ProvidedUIFilter var10 = RecipeEditorGUI.readIngredientFrom(var9[0], var2);
         ProvidedUIFilter var11 = RecipeEditorGUI.readIngredientFrom(var9[1], var2);
         ProvidedUIFilter var12 = RecipeEditorGUI.readIngredientFrom(var9[2], var2);
         ProvidedUIFilter var13 = RecipeEditorGUI.readIngredientFrom(var9[3], var2);
         ProvidedUIFilter var14 = RecipeEditorGUI.readIngredientFrom(var9[4], var2);
         if (!var10.isAir()) {
            var4 = true;
         }

         if (!var11.isAir()) {
            var4 = true;
         }

         if (!var12.isAir()) {
            var4 = true;
         }

         if (!var13.isAir()) {
            var4 = true;
         }

         if (!var14.isAir()) {
            var4 = true;
         }

         ShapedIngredient var15 = new ShapedIngredient(var10, 0, -var5);
         ShapedIngredient var16 = new ShapedIngredient(var11, 1, -var5);
         ShapedIngredient var17 = new ShapedIngredient(var12, 2, -var5);
         ShapedIngredient var18 = new ShapedIngredient(var13, 3, -var5);
         ShapedIngredient var19 = new ShapedIngredient(var14, 4, -var5);
         var3.add(var15);
         var3.add(var16);
         var3.add(var17);
         var3.add(var18);
         var3.add(var19);
         var5++;
      }

      return !var4 ? null : ShapedRecipe.unsharpen(new ShapedRecipe(var0, var3));
   }
}
