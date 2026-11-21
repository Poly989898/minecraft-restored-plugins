package litebans;

import java.awt.Color;

public final class KotlinHelper3 {
   private ColorTranslator colorTranslator;
   private Color color;
   private String font;
   private Boolean bold;
   private Boolean italic;
   private Boolean underlined;
   private Boolean strikethrough;
   private Boolean obfuscated;

   public KotlinHelper3 color(ColorTranslator colorTranslator) {
      this.colorTranslator = colorTranslator;
      return this;
   }

   public KotlinHelper3 rgb(Color color) {
      this.color = color;
      return this;
   }

   public KotlinHelper3 font(String text) {
      this.font = text;
      return this;
   }

   public KotlinHelper3 bold(Boolean flag) {
      this.bold = flag;
      return this;
   }

   public KotlinHelper3 italic(Boolean flag) {
      this.italic = flag;
      return this;
   }

   public KotlinHelper3 underlined(Boolean flag) {
      this.underlined = flag;
      return this;
   }

   public KotlinHelper3 strikethrough(Boolean flag) {
      this.strikethrough = flag;
      return this;
   }

   public KotlinHelper3 obfuscated(Boolean flag) {
      this.obfuscated = flag;
      return this;
   }

   public ChatStyle build() {
      return new ChatStyle(this.colorTranslator, this.color, this.font, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated);
   }
}
