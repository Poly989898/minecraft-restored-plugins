package com.nexomc.libs.gui.builder.gui;

import com.nexomc.libs.gui.components.GuiType;
import com.nexomc.libs.gui.guis.Gui;
import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ChestGuiBuilder extends BaseChestGuiBuilder<Gui, ChestGuiBuilder> {
   @Contract("_ -> new")
   public @NotNull TypedGuiBuilder type(@NotNull GuiType guiType) {
      return new TypedGuiBuilder(guiType, this);
   }

   @Contract(" -> new")
   public @NotNull Gui create() {
      Gui gui = new Gui(this.createContainer(), this.getModifiers());
      Consumer<Gui> consumer = this.getConsumer();
      if (consumer != null) {
         consumer.accept(gui);
      }

      return gui;
   }
}
