package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.CommonAttributes;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeywordAttribute;
import io.lumine.mythic.core.skills.variables.VariableUnstableUtils;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@MythicMetaKeyword(
   name = "getCustomData",
   aliases = {"customData"}
)
public class CustomDataMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, String> {
   @MythicMetaKeywordAttribute(
      index = 1
   )
   public static final MetaKeywords.MetaKeywordsAttribute NAMESPACE = CommonAttributes.NAMESPACE;
   @MythicMetaKeywordAttribute(
      index = 2
   )
   public static final MetaKeywords.MetaKeywordsAttribute VALUE = CommonAttributes.VALUE;

   public String extract(MetaKeywords.ExtractionHandler<BukkitItemStack, String> handler) {
      ItemMeta meta = handler.value().getItemMeta();
      if (meta == null) {
         return "UNDEFINED";
      } else {
         String namespace = handler.getString(NAMESPACE);
         String key = handler.getString(VALUE);
         PersistentDataContainer container = VariableUnstableUtils.getContainer(meta);
         String value = (String)container.get(new NamespacedKey(namespace, key), PersistentDataType.STRING);
         if (value != null) {
            return value;
         } else {
            CompoundTag nested = handler.value().getCustomData().getCompound(namespace);
            return nested == null ? null : nested.getString(key);
         }
      }
   }
}
