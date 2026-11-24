package net.Indyuce.mmoitems.gui.edition.recipe.gui;

import java.util.HashMap;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_HideFromBook;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_InputOutput;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_Shapeless;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMG_RecipeInterpreter;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RMG_Shapeless extends RecipeEditorGUI {
   @NotNull
   final HashMap<Integer, Integer> inputLinks = new HashMap<>();
   @NotNull
   final RMGRI_Shapeless interpreter;

   public RMG_Shapeless(@NotNull Player player, @NotNull MMOItemTemplate mmoItemTemplate, @NotNull String text, @NotNull RecipeRegistry recipeRegistry) {
      super(player, mmoItemTemplate, text, recipeRegistry);
      this.addButton(new RBA_InputOutput(this));
      this.addButton(new RBA_HideFromBook(this));
      ConfigurationSection section = RecipeEditorGUI.getSection(this.getEditedSection(), "crafting");
      ConfigurationSection section2 = RecipeEditorGUI.getSection(section, this.getRecipeRegistry().getRecipeConfigPath());
      ConfigurationSection section3 = RecipeEditorGUI.getSection(section2, this.getRecipeName());
      this.interpreter = new RMGRI_Shapeless(section3);
      this.inputLinks.put(30, 0);
      this.inputLinks.put(31, 1);
      this.inputLinks.put(32, 2);
      this.inputLinks.put(39, 3);
      this.inputLinks.put(40, 4);
      this.inputLinks.put(41, 5);
      this.inputLinks.put(48, 6);
      this.inputLinks.put(49, 7);
      this.inputLinks.put(50, 8);
   }

   @Override
   public int getButtonsRow() {
      return 1;
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

   @NotNull
   @Override
   public RMG_RecipeInterpreter getInterpreter() {
      return this.interpreter;
   }
}
