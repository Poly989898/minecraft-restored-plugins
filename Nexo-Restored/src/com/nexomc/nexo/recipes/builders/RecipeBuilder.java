package com.nexomc.nexo.recipes.builders;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.recipes.RecipeType;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.UtilsKt;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b&\u0018\u0000 22\u00020\u0001:\u00012B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u001a\u0010\u001d\u001a\u00020\u000f2\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0006\u0010\u001e\u001a\u00020\u001fJ\u001a\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\"H&J\u0018\u0010$\u001a\u00020\u001f2\u0006\u0010%\u001a\u00020&2\u0006\u0010'\u001a\u00020(H\u0004J\u0006\u0010)\u001a\u00020\u001fJ\u000e\u0010*\u001a\u00020\u001f2\u0006\u0010\u0010\u001a\u00020\u000fJ\u0006\u0010+\u001a\u00020\u001fJ\u0016\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020/2\u0006\u00100\u001a\u000201R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001e\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u000e\u001a\u00020\u000f@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0013\u001a\u00020\u0014¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u001b\u0010\u0017\u001a\u00020\u00188FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001b\u0010\u001c\u001a\u0004\b\u0019\u0010\u001a¨\u00063"},
   d2 = {"Lcom/nexomc/nexo/recipes/builders/RecipeBuilder;", "", "player", "Lorg/bukkit/entity/Player;", "recipeType", "Lcom/nexomc/nexo/recipes/RecipeType;", "inventoryTitle", "Lnet/kyori/adventure/text/Component;", "<init>", "(Lorg/bukkit/entity/Player;Lcom/nexomc/nexo/recipes/RecipeType;Lnet/kyori/adventure/text/Component;)V", "getPlayer", "()Lorg/bukkit/entity/Player;", "getInventoryTitle", "()Lnet/kyori/adventure/text/Component;", "value", "Lorg/bukkit/inventory/Inventory;", "inventory", "getInventory", "()Lorg/bukkit/inventory/Inventory;", "configFile", "Ljava/io/File;", "getConfigFile", "()Ljava/io/File;", "config", "Lorg/bukkit/configuration/file/YamlConfiguration;", "getConfig", "()Lorg/bukkit/configuration/file/YamlConfiguration;", "config$delegate", "Lkotlin/Lazy;", "createInventory", "close", "", "saveRecipe", "name", "", "permission", "setItemStack", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "saveConfig", "setInventory", "open", "validSlot", "", "slot", "", "slotType", "Lorg/bukkit/event/inventory/InventoryType$SlotType;", "Companion", "com.nexomc:core"}
)
public abstract class RecipeBuilder {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Player player;
   @NotNull
   private final RecipeType recipeType;
   @NotNull
   private final Component inventoryTitle;
   @NotNull
   private Inventory inventory;
   @NotNull
   private final File configFile;
   @NotNull
   private final Lazy config$delegate;
   @NotNull
   private static final Map<UUID, RecipeBuilder> BUILDER_MAP = (Map)(new LinkedHashMap());

   public RecipeBuilder(@NotNull Player player, @NotNull RecipeType recipeType) {
      this(player, recipeType, Component.text(player.getName() + " " + recipeType.getId() + " builder"));
   }

   public RecipeBuilder(@NotNull Player player, @NotNull RecipeType recipeType, @NotNull Component inventoryTitle) {
      Inventory inventory2;
      RecipeBuilder recipeBuilder;
      block: {
         /* implicit super() */
         this.player = player;
         this.recipeType = recipeType;
         this.inventoryTitle = inventoryTitle;
         recipeBuilder = this;
         Companion companion = Companion;
         UUID uniqueId = this.player.getUniqueId();
         RecipeBuilder recipeBuilder2 = companion.currentBuilder(uniqueId);
         if (recipeBuilder2 != null) {
            RecipeBuilder recipeBuilder3 = recipeBuilder2;
            int value = 0;
            boolean flag = recipeBuilder3.recipeType == this.recipeType;
            recipeBuilder = this;
            recipeBuilder2 = flag ? recipeBuilder3 : null;
            if ((flag ? recipeBuilder3 : null) != null) {
               inventory2 = recipeBuilder2.inventory;
               if (inventory2 != null) {
                  break block;
               }
            }
         }

         inventory2 = this.createInventory(this.player, this.inventoryTitle);
      }

      recipeBuilder.inventory = inventory2;
      File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      String[] strs = new String[1];
      String id = this.recipeType.getId();
      strs[0] = "recipes/" + id + "/" + this.recipeType.getId() + "_recipes.yml";
      File file = FileUtilsKt.resolve(dataFolder, strs);
      int value2 = 0;
      file.getParentFile().mkdirs();
      this.configFile = file;
      this.config$delegate = LazyKt.lazy((() -> RecipeBuilder.config_delegate$lambda$0(this)));
      BUILDER_MAP.put(this.player.getUniqueId(), this);
   }

   @NotNull
   public final Player getPlayer() {
      return this.player;
   }

   @NotNull
   public final Component getInventoryTitle() {
      return this.inventoryTitle;
   }

   @NotNull
   public final Inventory getInventory() {
      return this.inventory;
   }

   @NotNull
   public final File getConfigFile() {
      return this.configFile;
   }

   @NotNull
   public final YamlConfiguration getConfig() {
      Lazy lazy = this.config$delegate;
      return (YamlConfiguration)lazy.getValue();
   }

