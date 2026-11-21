package com.ticxo.modelengine.api.animation;

import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.config.Property;
import java.util.Locale;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public enum ModelState implements Property {
   IDLE(BlueprintAnimation.LoopMode.LOOP, null),
   WALK(BlueprintAnimation.LoopMode.LOOP, null),
   STRAFE(BlueprintAnimation.LoopMode.LOOP, null),
   JUMP_START(BlueprintAnimation.LoopMode.ONCE, BlueprintAnimation.OverrideMode.OVERRIDE),
   JUMP(BlueprintAnimation.LoopMode.LOOP, BlueprintAnimation.OverrideMode.OVERRIDE),
   JUMP_END(BlueprintAnimation.LoopMode.ONCE, BlueprintAnimation.OverrideMode.OVERRIDE),
   HOVER(BlueprintAnimation.LoopMode.LOOP, BlueprintAnimation.OverrideMode.OVERRIDE),
   FLY(BlueprintAnimation.LoopMode.LOOP, BlueprintAnimation.OverrideMode.OVERRIDE),
   SPAWN(BlueprintAnimation.LoopMode.ONCE, BlueprintAnimation.OverrideMode.OVERRIDE),
   DEATH(BlueprintAnimation.LoopMode.HOLD, BlueprintAnimation.OverrideMode.OVERRIDE);

   private final String path = ConfigProperty.DEFAULT_NAMES.getPath() + "." + this.name();
   private final Object def = this.name().toLowerCase(Locale.ENGLISH);
   private final BlueprintAnimation.LoopMode loopMode;
   private final BlueprintAnimation.OverrideMode override;

   ModelState(BlueprintAnimation.LoopMode loopMode, BlueprintAnimation.OverrideMode override) {
      this.loopMode = loopMode;
      this.override = override;
   }

   @Nullable
   public static ModelState get(String value) {
      try {
         return valueOf(value.toUpperCase(Locale.ENGLISH));
      } catch (IllegalArgumentException ignored) {
         return null;
      }
   }
}
