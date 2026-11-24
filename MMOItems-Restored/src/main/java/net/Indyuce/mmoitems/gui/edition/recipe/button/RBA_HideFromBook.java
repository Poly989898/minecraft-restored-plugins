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

   public RBA_HideFromBook(@NotNull RecipeEditorGUI recipeEditorGUI) {
      super(recipeEditorGUI);
   }

   @NotNull
   @Override
   public String getBooleanConfigPath() {
      return "hidden";
   }

   @Override
   public boolean runSecondary() {
      ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
      ItemMeta itemMeta = item.getItemMeta();
      if (itemMeta instanceof KnowledgeBookMeta) {
         ((KnowledgeBookMeta)itemMeta)
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

      item.setItemMeta(itemMeta);
      this.getInv().getPlayer().getInventory().addItem(new ItemStack[]{item});
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
      String text = this.isEnabled() ? "§cNO§8, it's hidden." : "§aYES§8, it's shown.";
      return RecipeEditorGUI.addLore(
         this.getBooleanButton().clone(),
         SilentNumbers.toArrayList(
            new String[]{
               "",
               "§7Currently in Book? " + text,
               "",
               ChatColor.YELLOW + "►" + " Right click to generate recipe unlock book.",
               ChatColor.YELLOW + "►" + " Left click to toggle this option."
            }
         )
      );
   }
}
