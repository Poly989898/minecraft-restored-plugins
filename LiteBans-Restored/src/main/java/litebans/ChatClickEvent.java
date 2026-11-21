package litebans;

public final class ChatClickEvent {
   private final ClickAction clickAction;
   private final String text3;

   public ClickAction getClickAction() {
      return this.clickAction;
   }

   public String getText3() {
      return this.text3;
   }

   @Override
   public String toString() {
      return "ClickEvent(action=" + this.getClickAction() + ", value=" + this.getText3() + ")";
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ChatClickEvent)) {
         return false;
      } else {
         ChatClickEvent chatClickEvent = (ChatClickEvent)obj;
         ClickAction clickAction2 = this.getClickAction();
         ClickAction clickAction3 = chatClickEvent.getClickAction();
         if (clickAction2 == null ? clickAction3 == null : clickAction2.equals(clickAction3)) {
            String text = this.getText3();
            String text2 = chatClickEvent.getText3();
            return text == null ? text2 == null : text.equals(text2);
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      byte b = 1;
      ClickAction clickAction2 = this.getClickAction();
      int value = b * 59 + (clickAction2 == null ? 43 : clickAction2.hashCode());
      String text = this.getText3();
      return value * 59 + (text == null ? 43 : text.hashCode());
   }

   public ChatClickEvent(ClickAction clickAction2, String text) {
      this.clickAction = clickAction2;
      this.text3 = text;
   }
}
