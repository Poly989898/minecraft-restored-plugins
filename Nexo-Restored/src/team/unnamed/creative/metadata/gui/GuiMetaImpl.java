package team.unnamed.creative.metadata.gui;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.metadata.MetadataPart;

record GuiMetaImpl(GuiScaling scaling) implements GuiMeta {
   GuiMetaImpl(final @NotNull GuiScaling scaling) {
      this.scaling = (GuiScaling)Objects.requireNonNull(scaling, "scaling");
   }

   public @NotNull Class<? extends MetadataPart> type() {
      return GuiMeta.class;
   }

   public @NotNull GuiScaling scaling() {
      return this.scaling;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{scaling=" + String.valueOf(this.scaling) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         GuiMetaImpl that = (GuiMetaImpl)o;
         return this.scaling.equals(that.scaling);
      } else {
         return false;
      }
   }
}
