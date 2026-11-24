package io.lumine.mythic.lib.util;

import com.mojang.authlib.GameProfile;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class NoClipItem extends TemporaryListener {
   private final Item item;

   public NoClipItem(@NotNull Location loc, @NotNull ItemStack item) {
      super();
      this.item = loc.getWorld().dropItem(loc, this.stripItemData(item));
      this.item.setPickupDelay(1000000);
   }

   public Item getEntity() {
      return this.item;
   }

   @Override
   public void whenClosed() {
      this.item.remove();
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

   private ItemStack stripItemData(ItemStack oldItem) {
      NBTItem oldItemNBT = MythicLib.plugin.getVersion().getWrapper().getNBTItem(oldItem);
      ItemStack newItem = new ItemStack(oldItem.getType());
      ItemMeta newItemMeta = newItem.getItemMeta();
      newItem.setAmount(1);
      if (oldItem.getItemMeta().hasEnchants()) {
         newItemMeta.addEnchant(Enchantment.KNOCKBACK, 0, true);
         newItemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
      }

      if (oldItem.getItemMeta().hasCustomModelData() && oldItem.getItemMeta().getCustomModelData() != 0) {
         newItemMeta.setCustomModelData(oldItem.getItemMeta().getCustomModelData());
      }

      if (oldItem.getItemMeta() instanceof SkullMeta) {
         try {
            Field oldProfileField = oldItem.getItemMeta().getClass().getDeclaredField("profile");
            oldProfileField.setAccessible(true);
            GameProfile oldProfile = (GameProfile)oldProfileField.get(oldItem.getItemMeta());
            Field profileField = newItemMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(newItemMeta, oldProfile);
         } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
            MythicLib.plugin
               .getLogger()
               .log(Level.WARNING, "Could not set skull texture on stripItemData method in the NoClipItem class. Please report this issue!");
         }
      }

      if (oldItem.getItemMeta() instanceof LeatherArmorMeta && newItemMeta instanceof LeatherArmorMeta) {
         ((LeatherArmorMeta)newItemMeta).setColor(((LeatherArmorMeta)oldItem.getItemMeta()).getColor());
      }

      newItem.setItemMeta(newItemMeta);
      NBTItem newItemNBT = MythicLib.plugin.getVersion().getWrapper().getNBTItem(newItem);
      newItemNBT.addTag(new ItemTag("MMOITEMS_TIER", oldItemNBT.getString("MMOITEMS_TIER").trim().isEmpty() ? null : oldItemNBT.getString("MMOITEMS_TIER")));
      Random random = new Random();
      newItemNBT.addTag(new ItemTag("MMOITEMS_NO_STACK", random.nextInt(Integer.MAX_VALUE)));
      newItemNBT.addTag(new ItemTag("MMOITEMS_NO_CLIP_ITEM", true));
      return newItemNBT.toItem();
   }
}
