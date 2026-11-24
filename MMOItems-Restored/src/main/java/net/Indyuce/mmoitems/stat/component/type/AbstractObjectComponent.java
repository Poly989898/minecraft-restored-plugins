package net.Indyuce.mmoitems.stat.component.type;

import net.Indyuce.mmoitems.stat.component.StatComponent;
import org.jetbrains.annotations.Nullable;

@Deprecated
public abstract class AbstractObjectComponent extends StatComponent {
   public AbstractObjectComponent(String var1) {
      super(var1);
   }

   @Nullable
   public StatComponent findComponent(String var1) {
      String[] var2 = var1.split("\\.");
      AbstractObjectComponent var3 = this;
      int var4 = var2.length - 1;

      for (int var5 = 0; var5 < var4; var5++) {
         StatComponent var6 = this.getComponent(var2[var5]);
         if (var6 == null || !(var6 instanceof AbstractObjectComponent)) {
            return null;
         }

         var3 = (AbstractObjectComponent)var6;
      }

      return var3.getComponent(var2[var4]);
   }

   @Nullable
   public abstract StatComponent getComponent(String var1);
}
