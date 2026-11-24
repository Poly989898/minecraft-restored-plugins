package net.Indyuce.mmoitems.gui.edition.recipe.button;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.gui.edition.recipe.button.type.RBA_BooleanButton;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.jetbrains.annotations.NotNull;

public class RBA_HideFromBook extends RBA_BooleanButton {
   public static final String BOOK_HIDDEN = "hidden";
   @NotNull
   final ItemStack booleanButton = ItemFactory.of(Material.KNOWLEDGE_BOOK)
      .name("§cHide from Crafting Book")
      .lore(SilentNumbers.chop("Even if the crafting book is enabled, this recipe wont be automatically unlocked by players.", 65, "§7"))
      .build();

   public RBA_HideFromBook(@NotNull RecipeEditorGUI var1) {
      super(var1);
   }

   @NotNull
   @Override
   public String getBooleanConfigPath() {
      return "hidden";
   }

   @Override
   public boolean runSecondary() {
      ItemStack var1 = new ItemStack(Material.KNOWLEDGE_BOOK);
      ItemMeta var2 = var1.getItemMeta();
      if (var2 instanceof KnowledgeBookMeta) {
         ((KnowledgeBookMeta)var2)
            .addRecipe(
               new NamespacedKey[]{
                  MMOItems.plugin
                     .getRecipes()
                     .getRecipeKey(
                        this.getInv().getEdited().getType(),
                        this.getInv().getEdited().getId(),
                        this.getInv().getRecipeRegistry().getRecipeConfigPath(),
                        this.getInv().getRecipeName()
                     )
               }
            );
      }

      var1.setItemMeta(var2);
      this.getInv().getPlayer().getInventory().addItem(new ItemStack[]{var1});
      this.getInv().getPlayer().playSound(this.getInv().getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.0F);
      return true;
   }

   @NotNull
   @Override
   public ItemStack getBooleanButton() {
      return this.booleanButton;
   }

   @NotNull
   @Override
   public ItemStack getButton() {
      String var1 = this.isEnabled() ? "§cNO§8, it's hidden." : "§aYES§8, it's shown.";
      return RecipeEditorGUI.addLore(
         this.getBooleanButton().clone(),
         SilentNumbers.toArrayList(
            new String[]{
               "",
               "§7Currently in Book? " + var1,
               "",
               ChatColor.YELLOW + "►" + " Right click to generate recipe unlock book.",
               ChatColor.YELLOW + "►" + " Left click to toggle this option."
            }
         )
      );
   }
}
