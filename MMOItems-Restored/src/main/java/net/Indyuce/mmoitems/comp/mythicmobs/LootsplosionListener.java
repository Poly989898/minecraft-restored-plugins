package net.Indyuce.mmoitems.comp.mythicmobs;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.version.VParticle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LootsplosionListener implements Listener {
   private static final Random RANDOM = new Random();
   private final boolean colored = MMOItems.plugin.getConfig().getBoolean("lootsplosion.color");

   @EventHandler(priority = EventPriority.HIGH)
   public void b(MythicMobDeathEvent event) {
      if (event.getMob().getVariables().has("Lootsplosion")) {
         new LootsplosionListener.LootsplosionHandler(event);
      }
   }

   private Vector randomVector() {
      double lootsplosionOffset = MMOItems.plugin.getConfig().getDouble("lootsplosion.offset");
      double lootsplosionHeight = MMOItems.plugin.getConfig().getDouble("lootsplosion.height");
      return new Vector(Math.cos(RANDOM.nextDouble() * Math.PI * 2.0) * lootsplosionOffset, lootsplosionHeight, Math.sin(RANDOM.nextDouble() * Math.PI * 2.0) * lootsplosionOffset);
   }

   public class LootColor extends BukkitRunnable {
      private final Item item;
      private final Color color;
      private int j = 0;

      public LootColor(Item nullx, ChatColor nullxx) {
         this.item = nullx;
         this.color = MMOUtils.toRGB(nullxx);
         this.runTaskTimer(MMOItems.plugin, 0L, 1L);
      }

      public void run() {
         if (this.j++ <= 100 && !this.item.isDead() && !this.item.isOnGround()) {
            this.item.getWorld().spawnParticle(VParticle.REDSTONE.get(), this.item.getLocation(), 1, new DustOptions(this.color, 1.3F));
         } else {
            this.cancel();
         }
      }
   }

   public class LootsplosionHandler implements Listener {
      private final List<ItemStack> drops;
      private final double offset;

      public LootsplosionHandler(MythicMobDeathEvent nullx) {
         this.offset = nullx.getEntity().getHeight() / 2.0;
         this.drops = new ArrayList<>(nullx.getDrops());
         Bukkit.getPluginManager().registerEvents(this, MMOItems.plugin);
      }

      private void close() {
         ItemSpawnEvent.getHandlerList().unregister(this);
      }

      @EventHandler
      public void a(ItemSpawnEvent event) {
         Item entity = event.getEntity();
         if (!this.drops.contains(entity.getItemStack())) {
            this.close();
         } else {
            this.drops.remove(entity.getItemStack());
            entity.teleport(entity.getLocation().add(0.0, this.offset, 0.0));
            entity.setVelocity(LootsplosionListener.this.randomVector());
            if (LootsplosionListener.this.colored) {
               Bukkit.getScheduler().runTask(MMOItems.plugin, () -> {
                  NBTItem nbtItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(entity.getItemStack());
                  if (nbtItem.hasTag("MMOITEMS_TIER")) {
                     ItemTier itemTier = MMOItems.plugin.getTiers().get(nbtItem.getString("MMOITEMS_TIER"));
                     if (itemTier.hasColor()) {
                        LootsplosionListener.this.new LootColor(entity, itemTier.getColor());
                     }
                  }
               });
            }
         }
      }
   }
}
