package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.enchantments.Enchantment;

@MythicMetaKeyword(
   name = "enchants"
)
public class EnchantsTypeMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, Map<String, String>> {
   public Map<String, String> extract(MetaKeywords.ExtractionHandler<BukkitItemStack, Map<String, String>> handler) {
      Map<Enchantment, Integer> enchantments = handler.value().getItemStack().getEnchantments();
      return enchantments.entrySet()
         .stream()
         .filter(e -> e.getKey() != null && e.getValue() != null)
         .collect(Collectors.toMap(e -> e.getKey().getName(), e -> String.valueOf(e.getValue())));
   }
}
