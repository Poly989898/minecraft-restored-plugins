package team.unnamed.creative.metadata.pack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface PackFormat {
   PackFormat UNKNOWN = new PackFormatImpl(FormatVersion.of(0), FormatVersion.of(0));

   FormatVersion min();

   FormatVersion max();

   boolean isSingle();

   boolean isInRange(FormatVersion var1);

   boolean isInRange(PackFormat var1);

   /** @deprecated */
   @Deprecated
   boolean isInRange(int var1);

   default @NotNull PackFormat union(@NotNull PackFormat other) {
      return this.isSingle() && other.isSingle() ? format(this.min()) : format(FormatVersion.min(this.min(), other.min()), FormatVersion.max(this.max(), other.max()));
   }

   static @NotNull PackFormat format(FormatVersion format) {
      return format(format, format);
   }

   static @NotNull PackFormat format(FormatVersion min, FormatVersion max) {
      return new PackFormatImpl(min, max);
   }
}
