package net.Indyuce.mmoitems.gui.listener;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.gui.ItemBrowser;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import net.Indyuce.mmoitems.gui.edition.recipe.RecipeListGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.RecipeTypeListGUI;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiListener implements Listener {
   @EventHandler
   public void a(InventoryClickEvent event) {
      Player whoClicked = (Player)event.getWhoClicked();
      ItemStack currentItem = event.getCurrentItem();
      if (event.getInventory().getHolder() instanceof PluginInventory) {
         PluginInventory holder = (PluginInventory)event.getInventory().getHolder();
         holder.whenClicked(event);
         if (!(holder instanceof EditionInventory)
            || event.getInventory() != event.getClickedInventory()
            || !MMOUtils.isMetaItem(currentItem, false)
            || !currentItem.getItemMeta().getDisplayName().startsWith(ChatColor.GREEN + "")) {
            return;
         }

         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "✤" + " Get the Item! " + "✤")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               for (ItemStack item : whoClicked.getInventory().addItem(new ItemStack[]{event.getInventory().getItem(4)}).values()) {
                  whoClicked.getWorld().dropItemNaturally(whoClicked.getLocation(), item);
               }

               if (NBTItem.get(event.getInventory().getItem(4)).getBoolean("MMOITEMS_UNSTACKABLE")) {
                  ((EditionInventory)holder).updateCachedItem();
                  event.getInventory().setItem(4, ((EditionInventory)holder).getCachedItem());
               }
            } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
               for (ItemStack item : whoClicked.getInventory().addItem(new ItemStack[]{event.getInventory().getItem(4)}).values()) {
                  whoClicked.getWorld().dropItemNaturally(whoClicked.getLocation(), item);
               }

               ((EditionInventory)holder).updateCachedItem();
               event.getInventory().setItem(4, ((EditionInventory)holder).getCachedItem());
            }
         }

         MMOItemTemplate edited = ((EditionInventory)holder).getEdited();
         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "⇨" + " Back")) {
            if (holder instanceof ItemEdition) {
               new ItemBrowser(whoClicked, edited.getType()).open();
            } else if (holder instanceof RecipeListGUI) {
               new RecipeTypeListGUI(whoClicked, edited).open((EditionInventory)holder);
            } else if (holder instanceof RecipeEditorGUI) {
               new RecipeListGUI(whoClicked, edited, ((RecipeEditorGUI)holder).getRecipeRegistry()).open((EditionInventory)holder);
            } else {
               new ItemEdition(whoClicked, edited).open((EditionInventory)holder);
            }
         }
      }
   }
}
