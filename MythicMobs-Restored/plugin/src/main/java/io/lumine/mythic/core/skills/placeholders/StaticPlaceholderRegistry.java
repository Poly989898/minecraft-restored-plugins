package io.lumine.mythic.core.skills.placeholders;

import com.roklenarcic.util.strings.MapMatchListener;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.utils.string.replacer.AhoCorasickReplacer;
import java.util.Map.Entry;

public class StaticPlaceholderRegistry extends AhoCorasickReplacer {
   public boolean hasPlaceholders() {
      return !this.dictionary.isEmpty();
   }

   @Override
   public void register(String placeholder, String rep) {
      this.dictionary.put("<" + placeholder + ">", rep);
      this.dictionary.put("<" + placeholder + "{", rep);
   }

   @Override
   public String replaceText(String text) {
      this.assureCompiled();
      StringBuilder result = new StringBuilder();
      int[] lastEndPosition = new int[]{0};
      MapMatchListener<String> listener = (haystack1, startPosition, endPosition, replacementValue) -> {
         if (startPosition < lastEndPosition[0]) {
            return true;
         } else {
            char lastChar = haystack1.charAt(endPosition - 1);
            result.append(haystack1, lastEndPosition[0], startPosition);
            if (lastChar == '>') {
               result.append(replacementValue);
               lastEndPosition[0] = endPosition;
               return true;
            } else {
               String firstBraces = PlaceholderExecutor.getFirstBraces(haystack1.substring(endPosition - 1));
               if (firstBraces == null) {
                  result.append(haystack1, startPosition, endPosition);
                  lastEndPosition[0] = endPosition;
                  return true;
               } else {
                  MythicLineConfig config = MythicLineConfigImpl.of(firstBraces);
                  String replacement = replacementValue;

                  for (Entry<String, String> entry : config.entrySet()) {
                     replacement = this.replaceParamPlaceholder(replacement, entry.getKey(), entry.getValue());
                  }

                  replacement = this.resolveDefaults(replacement);
                  result.append(replacement);
                  lastEndPosition[0] = endPosition + firstBraces.length();
                  return true;
               }
            }
         }
      };
      this.replacer.match(text, listener);
      result.append(text.substring(lastEndPosition[0]));
      return result.toString();
   }

   private String replaceParamPlaceholder(String text, String key, String value) {
      text = text.replace("<skill." + key + ">", value);
      String prefix = "<skill." + key + "{";

      int idx;
      while ((idx = text.indexOf(prefix)) != -1) {
         String braces = PlaceholderExecutor.getFirstBraces(text.substring(idx + prefix.length() - 1));
         if (braces == null) {
            break;
         }

         int end = idx + prefix.length() - 1 + braces.length() + 1;
         if (end > text.length() || text.charAt(end - 1) != '>') {
            break;
         }

         text = text.substring(0, idx) + value + text.substring(end);
      }

      return text;
   }

   private String resolveDefaults(String text) {
      String prefix = "<skill.";
      int idx = 0;

      while ((idx = text.indexOf(prefix, idx)) != -1) {
         int braceStart = text.indexOf(123, idx);
         int angleEnd = text.indexOf(62, idx);
         if (angleEnd == -1 || braceStart != -1 && angleEnd >= braceStart) {
            if (braceStart == -1) {
               break;
            }

            String braces = PlaceholderExecutor.getFirstBraces(text.substring(braceStart));
            if (braces == null) {
               idx = braceStart + 1;
            } else {
               MythicLineConfig config = MythicLineConfigImpl.of(braces);
               String defaultValue = config.getString(new String[]{"default", "def", "fallback", "|"}, null);
               int end = braceStart + braces.length() + 1;
               if (end > text.length() || text.charAt(end - 1) != '>') {
                  idx = braceStart + 1;
               } else if (defaultValue != null) {
                  text = text.substring(0, idx) + defaultValue + text.substring(end);
               } else {
                  idx = end;
               }
            }
         } else {
            idx = angleEnd + 1;
         }
      }

      return text;
   }
}
