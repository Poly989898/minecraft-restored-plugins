package io.lumine.mythic.core.skills.placeholders.segments;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import lombok.Generated;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderSegmentResolver<PARSER> {
   @NotNull
   private GenericPlaceholder<?> placeholder;
   @Nullable
   private PARSER defaultValue;
   private Function<MythicLineConfig, PARSER> mlcGetter;
   @Nullable
   private Integer segIndex;
   private Function<String, PARSER> segValueParser;
   private final Collection<Function<String, Boolean>> segValueCheckers;
   private SegmentSource listSource;

   @NotNull
   @Contract(" -> new")
   public ResolvedPlaceholderSegment<PARSER> get() {
      MythicLineConfig config = this.placeholder.getInstanceData().config;
      if (config != null && this.mlcGetter != null) {
         PARSER parsed = this.mlcGetter.apply(config);
         if (parsed != null) {
            return new ResolvedPlaceholderSegment<>(parsed, config);
         }
      }

      if (this.segIndex == null) {
         return new ResolvedPlaceholderSegment<>(this.defaultValue, null);
      } else {
         GenericPlaceholderSegment segment = this.listSource.getArg.apply(this.placeholder.getPlaceholderSegmentsManager(), this.segIndex);
         if (segment == null) {
            return new ResolvedPlaceholderSegment<>(this.defaultValue, null);
         } else {
            if (!this.segValueCheckers.isEmpty()) {
               for (Function<String, Boolean> checker : this.segValueCheckers) {
                  if (!checker.apply(segment.key())) {
                     return new ResolvedPlaceholderSegment<>(this.defaultValue, null);
                  }
               }
            }

            if (this.listSource == SegmentSource.EXTRA_ARGS
               && !this.placeholder.isMetaKeywordsHaveBeenInitialized()
               && this.placeholder.getUsedPlaceholderArguments().get() <= this.segIndex) {
               this.placeholder.getUsedPlaceholderArguments().set(this.segIndex + 1);
            }

            return new ResolvedPlaceholderSegment<>(this.segValueParser.apply(segment.key()), segment.config());
         }
      }
   }

   @Generated
   private static <PARSER> SegmentSource $default$listSource() {
      return SegmentSource.EXTRA_ARGS;
   }

   @Generated
   PlaceholderSegmentResolver(
      @NotNull GenericPlaceholder<?> placeholder,
      @Nullable PARSER defaultValue,
      Function<MythicLineConfig, PARSER> mlcGetter,
      @Nullable Integer segIndex,
      Function<String, PARSER> segValueParser,
      Collection<Function<String, Boolean>> segValueCheckers,
      SegmentSource listSource
   ) {
      if (placeholder == null) {
         throw new NullPointerException("placeholder is marked non-null but is null");
      } else {
         this.placeholder = placeholder;
         this.defaultValue = defaultValue;
         this.mlcGetter = mlcGetter;
         this.segIndex = segIndex;
         this.segValueParser = segValueParser;
         this.segValueCheckers = segValueCheckers;
         this.listSource = listSource;
      }
   }

   @Generated
   public static <PARSER> PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<PARSER> builder() {
      return new PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<>();
   }

   @Generated
   public static class PlaceholderSegmentResolverBuilder<PARSER> {
      @Generated
      private GenericPlaceholder<?> placeholder;
      @Generated
      private PARSER defaultValue;
      @Generated
      private Function<MythicLineConfig, PARSER> mlcGetter;
      @Generated
      private Integer segIndex;
      @Generated
      private Function<String, PARSER> segValueParser;
      @Generated
      private ArrayList<Function<String, Boolean>> segValueCheckers;
      @Generated
      private boolean listSource$set;
      @Generated
      private SegmentSource listSource$value;

      @Generated
      PlaceholderSegmentResolverBuilder() {
      }

      @Generated
      public PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<PARSER> placeholder(@NotNull GenericPlaceholder<?> placeholder) {
         if (placeholder == null) {
            throw new NullPointerException("placeholder is marked non-null but is null");
         } else {
            this.placeholder = placeholder;
            return this;
         }
      }

      @Generated
      public PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<PARSER> defaultValue(@Nullable PARSER defaultValue) {
         this.defaultValue = defaultValue;
         return this;
      }

      @Generated
      public PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<PARSER> mlcGetter(Function<MythicLineConfig, PARSER> mlcGetter) {
         this.mlcGetter = mlcGetter;
         return this;
      }

      @Generated
      public PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<PARSER> segIndex(@Nullable Integer segIndex) {
         this.segIndex = segIndex;
         return this;
      }

      @Generated
      public PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<PARSER> segValueParser(Function<String, PARSER> segValueParser) {
         this.segValueParser = segValueParser;
         return this;
      }

      @Generated
      public PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<PARSER> segValueChecker(Function<String, Boolean> segValueChecker) {
         if (this.segValueCheckers == null) {
            this.segValueCheckers = new ArrayList<>();
         }

         this.segValueCheckers.add(segValueChecker);
         return this;
      }

      @Generated
      public PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<PARSER> segValueCheckers(
         Collection<? extends Function<String, Boolean>> segValueCheckers
      ) {
         if (segValueCheckers != null) {
            if (this.segValueCheckers == null) {
               this.segValueCheckers = new ArrayList<>();
            }

            this.segValueCheckers.addAll(segValueCheckers);
         }

         return this;
      }

      @Generated
      public PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<PARSER> clearSegValueCheckers() {
         if (this.segValueCheckers != null) {
            this.segValueCheckers.clear();
         }

         return this;
      }

      @Generated
      public PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder<PARSER> listSource(SegmentSource listSource) {
         this.listSource$value = listSource;
         this.listSource$set = true;
         return this;
      }

      @Generated
      public PlaceholderSegmentResolver<PARSER> build() {
         Collection segValueCheckers = switch (this.segValueCheckers == null ? 0 : this.segValueCheckers.size()) {
            case 0 -> Collections.emptyList();
            case 1 -> Collections.singletonList(this.segValueCheckers.get(0));
            default -> Collections.unmodifiableList(new ArrayList<>(this.segValueCheckers));
         };
         SegmentSource listSource$value = this.listSource$value;
         if (!this.listSource$set) {
            listSource$value = PlaceholderSegmentResolver.$default$listSource();
         }

         return new PlaceholderSegmentResolver<>(
            this.placeholder, this.defaultValue, this.mlcGetter, this.segIndex, this.segValueParser, segValueCheckers, listSource$value
         );
      }

      @Generated
      @Override
      public String toString() {
         return "PlaceholderSegmentResolver.PlaceholderSegmentResolverBuilder(placeholder="
            + this.placeholder
            + ", defaultValue="
            + this.defaultValue
            + ", mlcGetter="
            + this.mlcGetter
            + ", segIndex="
            + this.segIndex
            + ", segValueParser="
            + this.segValueParser
            + ", segValueCheckers="
            + this.segValueCheckers
            + ", listSource$value="
            + this.listSource$value
            + ")";
      }
   }
}
