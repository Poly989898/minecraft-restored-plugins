package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;

public class ColorData implements StatData, RandomStatData<ColorData> {
   private final int red;
   private final int green;
   private final int blue;

   public ColorData(String text) {
      String[] texts = text.split(" ");
      Validate.isTrue(texts.length > 2, "Must specify 3 numbers for red, green and blue");
      this.red = Math.min(255, Math.max(0, Integer.parseInt(texts[0])));
      this.green = Math.min(255, Math.max(0, Integer.parseInt(texts[1])));
      this.blue = Math.min(255, Math.max(0, Integer.parseInt(texts[2])));
   }

   public ColorData(Color color) {
      this(color.getRed(), color.getGreen(), color.getBlue());
   }

   public ColorData(int count, int count2, int count3) {
      this.red = count;
      this.green = count2;
      this.blue = count3;
   }

   public int getRed() {
      return this.red;
   }

   public int getGreen() {
      return this.green;
   }

   public int getBlue() {
      return this.blue;
   }

   public Color getColor() {
      return Color.fromRGB(this.red, this.green, this.blue);
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   public String toString() {
      return "{Red=" + this.red + ",Green=" + this.green + ",Blue=" + this.blue + "}";
   }

   public ColorData randomize(MMOItemBuilder mmoItemBuilder) {
      return this;
   }
}
