package io.lumine.mythic.core.logging;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.utils.logging.ConsoleColor;
import io.lumine.mythic.core.skills.SkillCondition;
import java.util.function.Consumer;
import java.util.logging.Level;

public class MythicDebug {
   private static MythicDebug debugger = null;

   public static boolean toggleDebugging() {
      if (debugger == null) {
         debugger = new MythicDebug();
         return true;
      } else {
         debugger = null;
         return false;
      }
   }

   public static void debug(SkillCondition condition, Consumer<MythicDebug> action) {
      if (debugger != null) {
         action.accept(debugger);
      }
   }

   protected boolean filter(Object holder, SkillMetadata skillMetadata, AbstractEntity target) {
      return true;
   }

   public void log(String message, Object... params) {
      MythicLogger.log(Level.INFO, ConsoleColor.PURPLE + "[DEBUG] " + ConsoleColor.GREEN + message, params);
   }

   public void detail(String s, String s1) {
   }
}
