package io.lumine.mythic.core.mobs;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.LinkedList;
import java.util.Queue;

public class MobStack implements Comparable<MobStack> {
   private String internalName;
   private String stackString;
   private String file;
   private MythicMob base;
   private Queue<MythicMob> stack = new LinkedList<>();
   private boolean enabled = true;

   public MobStack(String name, String stackString, String file) {
      this.internalName = name;
      this.stackString = stackString;
      this.file = file;
      String[] part = this.stackString.split(",");
      MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(part[0]).orElseGet(() -> null);
      if (mm == null) {
         MythicLogger.error("Could not load MythicMobStack {0}! Invalid type(s) specified.", this.internalName);
         this.stack.clear();
         this.enabled = false;
      } else {
         this.base = mm;

         for (int i = 1; i < part.length; i++) {
            mm = MythicBukkit.inst().getMobManager().getMythicMob(part[i]).orElseGet(() -> null);
            if (mm == null) {
               MythicLogger.error("Could not load MythicMobStack {0}! Invalid type(s) specified.", this.internalName);
               this.stack.clear();
               this.enabled = false;
               return;
            }

            this.stack.add(mm);
         }
      }
   }

   public boolean isValid() {
      return this.enabled;
   }

   public String getName() {
      return this.internalName;
   }

   public String getFile() {
      return this.file;
   }

   public String getStackString() {
      return this.stackString;
   }

   public ActiveMob spawn(AbstractLocation l, double level, SpawnReason reason, MythicSpawner spawner) {
      if (!this.enabled) {
         return null;
      } else {
         ActiveMob base = this.base.spawn(l, level, reason);
         AbstractEntity f = base.getEntity();

         for (MythicMob mm : this.stack) {
            AbstractEntity e = mm.spawn(l, level, SpawnReason.SUMMON, null, spawner).getEntity();
            f.setPassenger(e);
            f = e;
         }

         return base;
      }
   }

   public int compareTo(MobStack o) {
      return this.internalName.compareTo(o.getName());
   }
}
