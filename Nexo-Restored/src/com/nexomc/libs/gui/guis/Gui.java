package com.nexomc.libs.gui.guis;

import com.nexomc.libs.gui.builder.gui.ChestGuiBuilder;
import com.nexomc.libs.gui.builder.gui.PaginatedBuilder;
import com.nexomc.libs.gui.builder.gui.ScrollingBuilder;
import com.nexomc.libs.gui.builder.gui.StorageBuilder;
import com.nexomc.libs.gui.builder.gui.TypedGuiBuilder;
import com.nexomc.libs.gui.components.GuiContainer;
import com.nexomc.libs.gui.components.GuiType;
import com.nexomc.libs.gui.components.InteractionModifier;
import com.nexomc.libs.gui.components.ScrollType;
import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Gui extends BaseGui {
   public Gui(@NotNull GuiContainer guiContainer, @NotNull Set<InteractionModifier> interactionModifiers) {
      super(guiContainer, interactionModifiers);
   }

   @Contract("_ -> new")
   public static @NotNull TypedGuiBuilder gui(@NotNull GuiType type) {
      return new TypedGuiBuilder(type);
   }

   @Contract(" -> new")
   public static @NotNull ChestGuiBuilder gui() {
      return new ChestGuiBuilder();
   }

   @Contract(" -> new")
   public static @NotNull StorageBuilder storage() {
      return new StorageBuilder();
   }

   @Contract(" -> new")
   public static @NotNull PaginatedBuilder paginated() {
      return new PaginatedBuilder();
   }

   @Contract("_ -> new")
   public static @NotNull ScrollingBuilder scrolling(@NotNull ScrollType scrollType) {
      return new ScrollingBuilder(scrollType);
   }

   @Contract(" -> new")
   public static @NotNull ScrollingBuilder scrolling() {
      return scrolling(ScrollType.VERTICAL);
   }
}
