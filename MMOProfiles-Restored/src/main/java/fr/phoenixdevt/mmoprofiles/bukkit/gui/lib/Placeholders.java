package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/** Values substituted for {key} in GUI item names and lore. */
public class Placeholders {
   private static final Pattern PATTERN = Pattern.compile("\\{([^{}]+)}");
   private final Map<String, String> values = new HashMap<>();

   public void register(@NotNull String key, @NotNull Object value) {
      this.values.put(key, String.valueOf(value));
   }

   /** Unknown keys are left untouched, as {key}. */
   @NotNull
   public String parsePlaceholder(@NotNull String key) {
      String value = this.values.get(key);
      return value != null ? value : "{" + key + "}";
   }

   @NotNull
   public String apply(@NotNull String text) {
      Matcher matcher = PATTERN.matcher(text);
      StringBuilder out = new StringBuilder();
      while (matcher.find()) {
         matcher.appendReplacement(out, Matcher.quoteReplacement(this.parsePlaceholder(matcher.group(1))));
      }
      matcher.appendTail(out);
      return out.toString();
   }
}
