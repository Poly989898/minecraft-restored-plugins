package io.lumine.mythic.bukkit.clock;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.ConfigExecutor;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.variables.types.MetaSkillVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;

public class MythicClock extends ReloadableModule<MythicBukkit> {
   private Task secondPassClock;
   private Task syncClock;
   private Task savingClock;
   private Task spawningClock;
   private Task randomSpawningClock;
   private Task skillClock;
   private Task threatClock;
   private Task cleanupClock;
   private int ticker;
   private long skillClockIteration = 0L;
   private List<Runnable> secondPass = Lists.newArrayList();
   private List<Runnable> postReload = Lists.newArrayList();
   private volatile boolean postReloadPending = true;
   private ReentrantLock loadingLock = new ReentrantLock();
   @Getter
   private boolean reloadingPause = false;

   public MythicClock(MythicBukkit plugin) {
      super(plugin, false);
   }

   public void load(MythicBukkit plugin) {
      ConfigExecutor configManager = plugin.getConfiguration();
      this.secondPassClock = Schedulers.sync().runRepeating(this::runSecondPass, 20L, 20L);
      this.savingClock = Schedulers.async().runRepeating(this::runSavingClock, 0L, (long)(configManager.getClockIntervalSaving() * 20));
      this.spawningClock = Schedulers.sync().runRepeating(this::runSpawningClock, 0L, (long)(configManager.getClockSpawningInterval() * 20));
      this.randomSpawningClock = Schedulers.sync().runRepeating(this::runRandomSpawningClock, 0L, (long)configManager.getClockIntervalRandomSpawning());
      this.cleanupClock = Schedulers.sync().runRepeating(this::runCleanupClock, 0L, (long)configManager.getClockCleaningInterval());
      this.threatClock = Schedulers.sync().runRepeating(this::runThreatClock, 0L, 20L);
      if (ServerVersion.isFolia()) {
         this.skillClock = Schedulers.async().runRepeating(this::runSkillClockFolia, 0L, (long)configManager.getClockIntervalMain());
      } else {
         this.skillClock = Schedulers.async().runRepeating(this::runSkillClock, 0L, (long)configManager.getClockIntervalMain());
      }
   }

   @Override
   public void unload() {
      if (this.secondPassClock != null) {
         this.secondPassClock.terminate();
      }

      if (this.syncClock != null) {
         this.syncClock.terminate();
      }

      if (this.savingClock != null) {
         this.savingClock.terminate();
      }

      if (this.skillClock != null) {
         this.skillClock.terminate();
      }

      if (this.spawningClock != null) {
         this.spawningClock.terminate();
      }

      if (this.randomSpawningClock != null) {
         this.randomSpawningClock.terminate();
      }

      if (this.threatClock != null) {
         this.threatClock.terminate();
      }
   }

   public void setReloadingPause(boolean b) {
      this.loadingLock.lock();

      try {
         this.reloadingPause = b;
      } finally {
         this.loadingLock.unlock();
      }
   }

   public void runSecondPass() {
      this.loadingLock.lock();

      try {
         if (this.reloadingPause || this.secondPass.isEmpty()) {
            return;
         }

         while (!this.secondPass.isEmpty()) {
            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Doing second pass on " + this.secondPass.size() + " skills.");
            List<Runnable> run = this.secondPass;
            this.secondPass = new ArrayList<>();
            run.forEach(r -> {
               if (r != null) {
                  try {
                     r.run();
                  } catch (Error | Exception e) {
                     e.printStackTrace();
                  }
               }
            });
         }
      } finally {
         this.loadingLock.unlock();
      }
   }

   public void queueSecondPass(Runnable r) {
      this.secondPass.add(r);
   }

   public void queueAfterLoad(Runnable r) {
      Schedulers.sync().runLater(r, 5L);
   }

   public void queuePostReload(Runnable r) {
      if (this.postReloadPending) {
         this.postReload.add(r);
      } else {
         try {
            r.run();
         } catch (Error | Exception e) {
            e.printStackTrace();
         }
      }
   }

   public void deferPostReload() {
      this.postReloadPending = true;
   }

   public void runPostReload() {
      while (!this.postReload.isEmpty()) {
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Doing post-reload pass on " + this.postReload.size() + " skills.");
         List<Runnable> run = this.postReload;
         this.postReload = new ArrayList<>();
         run.forEach(r -> {
            if (r != null) {
               try {
                  r.run();
               } catch (Error | Exception e) {
                  e.printStackTrace();
               }
            }
         });
      }

      this.postReloadPending = false;
   }

   public void runSyncClock() {
   }

   public void runSavingClock() {
      this.executeSave();
   }

   public void runSkillClock() {
      if (!this.reloadingPause) {
         MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "Skill clock fired");
         this.skillClockIteration++;
         this.getPlugin().getSkillManager().runTimerSkills(this.skillClockIteration);
      }
   }

   public void runSkillClockFolia() {
      if (!this.reloadingPause) {
         MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "Skill clock fired");
         this.skillClockIteration++;
         this.getPlugin().getSkillManager().runTimerSkillsFolia(this.skillClockIteration);
      }
   }

   public void runSpawningClock() {
      if (!this.reloadingPause) {
         MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "Spawner clock fired");
         if (!this.getPlugin().getConfiguration().isDebugMode()) {
            this.getPlugin().getTimingsHandler().markSpawnersNew();
            this.getPlugin().getSpawnerManager().tickSpawnerClocks();
            this.getPlugin().getTimingsHandler().markSpawnersComplete();
         }
      }
   }

   public void runRandomSpawningClock() {
      if (!this.reloadingPause) {
         MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "Random Spawning clock fired");
         if (!this.getPlugin().getConfiguration().isDebugMode()) {
            this.getPlugin().getTimingsHandler().markRandomSpawnersNew();
            this.getPlugin().getRandomSpawningManager().spawnMobs();
            this.getPlugin().getTimingsHandler().markRandomSpawnersComplete();
         }
      }
   }

   public void runThreatClock() {
      if (!this.reloadingPause) {
         MythicLogger.debug(MythicLogger.DebugLevel.CLOCK, "Threat clock fired");
         this.getPlugin().getTimingsHandler().markThreatTablesNew();
         this.getPlugin().getMobManager().getMobsInCombat().forEach(am -> {
            if (am.getEntity() != null && am.getEntity().getBukkitEntity() != null) {
               Schedulers.ensureSync(am.getEntity().getBukkitEntity(), () -> am.getThreatTable().tickThreat());
            } else {
               am.getThreatTable().tickThreat();
            }
         });
         this.getPlugin().getTimingsHandler().markThreatTablesComplete();
      }
   }

   public void runCleanupClock() {
      MetaSkillVariable.MetaSkillVariableManager.cleanup();
   }

   private void executeSave() {
      this.getPlugin().getConfiguration().doClockSave();
   }
}
