package io.lumine.mythic.bukkit.clock;

import java.util.HashMap;

public class TimingsHandler {
   private boolean enabled = false;
   private long startTime;
   private long total;
   private long lastMark;
   private long scannerTotal;
   private long scannerLastMark;
   private long skillsTotal;
   private long skillsLastMark;
   private long spawnersTotal;
   private long spawnersLastMark;
   private long randomSpawnersTotal;
   private long randomSpawnersLastMark;
   private long randomGeneratorsTotal;
   private long randomGeneratorsLastMark;
   private long threattablesTotal;
   private long threattablesLastMark;
   private HashMap<String, Long> skillMarks = new HashMap<>(64);
   private HashMap<String, Long> skillTimings = new HashMap<>(64);
   private HashMap<String, Long> spawnerMarks = new HashMap<>(64);
   private HashMap<String, Long> spawnerTimings = new HashMap<>(64);
   private final Object spawnerTimingLock = new Object();

   public void enable() {
      this.startTime = System.nanoTime();
      this.enabled = true;
   }

   public void disable() {
      this.enabled = false;
   }

   public void reset() {
      this.startTime = System.nanoTime();
      this.total = 0L;
      this.lastMark = 0L;
      this.scannerTotal = 0L;
      this.scannerLastMark = 0L;
      this.skillsTotal = 0L;
      this.skillsLastMark = 0L;
      this.spawnersTotal = 0L;
      this.spawnersLastMark = 0L;
      this.randomSpawnersTotal = 0L;
      this.randomSpawnersLastMark = 0L;
      this.randomGeneratorsTotal = 0L;
      this.randomGeneratorsLastMark = 0L;
      this.threattablesTotal = 0L;
      this.threattablesLastMark = 0L;
      synchronized (this.spawnerTimingLock) {
         this.spawnerMarks.clear();
         this.spawnerTimings.clear();
      }

      this.skillMarks.clear();
      this.skillTimings.clear();
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void markTotalNew() {
      if (this.enabled) {
         this.lastMark = System.nanoTime();
      }
   }

   public void markTotalComplete() {
      if (this.enabled) {
         if (this.lastMark != 0L) {
            this.total = this.total + (System.nanoTime() - this.lastMark);
         }
      }
   }

   public long getStartTime() {
      return this.startTime;
   }

   public long getRunTime() {
      return System.nanoTime() - this.startTime;
   }

   public double getRunTimeMillis() {
      return Math.round((float)((System.nanoTime() - this.startTime) / 10000L)) / 100;
   }

   public long getTimeTotal() {
      return this.total;
   }

   public void markScannerNew() {
      if (this.enabled) {
         this.scannerLastMark = System.nanoTime();
      }
   }

   public void markScannerComplete() {
      if (this.enabled) {
         if (this.scannerLastMark != 0L) {
            this.scannerTotal = this.scannerTotal + (System.nanoTime() - this.scannerLastMark);
         }
      }
   }

   public long getTimeScanner() {
      return this.scannerTotal;
   }

   public void markSkillsNew() {
      if (this.enabled) {
         this.skillsLastMark = System.nanoTime();
      }
   }

   public void markSkillsComplete() {
      if (this.enabled) {
         if (this.skillsLastMark != 0L) {
            this.skillsTotal = this.skillsTotal + (System.nanoTime() - this.skillsLastMark);
         }
      }
   }

   public long getTimeSkills() {
      return this.skillsTotal;
   }

   public void markRandomSpawnersNew() {
      if (this.enabled) {
         this.randomSpawnersLastMark = System.nanoTime();
      }
   }

   public void markRandomSpawnersComplete() {
      if (this.enabled) {
         if (this.randomSpawnersLastMark != 0L) {
            this.randomSpawnersTotal = this.randomSpawnersTotal + (System.nanoTime() - this.randomSpawnersLastMark);
         }
      }
   }

   public long getTimeRandomSpawners() {
      return this.randomSpawnersTotal;
   }

   public void markRandomGeneratorsNew() {
      if (this.enabled) {
         this.randomGeneratorsLastMark = System.nanoTime();
      }
   }

   public void markRandomGeneratorsComplete() {
      if (this.enabled) {
         if (this.randomGeneratorsLastMark != 0L) {
            this.randomGeneratorsTotal = this.randomGeneratorsTotal + (System.nanoTime() - this.randomGeneratorsLastMark);
         }
      }
   }

   public long getTimeRandomGenerators() {
      return this.randomGeneratorsTotal;
   }

   public void markSpawnersNew() {
      if (this.enabled) {
         this.spawnersLastMark = System.nanoTime();
      }
   }

   public void markSpawnersComplete() {
      if (this.enabled) {
         if (this.spawnersLastMark != 0L) {
            this.spawnersTotal = this.spawnersTotal + (System.nanoTime() - this.spawnersLastMark);
         }
      }
   }

   public long getTimeSpawners() {
      return this.spawnersTotal;
   }

   public void markThreatTablesNew() {
      if (this.enabled) {
         this.threattablesLastMark = System.nanoTime();
      }
   }

   public void markThreatTablesComplete() {
      if (this.enabled) {
         if (this.threattablesLastMark != 0L) {
            this.threattablesTotal = this.threattablesTotal + (System.nanoTime() - this.threattablesLastMark);
         }
      }
   }

   public long getTimeThreatTables() {
      return this.threattablesTotal;
   }

   public void markSpawnerNew(String s) {
      if (this.enabled) {
         synchronized (this.spawnerTimingLock) {
            this.spawnerMarks.put(s, System.nanoTime());
         }
      }
   }

   public void markSpawnerComplete(String s) {
      if (this.enabled) {
         synchronized (this.spawnerTimingLock) {
            long l = 0L;
            if (this.spawnerMarks.containsKey(s)) {
               l = this.spawnerMarks.get(s);
               if (this.spawnerTimings.containsKey(s)) {
                  long ct = this.spawnerTimings.get(s);
                  this.spawnerTimings.put(s, ct + (System.nanoTime() - l));
               } else {
                  this.spawnerTimings.put(s, System.nanoTime() - l);
               }
            }
         }
      }
   }

   public HashMap<String, Long> getAllSpawnerTimes() {
      synchronized (this.spawnerTimingLock) {
         return new HashMap<>(this.spawnerTimings);
      }
   }

   public void markSkillNew(String s) {
      if (this.enabled) {
         this.skillMarks.put(s, System.nanoTime());
      }
   }

   public void markSkillComplete(String s) {
      if (this.enabled) {
         long l = 0L;
         if (this.skillMarks.containsKey(s)) {
            l = this.skillMarks.get(s);
            if (this.skillTimings.containsKey(s)) {
               long ct = this.skillTimings.get(s);
               this.skillTimings.put(s, ct + (System.nanoTime() - l));
            } else {
               this.skillTimings.put(s, System.nanoTime() - l);
            }
         }
      }
   }

   public HashMap<String, Long> getAllSkillTimes() {
      return this.skillTimings;
   }
}
