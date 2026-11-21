package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractItemPotDecorationsComponent implements AbstractItemComponent {
   private String back;
   private String left;
   private String right;
   private String front;

   public AbstractItemPotDecorationsComponent(MythicItem mythicItem, MythicConfig config) {
      List<String> list = config.isList("PotDecorations") ? config.getStringList("PotDecorations") : null;
      if (list != null && list.size() == 4) {
         this.back = MythicConfig.normalizeNamespacedKey(list.get(0));
         this.left = MythicConfig.normalizeNamespacedKey(list.get(1));
         this.right = MythicConfig.normalizeNamespacedKey(list.get(2));
         this.front = MythicConfig.normalizeNamespacedKey(list.get(3));
      } else {
         if (list != null && !list.isEmpty()) {
            MythicLogger.errorGenericConfig(config, "PotDecorations must have exactly 4 entries (back, left, right, front)");
         }

         this.back = MythicConfig.normalizeNamespacedKey(config.getString("PotDecorations.Back", null));
         this.left = MythicConfig.normalizeNamespacedKey(config.getString("PotDecorations.Left", null));
         this.right = MythicConfig.normalizeNamespacedKey(config.getString("PotDecorations.Right", null));
         this.front = MythicConfig.normalizeNamespacedKey(config.getString("PotDecorations.Front", null));
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyPotDecorationsComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemPotDecorationsComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$back = this.getBack();
         Object other$back = other.getBack();
         if (this$back == null ? other$back == null : this$back.equals(other$back)) {
            Object this$left = this.getLeft();
            Object other$left = other.getLeft();
            if (this$left == null ? other$left == null : this$left.equals(other$left)) {
               Object this$right = this.getRight();
               Object other$right = other.getRight();
               if (this$right == null ? other$right == null : this$right.equals(other$right)) {
                  Object this$front = this.getFront();
                  Object other$front = other.getFront();
                  return this$front == null ? other$front == null : this$front.equals(other$front);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemPotDecorationsComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $back = this.getBack();
      result = result * 59 + ($back == null ? 43 : $back.hashCode());
      Object $left = this.getLeft();
      result = result * 59 + ($left == null ? 43 : $left.hashCode());
      Object $right = this.getRight();
      result = result * 59 + ($right == null ? 43 : $right.hashCode());
      Object $front = this.getFront();
      return result * 59 + ($front == null ? 43 : $front.hashCode());
   }
}
