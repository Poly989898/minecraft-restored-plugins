package io.lumine.mythic.bukkit.commands.mobs;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.spawning.MythicSpawnState;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;

public class StatsCommand extends Command<MythicBukkit> {
   public StatsCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      int amount = 0;
      int alive = 0;
      int dead = 0;
      int valid = 0;
      int invalid = 0;
      int n = 0;
      Player bukkitPlayer = (Player)sender;
      World bukkitWorld = bukkitPlayer.getWorld();
      AbstractPlayer player = BukkitAdapter.adapt(bukkitPlayer);

      for (ActiveMob am : this.getPlugin().getMobManager().getActiveMobs()) {
         if (am.getLocation().getWorld().equals(player.getWorld())) {
            amount++;
            if (am.getEntity() == null) {
               n++;
            } else {
               if (am.getEntity().isDead()) {
                  dead++;
               } else {
                  alive++;
               }

               if (am.getEntity().isValid()) {
                  valid++;
               } else {
                  invalid++;
               }
            }
         }
      }

      MythicSpawnState spawnState = this.getPlugin().getVolatileCodeHandler().getSpawningHandler().getSpawnState(player.getLocation().getWorld());
      Map<SpawnCategory, StatsCommand.MobStats> categoryInfo = Maps.newConcurrentMap();

      for (Entity entity : bukkitWorld.getEntities()) {
         SpawnCategory category = entity.getSpawnCategory();
         if (!categoryInfo.containsKey(category)) {
            categoryInfo.put(category, new StatsCommand.MobStats(category));
         }

         categoryInfo.get(category).add(entity);
      }

      CommandHelper.sendHeader(sender);
      CommandHelper.send(sender, "<gold>Total Active Mobs<gray>: <white>" + amount);
      CommandHelper.send(sender, "<yellow>Alive<gray>: <white>" + alive + " <gray>(Valid: " + valid + "<gray>)");
      if (dead > 0) {
         CommandHelper.send(sender, "<yellow>Dead<gray>: <white>" + dead);
      }

      if (invalid > 0) {
         CommandHelper.send(sender, "<yellow>Invalid<gray>: <white>" + invalid);
      }

      if (n > 0) {
         CommandHelper.send(sender, "<yellow>Null<gray>: <white>" + n);
      }

      CommandHelper.send(sender, "<red> ");
      CommandHelper.send(sender, "<gold>Statistics<gray>: ");

      for (StatsCommand.MobStats cat : categoryInfo.values()) {
         CommandHelper.send(
            sender,
            "<gray>-> <yellow>"
               + cat.getCategory().toString()
               + " <gray>- <white>"
               + cat.getCount()
               + " <gray>/ <white>Mythic: "
               + cat.getMythicCount()
               + " <gray>/ <white>Persistent: "
               + cat.getPersistentCount()
         );
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.mobs.stats";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "stats";
   }

   private class MobStats {
      private final SpawnCategory category;
      private int count = 0;
      private int mythicCount = 0;
      private int persistentCount = 0;

      public void add(Entity entity) {
         this.count++;
         if (StatsCommand.this.getPlugin().getMobManager().isMythicMob(entity)) {
            this.mythicCount++;
         }

         if (entity instanceof LivingEntity le && !le.getRemoveWhenFarAway()) {
            this.persistentCount++;
         }
      }

      @Generated
      public MobStats(final SpawnCategory category) {
         this.category = category;
      }

      @Generated
      public SpawnCategory getCategory() {
         return this.category;
      }

      @Generated
      public int getCount() {
         return this.count;
      }

      @Generated
      public int getMythicCount() {
         return this.mythicCount;
      }

      @Generated
      public int getPersistentCount() {
         return this.persistentCount;
      }

      @Generated
      public void setCount(int count) {
         this.count = count;
      }

      @Generated
      public void setMythicCount(int mythicCount) {
         this.mythicCount = mythicCount;
      }

      @Generated
      public void setPersistentCount(int persistentCount) {
         this.persistentCount = persistentCount;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof StatsCommand.MobStats other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.getCount() != other.getCount()) {
            return false;
         } else if (this.getMythicCount() != other.getMythicCount()) {
            return false;
         } else if (this.getPersistentCount() != other.getPersistentCount()) {
            return false;
         } else {
            Object this$category = this.getCategory();
            Object other$category = other.getCategory();
            return this$category == null ? other$category == null : this$category.equals(other$category);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof StatsCommand.MobStats;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + this.getCount();
         result = result * 59 + this.getMythicCount();
         result = result * 59 + this.getPersistentCount();
         Object $category = this.getCategory();
         return result * 59 + ($category == null ? 43 : $category.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "StatsCommand.MobStats(category="
            + this.getCategory()
            + ", count="
            + this.getCount()
            + ", mythicCount="
            + this.getMythicCount()
            + ", persistentCount="
            + this.getPersistentCount()
            + ")";
      }
   }
}
