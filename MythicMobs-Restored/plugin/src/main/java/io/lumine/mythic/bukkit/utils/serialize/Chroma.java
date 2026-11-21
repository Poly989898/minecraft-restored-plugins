package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

public class Chroma implements GsonSerializable {
   private static final double MAX_EUCLIDEAN_DISTANCE = Math.sqrt(195075.0);
   private final int rgb;
   private final int rgba;

   public static Chroma deserialize(String element) {
      Preconditions.checkNotNull(element);
      int r = Integer.parseInt(element);
      return of(r);
   }

   public static Chroma of(int rgba) {
      return new Chroma(rgba);
   }

   public static Chroma of(int r, int g, int b) {
      return new Chroma(r, g, b, 255);
   }

   public static Chroma of(int r, int g, int b, int a) {
      return new Chroma(r, g, b, a);
   }

   public static Chroma of(float r, float g, float b) {
      return new Chroma((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), 65025);
   }

   public static Chroma of(float r, float g, float b, float a) {
      return new Chroma((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), (int)(a * 255.0F));
   }

   public static Chroma of(DyeColor dyeColor) {
      return of(dyeColor.getColor());
   }

   public static Chroma of(Color bukkitColor) {
      int red = bukkitColor.asRGB() >> 16 & 0xFF;
      int green = bukkitColor.asRGB() >> 8 & 0xFF;
      int blue = bukkitColor.asRGB() & 0xFF;
      return of(red, green, blue);
   }

