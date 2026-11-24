package net.Indyuce.mmoitems.stat.component.type;

import net.Indyuce.mmoitems.stat.component.Mergeable;
import net.Indyuce.mmoitems.stat.component.StatComponent;

@Deprecated
public class DoubleComponent extends StatComponent implements Mergeable<DoubleComponent> {
   private double value;

   public DoubleComponent(String text) {
      super(text);
   }

   public void merge(DoubleComponent doubleComponent) {
      this.value = this.value + doubleComponent.value;
   }
}
