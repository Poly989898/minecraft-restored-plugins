package io.lumine.mythic.core.skills.placeholders.parsers.tokens;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderParser;
import io.lumine.mythic.core.skills.placeholders.all.MetaTargetPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.EntityPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GeneralPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.ItemPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.LocationPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.SpawnerPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.TargetPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.WorldPlaceholder;

public class LegacyPlaceholderParserToken extends PlaceholderParserToken<PlaceholderParser.FoundPlaceholder> {
   public LegacyPlaceholderParserToken(PlaceholderParser.FoundPlaceholder value) {
      super(value);
   }

   @Override
   public boolean isStatic() {
      return false;
   }

   @Override
   public String apply(PlaceholderContext placeholderContext) {
      try {
         switch (this.value.kind) {
            case 1:
               return ((GeneralPlaceholder)this.value.placeholder).apply(this.value.arg);
            case 2:
               if (placeholderContext.meta() != null) {
                  return ((MetaPlaceholder)this.value.placeholder).apply(placeholderContext.meta(), this.value.arg);
               }
               break;
            case 3:
               if (placeholderContext.entity() != null) {
                  return ((EntityPlaceholder)this.value.placeholder).apply(placeholderContext.entity(), this.value.arg);
               }
               break;
            case 4:
               if (placeholderContext.location() != null) {
                  return ((LocationPlaceholder)this.value.placeholder).apply(placeholderContext.location(), this.value.arg);
               }

               if (placeholderContext.entity() != null) {
                  return ((LocationPlaceholder)this.value.placeholder).apply(placeholderContext.entity().getLocation(), this.value.arg);
               }
               break;
            case 5:
               if (placeholderContext.meta() != null && placeholderContext.entity() != null) {
                  return ((TargetPlaceholder)this.value.placeholder).apply(placeholderContext.meta(), placeholderContext.entity(), this.value.arg);
               }
               break;
            case 6:
               if (placeholderContext.meta() != null && placeholderContext.entity() != null) {
                  return ((MetaTargetPlaceholder)this.value.placeholder).apply(placeholderContext.meta(), placeholderContext.entity(), this.value.arg);
               }
               break;
            case 7:
               AbstractItemStack equippedItem = placeholderContext.getBukkitItemStack();
               if (equippedItem != null) {
                  return ((ItemPlaceholder)this.value.placeholder).apply(equippedItem, this.value.arg);
               }
               break;
            case 8:
               if (placeholderContext.entity() != null) {
                  return ((WorldPlaceholder)this.value.placeholder).apply(placeholderContext.entity().getWorld(), this.value.arg);
               }

               if (placeholderContext.location() != null) {
                  return ((WorldPlaceholder)this.value.placeholder).apply(placeholderContext.location().getWorld(), this.value.arg);
               }

               if (placeholderContext.spawner() != null) {
                  return ((WorldPlaceholder)this.value.placeholder).apply(placeholderContext.spawner().getLocation().getWorld(), this.value.arg);
               }
               break;
            case 9:
               if (placeholderContext.spawner() != null) {
                  return ((SpawnerPlaceholder)this.value.placeholder).apply(placeholderContext.spawner(), this.value.arg);
               }
         }
      } catch (NullPointerException e) {
         MythicLogger.error("Failed to process legacy placeholder " + this.value.token, e);
      }

      return "UNDEFINED";
   }
}
