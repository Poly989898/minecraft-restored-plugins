package team.unnamed.creative.metadata.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface GuiScaling {
   static @NotNull StretchGuiScaling stretch() {
      return StretchGuiScalingImpl.INSTANCE;
   }

   static @NotNull TileGuiScaling tile(int width, int height) {
      return new TileGuiScalingImpl(width, height);
   }

   static @NotNull NineSliceGuiScaling nineSlice(int width, int height, @NotNull GuiBorder border, boolean stretchInner) {
      return new NineSliceGuiScalingImpl(width, height, border, stretchInner);
   }

   static @NotNull NineSliceGuiScaling nineSlice(int width, int height, int border, boolean stretchInner) {
      return new NineSliceGuiScalingImpl(width, height, GuiBorder.border(border), stretchInner);
   }
}
