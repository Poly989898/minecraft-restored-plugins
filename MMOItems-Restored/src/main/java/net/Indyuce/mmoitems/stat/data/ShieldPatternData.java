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

   public ShieldPatternData(DyeColor var1, Pattern... var2) {
      this.base = var1;
      this.patterns.addAll(Arrays.asList(var2));
   }

   public void setBase(@Nullable DyeColor var1) {
      this.base = var1;
   }

   @Nullable
   public DyeColor getBaseColor() {
      return this.base;
   }

   @NotNull
   public List<Pattern> getPatterns() {
      return this.patterns;
   }

   public void add(Pattern var1) {
      this.patterns.add(var1);
   }

   public void addAll(List<Pattern> var1) {
      this.patterns.addAll(var1);
   }

   public ShieldPatternData clone() {
      ShieldPatternData var1 = new ShieldPatternData(this.base);
      var1.patterns.addAll(this.patterns);
      return var1;
   }

   @Override
   public boolean isEmpty() {
      return this.base == null && this.patterns.isEmpty();
   }

   public ShieldPatternData randomize(MMOItemBuilder var1) {
      return this;
   }
}
