package com.nexomc.libs.gui.components;

import org.bukkit.event.Event;

@FunctionalInterface
public interface GuiAction<T extends Event> {
   void execute(T var1);
}