   @NotNull
   public abstract Inventory createInventory(@Nullable Player player2, @NotNull Component component);

   public final void close() {
      BUILDER_MAP.remove(this.player.getUniqueId());
   }

   public abstract void saveRecipe(@NotNull String str, @Nullable String str2);

   protected final void setItemStack(@NotNull ConfigurationSection section, @NotNull ItemStack itemStack) {
      if (NexoItems.exists(itemStack)) {
         section.set("nexo_item", NexoItems.idFromItem(itemStack));
      } else if (itemStack.isSimilar(ItemStack.of(itemStack.getType()))) {
         section.set("minecraft_type", itemStack.getType().name());
      } else {
         section.set("minecraft_item", itemStack);
      }

      if (itemStack.getAmount() > 1) {
         section.set("amount", itemStack.getAmount());
      }

   }

   public final void saveConfig() {
      RecipeBuilder recipeBuilder = this;

      Object obj;
      try {
         RecipeBuilder recipeBuilder2 = recipeBuilder;
         int value = 0;
         recipeBuilder2.getConfig().save(recipeBuilder2.configFile);
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      UtilsKt.printOnFailure(obj, false);
   }

   public final void setInventory(@NotNull Inventory inventory) {
      this.inventory = inventory;
      BUILDER_MAP.put(this.player.getUniqueId(), this);
   }

   public final void open() {
      Inventory inventory2;
      RecipeBuilder recipeBuilder;
      block: {
         recipeBuilder = this;
         Companion companion = Companion;
         UUID uniqueId = this.player.getUniqueId();
         RecipeBuilder recipeBuilder2 = companion.currentBuilder(uniqueId);
         if (recipeBuilder2 != null) {
            RecipeBuilder recipeBuilder3 = recipeBuilder2;
            int value = 0;
            boolean flag = recipeBuilder3.recipeType == this.recipeType;
            recipeBuilder = this;
            recipeBuilder2 = flag ? recipeBuilder3 : null;
            if ((flag ? recipeBuilder3 : null) != null) {
               inventory2 = recipeBuilder2.inventory;
               if (inventory2 != null) {
                  break block;
               }
            }
         }

         inventory2 = this.createInventory(this.player, this.inventoryTitle);
      }

      recipeBuilder.inventory = inventory2;
      this.player.openInventory(this.inventory);
      BUILDER_MAP.put(this.player.getUniqueId(), this);
   }

   public final boolean validSlot(int slot, @NotNull InventoryType.SlotType slotType) {
      return !(this instanceof ShapedBuilder) && !(this instanceof ShapelessBuilder) && !(this instanceof CookingBuilder) ? (this instanceof BrewingBuilder ? slot != 4 && slot != 1 && (slotType == SlotType.FUEL || slotType == SlotType.CRAFTING) : false) : slotType == SlotType.RESULT;
   }

   private static final YamlConfiguration config_delegate$lambda$0(RecipeBuilder recipeBuilder) {
      return NexoYaml.Companion.loadConfiguration(recipeBuilder.configFile);
   }

   @JvmStatic
   @Nullable
   public static final RecipeBuilder currentBuilder(@NotNull UUID playerUUID) {
      return Companion.currentBuilder(playerUUID);
   }

   @JvmStatic
   @Nullable
   public static final <T extends RecipeBuilder> T currentBuilder(@NotNull UUID playerUUID, @NotNull Class<T> builder) {
      return (T)Companion.currentBuilder(playerUUID, builder);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\b\u001a\u0004\u0018\u00010\u00072\u0006\u0010\t\u001a\u00020\u0006H\u0007b\u0002\b\nJ3\u0010\b\u001a\u0004\u0018\u0001H\u000b\"\b\b\u0000\u0010\u000b*\u00020\u00072\u0006\u0010\t\u001a\u00020\u00062\f\u0010\f\u001a\b\u0012\u0004\u0012\u0002H\u000b0\rH\u0007b\u0002\b\n¢\u0006\u0002\u0010\u000eR\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"},
      d2 = {"Lcom/nexomc/nexo/recipes/builders/RecipeBuilder$Companion;", "", "<init>", "()V", "BUILDER_MAP", "", "Ljava/util/UUID;", "Lcom/nexomc/nexo/recipes/builders/RecipeBuilder;", "currentBuilder", "playerUUID", "Lkotlin/jvm/JvmStatic;", "T", "builder", "Ljava/lang/Class;", "(Ljava/util/UUID;Ljava/lang/Class;)Lcom/nexomc/nexo/recipes/builders/RecipeBuilder;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @Nullable
      public final RecipeBuilder currentBuilder(@NotNull UUID playerUUID) {
         return (RecipeBuilder)RecipeBuilder.BUILDER_MAP.get(playerUUID);
      }

      @JvmStatic
      @Nullable
      public final <T extends RecipeBuilder> T currentBuilder(@NotNull UUID playerUUID, @NotNull Class<T> builder) {
         RecipeBuilder recipeBuilder = (RecipeBuilder)RecipeBuilder.BUILDER_MAP.get(playerUUID);
         if (recipeBuilder != null) {
            RecipeBuilder it = recipeBuilder;
            int value = 0;
            recipeBuilder = Intrinsics.areEqual(it.getClass(), builder) ? it : null;
         } else {
            recipeBuilder = null;
         }

         RecipeBuilder recipeBuilder2 = recipeBuilder;
         return (T)(recipeBuilder2 instanceof RecipeBuilder ? recipeBuilder2 : null);
      }
   }
}
