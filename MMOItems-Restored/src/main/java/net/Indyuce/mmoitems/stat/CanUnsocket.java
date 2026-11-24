package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.List;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.GemSocketsData;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class CanUnsocket extends BooleanStat implements ConsumableItemInteraction {
   public CanUnsocket() {
      super(
         "CAN_UNSOCKET",
         Material.PAPER,
         "Can Unsocket?",
         new String[]{"This item, when used on another item, if", "that other item has Gem Stones", "may be used to remove those Gems."},
         new String[]{"consumable"}
      );
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent event, @NotNull PlayerData playerData, @NotNull Consumable consumable, @NotNull NBTItem nbtItem, Type type
   ) {
      if (type == null) {
         return false;
      }

      VolatileMMOItem volatileMMOItem = new VolatileMMOItem(nbtItem);
      if (!volatileMMOItem.hasData(ItemStats.GEM_SOCKETS)) {
         return false;
      }

      GemSocketsData data = (GemSocketsData)volatileMMOItem.getData(ItemStats.GEM_SOCKETS);
      if (data != null && data.getGemstones().size() != 0) {
         Player player = playerData.getPlayer();
         LiveMMOItem liveMMOItem = new LiveMMOItem(nbtItem);
         List list = liveMMOItem.extractGemstones();
         if (list.isEmpty()) {
            Message.RANDOM_UNSOCKET_GEM_TOO_OLD.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(event.getCurrentItem())).send(player);
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }
}
