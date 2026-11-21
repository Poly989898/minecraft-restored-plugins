package team.unnamed.creative.metadata.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface NineSliceGuiScaling extends GuiScaling {
   int width();

   int height();

   @NotNull GuiBorder border();

   boolean stretchInner();
}
