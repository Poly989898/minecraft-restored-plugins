package io.lumine.mythic.core.mobs.equipment;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.skills.stats.StatSource;
import io.lumine.mythic.core.skills.stats.StatType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

@Getter
public class MobEquipment implements StatSource {
   @Setter
   private ItemStack itemStack;
   private EquipSlot equippedSlot;
   private Map<StatType, Map<StatModifierType, Double>> stats = Maps.newConcurrentMap();

   public MobEquipment(ActiveMob activeMob, ItemStack item, EquipSlot equippedSlot, MobEquipment previousItem) {
      if (item.hasItemMeta()) {
         ItemMeta itemMeta = item.getItemMeta();
         PersistentDataContainer data = itemMeta.getPersistentDataContainer();
         if (data.has(MobKeys.STATS, MobKeys.STAT_FIELD_ITEMS)) {
            HashMap<String, HashMap<String, Double>> stats = data.get(MobKeys.STATS, MobKeys.STAT_FIELD_ITEMS);
            Map<StatType, Map<StatModifierType, Double>> previousStats = previousItem != null && !previousItem.getStats().isEmpty()
               ? previousItem.getStats()
               : null;

            for (Entry<String, HashMap<String, Double>> statEntry : stats.entrySet()) {
               String statName = statEntry.getKey();
               Optional<StatType> maybeStat = MythicProvider.get().getStatManager().getStat(statName);
               if (!maybeStat.isEmpty()) {
                  StatType statType = maybeStat.get();
                  if (previousStats != null && previousStats.containsKey(statType)) {
                     previousItem.getStats().remove(statType);
                     activeMob.getStatRegistry().removeValueSilently(statType, previousItem);
                  }

                  Map<StatModifierType, Double> statMap = this.stats.compute(statType, (x, y) -> Maps.newHashMap());

                  for (Entry<String, Double> valueEntry : statEntry.getValue().entrySet()) {
                     StatModifierType operation = StatModifierType.valueOf(valueEntry.getKey());
                     statMap.put(operation, valueEntry.getValue());
                     activeMob.getStatRegistry().putValueSilently(statType, this, operation, valueEntry.getValue());
                  }

                  this.stats.put(statType, statMap);
               }
            }

            if (previousStats != null) {
               for (StatType stat : previousStats.keySet()) {
                  activeMob.getStatRegistry().removeValueSilently(stat, previousItem);
               }
            }
         } else if (previousItem != null && previousItem.hasStats()) {
            for (StatType stat : previousItem.getStats().keySet()) {
               activeMob.getStatRegistry().removeValueSilently(stat, previousItem);
            }
         }
      } else if (previousItem != null && previousItem.hasStats()) {
         for (StatType stat : previousItem.getStats().keySet()) {
            activeMob.getStatRegistry().removeValueSilently(stat, previousItem);
         }
      }
   }

   public boolean hasStats() {
      return !this.stats.isEmpty();
   }
}
