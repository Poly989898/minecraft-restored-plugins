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

   public ElementsEdition(Player player, MMOItemTemplate mmoItemTemplate) {
      super(player, mmoItemTemplate);
      this.elements.addAll(MythicLib.plugin.getElements().getAll());
      this.maxPage = 1 + (MythicLib.plugin.getElements().getAll().size() - 1) / 3;
   }

   @Override
   public String getName() {
      return "Elements: " + this.template.getId();
   }

   @Override
   public void arrangeInventory() {
      Optional eventualStatData = this.getEventualStatData(ItemStats.ELEMENTS);
      ItemStack item = new ItemStack(Material.ARROW);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
      item.setItemMeta(itemMeta);
      this.inventory.setItem(25, item);
      ItemStack nextPageItem = new ItemStack(Material.ARROW);
      ItemMeta nextPageMeta = nextPageItem.getItemMeta();
      nextPageMeta.setDisplayName(ChatColor.GREEN + "Next Page");
      nextPageItem.setItemMeta(nextPageMeta);
      this.inventory.setItem(43, nextPageItem);
      this.editableStats.clear();
      int count = (this.page - 1) * 3;

      for (int i = 0; i < 3; i++) {
         int index = count + i;
         if (index >= this.elements.size()) {
            break;
         }

         Element element = this.elements.get(index);
         int elementIndex = 0;

         for (ElementStatType elementStatType : ElementStatType.values()) {
            ItemStack icon = new ItemStack(element.getIcon());
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(ChatColor.GREEN + element.getName() + " " + elementStatType.getName());
            ArrayList list = new ArrayList();
            list.add(
               ChatColor.GRAY
                  + "Current Value: "
                  + ChatColor.GREEN
                  + (
                     eventualStatData.isPresent() && ((RandomElementListData)eventualStatData.get()).hasStat(element, elementStatType)
                        ? ((RandomElementListData)eventualStatData.get()).getStat(element, elementStatType)
                        : "---"
                  )
            );
            list.add("");
            list.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            list.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
            iconMeta.setLore(list);
            icon.setItemMeta(iconMeta);
            int slot = INIT_SLOTS[i] + elementIndex;
            this.inventory.setItem(slot, icon);
            this.editableStats.put(slot, Pair.of(element, elementStatType));
            elementIndex++;
         }
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory() && MMOUtils.isMetaItem(currentItem, false)) {
         if (this.page > 1 && currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous Page")) {
            this.page--;
            this.refreshInventory();
         } else if (this.page < this.maxPage && currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next Page")) {
            this.page++;
            this.refreshInventory();
         } else {
            Pair pair = this.editableStats.get(event.getSlot());
            if (pair != null) {
               String concatenatedConfigPath = ((ElementStatType)pair.getValue()).getConcatenatedConfigPath((Element)pair.getKey());
               if (event.getAction() == InventoryAction.PICKUP_ALL) {
                  new StatEdition(this, ItemStats.ELEMENTS, concatenatedConfigPath).enable("Write in the value you want.");
               } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                  this.getEditedSection().set("element." + concatenatedConfigPath, null);
                  String id = ((Element)pair.getKey()).getId();
                  if (this.getEditedSection().contains("element." + id)
                     && this.getEditedSection().getConfigurationSection("element." + id).getKeys(false).isEmpty()) {
                     this.getEditedSection().set("element." + id, null);
                     if (this.getEditedSection().getConfigurationSection("element").getKeys(false).isEmpty()) {
                        this.getEditedSection().set("element", null);
                     }
                  }

                  this.registerTemplateEdition();
                  this.player
                     .sendMessage(
                        MMOItems.plugin.getPrefix()
                           + ChatColor.RED
                           + ((Element)pair.getKey()).getName()
                           + " "
                           + ((ElementStatType)pair.getValue()).getName()
                           + ChatColor.GRAY
                           + " successfully removed."
                     );
               }
            }
         }
      }
   }
}
