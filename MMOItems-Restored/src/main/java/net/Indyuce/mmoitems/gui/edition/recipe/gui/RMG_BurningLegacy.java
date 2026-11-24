package net.Indyuce.mmoitems.gui.edition.recipe.gui;

import java.util.HashMap;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_CookingTime;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_Experience;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_HideFromBook;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_LegacyBurning;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMG_RecipeInterpreter;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RMG_BurningLegacy extends RecipeEditorGUI {
   @NotNull
   HashMap<Integer, Integer> inputLinks = new HashMap<>();
   @NotNull
   final RMGRI_LegacyBurning interpreter;

   public RMG_BurningLegacy(@NotNull Player player, @NotNull MMOItemTemplate mmoItemTemplate, @NotNull String text, @NotNull RecipeRegistry recipeRegistry) {
      super(player, mmoItemTemplate, text, recipeRegistry);
      this.addButton(new RBA_HideFromBook(this));
      this.addButton(new RBA_Experience(this));
      this.addButton(new RBA_CookingTime(this));
      if (!this.isShowingInput()) {
         this.switchInput();
      }

      this.interpreter = new RMGRI_LegacyBurning(this.getNameSection());
      this.inputLinks.put(40, 0);
   }

   @Override
   public int getButtonsRow() {
      return 2;
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
