package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.skills.variables.types.ItemVariable;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "mythicitem",
   version = "5.4",
   usedPlaceholderArguments = 2
)
public class MythicItemPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> name = this.getPlaceholderString(0, new String[0]);
   private final ResolvedPlaceholderSegment<PlaceholderString> attribute = this.getPlaceholderString(1, new String[0]);

   public MythicItemPlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      String name = this.name.getOrDefault(placeholderContext, PlaceholderString::get, null);
      if (name == null) {
         return null;
      } else {
         Optional<MythicItem> maybeItem = this.manager.getPlugin().getItemManager().getItem(name);
         if (maybeItem.isEmpty()) {
            return null;
         } else {
            MythicItem mythicItem = maybeItem.get();
            String orDefault = this.attribute.getOrDefault(placeholderContext, PlaceholderString::get, "default");

            return switch (orDefault) {
               case "material" -> mythicItem.getMaterial().toString();
               case "model" -> String.valueOf(mythicItem.getCustomModelData());
               case "display" -> mythicItem.getDisplayName();
               case "lore" -> mythicItem.getLoreRaw() == null
                  ? ""
                  : (String)mythicItem.getLoreRaw()
                     .stream()
                     .map(line -> SkillString.parseMessageSpecialChars(line.get(placeholderContext)))
                     .collect(Collectors.joining("\n"));
               case "itemstack" -> ItemVariable.Utils.serializeToString((BukkitItemStack)mythicItem.generateItemStack(1));
               default -> null;
            };
         }
      }
   }
}
