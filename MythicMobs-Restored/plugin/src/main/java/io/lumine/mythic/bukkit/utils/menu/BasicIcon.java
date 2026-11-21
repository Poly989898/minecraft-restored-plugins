package io.lumine.mythic.bukkit.utils.menu;

import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class BasicIcon<T> implements Icon<T> {
   private final BiFunction<T, Player, T> initFunc;
   private final BiFunction<T, Player, ItemFactory> itemBuilderFunc;
   private final BiFunction<T, Player, Material> materialFunc;
   private final BiFunction<T, Player, String> skullTexture;
   private final BiFunction<T, Player, NamespacedKey> itemModelFunction;
   private final BiFunction<T, Player, NamespacedKey> tooltipStyleFunction;
   private final BiFunction<T, Player, Integer> modelFunction;
   private final BiFunction<T, Player, Integer> amountFunction;
   private final BiFunction<T, Player, String> nameFunc;
   private final BiFunction<T, Player, List<String>> loreFunc;
   private final BiFunction<T, Player, Boolean> glintFunc;
   private final BiFunction<T, Player, Chroma> colorFunc;
   private final Optional<BiConsumer<T, Player>> clickFunc;
   private final Optional<BiConsumer<T, Player>> rightClickFunc;
   private final Optional<BiConsumer<T, Player>> shiftClickFunc;
   private final Optional<BiConsumer<T, Player>> shiftRightClickFunc;
   private final Optional<BiConsumer<T, Player>> doubleClickFunc;
   private final Optional<BiConsumer<T, Player>> dropClickFunc;
   private final Optional<BiConsumer<T, Player>> dropAllClickFunc;
   private final Optional<BiConsumer<T, Player>> middleClickFunc;
   private final Optional<BiConsumer<T, Player>> swapOffhandClickFunc;
   private final Optional<BiFunction<T, Player, ItemFactory>> blinkFunc;
   private boolean hideFlags = false;
   private final List<ItemFlag> flags;
   private final Map<Enchantment, Integer> enchants;
   private boolean blink = false;

   public BasicIcon(
      BiFunction<T, Player, T> initFunc,
      BiFunction<T, Player, ItemFactory> iconFunc,
      BiFunction<T, Player, Material> materialFunc,
      BiFunction<T, Player, String> skullTexture,
      BiFunction<T, Player, NamespacedKey> itemModelFunc,
      BiFunction<T, Player, NamespacedKey> tooltipStyleFunc,
      BiFunction<T, Player, String> nameFunc,
      BiFunction<T, Player, List<String>> loreFunc,
      BiFunction<T, Player, Boolean> glintFunc,
      BiFunction<T, Player, Chroma> colorFunc,
      BiConsumer<T, Player> clickFunc,
      BiConsumer<T, Player> rclickFunc,
      BiFunction<T, Player, Integer> byteFunction,
      BiFunction<T, Player, Integer> amountFunction,
      BiFunction<T, Player, ItemFactory> blinkFunc,
      boolean hideFlags,
      BiConsumer<T, Player> shiftClickFunc,
      BiConsumer<T, Player> shiftRightClickFunc,
      BiConsumer<T, Player> doubleClickFunc,
      BiConsumer<T, Player> dropClickFunc,
      BiConsumer<T, Player> dropAllClickFunc,
      BiConsumer<T, Player> middleClickFunc,
      BiConsumer<T, Player> swapOffhandClickFunc,
      List<ItemFlag> flags,
      Map<Enchantment, Integer> enchants
   ) {
      this.initFunc = initFunc;
      this.itemBuilderFunc = iconFunc;
      this.materialFunc = materialFunc;
      this.skullTexture = skullTexture;
      this.itemModelFunction = itemModelFunc;
      this.tooltipStyleFunction = tooltipStyleFunc;
      this.nameFunc = nameFunc;
      this.loreFunc = loreFunc;
      this.glintFunc = glintFunc;
      this.colorFunc = colorFunc;
      this.clickFunc = Optional.ofNullable(clickFunc);
      this.rightClickFunc = Optional.ofNullable(rclickFunc);
      this.shiftClickFunc = Optional.ofNullable(shiftClickFunc);
      this.shiftRightClickFunc = Optional.ofNullable(shiftRightClickFunc);
      this.doubleClickFunc = Optional.ofNullable(doubleClickFunc);
      this.dropClickFunc = Optional.ofNullable(dropClickFunc);
      this.dropAllClickFunc = Optional.ofNullable(dropAllClickFunc);
      this.middleClickFunc = Optional.ofNullable(middleClickFunc);
      this.swapOffhandClickFunc = Optional.ofNullable(swapOffhandClickFunc);
      this.modelFunction = byteFunction;
      this.amountFunction = amountFunction;
      this.blinkFunc = Optional.ofNullable(blinkFunc);
      this.hideFlags = hideFlags;
      this.flags = flags;
      this.enchants = enchants;
   }

   @Override
   public ItemStack build(T state, Player player) {
      if (this.initFunc != null) {
         state = this.initFunc.apply(state, player);
      }

      ItemFactory factory = this.getIconFactory(state, player);
      if (this.itemModelFunction != null) {
         factory.itemModel(this.itemModelFunction.apply(state, player));
      }

      if (this.tooltipStyleFunction != null) {
         factory.tooltipStyle(this.tooltipStyleFunction.apply(state, player));
      }

      if (this.amountFunction != null) {
         factory.amount(this.amountFunction.apply(state, player));
      }

      if (!this.enchants.isEmpty()) {
         this.enchants.forEach(factory::enchant);
      }

      factory.beginBatch();

      try {
         if (this.skullTexture != null) {
            factory.skullTexture(this.skullTexture.apply(state, player));
         }

         if (this.modelFunction != null) {
            factory.model(this.modelFunction.apply(state, player));
         }

         if (this.glintFunc != null) {
            factory.glint(this.glintFunc.apply(state, player));
         }

         if (this.nameFunc != null) {
            factory.name(this.nameFunc.apply(state, player));
         }

         if (this.loreFunc != null) {
            factory.clearLore();
            factory.lore(this.loreFunc.apply(state, player));
         }

         if (this.hideFlags) {
            factory.hideAttributes();
         } else {
            factory.showAttributes();
         }

         if (!this.flags.isEmpty()) {
            factory.flag(this.flags);
         }
      } finally {
         factory.endBatch();
      }

      return factory.build();
   }

   @Override
   public ItemFactory getIconFactory(T state, Player player) {
      ItemFactory factory;
      if (this.blinkFunc.isPresent()) {
         this.blink = !this.blink;
         if (this.blink) {
            factory = this.blinkFunc.get().apply(state, player);
         } else if (this.itemBuilderFunc != null) {
            factory = this.itemBuilderFunc.apply(state, player);
         } else {
            factory = ItemFactory.of(this.materialFunc.apply(state, player));
         }
      } else if (this.itemBuilderFunc != null) {
         factory = this.itemBuilderFunc.apply(state, player);
      } else {
         factory = ItemFactory.of(this.materialFunc.apply(state, player));
      }

      return factory;
   }

   @Override
   public int getAmount(T state, Player player) {
      return this.amountFunction == null ? 1 : this.amountFunction.apply(state, player);
   }

   @Override
   public boolean isBlinkingIcon() {
      return this.blinkFunc.isPresent();
   }

   @Override
   public long getMonitorTimestamp() {
      return 0L;
   }

   public BiFunction<T, Player, T> getInitFunc() {
      return this.initFunc;
   }

   public BiFunction<T, Player, ItemFactory> getItemBuilderFunc() {
      return this.itemBuilderFunc;
   }

   public BiFunction<T, Player, Material> getMaterialFunc() {
      return this.materialFunc;
   }

   public BiFunction<T, Player, String> getSkullTexture() {
      return this.skullTexture;
   }

   public BiFunction<T, Player, NamespacedKey> getItemModelFunction() {
      return this.itemModelFunction;
   }

   public BiFunction<T, Player, NamespacedKey> getTooltipStyleFunction() {
      return this.tooltipStyleFunction;
   }

   public BiFunction<T, Player, Integer> getModelFunction() {
      return this.modelFunction;
   }

   public BiFunction<T, Player, Integer> getAmountFunction() {
      return this.amountFunction;
   }

   public BiFunction<T, Player, String> getNameFunc() {
      return this.nameFunc;
   }

   public BiFunction<T, Player, List<String>> getLoreFunc() {
      return this.loreFunc;
   }

   public BiFunction<T, Player, Boolean> getGlintFunc() {
      return this.glintFunc;
   }

   public BiFunction<T, Player, Chroma> getColorFunc() {
      return this.colorFunc;
   }

   @Override
   public Optional<BiConsumer<T, Player>> getClickFunc() {
      return this.clickFunc;
   }

   @Override
   public Optional<BiConsumer<T, Player>> getRightClickFunc() {
      return this.rightClickFunc;
   }

   @Override
   public Optional<BiConsumer<T, Player>> getShiftClickFunc() {
      return this.shiftClickFunc;
   }

   @Override
   public Optional<BiConsumer<T, Player>> getShiftRightClickFunc() {
      return this.shiftRightClickFunc;
   }

   @Override
   public Optional<BiConsumer<T, Player>> getDoubleClickFunc() {
      return this.doubleClickFunc;
   }

   @Override
   public Optional<BiConsumer<T, Player>> getDropClickFunc() {
      return this.dropClickFunc;
   }

   @Override
   public Optional<BiConsumer<T, Player>> getDropAllClickFunc() {
      return this.dropAllClickFunc;
   }

   @Override
   public Optional<BiConsumer<T, Player>> getMiddleClickFunc() {
      return this.middleClickFunc;
   }

   @Override
   public Optional<BiConsumer<T, Player>> getSwapOffhandClickFunc() {
      return this.swapOffhandClickFunc;
   }

   public Optional<BiFunction<T, Player, ItemFactory>> getBlinkFunc() {
      return this.blinkFunc;
   }

   public boolean isHideFlags() {
      return this.hideFlags;
   }

   public List<ItemFlag> getFlags() {
      return this.flags;
   }

   public Map<Enchantment, Integer> getEnchants() {
      return this.enchants;
   }
}
