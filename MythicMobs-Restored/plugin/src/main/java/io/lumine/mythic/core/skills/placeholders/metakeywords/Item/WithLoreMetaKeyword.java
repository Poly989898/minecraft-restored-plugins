package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;

@MythicMetaKeyword(
   name = "withLore"
)
public class WithLoreMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, BukkitItemStack> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute LORE = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("LORE")
      .defaultValue("default,lore")
      .names(new String[]{"lore", "loreList", "loreLines"})
      .build();

   public BukkitItemStack extract(MetaKeywords.ExtractionHandler<BukkitItemStack, BukkitItemStack> handler) {
      handler.value().setLore(handler.getObject(LORE, PolymorphicPlaceholder::getList));
      return handler.value();
   }
}
