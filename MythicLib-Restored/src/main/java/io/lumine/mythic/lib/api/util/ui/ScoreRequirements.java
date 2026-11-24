package io.lumine.mythic.lib.api.util.ui;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScoreRequirements extends QuickNumberRange {
   @NotNull
   String objectiveName;
   @Nullable
   Objective objective;

   @NotNull
   public String getObjectiveName() {
      return this.objectiveName;
   }

   @Nullable
   public Objective getObjective() {
      return this.objective;
   }

   public boolean validObjective() {
      return this.objective != null;
   }

   public ScoreRequirements(@Nullable Double min, @Nullable Double max, @NotNull String objective) {
      super(min, max);
      this.objectiveName = objective;
      this.objective = getObjective(objective);
   }

   public ScoreRequirements(@NotNull QuickNumberRange range, @NotNull String objective) {
      super(range.getMinimumInclusive(), range.getMaximumInclusive());
      this.objectiveName = objective;
      this.objective = getObjective(objective);
   }

   @NotNull
   public static ArrayList<ScoreRequirements> getFromCompactString(@Nullable String source) {
      ArrayList<ScoreRequirements> ret = new ArrayList<>();
      if (source == null) {
         return ret;
      }

      source = SilentNumbers.unwrapFromCurlyBrackets(source);
      String[] splitSR = source.split(",");

      for (String str : splitSR) {
         ScoreRequirements sR = getFromString(str);
         if (sR != null) {
            ret.add(sR);
         }
      }

      return ret;
   }

   @Nullable
   public static ScoreRequirements getFromString(@Nullable String source) {
      if (source == null) {
         return null;
      }

      if (source.contains("=")) {
         String[] args = source.split("=");
         if (args.length == 2) {
            QuickNumberRange qnr = QuickNumberRange.getFromString(args[1]);
            if (qnr != null) {
               return new ScoreRequirements(qnr, args[0]);
            }
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return this.objectiveName + "=" + super.toString();
   }

   @NotNull
   @Override
   public String toStringColored() {
      return this.validObjective()
         ? ChatColor.GREEN + this.objectiveName + " §7: §e" + super.toStringColored()
         : ChatColor.RED + this.objectiveName + " §7: §e" + super.toStringColored();
   }

   @Nullable
   public static Objective getObjective(@Nullable String name) {
      if (name == null) {
         return null;
      }

      ScoreboardManager manager = Bukkit.getScoreboardManager();
      if (manager == null) {
         return null;
      }

      Scoreboard targetScoreboard = manager.getMainScoreboard();
      return targetScoreboard.getObjective(name);
   }

   public static void setPlayerScore(@NotNull Objective objective, @NotNull Player player, @NotNull PlusMinusPercent score) {
      setEntryScore(objective, player.getName(), score);
   }

   public static void setEntryScore(@NotNull Objective objective, @NotNull String entree, @NotNull PlusMinusPercent score) {
      setEntryScore(objective, entree, SilentNumbers.round(score.apply(getEntryScore(objective, entree) + 0.0)));
   }

   public static int getPlayerScore(@NotNull Objective objective, @NotNull Player player) {
      return getEntryScore(objective, player.getName());
   }

   public static int getEntryScore(@NotNull Objective objective, @NotNull String entree) {
      Score sc = objective.getScore(entree);
      return sc.getScore();
   }

   public static void setPlayerScore(@NotNull Objective objective, @NotNull Player player, int score) {
      setEntryScore(objective, player.getName(), score);
   }

   public static void setEntryScore(@NotNull Objective objective, @NotNull String entry, int score) {
      objective.getScore(entry).setScore(score);
   }

   public static void setEntityScore(@NotNull Objective objective, @NotNull Entity player, @NotNull PlusMinusPercent score) {
      setEntityScore(objective, player, SilentNumbers.round(score.apply(getEntityScore(objective, player) + 0.0)));
   }

   public static int getEntityScore(@NotNull Objective objective, @NotNull Entity player) {
      return objective.getScore(player.getUniqueId().toString()).getScore();
   }

   public static void setEntityScore(@NotNull Objective objective, @NotNull Entity player, int score) {
      objective.getScore(player.getUniqueId().toString()).setScore(score);
   }
}
