package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "customModelData"
)
public class CustomModelDataMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, Integer> {
   public Integer extract(MetaKeywords.ExtractionHandler<BukkitItemStack, Integer> handler) {
      return handler.value().getModelData();
   }
}
