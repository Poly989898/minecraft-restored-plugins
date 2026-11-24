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
   public List<EquippedItem> getInventory(Player var1) {
      ArrayList var2 = new ArrayList();
      ItemStack[] var3 = var1.getInventory().getContents();

      for (int var4 = 0; var4 < var3.length; var4++) {
         ItemStack var5 = var3[var4];
         if (var5 != null && var5.getType() != Material.AIR) {
            NBTItem var6 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var5);
            Type var7 = Type.get(var6.getType());
            if (var7 != null && var7.getSupertype().equals(Type.ORNAMENT)) {
               var2.add(new SlotEquippedItem(var1, var4, var6, EquipmentSlot.OTHER));
            }
         }
      }

      return var2;
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void updateOnItemPickup(EntityPickupItemEvent var1) {
      if (var1.getEntityType() == EntityType.PLAYER) {
         NBTItem var2 = NBTItem.get(var1.getItem().getItemStack());
         if (var2.hasType() && Type.get(var2.getType()).getSupertype().equals(Type.ORNAMENT)) {
            PlayerData.get((Player)var1.getEntity()).getInventory().scheduleUpdate();
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void updateOnItemDrop(PlayerDropItemEvent var1) {
      NBTItem var2 = NBTItem.get(var1.getItemDrop().getItemStack());
      if (var2.hasType() && Type.get(var2.getType()).getSupertype().equals(Type.ORNAMENT)) {
         PlayerData.get(var1.getPlayer()).updateInventory();
      }
   }
}
