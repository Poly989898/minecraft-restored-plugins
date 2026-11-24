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

   public ObjectComponent(String text) {
      super(text);
   }

   @Nullable
   @Override
   public StatComponent getComponent(String text) {
      return this.components.get(text);
   }

   public void forEachComponent(Consumer<StatComponent> consumer) {
      for (StatComponent statComponent : this.components.values()) {
         consumer.accept(statComponent);
      }
   }

   public Set<String> getComponentKeys() {
      return this.components.keySet();
   }
}
