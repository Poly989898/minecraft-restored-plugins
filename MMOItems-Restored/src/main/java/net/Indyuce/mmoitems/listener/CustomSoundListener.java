package net.Indyuce.mmoitems.listener;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.CustomSound;
import net.Indyuce.mmoitems.api.util.SoundReader;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomSoundListener implements Listener {
   @EventHandler(priority = EventPriority.HIGH)
   public void a(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
         Player damager = (Player)event.getDamager();
         playSound(damager.getInventory().getItemInMainHand(), CustomSound.ON_ATTACK, damager);
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void b(EntityPickupItemEvent event) {
      if (event.getEntityType().equals(EntityType.PLAYER)) {
         playSound(event.getItem().getItemStack(), CustomSound.ON_PICKUP, (Player)event.getEntity());
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void c(BlockBreakEvent event) {
      playSound(event.getPlayer().getInventory().getItemInMainHand(), CustomSound.ON_BLOCK_BREAK, event.getPlayer());
   }

   @EventHandler
   public void d(PlayerInteractEvent event) {
      if (event.getAction() != Action.PHYSICAL && event.hasItem()) {
         if (event.getAction().name().contains("RIGHT_CLICK")) {
            playSound(event.getItem(), CustomSound.ON_RIGHT_CLICK, event.getPlayer());
         }

         if (event.getAction().name().contains("LEFT_CLICK")) {
            playSound(event.getItem(), CustomSound.ON_LEFT_CLICK, event.getPlayer());
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void e(CraftItemEvent event) {
      playSound(event.getInventory().getResult(), CustomSound.ON_CRAFT, event.getWhoClicked().getLocation());
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void f(FurnaceSmeltEvent event) {
      playSound(event.getResult(), CustomSound.ON_CRAFT, event.getBlock().getLocation());
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void g(PlayerItemConsumeEvent event) {
      playSound(event.getItem(), CustomSound.ON_CONSUME, event.getPlayer());
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void h1(PlayerItemBreakEvent event) {
      playSound(event.getBrokenItem(), CustomSound.ON_ITEM_BREAK, event.getPlayer());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void i(BlockPlaceEvent event) {
      playSound(event.getItemInHand(), CustomSound.ON_PLACED, event.getPlayer());
   }

   public static void playSound(@Nullable ItemStack item, @NotNull CustomSound customSound, @NotNull Player player) {
      playSound(item, customSound, player.getLocation(), null);
   }

   public static void playSound(@Nullable ItemStack item, @NotNull CustomSound customSound, @NotNull Player player, @Nullable Sound sound) {
      playSound(item, customSound, player.getLocation(), sound);
   }

   public static void playSound(@Nullable ItemStack item, @NotNull CustomSound customSound, @NotNull Location location) {
      playSound(item, customSound, location, null);
   }

   public static void playSound(@Nullable ItemStack item, @NotNull CustomSound customSound, @NotNull Location location, @Nullable Sound sound) {
      if (item != null) {
         NBTItem nbtItem = NBTItem.get(item);
         String text = nbtItem.getString("MMOITEMS_SOUND_" + customSound.name());
         if (text != null && !text.isEmpty()) {
            SoundReader soundReader = new SoundReader(text, sound);
            soundReader.play(location, (float)nbtItem.getDouble("MMOITEMS_SOUND_" + customSound.name() + "_VOL"), (float)nbtItem.getDouble("MMOITEMS_SOUND_" + customSound.name() + "_PIT"));
         } else {
            if (sound != null) {
               location.getWorld().playSound(location, sound, 1.0F, 1.0F);
            }
         }
      }
   }
}
