package net.Indyuce.mmoitems.gui.edition.recipe.registry;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeIngredient;
import io.lumine.mythic.lib.api.crafting.outputs.MRORecipe;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeBlueprint;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeStation;
import io.lumine.mythic.lib.api.crafting.recipes.ShapedRecipe;
import io.lumine.mythic.lib.api.crafting.recipes.ShapelessRecipe;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.crafting.MMOItemUIFilter;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RMG_Shapeless;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RMGRR_Shapeless implements RecipeRegistry {
   @NotNull
   final ItemStack displayListItem = RecipeEditorGUI.rename(new ItemStack(Material.OAK_LOG), FFPMMOItems.get().getExampleFormat() + "Shapeless Recipe");

   @NotNull
   @Override
   public String getRecipeTypeName() {
      return "Shapeless";
   }

   @NotNull
   @Override
   public String getRecipeConfigPath() {
      return "shapeless";
   }

   @NotNull
   @Override
   public ItemStack getDisplayListItem() {
      return this.displayListItem;
   }

   @Override
   public void openForPlayer(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      new RMG_Shapeless(var1.getPlayer(), var1.getEdited(), var2, this).open(var1);
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
      }

      ArrayList var8 = new ArrayList();
      ArrayList<String> var9 = new ArrayList(var6.getStringList("input"));
      boolean var10 = false;

      for (String var12 : var9) {
         if (var12 != null && !"AIR".equals(var12)) {
            ProvidedUIFilter var13 = RecipeEditorGUI.readIngredientFrom(var12, var5);
            if (!var13.isAir()) {
               var10 = true;
               var8.add(new MythicRecipeIngredient(var13));
            }
         }
      }

      if (!var10) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Shapeless recipe containing only AIR, $fignored$b.", new String[0])
         );
      }

      ShapelessRecipe var20 = new ShapelessRecipe(var7.getKey(), var8);
      ShapedRecipe var21 = RMGRR_Shaped.shapedRecipeFromList(var7.getKey(), new ArrayList<>(var6.getStringList("output")), var5);
      int var22 = var6.getInt("amount", 1);
      boolean var14 = var6.getBoolean("hidden", false);
      ShapedRecipe var15 = ShapedRecipe.single(
         var7.getKey(), new ProvidedUIFilter[]{new ProvidedUIFilter(MMOItemUIFilter.get(), var1.getType().getId(), var1.getId(), Math.max(var22, 1))}
      );
      MRORecipe var16 = new MRORecipe(var15, var21);
      MythicRecipeBlueprint var17 = new MythicRecipeBlueprint(var20, var16, var7);
      RandomStatData var18 = var1.getBaseItemData().get(ItemStats.CRAFT_PERMISSION);
      if (var18 instanceof StringData) {
         String var19 = ((StringData)var18).getString();
         if (var19 != null) {
            var17.addRequiredPermission(var19);
         }
      }

      var17.deploy(MythicRecipeStation.WORKBENCH, var4);
      if (var14) {
         var4.setValue(null);
      }

      return var17;
   }
}
