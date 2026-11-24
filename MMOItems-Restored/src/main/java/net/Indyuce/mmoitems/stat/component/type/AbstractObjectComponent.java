package net.Indyuce.mmoitems.stat.component.type;

import net.Indyuce.mmoitems.stat.component.StatComponent;
import org.jetbrains.annotations.Nullable;

@Deprecated
public abstract class AbstractObjectComponent extends StatComponent {
   public AbstractObjectComponent(String text) {
      super(text);
   }

   @Nullable
   public StatComponent findComponent(String text) {
      String[] texts = text.split("\\.");
      AbstractObjectComponent abstractObjectComponent = this;
      int index = texts.length - 1;

      for (int i = 0; i < index; i++) {
         StatComponent statComponent = this.getComponent(texts[i]);
         if (statComponent == null || !(statComponent instanceof AbstractObjectComponent)) {
            return null;
         }

         abstractObjectComponent = (AbstractObjectComponent)statComponent;
      }

      return abstractObjectComponent.getComponent(texts[index]);
   }

   @Nullable
   public abstract StatComponent getComponent(String text);
}
