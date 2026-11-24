package net.Indyuce.mmoitems.gui.edition.recipe.gui;

import java.util.HashMap;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_DropGems;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_InputOutput;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_SmithingEnchantments;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_SmithingUpgrades;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_Smithing;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMG_RecipeInterpreter;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RMG_Smithing extends RecipeEditorGUI {
   @NotNull
   final HashMap<Integer, Integer> inputLinks = new HashMap<>();
   @NotNull
   final RMGRI_Smithing interpreter;

   public RMG_Smithing(@NotNull Player player, @NotNull MMOItemTemplate mmoItemTemplate, @NotNull String text, @NotNull RecipeRegistry recipeRegistry) {
      super(player, mmoItemTemplate, text, recipeRegistry);
      ConfigurationSection section = RecipeEditorGUI.getSection(this.getEditedSection(), "crafting");
      ConfigurationSection section2 = RecipeEditorGUI.getSection(section, this.getRecipeRegistry().getRecipeConfigPath());
      ConfigurationSection section3 = RecipeEditorGUI.getSection(section2, this.getRecipeName());
      this.interpreter = new RMGRI_Smithing(section3);
      this.inputLinks.put(39, 0);
      this.inputLinks.put(41, 1);
      this.addButton(new RBA_InputOutput(this));
      this.addButton(new RBA_SmithingUpgrades(this));
      this.addButton(new RBA_SmithingEnchantments(this));
      this.addButton(new RBA_DropGems(this));
   }

   @Override
   public void putRecipe() {
      for (Integer key : this.inputLinks.keySet()) {
         this.inventory.setItem(key, this.getDisplay(this.isShowingInput(), this.inputLinks.get(key)));
      }
   }

   @Override
   int getInputSlot(int index) {
      Integer value = this.inputLinks.get(index);
      return value != null ? value : -1;
   }

   @Override
   public int getButtonsRow() {
      return 1;
   }

   @NotNull
   @Override
   public RMG_RecipeInterpreter getInterpreter() {
      return this.interpreter;
   }
}
