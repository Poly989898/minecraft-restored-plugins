package team.unnamed.creative.item.special;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.DyeColor;
import team.unnamed.creative.base.HeadType;
import team.unnamed.creative.base.Pose;
import team.unnamed.creative.base.WoodType;

public interface SpecialRender {
   static @NotNull BannerSpecialRender banner(@NotNull DyeColor color) {
      return new BannerSpecialRenderImpl(color);
   }

   static @NotNull BedSpecialRender bed(@NotNull Key texture) {
      return new BedSpecialRenderImpl(texture);
   }

   static @NotNull NoFieldSpecialRender conduit() {
      return NoFieldSpecialRenderImpl.CONDUIT;
   }

   static @NotNull ChestSpecialRender chest(@NotNull Key texture, float openness) {
      return new ChestSpecialRenderImpl(texture, openness);
   }

   static @NotNull ChestSpecialRender chest(@NotNull Key texture) {
      return chest(texture, 0.0F);
   }

   static @NotNull ChestSpecialRender normalChest() {
      return chest(ChestSpecialRender.NORMAL_CHEST_TEXTURE);
   }

   static @NotNull NoFieldSpecialRender decoratedPot() {
      return NoFieldSpecialRenderImpl.DECORATED_POT;
   }

   static @NotNull NoFieldSpecialRender shield() {
      return NoFieldSpecialRenderImpl.SHIELD;
   }

   static @NotNull NoFieldSpecialRender trident() {
      return NoFieldSpecialRenderImpl.TRIDENT;
   }

   static @NotNull NoFieldSpecialRender playerHead() {
      return NoFieldSpecialRenderImpl.PLAYER_HEAD;
   }

   /** @deprecated */
   @Deprecated(
      since = "1.14.0"
   )
   static @NotNull SignSpecialRender hangingSign(@NotNull WoodType woodType, @Nullable Key texture) {
      return new SignSpecialRenderImpl(true, woodType, texture);
   }

   static @NotNull SignSpecialRender standingSign(@NotNull WoodType woodType, @Nullable Key texture) {
      return new SignSpecialRenderImpl(false, woodType, texture);
   }

   static @NotNull SignSpecialRender hangingSign(@NotNull WoodType woodType) {
      return hangingSign(woodType, (Key)null);
   }

   static @NotNull HeadSpecialRender head(@NotNull HeadType kind, @Nullable Key texture, float animation) {
      return new HeadSpecialRenderImpl(kind, texture, animation);
   }

   static @NotNull HeadSpecialRender head(@NotNull HeadType kind, @Nullable Key texture) {
      return head(kind, texture, 0.0F);
   }

   static @NotNull HeadSpecialRender head(@NotNull HeadType kind, float animation) {
      return head(kind, (Key)null, animation);
   }

   static @NotNull HeadSpecialRender head(@NotNull HeadType kind) {
      return head(kind, (Key)null, 0.0F);
   }

   static @NotNull ShulkerBoxSpecialRender shulkerBox(@NotNull Key texture, float openness, @NotNull CubeFace orientation) {
      return new ShulkerBoxSpecialRenderImpl(texture, openness, orientation);
   }

   static @NotNull ShulkerBoxSpecialRender shulkerBox(@NotNull Key texture, float openness) {
      return shulkerBox(texture, openness, ShulkerBoxSpecialRender.DEFAULT_ORIENTATION);
   }

   static @NotNull ShulkerBoxSpecialRender shulkerBox(@NotNull Key texture, @NotNull CubeFace orientation) {
      return shulkerBox(texture, 0.0F, orientation);
   }

   static @NotNull ShulkerBoxSpecialRender shulkerBox(@NotNull Key texture) {
      return shulkerBox(texture, 0.0F, ShulkerBoxSpecialRender.DEFAULT_ORIENTATION);
   }

   static @NotNull CopperGolemStatueSpecialRender copperGolemStatue(@NotNull Pose pose, @NotNull Key texture) {
      return new CopperGolemStatueSpecialRenderImpl(pose, texture);
   }
}
