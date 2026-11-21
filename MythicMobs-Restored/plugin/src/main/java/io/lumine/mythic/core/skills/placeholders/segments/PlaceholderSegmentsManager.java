package io.lumine.mythic.core.skills.placeholders.segments;

import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderSegmentsManager {
   @Getter
   @NotNull
   private final List<GenericPlaceholderSegment> allArgs;
   @NotNull
   private final List<Integer> wildcardArgsIndexes;
   private final int extraArgsStartIndex;

   @Nullable
   public GenericPlaceholderSegment getArg(int index) {
      return index < this.allArgs.size() && index >= 0 ? this.allArgs.get(index) : null;
   }

   public List<GenericPlaceholderSegment> getExtraArgs() {
      return this.extraArgsStartIndex > 0 && this.extraArgsStartIndex <= this.allArgs.size()
         ? this.allArgs.subList(this.extraArgsStartIndex, this.allArgs.size())
         : List.of();
   }

   @Nullable
   public GenericPlaceholderSegment getExtraArg(int i) {
      if (this.extraArgsStartIndex > 0 && i >= 0) {
         int index = this.extraArgsStartIndex + i;
         return index >= this.allArgs.size() ? null : this.allArgs.get(index);
      } else {
         return null;
      }
   }

   @Nullable
   public GenericPlaceholderSegment getWildcardArg(int i) {
      if (i >= 0 && i < this.wildcardArgsIndexes.size()) {
         Integer index = this.wildcardArgsIndexes.get(i);
         return index != null && index >= 0 && index < this.allArgs.size() ? this.allArgs.get(index) : null;
      } else {
         return null;
      }
   }

   @Generated
   PlaceholderSegmentsManager(@NotNull List<GenericPlaceholderSegment> allArgs, @NotNull List<Integer> wildcardArgsIndexes, int extraArgsStartIndex) {
      if (allArgs == null) {
         throw new NullPointerException("allArgs is marked non-null but is null");
      } else if (wildcardArgsIndexes == null) {
         throw new NullPointerException("wildcardArgsIndexes is marked non-null but is null");
      } else {
         this.allArgs = allArgs;
         this.wildcardArgsIndexes = wildcardArgsIndexes;
         this.extraArgsStartIndex = extraArgsStartIndex;
      }
   }

   @Generated
   public static PlaceholderSegmentsManager.PlaceholderSegmentsManagerBuilder builder() {
      return new PlaceholderSegmentsManager.PlaceholderSegmentsManagerBuilder();
   }

   @ToString
   @Generated
   public static class PlaceholderSegmentsManagerBuilder {
      @Generated
      private ArrayList<GenericPlaceholderSegment> allArgs;
      @Generated
      private ArrayList<Integer> wildcardArgsIndexes;
      @Generated
      private int extraArgsStartIndex;

      @Generated
      PlaceholderSegmentsManagerBuilder() {
      }

      @Generated
      public PlaceholderSegmentsManager.PlaceholderSegmentsManagerBuilder allArg(GenericPlaceholderSegment allArg) {
         if (this.allArgs == null) {
            this.allArgs = new ArrayList<>();
         }

         this.allArgs.add(allArg);
         return this;
      }

      @Generated
      public PlaceholderSegmentsManager.PlaceholderSegmentsManagerBuilder allArgs(Collection<? extends GenericPlaceholderSegment> allArgs) {
         if (allArgs == null) {
            throw new NullPointerException("allArgs cannot be null");
         } else {
            if (this.allArgs == null) {
               this.allArgs = new ArrayList<>();
            }

            this.allArgs.addAll(allArgs);
            return this;
         }
      }

      @Generated
      public PlaceholderSegmentsManager.PlaceholderSegmentsManagerBuilder clearAllArgs() {
         if (this.allArgs != null) {
            this.allArgs.clear();
         }

         return this;
      }

      @Generated
      public PlaceholderSegmentsManager.PlaceholderSegmentsManagerBuilder wildcardArgsIndex(Integer wildcardArgsIndex) {
         if (this.wildcardArgsIndexes == null) {
            this.wildcardArgsIndexes = new ArrayList<>();
         }

         this.wildcardArgsIndexes.add(wildcardArgsIndex);
         return this;
      }

      @Generated
      public PlaceholderSegmentsManager.PlaceholderSegmentsManagerBuilder wildcardArgsIndexes(Collection<? extends Integer> wildcardArgsIndexes) {
         if (wildcardArgsIndexes == null) {
            throw new NullPointerException("wildcardArgsIndexes cannot be null");
         } else {
            if (this.wildcardArgsIndexes == null) {
               this.wildcardArgsIndexes = new ArrayList<>();
            }

            this.wildcardArgsIndexes.addAll(wildcardArgsIndexes);
            return this;
         }
      }

      @Generated
      public PlaceholderSegmentsManager.PlaceholderSegmentsManagerBuilder clearWildcardArgsIndexes() {
         if (this.wildcardArgsIndexes != null) {
            this.wildcardArgsIndexes.clear();
         }

         return this;
      }

      @Generated
      public PlaceholderSegmentsManager.PlaceholderSegmentsManagerBuilder extraArgsStartIndex(int extraArgsStartIndex) {
         this.extraArgsStartIndex = extraArgsStartIndex;
         return this;
      }

      @Generated
      public PlaceholderSegmentsManager build() {
         List allArgs = switch (this.allArgs == null ? 0 : this.allArgs.size()) {
            case 0 -> Collections.emptyList();
            case 1 -> Collections.singletonList(this.allArgs.get(0));
            default -> Collections.unmodifiableList(new ArrayList<>(this.allArgs));
         };

         return new PlaceholderSegmentsManager(allArgs, switch (this.wildcardArgsIndexes == null ? 0 : this.wildcardArgsIndexes.size()) {
            case 0 -> Collections.emptyList();
            case 1 -> Collections.singletonList(this.wildcardArgsIndexes.get(0));
            default -> Collections.unmodifiableList(new ArrayList<>(this.wildcardArgsIndexes));
         }, this.extraArgsStartIndex);
      }
   }
}
