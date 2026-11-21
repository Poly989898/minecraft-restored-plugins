package team.unnamed.creative.metadata.overlays;

import java.util.Objects;
import java.util.regex.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.pack.PackFormat;

record OverlayEntryImpl(PackFormat formats, String directory) implements OverlayEntry {
   @RegExp
   static final String DIRECTORY_PATTERN = "[a-z0-9-_]+";
   private static final Pattern DIRECTORY_COMPILED_PATTERN = Pattern.compile("[a-z0-9-_]+");

   OverlayEntryImpl(final @NotNull PackFormat formats, @OverlayEntry.Directory final @NotNull String directory) {
      this.formats = (PackFormat)Objects.requireNonNull(formats, "formats");
      this.directory = (String)Objects.requireNonNull(directory, "directory");
      if (!DIRECTORY_COMPILED_PATTERN.matcher(directory).matches()) {
         throw new IllegalArgumentException("Invalid directory name: '" + directory + "' must match pattern: [a-z0-9-_]+");
      }
   }

   public @NotNull PackFormat formats() {
      return this.formats;
   }

   public @NotNull String directory() {
      return this.directory;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{formats=" + String.valueOf(this.formats) + ", directory=" + this.directory + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         OverlayEntryImpl that = (OverlayEntryImpl)o;
         return !this.formats.equals(that.formats) ? false : this.directory.equals(that.directory);
      } else {
         return false;
      }
   }
}
