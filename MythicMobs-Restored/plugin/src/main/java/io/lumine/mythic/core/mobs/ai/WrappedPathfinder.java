package io.lumine.mythic.core.mobs.ai;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.volatilecode.handlers.VolatileAIHandler;
import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.Getter;

public abstract class WrappedPathfinder {
   @Getter
   protected AbstractEntity entity;
   @Getter
   private int index = -1;
   protected String dataVar1 = null;
   protected String dataVar2 = null;

   protected static MythicBukkit getPlugin() {
      return MythicBukkit.inst();
   }

   protected static VolatileAIHandler ai() {
      return MythicBukkit.inst().getVolatileCodeHandler().getAIHandler();
   }

   public WrappedPathfinder(AbstractEntity entity, String line, MythicLineConfig mlc) {
      this.entity = entity;
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
}
