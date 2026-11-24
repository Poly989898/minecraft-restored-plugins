package net.Indyuce.mmoitems.stat.component.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.Indyuce.mmoitems.stat.component.StatComponent;
import net.Indyuce.mmoitems.stat.component.Upgradable;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class ObjectComponent extends AbstractObjectComponent implements Upgradable {
   private final Map<String, StatComponent> components = new HashMap<>();

   public ObjectComponent(String var1) {
      super(var1);
   }

   @Nullable
   @Override
   public StatComponent getComponent(String var1) {
      return this.components.get(var1);
   }

   public void forEachComponent(Consumer<StatComponent> var1) {
      for (StatComponent var3 : this.components.values()) {
         var1.accept(var3);
      }
   }

   public Set<String> getComponentKeys() {
      return this.components.keySet();
   }
}
