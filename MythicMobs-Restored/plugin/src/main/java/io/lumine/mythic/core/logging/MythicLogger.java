package io.lumine.mythic.core.logging;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.logging.ConsoleColor;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobType;
import io.lumine.mythic.core.skills.MetaSkill;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.spawning.random.RandomSpawner;
import java.io.File;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.logging.Level;
import org.bukkit.Bukkit;

public class MythicLogger {
   public static void log(String message) {
      MythicBukkit.inst().getLogger().log(Level.INFO, message + ConsoleColor.RESET);
   }

   public static void log(String message, Object... params) {
      MythicBukkit.inst().getLogger().log(Level.INFO, message + ConsoleColor.RESET, params);
   }

   public static void log(Level level, String message) {
      MythicBukkit.inst().getLogger().log(level, message + ConsoleColor.RESET);
   }

   public static void log(Level level, String message, Object... params) {
      MythicBukkit.inst().getLogger().log(level, message + ConsoleColor.RESET, params);
   }

   public static void error(String message) {
      log(Level.WARNING, message + ConsoleColor.RESET);
   }

   public static void error(SkillCaster caster, String message) {
      String source;
      if (caster instanceof ActiveMob am) {
         source = ConsoleColor.PURPLE + "[" + ConsoleColor.YELLOW + "MobType=" + am.getType().getInternalName() + ConsoleColor.PURPLE + "]";
      } else {
         source = ConsoleColor.PURPLE + "[" + ConsoleColor.YELLOW + "Caster=" + caster.getName() + ConsoleColor.PURPLE + "]";
      }

      log(Level.WARNING, source + " " + message + ConsoleColor.RESET);
   }

   public static void error(String message, Object... params) {
      log(Level.WARNING, message + ConsoleColor.RESET, params);
   }

   public static void error(SkillCaster caster, String message, Object... params) {
      String source;
      if (caster instanceof ActiveMob am) {
         source = ConsoleColor.PURPLE + "[" + ConsoleColor.YELLOW + "MobType=" + am.getType().getInternalName() + ConsoleColor.PURPLE + "]";
      } else {
         source = ConsoleColor.PURPLE + "[" + ConsoleColor.YELLOW + "Caster=" + caster.getName() + ConsoleColor.PURPLE + "]";
      }

      log(Level.WARNING, source + " " + message + ConsoleColor.RESET, params);
   }

   public static void error(String message, Throwable ex) {
      log(Level.SEVERE, message + ConsoleColor.RESET);
      log(Level.SEVERE, "    " + ex.getMessage() + " (" + ex.getClass().getName() + ")");
      if (MythicBukkit.inst().getConfiguration().getDebugLevel() > 0) {
         ex.printStackTrace();
      }

      if (MythicBukkit.inst().getConfiguration().getDoErrorLogging()) {
         try {
            File logsFolder = new File(MythicBukkit.inst().getDataFolder(), "logs");
            if (!logsFolder.exists()) {
               logsFolder.mkdir();
            }

            File folder = new File(logsFolder, "errors");
            if (!folder.exists()) {
               folder.mkdir();
            }

            File outputFile = new File(folder, System.currentTimeMillis() + ".txt");

            try (PrintWriter writer = new PrintWriter(outputFile)) {
               for (Throwable t = ex; t != null; t = t.getCause()) {
                  t.printStackTrace(writer);
                  writer.println();
               }

               writer.println("Mythic version: " + MythicBukkit.inst().getDescription().getVersion());
               writer.println("Bukkit version: " + Bukkit.getServer().getVersion());
            }

            error("This error has been saved in the logs/errors folder. Please report it on the MythicCraft Issue Tracker or discord.");
            error("Error file: " + outputFile.getName());
         } catch (Exception e) {
            error("ERROR HANDLING EXCEPTION");
            e.printStackTrace();
            ex.printStackTrace();
         }
      } else {
         ex.printStackTrace();
      }
   }

   public static void error(String message, Throwable ex, Object... params) {
      log(Level.SEVERE, message + ConsoleColor.RESET, params);
      log(Level.SEVERE, "    " + ex.getMessage() + " (" + ex.getClass().getName() + ")");
      if (MythicBukkit.inst().getConfiguration().getDebugLevel() > 0) {
         ex.printStackTrace();
      }

      if (MythicBukkit.inst().getConfiguration().getDoErrorLogging()) {
         try {
            File logsFolder = new File(MythicBukkit.inst().getDataFolder(), "logs");
            if (!logsFolder.exists()) {
               logsFolder.mkdir();
            }

            File folder = new File(logsFolder, "errors");
            if (!folder.exists()) {
               folder.mkdir();
            }

            File outputFile = new File(folder, System.currentTimeMillis() + ".txt");

            try (PrintWriter writer = new PrintWriter(outputFile)) {
               for (Throwable t = ex; t != null; t = t.getCause()) {
                  t.printStackTrace(writer);
                  writer.println();
               }

               writer.println("Mythic version: " + MythicBukkit.inst().getDescription().getVersion());
               writer.println("Bukkit version: " + Bukkit.getServer().getVersion());
            }

            error("This error has been saved in the Error Logs folder. Please report it on the MythicCraft Issue Tracker or discord.");
         } catch (Exception e) {
            error("ERROR HANDLING EXCEPTION");
            e.printStackTrace();
            ex.printStackTrace();
         }
      } else {
         ex.printStackTrace();
      }
   }

