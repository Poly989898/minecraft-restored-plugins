package net.Indyuce.mmoitems.api.util;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.version.VEnchantment;
import java.util.Random;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

@Deprecated
public class NoClipItem implements Listener {
   private final Item item;

   /** @deprecated */
   public NoClipItem(Location location, ItemStack item) {
      this.item = location.getWorld().dropItem(location, this.stripItemData(item));
      this.item.setPickupDelay(1000000);
      Bukkit.getPluginManager().registerEvents(this, MMOItems.plugin);
   }

   public Item getEntity() {
      return this.item;
   }

   public void close() {
      this.item.remove();
      EntityPortalEnterEvent.getHandlerList().unregister(this);
      InventoryPickupItemEvent.getHandlerList().unregister(this);
      EntityPickupItemEvent.getHandlerList().unregister(this);
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void a(InventoryPickupItemEvent event) {
      if (event.getItem().equals(this.item)) {
         event.setCancelled(true);
         this.close();
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void b(EntityPortalEnterEvent event) {
      if (event.getEntity().equals(this.item)) {
         this.close();
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void c(EntityPickupItemEvent event) {
      if (event.getItem().equals(this.item)) {
         this.close();
      }
   }

   private ItemStack stripItemData(ItemStack item) {
      NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(item);
      ItemStack item2 = new ItemStack(item.getType());
      ItemMeta itemMeta = item2.getItemMeta();
      item2.setAmount(1);
      if (item.getItemMeta().hasEnchants()) {
         itemMeta.addEnchant(VEnchantment.POWER.get(), 0, true);
         itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
      }

      if (MythicLib.plugin.getVersion().isStrictlyHigher(new int[]{1, 13})
         && item.getItemMeta().hasCustomModelData()
         && item.getItemMeta().getCustomModelData() != 0) {
         itemMeta.setCustomModelData(item.getItemMeta().getCustomModelData());
      }

      if (item.getItemMeta() instanceof SkullMeta) {
         try {
            Object profile = MythicLib.plugin.getVersion().getWrapper().getProfile((SkullMeta)item.getItemMeta());
            MythicLib.plugin.getVersion().getWrapper().setProfile((SkullMeta)itemMeta, profile);
         } catch (RuntimeException exception) {
            MMOItems.plugin
               .getLogger()
               .log(Level.SEVERE, "Could not set skull texture on stripItemData method in the NoClipItem class. Please report this issue!");
         }
      }

      if (item.getItemMeta() instanceof LeatherArmorMeta && itemMeta instanceof LeatherArmorMeta) {
         ((LeatherArmorMeta)itemMeta).setColor(((LeatherArmorMeta)item.getItemMeta()).getColor());
      }

      item2.setItemMeta(itemMeta);
      NBTItem nBTItem2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(item2);
      nBTItem2.addTag(new ItemTag[]{new ItemTag("MMOITEMS_TIER", nBTItem.getString("MMOITEMS_TIER").trim().isEmpty() ? null : nBTItem.getString("MMOITEMS_TIER"))});
      Random random = new Random();
      nBTItem2.addTag(new ItemTag[]{new ItemTag("MMOITEMS_NO_STACK", random.nextInt(Integer.MAX_VALUE))});
      nBTItem2.addTag(new ItemTag[]{new ItemTag("MMOITEMS_NO_CLIP_ITEM", true)});
      return nBTItem2.toItem();
   }
}
