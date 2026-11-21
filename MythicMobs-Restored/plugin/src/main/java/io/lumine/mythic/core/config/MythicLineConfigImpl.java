package io.lumine.mythic.core.config;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderAngle;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderLong;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.placeholders.PlaceholderVector;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.LineConfig;
import io.lumine.mythic.bukkit.utils.numbers.AngleUnit;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderColorImpl;
import io.lumine.mythic.core.skills.variables.VariableScopedName;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MythicLineConfigImpl extends LineConfig implements GenericConfig, MythicLineConfig {
   private static final Pattern STRING_LIST_PATTERN = Pattern.compile("(?:\\[.+?\\]|\\(.+?\\)|\\{.+?\\}|[^,])+?(?=\\s*,|\\z)");
   private static final char DASH_LIST_MARKER = '-';

   public static MythicLineConfigImpl of(String line) {
      return new MythicLineConfigImpl(line);
   }

   public static MythicLineConfigImpl of(File file, String line) {
      return new MythicLineConfigImpl(line);
   }

   public MythicLineConfigImpl(String fileName, String line) {
      super(fileName, line);
   }

   public MythicLineConfigImpl(File file, String line) {
      super(file, line);
   }

   public MythicLineConfigImpl(String s) {
      super(s);
   }

   @Override
   public Boolean getBooleanNullable(String key) {
      String str = this.getString(key, null);
      if (str == null) {
         return null;
      } else {
         try {
            return Boolean.valueOf(str);
         } catch (Throwable e) {
            return null;
         }
      }
   }

   @Override
   public Boolean getBooleanNullable(String[] key) {
      String str = this.getString(key, null, new String[0]);
      if (str == null) {
         return null;
      } else {
         try {
            return Boolean.valueOf(str);
         } catch (Throwable e) {
            return null;
         }
      }
   }

   @Override
   public PlaceholderBoolean getPlaceholderBoolean(String key) {
      String str = this.getString(key, null);
      if (str == null) {
         return null;
      } else {
         try {
            return PlaceholderBoolean.of(str);
         } catch (Throwable e) {
            return null;
         }
      }
   }

   @Override
   public PlaceholderBoolean getPlaceholderBoolean(String[] key) {
      String str = this.getString(key, null, new String[0]);
      if (str == null) {
         return null;
      } else {
         try {
            return PlaceholderBoolean.of(str);
         } catch (Throwable e) {
            return null;
         }
      }
   }

   @Override
   public PlaceholderBoolean getPlaceholderBoolean(String key, Boolean def) {
      String str = this.getString(key, def == null ? null : def.toString());
      if (str == null) {
         return null;
      } else {
         try {
            return PlaceholderBoolean.of(str);
         } catch (Throwable e) {
            return null;
         }
      }
   }

   @Override
   public PlaceholderBoolean getPlaceholderBoolean(String[] key, Boolean def) {
      String str = this.getString(key, def == null ? null : def.toString(), new String[0]);
      if (str == null) {
         return null;
      } else {
         try {
            return PlaceholderBoolean.of(str);
         } catch (Throwable e) {
            return null;
         }
      }
   }

   @Override
   public Chroma getColor(String key) {
      String value = this.getString(key, null);
      return value != null ? Chroma.of(value) : null;
   }

   @Override
   public Chroma getColor(String key, Chroma def) {
      String value = this.getString(key, null);
      return value != null ? Chroma.of(value) : def;
   }

   @Override
   public Chroma getColor(String[] keys) {
      for (String key : keys) {
         String value = this.getString(key, null);
         if (value != null) {
            return Chroma.of(value);
         }
      }

      return null;
   }

   @Override
   public Chroma getColor(String[] keys, Chroma def) {
      for (String key : keys) {
         String value = this.getString(key, null);
         if (value != null) {
            return Chroma.of(value);
         }
      }

      return null;
   }

   @Override
   public PlaceholderString getPlaceholderString(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderString.of(s);
   }

   @Override
   public PlaceholderString getPlaceholderString(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderString.of(s);
   }

   @Override
   public PlaceholderInt getPlaceholderInteger(String key, PlaceholderInt def) {
      String s = this.getString(key, null);
      return s == null ? def : PlaceholderInt.of(s);
   }

   @Override
   public PlaceholderInt getPlaceholderInteger(String[] key, PlaceholderInt def, String... args) {
      String s = this.getString(key, null, args);
      return s == null ? def : PlaceholderInt.of(s);
   }

   @Override
   public PlaceholderInt getPlaceholderInteger(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderInt.of(s);
   }

   @Override
   public PlaceholderInt getPlaceholderInteger(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderInt.of(s);
   }

   @Override
   public PlaceholderInt getPlaceholderInteger(String key, int def) {
      return this.getPlaceholderInteger(key, String.valueOf(def));
   }

   @Override
   public PlaceholderInt getPlaceholderInteger(String[] key, int def, String... args) {
      return this.getPlaceholderInteger(key, String.valueOf(def), args);
   }

   @Override
   public PlaceholderLong getPlaceholderLong(String key, PlaceholderLong def) {
      String s = this.getString(key, null);
      return s == null ? def : PlaceholderLong.of(s);
   }

   @Override
   public PlaceholderLong getPlaceholderLong(String[] key, PlaceholderLong def, String... args) {
      String s = this.getString(key, null, args);
      return s == null ? def : PlaceholderLong.of(s);
   }

   @Override
   public PlaceholderLong getPlaceholderLong(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderLong.of(s);
   }

   @Override
   public PlaceholderLong getPlaceholderLong(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderLong.of(s);
   }

   @Override
   public PlaceholderLong getPlaceholderLong(String key, long def) {
      return this.getPlaceholderLong(key, String.valueOf(def));
   }

   @Override
   public PlaceholderLong getPlaceholderLong(String[] key, long def, String... args) {
      return this.getPlaceholderLong(key, String.valueOf(def), args);
   }

   @Override
   public PlaceholderDouble getPlaceholderDouble(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderDouble.of(s);
   }

   @Override
   public PlaceholderDouble getPlaceholderDouble(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderDouble.of(s);
   }

   @Override
   public PlaceholderDouble getPlaceholderDouble(String key, double def) {
      return this.getPlaceholderDouble(key, String.valueOf(def));
   }

   @Override
   public PlaceholderDouble getPlaceholderDouble(String[] key, double def, String... args) {
      return this.getPlaceholderDouble(key, String.valueOf(def), args);
   }

   @Override
   public PlaceholderDouble getPlaceholderDouble(String key, PlaceholderDouble def) {
      String s = this.getString(key, null);
      return s == null ? def : PlaceholderDouble.of(s);
   }

   @Override
   public PlaceholderDouble getPlaceholderDouble(String[] key, PlaceholderDouble def, String... args) {
      String s = this.getString(key, null, args);
      return s == null ? def : PlaceholderDouble.of(s);
   }

   @Override
   public PlaceholderDuration getPlaceholderDuration(String key, DurationUnit defaultUnit, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderDuration.of(s, defaultUnit);
   }

   @Override
   public PlaceholderDuration getPlaceholderDuration(String[] key, DurationUnit defaultUnit, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderDuration.of(s, defaultUnit);
   }

   @Override
   public PlaceholderFloat getPlaceholderFloat(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderFloat.of(s);
   }

   @Override
   public PlaceholderFloat getPlaceholderFloat(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderFloat.of(s);
   }

   @Override
   public PlaceholderFloat getPlaceholderFloat(String key, float def) {
      return this.getPlaceholderFloat(key, String.valueOf(def));
   }

   @Override
   public PlaceholderFloat getPlaceholderFloat(String[] key, float def, String... args) {
      return this.getPlaceholderFloat(key, String.valueOf(def), args);
   }

   @Override
   public PlaceholderColorImpl getPlaceholderColor(String key, String def) {
      PlaceholderString s = this.getPlaceholderString(key, def);
      return s == null ? null : new PlaceholderColorImpl(s);
   }

   public PlaceholderColorImpl getPlaceholderColor(String[] key, String def, String... args) {
      PlaceholderString s = this.getPlaceholderString(key, def, args);
      return s == null ? null : new PlaceholderColorImpl(s);
   }

   @Override
   public PlaceholderVector getPlaceholderVector(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : PlaceholderVector.of(s);
   }

   @Override
   public PlaceholderVector getPlaceholderVector(String[] key, String def) {
      String s = this.getString(key, def, new String[0]);
      return s == null ? null : PlaceholderVector.of(s);
   }

   @Override
   public PlaceholderVector getPlaceholderVector(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderVector.of(s);
   }

   @Override
   public PlaceholderAngle getPlaceholderAngle(String[] key, AngleUnit defaultUnit, String def) {
      String s = this.getString(key, def, new String[0]);
      return s == null ? null : PlaceholderAngle.of(s, defaultUnit);
   }

   @Override
   public PlaceholderAngle getPlaceholderAngle(String[] key, AngleUnit defaultUnit, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : PlaceholderAngle.of(s, defaultUnit);
   }

   @Override
   public SkillAudience getAudience(String key, String def) {
      String s = this.getString(key, def);
      return s == null ? null : MythicBukkit.inst().getSkillManager().getAudience(s);
   }

   @Override
   public SkillAudience getAudience(String[] key, String def, String... args) {
      String s = this.getString(key, def, args);
      return s == null ? null : MythicBukkit.inst().getSkillManager().getAudience(s);
   }

   @Override
   public PolymorphicPlaceholder getPolymorphicPlaceholder(String key) {
      String s = this.getString(key, null);
      return this.safePolymorphicPlaceholder(s);
   }

   @Override
   public PolymorphicPlaceholder getPolymorphicPlaceholder(String key, String def) {
      String s = this.getString(key, def);
      return this.safePolymorphicPlaceholder(s);
   }

   @Override
   public PolymorphicPlaceholder getPolymorphicPlaceholder(String[] key) {
      String s = this.getString(key, null, new String[0]);
      return this.safePolymorphicPlaceholder(s);
   }

   @Override
   public PolymorphicPlaceholder getPolymorphicPlaceholder(String[] key, String def) {
      String s = this.getString(key, def, new String[0]);
      return this.safePolymorphicPlaceholder(s);
   }

   private PolymorphicPlaceholder safePolymorphicPlaceholder(String str) {
      try {
         return str == null ? null : PolymorphicPlaceholder.of(str);
      } catch (Throwable e) {
         return null;
      }
   }

   @Override
   public VariableScopedName getVariableScopedName(String str) {
      return this.safeVariableScopedName(str);
   }

   @Override
   public VariableScopedName getVariableScopedName(String str, String def) {
      String string = this.getString(str, def);
      return this.safeVariableScopedName(str);
   }

   @Override
   public VariableScopedName getVariableScopedName(String[] str) {
      String string = this.getString(str, null, new String[0]);
      return this.safeVariableScopedName(string);
   }

   @Override
   public VariableScopedName getVariableScopedName(String[] str, String def) {
      String string = this.getString(str, def, new String[0]);
      return this.safeVariableScopedName(string);
   }

   private VariableScopedName safeVariableScopedName(String str) {
      try {
         return str == null ? null : VariableScopedName.of(str);
      } catch (Throwable e) {
         return null;
      }
   }

   @Override
   public <T extends Enum<T>> T getEnum(String[] key, Class<T> clazz, T def) {
      try {
         String in = this.getString(key);
         return in == null ? def : Enum.valueOf(clazz, in.toUpperCase());
      } catch (Error | Exception e) {
         return def;
      }
   }

   @Override
   public <T extends Enum<T>> T getEnum(String[] key, Class<T> clazz, T def, String errorMessage) {
      try {
         String in = this.getString(key);
         return in == null ? def : Enum.valueOf(clazz, in.toUpperCase());
      } catch (Error | Exception e) {
         MythicLogger.errorGenericConfig(this, errorMessage);
         return def;
      }
   }

   @Override
   public List<String> parseStringList(String in) {
      if (in == null) {
         return null;
      } else {
         List<String> found = Lists.newArrayList();

         for (String item : splitTopLevelCommaList(in)) {
            List<String> dashItems = tryParseDashSeparatedBracketList(item);
            if (dashItems != null) {
               found.addAll(dashItems);
            } else {
               found.add(item);
            }
         }

         return found;
      }
   }

   public static List<String> splitTopLevelCommaList(String in) {
      Matcher matcher = STRING_LIST_PATTERN.matcher(in);
      List<String> found = Lists.newArrayList();

      while (matcher.find()) {
         found.add(matcher.group().trim());
      }

      return found;
   }

   private static List<String> tryParseDashSeparatedBracketList(String value) {
      String trimmed = value.trim();
      if (trimmed.length() >= 2 && trimmed.charAt(0) == '[' && trimmed.charAt(trimmed.length() - 1) == ']') {
         String inner = trimmed.substring(1, trimmed.length() - 1).trim();
         if (inner.isEmpty()) {
            return Lists.newArrayList();
         } else {
            return !inner.contains(" - ") && !inner.startsWith("-") ? null : splitDashSeparatedList(inner);
         }
      } else {
         return null;
      }
   }

   private static List<String> splitDashSeparatedList(String value) {
      List<String> items = Lists.newArrayList();
      StringBuilder current = new StringBuilder();
      int braceDepth = 0;
      boolean foundSeparator = false;

      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         if (c == '{') {
            braceDepth++;
         } else if (c == '}' && braceDepth > 0) {
            braceDepth--;
         }

         if (braceDepth == 0 && c == '-' && isDashSeparator(value, i)) {
            foundSeparator = true;
            addDashItem(items, current);
            current.setLength(0);

            while (i + 1 < value.length() && Character.isWhitespace(value.charAt(i + 1))) {
               i++;
            }
         } else {
            current.append(c);
         }
      }

      addDashItem(items, current);
      return foundSeparator ? items : null;
   }

   private static boolean isDashSeparator(String value, int index) {
      char prev = index > 0 ? value.charAt(index - 1) : 32;
      char next = index + 1 < value.length() ? value.charAt(index + 1) : 0;
      boolean prevWhitespace = index == 0 || Character.isWhitespace(prev);
      boolean nextWhitespace = index + 1 < value.length() && Character.isWhitespace(next);
      return prevWhitespace && nextWhitespace;
   }

   private static void addDashItem(List<String> items, StringBuilder current) {
      String item = current.toString().trim();
      if (!item.isEmpty()) {
         items.add(item);
      }
   }

   @Override
   public List<String> getStringList(String key, String def) {
      return this.parseStringList(this.getString(key, def));
   }

   @Override
   public List<String> getStringList(String[] key, String def) {
      return this.parseStringList(this.getString(key, def, new String[0]));
   }

   @Override
   public List<String> getStringList(String[] key, String def, String... args) {
      return this.parseStringList(this.getString(key, def, args));
   }

   public static String unparseBlock(String s) {
      if (s.contains("\"")) {
         String[] split = s.split("\"");
         int i = 0;
         String ns = "";

         for (String ss : split) {
            if (i % 2 == 1) {
               ns = ns.concat("\"" + SkillString.unparseMessageSpecialChars(ss) + "\"");
            } else {
               ns = ns.concat(ss);
            }

            i++;
         }

         s = ns;
      }

      if (s.contains("'")) {
         String[] split = s.split("'");
         int i = 0;
         String ns = "";

         for (String ss : split) {
            if (i % 2 == 1) {
               ns = ns.concat("'" + SkillString.unparseMessageSpecialChars(ss) + "'");
            } else {
               ns = ns.concat(ss);
            }

            i++;
         }

         s = ns;
      }

      int pos = 0;
      int count = 0;
      int ss = 0;
      int sc = 0;
      int ec = 0;
      String parsed = "";

      for (char c : s.toCharArray()) {
         if (c == '{') {
            if (count == 0) {
               sc = pos;
            }

            count++;
         }

         if (c == '}') {
            if (--count == 0) {
               String f = s.substring(ss, sc);
               String m = s.substring(sc, pos).replace(" ", "<&csp>").replace("-", "<&da>");
               String e = s.substring(pos);
               parsed = parsed + f + m;
               ss = pos;
            }
         }

         pos++;
      }

      return parsed + s.substring(ss, pos);
   }

   public static String parseBlock(String s) {
      return s.replace("<&csp>", " ").replace("<&da>", "-").trim();
   }
}
