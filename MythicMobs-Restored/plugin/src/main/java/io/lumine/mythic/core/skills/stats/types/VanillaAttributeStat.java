package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.core.skills.stats.AttributeMappedStat;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatType;
import java.util.Locale;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;

@Getter
public class VanillaAttributeStat extends StatType implements AttributeMappedStat {
   private final Attribute attribute;

   public VanillaAttributeStat(Attribute attribute) {
      super(statKey(((org.bukkit.Keyed)attribute).getKey()));
      this.attribute = attribute;
   }

   public static String statKey(NamespacedKey attributeKey) {
      String path = attributeKey.getKey();
      return path.substring(path.lastIndexOf(46) + 1).toUpperCase(Locale.ROOT);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      this.executionPoint = StatExecution.NONE;
   }

   @Override
   public void applyAttribute(AbstractEntity entity, double newValue) {
      entity.setAttributeBaseValue(this.attribute, newValue);
   }
}
