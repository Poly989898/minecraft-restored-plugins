package com.nexomc.libs.gui.builder.gui;

import com.nexomc.libs.gui.components.ScrollType;
import com.nexomc.libs.gui.guis.ScrollingGui;
import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ScrollingBuilder extends BaseChestGuiBuilder<ScrollingGui, ScrollingBuilder> {
   private ScrollType scrollType;
   private int pageSize = 0;

   public ScrollingBuilder(@NotNull ScrollType scrollType) {
      this.scrollType = scrollType;
   }

   @Contract("_ -> this")
   public @NotNull ScrollingBuilder scrollType(@NotNull ScrollType scrollType) {
      this.scrollType = scrollType;
      return this;
   }

   @Contract("_ -> this")
   public @NotNull ScrollingBuilder pageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
   }

   @Contract(" -> new")
   public @NotNull ScrollingGui create() {
      ScrollingGui gui = new ScrollingGui(this.createContainer(), this.pageSize, this.scrollType, this.getModifiers());
      Consumer<ScrollingGui> consumer = this.getConsumer();
      if (consumer != null) {
         consumer.accept(gui);
      }

      return gui;
   }
}
