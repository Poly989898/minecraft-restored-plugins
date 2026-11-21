package team.unnamed.creative.item.special;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Pose;

public interface CopperGolemStatueSpecialRender extends SpecialRender {
   @NotNull Pose pose();

   @NotNull Key texture();
}
