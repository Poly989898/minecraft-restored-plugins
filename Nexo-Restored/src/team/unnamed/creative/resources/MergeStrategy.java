package team.unnamed.creative.resources;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface MergeStrategy {
   static @NotNull MergeStrategy override() {
      return MergeStrategyImpl.OVERRIDE;
   }

   static @NotNull MergeStrategy mergeAndFailOnError() {
      return MergeStrategyImpl.MERGE_AND_FAIL_ON_ERROR;
   }

   static @NotNull MergeStrategy mergeAndKeepFirstOnError() {
      return MergeStrategyImpl.MERGE_AND_KEEP_FIRST_ON_ERROR;
   }
}
