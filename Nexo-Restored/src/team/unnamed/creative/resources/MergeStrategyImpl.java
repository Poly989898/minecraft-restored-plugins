package team.unnamed.creative.resources;

enum MergeStrategyImpl implements MergeStrategy {
   OVERRIDE,
   MERGE_AND_FAIL_ON_ERROR,
   MERGE_AND_KEEP_FIRST_ON_ERROR;
}
