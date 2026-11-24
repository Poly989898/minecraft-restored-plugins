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
   public void openForPlayer(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      new RMG_Shapeless(editionInventory.getPlayer(), editionInventory.getEdited(), text, this).open(editionInventory);
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
      }

      ArrayList list = new ArrayList();
      ArrayList<String> texts = new ArrayList(section2.getStringList("input"));
      boolean flag = false;

      for (String text2 : texts) {
         if (text2 != null && !"AIR".equals(text2)) {
            ProvidedUIFilter ingredientFrom = RecipeEditorGUI.readIngredientFrom(text2, friendlyFeedbackProvider);
            if (!ingredientFrom.isAir()) {
               flag = true;
               list.add(new MythicRecipeIngredient(ingredientFrom));
            }
         }
      }

      if (!flag) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Shapeless recipe containing only AIR, $fignored$b.", new String[0])
         );
      }

      ShapelessRecipe shapelessRecipe = new ShapelessRecipe(namespacedKey.getKey(), list);
      ShapedRecipe shapedRecipe = RMGRR_Shaped.shapedRecipeFromList(namespacedKey.getKey(), new ArrayList<>(section2.getStringList("output")), friendlyFeedbackProvider);
      int amount = section2.getInt("amount", 1);
      boolean hidden = section2.getBoolean("hidden", false);
      ShapedRecipe shapedRecipe2 = ShapedRecipe.single(
         namespacedKey.getKey(), new ProvidedUIFilter[]{new ProvidedUIFilter(MMOItemUIFilter.get(), mmoItemTemplate.getType().getId(), mmoItemTemplate.getId(), Math.max(amount, 1))}
      );
      MRORecipe mroRecipe = new MRORecipe(shapedRecipe2, shapedRecipe);
      MythicRecipeBlueprint mythicRecipeBlueprint = new MythicRecipeBlueprint(shapelessRecipe, mroRecipe, namespacedKey);
      RandomStatData randomStatData = mmoItemTemplate.getBaseItemData().get(ItemStats.CRAFT_PERMISSION);
      if (randomStatData instanceof StringData) {
         String text2 = ((StringData)randomStatData).getString();
         if (text2 != null) {
            mythicRecipeBlueprint.addRequiredPermission(text2);
         }
      }

      mythicRecipeBlueprint.deploy(MythicRecipeStation.WORKBENCH, ref);
      if (hidden) {
         ref.setValue(null);
      }

      return mythicRecipeBlueprint;
   }
}
