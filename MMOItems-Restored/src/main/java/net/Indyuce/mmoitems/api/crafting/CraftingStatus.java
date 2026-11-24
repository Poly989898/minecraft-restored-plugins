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

   public void load(PlayerData playerData, ConfigurationSection section) {
      String text = playerData.isOnline() ? playerData.getPlayer().getName() : "Unknown Player";

      for (String key : section.getKeys(false)) {
         if (!MMOItems.plugin.getCrafting().hasStation(key)) {
            MMOItems.plugin
               .getLogger()
               .log(
                  Level.SEVERE,
                  "An error occurred while trying to load crafting station recipe data of '"
                     + text
                     + "': could not find crafting station with ID '"
                     + key
                     + "', make sure you backup that player data file before the user logs off."
               );
         } else {
            CraftingStation station = MMOItems.plugin.getCrafting().getStation(key);
            CraftingStatus.CraftingQueue craftingQueue = new CraftingStatus.CraftingQueue(station);
            this.queues.put(station, craftingQueue);
            Optional first = section.getConfigurationSection(key).getKeys(false).stream().findFirst();
            boolean flag = first.isPresent() && section.contains(key + "." + (String)first.get() + ".delay");

            for (String entryKey : section.getConfigurationSection(key).getKeys(false)) {
               String recipeId = section.getString(key + "." + entryKey + ".recipe");
               if (recipeId != null && station.hasRecipe(recipeId)) {
                  Recipe recipe = station.getRecipe(recipeId);
                  if (!(recipe instanceof CraftingRecipe)) {
                     MMOItems.plugin
                        .getLogger()
                        .log(
                           Level.SEVERE,
                           "An error occurred while trying to load crafting station recipe data of '"
                              + text
                              + "': recipe '"
                              + recipe.getId()
                              + "' is not a CRAFTING recipe."
                        );
                  } else if (flag) {
                     long value = section.getLong(key + "." + entryKey + ".started");
                     long delay = section.getLong(key + "." + entryKey + ".delay");
                     craftingQueue.add((CraftingRecipe)recipe, value, value + delay);
                  } else {
                     craftingQueue.add((CraftingRecipe)recipe, section.getLong(key + "." + entryKey + ".start"), section.getLong(key + "." + entryKey + ".completion"));
                  }
               } else {
                  MMOItems.plugin
                     .getLogger()
                     .log(
                        Level.SEVERE,
                        "An error occurred while trying to load crafting station recipe data of '"
                           + text
                           + "': could not find recipe with ID '"
                           + recipeId
                           + "', make sure you backup that player data file before the user logs off."
                     );
               }
            }
         }
      }
   }

   public void save(ConfigurationSection section) {
      this.queues.forEach((var1x, craftingQueue) -> {
         for (CraftingStatus.CraftingQueue.QueueItem craft : craftingQueue.getCrafts()) {
            section.set(var1x.getId() + ".recipe-" + craft.getUniqueId().toString() + ".recipe", craft.getRecipe().getId());
            section.set(var1x.getId() + ".recipe-" + craft.getUniqueId().toString() + ".start", craft.start);
            section.set(var1x.getId() + ".recipe-" + craft.getUniqueId().toString() + ".completion", craft.completion);
         }
      });
   }

   public CraftingStatus.CraftingQueue getQueue(@NotNull CraftingStation craftingStation) {
      return this.queues.computeIfAbsent(craftingStation, CraftingStatus.CraftingQueue::new);
   }

   public static class CraftingQueue {
      private final String station;
      private final List<CraftingStatus.CraftingQueue.QueueItem> crafts = new ArrayList<>();

      public CraftingQueue(CraftingStation craftingStation) {
         this.station = craftingStation.getId();
      }

      public List<CraftingStatus.CraftingQueue.QueueItem> getCrafts() {
         return this.crafts;
      }

      public boolean isFull(CraftingStation craftingStation) {
         return this.crafts.size() >= craftingStation.getMaxQueueSize();
      }

      public void remove(CraftingStatus.CraftingQueue.QueueItem queueItem) {
         int count = this.crafts.indexOf(queueItem);
         Validate.isTrue(count >= 0, "Could not find item in queue");
         this.crafts.remove(count);
         long value = Math.min(queueItem.getLeft(), (long)(1000.0 * queueItem.getRecipe().getCraftingTime()));

         for (int i = count; i < this.crafts.size(); i++) {
            this.crafts.get(i).removeDelay(value);
         }
      }

      @Nullable
      public CraftingStatus.CraftingQueue.QueueItem getCraft(UUID uuid) {
         for (CraftingStatus.CraftingQueue.QueueItem craft : this.crafts) {
            if (craft.getUniqueId().equals(uuid)) {
               return craft;
            }
         }

         return null;
      }

      public void add(CraftingRecipe craftingRecipe) {
         long value = (long)craftingRecipe.getCraftingTime() * 1000L
            + (this.crafts.isEmpty() ? System.currentTimeMillis() : this.crafts.get(this.crafts.size() - 1).completion);
         this.add(craftingRecipe, System.currentTimeMillis(), value);
      }

      private void add(CraftingRecipe craftingRecipe, long value, long value2) {
         this.crafts.add(new CraftingStatus.CraftingQueue.QueueItem(craftingRecipe, value, value2));
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

         public void removeDelay(long value) {
            this.completion -= value;
         }

         public long getElapsed() {
            return Math.max((long)this.getRecipe().getCraftingTime() * 1000L, System.currentTimeMillis() - this.start);
         }

         public long getLeft() {
            return Math.max(0L, this.completion - System.currentTimeMillis());
         }

         @Override
         public boolean equals(Object obj) {
            if (this == obj) {
               return true;
            } else if (obj != null && this.getClass() == obj.getClass()) {
               CraftingStatus.CraftingQueue.QueueItem queueItem = (CraftingStatus.CraftingQueue.QueueItem)obj;
               return Objects.equals(this.uuid, queueItem.uuid);
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
