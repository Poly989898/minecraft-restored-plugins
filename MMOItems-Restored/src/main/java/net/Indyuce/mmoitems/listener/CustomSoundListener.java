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
   public void a(EntityDamageByEntityEvent var1) {
      if (var1.getDamager() instanceof Player && var1.getEntity() instanceof LivingEntity) {
         Player var2 = (Player)var1.getDamager();
         playSound(var2.getInventory().getItemInMainHand(), CustomSound.ON_ATTACK, var2);
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void b(EntityPickupItemEvent var1) {
      if (var1.getEntityType().equals(EntityType.PLAYER)) {
         playSound(var1.getItem().getItemStack(), CustomSound.ON_PICKUP, (Player)var1.getEntity());
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void c(BlockBreakEvent var1) {
      playSound(var1.getPlayer().getInventory().getItemInMainHand(), CustomSound.ON_BLOCK_BREAK, var1.getPlayer());
   }

   @EventHandler
   public void d(PlayerInteractEvent var1) {
      if (var1.getAction() != Action.PHYSICAL && var1.hasItem()) {
         if (var1.getAction().name().contains("RIGHT_CLICK")) {
            playSound(var1.getItem(), CustomSound.ON_RIGHT_CLICK, var1.getPlayer());
         }

         if (var1.getAction().name().contains("LEFT_CLICK")) {
            playSound(var1.getItem(), CustomSound.ON_LEFT_CLICK, var1.getPlayer());
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void e(CraftItemEvent var1) {
      playSound(var1.getInventory().getResult(), CustomSound.ON_CRAFT, var1.getWhoClicked().getLocation());
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void f(FurnaceSmeltEvent var1) {
      playSound(var1.getResult(), CustomSound.ON_CRAFT, var1.getBlock().getLocation());
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void g(PlayerItemConsumeEvent var1) {
      playSound(var1.getItem(), CustomSound.ON_CONSUME, var1.getPlayer());
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void h1(PlayerItemBreakEvent var1) {
      playSound(var1.getBrokenItem(), CustomSound.ON_ITEM_BREAK, var1.getPlayer());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void i(BlockPlaceEvent var1) {
      playSound(var1.getItemInHand(), CustomSound.ON_PLACED, var1.getPlayer());
   }

   public static void playSound(@Nullable ItemStack var0, @NotNull CustomSound var1, @NotNull Player var2) {
      playSound(var0, var1, var2.getLocation(), null);
   }

   public static void playSound(@Nullable ItemStack var0, @NotNull CustomSound var1, @NotNull Player var2, @Nullable Sound var3) {
      playSound(var0, var1, var2.getLocation(), var3);
   }

   public static void playSound(@Nullable ItemStack var0, @NotNull CustomSound var1, @NotNull Location var2) {
      playSound(var0, var1, var2, null);
   }

   public static void playSound(@Nullable ItemStack var0, @NotNull CustomSound var1, @NotNull Location var2, @Nullable Sound var3) {
      if (var0 != null) {
         NBTItem var4 = NBTItem.get(var0);
         String var5 = var4.getString("MMOITEMS_SOUND_" + var1.name());
         if (var5 != null && !var5.isEmpty()) {
            SoundReader var6 = new SoundReader(var5, var3);
            var6.play(var2, (float)var4.getDouble("MMOITEMS_SOUND_" + var1.name() + "_VOL"), (float)var4.getDouble("MMOITEMS_SOUND_" + var1.name() + "_PIT"));
         } else {
            if (var3 != null) {
               var2.getWorld().playSound(var2, var3, 1.0F, 1.0F);
            }
         }
      }
   }
}
