package com.Zrips.CMI.Components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class CMIComponentDeserializer {
  public static String serialize(@NotNull Component component) {
    return LegacyComponentSerializer.legacySection().serialize(component);
  }
}
