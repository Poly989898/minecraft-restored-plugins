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

   public ItemBrowser(Player var1) {
      this(var1, null);
   }

   public ItemBrowser(Player var1, Type var2) {
      super(var1);
      this.type = var2;
      this.itemTypes = MMOItems.plugin.getTypes().getAll().stream().filter(Type::isDisplayed).collect(Collectors.toList());
   }

   @NotNull
   @Override
   public Inventory getInventory() {
      if (this.type == null) {
         int[] var29 = slots;
         int var30 = (this.page - 1) * var29.length;
         int var31 = this.page * var29.length;
         int var32 = 0;
         Inventory var33 = Bukkit.createInventory(this, 54, "Item Explorer");

         for (int var34 = var30; var34 < Math.min(var31, this.itemTypes.size()); var34++) {
            Type var36 = this.itemTypes.get(var34);
            int var38 = MMOItems.plugin.getTemplates().getTemplates(var36).size();
            ItemStack var40 = var36.getItem();
            var40.setAmount(Math.max(1, Math.min(64, var38)));
            ItemMeta var42 = var40.getItemMeta();
            if (MythicLib.plugin.getVersion().isAbove(new int[]{1, 20, 5})) {
               VersionUtils.addEmptyAttributeModifier(var42);
            }

            AdventureUtils.setDisplayName(var42, String.format("&a%s&8 (click to browse)", var36.getName()));
            var42.addItemFlags(ItemFlag.values());
            ArrayList var44 = new ArrayList();
            var44.add(
               String.format(
                  "&7&oThere %s %s &7&oitem%s in this type.", var38 == 1 ? "is" : "are", var38 < 1 ? "&c&ono" : "&6&o" + var38, var38 == 1 ? "" : "s"
               )
            );
            AdventureUtils.setLore(var42, var44);
            var42.getPersistentDataContainer().set(TYPE_ID_KEY, PersistentDataType.STRING, var36.getId());
            var40.setItemMeta(var42);
            var33.setItem(slots[var32++], var40);
         }

         ItemStack var35 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
         ItemMeta var37 = var35.getItemMeta();
         var37.setDisplayName(ChatColor.RED + "- No type -");
         var35.setItemMeta(var37);
         ItemStack var39 = new ItemStack(Material.ARROW);
         ItemMeta var41 = var39.getItemMeta();
         var41.setDisplayName(ChatColor.GREEN + "Next Page");
         var39.setItemMeta(var41);
         ItemStack var43 = new ItemStack(Material.ARROW);
         ItemMeta var45 = var43.getItemMeta();
         var45.setDisplayName(ChatColor.GREEN + "Previous Page");
         var43.setItemMeta(var45);

         while (var32 < slots.length) {
            var33.setItem(slots[var32++], var35);
         }

         var33.setItem(18, this.page > 1 ? var43 : null);
         var33.setItem(26, var31 >= MMOItems.plugin.getTypes().getAll().size() ? null : var39);
         return var33;
      } else {
         Inventory var1 = Bukkit.createInventory(
            this, 54, (this.deleteMode ? "Delete Mode: " : "Item Explorer: ") + MythicLib.plugin.getAdventureParser().stripColors(this.type.getName())
         );
         ItemStack var2 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
         ItemMeta var3 = var2.getItemMeta();
         var3.setDisplayName(ChatColor.RED + "- Error -");
         ArrayList var4 = new ArrayList();
         var4.add("§§oAn error occurred while");
         var4.add("§§otrying to generate that item.");
         var3.setLore(var4);
         var2.setItemMeta(var3);
         ItemStack var5 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
         ItemMeta var6 = var5.getItemMeta();
         var6.setDisplayName(ChatColor.RED + "- No Item -");
         var5.setItemMeta(var6);
         ItemStack var7 = new ItemStack(Material.ARROW);
         ItemMeta var8 = var7.getItemMeta();
         var8.setDisplayName(ChatColor.GREEN + "Next Page");
         var7.setItemMeta(var8);
         ItemStack var9 = new ItemStack(Material.ARROW);
         ItemMeta var10 = var9.getItemMeta();
         var10.setDisplayName(ChatColor.GREEN + "⇨" + " Back");
         var9.setItemMeta(var10);
         ItemStack var11 = new ItemStack(new ItemStack(Material.WRITABLE_BOOK));
         ItemMeta var12 = var11.getItemMeta();
         var12.setDisplayName(ChatColor.GREEN + "Create New");
         var11.setItemMeta(var12);
         ItemStack var13 = new ItemStack(new ItemStack(Material.CAULDRON));
         ItemMeta var14 = var13.getItemMeta();
         var14.setDisplayName(ChatColor.RED + (this.deleteMode ? "Cancel Deletion" : "Delete Item"));
         var13.setItemMeta(var14);
         ItemStack var15 = new ItemStack(Material.ARROW);
         ItemMeta var16 = var15.getItemMeta();
         var16.setDisplayName(ChatColor.GREEN + "Previous Page");
         var15.setItemMeta(var16);
         if (this.type == Type.BLOCK) {
            ItemStack var17 = new ItemStack(Material.HOPPER);
            ItemMeta var18 = var17.getItemMeta();
            var18.setDisplayName(ChatColor.GREEN + "Download Default Resourcepack");
            var18.setLore(
               Arrays.asList(
                  ChatColor.LIGHT_PURPLE + "Only seeing stone blocks?",
                  "",
                  ChatColor.RED + "By downloading the default resourcepack you can",
                  ChatColor.RED + "edit the blocks however you want.",
                  ChatColor.RED + "You will still have to add it to your server!"
               )
            );
            var17.setItemMeta(var18);
            var1.setItem(45, var17);
         }

         HashMap<Double, ArrayList<MMOItemTemplate>> var46 = BrowserDisplayIDX.select(MMOItems.plugin.getTemplates().getTemplates(this.type));
         int[] var47 = this.type.isFourGUIMode() ? slotsAlt : slots;
         int var19 = (this.page - 1) * var47.length;
         int var20 = this.page * var47.length;
         int var50 = 0;
         int var22 = this.type.isFourGUIMode() ? 4 : 3;
         int var23 = 0;

         for (Entry<Double, ArrayList<MMOItemTemplate>> var25 : var46.entrySet()) {
            for (int var26 = ((ArrayList)var25.getValue()).size(); var26 > 0; var26 -= var22) {
               var23 += var22;
            }
         }

         for (int var51 = var19; var51 < Math.min(var20, var23); var51++) {
            MMOItemTemplate var53 = BrowserDisplayIDX.getAt(var51, var46);
            if (var53 == null) {
               var1.setItem(var47[var50], var5);
               var50 += 7;
               if (var50 >= var47.length) {
                  var50 -= 7 * var22;
                  var50++;
               }
            } else {
               ItemStack var55 = var53.newBuilder(this.playerData.getRPG(), true).build().newBuilder().build();
               if (var55 != null && !var55.getType().isAir() && var55.getType().isItem() && var55.getItemMeta() != null) {
                  ItemMeta var27 = var55.getItemMeta();
                  List var28 = var27.getLore();
                  if (var28 == null) {
                     var28 = new ArrayList();
                  }

                  var28.add("");
                  if (this.deleteMode) {
                     var28.add(ChatColor.RED + "✖" + " CLICK TO DELETE " + "✖");
                     var27.setDisplayName(ChatColor.RED + "DELETE: " + (var27.hasDisplayName() ? var27.getDisplayName() : MMOUtils.getDisplayName(var55)));
                  } else {
                     var28.add(ChatColor.YELLOW + "▸" + " Left click to obtain this item.");
                     var28.add(ChatColor.YELLOW + "▸" + " Right click to edit this item.");
                  }

                  var27.setLore(var28);
                  var55.setItemMeta(var27);
                  this.cached.put(var53.getId(), var55);
                  var1.setItem(var47[var50], this.cached.get(var53.getId()));
                  var50 += 7;
                  if (var50 >= var47.length) {
                     var50 -= 7 * var22;
                     var50++;
                  }
               } else {
                  this.cached.put(var53.getId(), var2);
                  var1.setItem(var47[var50], var2);
                  var50 += 7;
                  if (var50 >= var47.length) {
                     var50 -= 7 * var22;
                     var50++;
                  }
               }
            }
         }

         if (!this.deleteMode) {
            var1.setItem(51, var11);
         }

         var1.setItem(47, var13);
         var1.setItem(49, var9);
         var1.setItem(18, this.page > 1 ? var15 : null);
         var1.setItem(26, var20 >= var23 ? null : var7);

         for (int var57 : var47) {
            if (SilentNumbers.isAir(var1.getItem(var57))) {
               var1.setItem(var57, var5);
            }
         }

         return var1;
      }
   }

   public Type getType() {
      return this.type;
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory()) {
         ItemStack var2 = var1.getCurrentItem();
         if (MMOUtils.isMetaItem(var2, false)) {
            if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next Page")) {
               this.page++;
               this.open();
            } else if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous Page")) {
               this.page--;
               this.open();
            } else if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "⇨" + " Back")) {
               new ItemBrowser(this.getPlayer()).open();
            } else if (var2.getItemMeta().getDisplayName().equals(ChatColor.RED + "Cancel Deletion")) {
               this.deleteMode = false;
               this.open();
            } else if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Create New")) {
               new NewItemEdition(this).enable("Write in the chat the text you want.");
            } else if (this.type != null && var2.getItemMeta().getDisplayName().equals(ChatColor.RED + "Delete Item")) {
               this.deleteMode = true;
               this.open();
            } else if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Download Default Resourcepack")) {
               MythicLib.plugin
                  .getVersion()
                  .getWrapper()
                  .sendJson(
                     this.getPlayer(),
                     "[{\"text\":\"Click to download!\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.dropbox.com/s/90w9pvdbfeyxu94/MICustomBlockPack.zip?dl=1\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click to download via Dropbox\",\"italic\":true,\"color\":\"white\"}]}}]"
                  );
               this.getPlayer().closeInventory();
            } else if (this.type == null && !var2.getItemMeta().getDisplayName().equals(ChatColor.RED + "- No type -")) {
               String var3 = (String)var2.getItemMeta().getPersistentDataContainer().get(TYPE_ID_KEY, PersistentDataType.STRING);
               new ItemBrowser(this.getPlayer(), MMOItems.plugin.getTypes().get(var3)).open();
            }
         }

         String var4 = NBTItem.get(var2).getString("MMOITEMS_ITEM_ID");
         if (!var4.equals("")) {
            if (this.deleteMode) {
               MMOItems.plugin.getTemplates().deleteTemplate(this.type, var4);
               this.deleteMode = false;
               this.open();
            } else {
               if (var1.getAction() == InventoryAction.PICKUP_ALL) {
                  this.getPlayer().getInventory().addItem(new ItemStack[]{MMOItems.plugin.getItem(this.type, var4, this.playerData)});
                  this.getPlayer().playSound(this.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.0F);
               }

               if (var1.getAction() == InventoryAction.PICKUP_HALF) {
                  new ItemEdition(this.getPlayer(), MMOItems.plugin.getTemplates().getTemplate(this.type, var4)).open();
               }
            }
         }
      }
   }
}
