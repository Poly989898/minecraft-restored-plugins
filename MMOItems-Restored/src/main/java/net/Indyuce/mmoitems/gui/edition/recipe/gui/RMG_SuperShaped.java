package net.Indyuce.mmoitems.gui.edition.recipe.gui;

import java.util.HashMap;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RBA_InputOutput;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMGRI_SuperShaped;
import net.Indyuce.mmoitems.gui.edition.recipe.interpreter.RMG_RecipeInterpreter;
import net.Indyuce.mmoitems.gui.edition.recipe.registry.RecipeRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RMG_SuperShaped extends RecipeEditorGUI {
   @NotNull
   HashMap<Integer, Integer> inputLinks = new HashMap<>();
   @NotNull
   final RMGRI_SuperShaped interpreter;

   public RMG_SuperShaped(@NotNull Player var1, @NotNull MMOItemTemplate var2, @NotNull String var3, @NotNull RecipeRegistry var4) {
      super(var1, var2, var3, var4);
      this.addButton(new RBA_InputOutput(this));
      this.interpreter = new RMGRI_SuperShaped(this.getNameSection());
      this.inputLinks.put(11, 0);
      this.inputLinks.put(12, 1);
      this.inputLinks.put(13, 2);
      this.inputLinks.put(14, 3);
      this.inputLinks.put(15, 4);
      this.inputLinks.put(20, 5);
      this.inputLinks.put(21, 6);
      this.inputLinks.put(22, 7);
      this.inputLinks.put(23, 8);
      this.inputLinks.put(24, 9);
      this.inputLinks.put(29, 10);
      this.inputLinks.put(30, 11);
      this.inputLinks.put(31, 12);
      this.inputLinks.put(32, 13);
      this.inputLinks.put(33, 14);
      this.inputLinks.put(38, 15);
      this.inputLinks.put(39, 16);
      this.inputLinks.put(40, 17);
      this.inputLinks.put(41, 18);
      this.inputLinks.put(42, 19);
      this.inputLinks.put(47, 20);
      this.inputLinks.put(48, 21);
      this.inputLinks.put(49, 22);
      this.inputLinks.put(50, 23);
      this.inputLinks.put(51, 24);
   }

   @Override
   public int getButtonsRow() {
      return 0;
   }

   @Override
   public void putRecipe() {
      for (Integer var2 : this.inputLinks.keySet()) {
         this.inventory.setItem(var2, this.getDisplay(this.isShowingInput(), this.inputLinks.get(var2)));
      }
   }

   @Override
   int getInputSlot(int var1) {
      Integer var2 = this.inputLinks.get(var1);
      return var2 != null ? var2 : -1;
   }

   @NotNull
   @Override
   public RMG_RecipeInterpreter getInterpreter() {
      return this.interpreter;
   }
}
