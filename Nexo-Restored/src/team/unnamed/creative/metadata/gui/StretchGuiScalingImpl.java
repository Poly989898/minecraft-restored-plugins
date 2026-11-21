package team.unnamed.creative.metadata.gui;

import org.jetbrains.annotations.NotNull;

final class StretchGuiScalingImpl implements StretchGuiScaling {
   static final StretchGuiScaling INSTANCE = new StretchGuiScalingImpl();

   private StretchGuiScalingImpl() {
   }

   public @NotNull String toString() {
      return "StretchGuiScalingImpl.INSTANCE";
   }
}
