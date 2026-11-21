package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import org.bukkit.NamespacedKey;

@MythicMetaKeyword(
   name = "withModel"
)
public class WithModelMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, BukkitItemStack> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute NAMESPACE = CommonAttributes.NAMESPACE;
   @MythicMetaKeywordAttribute(
      index = 2
   )
   public static final MetaKeywords.MetaKeywordsAttribute VALUE = CommonAttributes.VALUE;

   public BukkitItemStack extract(MetaKeywords.ExtractionHandler<BukkitItemStack, BukkitItemStack> handler) {
      return handler.value().itemModel(new NamespacedKey(handler.getString(NAMESPACE), handler.getString(VALUE)));
   }
}
