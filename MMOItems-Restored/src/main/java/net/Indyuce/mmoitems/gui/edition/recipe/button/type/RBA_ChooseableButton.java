package net.Indyuce.mmoitems.gui.edition.recipe.button.type;

import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RecipeButtonAction;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class RBA_ChooseableButton extends RecipeButtonAction {
   public RBA_ChooseableButton(@NotNull RecipeEditorGUI recipeEditorGUI) {
      super(recipeEditorGUI);
   }

   @Override
   public boolean runPrimary() {
      String currentChooseableValue = this.getCurrentChooseableValue();
      int index = this.getChooseableList().indexOf(currentChooseableValue);
      if (index == -1) {
         return this.runSecondary();
      }

      if (++index >= this.getChooseableList().size()) {
         index = 0;
      }

      String text = this.getChooseableList().get(index);
      this.getInv().getNameSection().set(this.getChooseableConfigPath(), text);
      this.clickSFX();
      this.getInv().registerTemplateEdition();
      return true;
   }

   @Override
   public boolean runSecondary() {
      this.getInv().getNameSection().set(this.getChooseableConfigPath(), null);
      this.clickSFX();
      this.getInv().registerTemplateEdition();
      return true;
   }

   @NotNull
   public abstract ItemStack getChooseableButton();

   @NotNull
   @Override
   public ItemStack getButton() {
      String currentChooseableValue = this.getCurrentChooseableValue();
      ArrayList list = new ArrayList();
      list.add("§7Current Value:§3 " + currentChooseableValue);
      list.addAll(SilentNumbers.chop(this.getChooseableDefinition(currentChooseableValue), 50, "  §b§o"));
      list.add("");
      list.add(ChatColor.YELLOW + "►" + " Right click to return to default value.");
      list.add(ChatColor.YELLOW + "►" + " Left click to cycle through the options:");

      for (String chooseable : this.getChooseableList()) {
         String text = ChatColor.GOLD.toString();
         if (chooseable.equals(currentChooseableValue)) {
            text = ChatColor.RED.toString() + ChatColor.BOLD;
         }

         list.add(text + "  " + "▸" + " §7" + chooseable);
      }

      return RecipeEditorGUI.addLore(this.getChooseableButton().clone(), list);
   }

   @NotNull
   public abstract String getChooseableConfigPath();

   @NotNull
   public abstract ArrayList<String> getChooseableList();

   @NotNull
   public String getCurrentChooseableValue() {
      String text = this.getInv().getNameSection().getString(this.getChooseableConfigPath());
      return text != null ? text : this.getDefaultValue();
   }

   @NotNull
   public abstract String getDefaultValue();

   @NotNull
   public abstract String getChooseableDefinition(@NotNull String text);

   @Override
   public void secondaryProcessInput(@NotNull String text, Object... objs) {
   }

   @Override
   public void primaryProcessInput(@NotNull String text, Object... objs) {
   }
}
