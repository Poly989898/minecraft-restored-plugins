package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import java.util.List;

@MythicMetaKeyword(
   name = "lore"
)
public class LoreMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, List<String>> {
   public List<String> extract(MetaKeywords.ExtractionHandler<BukkitItemStack, List<String>> handler) {
      return handler.value().getItemMeta().getLore();
   }
}
