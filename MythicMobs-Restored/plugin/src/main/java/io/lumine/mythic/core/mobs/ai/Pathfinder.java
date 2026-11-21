package io.lumine.mythic.core.mobs.ai;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.volatilecode.handlers.VolatileAIHandler;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.Getter;
import lombok.Setter;

public abstract class Pathfinder implements PathfinderAdapter {
   @Getter
   protected ActiveMob activeMob;
   @Getter
   protected AbstractEntity entity;
   @Getter
   private int index = -1;
   protected String dataVar1 = null;
   protected String dataVar2 = null;
   @Getter @Setter
   protected Pathfinder.GoalType goalType = Pathfinder.GoalType.NONE;

   protected static MythicBukkit getPlugin() {
      return MythicBukkit.inst();
   }

   protected static VolatileAIHandler ai() {
      return MythicBukkit.inst().getVolatileCodeHandler().getAIHandler();
   }

   public Pathfinder(AbstractEntity entity, String line, MythicLineConfig mlc) {
      this.entity = entity;
      this.activeMob = getPlugin().getMobManager().getMythicMobInstance(this.entity);
      String[] spLine = line.split(" ");
      if (spLine[0].matches("[0-9]*")) {
         this.index = Integer.parseInt(spLine[0]);
         String goal = spLine[1];
         if (spLine.length > 2) {
            this.dataVar1 = spLine[2];
         } else {
            this.dataVar1 = null;
         }

         if (spLine.length > 3) {
            this.dataVar2 = spLine[3];
         } else {
            this.dataVar2 = null;
         }
      } else {
         this.index = -1;
         String goalx = spLine[0];
         if (spLine.length > 1) {
            this.dataVar1 = spLine[1];
         } else {
            this.dataVar1 = null;
         }

         if (spLine.length > 2) {
            this.dataVar2 = spLine[2];
         } else {
            this.dataVar2 = null;
         }
      }
   }

   public Pathfinder(AbstractEntity entity, int index, String line, MythicLineConfig mlc) {
      this.entity = entity;
      this.index = index;
   }

   @Override
   public boolean isValid() {
      return true;
   }

   public abstract boolean shouldStart();

   public abstract void start();

   public abstract void tick();

   public abstract boolean shouldEnd();

   public abstract void end();

   public static enum GoalType {
      MOVE,
      MOVE_LOOK,
      TARGET,
      NONE;
   }
}
