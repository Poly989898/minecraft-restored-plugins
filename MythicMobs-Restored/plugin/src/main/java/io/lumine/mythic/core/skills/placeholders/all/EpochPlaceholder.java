package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.LongPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.time.Instant;
import java.time.temporal.ChronoField;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "utils.epoch",
   version = "5.9",
   description = "Returns the current epoch time in seconds",
   usedPlaceholderArguments = -1
)
public class EpochPlaceholder extends GenericPlaceholder<Long> implements LongPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> type = this.getPlaceholderString(0, new String[0]);

   public EpochPlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
      this.initializeMetaKeywords();
   }

   @Nullable
   public Long applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      if (this.type.value() == null) {
         return Instant.now().getEpochSecond();
      } else {
         String orDefault = this.type.getOrDefault(placeholderContext, PlaceholderString::get, "seconds");

         return switch (orDefault) {
            case "millis" -> Instant.now().getLong(ChronoField.MILLI_OF_SECOND);
            case "ticks" -> Instant.now().getEpochSecond() * 20L + Instant.now().getLong(ChronoField.MILLI_OF_SECOND) / 50L;
            case "timestamp" -> Instant.now().toEpochMilli();
            default -> Instant.now().getEpochSecond();
         };
      }
   }
}
