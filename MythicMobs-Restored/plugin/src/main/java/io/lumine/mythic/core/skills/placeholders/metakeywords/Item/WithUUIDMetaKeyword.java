package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import java.util.UUID;

@MythicMetaKeyword(
   name = "withUUID"
)
public class WithUUIDMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, BukkitItemStack> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute UUID = MetaKeywords.MetaKeywordsAttribute.builder()
      .identifier("UUID")
      .defaultValue("0")
      .names(new String[]{"uuid"})
      .build();

   public BukkitItemStack extract(MetaKeywords.ExtractionHandler<BukkitItemStack, BukkitItemStack> handler) {
      return handler.value().UUID(java.util.UUID.fromString(handler.getString(UUID)));
   }
}
