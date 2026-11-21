package io.lumine.mythic.core.glow;

import io.lumine.mythic.bukkit.utils.serialize.Chroma;

public enum GlowColor {
   BLACK("black"),
   DARK_BLUE("dark_blue"),
   DARK_GREEN("dark_green"),
   DARK_AQUA("dark_aqua"),
   DARK_RED("dark_red"),
   DARK_PURPLE("dark_purple"),
   GOLD("gold"),
   GRAY("gray"),
   DARK_GRAY("dark_gray"),
   BLUE("blue"),
   GREEN("green"),
   AQUA("aqua"),
   RED("red"),
   LIGHT_PURPLE("light_purple"),
   YELLOW("yellow"),
   WHITE("white");

   private final String name;

   private GlowColor(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public String teamName() {
      return "mythic_" + this.name;
   }

   public static final GlowColor of(Chroma chroma) {
      return valueOf(chroma.toBukkitDyeColor().toString());
   }
}