   public static void handleMinorError(Throwable ex) {
      if (MythicBukkit.inst().getConfiguration().getDebugLevel() > 0) {
         ex.printStackTrace();
      }
   }

   public static void errorYMLConfig(File file, String error) {
      error(
         "" + ConsoleColor.ERROR_MARK + ConsoleColor.ERROR_MARK + ConsoleColor.ERROR_MARK + ConsoleColor.RED + ConsoleColor.BOLD + " Failed to load YML File"
      );
      error("--| " + ConsoleColor.RED + "File: " + ConsoleColor.WHITE + file.getAbsolutePath());
      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
   }

   public static void errorGenericConfig(String error) {
      error("" + ConsoleColor.ERROR_MARK + ConsoleColor.RED + " Configuration Error: " + ConsoleColor.RESET + error);
   }

   public static void errorGenericConfig(MythicConfig config, String error) {
      error("" + ConsoleColor.ERROR_MARK + ConsoleColor.RED + ConsoleColor.BOLD + " Configuration Error");
      error("--| " + ConsoleColor.RED + "File: " + ConsoleColor.WHITE + config.getFile().getAbsolutePath());
      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
   }

   public static void errorGenericConfig(MythicLineConfig config, String error) {
      error("" + ConsoleColor.ERROR_MARK + ConsoleColor.RED + ConsoleColor.BOLD + " Configuration Error");
      error("--| " + ConsoleColor.RED + "File: " + ConsoleColor.WHITE + config.getFileName());
      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
      error("--| " + ConsoleColor.PURPLE + "Line: " + ConsoleColor.RESET + config.getLine());
   }

   public static void errorMobConfig(MythicMob mob, MythicConfig config, String error) {
      error("" + ConsoleColor.ERROR_MARK + ConsoleColor.RED + ConsoleColor.BOLD + " Configuration Error in Mob " + ConsoleColor.CYAN + mob.getInternalName());
      error("--| " + ConsoleColor.RED + "File: " + ConsoleColor.WHITE + config.getFile().getAbsolutePath());
      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
   }

   public static void errorEntityConfig(BukkitEntityType entity, MythicConfig config, String error) {
      error("" + ConsoleColor.ERROR_MARK + ConsoleColor.RED + ConsoleColor.BOLD + " Entity Configuration Error in Mob " + ConsoleColor.CYAN + config.getKey());
      error("--| " + ConsoleColor.RED + "File: " + ConsoleColor.WHITE + config.getFile().getAbsolutePath());
      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
   }

   public static void errorMechanic(SkillMechanic mechanic, String error) {
      errorMechanic(mechanic, error, null);
   }

   public static void errorMechanic(SkillMechanic mechanic, String error, Throwable ex) {
      Optional<SkillHolder> maybeParent = mechanic.getParent();
      MythicLineConfig config = mechanic.getConfig();
      String parentString;
      if (maybeParent.isEmpty()) {
         parentString = "";
      } else if (maybeParent.get() instanceof MobType mobType) {
         parentString = "Mob: " + ConsoleColor.WHITE + mobType.getInternalName() + ConsoleColor.CYAN + " | " + ConsoleColor.RED;
      } else {
         parentString = "Skill: " + ConsoleColor.WHITE + maybeParent.get().getInternalName() + ConsoleColor.CYAN + " | " + ConsoleColor.RED;
      }

      error(
         "--| "
            + ConsoleColor.ERROR_MARK
            + ConsoleColor.RED
            + ConsoleColor.BOLD
            + " An Error Occurred in Mechanic "
            + ConsoleColor.CYAN
            + mechanic.getTypeName()
      );
      error("--| " + ConsoleColor.RED + parentString + "File: " + ConsoleColor.WHITE + mechanic.getFilePath());
      error("--| " + ConsoleColor.PURPLE + "Mechanic: " + ConsoleColor.RESET + config.getLine());
      error("--| " + ConsoleColor.PURPLE + "Error: " + ConsoleColor.RESET + error);
      if (ex != null) {
         ex.printStackTrace();
      }
   }

