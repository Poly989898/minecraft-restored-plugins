package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "bar",
   version = "5.2",
   usedPlaceholderArguments = -1
)
public class BarPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   private BarPlaceholder.BarType barType = this.<BarPlaceholder.BarType>getResolver()
      .segIndex(0)
      .segValueParser(s -> BarPlaceholder.BarType.valueOf(s.toUpperCase()))
      .segValueChecker(s -> Arrays.stream(BarPlaceholder.BarType.values()).anyMatch(b -> b.name().equals(s.toUpperCase())))
      .defaultValue(BarPlaceholder.BarType.PROGRESS)
      .build()
      .get()
      .value();
   private final PlaceholderDouble value;
   private final PlaceholderDouble maxValue;
   private final PlaceholderInt length;
   private final PlaceholderString baseChar;
   private final PlaceholderString fillChar;
   private final PlaceholderString basePrefix;
   private final PlaceholderString fillPrefix;

   public BarPlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
      if (metaContext.config == null) {
         throw new NullPointerException("Config can't be null");
      } else {
         this.value = metaContext.config.getPlaceholderDouble(new String[]{"value", "v"}, 0.0);
         this.maxValue = metaContext.config.getPlaceholderDouble(new String[]{"maxvalue", "mv", "max"}, 100.0);
         this.length = metaContext.config.getPlaceholderInteger(new String[]{"length", "len", "l"}, 100);
         this.baseChar = metaContext.config.getPlaceholderString(new String[]{"remainingSymbol", "remaining", "r", "char", "c"}, "|");
         this.fillChar = metaContext.config.getPlaceholderString(new String[]{"progressSymbol", "progress", "p"}, this.baseChar.toString());
         this.basePrefix = metaContext.config.getPlaceholderString(new String[]{"remainingPrefix", "rp"}, "<gray>");
         this.fillPrefix = metaContext.config.getPlaceholderString(new String[]{"progressPrefix", "pp"}, "<green>");
         this.initializeMetaKeywords();
      }
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      switch (this.barType) {
         case PROGRESS:
            return this.getProgressBar(placeholderContext);
         case REVERSE:
            return this.getReverseBar(placeholderContext);
         default:
            return this.getProgressBar(placeholderContext);
      }
   }

   private String getProgressBar(PlaceholderContext placeholderContext) {
      double value = this.value.get(placeholderContext);
      double maxValue = this.maxValue.get(placeholderContext);
      double percent = value / maxValue;
      int length = this.length.get(placeholderContext);
      int gray = (int)Math.floor(percent * length);
      int rest = length - gray;
      String baseChar = this.baseChar.get(placeholderContext);
      String fillChar = this.fillChar.get(placeholderContext);
      StringBuilder line = new StringBuilder().append(this.fillPrefix.get(placeholderContext));
      line.append(String.valueOf(fillChar).repeat(Math.max(0, gray)));
      line.append(this.basePrefix.get(placeholderContext));
      line.append(String.valueOf(baseChar).repeat(Math.max(0, rest)));
      return line.toString();
   }

   private String getReverseBar(PlaceholderContext placeholderContext) {
      double value = this.value.get(placeholderContext);
      double maxValue = this.maxValue.get(placeholderContext);
      double percent = value / maxValue;
      int length = this.length.get(placeholderContext);
      int rest = (int)Math.floor(percent * length);
      int gray = length - rest;
      String baseChar = this.baseChar.get(placeholderContext);
      String fillChar = this.fillChar.get(placeholderContext);
      StringBuilder line = new StringBuilder().append(this.fillPrefix.get(placeholderContext));
      line.append(String.valueOf(fillChar).repeat(Math.max(0, gray)));
      line.append(this.basePrefix.get(placeholderContext));
      line.append(String.valueOf(baseChar).repeat(Math.max(0, rest)));
      return line.toString();
   }

   private static enum BarType {
      PROGRESS,
      REVERSE;
   }
}
