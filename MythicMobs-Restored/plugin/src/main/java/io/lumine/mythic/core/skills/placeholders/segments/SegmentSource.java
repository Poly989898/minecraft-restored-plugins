package io.lumine.mythic.core.skills.placeholders.segments;

import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import java.util.function.BiFunction;

public enum SegmentSource {
   ALL_ARGS(PlaceholderSegmentsManager::getArg),
   EXTRA_ARGS(PlaceholderSegmentsManager::getExtraArg),
   WILDCARD_ARGS(PlaceholderSegmentsManager::getWildcardArg);

   public final BiFunction<PlaceholderSegmentsManager, Integer, GenericPlaceholderSegment> getArg;

   private SegmentSource(BiFunction<PlaceholderSegmentsManager, Integer, GenericPlaceholderSegment> function) {
      this.getArg = function;
   }
}
