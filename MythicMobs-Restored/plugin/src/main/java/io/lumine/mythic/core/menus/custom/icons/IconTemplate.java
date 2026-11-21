package io.lumine.mythic.core.menus.custom.icons;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.core.menus.custom.CustomMenuContext;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IconTemplate {
   private PlaceholderString display;
   private IconLoreTemplate tooltip;

   public IconTemplate(MythicConfig config, IconLoreParser parser) {
      this.display = config.getPlaceholderString("Display");
      List<String> tooltipLines = config.getStringList("Lore");
      this.tooltip = new IconLoreTemplate(parser, tooltipLines);
   }

   public IconBuilder<CustomMenuContext> apply(CustomMenuContext context, IconBuilder<CustomMenuContext> icon) {
      if (this.display != null) {
         icon.name(this.display.get(context));
      }

      if (this.tooltip != null) {
         this.tooltip.apply(context, icon);
      }

      return icon;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof IconTemplate other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$display = this.getDisplay();
         Object other$display = other.getDisplay();
         if (this$display == null ? other$display == null : this$display.equals(other$display)) {
            Object this$tooltip = this.getTooltip();
            Object other$tooltip = other.getTooltip();
            return this$tooltip == null ? other$tooltip == null : this$tooltip.equals(other$tooltip);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof IconTemplate;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $display = this.getDisplay();
      result = result * 59 + ($display == null ? 43 : $display.hashCode());
      Object $tooltip = this.getTooltip();
      return result * 59 + ($tooltip == null ? 43 : $tooltip.hashCode());
   }
}
