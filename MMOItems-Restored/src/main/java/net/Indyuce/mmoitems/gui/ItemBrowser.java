package net.Indyuce.mmoitems.gui;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.util.AdventureUtils;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.NewItemEdition;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import net.Indyuce.mmoitems.stat.BrowserDisplayIDX;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ItemBrowser extends PluginInventory {
   private final Map<String, ItemStack> cached = new LinkedHashMap<>();
   private final List<Type> itemTypes;
   private final Type type;
   private boolean deleteMode;
   private static final int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
   private static final int[] slotsAlt = new int[]{1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
   private static final String CUSTOM_RP_DOWNLOAD_LINK = "https://www.dropbox.com/s/90w9pvdbfeyxu94/MICustomBlockPack.zip?dl=1";
   private static final NamespacedKey TYPE_ID_KEY = new NamespacedKey(MMOItems.plugin, "TypeId");

   public ItemBrowser(Player player) {
      this(player, null);
   }

   public ItemBrowser(Player player, Type type) {
      super(player);
      this.type = type;
      this.itemTypes = MMOItems.plugin.getTypes().getAll().stream().filter(Type::isDisplayed).collect(Collectors.toList());
   }

   @NotNull
   @Override
   public Inventory getInventory() {
      if (this.type == null) {
         int[] counts = slots;
         int count = (this.page - 1) * counts.length;
         int firstIndex = this.page * counts.length;
         int slotIndex = 0;
         Inventory inventory = Bukkit.createInventory(this, 54, "Item Explorer");

         for (int i = count; i < Math.min(firstIndex, this.itemTypes.size()); i++) {
            Type type = this.itemTypes.get(i);
            int size = MMOItems.plugin.getTemplates().getTemplates(type).size();
            ItemStack item = type.getItem();
            item.setAmount(Math.max(1, Math.min(64, size)));
            ItemMeta itemMeta = item.getItemMeta();
            if (MythicLib.plugin.getVersion().isAbove(new int[]{1, 20, 5})) {
               VersionUtils.addEmptyAttributeModifier(itemMeta);
            }

            AdventureUtils.setDisplayName(itemMeta, String.format("&a%s&8 (click to browse)", type.getName()));
            itemMeta.addItemFlags(ItemFlag.values());
            ArrayList list = new ArrayList();
            list.add(
               String.format(
                  "&7&oThere %s %s &7&oitem%s in this type.", size == 1 ? "is" : "are", size < 1 ? "&c&ono" : "&6&o" + size, size == 1 ? "" : "s"
               )
            );
            AdventureUtils.setLore(itemMeta, list);
            itemMeta.getPersistentDataContainer().set(TYPE_ID_KEY, PersistentDataType.STRING, type.getId());
            item.setItemMeta(itemMeta);
            inventory.setItem(slots[slotIndex++], item);
         }

         ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
         ItemMeta itemMeta = item.getItemMeta();
         itemMeta.setDisplayName(ChatColor.RED + "- No type -");
         item.setItemMeta(itemMeta);
         ItemStack nextPageItem = new ItemStack(Material.ARROW);
         ItemMeta nextPageMeta = nextPageItem.getItemMeta();
         nextPageMeta.setDisplayName(ChatColor.GREEN + "Next Page");
         nextPageItem.setItemMeta(nextPageMeta);
         ItemStack previousPageItem = new ItemStack(Material.ARROW);
         ItemMeta previousPageMeta = previousPageItem.getItemMeta();
         previousPageMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
         previousPageItem.setItemMeta(previousPageMeta);

         while (slotIndex < slots.length) {
            inventory.setItem(slots[slotIndex++], item);
         }

         inventory.setItem(18, this.page > 1 ? previousPageItem : null);
         inventory.setItem(26, firstIndex >= MMOItems.plugin.getTypes().getAll().size() ? null : nextPageItem);
         return inventory;
      } else {
         Inventory inventory = Bukkit.createInventory(
            this, 54, (this.deleteMode ? "Delete Mode: " : "Item Explorer: ") + MythicLib.plugin.getAdventureParser().stripColors(this.type.getName())
         );
         ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
         ItemMeta itemMeta = item.getItemMeta();
         itemMeta.setDisplayName(ChatColor.RED + "- Error -");
         ArrayList list = new ArrayList();
         list.add("§§oAn error occurred while");
         list.add("§§otrying to generate that item.");
         itemMeta.setLore(list);
         item.setItemMeta(itemMeta);
         ItemStack noItemItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
         ItemMeta noItemMeta = noItemItem.getItemMeta();
         noItemMeta.setDisplayName(ChatColor.RED + "- No Item -");
         noItemItem.setItemMeta(noItemMeta);
         ItemStack nextPageItem = new ItemStack(Material.ARROW);
         ItemMeta nextPageMeta = nextPageItem.getItemMeta();
         nextPageMeta.setDisplayName(ChatColor.GREEN + "Next Page");
         nextPageItem.setItemMeta(nextPageMeta);
         ItemStack arrowItem = new ItemStack(Material.ARROW);
         ItemMeta arrowMeta = arrowItem.getItemMeta();
         arrowMeta.setDisplayName(ChatColor.GREEN + "⇨" + " Back");
         arrowItem.setItemMeta(arrowMeta);
         ItemStack createNewItem = new ItemStack(new ItemStack(Material.WRITABLE_BOOK));
         ItemMeta createNewMeta = createNewItem.getItemMeta();
         createNewMeta.setDisplayName(ChatColor.GREEN + "Create New");
         createNewItem.setItemMeta(createNewMeta);
         ItemStack cancelDeletionItem = new ItemStack(new ItemStack(Material.CAULDRON));
         ItemMeta cancelDeletionMeta = cancelDeletionItem.getItemMeta();
         cancelDeletionMeta.setDisplayName(ChatColor.RED + (this.deleteMode ? "Cancel Deletion" : "Delete Item"));
         cancelDeletionItem.setItemMeta(cancelDeletionMeta);
         ItemStack previousPageItem = new ItemStack(Material.ARROW);
         ItemMeta previousPageMeta = previousPageItem.getItemMeta();
         previousPageMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
         previousPageItem.setItemMeta(previousPageMeta);
         if (this.type == Type.BLOCK) {
            ItemStack downloadDefaultResourcepackItem = new ItemStack(Material.HOPPER);
            ItemMeta downloadDefaultResourcepackMeta = downloadDefaultResourcepackItem.getItemMeta();
            downloadDefaultResourcepackMeta.setDisplayName(ChatColor.GREEN + "Download Default Resourcepack");
            downloadDefaultResourcepackMeta.setLore(
               Arrays.asList(
                  ChatColor.LIGHT_PURPLE + "Only seeing stone blocks?",
                  "",
                  ChatColor.RED + "By downloading the default resourcepack you can",
                  ChatColor.RED + "edit the blocks however you want.",
                  ChatColor.RED + "You will still have to add it to your server!"
               )
            );
            downloadDefaultResourcepackItem.setItemMeta(downloadDefaultResourcepackMeta);
            inventory.setItem(45, downloadDefaultResourcepackItem);
         }

         HashMap<Double, ArrayList<MMOItemTemplate>> map = BrowserDisplayIDX.select(MMOItems.plugin.getTemplates().getTemplates(this.type));
         int[] counts = this.type.isFourGUIMode() ? slotsAlt : slots;
         int count = (this.page - 1) * counts.length;
         int firstIndex = this.page * counts.length;
         int index = 0;
         int rowCount = this.type.isFourGUIMode() ? 4 : 3;
         int slotIndex = 0;

         for (Entry<Double, ArrayList<MMOItemTemplate>> entry : map.entrySet()) {
            for (int i = ((ArrayList)entry.getValue()).size(); i > 0; i -= rowCount) {
               slotIndex += rowCount;
            }
         }

         for (int i = count; i < Math.min(firstIndex, slotIndex); i++) {
            MMOItemTemplate at = BrowserDisplayIDX.getAt(i, map);
            if (at == null) {
               inventory.setItem(counts[index], noItemItem);
               index += 7;
               if (index >= counts.length) {
                  index -= 7 * rowCount;
                  index++;
               }
            } else {
               ItemStack templateItem = at.newBuilder(this.playerData.getRPG(), true).build().newBuilder().build();
               if (templateItem != null && !templateItem.getType().isAir() && templateItem.getType().isItem() && templateItem.getItemMeta() != null) {
                  ItemMeta templateMeta = templateItem.getItemMeta();
                  List lore = templateMeta.getLore();
                  if (lore == null) {
                     lore = new ArrayList();
                  }

                  lore.add("");
                  if (this.deleteMode) {
                     lore.add(ChatColor.RED + "✖" + " CLICK TO DELETE " + "✖");
                     templateMeta.setDisplayName(ChatColor.RED + "DELETE: " + (templateMeta.hasDisplayName() ? templateMeta.getDisplayName() : MMOUtils.getDisplayName(templateItem)));
                  } else {
                     lore.add(ChatColor.YELLOW + "▸" + " Left click to obtain this item.");
                     lore.add(ChatColor.YELLOW + "▸" + " Right click to edit this item.");
                  }

                  templateMeta.setLore(lore);
                  templateItem.setItemMeta(templateMeta);
                  this.cached.put(at.getId(), templateItem);
                  inventory.setItem(counts[index], this.cached.get(at.getId()));
                  index += 7;
                  if (index >= counts.length) {
                     index -= 7 * rowCount;
                     index++;
                  }
               } else {
                  this.cached.put(at.getId(), item);
                  inventory.setItem(counts[index], item);
                  index += 7;
                  if (index >= counts.length) {
                     index -= 7 * rowCount;
                     index++;
                  }
               }
            }
         }

         if (!this.deleteMode) {
            inventory.setItem(51, createNewItem);
         }

         inventory.setItem(47, cancelDeletionItem);
         inventory.setItem(49, arrowItem);
         inventory.setItem(18, this.page > 1 ? previousPageItem : null);
         inventory.setItem(26, firstIndex >= slotIndex ? null : nextPageItem);

         for (int slot : counts) {
            if (SilentNumbers.isAir(inventory.getItem(slot))) {
               inventory.setItem(slot, noItemItem);
            }
         }

         return inventory;
      }
   }

   public Type getType() {
      return this.type;
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory()) {
         ItemStack currentItem = event.getCurrentItem();
         if (MMOUtils.isMetaItem(currentItem, false)) {
            if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next Page")) {
               this.page++;
               this.open();
            } else if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous Page")) {
               this.page--;
               this.open();
            } else if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "⇨" + " Back")) {
               new ItemBrowser(this.getPlayer()).open();
            } else if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "Cancel Deletion")) {
               this.deleteMode = false;
               this.open();
            } else if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Create New")) {
               new NewItemEdition(this).enable("Write in the chat the text you want.");
            } else if (this.type != null && currentItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "Delete Item")) {
               this.deleteMode = true;
               this.open();
            } else if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Download Default Resourcepack")) {
               MythicLib.plugin
                  .getVersion()
                  .getWrapper()
                  .sendJson(
                     this.getPlayer(),
                     "[{\"text\":\"Click to download!\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.dropbox.com/s/90w9pvdbfeyxu94/MICustomBlockPack.zip?dl=1\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click to download via Dropbox\",\"italic\":true,\"color\":\"white\"}]}}]"
                  );
               this.getPlayer().closeInventory();
            } else if (this.type == null && !currentItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "- No type -")) {
               String text = (String)currentItem.getItemMeta().getPersistentDataContainer().get(TYPE_ID_KEY, PersistentDataType.STRING);
               new ItemBrowser(this.getPlayer(), MMOItems.plugin.getTypes().get(text)).open();
            }
         }

         String mMOITEMSITEMID = NBTItem.get(currentItem).getString("MMOITEMS_ITEM_ID");
         if (!mMOITEMSITEMID.equals("")) {
            if (this.deleteMode) {
               MMOItems.plugin.getTemplates().deleteTemplate(this.type, mMOITEMSITEMID);
               this.deleteMode = false;
               this.open();
            } else {
               if (event.getAction() == InventoryAction.PICKUP_ALL) {
                  this.getPlayer().getInventory().addItem(new ItemStack[]{MMOItems.plugin.getItem(this.type, mMOITEMSITEMID, this.playerData)});
                  this.getPlayer().playSound(this.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.0F);
               }

               if (event.getAction() == InventoryAction.PICKUP_HALF) {
                  new ItemEdition(this.getPlayer(), MMOItems.plugin.getTemplates().getTemplate(this.type, mMOITEMSITEMID)).open();
               }
            }
         }
      }
   }
}
