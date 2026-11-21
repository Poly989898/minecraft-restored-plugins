package team.unnamed.creative.metadata.gui;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.metadata.MetadataPart;

public interface GuiMeta extends MetadataPart {
   static @NotNull GuiMeta of(@NotNull GuiScaling scaling) {
      return new GuiMetaImpl(scaling);
   }

   @NotNull GuiScaling scaling();
}
