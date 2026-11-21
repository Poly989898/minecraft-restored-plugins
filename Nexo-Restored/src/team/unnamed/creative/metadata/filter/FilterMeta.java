package team.unnamed.creative.metadata.filter;

import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.KeyPattern;
import team.unnamed.creative.metadata.MetadataPart;

@NonExtendable
public interface FilterMeta extends MetadataPart {
   @Contract("_ -> new")
   static @NotNull FilterMeta filter(@NotNull List<KeyPattern> patterns) {
      return new FilterMetaImpl(patterns);
   }

   @Contract("_ -> new")
   static @NotNull FilterMeta filter(@NotNull KeyPattern... patterns) {
      return filter(Arrays.asList(patterns));
   }

   /** @deprecated */
   @Deprecated
   @Contract("-> fail")
   static @NotNull FilterMeta filter() {
      throw new UnsupportedOperationException("Cannot create an empty filter meta");
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("_ -> new")
   static @NotNull FilterMeta of(@NotNull List<KeyPattern> patterns) {
      return filter(patterns);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("_ -> new")
   static @NotNull FilterMeta of(@NotNull KeyPattern... patterns) {
      return filter(Arrays.asList(patterns));
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("-> fail")
   static @NotNull FilterMeta of() {
      throw new UnsupportedOperationException("Cannot create an empty filter meta");
   }

   @NotNull @Unmodifiable List<KeyPattern> patterns();
}
