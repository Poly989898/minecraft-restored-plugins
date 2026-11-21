package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import lombok.Generated;
import lombok.Getter;

@Getter
public class AbstractItemTooltipComponent implements AbstractItemComponent {
   public static final String KEY_TOOLTIP_STYLE = "TooltipStyle";
   private String tooltipStyle;

   public AbstractItemTooltipComponent(MythicItem mythicItem, MythicConfig config) {
      this.tooltipStyle = config.getString("TooltipStyle", null);
      if (this.tooltipStyle != null) {
         this.tooltipStyle = this.tooltipStyle.toLowerCase();
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyTooltipComponent(this);
   }

   @Generated
   public void setTooltipStyle(String tooltipStyle) {
      this.tooltipStyle = tooltipStyle;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemTooltipComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$tooltipStyle = this.getTooltipStyle();
         Object other$tooltipStyle = other.getTooltipStyle();
         return this$tooltipStyle == null ? other$tooltipStyle == null : this$tooltipStyle.equals(other$tooltipStyle);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemTooltipComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $tooltipStyle = this.getTooltipStyle();
      return result * 59 + ($tooltipStyle == null ? 43 : $tooltipStyle.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "AbstractItemTooltipComponent(tooltipStyle=" + this.getTooltipStyle() + ")";
   }
}
