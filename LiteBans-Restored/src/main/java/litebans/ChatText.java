package litebans;

import java.util.Arrays;

public class ChatText extends LiteHelper4 {
   private final Object chatBaseComponents;

   public ChatText(ChatBaseComponent[] chatBaseComponents) {
      this.chatBaseComponents = chatBaseComponents;
   }

   public ChatText(String text) {
      this.chatBaseComponents = text;
   }

   @Override
   public boolean equals(Object obj) {
      return !(this.chatBaseComponents instanceof ChatBaseComponent[])
         ? this.chatBaseComponents.equals(obj)
         : obj instanceof ChatText && ((ChatText)obj).chatBaseComponents instanceof ChatBaseComponent[] && Arrays.equals((ChatBaseComponent[])this.chatBaseComponents, (ChatBaseComponent[])((ChatText)obj).chatBaseComponents);
   }

   @Override
   public int hashCode() {
      return this.chatBaseComponents instanceof ChatBaseComponent[] ? Arrays.hashCode((ChatBaseComponent[])this.chatBaseComponents) : this.chatBaseComponents.hashCode();
   }

   public Object getChatBaseComponents() {
      return this.chatBaseComponents;
   }

   @Override
   public String toString() {
      return "Text(value=" + this.getChatBaseComponents() + ")";
   }
}
