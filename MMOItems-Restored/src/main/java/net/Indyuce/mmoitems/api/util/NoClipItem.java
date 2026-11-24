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
   public NoClipItem(Location var1, ItemStack var2) {
      this.item = var1.getWorld().dropItem(var1, this.stripItemData(var2));
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
   public void a(InventoryPickupItemEvent var1) {
      if (var1.getItem().equals(this.item)) {
         var1.setCancelled(true);
         this.close();
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void b(EntityPortalEnterEvent var1) {
      if (var1.getEntity().equals(this.item)) {
         this.close();
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void c(EntityPickupItemEvent var1) {
      if (var1.getItem().equals(this.item)) {
         this.close();
      }
   }

   private ItemStack stripItemData(ItemStack var1) {
      NBTItem var2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1);
      ItemStack var3 = new ItemStack(var1.getType());
      ItemMeta var4 = var3.getItemMeta();
      var3.setAmount(1);
      if (var1.getItemMeta().hasEnchants()) {
         var4.addEnchant(VEnchantment.POWER.get(), 0, true);
         var4.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
      }

      if (MythicLib.plugin.getVersion().isStrictlyHigher(new int[]{1, 13})
         && var1.getItemMeta().hasCustomModelData()
         && var1.getItemMeta().getCustomModelData() != 0) {
         var4.setCustomModelData(var1.getItemMeta().getCustomModelData());
      }

      if (var1.getItemMeta() instanceof SkullMeta) {
         try {
            Object var5 = MythicLib.plugin.getVersion().getWrapper().getProfile((SkullMeta)var1.getItemMeta());
            MythicLib.plugin.getVersion().getWrapper().setProfile((SkullMeta)var4, var5);
         } catch (RuntimeException var7) {
            MMOItems.plugin
               .getLogger()
               .log(Level.SEVERE, "Could not set skull texture on stripItemData method in the NoClipItem class. Please report this issue!");
         }
      }

      if (var1.getItemMeta() instanceof LeatherArmorMeta && var4 instanceof LeatherArmorMeta) {
         ((LeatherArmorMeta)var4).setColor(((LeatherArmorMeta)var1.getItemMeta()).getColor());
      }

      var3.setItemMeta(var4);
      NBTItem var8 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var3);
      var8.addTag(new ItemTag[]{new ItemTag("MMOITEMS_TIER", var2.getString("MMOITEMS_TIER").trim().isEmpty() ? null : var2.getString("MMOITEMS_TIER"))});
      Random var6 = new Random();
      var8.addTag(new ItemTag[]{new ItemTag("MMOITEMS_NO_STACK", var6.nextInt(Integer.MAX_VALUE))});
      var8.addTag(new ItemTag[]{new ItemTag("MMOITEMS_NO_CLIP_ITEM", true)});
      return var8.toItem();
   }
}