   public static void errorMechanicConfig(SkillMechanic mechanic, MythicLineConfig config, String error) {
      Optional<SkillHolder> maybeParent = mechanic.getParent();
      String parentString;
      if (maybeParent.isEmpty()) {
         parentString = "";
      } else if (maybeParent.get() instanceof MobType mobType) {
         parentString = "Mob: " + ConsoleColor.CYAN + mobType.getInternalName() + ConsoleColor.WHITE + " | " + ConsoleColor.RED;
      } else {
         parentString = "Skill: " + ConsoleColor.CYAN + maybeParent.get().getInternalName() + ConsoleColor.WHITE + " | " + ConsoleColor.RED;
      }

      error(
         "" + ConsoleColor.ERROR_MARK + ConsoleColor.RED + ConsoleColor.BOLD + " Configuration Error in Mechanic " + ConsoleColor.CYAN + mechanic.getTypeName()
      );
      error("--| " + ConsoleColor.RED + parentString + "File: " + ConsoleColor.WHITE + mechanic.getFilePath());
      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
      error("--| " + ConsoleColor.PURPLE + "Mechanic Line: " + ConsoleColor.RESET + config.getLine());
   }

   public static void errorCondition(SkillCondition condition, String error) {
      errorCondition(condition, error, null);
   }

   public static void errorCondition(SkillCondition condition, String error, Throwable ex) {
      Optional<SkillHolder> maybeParent = condition.getParent();
      String parentString;
      String fileString;
      if (maybeParent.isEmpty()) {
         parentString = "";
         fileString = "";
      } else if (maybeParent.get() instanceof SkillMechanic mechanic) {
         parentString = "Mechanic: " + ConsoleColor.CYAN + mechanic.getInternalName() + ConsoleColor.WHITE + " | " + ConsoleColor.RED;
         fileString = mechanic.getConfig().getFileName();
      } else if (maybeParent.get() instanceof MetaSkill metaSkill) {
         parentString = "Mechanic: " + ConsoleColor.CYAN + metaSkill.getInternalName() + ConsoleColor.WHITE + " | " + ConsoleColor.RED;
         fileString = metaSkill.getFile().getAbsolutePath();
      } else {
         parentString = "Skill: " + ConsoleColor.CYAN + maybeParent.get().getInternalName() + ConsoleColor.WHITE + " | " + ConsoleColor.RED;
         fileString = "";
      }

      error(
         ""
            + ConsoleColor.ERROR_MARK
            + ConsoleColor.RED
            + ConsoleColor.BOLD
            + " Configuration Error in Condition "
            + ConsoleColor.CYAN
            + condition.getTypeName()
      );
      if (!parentString.isEmpty() || !fileString.isEmpty()) {
         error("--| " + ConsoleColor.RED + parentString + "File: " + ConsoleColor.WHITE + fileString);
      }

      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
      error("--| " + ConsoleColor.PURPLE + "Condition Line: " + ConsoleColor.RESET + condition.getLine());
      if (ex != null) {
         ex.printStackTrace();
      }
   }

   public static void errorConditionConfig(SkillCondition condition, MythicLineConfig config, String error) {
      Optional<SkillHolder> maybeParent = condition.getParent();
      String parentString;
      if (maybeParent.isEmpty()) {
         parentString = "";
      } else if (maybeParent.get() instanceof SkillMechanic mechanic) {
         parentString = "Mechanic: " + ConsoleColor.CYAN + mechanic.getInternalName() + ConsoleColor.WHITE + " | " + ConsoleColor.RED;
      } else {
         parentString = "Skill: " + ConsoleColor.CYAN + maybeParent.get().getInternalName() + ConsoleColor.WHITE + " | " + ConsoleColor.RED;
      }

      error(
         ""
            + ConsoleColor.ERROR_MARK
            + ConsoleColor.RED
            + ConsoleColor.BOLD
            + " Configuration Error in Condition "
            + ConsoleColor.CYAN
            + condition.getTypeName()
      );
      error("--| " + ConsoleColor.RED + parentString + "File: " + ConsoleColor.WHITE + config.getFileName());
      error("--| " + ConsoleColor.YELLOW + "Error Message: " + ConsoleColor.RESET + error);
      error("--| " + ConsoleColor.YELLOW + "Mechanic Line: " + ConsoleColor.RESET + config.getLine());
   }

   public static void errorItemConfig(MythicItem item, MythicConfig config, String error) {
      error("" + ConsoleColor.ERROR_MARK + ConsoleColor.RED + ConsoleColor.BOLD + " Configuration Error in Item " + ConsoleColor.CYAN + config.getKey());
      error("--| " + ConsoleColor.RED + "File: " + ConsoleColor.WHITE + config.getFileName());
      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
   }

   public static void errorDropConfig(Drop drop, MythicLineConfig config, String error) {
      error("" + ConsoleColor.ERROR_MARK + ConsoleColor.RED + ConsoleColor.BOLD + " Configuration Error in Drop " + ConsoleColor.CYAN + drop.getLine());
      error("--| " + ConsoleColor.RED + "File: " + ConsoleColor.WHITE + config.getFileName());
      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
   }

