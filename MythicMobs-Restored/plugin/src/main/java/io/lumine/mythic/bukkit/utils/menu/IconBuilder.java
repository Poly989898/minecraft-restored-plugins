package io.lumine.mythic.bukkit.utils.menu;

import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IconBuilder<T> {
   private Supplier<Icon<T>> baseIconFunc;
   private BiFunction<T, Player, T> initFunc;
   private BiFunction<T, Player, ItemFactory> iconFunc;
   private BiFunction<T, Player, ItemFactory> blinkFunc;
   private BiFunction<T, Player, Material> materialFunc;
   private BiFunction<T, Player, String> skullTextureFunc;
   private BiFunction<T, Player, NamespacedKey> itemModelFunc;
   private BiFunction<T, Player, NamespacedKey> tooltipStyleFunc;
   private BiFunction<T, Player, Integer> modelFunc;
   private BiFunction<T, Player, String> nameFunc;
   private BiFunction<T, Player, List<String>> loreFunc;
   private BiFunction<T, Player, Integer> amountFunc;
   private BiFunction<T, Player, Boolean> glintFunc;
   private BiFunction<T, Player, Chroma> colorFunc;
   private BiConsumer<T, Player> clickFunc;
   private BiConsumer<T, Player> rightClickFunc;
   private BiConsumer<T, Player> shiftClickFunc;
   private BiConsumer<T, Player> shiftRightClickFunc;
   private BiConsumer<T, Player> doubleClickFunc;
   private BiConsumer<T, Player> dropClickFunc;
   private BiConsumer<T, Player> dropAllClickFunc;
   private BiConsumer<T, Player> middleClickFunc;
   private BiConsumer<T, Player> swapOffhandClickFunc;
   private List<IconBuilder<T>.ConditionalIconEntry> conditionalIcons = new ArrayList<>();
   private boolean hideFlags = false;
   private final List<ItemFlag> flags = new ArrayList<>();
   private final Map<Enchantment, Integer> enchants = new LinkedHashMap<>();

   public static <T> IconBuilder<T> create() {
      return new IconBuilder<>();
   }

   private IconBuilder() {
   }

   public IconBuilder<T> init(BiFunction<T, Player, T> initFunc) {
      this.initFunc = initFunc;
      return this;
   }

   public IconBuilder<T> baseIcon(Icon<T> icon) {
      this.baseIconFunc = () -> icon;
      return this;
   }

   public IconBuilder<T> baseIcon(MenuData<T> icon) {
      this.baseIconFunc = icon::getIcon;
      return this;
   }

   public IconBuilder<T> itemFromIcon(MenuData<T> icon) {
      this.iconFunc = (_state, player) -> ItemFactory.of(icon.getIcon().build(_state, player));
      return this;
   }

   public IconBuilder<T> itemFromIcon(Function<T, MenuData<T>> icon) {
      this.iconFunc = (_state, player) -> {
         MenuData<T> menuData = icon.apply(_state);
         ItemStack item = menuData.getIcon().build(_state, player);
         return ItemFactory.of(item);
      };
      return this;
   }

   public IconBuilder<T> item(ItemFactory itemStack) {
      this.iconFunc = (_state, player) -> itemStack;
      return this;
   }

   public IconBuilder<T> item(Function<T, ItemFactory> materialFunc) {
      this.iconFunc = (_state, player) -> materialFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> item(BiFunction<T, Player, ItemFactory> materialFunc) {
      this.iconFunc = materialFunc;
      return this;
   }

   public IconBuilder<T> itemStack(ItemStack itemStack) {
      this.iconFunc = (_state, player) -> ItemFactory.of(itemStack);
      return this;
   }

   public IconBuilder<T> itemStack(Function<T, ItemStack> materialFunc) {
      this.iconFunc = (_state, player) -> ItemFactory.of(materialFunc.apply(_state));
      return this;
   }

   public IconBuilder<T> itemStack(BiFunction<T, Player, ItemStack> materialFunc) {
      this.iconFunc = (_state, player) -> ItemFactory.of(materialFunc.apply(_state, player));
      return this;
   }

   public IconBuilder<T> blink(Function<T, ItemFactory> materialFunc) {
      this.blinkFunc = (_state, player) -> materialFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> blink(BiFunction<T, Player, ItemFactory> materialFunc) {
      this.blinkFunc = materialFunc;
      return this;
   }

   public IconBuilder<T> blinkStack(Function<T, ItemStack> materialFunc) {
      this.blinkFunc = (_state, player) -> ItemFactory.of(materialFunc.apply(_state));
      return this;
   }

   public IconBuilder<T> blinkStack(BiFunction<T, Player, ItemStack> materialFunc) {
      this.blinkFunc = (_state, player) -> ItemFactory.of(materialFunc.apply(_state, player));
      return this;
   }

   public IconBuilder<T> material(Material material) {
      this.materialFunc = (_state, player) -> material;
      return this;
   }

   public IconBuilder<T> material(Function<T, Material> materialFunc) {
      this.materialFunc = (_state, player) -> materialFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> skullTexture(String skullTexture) {
      this.skullTextureFunc = (_state, player) -> skullTexture;
      return this;
   }

   public IconBuilder<T> skullTexture(Function<T, String> skullTextureFunc) {
      this.skullTextureFunc = (_state, player) -> skullTextureFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> name(String name) {
      this.nameFunc = (_state, player) -> name;
      return this;
   }

   public IconBuilder<T> name(Function<T, String> nameFunc) {
      this.nameFunc = (_state, player) -> nameFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> glint(Function<T, Boolean> glintFunc) {
      this.glintFunc = (_state, player) -> glintFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> color(Chroma color) {
      this.colorFunc = (_state, player) -> color;
      return this;
   }

   public IconBuilder<T> color(Function<T, Chroma> colorFunc) {
      this.colorFunc = (_state, player) -> colorFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> lore(List<String> lore) {
      this.loreFunc = (_state, player) -> lore;
      return this;
   }

   public IconBuilder<T> lore(Function<T, List<String>> loreFunc) {
      this.loreFunc = (_state, player) -> loreFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> click(BiConsumer<T, Player> clickFunc) {
      this.clickFunc = clickFunc;
      return this;
   }

   public IconBuilder<T> rightClick(BiConsumer<T, Player> clickFunc) {
      this.rightClickFunc = clickFunc;
      return this;
   }

   public IconBuilder<T> shiftClick(BiConsumer<T, Player> clickFunc) {
      this.shiftClickFunc = clickFunc;
      return this;
   }

   public IconBuilder<T> shiftRightClick(BiConsumer<T, Player> clickFunc) {
      this.shiftRightClickFunc = clickFunc;
      return this;
   }

   public IconBuilder<T> doubleClick(BiConsumer<T, Player> clickFunc) {
      this.doubleClickFunc = clickFunc;
      return this;
   }

   public IconBuilder<T> dropClick(BiConsumer<T, Player> clickFunc) {
      this.dropClickFunc = clickFunc;
      return this;
   }

   public IconBuilder<T> dropAllClick(BiConsumer<T, Player> clickFunc) {
      this.dropAllClickFunc = clickFunc;
      return this;
   }

   public IconBuilder<T> middleClick(BiConsumer<T, Player> clickFunc) {
      this.middleClickFunc = clickFunc;
      return this;
   }

   public IconBuilder<T> swapOffhandClick(BiConsumer<T, Player> clickFunc) {
      this.swapOffhandClickFunc = clickFunc;
      return this;
   }

   @Deprecated
   public IconBuilder<T> data(Byte data) {
      return this;
   }

   @Deprecated
   public IconBuilder<T> data(Function<T, Byte> dataFunc) {
      return this;
   }

   public IconBuilder<T> model(int data) {
      this.modelFunc = (_state, player) -> data;
      return this;
   }

   public IconBuilder<T> model(Function<T, Integer> dataFunc) {
      this.modelFunc = (_state, player) -> dataFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> itemModel(NamespacedKey data) {
      this.itemModelFunc = (_state, player) -> data;
      return this;
   }

   public IconBuilder<T> itemModel(Function<T, NamespacedKey> dataFunc) {
      this.itemModelFunc = (_state, player) -> dataFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> tooltipStyle(NamespacedKey data) {
      this.tooltipStyleFunc = (_state, player) -> data;
      return this;
   }

   public IconBuilder<T> amount(int data) {
      this.amountFunc = (_state, player) -> data;
      return this;
   }

   public IconBuilder<T> amount(Function<T, Integer> amountFunc) {
      this.amountFunc = (_state, player) -> amountFunc.apply(_state);
      return this;
   }

   public IconBuilder<T> hideFlags() {
      this.hideFlags = true;
      return this;
   }

   public IconBuilder<T> hideFlags(boolean bool) {
      this.hideFlags = bool;
      return this;
   }

   public IconBuilder<T> flag(ItemFlag... flags) {
      this.flags.addAll(Arrays.asList(flags));
      return this;
   }

   public IconBuilder<T> flags(Collection<ItemFlag> flags) {
      this.flags.addAll(flags);
      return this;
   }

   public IconBuilder<T> enchant(Enchantment enchantment, int level) {
      this.enchants.put(enchantment, level);
      return this;
   }

   public IconBuilder<T> conditionalOverride(BiPredicate<T, Player> predicate, Consumer<IconBuilder<T>> iconBuilderFunc) {
      IconBuilder<T> iconBuilder = new IconBuilder<>();
      iconBuilderFunc.accept(iconBuilder);
      this.conditionalIcons.add(new IconBuilder.ConditionalIconEntry(predicate, iconBuilder));
      return this;
   }

   public IconBuilder<T> conditionalOverride(BiPredicate<T, Player> predicate, IconBuilder<T> iconBuilder) {
      this.conditionalIcons.add(new IconBuilder.ConditionalIconEntry(predicate, iconBuilder));
      return this;
   }

   public Icon<T> build() {
      if (this.iconFunc == null && this.materialFunc == null) {
         this.materialFunc = (a, b) -> Material.PAPER;
      }

      if (this.conditionalIcons.isEmpty()) {
         return this.baseIconFunc != null && this.baseIconFunc.get() instanceof BasicIcon<T> baseIcon
            ? this.buildOverlapped(baseIcon)
            : new BasicIcon<>(
               this.initFunc,
               this.iconFunc,
               this.materialFunc,
               this.skullTextureFunc,
               this.itemModelFunc,
               this.tooltipStyleFunc,
               this.nameFunc,
               this.loreFunc,
               this.glintFunc,
               this.colorFunc,
               this.clickFunc,
               this.rightClickFunc,
               this.modelFunc,
               this.amountFunc,
               this.blinkFunc,
               this.hideFlags,
               this.shiftClickFunc,
               this.shiftRightClickFunc,
               this.doubleClickFunc,
               this.dropClickFunc,
               this.dropAllClickFunc,
               this.middleClickFunc,
               this.swapOffhandClickFunc,
               this.flags,
               this.enchants
            );
      } else {
         BasicIcon<T> fallbackIcon = new BasicIcon<>(
            this.initFunc,
            this.iconFunc,
            this.materialFunc,
            this.skullTextureFunc,
            this.itemModelFunc,
            this.tooltipStyleFunc,
            this.nameFunc,
            this.loreFunc,
            this.glintFunc,
            this.colorFunc,
            this.clickFunc,
            this.rightClickFunc,
            this.modelFunc,
            this.amountFunc,
            this.blinkFunc,
            this.hideFlags,
            this.shiftClickFunc,
            this.shiftRightClickFunc,
            this.doubleClickFunc,
            this.dropClickFunc,
            this.dropAllClickFunc,
            this.middleClickFunc,
            this.swapOffhandClickFunc,
            this.flags,
            this.enchants
         );
         Map<BiPredicate<T, Player>, Icon<T>> conditionalIcons = new LinkedHashMap<>();

         for (IconBuilder<T>.ConditionalIconEntry entry : this.conditionalIcons) {
            conditionalIcons.put(entry.getCondition(), entry.toIcon());
         }

         return new ConditionalIcon<>(fallbackIcon, conditionalIcons);
      }
   }

   private BasicIcon<T> buildOverlapped(@NotNull BasicIcon<T> base) {
      return new BasicIcon<>(
         this.overlap(base.getInitFunc(), this.initFunc),
         this.overlap(base.getItemBuilderFunc(), this.iconFunc),
         this.overlap(base.getMaterialFunc(), this.materialFunc),
         this.overlap(base.getSkullTexture(), this.skullTextureFunc),
         this.overlap(base.getItemModelFunction(), this.itemModelFunc),
         this.overlap(base.getTooltipStyleFunction(), this.tooltipStyleFunc),
         this.overlap(base.getNameFunc(), this.nameFunc),
         this.overlap(base.getLoreFunc(), this.loreFunc),
         this.overlap(base.getGlintFunc(), this.glintFunc),
         this.overlap(base.getColorFunc(), this.colorFunc),
         this.overlapOpt(base.getClickFunc(), this.clickFunc),
         this.overlapOpt(base.getRightClickFunc(), this.rightClickFunc),
         this.overlap(base.getModelFunction(), this.modelFunc),
         this.overlap(base.getAmountFunction(), this.amountFunc),
         this.overlapOpt(base.getBlinkFunc(), this.blinkFunc),
         base.isHideFlags() || this.hideFlags,
         this.overlapOpt(base.getShiftClickFunc(), this.shiftClickFunc),
         this.overlapOpt(base.getShiftRightClickFunc(), this.shiftRightClickFunc),
         this.overlapOpt(base.getDoubleClickFunc(), this.doubleClickFunc),
         this.overlapOpt(base.getDropClickFunc(), this.dropClickFunc),
         this.overlapOpt(base.getDropAllClickFunc(), this.dropAllClickFunc),
         this.overlapOpt(base.getMiddleClickFunc(), this.middleClickFunc),
         this.overlapOpt(base.getSwapOffhandClickFunc(), this.swapOffhandClickFunc),
         this.overlapFlags(base.getFlags(), this.flags),
         this.overlapEnchants(base.getEnchants(), this.enchants)
      );
   }

   private List<ItemFlag> overlapFlags(List<ItemFlag> first, List<ItemFlag> second) {
      if (second.isEmpty()) {
         return first;
      } else if (first.isEmpty()) {
         return second;
      } else {
         ArrayList<ItemFlag> merged = new ArrayList<>(first);
         second.stream().filter(flag -> !merged.contains(flag)).forEach(merged::add);
         return merged;
      }
   }

   private Map<Enchantment, Integer> overlapEnchants(Map<Enchantment, Integer> first, Map<Enchantment, Integer> second) {
      if (second.isEmpty()) {
         return first;
      } else if (first.isEmpty()) {
         return second;
      } else {
         LinkedHashMap<Enchantment, Integer> merged = new LinkedHashMap<>(first);
         merged.putAll(second);
         return merged;
      }
   }

   private <K> K overlapOpt(Optional<K> first, K second) {
      return second != null ? second : first.orElse(null);
   }

   private <K> K overlap(K first, K second) {
      return second != null ? second : first;
   }

   private class ConditionalIconEntry {
      private final BiPredicate<T, Player> condition;
      private final IconBuilder<T> builder;

      public Icon<T> toIcon() {
         return this.builder.build();
      }

      public ConditionalIconEntry(final BiPredicate<T, Player> condition, final IconBuilder<T> builder) {
         this.condition = condition;
         this.builder = builder;
      }

      public BiPredicate<T, Player> getCondition() {
         return this.condition;
      }

      public IconBuilder<T> getBuilder() {
         return this.builder;
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof IconBuilder<?>.ConditionalIconEntry other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$condition = this.getCondition();
            Object other$condition = other.getCondition();
            if (this$condition == null ? other$condition == null : this$condition.equals(other$condition)) {
               Object this$builder = this.getBuilder();
               Object other$builder = other.getBuilder();
               return this$builder == null ? other$builder == null : this$builder.equals(other$builder);
            } else {
               return false;
            }
         }
      }

      protected boolean canEqual(Object other) {
         return other instanceof IconBuilder.ConditionalIconEntry;
      }

      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $condition = this.getCondition();
         result = result * 59 + ($condition == null ? 43 : $condition.hashCode());
         Object $builder = this.getBuilder();
         return result * 59 + ($builder == null ? 43 : $builder.hashCode());
      }

      @Override
      public String toString() {
         return "IconBuilder.ConditionalIconEntry(condition=" + this.getCondition() + ", builder=" + this.getBuilder() + ")";
      }
   }
}
