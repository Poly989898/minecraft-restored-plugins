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
   public void a(InventoryClickEvent var1) {
      Player var2 = (Player)var1.getWhoClicked();
      ItemStack var3 = var1.getCurrentItem();
      if (var1.getInventory().getHolder() instanceof PluginInventory) {
         PluginInventory var4 = (PluginInventory)var1.getInventory().getHolder();
         var4.whenClicked(var1);
         if (!(var4 instanceof EditionInventory)
            || var1.getInventory() != var1.getClickedInventory()
            || !MMOUtils.isMetaItem(var3, false)
            || !var3.getItemMeta().getDisplayName().startsWith(ChatColor.GREEN + "")) {
            return;
         }

         if (var3.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "✤" + " Get the Item! " + "✤")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               for (ItemStack var9 : var2.getInventory().addItem(new ItemStack[]{var1.getInventory().getItem(4)}).values()) {
                  var2.getWorld().dropItemNaturally(var2.getLocation(), var9);
               }

               if (NBTItem.get(var1.getInventory().getItem(4)).getBoolean("MMOITEMS_UNSTACKABLE")) {
                  ((EditionInventory)var4).updateCachedItem();
                  var1.getInventory().setItem(4, ((EditionInventory)var4).getCachedItem());
               }
            } else if (var1.getAction() == InventoryAction.PICKUP_HALF) {
               for (ItemStack var6 : var2.getInventory().addItem(new ItemStack[]{var1.getInventory().getItem(4)}).values()) {
                  var2.getWorld().dropItemNaturally(var2.getLocation(), var6);
               }

               ((EditionInventory)var4).updateCachedItem();
               var1.getInventory().setItem(4, ((EditionInventory)var4).getCachedItem());
            }
         }

         MMOItemTemplate var8 = ((EditionInventory)var4).getEdited();
         if (var3.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "⇨" + " Back")) {
            if (var4 instanceof ItemEdition) {
               new ItemBrowser(var2, var8.getType()).open();
            } else if (var4 instanceof RecipeListGUI) {
               new RecipeTypeListGUI(var2, var8).open((EditionInventory)var4);
            } else if (var4 instanceof RecipeEditorGUI) {
               new RecipeListGUI(var2, var8, ((RecipeEditorGUI)var4).getRecipeRegistry()).open((EditionInventory)var4);
            } else {
               new ItemEdition(var2, var8).open((EditionInventory)var4);
            }
         }
      }
   }
}
