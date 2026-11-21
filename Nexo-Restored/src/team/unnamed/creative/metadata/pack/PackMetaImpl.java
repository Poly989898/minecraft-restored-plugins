package team.unnamed.creative.metadata.pack;

import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.MetadataPart;

record PackMetaImpl(PackFormat format, Component description) implements PackMeta {
   PackMetaImpl(final @NotNull PackFormat format, final @NotNull Component description) {
      this.format = (PackFormat)Objects.requireNonNull(format, "format");
      this.description = (Component)Objects.requireNonNull(description, "description");
   }

   public @NotNull Class<? extends MetadataPart> type() {
      return PackMeta.class;
   }

   public @NotNull PackFormat formats() {
      return this.format;
   }

   public @NotNull Component description() {
      return this.description;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{format=" + String.valueOf(this.format) + ", description=" + String.valueOf(this.description) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PackMetaImpl packMeta = (PackMetaImpl)o;
         return this.format.equals(packMeta.format) && this.description.equals(packMeta.description);
      } else {
         return false;
      }
   }
}
