package me.TechsCode.UltraPermissions.base.visual;

import java.util.Arrays;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.legacy.utils.MinecraftVersion;

public class Color {
   private static final String HEX_PATTERN = "(#(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3}))|(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3})";
   private String colorCode;
   private final int r;
   private final int g;
   private final int b;
   private final boolean valid;

   public static Color from(String text) {
      return new Color(text);
   }

   public static Color from(int red, int green, int blue) {
      java.awt.Color color = new java.awt.Color(red, green, blue);
      return from(Integer.toHexString(color.getRGB()).substring(2));
   }

   private Color(String text) {
      if (!text.matches("(#(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3}))|(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3})")) {
         this.valid = false;
         this.r = this.g = this.b = 0;
      } else {
         this.valid = true;
         this.colorCode = text.replace("#", "");
         if (this.colorCode.length() == 3) {
            String[] texts = this.colorCode.split("");
            this.colorCode = texts[0] + texts[0] + texts[1] + texts[1] + texts[2] + texts[2];
         }

         java.awt.Color color = new java.awt.Color(Integer.parseInt(this.colorCode, 16));
         this.r = color.getRed();
         this.g = color.getGreen();
         this.b = color.getBlue();
      }
   }

   public String getTag() {
      return "{#" + this.colorCode + "}";
   }

   public String getColorCode() {
      return this.colorCode;
   }

   public int getRed() {
      return this.r;
   }

   public int getGreen() {
      return this.g;
   }

   public int getBlue() {
      return this.b;
   }

   public boolean isValid() {
      return this.valid;
   }

   public String getAppliedTag() {
      return this.isValid()
         ? (
            hasHexSupport()
               ? "\u00a7x" + Arrays.stream(this.colorCode.split("")).map(text -> "\u00a7" + text).collect(Collectors.joining())
               : MinecraftColor.getClosest(this).getAppliedTag()
         )
         : "";
   }

   public String getColorTag() {
      return "{#" + this.colorCode + "}";
   }

   public static int difference(Color color, Color other) {
      return Math.abs(color.r - other.r) + Math.abs(color.g - other.g) + Math.abs(color.b - other.b);
   }

   public static boolean hasHexSupport() {
      return MinecraftVersion.getServersVersion().isAboveOrEqual(MinecraftVersion.V1_16_R1);
   }

   @Override
   public String toString() {
      return this.getAppliedTag();
   }
}
