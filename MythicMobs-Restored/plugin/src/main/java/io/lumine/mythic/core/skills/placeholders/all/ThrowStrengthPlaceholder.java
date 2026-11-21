package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.skills.triggers.meta.ThrownMetadata;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "throwstrength",
   aliases = {"throwstr"},
   usedPlaceholderArguments = -1
)
public class ThrowStrengthPlaceholder extends NumericEntityScopedPlaceholder {
   private final ThrowStrengthPlaceholder.StrengthComponent component;

   public ThrowStrengthPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
      ThrowStrengthPlaceholder.StrengthComponent parsedComponent = ThrowStrengthPlaceholder.StrengthComponent.TOTAL;
      GenericPlaceholderSegment maybeArg = this.getPlaceholderSegmentsManager().getExtraArg(0);
      if (maybeArg != null) {
         ThrowStrengthPlaceholder.StrengthComponent maybeComponent = ThrowStrengthPlaceholder.StrengthComponent.fromKey(maybeArg.key());
         if (maybeComponent != null) {
            parsedComponent = maybeComponent;
            this.usedPlaceholderArguments.set(1);
         }
      }

      this.component = parsedComponent;
      this.initializeMetaKeywords();
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      if (placeholderContext.meta() instanceof SkillMetadata skillMetadata) {
         if (skillMetadata.getTriggerMetadata() instanceof ThrownMetadata thrownMetadata) {
            double throwStrength = thrownMetadata.getThrowStrength();
            double vertical = thrownMetadata.getThrowStrengthY();

            return switch (this.component) {
               case HORIZONTAL -> throwStrength;
               case VERTICAL -> vertical;
               case TOTAL -> Math.sqrt(throwStrength * throwStrength + vertical * vertical);
            };
         } else {
            return 0.0;
         }
      } else {
         return 0.0;
      }
   }

   private static enum StrengthComponent {
      HORIZONTAL,
      VERTICAL,
      TOTAL;

      static ThrowStrengthPlaceholder.StrengthComponent fromKey(@Nullable String key) {
         if (key == null) {
            return null;
         } else {
            String str = key.toLowerCase(Locale.ROOT);

            return switch (str) {
               case "x", "h", "horizontal", "strength" -> HORIZONTAL;
               case "y", "v", "vertical", "up", "ystrength", "strengthy" -> VERTICAL;
               case "total", "t", "length", "len", "magnitude", "mag" -> TOTAL;
               default -> null;
            };
         }
      }
   }
}
