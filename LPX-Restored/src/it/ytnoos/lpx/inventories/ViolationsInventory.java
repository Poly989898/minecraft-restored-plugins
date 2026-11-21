package it.ytnoos.lpx.inventories;

import com.google.common.collect.Lists;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.IterationType;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.check.PendingPunishment;
import it.ytnoos.lpx.inventories.items.ItemBuilder;
import it.ytnoos.lpx.inventories.smartinvs.ClickableItem;
import it.ytnoos.lpx.inventories.smartinvs.SmartInventory;
import it.ytnoos.lpx.inventories.smartinvs.content.InventoryContents;
import it.ytnoos.lpx.inventories.smartinvs.content.InventoryProvider;
import it.ytnoos.lpx.inventories.smartinvs.content.Pagination;
import it.ytnoos.lpx.utils.string.MessageFormatter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ViolationsInventory implements InventoryProvider {
   private final Format format = new SimpleDateFormat("dd/MM HH:mm:ss");
   private final LPXBukkit lpxBukkit;

   public ViolationsInventory(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   @Override
   public void init(Player player, InventoryContents inventoryContents) {
      Pagination pagination = inventoryContents.getPagination();
      String string = this.lpxBukkit.getConfigManager().getMessagesConfig().getMainColor();
      String string2 = this.lpxBukkit.getConfigManager().getMessagesConfig().getSecondColor();
      ArrayList arrayList = Lists.newArrayList();

      for (Object var36_o : this.lpxBukkit.getRecentViolations()) { PendingPunishment pendingPunishment = (PendingPunishment) var36_o;
         String string3 = pendingPunishment.getOptions().getCheckOptions().getPath();
         String string4 = string3.substring(0, string3.length() - 1);
         String string5 = string2;
         String string6 = string + "Config: " + string5 + string4;
         ArrayList arrayList2 = new ArrayList();
         arrayList2.add(string6);
         String string7 = this.convertTime(pendingPunishment.getTimestamp());
         String string8 = string2;
         arrayList2.add(string + "Time: " + string8 + string7);
         arrayList2.add("");
         arrayList2.add(string + "Infos:");

         for (FlagInfo flagInfo : pendingPunishment.getInfo()) {
            String string9 = flagInfo.getName();
            String string10 = MessageFormatter.escapeTags(String.valueOf(flagInfo.getValue()));
            String string11 = string2;
            String string12 = string9;
            String string13 = string + string12 + ": " + string11 + string10;
            arrayList2.add(string13);
         }

         arrayList2.add("");
         arrayList2.add(string2 + "Press Q to delete this log");
         arrayList.add(ClickableItem.of(ItemBuilder.of(Material.BOOK).name(string + pendingPunishment.getName()).lore(arrayList2).toItemStack(), inventoryClickEvent -> {
            if (inventoryClickEvent.getClick() == ClickType.DROP || inventoryClickEvent.getClick() == ClickType.CONTROL_DROP) {
               this.lpxBukkit.getRecentViolations().remove(pendingPunishment);
               this.build().open(player, this.getMaxPage(pagination.getPage()));
            }
         }));
      }

      pagination.setItems((ClickableItem[])arrayList.toArray(new ClickableItem[0]));
      pagination.setItemsPerPage(36);
      pagination.addToIterator(inventoryContents.newIterator(IterationType.HORIZONTAL, 0, 0));
      if (!pagination.isFirst()) {
         inventoryContents.set(
            4,
            0,
            ClickableItem.of(new ItemBuilder(Material.ARROW).name(string2 + "Previous page").toItemStack(), inventoryClickEvent2 -> this.build().open(player, pagination.previous().getPage()))
         );
      }

      if (!pagination.isLast()) {
         inventoryContents.set(
            4, 8, ClickableItem.of(new ItemBuilder(Material.ARROW).name(string2 + "Next page").toItemStack(), inventoryClickEvent3 -> this.build().open(player, pagination.next().getPage()))
         );
      }
   }

   public SmartInventory build() {
      return SmartInventory.builder()
         .id("violations")
         .provider(this)
         .manager(this.lpxBukkit.getInventoryManager())
         .size(5, 9)
         .title(
            this.lpxBukkit.getConfigManager().getMessagesConfig().getMainColor()
               + "<bold>LPX >><reset>"
               + this.lpxBukkit.getConfigManager().getMessagesConfig().getSecondColor()
               + " Recent Violations"
         )
         .build();
   }

   private String convertTime(long longValue) {
      return this.format.format(new Date(longValue));
   }

   private int getMaxPage(int value) {
      int value2 = this.lpxBukkit.getRecentViolations().size();
      if (value2 == 0) {
         return 0;
      }

      int value3 = (value2 - 1) / 36;
      return Math.min(value, value3);
   }

}
