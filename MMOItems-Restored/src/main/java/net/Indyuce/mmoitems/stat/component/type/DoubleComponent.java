package net.Indyuce.mmoitems.stat.component.type;

import net.Indyuce.mmoitems.stat.component.Mergeable;
import net.Indyuce.mmoitems.stat.component.StatComponent;

@Deprecated
public class DoubleComponent extends StatComponent implements Mergeable<DoubleComponent> {
   private double value;

   public DoubleComponent(String var1) {
      super(var1);
   }

   public void merge(DoubleComponent var1) {
      this.value = this.value + var1.value;
   }
}
