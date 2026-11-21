package io.lumine.mythic.core.skills.placeholders.segments.types;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record MetaKeywordsSegment(
   @NotNull PolymorphicPlaceholder key, @Nullable MythicLineConfig config, @NotNull Map<String, PolymorphicPlaceholder> attributes
) {
   @NotNull
   public static List<MetaKeywordsSegment> getMetaKeywordSegments(@NotNull List<GenericPlaceholderSegment> genericPlaceholderSegments, int offset) {
      List<MetaKeywordsSegment> metaKeywords = new ArrayList<>();
      if (offset >= 0 && offset <= genericPlaceholderSegments.size()) {
         genericPlaceholderSegments.listIterator(offset)
            .forEachRemaining(segment -> metaKeywords.add(new MetaKeywordsSegment(PolymorphicPlaceholder.of(segment.key()), segment.config(), new HashMap<>())));
         return metaKeywords;
      } else {
         return metaKeywords;
      }
   }
}
