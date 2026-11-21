package team.unnamed.creative.metadata.overlays;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import team.unnamed.creative.metadata.pack.PackFormat;

@NonExtendable
public interface OverlayEntry {
   @NotNull PackFormat formats();

   @Subst("dir")
   @NotNull String directory();

   static @NotNull OverlayEntry of(@NotNull PackFormat formats, @Subst("dir") @OverlayEntry.Directory @NotNull String directory) {
      return new OverlayEntryImpl(formats, directory);
   }

   @Retention(RetentionPolicy.CLASS)
   @Documented
   @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
   @Pattern("[a-z0-9-_]+")
   @NonNls
   public @interface Directory {
   }
}
