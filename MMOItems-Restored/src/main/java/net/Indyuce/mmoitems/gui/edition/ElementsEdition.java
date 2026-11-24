package net.Indyuce.mmoitems.gui.edition;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.element.Element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.stat.data.random.RandomElementListData;
import net.Indyuce.mmoitems.util.ElementStatType;
import net.Indyuce.mmoitems.util.MMOUtils;
import net.Indyuce.mmoitems.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ElementsEdition extends EditionInventory {
   private final List<Element> elements = new ArrayList<>();
   private final int maxPage;
   private final Map<Integer, Pair<Element, ElementStatType>> editableStats = new HashMap<>();
   private int page = 1;
   private static final int[] INIT_SLOTS = new int[]{19, 28, 37};
   private static final int ELEMENTS_PER_PAGE = 3;

   public ElementsEdition(Player var1, MMOItemTemplate var2) {
      super(var1, var2);
      this.elements.addAll(MythicLib.plugin.getElements().getAll());
      this.maxPage = 1 + (MythicLib.plugin.getElements().getAll().size() - 1) / 3;
   }

   @Override
   public String getName() {
      return "Elements: " + this.template.getId();
   }

   @Override
   public void arrangeInventory() {
      Optional var1 = this.getEventualStatData(ItemStats.ELEMENTS);
      ItemStack var2 = new ItemStack(Material.ARROW);
      ItemMeta var3 = var2.getItemMeta();
      var3.setDisplayName(ChatColor.GREEN + "Previous Page");
      var2.setItemMeta(var3);
      this.inventory.setItem(25, var2);
      ItemStack var4 = new ItemStack(Material.ARROW);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(ChatColor.GREEN + "Next Page");
      var4.setItemMeta(var5);
      this.inventory.setItem(43, var4);
      this.editableStats.clear();
      int var6 = (this.page - 1) * 3;

      for (int var7 = 0; var7 < 3; var7++) {
         int var8 = var6 + var7;
         if (var8 >= this.elements.size()) {
            break;
         }

         Element var9 = this.elements.get(var8);
         int var10 = 0;

         for (ElementStatType var14 : ElementStatType.values()) {
            ItemStack var15 = new ItemStack(var9.getIcon());
            ItemMeta var16 = var15.getItemMeta();
            var16.setDisplayName(ChatColor.GREEN + var9.getName() + " " + var14.getName());
            ArrayList var17 = new ArrayList();
            var17.add(
               ChatColor.GRAY
                  + "Current Value: "
                  + ChatColor.GREEN
                  + (
                     var1.isPresent() && ((RandomElementListData)var1.get()).hasStat(var9, var14)
                        ? ((RandomElementListData)var1.get()).getStat(var9, var14)
                        : "---"
                  )
            );
            var17.add("");
            var17.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            var17.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
            var16.setLore(var17);
            var15.setItemMeta(var16);
            int var18 = INIT_SLOTS[var7] + var10;
            this.inventory.setItem(var18, var15);
            this.editableStats.put(var18, Pair.of(var9, var14));
            var10++;
         }
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      ItemStack var2 = var1.getCurrentItem();
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory() && MMOUtils.isMetaItem(var2, false)) {
         if (this.page > 1 && var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous Page")) {
            this.page--;
            this.refreshInventory();
         } else if (this.page < this.maxPage && var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next Page")) {
            this.page++;
            this.refreshInventory();
         } else {
            Pair var3 = this.editableStats.get(var1.getSlot());
            if (var3 != null) {
               String var4 = ((ElementStatType)var3.getValue()).getConcatenatedConfigPath((Element)var3.getKey());
               if (var1.getAction() == InventoryAction.PICKUP_ALL) {
                  new StatEdition(this, ItemStats.ELEMENTS, var4).enable("Write in the value you want.");
               } else if (var1.getAction() == InventoryAction.PICKUP_HALF) {
                  this.getEditedSection().set("element." + var4, null);
                  String var5 = ((Element)var3.getKey()).getId();
                  if (this.getEditedSection().contains("element." + var5)
                     && this.getEditedSection().getConfigurationSection("element." + var5).getKeys(false).isEmpty()) {
                     this.getEditedSection().set("element." + var5, null);
                     if (this.getEditedSection().getConfigurationSection("element").getKeys(false).isEmpty()) {
                        this.getEditedSection().set("element", null);
                     }
                  }

                  this.registerTemplateEdition();
                  this.player
                     .sendMessage(
                        MMOItems.plugin.getPrefix()
                           + ChatColor.RED
                           + ((Element)var3.getKey()).getName()
                           + " "
                           + ((ElementStatType)var3.getValue()).getName()
                           + ChatColor.GRAY
                           + " successfully removed."
                     );
               }
            }
         }
      }
   }
}
