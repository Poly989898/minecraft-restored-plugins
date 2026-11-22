package com.Zrips.CMI.Components;

import java.awt.Color;
import net.kyori.adventure.util.ARGBLike;
import org.jetbrains.annotations.Range;

public class CMIARGBLike implements ARGBLike {
  int alpha = 0;
  int red = 0;
  int green = 0;
  int blue = 0;

  public CMIARGBLike(Color color) {
    this.alpha = color.getAlpha();
    this.red = color.getRed();
    this.green = color.getGreen();
    this.blue = color.getBlue();
  }

  public static CMIARGBLike fromColor(Color color) {
    return new CMIARGBLike(color);
  }

  public @Range(from = 0L, to = 255L) int blue() {
    return this.blue;
  }

  public @Range(from = 0L, to = 255L) int green() {
    return this.green;
  }

  public @Range(from = 0L, to = 255L) int red() {
    return this.red;
  }

  public @Range(from = 0L, to = 255L) int alpha() {
    return this.alpha;
  }
}
