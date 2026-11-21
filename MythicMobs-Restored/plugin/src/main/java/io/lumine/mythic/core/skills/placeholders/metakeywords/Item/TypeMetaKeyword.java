package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;

@MythicMetaKeyword(
   name = "type"
)
public class TypeMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, String> {
   public String extract(MetaKeywords.ExtractionHandler<BukkitItemStack, String> handler) {
      return handler.value().getType().toString();
   }
}