   public static Chroma of(String input) {
      int r;
      int g;
      int b;
      int a;
      if (input.startsWith("#")) {
         if (input.length() == 9) {
            long val = Long.parseLong(input.substring(1), 16);
            r = (int)(val >> 24) & 0xFF;
            g = (int)(val >> 16) & 0xFF;
            b = (int)(val >> 8) & 0xFF;
            a = (int)val & 0xFF;
         } else {
            java.awt.Color color = java.awt.Color.decode(input);
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            a = 255;
         }
      } else if (input.contains(",")) {
         String[] rgb = input.split(",");
         r = Integer.parseInt(rgb[0]);
         g = Integer.parseInt(rgb[1]);
         b = Integer.parseInt(rgb[2]);
         if (rgb.length > 3) {
            float alpha = Float.parseFloat(rgb[3].trim());
            if (alpha > 0.0F && alpha < 1.0F) {
               a = (int)(alpha * 255.0F);
            } else {
               a = (int)alpha;
            }
         } else {
            a = 255;
         }
      } else {
         Optional<Integer> maybeInt = Numbers.parseIntegerOpt(input);
         if (maybeInt.isPresent()) {
            return of(maybeInt.get());
         }

         DyeColor dColor = DyeColor.valueOf(input.toUpperCase());
         Color color = dColor.getFireworkColor();
         r = color.getRed();
         g = color.getGreen();
         b = color.getBlue();
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_19_4)) {
            a = color.getAlpha();
         } else {
            a = 255;
         }
      }

      return of(r, g, b, a);
   }

   private Chroma(int minecraftRGB) {
      int r = minecraftRGB >> 16 & 0xFF;
      int g = minecraftRGB >> 8 & 0xFF;
      int b = minecraftRGB & 0xFF;
      int a = 255;
      this.rgba = r << 24 | g << 16 | b << 8 | a;
      this.rgb = r << 16 | g << 8 | b;
   }

   private Chroma(int r, int g, int b, int a) {
      this.rgba = (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | a & 0xFF;
      this.rgb = (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
   }

   public Chroma(int r, int g, int b, float alpha) {
      if (alpha < 0.0F) {
         alpha = 0.0F;
      }

      if (alpha > 1.0F) {
         alpha = 1.0F;
      }

      int a = (int)(alpha * 255.0F) & 0xFF;
      this.rgba = (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | a & 0xFF;
      this.rgb = (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
   }

   public int value() {
      return this.rgba;
   }

   public int red() {
      return this.value() >> 24 & 0xFF;
   }

   public int green() {
      return this.value() >> 16 & 0xFF;
   }

   public int blue() {
      return this.value() >> 8 & 0xFF;
   }

   public int alpha() {
      return this.value() & 0xFF;
   }

   public Color toBukkitColor() {
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_19_4)) {
         int argb = (this.alpha() & 0xFF) << 24 | (this.red() & 0xFF) << 16 | (this.green() & 0xFF) << 8 | this.blue() & 0xFF;
         return Color.fromARGB(argb);
      } else {
         return Color.fromRGB(this.rgb);
      }
   }

   public Color toSolidBukkitColor() {
      return Color.fromRGB(this.rgb);
   }

   public DyeColor toBukkitDyeColor() {
      return DyeColor.getByColor(this.toSolidBukkitColor());
   }

   public TextColor toComponentColor() {
      return TextColor.color(this.rgb);
   }

   public String toHexString() {
      return String.format("#%06x", this.rgb);
   }

   public String toAlphaHexString() {
      return String.format("#%08x", this.value());
   }

   public Chroma blend(Chroma with, float i) {
      float clampedT = Math.min(1.0F, Math.max(0.0F, i));
      int ar = this.red();
      int ag = this.green();
      int ab = this.blue();
      int aa = this.alpha();
      int br = with.red();
      int bg = with.green();
      int bb = with.blue();
      int ba = with.alpha();
      return of(
         Math.round(ar + clampedT * (br - ar)),
         Math.round(ag + clampedT * (bg - ag)),
         Math.round(ab + clampedT * (bb - ab)),
         Math.round(aa + clampedT * (ba - aa))
      );
   }

   public double getDistance(Chroma other) {
      int rDiff = this.red() - other.red();
      int gDiff = this.green() - other.green();
      int bDiff = this.blue() - other.blue();
      return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
   }

   public int getDifference(Chroma other) {
      int rDiff = Math.abs(this.red() - other.red());
      int gDiff = Math.abs(this.green() - other.green());
      int bDiff = Math.abs(this.blue() - other.blue());
      return rDiff + gDiff + bDiff;
   }

   public Chroma constrainedBlend(Chroma with, int maxDifference) {
      int totalDifference = this.getDifference(with);
      float i = 1.0F;
      if (totalDifference > maxDifference) {
         i = (float)maxDifference / totalDifference;
      }

      float clampedT = Math.min(1.0F, Math.max(0.0F, i));
      int ar = this.red();
      int ag = this.green();
      int ab = this.blue();
      int aa = this.alpha();
      int br = with.red();
      int bg = with.green();
      int bb = with.blue();
      int ba = with.alpha();
      return of(
         Math.round(ar + clampedT * (br - ar)),
         Math.round(ag + clampedT * (bg - ag)),
         Math.round(ab + clampedT * (bb - ab)),
         Math.round(aa + clampedT * (ba - aa))
      );
   }

   public Chroma nearest(Collection<Chroma> chromas) {
      double minDifference = Double.MAX_VALUE;
      Chroma nearestChroma = null;

      for (Chroma otherChroma : chromas) {
         double diff = this.getDistance(otherChroma);
         if (diff < minDifference) {
            minDifference = diff;
            nearestChroma = otherChroma;
         }
      }

      return nearestChroma;
   }

   public DyeColor bestDyeTowards(Chroma target) {
      Chroma.Dyes bestDye = null;
      Chroma bestBlend = null;
      double smallestDifference = Double.MAX_VALUE;

      for (Chroma.Dyes dye : Chroma.Dyes.collection()) {
         Chroma blendedColor = this.blend(dye.getBlendingColor(), 1.0F);
         double newDifference = blendedColor.getDistance(target);
         if (newDifference < smallestDifference) {
            smallestDifference = newDifference;
            bestDye = dye;
         }
      }

      return bestDye.getDyeColor();
   }

   public Map<DyeColor, Integer> getDyesTowards(Chroma targetColor, double threshold, int maxIterations) {
      Map<DyeColor, Integer> result = new HashMap<>();
      double currentDifference = this.getDistance(targetColor);
      if (currentDifference <= 0.0) {
         return result;
      } else {
         Chroma currentColor = this;

         for (int i = 0; i < maxIterations; i++) {
            Chroma.Dyes bestDye = null;
            Chroma bestBlend = null;
            double smallestDifference = Double.MAX_VALUE;

            for (Chroma.Dyes dye : Chroma.Dyes.collection()) {
               Chroma blendedColor = currentColor.blend(dye.getBlendingColor(), 0.5F);
               double newDifference = blendedColor.getDistance(targetColor);
               if (newDifference < smallestDifference) {
                  smallestDifference = newDifference;
                  bestDye = dye;
                  bestBlend = blendedColor;
               }
            }

            if (bestDye == null) {
               break;
            }

            currentColor = bestBlend;
            result.put(bestDye.getDyeColor(), result.getOrDefault(bestDye.getDyeColor(), 0) + 1);
            double similarity = 1.0 - smallestDifference / MAX_EUCLIDEAN_DISTANCE;
            if (similarity >= threshold) {
               break;
            }
         }

         return result;
      }
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().add("color", this.rgba).build();
   }

   public String serializeShortForm() {
      return String.format("#%08x", this.value());
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof Chroma other)
            ? false
            : Double.compare(this.red(), other.red()) == 0
               && Double.compare(this.green(), other.green()) == 0
               && Double.compare(this.blue(), other.blue()) == 0
               && Double.compare(this.alpha(), other.alpha()) == 0;
      }
   }

   @Override
   public int hashCode() {
      return this.value();
   }

   @Override
   public String toString() {
      return "Chroma(r=" + this.red() + ", g=" + this.green() + ", b=" + this.blue() + ", a=" + this.alpha() + ")";
   }

   public static enum Dyes {
      WHITE(Chroma.of("#F9FFFE"), Chroma.of("#FFFFFE"), DyeColor.WHITE),
      LIGHT_GRAY(Chroma.of("#9D9D97"), Chroma.of("#AAAAAA"), DyeColor.LIGHT_GRAY),
      GRAY(Chroma.of("#474F52"), Chroma.of("#666666"), DyeColor.GRAY),
      BLACK(Chroma.of("#1D1D21"), Chroma.of("#000000"), DyeColor.BLACK),
      BROWN(Chroma.of("#835432"), Chroma.of("#8B4513"), DyeColor.BROWN),
      RED(Chroma.of("#B02E26"), Chroma.of("#FF0000"), DyeColor.RED),
      ORANGE(Chroma.of("#F9801D"), Chroma.of("#FFA500"), DyeColor.ORANGE),
      YELLOW(Chroma.of("#FED83D"), Chroma.of("#FFFF00"), DyeColor.YELLOW),
      LIME(Chroma.of("#80C71F"), Chroma.of("#00FF00"), DyeColor.LIME),
      GREEN(Chroma.of("#5E7C16"), Chroma.of("#008000"), DyeColor.GREEN),
      CYAN(Chroma.of("#169C9C"), Chroma.of("#00FFFF"), DyeColor.CYAN),
      LIGHT_BLUE(Chroma.of("#3AB3DA"), Chroma.of("#ADD8E6"), DyeColor.LIGHT_BLUE),
      BLUE(Chroma.of("#3C44AA"), Chroma.of("#0000FF"), DyeColor.BLUE),
      PURPLE(Chroma.of("#8932B8"), Chroma.of("#800080"), DyeColor.PURPLE),
      MAGENTA(Chroma.of("#C74EBD"), Chroma.of("#FF00FF"), DyeColor.MAGENTA),
      PINK(Chroma.of("#F38BAA"), Chroma.of("#FFC0CB"), DyeColor.PINK);

      private final Chroma color;
      private final Chroma blendingColor;
      private final DyeColor dyeColor;

      private Dyes(Chroma color, Chroma blendingColor, DyeColor dyeColor) {
         this.color = color;
         this.blendingColor = blendingColor;
         this.dyeColor = dyeColor;
      }

      public static List<Chroma.Dyes> collection() {
         return Collections.unmodifiableList(Arrays.asList(values()));
      }

      public Chroma getColor() {
         return this.color;
      }

      public Chroma getBlendingColor() {
         return this.blendingColor;
      }

      public DyeColor getDyeColor() {
         return this.dyeColor;
      }
   }
}
