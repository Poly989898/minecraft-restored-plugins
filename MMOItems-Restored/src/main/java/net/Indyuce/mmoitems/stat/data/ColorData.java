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

   public ColorData(String var1) {
      String[] var2 = var1.split(" ");
      Validate.isTrue(var2.length > 2, "Must specify 3 numbers for red, green and blue");
      this.red = Math.min(255, Math.max(0, Integer.parseInt(var2[0])));
      this.green = Math.min(255, Math.max(0, Integer.parseInt(var2[1])));
      this.blue = Math.min(255, Math.max(0, Integer.parseInt(var2[2])));
   }

   public ColorData(Color var1) {
      this(var1.getRed(), var1.getGreen(), var1.getBlue());
   }

   public ColorData(int var1, int var2, int var3) {
      this.red = var1;
      this.green = var2;
      this.blue = var3;
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

   public ColorData randomize(MMOItemBuilder var1) {
      return this;
   }
}
