package io.lumine.mythic.core.utils;

import java.util.regex.Pattern;

public class Patterns {
   public static Pattern MSMobX;
   public static Pattern MSMobY;
   public static Pattern MSMobZ;
   public static Pattern MSTargetX;
   public static Pattern MSTargetY;
   public static Pattern MSTargetZ;
   public static Pattern MSTriggerX;
   public static Pattern MSTriggerY;
   public static Pattern MSTriggerZ;
   public static Pattern VariableRanges;
   public static Pattern MobScore;
   public static Pattern GlobalScore;
   public static Pattern TargetScore;
   public static Pattern TriggerScore;
   public static Pattern GenericScore;
   public static Pattern LegacyBossX;
   public static Pattern LegacyBossY;
   public static Pattern LegacyBossZ;
   public static Pattern LegacyPlayerX;
   public static Pattern LegacyPlayerY;
   public static Pattern LegacyPlayerYC;
   public static Pattern LegacyPlayerZ;
   public static Pattern LoreRanges;

   public static void CompilePatterns() {
      LegacyBossX = Pattern.compile("boss_x%([0-9]+)");
      LegacyBossY = Pattern.compile("boss_y%([0-9]+)");
      LegacyBossZ = Pattern.compile("boss_z%([0-9]+)");
      LegacyPlayerX = Pattern.compile("player_x%([0-9]+)");
      LegacyPlayerY = Pattern.compile("player_y%([0-9]+)");
      LegacyPlayerYC = Pattern.compile("player_yc%([0-9]+)");
      LegacyPlayerZ = Pattern.compile("player_z%([0-9]+)");
      MSMobX = Pattern.compile("<mob.l.x%([0-9]+)>");
      MSMobY = Pattern.compile("<mob.l.y%([0-9]+)>");
      MSMobZ = Pattern.compile("<mob.l.z%([0-9]+)>");
      MSTargetX = Pattern.compile("<target.l.x%([0-9]+)>");
      MSTargetY = Pattern.compile("<target.l.y%([0-9]+)>");
      MSTargetZ = Pattern.compile("<target.l.z%([0-9]+)>");
      MSTriggerX = Pattern.compile("<trigger.l.x%([0-9]+)>");
      MSTriggerY = Pattern.compile("<trigger.l.y%([0-9]+)>");
      MSTriggerZ = Pattern.compile("<trigger.l.z%([0-9]+)>");
      VariableRanges = Pattern.compile("<random.([0-9]*)-([0-9]*)>");
      LoreRanges = Pattern.compile("\\{([0-9]*)-([0-9]*)\\}");
      MobScore = Pattern.compile("<mob.score.([a-zA-Z0-9]+)>");
      GlobalScore = Pattern.compile("<global.score.([a-zA-Z0-9]+)>");
      TargetScore = Pattern.compile("<target.score.([a-zA-Z0-9]+)>");
      TriggerScore = Pattern.compile("<trigger.score.([a-zA-Z0-9]+)>");
      GenericScore = Pattern.compile("<score.([-a-zA-Z0-9]+).([a-zA-Z0-9]+)>");
   }

   public static String toRegex(String regexstr) {
      StringBuilder s = new StringBuilder(regexstr.length());
      s.append('^');
      int i = 0;

      for (int is = regexstr.length(); i < is; i++) {
         char c = regexstr.charAt(i);
         switch (c) {
            case '$':
            case '(':
            case ')':
            case '.':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '{':
            case '|':
            case '}':
               s.append("\\");
               s.append(c);
               break;
            case '*':
               s.append(".*");
               break;
            case '?':
               s.append(".");
               break;
            default:
               s.append(c);
         }
      }

      s.append('$');
      return s.toString();
   }
}
