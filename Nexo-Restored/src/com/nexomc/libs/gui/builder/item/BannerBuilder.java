package com.nexomc.libs.gui.builder.item;

import com.nexomc.libs.gui.components.exception.GuiException;
import com.nexomc.libs.gui.components.util.VersionHelper;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BannerBuilder extends BaseItemBuilder<BannerBuilder> {
   private static final Material DEFAULT_BANNER;
   private static final EnumSet<Material> BANNERS;

   BannerBuilder() {
      super(new ItemStack(DEFAULT_BANNER));
   }

   BannerBuilder(@NotNull ItemStack itemStack) {
      super(itemStack);
      if (!BANNERS.contains(itemStack.getType())) {
         throw new GuiException("BannerBuilder requires the material to be a banner!");
      }
   }

   @Contract("_ -> this")
   public @NotNull BannerBuilder baseColor(@NotNull DyeColor color) {
   // Paper 26.x removed BannerMeta#setBaseColor: the base colour is carried by the banner material.
   Material banner = Material.matchMaterial(color.name() + "_BANNER");
   if (banner == null) {
   return this;
   }
   ItemStack stack = this.getItemStack();
   stack.setType(banner);
   this.setItemStack(stack);
   this.setMeta(stack.getItemMeta());
   return this;
   }

   @Contract("_, _ -> this")
   public @NotNull BannerBuilder pattern(@NotNull DyeColor color, @NotNull PatternType pattern) {
      BannerMeta bannerMeta = (BannerMeta)this.getMeta();
      bannerMeta.addPattern(new Pattern(color, pattern));
      this.setMeta(bannerMeta);
      return this;
   }

   @Contract("_ -> this")
   public @NotNull BannerBuilder pattern(Pattern... pattern) {
      return this.pattern(Arrays.asList(pattern));
   }

   @Contract("_ -> this")
   public @NotNull BannerBuilder pattern(@NotNull List<Pattern> patterns) {
      BannerMeta bannerMeta = (BannerMeta)this.getMeta();

      for(Pattern it : patterns) {
         bannerMeta.addPattern(it);
      }

      this.setMeta(bannerMeta);
      return this;
   }

   @Contract("_, _, _ -> this")
   public @NotNull BannerBuilder pattern(int index, @NotNull DyeColor color, @NotNull PatternType pattern) {
      return this.pattern(index, new Pattern(color, pattern));
   }

   @Contract("_, _ -> this")
   public @NotNull BannerBuilder pattern(int index, @NotNull Pattern pattern) {
      BannerMeta bannerMeta = (BannerMeta)this.getMeta();
      bannerMeta.setPattern(index, pattern);
      this.setMeta(bannerMeta);
      return this;
   }

   @Contract("_ -> this")
   public @NotNull BannerBuilder setPatterns(@NotNull List<@NotNull Pattern> patterns) {
      BannerMeta bannerMeta = (BannerMeta)this.getMeta();
      bannerMeta.setPatterns(patterns);
      this.setMeta(bannerMeta);
      return this;
   }

   static {
      if (VersionHelper.IS_ITEM_LEGACY) {
         DEFAULT_BANNER = Material.valueOf("BANNER");
         BANNERS = EnumSet.of(Material.valueOf("BANNER"));
      } else {
         DEFAULT_BANNER = Material.WHITE_BANNER;
         BANNERS = EnumSet.copyOf(Tag.BANNERS.getValues());
      }

   }
}
