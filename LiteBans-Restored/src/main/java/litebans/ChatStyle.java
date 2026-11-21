package litebans;

import java.awt.Color;

public final class ChatStyle implements Cloneable {
   private ColorTranslator colorTranslator;
   private Color rgbColor;
   private String font;
   private Boolean bold;
   private Boolean italic;
   private Boolean underlined;
   private Boolean strikethrough;
   private Boolean obfuscated;

   public ColorTranslator getColor() {
      return this.colorTranslator;
   }

   public boolean hasColor() {
      return this.colorTranslator != null;
   }

   public Color getRgbColor() {
      return this.rgbColor;
   }

   public boolean hasRgbColor() {
      return this.rgbColor != null;
   }

   public String getFont() {
      return this.font;
   }

   public boolean hasFont() {
      return this.font != null;
   }

   public boolean isBold() {
      return this.bold != null && this.bold;
   }

   public Boolean getBold() {
      return this.bold;
   }

   public boolean isItalic() {
      return this.italic != null && this.italic;
   }

   public Boolean getItalic() {
      return this.italic;
   }

   public boolean isUnderlined() {
      return this.underlined != null && this.underlined;
   }

   public Boolean getUnderlined() {
      return this.underlined;
   }

   public boolean isStrikethrough() {
      return this.strikethrough != null && this.strikethrough;
   }

   public Boolean getStrikethrough() {
      return this.strikethrough;
   }

   public boolean isObfuscated() {
      return this.obfuscated != null && this.obfuscated;
   }

   public Boolean getObfuscated() {
      return this.obfuscated;
   }

   public ChatStyle copy() {
      return new ChatStyle(this.colorTranslator, this.rgbColor, this.font, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated);
   }

   public static KotlinHelper3 obtainKotlinHelper() {
      return new KotlinHelper3();
   }

   public void setColor(ColorTranslator colorTranslator) {
      this.colorTranslator = colorTranslator;
   }

   public void setRgbColor(Color color) {
      this.rgbColor = color;
   }

   public void setFont(String text) {
      this.font = text;
   }

   public void setBold(Boolean flag) {
      this.bold = flag;
   }

   public void setItalic(Boolean flag) {
      this.italic = flag;
   }

   public void setUnderlined(Boolean flag) {
      this.underlined = flag;
   }

   public void setStrikethrough(Boolean flag) {
      this.strikethrough = flag;
   }

   public void setObfuscated(Boolean flag) {
      this.obfuscated = flag;
   }

   public ChatStyle(ColorTranslator colorTranslator, Color color, String text, Boolean flag, Boolean flag2, Boolean flag3, Boolean flag4, Boolean flag5) {
      this.colorTranslator = colorTranslator;
      this.rgbColor = color;
      this.font = text;
      this.bold = flag;
      this.italic = flag2;
      this.underlined = flag3;
      this.strikethrough = flag4;
      this.obfuscated = flag5;
   }

   public ChatStyle() {
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof ChatStyle)) {
         return false;
      }

      ChatStyle chatStyle = (ChatStyle)obj;
      Boolean flag = this.bold;
      Boolean flag2 = chatStyle.bold;
      if (flag == null ? flag2 == null : flag.equals(flag2)) {
         Boolean flag3 = this.italic;
         Boolean flag4 = chatStyle.italic;
         if (flag3 == null ? flag4 == null : flag3.equals(flag4)) {
            Boolean flag5 = this.underlined;
            Boolean flag6 = chatStyle.underlined;
            if (flag5 == null ? flag6 == null : flag5.equals(flag6)) {
               Boolean flag7 = this.strikethrough;
               Boolean flag8 = chatStyle.strikethrough;
               if (flag7 == null ? flag8 == null : flag7.equals(flag8)) {
                  Boolean flag9 = this.obfuscated;
                  Boolean flag10 = chatStyle.obfuscated;
                  if (flag9 == null ? flag10 == null : flag9.equals(flag10)) {
                     ColorTranslator color = this.getColor();
                     ColorTranslator color2 = chatStyle.getColor();
                     if (color == null ? color2 == null : color.equals(color2)) {
                        Color rgbColor = this.getRgbColor();
                        Color rgbColor2 = chatStyle.getRgbColor();
                        if (rgbColor == null ? rgbColor2 == null : rgbColor.equals(rgbColor2)) {
                           String font = this.getFont();
                           String font2 = chatStyle.getFont();
                           return font == null ? font2 == null : font.equals(font2);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      byte b = 1;
      Boolean flag = this.bold;
      int value = b * 59 + (flag == null ? 43 : flag.hashCode());
      Boolean flag2 = this.italic;
      int value2 = value * 59 + (flag2 == null ? 43 : flag2.hashCode());
      Boolean flag3 = this.underlined;
      int value3 = value2 * 59 + (flag3 == null ? 43 : flag3.hashCode());
      Boolean flag4 = this.strikethrough;
      int value4 = value3 * 59 + (flag4 == null ? 43 : flag4.hashCode());
      Boolean flag5 = this.obfuscated;
      int value5 = value4 * 59 + (flag5 == null ? 43 : flag5.hashCode());
      ColorTranslator color = this.getColor();
      int value6 = value5 * 59 + (color == null ? 43 : color.hashCode());
      Color rgbColor = this.getRgbColor();
      int value7 = value6 * 59 + (rgbColor == null ? 43 : rgbColor.hashCode());
      String font = this.getFont();
      return value7 * 59 + (font == null ? 43 : font.hashCode());
   }

   @Override
   public Object clone() {
      return this.copy();
   }
}
