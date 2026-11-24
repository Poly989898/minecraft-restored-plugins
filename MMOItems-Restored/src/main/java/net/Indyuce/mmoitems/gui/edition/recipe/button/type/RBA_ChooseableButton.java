package net.Indyuce.mmoitems.gui.edition.recipe.button.type;

import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import net.Indyuce.mmoitems.gui.edition.recipe.button.RecipeButtonAction;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class RBA_ChooseableButton extends RecipeButtonAction {
   public RBA_ChooseableButton(@NotNull RecipeEditorGUI var1) {
      super(var1);
   }

   @Override
   public boolean runPrimary() {
      String var1 = this.getCurrentChooseableValue();
      int var2 = this.getChooseableList().indexOf(var1);
      if (var2 == -1) {
         return this.runSecondary();
      }

      if (++var2 >= this.getChooseableList().size()) {
         var2 = 0;
      }

      String var3 = this.getChooseableList().get(var2);
      this.getInv().getNameSection().set(this.getChooseableConfigPath(), var3);
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
      String var1 = this.getCurrentChooseableValue();
      ArrayList var2 = new ArrayList();
      var2.add("§7Current Value:§3 " + var1);
      var2.addAll(SilentNumbers.chop(this.getChooseableDefinition(var1), 50, "  §b§o"));
      var2.add("");
      var2.add(ChatColor.YELLOW + "►" + " Right click to return to default value.");
      var2.add(ChatColor.YELLOW + "►" + " Left click to cycle through the options:");

      for (String var4 : this.getChooseableList()) {
         String var5 = ChatColor.GOLD.toString();
         if (var4.equals(var1)) {
            var5 = ChatColor.RED.toString() + ChatColor.BOLD;
         }

         var2.add(var5 + "  " + "▸" + " §7" + var4);
      }

      return RecipeEditorGUI.addLore(this.getChooseableButton().clone(), var2);
   }

   @NotNull
   public abstract String getChooseableConfigPath();

   @NotNull
   public abstract ArrayList<String> getChooseableList();

   @NotNull
   public String getCurrentChooseableValue() {
      String var1 = this.getInv().getNameSection().getString(this.getChooseableConfigPath());
      return var1 != null ? var1 : this.getDefaultValue();
   }

   @NotNull
   public abstract String getDefaultValue();

   @NotNull
   public abstract String getChooseableDefinition(@NotNull String var1);

   @Override
   public void secondaryProcessInput(@NotNull String var1, Object... var2) {
   }

   @Override
   public void primaryProcessInput(@NotNull String var1, Object... var2) {
   }
}
