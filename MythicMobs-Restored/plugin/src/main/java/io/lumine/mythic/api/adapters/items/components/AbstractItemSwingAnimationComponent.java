package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractItemSwingAnimationComponent implements AbstractItemComponent {
   private final AbstractItemSwingAnimationComponent.Kind kind;
   private String type;
   private Integer duration;

   public AbstractItemSwingAnimationComponent(MythicItem mythicItem, MythicConfig config) {
      this(mythicItem, config, AbstractItemSwingAnimationComponent.Kind.SWING);
   }

   public AbstractItemSwingAnimationComponent(MythicItem mythicItem, MythicConfig config, AbstractItemSwingAnimationComponent.Kind kind) {
      this.kind = kind;
      if (config.isSet("Type")) {
         this.type = config.getString("Type");
      }

      if (config.isSet("Duration")) {
         this.duration = config.getInteger("Duration");
         if (this.duration < 1) {
            MythicLogger.errorGenericConfig(config, kind.getSection() + ".Duration must be greater than 0, using 1 instead");
            this.duration = 1;
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applySwingAnimationComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemSwingAnimationComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$duration = this.getDuration();
         Object other$duration = other.getDuration();
         if (this$duration == null ? other$duration == null : this$duration.equals(other$duration)) {
            Object this$kind = this.getKind();
            Object other$kind = other.getKind();
            if (this$kind == null ? other$kind == null : this$kind.equals(other$kind)) {
               Object this$type = this.getType();
               Object other$type = other.getType();
               return this$type == null ? other$type == null : this$type.equals(other$type);
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
      return other instanceof AbstractItemSwingAnimationComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $duration = this.getDuration();
      result = result * 59 + ($duration == null ? 43 : $duration.hashCode());
      Object $kind = this.getKind();
      result = result * 59 + ($kind == null ? 43 : $kind.hashCode());
      Object $type = this.getType();
      return result * 59 + ($type == null ? 43 : $type.hashCode());
   }

   @Getter
   @AllArgsConstructor(access = AccessLevel.PRIVATE)
   public static enum Kind {
      SWING("SwingAnimation"),
      ATTACK("AttackAnimation"),
      INTERACT("InteractAnimation");

      private final String section;
   }
}
