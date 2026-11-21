package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import io.lumine.mythic.core.skills.variables.types.MapVariable;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.enchantments.Enchantment;

@MythicMetaKeyword(
   name = "withEnchants"
)
public class WithEnchantsMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, BukkitItemStack> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute ENCHANTS = CommonAttributes.ENCHANTMENT;

   public BukkitItemStack extract(MetaKeywords.ExtractionHandler<BukkitItemStack, BukkitItemStack> handler) {
      Map<String, String> newEnchants = MapVariable.fromString(handler.getString(ENCHANTS));
      Map<Enchantment, Integer> actualMap = newEnchants.entrySet()
         .stream()
         .filter(e -> e.getKey() != null && e.getValue() != null)
         .collect(Collectors.toMap(e -> Enchantment.getByName(e.getKey()), e -> Integer.parseInt(e.getValue())));
      handler.value().editMeta(meta -> meta.getEnchants().keySet().forEach(meta::removeEnchant));
      actualMap.forEach((e, l) -> handler.value().enchant(e, l));
      return handler.value();
   }
}
