package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatTextComponent extends ChatBaseComponent {
   private static final Pattern PATTERN = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/[a-z0-9]*)?$");
   private String text3;

   @Deprecated
   public static ChatBaseComponent[] fromLegacyText(String text) {
      return fromLegacyText(text, ColorTranslator.WHITE);
   }

   @Deprecated
   public static ChatBaseComponent[] fromLegacyText(String text, ColorTranslator colorTranslator) {
      ArrayList arrayList = new ArrayList();
      parseLegacyText(text, colorTranslator, arrayList::add);
      return (ChatBaseComponent[]) arrayList.toArray(new ChatBaseComponent[0]);
   }

   private static final void parseLegacyText(String text, ColorTranslator colorTranslator, Consumer consumer) {
      StringBuilder stringBuilder = new StringBuilder();
      ChatTextComponent chatTextComponent = new ChatTextComponent();
      Matcher matcher2 = PATTERN.matcher(text);

      for (int i = 0; i < text.length(); i++) {
         char charAt2 = text.charAt(i);
         if (charAt2 == 167) {
            if (++i >= text.length()) {
               break;
            }

            char charAt3 = text.charAt(i);
            if (charAt3 >= 'A' && charAt3 <= 'Z') {
               charAt3 = (char)(charAt3 + ' ');
            }

            ColorTranslator colorTranslator2;
            if (charAt3 == 'x' && i + 12 < text.length()) {
               StringBuilder stringBuilder2 = new StringBuilder("#");

               for (int i2 = 0; i2 < 6; i2++) {
                  stringBuilder2.append(text.charAt(i + 2 + i2 * 2));
               }

               try {
                  colorTranslator2 = ColorTranslator.computeColorTranslator(stringBuilder2.toString());
               } catch (IllegalArgumentException e) {
                  colorTranslator2 = null;
               }

               i += 12;
            } else {
               colorTranslator2 = ColorTranslator.computeColorTranslator(charAt3);
            }

            if (colorTranslator2 != null) {
               if (stringBuilder.length() > 0) {
                  ChatTextComponent chatTextComponent2 = chatTextComponent;
                  chatTextComponent = new ChatTextComponent(chatTextComponent2);
                  chatTextComponent2.setText3(stringBuilder.toString());
                  stringBuilder = new StringBuilder();
                  consumer.accept(chatTextComponent2);
               }

               if (colorTranslator2 == ColorTranslator.BOLD) {
                  chatTextComponent.setBold(true);
               } else if (colorTranslator2 == ColorTranslator.ITALIC) {
                  chatTextComponent.setItalic(true);
               } else if (colorTranslator2 == ColorTranslator.UNDERLINE) {
                  chatTextComponent.setUnderlined(true);
               } else if (colorTranslator2 == ColorTranslator.STRIKETHROUGH) {
                  chatTextComponent.setStrikethrough(true);
               } else if (colorTranslator2 == ColorTranslator.OBFUSCATED) {
                  chatTextComponent.setObfuscated(true);
               } else {
                  if (colorTranslator2 == ColorTranslator.RESET) {
                     colorTranslator2 = colorTranslator;
                  }

                  chatTextComponent = new ChatTextComponent();
                  chatTextComponent.setColor(colorTranslator2);
                  chatTextComponent.setFlag2(true);
               }
            }
         } else {
            int indexOfUrlEnd2 = indexOfUrlEnd(text, i);
            if (indexOfUrlEnd2 == -1) {
               indexOfUrlEnd2 = text.length();
            }

            if (matcher2.region(i, indexOfUrlEnd2).find()) {
               if (stringBuilder.length() > 0) {
                  ChatTextComponent chatTextComponent3 = chatTextComponent;
                  chatTextComponent = new ChatTextComponent(chatTextComponent3);
                  chatTextComponent3.setText3(stringBuilder.toString());
                  stringBuilder = new StringBuilder();
                  consumer.accept(chatTextComponent3);
               }

               ChatTextComponent chatTextComponent4 = chatTextComponent;
               ChatTextComponent chatTextComponent5 = new ChatTextComponent(chatTextComponent4);
               String stripColor2 = LiteColor.stripColor(text.substring(i, indexOfUrlEnd2));
               chatTextComponent5.setText3(stripColor2);
               chatTextComponent5.setClickEvent(new ChatClickEvent(ClickAction.OPEN_URL, stripColor2.startsWith("http") ? stripColor2 : "http://" + stripColor2));
               consumer.accept(chatTextComponent5);
               i += indexOfUrlEnd2 - i - 1;
               chatTextComponent = chatTextComponent4;
            } else {
               stringBuilder.append(charAt2);
            }
         }
      }

      chatTextComponent.setText3(stringBuilder.toString());
      consumer.accept(chatTextComponent);
   }

   private static final int indexOfUrlEnd(String text, int value) {
      for (int i = value; i < text.length(); i++) {
         char charAt2 = text.charAt(i);
         if (charAt2 == ' ' || Character.isISOControl(charAt2)) {
            return i;
         }
      }

      return -1;
   }

   public ChatTextComponent() {
      this.text3 = "";
   }

   public ChatTextComponent(ChatTextComponent chatTextComponent) {
      super(chatTextComponent);
      this.setText3(chatTextComponent.getText3());
   }

   public ChatTextComponent(ChatBaseComponent[] chatBaseComponents) {
      this();
      if (chatBaseComponents.length != 0) {
         this.setExtra(new ArrayList<>(Arrays.asList(chatBaseComponents)));
      }
   }

   public ChatTextComponent duplicate() {
      return new ChatTextComponent(this);
   }

   @Override
   protected void appendPlainText(StringBuilder builder) {
      this.appendLegacyText(builder);
      builder.append(this.text3);
      super.appendPlainText(builder);
   }

   @Override
   public String toString() {
      return "TextComponent{text=" + this.text3 + ", " + super.toString() + '}';
   }

   public String getText3() {
      return this.text3;
   }

   public void setText3(String text) {
      this.text3 = text;
   }

   public ChatTextComponent(String text) {
      this.text3 = text;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof ChatTextComponent)) {
         return false;
      }

      ChatTextComponent chatTextComponent = (ChatTextComponent)obj;
      if (!chatTextComponent.checkObj(this)) {
         return false;
      }

      if (!super.equals(obj)) {
         return false;
      }

      String text = this.getText3();
      String text2 = chatTextComponent.getText3();
      return text == null ? text2 == null : text.equals(text2);
   }

   @Override
   protected boolean checkObj(Object obj) {
      return obj instanceof ChatTextComponent;
   }

   @Override
   public int hashCode() {
      int value = super.hashCode();
      String text = this.getText3();
      return value * 59 + (text == null ? 43 : text.hashCode());
   }

   @Override
   public ChatBaseComponent obtainChatBaseComponent() {
      return this.duplicate();
   }
}
