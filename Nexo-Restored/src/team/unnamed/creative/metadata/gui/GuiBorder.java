package team.unnamed.creative.metadata.gui;

import org.jetbrains.annotations.NotNull;

public interface GuiBorder {
   static @NotNull GuiBorder border(int top, int bottom, int left, int right) {
      return new GuiBorderImpl(top, bottom, left, right);
   }

   static @NotNull GuiBorder border(int size) {
      return border(size, size, size, size);
   }

   int top();

   int bottom();

   int left();

   int right();
}
