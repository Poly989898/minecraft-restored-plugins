package litebans;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class ChatBaseComponent {
   ChatBaseComponent chatBaseComponent3;
   private ChatStyle style = new ChatStyle();
   private String insertion;
   private List extra;
   private ChatClickEvent chatClickEvent;
   private HoverEvent hoverEvent;
   private transient boolean flag2;

   @Deprecated
   public ChatBaseComponent() {
   }

   ChatBaseComponent(ChatBaseComponent chatBaseComponent) {
      this.handleChatBaseComponent(chatBaseComponent, ArchiveNoneFormattingEventsEnum.ALL, true);
      if (chatBaseComponent.getExtra() != null) {
         for (ChatBaseComponent chatBaseComponent2 : (Iterable<ChatBaseComponent>) chatBaseComponent.getExtra()) {
            this.addExtra(chatBaseComponent2.obtainChatBaseComponent());
         }
      }
   }

   public void handleChatBaseComponent(ChatBaseComponent chatBaseComponent, ArchiveNoneFormattingEventsEnum value, boolean flag) {
      if (value == ArchiveNoneFormattingEventsEnum.EVENTS || value == ArchiveNoneFormattingEventsEnum.ALL) {
         if (flag || this.chatClickEvent == null) {
            this.setClickEvent(chatBaseComponent.getClickEvent());
         }

         if (flag || this.hoverEvent == null) {
            this.setHoverEvent(chatBaseComponent.getHoverEvent());
         }
      }

      if (value == ArchiveNoneFormattingEventsEnum.FORMATTING || value == ArchiveNoneFormattingEventsEnum.ALL) {
         if (flag || !this.style.hasColor()) {
            this.setColor(chatBaseComponent.getColorRaw());
         }

         if (flag || !this.style.hasRgbColor()) {
            this.handleColor(chatBaseComponent.obtainColor());
         }

         if (flag || !this.style.hasFont()) {
            this.handleText(chatBaseComponent.obtainText());
         }

         if (flag || this.style.getBold() == null) {
            this.setBold(chatBaseComponent.getBoldRaw());
         }

         if (flag || this.style.getItalic() == null) {
            this.setItalic(chatBaseComponent.getItalicRaw());
         }

         if (flag || this.style.getUnderlined() == null) {
            this.setUnderlined(chatBaseComponent.getUnderlinedRaw());
         }

         if (flag || this.style.getStrikethrough() == null) {
            this.setStrikethrough(chatBaseComponent.getStrikethroughRaw());
         }

         if (flag || this.style.getObfuscated() == null) {
            this.setObfuscated(chatBaseComponent.getObfuscatedRaw());
         }

         if (flag || this.insertion == null) {
            this.setInsertion(chatBaseComponent.getInsertion());
         }
      }
   }

   public abstract ChatBaseComponent obtainChatBaseComponent();

   public static String computeText(ChatBaseComponent[] chatBaseComponents) {
      StringBuilder stringBuilder = new StringBuilder();

      for (ChatBaseComponent chatBaseComponent : chatBaseComponents) {
         stringBuilder.append(chatBaseComponent.toPlainText());
      }

      return stringBuilder.toString();
   }

   public void setColor(ColorTranslator colorTranslator) {
      this.style.setColor(colorTranslator);
   }

   public ColorTranslator getColor() {
      if (!this.style.hasColor()) {
         return this.chatBaseComponent3 == null ? ColorTranslator.WHITE : this.chatBaseComponent3.getColor();
      } else {
         return this.style.getColor();
      }
   }

   public ColorTranslator getColorRaw() {
      return this.style.getColor();
   }

   public void handleColor(Color color) {
      this.style.setRgbColor(color);
   }

   public Color obtainColor() {
      return this.style.getRgbColor();
   }

   public void handleText(String text) {
      this.style.setFont(text);
   }

   public String obtainText() {
      return this.style.getFont();
   }

   public void setBold(Boolean flag) {
      this.style.setBold(flag);
   }

   public boolean isBold() {
      return this.style.getBold() != null ? this.style.isBold() : this.chatBaseComponent3 != null && this.chatBaseComponent3.isBold();
   }

   public Boolean getBoldRaw() {
      return this.style.getBold();
   }

   public void setItalic(Boolean flag) {
      this.style.setItalic(flag);
   }

   public boolean isItalic() {
      return this.style.getItalic() != null ? this.style.isItalic() : this.chatBaseComponent3 != null && this.chatBaseComponent3.isItalic();
   }

   public Boolean getItalicRaw() {
      return this.style.getItalic();
   }

   public void setUnderlined(Boolean flag) {
      this.style.setUnderlined(flag);
   }

   public boolean isUnderlined() {
      return this.style.getUnderlined() != null ? this.style.isUnderlined() : this.chatBaseComponent3 != null && this.chatBaseComponent3.isUnderlined();
   }

   public Boolean getUnderlinedRaw() {
      return this.style.getUnderlined();
   }

   public void setStrikethrough(Boolean flag) {
      this.style.setStrikethrough(flag);
   }

   public boolean isStrikethrough() {
      return this.style.getStrikethrough() != null ? this.style.isStrikethrough() : this.chatBaseComponent3 != null && this.chatBaseComponent3.isStrikethrough();
   }

   public Boolean getStrikethroughRaw() {
      return this.style.getStrikethrough();
   }

   public void setObfuscated(Boolean flag) {
      this.style.setObfuscated(flag);
   }

   public boolean isObfuscated() {
      return this.style.getObfuscated() != null ? this.style.isObfuscated() : this.chatBaseComponent3 != null && this.chatBaseComponent3.isObfuscated();
   }

   public Boolean getObfuscatedRaw() {
      return this.style.getObfuscated();
   }

   public void handleChatStyle(ChatStyle chatStyle) {
      if (chatStyle.hasColor()) {
         this.setColor(chatStyle.getColor());
      }

      if (chatStyle.hasRgbColor()) {
         this.handleColor(chatStyle.getRgbColor());
      }

      if (chatStyle.hasFont()) {
         this.handleText(chatStyle.getFont());
      }

      if (chatStyle.getBold() != null) {
         this.setBold(chatStyle.getBold());
      }

      if (chatStyle.getItalic() != null) {
         this.setItalic(chatStyle.getItalic());
      }

      if (chatStyle.getUnderlined() != null) {
         this.setUnderlined(chatStyle.getUnderlined());
      }

      if (chatStyle.getStrikethrough() != null) {
         this.setStrikethrough(chatStyle.getStrikethrough());
      }

      if (chatStyle.getObfuscated() != null) {
         this.setObfuscated(chatStyle.getObfuscated());
      }
   }

   public void setExtra(List list) {
      for (ChatBaseComponent chatBaseComponent : (Iterable<ChatBaseComponent>) list) {
         chatBaseComponent.chatBaseComponent3 = this;
      }

      this.extra = list;
   }

   public void addExtra(ChatBaseComponent chatBaseComponent) {
      if (this.extra == null) {
         this.extra = new ArrayList();
      }

      chatBaseComponent.chatBaseComponent3 = this;
      this.extra.add(chatBaseComponent);
   }

   public String toPlainText() {
      StringBuilder stringBuilder = new StringBuilder();
      this.appendPlainText(stringBuilder);
      return stringBuilder.toString();
   }

   void appendPlainText(StringBuilder builder) {
      if (this.extra != null) {
         for (ChatBaseComponent chatBaseComponent : (Iterable<ChatBaseComponent>) this.extra) {
            chatBaseComponent.appendPlainText(builder);
         }
      }
   }

   void appendLegacyText(StringBuilder builder) {
      builder.append(this.getColor());
      if (this.isBold()) {
         builder.append(ColorTranslator.BOLD);
      }

      if (this.isItalic()) {
         builder.append(ColorTranslator.ITALIC);
      }

      if (this.isUnderlined()) {
         builder.append(ColorTranslator.UNDERLINE);
      }

      if (this.isStrikethrough()) {
         builder.append(ColorTranslator.STRIKETHROUGH);
      }

      if (this.isObfuscated()) {
         builder.append(ColorTranslator.OBFUSCATED);
      }
   }

   public void setInsertion(String text) {
      this.insertion = text;
   }

   public void setClickEvent(ChatClickEvent chatClickEvent) {
      this.chatClickEvent = chatClickEvent;
   }

   public void setHoverEvent(HoverEvent hoverEvent) {
      this.hoverEvent = hoverEvent;
   }

   public void setFlag2(boolean flag) {
      this.flag2 = flag;
   }

   @Override
   public String toString() {
      return "BaseComponent(style="
         + this.getStyle()
         + ", insertion="
         + this.getInsertion()
         + ", extra="
         + this.getExtra()
         + ", clickEvent="
         + this.getClickEvent()
         + ", hoverEvent="
         + this.getHoverEvent()
         + ", reset="
         + this.isFlag2()
         + ")";
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof ChatBaseComponent)) {
         return false;
      }

      ChatBaseComponent chatBaseComponent = (ChatBaseComponent)obj;
      if (!chatBaseComponent.checkObj(this)) {
         return false;
      }

      ChatStyle style = this.getStyle();
      ChatStyle style2 = chatBaseComponent.getStyle();
      if (style == null ? style2 == null : style.equals(style2)) {
         String insertion = this.getInsertion();
         String insertion2 = chatBaseComponent.getInsertion();
         if (insertion == null ? insertion2 == null : insertion.equals(insertion2)) {
            List extra = this.getExtra();
            List extra2 = chatBaseComponent.getExtra();
            if (extra == null ? extra2 == null : extra.equals(extra2)) {
               ChatClickEvent clickEvent = this.getClickEvent();
               ChatClickEvent clickEvent2 = chatBaseComponent.getClickEvent();
               if (clickEvent == null ? clickEvent2 == null : clickEvent.equals(clickEvent2)) {
                  HoverEvent hoverEvent = this.getHoverEvent();
                  HoverEvent hoverEvent2 = chatBaseComponent.getHoverEvent();
                  return hoverEvent == null ? hoverEvent2 == null : hoverEvent.equals(hoverEvent2);
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

   protected boolean checkObj(Object obj) {
      return obj instanceof ChatBaseComponent;
   }

   @Override
   public int hashCode() {
      byte b = 1;
      ChatStyle style = this.getStyle();
      int value = b * 59 + (style == null ? 43 : style.hashCode());
      String insertion = this.getInsertion();
      int value2 = value * 59 + (insertion == null ? 43 : insertion.hashCode());
      List extra = this.getExtra();
      int value3 = value2 * 59 + (extra == null ? 43 : extra.hashCode());
      ChatClickEvent clickEvent = this.getClickEvent();
      int value4 = value3 * 59 + (clickEvent == null ? 43 : clickEvent.hashCode());
      HoverEvent hoverEvent = this.getHoverEvent();
      return value4 * 59 + (hoverEvent == null ? 43 : hoverEvent.hashCode());
   }

   public ChatStyle getStyle() {
      return this.style;
   }

   public String getInsertion() {
      return this.insertion;
   }

   public List getExtra() {
      return this.extra;
   }

   public ChatClickEvent getClickEvent() {
      return this.chatClickEvent;
   }

   public HoverEvent getHoverEvent() {
      return this.hoverEvent;
   }

   public boolean isFlag2() {
      return this.flag2;
   }
}
