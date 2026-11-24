package net.Indyuce.mmoitems.api.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.Recipe;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftingStatus {
   private final Map<CraftingStation, CraftingStatus.CraftingQueue> queues = new HashMap<>();

   public void load(PlayerData var1, ConfigurationSection var2) {
      String var3 = var1.isOnline() ? var1.getPlayer().getName() : "Unknown Player";

      for (String var5 : var2.getKeys(false)) {
         if (!MMOItems.plugin.getCrafting().hasStation(var5)) {
            MMOItems.plugin
               .getLogger()
               .log(
                  Level.SEVERE,
                  "An error occurred while trying to load crafting station recipe data of '"
                     + var3
                     + "': could not find crafting station with ID '"
                     + var5
                     + "', make sure you backup that player data file before the user logs off."
               );
         } else {
            CraftingStation var6 = MMOItems.plugin.getCrafting().getStation(var5);
            CraftingStatus.CraftingQueue var7 = new CraftingStatus.CraftingQueue(var6);
            this.queues.put(var6, var7);
            Optional var8 = var2.getConfigurationSection(var5).getKeys(false).stream().findFirst();
            boolean var9 = var8.isPresent() && var2.contains(var5 + "." + (String)var8.get() + ".delay");

            for (String var11 : var2.getConfigurationSection(var5).getKeys(false)) {
               String var12 = var2.getString(var5 + "." + var11 + ".recipe");
               if (var12 != null && var6.hasRecipe(var12)) {
                  Recipe var13 = var6.getRecipe(var12);
                  if (!(var13 instanceof CraftingRecipe)) {
                     MMOItems.plugin
                        .getLogger()
                        .log(
                           Level.SEVERE,
                           "An error occurred while trying to load crafting station recipe data of '"
                              + var3
                              + "': recipe '"
                              + var13.getId()
                              + "' is not a CRAFTING recipe."
                        );
                  } else if (var9) {
                     long var14 = var2.getLong(var5 + "." + var11 + ".started");
                     long var16 = var2.getLong(var5 + "." + var11 + ".delay");
                     var7.add((CraftingRecipe)var13, var14, var14 + var16);
                  } else {
                     var7.add((CraftingRecipe)var13, var2.getLong(var5 + "." + var11 + ".start"), var2.getLong(var5 + "." + var11 + ".completion"));
                  }
               } else {
                  MMOItems.plugin
                     .getLogger()
                     .log(
                        Level.SEVERE,
                        "An error occurred while trying to load crafting station recipe data of '"
                           + var3
                           + "': could not find recipe with ID '"
                           + var12
                           + "', make sure you backup that player data file before the user logs off."
                     );
               }
            }
         }
      }
   }

   public void save(ConfigurationSection var1) {
      this.queues.forEach((var1x, var2) -> {
         for (CraftingStatus.CraftingQueue.QueueItem var4 : var2.getCrafts()) {
            var1.set(var1x.getId() + ".recipe-" + var4.getUniqueId().toString() + ".recipe", var4.getRecipe().getId());
            var1.set(var1x.getId() + ".recipe-" + var4.getUniqueId().toString() + ".start", var4.start);
            var1.set(var1x.getId() + ".recipe-" + var4.getUniqueId().toString() + ".completion", var4.completion);
         }
      });
   }

   public CraftingStatus.CraftingQueue getQueue(@NotNull CraftingStation var1) {
      return this.queues.computeIfAbsent(var1, CraftingStatus.CraftingQueue::new);
   }

   public static class CraftingQueue {
      private final String station;
      private final List<CraftingStatus.CraftingQueue.QueueItem> crafts = new ArrayList<>();

      public CraftingQueue(CraftingStation var1) {
         this.station = var1.getId();
      }

      public List<CraftingStatus.CraftingQueue.QueueItem> getCrafts() {
         return this.crafts;
      }

      public boolean isFull(CraftingStation var1) {
         return this.crafts.size() >= var1.getMaxQueueSize();
      }

      public void remove(CraftingStatus.CraftingQueue.QueueItem var1) {
         int var2 = this.crafts.indexOf(var1);
         Validate.isTrue(var2 >= 0, "Could not find item in queue");
         this.crafts.remove(var2);
         long var3 = Math.min(var1.getLeft(), (long)(1000.0 * var1.getRecipe().getCraftingTime()));

         for (int var5 = var2; var5 < this.crafts.size(); var5++) {
            this.crafts.get(var5).removeDelay(var3);
         }
      }

      @Nullable
      public CraftingStatus.CraftingQueue.QueueItem getCraft(UUID var1) {
         for (CraftingStatus.CraftingQueue.QueueItem var3 : this.crafts) {
            if (var3.getUniqueId().equals(var1)) {
               return var3;
            }
         }

         return null;
      }

      public void add(CraftingRecipe var1) {
         long var2 = (long)var1.getCraftingTime() * 1000L
            + (this.crafts.isEmpty() ? System.currentTimeMillis() : this.crafts.get(this.crafts.size() - 1).completion);
         this.add(var1, System.currentTimeMillis(), var2);
      }

      private void add(CraftingRecipe var1, long var2, long var4) {
         this.crafts.add(new CraftingStatus.CraftingQueue.QueueItem(var1, var2, var4));
      }

      public CraftingStation getStation() {
         return MMOItems.plugin.getCrafting().getStation(this.station);
      }

      public class QueueItem {
         private final String recipe;
         private final UUID uuid = UUID.randomUUID();
         private final long start;
         private long completion;

         public QueueItem(@NotNull CraftingRecipe nullx, long nullxx, long nullxxx) {
            this.recipe = nullx.getId();
            this.start = nullxx;
            this.completion = nullxxx;
         }

         public UUID getUniqueId() {
            return this.uuid;
         }

         public CraftingRecipe getRecipe() {
            return (CraftingRecipe)CraftingQueue.this.getStation().getRecipe(this.recipe);
         }

         public boolean isReady() {
            return this.getLeft() == 0L;
         }

         public void removeDelay(long var1) {
            this.completion -= var1;
         }

         public long getElapsed() {
            return Math.max((long)this.getRecipe().getCraftingTime() * 1000L, System.currentTimeMillis() - this.start);
         }

         public long getLeft() {
            return Math.max(0L, this.completion - System.currentTimeMillis());
         }

         @Override
         public boolean equals(Object var1) {
            if (this == var1) {
               return true;
            } else if (var1 != null && this.getClass() == var1.getClass()) {
               CraftingStatus.CraftingQueue.QueueItem var2 = (CraftingStatus.CraftingQueue.QueueItem)var1;
               return Objects.equals(this.uuid, var2.uuid);
            } else {
               return false;
            }
         }

         @Override
         public int hashCode() {
            return Objects.hash(this.uuid);
         }
      }
   }
}
