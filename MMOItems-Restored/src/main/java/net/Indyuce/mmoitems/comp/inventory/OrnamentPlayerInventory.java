package net.Indyuce.mmoitems.comp.inventory;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class OrnamentPlayerInventory implements PlayerInventory, Listener {
   @Override
   public List<EquippedItem> getInventory(Player player) {
      ArrayList list = new ArrayList();
      ItemStack[] contents = player.getInventory().getContents();

      for (int i = 0; i < contents.length; i++) {
         ItemStack item = contents[i];
         if (item != null && item.getType() != Material.AIR) {
            NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(item);
            Type type = Type.get(nBTItem.getType());
            if (type != null && type.getSupertype().equals(Type.ORNAMENT)) {
               list.add(new SlotEquippedItem(player, i, nBTItem, EquipmentSlot.OTHER));
            }
         }
      }

      return list;
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void updateOnItemPickup(EntityPickupItemEvent event) {
      if (event.getEntityType() == EntityType.PLAYER) {
         NBTItem nbtItem = NBTItem.get(event.getItem().getItemStack());
         if (nbtItem.hasType() && Type.get(nbtItem.getType()).getSupertype().equals(Type.ORNAMENT)) {
            PlayerData.get((Player)event.getEntity()).getInventory().scheduleUpdate();
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void updateOnItemDrop(PlayerDropItemEvent event) {
      NBTItem nbtItem = NBTItem.get(event.getItemDrop().getItemStack());
      if (nbtItem.hasType() && Type.get(nbtItem.getType()).getSupertype().equals(Type.ORNAMENT)) {
         PlayerData.get(event.getPlayer()).updateInventory();
      }
   }
}
