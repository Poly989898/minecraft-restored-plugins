package net.Indyuce.mmocore.listener.profession;

import io.lumine.mythic.lib.version.Sounds;
import io.lumine.mythic.lib.version.VParticle;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.event.CustomPlayerFishEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.loot.LootBuilder;
import net.Indyuce.mmocore.loot.fishing.FishingDropItem;
import net.Indyuce.mmocore.manager.profession.FishingManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Statistic;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FishingListener implements Listener {
   private final Set<UUID> fishing = new HashSet<>();
   private static final Random RANDOM = new Random();

   @EventHandler(priority = EventPriority.LOW)
   public void a(PlayerFishEvent event) {
      Player player = event.getPlayer();
      FishHook hook = event.getHook();
      if (event.getState() == State.BITE && !this.fishing.contains(player.getUniqueId()) && !player.hasMetadata("NPC")) {
         FishingManager.FishingDropTable table = MMOCore.plugin.fishingManager.calculateDropTable(player, hook);
         if (table == null) {
            return;
         }

         new FishingListener.FishingData(player, hook, table);
         MMOCoreUtils.displayIndicator(hook.getLocation().add(0.0, 1.25, 0.0), ConfigMessage.fromKey("caught-fish").asLine());
      }
   }

   public class FishingData extends BukkitRunnable implements Listener {
      private final Location location;
      private final FishingDropItem caught;
      private final PlayerData playerData;
      private final Player player;
      private final FishHook hook;
      private final int fishStrength;
      private final int experienceDropped;
      private final int vanillaExpDropped;
      private int currentPulls;
      private long last = System.currentTimeMillis();
      private static final long TIME_OUT = 1000L;

      public FishingData(Player player, FishHook hook, FishingManager.FishingDropTable table) {
         this.location = hook.getLocation();
         this.caught = table.getRandomItem(PlayerData.get(player));
         this.playerData = PlayerData.get(this.player = player);
         this.hook = hook;
         this.fishStrength = (int)Math.floor(this.caught.rollTugs() * (1.0 - PlayerData.get(player).getStats().getStat("FISHING_STRENGTH") / 100.0));
         this.experienceDropped = this.caught.rollExperience();
         this.vanillaExpDropped = this.caught.rollVanillaExp();
         FishingListener.this.fishing.add(player.getUniqueId());
         this.runTaskTimer(MMOCore.plugin, 0L, 2L);
         Bukkit.getPluginManager().registerEvents(this, MMOCore.plugin);
         if (this.fishStrength == 0) {
            this.lootFish();
         }
      }

      public void setCriticalFish() {
         this.currentPulls = this.fishStrength + 2;
      }

      public boolean isTimedOut() {
         return this.last + 1000L < System.currentTimeMillis();
      }

      public boolean pullOnce() {
         this.last = System.currentTimeMillis();
         this.currentPulls++;
         return this.currentPulls >= this.fishStrength;
      }

      public boolean isCriticalFish() {
         return this.currentPulls > this.fishStrength + 1;
      }

      private void close() {
         FishingListener.this.fishing.remove(this.player.getUniqueId());
         this.hook.remove();
         HandlerList.unregisterAll(this);
         this.cancel();
      }

      public void run() {
         if (this.isTimedOut()) {
            this.close();
         }

         this.location
            .getWorld()
            .spawnParticle(
               Particle.CRIT, this.location, 0, 2.0 * (FishingListener.RANDOM.nextDouble() - 0.5), 3.0, 2.0 * (FishingListener.RANDOM.nextDouble() - 0.5), 0.6
            );
      }

      @EventHandler(priority = EventPriority.LOWEST)
      public void a(PlayerFishEvent event) {
         if (event.getPlayer().equals(this.player)
            && (event.getState() == State.CAUGHT_FISH || event.getState() == State.FAILED_ATTEMPT || event.getState() == State.REEL_IN)) {
            event.setCancelled(true);
            if (this.isTimedOut()) {
               this.close();
               this.hook.getWorld().spawnParticle(VParticle.SMOKE.get(), this.hook.getLocation(), 16, 0.0, 0.0, 0.0, 0.1);
               return;
            }

            if (this.currentPulls == 0
               && FishingListener.RANDOM.nextDouble() < PlayerData.get(this.player).getStats().getStat("CRITICAL_FISHING_CHANCE") / 100.0) {
               this.setCriticalFish();
            }

            if (this.pullOnce()) {
               this.lootFish();
            }
         }
      }

      public void lootFish() {
         this.close();
         ItemStack mainhand = this.player.getInventory().getItem(EquipmentSlot.HAND);
         MMOCoreUtils.decreaseDurability(
            this.player, mainhand != null && mainhand.getType() == Material.FISHING_ROD ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND, 1
         );
         if (!this.isCriticalFish()
            && FishingListener.RANDOM.nextDouble() < PlayerData.get(this.player).getStats().getStat("CRITICAL_FISHING_FAILURE_CHANCE") / 100.0) {
            this.player.setVelocity(this.hook.getLocation().subtract(this.player.getLocation()).toVector().setY(0).multiply(3).setY(0.5));
            this.hook.getWorld().spawnParticle(VParticle.SMOKE.get(), this.location, 24, 0.0, 0.0, 0.0, 0.08);
         } else {
            ItemStack collect = this.caught.collect(new LootBuilder(this.playerData));
            if (collect == null) {
               this.hook.getWorld().spawnParticle(VParticle.SMOKE.get(), this.location, 24, 0.0, 0.0, 0.0, 0.08);
            } else {
               Item item = this.hook.getWorld().dropItemNaturally(this.hook.getLocation(), collect);
               CustomPlayerFishEvent called = new CustomPlayerFishEvent(this.playerData, collect, item);
               Bukkit.getPluginManager().callEvent(called);
               if (called.isCancelled()) {
                  item.remove();
               } else {
                  this.player.incrementStatistic(Statistic.FISH_CAUGHT);
                  MMOCoreUtils.displayIndicator(
                     this.location.add(0.0, 1.25, 0.0), ConfigMessage.fromKey("fish-out-water" + (this.isCriticalFish() ? "-crit" : "")).asLine()
                  );
                  Vector vec = this.player.getLocation().subtract(this.hook.getLocation()).toVector();
                  vec.setY(vec.getY() * 0.031 + vec.length() * 0.05);
                  vec.setX(vec.getX() * 0.08);
                  vec.setZ(vec.getZ() * 0.08);
                  item.setVelocity(vec);
                  this.player.getWorld().playSound(this.player.getLocation(), Sounds.BLOCK_NOTE_BLOCK_HAT, 1.0F, 0.0F);

                  for (int j = 0; j < 8; j++) {
                     this.location
                        .getWorld()
                        .spawnParticle(
                           VParticle.FIREWORK.get(),
                           this.location,
                           0,
                           4.0 * (FishingListener.RANDOM.nextDouble() - 0.5),
                           FishingListener.RANDOM.nextDouble() + 1.0,
                           4.0 * (FishingListener.RANDOM.nextDouble() - 0.5),
                           0.08
                        );
                  }

                  if (this.vanillaExpDropped > 0) {
                     this.player.giveExp(this.vanillaExpDropped);
                  }

                  if (this.experienceDropped > 0 && MMOCore.plugin.fishingManager.hasLinkedProfession()) {
                     this.playerData
                        .getCollectionSkills()
                        .giveExperience(MMOCore.plugin.fishingManager.getLinkedProfession(), this.experienceDropped, EXPSource.FISHING, this.location, true);
                  }
               }
            }
         }
      }
   }
}
