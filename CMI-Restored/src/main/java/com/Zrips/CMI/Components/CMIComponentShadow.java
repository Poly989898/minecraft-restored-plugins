package com.Zrips.CMI.Components;

import java.awt.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public class CMIComponentShadow {
  @NotNull
  public static Component applyShadow(Component component, Color color) {
    return component.style((Style) component.style().shadowColor(CMIARGBLike.fromColor(color)));
  }
}
