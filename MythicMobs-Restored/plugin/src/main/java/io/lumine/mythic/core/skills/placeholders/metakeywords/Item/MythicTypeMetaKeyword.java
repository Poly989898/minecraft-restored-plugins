package io.lumine.mythic.core.skills.placeholders.metakeywords.Item;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.constants.ItemKeys;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.variables.VariableUnstableUtils;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@MythicMetaKeyword(
   name = "mythicType"
)
public class MythicTypeMetaKeyword extends AbstractMetaKeyword<BukkitItemStack, String> {
   public String extract(MetaKeywords.ExtractionHandler<BukkitItemStack, String> handler) {
      ItemMeta meta = handler.value().getItemMeta();
      if (meta == null) {
         return "UNDEFINED";
      } else {
         PersistentDataContainer container = VariableUnstableUtils.getContainer(meta);
         return (String)container.get(ItemKeys.TYPE, PersistentDataType.STRING);
      }
   }
}
