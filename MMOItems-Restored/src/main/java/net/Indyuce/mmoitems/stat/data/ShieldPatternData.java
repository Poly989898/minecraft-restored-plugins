package net.Indyuce.mmoitems.stat.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShieldPatternData implements StatData, RandomStatData<ShieldPatternData> {
   private DyeColor base;
   private final List<Pattern> patterns = new ArrayList<>();

   public ShieldPatternData(DyeColor dyeColor, Pattern... patterns) {
      this.base = dyeColor;
      this.patterns.addAll(Arrays.asList(patterns));
   }

   public void setBase(@Nullable DyeColor dyeColor) {
      this.base = dyeColor;
   }

   @Nullable
   public DyeColor getBaseColor() {
      return this.base;
   }

   @NotNull
   public List<Pattern> getPatterns() {
      return this.patterns;
   }

   public void add(Pattern pattern) {
      this.patterns.add(pattern);
   }

   public void addAll(List<Pattern> patterns) {
      this.patterns.addAll(patterns);
   }

   public ShieldPatternData clone() {
      ShieldPatternData shieldPatternData = new ShieldPatternData(this.base);
      shieldPatternData.patterns.addAll(this.patterns);
      return shieldPatternData;
   }

   @Override
   public boolean isEmpty() {
      return this.base == null && this.patterns.isEmpty();
   }

   public ShieldPatternData randomize(MMOItemBuilder mmoItemBuilder) {
      return this;
   }
}
