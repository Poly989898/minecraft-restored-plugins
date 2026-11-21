package io.lumine.mythic.bukkit.commands.utility;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.mobs.MobSpawnCategory;
import io.lumine.mythic.api.spawning.MythicSpawnState;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;

public class GetSpawnStateCommand extends Command<MythicBukkit> {
   public GetSpawnStateCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      AbstractPlayer aplayer = BukkitAdapter.adapt(player);
      AbstractLocation location = BukkitAdapter.adapt(player.getLocation());
      CommandHelper.sendHeader(sender);
      int amount = 0;
      int alive = 0;
      int dead = 0;
      int valid = 0;
      int invalid = 0;
      int n = 0;
      Player bukkitPlayer = (Player)sender;
      World bukkitWorld = bukkitPlayer.getWorld();

      for (ActiveMob am : this.getPlugin().getMobManager().getActiveMobs()) {
         if (am.getLocation().getWorld().equals(aplayer.getWorld())) {
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

      Map<SpawnCategory, GetSpawnStateCommand.MobStats> categoryInfo = Maps.newConcurrentMap();

      for (Entity entity : bukkitWorld.getEntities()) {
         SpawnCategory category = entity.getSpawnCategory();
         if (!categoryInfo.containsKey(category)) {
            categoryInfo.put(category, new GetSpawnStateCommand.MobStats(category));
         }

         categoryInfo.get(category).add(entity);
      }

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

      MythicSpawnState state = this.getPlugin().getVolatileCodeHandler().getSpawningHandler().getSpawnState(location.getWorld());
      CommandHelper.send(sender, "<red> ");
      CommandHelper.send(sender, "<gold>SpawnState Information<gray>: ");
      CommandHelper.send(sender, "<yellow>Spawnable Chunks <gray>>> <white>" + state.getSpawnableChunkCount());
      CommandHelper.send(sender, "<yellow>Global Max Mobs <gray>>> <white>" + state.getMaxMobCount());
      CommandHelper.send(sender, "<yellow>Per Player Max Mobs <gray>>> <white>" + state.getMaxMobsPerPlayer());
      CommandHelper.send(sender, "<red> ");
      CommandHelper.send(sender, "<gold>Configuration<gray>: <white>");
      CommandHelper.send(sender, "<yellow>Point Generation Enabled<gray>: <white>" + this.getPlugin().getConfiguration().isRandomSpawningGenerationEnabled());
      CommandHelper.send(sender, "<yellow>Point Generation Interval<gray>: <white>" + this.getPlugin().getConfiguration().getClockIntervalRandomSpawning());
      CommandHelper.send(
         sender,
         "<yellow>Number of Spawners<gray>: <white>"
            + this.getPlugin().getRandomSpawningManager().getNumberOfSpawners()
            + " <gray>(a="
            + this.getPlugin().getRandomSpawningManager().getRandomSpawnersAdd().size()
            + ",r="
            + this.getPlugin().getRandomSpawningManager().getRandomSpawnersReplace().size()
            + ",d="
            + this.getPlugin().getRandomSpawningManager().getRandomSpawnersDeny().size()
            + "<gray>)"
      );
      CommandHelper.send(sender, "<yellow>GameRule MobSpawning: " + player.getWorld().getGameRuleValue(GameRule.DO_MOB_SPAWNING));
      int nx = location.getBlockX();
      int nz = location.getBlockZ();
      int maxHeight = player.getLocation().getChunk().getChunkSnapshot(true, false, false).getHighestBlockYAt(Math.abs(nx % 16), Math.abs(nz % 16)) + 1;
      int minHeight = player.getLocation().getWorld().getMinHeight();
      CommandHelper.send(sender, "<yellow>Spawn Y Range <gray>>> <white>" + minHeight + " <gray>to <white>" + maxHeight);
      CommandHelper.send(sender, "Bukkit Monster Spawn Time " + Bukkit.getTicksPerSpawns(SpawnCategory.MONSTER));
      CommandHelper.send(sender, "Bukkit Monster Spawn Limit " + Bukkit.getSpawnLimit(SpawnCategory.MONSTER));
      Location loc = player.getLocation();
      World world = loc.getWorld();
      CommandHelper.send(sender, "World Monster Spawn Time " + world.getTicksPerSpawns(SpawnCategory.MONSTER));
      CommandHelper.send(sender, "World Monster Spawn Limit " + world.getSpawnLimit(SpawnCategory.MONSTER));
      int limit = world.getSpawnLimit(SpawnCategory.MONSTER);
      int chunks = world.getLoadedChunks().length;
      limit = limit * chunks / 289;
      CommandHelper.send(sender, "Real Monster Spawn Limit " + limit);
      CommandHelper.send(sender, "<red> ");
      CommandHelper.send(sender, "<gold>Bukkit Tracked Entities<gray>: ");

      for (GetSpawnStateCommand.MobStats cat : categoryInfo.values()) {
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

      CommandHelper.send(sender, "<red> ");
      CommandHelper.send(sender, "<gold>NMS Tracked Entities<gray>:");

      for (Entry<MobSpawnCategory, Integer> entry : state.getCurrentMobCounts().entrySet()) {
         try {
            CommandHelper.send(sender, "<gold>-- <yellow>" + entry.getKey().getName() + "<gray>: <white>" + entry.getValue());
         } catch (Error | Exception e) {
            e.printStackTrace();
         }
      }

      CommandHelper.sendFooter(sender);
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.test.spawncheck";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "getspawnstate";
   }

   private class MobStats {
      private final SpawnCategory category;
      private int count = 0;
      private int mythicCount = 0;
      private int persistentCount = 0;

      public void add(Entity entity) {
         this.count++;
         if (GetSpawnStateCommand.this.getPlugin().getMobManager().isMythicMob(entity)) {
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
         } else if (!(o instanceof GetSpawnStateCommand.MobStats other)) {
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
         return other instanceof GetSpawnStateCommand.MobStats;
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
         return "GetSpawnStateCommand.MobStats(category="
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