   public static void errorDropTableConfig(DropTable drop, MythicLineConfig config, String error) {
      error(
         ""
            + ConsoleColor.ERROR_MARK
            + ConsoleColor.RED
            + ConsoleColor.BOLD
            + " Configuration Error in DropTable "
            + ConsoleColor.CYAN
            + drop.getInternalName()
      );
      error("--| " + ConsoleColor.RED + "File: " + ConsoleColor.WHITE + config.getFileName());
      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
   }

   public static void errorTargeterConfig(SkillTargeter targeter, MythicLineConfig config, String error) {
      error(
         ""
            + ConsoleColor.ERROR_MARK
            + ConsoleColor.YELLOW
            + " Config Error for Targeter line '"
            + ConsoleColor.UNDERLINE
            + config.getLine()
            + ConsoleColor.RESET
            + ConsoleColor.YELLOW
            + "': "
            + ConsoleColor.WHITE
            + error
      );
   }

   public static void errorTargeterConfig(SkillTargeter targeter, SkillCaster caster, MythicLineConfig config, String error) {
      error(
         caster,
         ""
            + ConsoleColor.ERROR_MARK
            + ConsoleColor.YELLOW
            + " Config Error for Targeter line '"
            + ConsoleColor.UNDERLINE
            + config.getLine()
            + ConsoleColor.RESET
            + ConsoleColor.YELLOW
            + "': "
            + ConsoleColor.WHITE
            + error
      );
   }

   public static void errorSpawnConfig(RandomSpawner spawner, MythicConfig config, String error) {
      String fileName = config.getFile() == null ? "Unknown" : config.getFile().getAbsolutePath();
      error(
         "" + ConsoleColor.ERROR_MARK + ConsoleColor.RED + ConsoleColor.BOLD + " Configuration Error in RandomSpawn " + ConsoleColor.CYAN + spawner.getName()
      );
      error("--| " + ConsoleColor.RED + "File: " + ConsoleColor.WHITE + fileName);
      error("--| " + ConsoleColor.PURPLE + "Error Message: " + ConsoleColor.RESET + error);
   }

   public static void errorCompatibility(String plugin, String error) {
      error(
         ""
            + ConsoleColor.ERROR_MARK
            + ConsoleColor.YELLOW
            + " Compatibility Error for '"
            + ConsoleColor.UNDERLINE
            + plugin
            + ConsoleColor.RESET
            + ConsoleColor.YELLOW
            + "': "
            + ConsoleColor.WHITE
            + error
      );
   }

   public static boolean isDebugEnabled(MythicLogger.DebugLevel level) {
      int debugLevel = MythicBukkit.inst().getConfiguration().getDebugLevel();
      return debugLevel != 0 && debugLevel >= level.ordinal();
   }

   public static void debug(MythicLogger.DebugLevel level, String message, Object... params) {
      if (MythicBukkit.inst().getConfiguration().getDebugLevel() != 0) {
         if (MythicBukkit.inst().getConfiguration().getDebugLevel() >= level.ordinal()) {
            switch (level) {
               case INFO:
                  log(Level.INFO, ConsoleColor.CYAN + "[i] " + message, params);
                  return;
               case SKILL:
                  log(Level.INFO, ConsoleColor.PURPLE + "+--- " + ConsoleColor.CYAN + message, params);
                  return;
               case SKILL_CHECK:
                  log(Level.INFO, ConsoleColor.PURPLE + "|----- " + ConsoleColor.CYAN + message, params);
                  return;
               case SKILL_INFO:
                  log(Level.INFO, ConsoleColor.PURPLE + "|------- " + ConsoleColor.CYAN + message, params);
                  return;
               case MECHANIC:
                  log(Level.INFO, ConsoleColor.PURPLE + "~----------- " + ConsoleColor.CYAN + message, params);
                  return;
               case CONDITION:
                  log(Level.INFO, ConsoleColor.PURPLE + "|--------? " + ConsoleColor.CYAN + message, params);
                  return;
               case TRIGGER:
                  log(Level.INFO, ConsoleColor.PURPLE + "#- " + ConsoleColor.CYAN + message, params);
                  return;
               case EVENT:
                  log(Level.INFO, ConsoleColor.CYAN + "[e] " + message, params);
                  return;
               case CLOCK:
                  log(Level.INFO, ConsoleColor.CYAN + "[c] " + message, params);
                  return;
            }
         }
      }
   }

   private MythicLogger() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   public static enum DebugLevel {
      NONE,
      INFO,
      SKILL,
      SKILL_CHECK,
      SKILL_INFO,
      MECHANIC,
      CONDITION,
      TRIGGER,
      EVENT,
      CLOCK;
   }
}
