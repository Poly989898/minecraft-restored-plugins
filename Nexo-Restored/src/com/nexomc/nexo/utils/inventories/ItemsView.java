package com.nexomc.nexo.utils.inventories;

import com.nexomc.libs.gui.builder.gui.BaseChestGuiBuilder;
import com.nexomc.libs.gui.builder.gui.PaginatedBuilder;
import com.nexomc.libs.gui.guis.Gui;
import com.nexomc.libs.gui.guis.GuiItem;
import com.nexomc.libs.gui.guis.PaginatedGui;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.NexoPack;
import com.nexomc.nexo.configs.ConfigsManager;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.VersionUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.RegistryBuilderFactory;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickCallback.Options;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.Item;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u00002\u00020\u0001:\u0003-./B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\n\u0010\u001f\u001a\u00060 R\u00020\u0000J\u0010\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$H\u0002J\u0012\u0010%\u001a\u00020&2\b\b\u0002\u0010'\u001a\u00020(H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u0010\b\u001a\u00020\t8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000bR\u001b\u0010\u000e\u001a\u00020\t8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0010\u0010\r\u001a\u0004\b\u000f\u0010\u000bR\u001b\u0010\u0011\u001a\u00020\t8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0013\u0010\r\u001a\u0004\b\u0012\u0010\u000bR\u001b\u0010\u0014\u001a\u00020\t8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0016\u0010\r\u001a\u0004\b\u0015\u0010\u000bR\u001b\u0010\u0017\u001a\u00020\u00188BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001b\u0010\r\u001a\u0004\b\u0019\u0010\u001aR\u0015\u0010)\u001a\u00020**\u00020$8F¢\u0006\u0006\u001a\u0004\b+\u0010,¨\u00060"},
   d2 = {"Lcom/nexomc/nexo/utils/inventories/ItemsView;", "", "<init>", "()V", "inventory", "Lorg/bukkit/configuration/file/YamlConfiguration;", "type", "Lcom/nexomc/nexo/utils/inventories/NexoInventory$Type;", "nextPage", "Lorg/bukkit/inventory/ItemStack;", "getNextPage", "()Lorg/bukkit/inventory/ItemStack;", "nextPage$delegate", "Lkotlin/Lazy;", "previousPage", "getPreviousPage", "previousPage$delegate", "exitPage", "getExitPage", "exitPage$delegate", "searchItem", "getSearchItem", "searchItem$delegate", "directoryItem", "Lcom/nexomc/nexo/items/ItemBuilder;", "getDirectoryItem", "()Lcom/nexomc/nexo/items/ItemBuilder;", "directoryItem$delegate", "builderFromIcon", "icon", "Lcom/nexomc/nexo/utils/inventories/NexoInventory;", "create", "Lcom/nexomc/nexo/utils/inventories/ItemsView$DirectoryGui;", "guiItemSlot", "Lcom/nexomc/nexo/utils/inventories/ItemsView$GuiItemSlot;", "file", "Ljava/io/File;", "createPaginatedBase", "Lcom/nexomc/libs/gui/builder/gui/PaginatedBuilder;", "title", "Lnet/kyori/adventure/text/Component;", "layoutPath", "", "getLayoutPath", "(Ljava/io/File;)Ljava/lang/String;", "SearchFilterCallback", "DirectoryGui", "GuiItemSlot", "com.nexomc:core"}
)
public final class ItemsView {
   @NotNull
   private final YamlConfiguration inventory;
   @NotNull
   private final NexoInventory.Type type;
   @NotNull
   private final Lazy nextPage$delegate;
   @NotNull
   private final Lazy previousPage$delegate;
   @NotNull
   private final Lazy exitPage$delegate;
   @NotNull
   private final Lazy searchItem$delegate;
   @NotNull
   private final Lazy directoryItem$delegate;

   public ItemsView() {
      this.inventory = NexoPlugin.Companion.instance().configsManager().inventory();
      this.type = NexoInventory.TYPE.toEnum(NexoInventory.Type.class, NexoInventory.Type.FILE);
      this.nextPage$delegate = LazyKt.lazy((() -> ItemsView.nextPage_delegate$lambda$0(this)));
      this.previousPage$delegate = LazyKt.lazy((() -> ItemsView.previousPage_delegate$lambda$0(this)));
      this.exitPage$delegate = LazyKt.lazy((() -> ItemsView.exitPage_delegate$lambda$0(this)));
      this.searchItem$delegate = LazyKt.lazy((() -> ItemsView.searchItem_delegate$lambda$0(this)));
      this.directoryItem$delegate = LazyKt.lazy((() -> ItemsView.directoryItem_delegate$lambda$0(this)));
   }

   private final ItemStack getNextPage() {
      Lazy lazy = this.nextPage$delegate;
      return (ItemStack)lazy.getValue();
   }

   private final ItemStack getPreviousPage() {
      Lazy lazy = this.previousPage$delegate;
      return (ItemStack)lazy.getValue();
   }

   private final ItemStack getExitPage() {
      Lazy lazy = this.exitPage$delegate;
      return (ItemStack)lazy.getValue();
   }

   private final ItemStack getSearchItem() {
      Lazy lazy = this.searchItem$delegate;
      return (ItemStack)lazy.getValue();
   }

   private final ItemBuilder getDirectoryItem() {
      Lazy lazy = this.directoryItem$delegate;
      return (ItemBuilder)lazy.getValue();
   }

   private final ItemBuilder builderFromIcon(NexoInventory icon) {
      Item item = NexoPack.resourcePack().item(icon.toKey());
      Key itemModel = item != null ? item.key() : null;
      ItemBuilder itemBuilder = NexoItems.itemFromId(StringsKt.substringAfter(icon.toString(), ":", icon.toString()));
      if (itemBuilder != null) {
         itemBuilder = itemBuilder.clone();
         if (itemBuilder != null) {
            return itemBuilder;
         }
      }

      itemBuilder = (new ItemBuilder(Material.PAPER)).setItemModel(itemModel);
      return itemBuilder;
   }

   @NotNull
   public final DirectoryGui create() {
      DirectoryGui directoryGui = new DirectoryGui(ConfigsManager.Companion.getItemsFolder$com_nexomc_core(), (PaginatedGui)null, "");
      int value = 0;
      directoryGui.createDirectoryFileGUI();
      return directoryGui;
   }

   private final GuiItemSlot guiItemSlot(File file) {
      com.nexomc.nexo.items.ItemBuilder itemBuilder2;
      String layoutPath = this.getLayoutPath(file);
      String str;
      if (NexoInventory.STYLE_DEFAULT_NAMES.toBool()) {
         CharSequence nameWithoutExtension = (CharSequence)FilesKt.getNameWithoutExtension(file);
         String[] strs = new String[]{"_", "-"};
         str = CollectionsKt.joinToString((Iterable)StringsKt.split(nameWithoutExtension, strs, false, 0), (CharSequence)" ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", ItemsView::guiItemSlot$lambda$0);
      } else {
         str = file.getName();
      }

      String defaultName = str;
      AdventureUtils adventureUtils = AdventureUtils.INSTANCE;
      String str2 = NexoYamlKt.stringOrNull((ConfigurationSection)this.inventory, "NexoInventory.layout." + layoutPath + ".itemname");
      if (str2 == null) {
         str2 = defaultName;
      }

      String icon = str2;
      Component itemName = adventureUtils.setDefaultStyle(AdventureUtilsKt.deserialize(icon), (TextColor)NamedTextColor.GREEN);
      String string = this.inventory.getString("NexoInventory.layout." + layoutPath + ".icon");
      if (string == null) {
         string = "";
      }

      block: {
         icon = string;
         ItemBuilder itemBuilder3 = NexoItems.itemFromId(icon);
         if (itemBuilder3 != null) {
            itemBuilder2 = itemBuilder3.clone();
            if (itemBuilder2 != null) {
               break block;
            }
         }

         if (file.isDirectory()) {
            itemBuilder2 = this.getDirectoryItem().clone();
         } else {
            block2: {
               Item item = NexoPack.resourcePack().item(Key.key(icon));
               if (item != null) {
                  Key it = item.key();
                  if (it != null) {
                     int value = 0;
                     ItemBuilder itemBuilder4 = (new ItemBuilder(Material.PAPER)).setItemModel(it);
                     if (itemBuilder4 != null) {
                        itemBuilder2 = itemBuilder4;
                        break block2;
                     }
                  }
               }

               Map map = (Map)NexoItems.itemMap().get(file);
               if (map != null) {
                  Collection collection = map.values();
                  if (collection != null) {
                     ItemBuilder itemBuilder5 = (ItemBuilder)CollectionsKt.firstOrNull((Iterable)collection);
                     if (itemBuilder5 != null) {
                        itemBuilder2 = itemBuilder5.clone();
                        break block2;
                     }
                  }
               }

               itemBuilder2 = null;
            }
         }

         if (itemBuilder2 == null) {
            itemBuilder2 = new ItemBuilder(Material.PAPER);
         }
      }

      ItemBuilder itemBuilder = itemBuilder2;
      ItemBuilder itemBuilder6 = itemBuilder.itemName(itemName).lore(CollectionsKt.emptyList());
      Key[] keys = new Key[1];
      Key key = DataComponentTypes.ATTRIBUTE_MODIFIERS.key();
      keys[0] = key;
      ItemStack itemStack = itemBuilder6.hideComponents(keys).build();
      int slot = this.inventory.getInt("NexoInventory.layout." + layoutPath + ".slot", 0) - 1;
      return new GuiItemSlot(itemStack, slot);
   }

   private final PaginatedBuilder createPaginatedBase(Component title) {
      BaseChestGuiBuilder baseChestGuiBuilder = (((Gui.paginated().rows(NexoInventory.ROWS.toInt())).pageSize(NexoInventory.SIZE.toInt(45)).title(title)).disableAllInteractions()).inventory(ItemsView::createPaginatedBase$lambda$0);
      return (PaginatedBuilder)baseChestGuiBuilder;
   }

   @NotNull
   public final String getLayoutPath(@NotNull File file) {
      String str;
      switch (ItemsView.WhenMappings.$EnumSwitchMapping$0[this.type.ordinal()]) {
         case 1:
            str = FilesKt.relativeTo(file, ConfigsManager.Companion.getItemsFolder$com_nexomc_core()).getPath();
            str = StringsKt.removeSuffix(StringsKt.replace(StringsKt.substringBeforeLast(str, ".", str), "/", ".", false), (CharSequence)".");
            break;
         case 2:
            str = FilesKt.getNameWithoutExtension(file);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return str;
   }

   private static final ItemStack nextPage_delegate$lambda$0(ItemsView itemsView) {
      return itemsView.builderFromIcon(NexoInventory.NEXT_ICON).itemName((Component)Component.text("Next Page")).build();
   }

   private static final ItemStack previousPage_delegate$lambda$0(ItemsView itemsView) {
      return itemsView.builderFromIcon(NexoInventory.PREVIOUS_ICON).itemName((Component)Component.text("Previous Page")).build();
   }

   private static final ItemStack exitPage_delegate$lambda$0(ItemsView itemsView) {
      return itemsView.builderFromIcon(NexoInventory.EXIT_ICON).itemName((Component)Component.text("Exit")).build();
   }

   private static final ItemStack searchItem_delegate$lambda$0(ItemsView itemsView) {
      ItemBuilder itemBuilder = itemsView.builderFromIcon(NexoInventory.SEARCH_ICON);
      boolean condition = VersionUtil.INSTANCE.below("1.21.6");
      if (condition) {
         int value = 0;
         Component[] components = new Component[]{AdventureUtilsKt.deserialize("<red>This is only available for 1.21.6+ Servers at the moment")};
         itemBuilder.lore(components);
      }

      return itemBuilder.itemName((Component)Component.text("Search")).build();
   }

   private static final ItemBuilder directoryItem_delegate$lambda$0(ItemsView itemsView) {
      return itemsView.builderFromIcon(NexoInventory.DIRECTORY_ICON);
   }

   private static final CharSequence guiItemSlot$lambda$0(String it) {
      String str;
      if (((CharSequence)it).length() > 0) {
         StringBuilder stringBuilder = new StringBuilder();
         char it_10 = it.charAt(0);
         StringBuilder stringBuilder2 = stringBuilder;
         int value = 0;
         String str2 = String.valueOf(it_10);
         str2 = str2.toUpperCase(Locale.ROOT);
         StringBuilder stringBuilder3 = stringBuilder2.append((CharSequence)str2);
         value = 1;
         String str3 = it.substring(value);
         str = stringBuilder3.append(str3).toString();
      } else {
         str = it;
      }

      return (CharSequence)str;
   }

   private static final Inventory createPaginatedBase$lambda$0(Component title, InventoryHolder owner, int size) {
      return Bukkit.createInventory(owner, size, title);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u000e\u0010\f\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u000f"},
      d2 = {"Lcom/nexomc/nexo/utils/inventories/ItemsView$SearchFilterCallback;", "Lio/papermc/paper/registry/data/dialog/action/DialogActionCallback;", "<init>", "()V", "SEARCH_CALLBACK", "", "accept", "", "response", "Lio/papermc/paper/dialog/DialogResponseView;", "audience", "Lnet/kyori/adventure/audience/Audience;", "openSearchDialogScreen", "player", "Lorg/bukkit/entity/Player;", "com.nexomc:core"}
   )
   public static final class SearchFilterCallback implements DialogActionCallback {
      @NotNull
      public static final SearchFilterCallback INSTANCE = new SearchFilterCallback();
      @NotNull
      private static final String SEARCH_CALLBACK = "nexo_inventory_search";

      private SearchFilterCallback() {
      }

      public void accept(@NotNull DialogResponseView response, @NotNull Audience audience) {
         Player player2 = audience instanceof Player ? (Player)audience : null;
         if ((audience instanceof Player ? (Player)audience : null) != null) {
            Player player = player2;
            DirectoryGui directoryGui = NexoPlugin.Companion.instance().getInvManager$com_nexomc_core().itemsView(player);
            int value = 0;
            String text = response.getText("nexo_inventory_search");
            if (text == null) {
               text = "";
            }

            directoryGui.setSearchFilter(text);
            directoryGui.reload();
            directoryGui.getGui().open((HumanEntity)player);
         }
      }

      public final void openSearchDialogScreen(@NotNull Player player) {
         String initial = NexoPlugin.Companion.instance().getInvManager$com_nexomc_core().itemsView(player).getSearchFilter();
         Dialog dialog2 = Dialog.create((a0_ -> SearchFilterCallback.openSearchDialogScreen$lambda$0(initial, (RegistryBuilderFactory)a0_)));
         Dialog dialog = dialog2;
         player.showDialog((DialogLike)dialog);
      }

      private static final void openSearchDialogScreen$lambda$0(String initial, RegistryBuilderFactory builder) {
         ((DialogRegistryEntry.Builder)builder.empty()).type((DialogType)DialogType.notice(ActionButton.builder((Component)Component.text("Filter NexoInventory")).action((DialogAction)DialogAction.customClick(INSTANCE, Options.builder().build())).build())).base(DialogBase.builder(NexoInventory.SEARCH_TITLE.toComponent()).canCloseWithEscape(true).inputs(CollectionsKt.listOf(DialogInput.text("nexo_inventory_search", (Component)Component.text("Filter NexoItems containing...")).initial(initial).build())).build());
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0086\u0004\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0006\u0010\u0019\u001a\u00020\u001aJ\b\u0010\u001b\u001a\u0004\u0018\u00010\u0005J\u001a\u0010\u001c\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u001d\u001a\u00020\u0003H\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u0013\u0010\u0012\u001a\u00070\u0007¢\u0006\u0002\b\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\r¨\u0006\u001e"},
      d2 = {"Lcom/nexomc/nexo/utils/inventories/ItemsView$DirectoryGui;", "", "directory", "Ljava/io/File;", "parentGui", "Lcom/nexomc/libs/gui/guis/PaginatedGui;", "searchFilter", "", "<init>", "(Lcom/nexomc/nexo/utils/inventories/ItemsView;Ljava/io/File;Lcom/nexomc/libs/gui/guis/PaginatedGui;Ljava/lang/String;)V", "getDirectory", "()Ljava/io/File;", "getParentGui", "()Lcom/nexomc/libs/gui/guis/PaginatedGui;", "getSearchFilter", "()Ljava/lang/String;", "setSearchFilter", "(Ljava/lang/String;)V", "title", "Lorg/jetbrains/annotations/Nullable;", "titleComponent", "Lnet/kyori/adventure/text/Component;", "value", "gui", "getGui", "reload", "", "createDirectoryFileGUI", "createItemFileGUI", "file", "com.nexomc:core"}
   )
   public final class DirectoryGui {
      @NotNull
      private final File directory;
      @Nullable
      private final PaginatedGui parentGui;
      @NotNull
      private String searchFilter;
      @NotNull
      private final String title;
      @NotNull
      private final Component titleComponent;
      @NotNull
      private PaginatedGui gui;

      public DirectoryGui(@NotNull File directory, @Nullable PaginatedGui parentGui, @NotNull String searchFilter) {
         super();
         this.directory = directory;
         this.parentGui = parentGui;
         this.searchFilter = searchFilter;
         String string = ItemsView.this.inventory.getString("NexoInventory.layout." + ItemsView.this.getLayoutPath(this.directory) + ".title");
         if (string == null) {
            string = NexoInventory.TITLE.toString();
         }

         this.title = string;
         this.titleComponent = AdventureUtilsKt.deserialize(StringsKt.replace(this.title, "<main_menu_title>", NexoInventory.TITLE.toString(), false));
         PaginatedGui paginatedGui = ItemsView.this.createPaginatedBase(this.titleComponent).create();
         this.gui = paginatedGui;
         GuiItem prevItem = new GuiItem(ItemsView.this.getPreviousPage(), (a0_ -> DirectoryGui._init_$lambda$0(this, a0_)));
         GuiItem nextItem = new GuiItem(ItemsView.this.getNextPage(), (a0_ -> DirectoryGui._init_$lambda$1(this, a0_)));
         GuiItem exitItem = new GuiItem(ItemsView.this.getExitPage(), (a0_ -> DirectoryGui._init_$lambda$2(this, a0_)));
         GuiItem searchItem = new GuiItem(ItemsView.this.getSearchItem(), DirectoryGui::_init_$lambda$3);
         this.gui.setOpenGuiAction((a0_ -> DirectoryGui._init_$lambda$4(this, a0_)));
         this.gui.setItem(6, 3, prevItem);
         this.gui.setItem(6, 7, nextItem);
         this.gui.setItem(6, 5, exitItem);
         this.gui.setItem(6, 9, searchItem);
      }

      @NotNull
      public final File getDirectory() {
         return this.directory;
      }

      @Nullable
      public final PaginatedGui getParentGui() {
         return this.parentGui;
      }

      @NotNull
      public final String getSearchFilter() {
         return this.searchFilter;
      }

      public final void setSearchFilter(@NotNull String str) {
         this.searchFilter = str;
      }

      @NotNull
      public final PaginatedGui getGui() {
         return this.gui;
      }

      public final void reload() {
         DirectoryGui directoryGui = ItemsView.this.new DirectoryGui(ConfigsManager.Companion.getItemsFolder$com_nexomc_core(), this.parentGui, this.searchFilter);
         int value = 0;
         directoryGui.createDirectoryFileGUI();
         this.gui = directoryGui.gui;
      }

      @Nullable
      public final PaginatedGui createDirectoryFileGUI() {
         File[] files2 = this.directory.listFiles(DirectoryGui::createDirectoryFileGUI$lambda$0);
         if (files2 != null) {
            File[] files3 = files2;
            if (files3.length == 0) {
               int value = 0;
               files2 = null;
            } else {
               files2 = files3;
            }

            if (files2 != null) {
               File[] files = files2;
               if (ItemsView.this.type == NexoInventory.Type.FILE) {
                  File[] files4 = files;
                  Collection destination = (Collection)(new ArrayList());
                  int thisCollection = 0;

                  for(int i = files.length; thisCollection < i; ++thisCollection) {
                     Object element = files4[thisCollection];
                     int value2 = 0;
                     Iterable list = (Iterable)FileUtilsKt.listYamlFiles((File)element, true);
                     CollectionsKt.addAll(destination, list);
                  }

                  Collection collection = (Collection)CollectionsKt.distinct((Iterable)((List)destination));
                  files2 = (File[])collection.toArray(new File[0]);
               } else {
                  files2 = files;
               }

               File[] itemFiles = files2;
               ItemsView itemsView = ItemsView.this;
               LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(itemFiles.length), 16));
               File[] files5 = itemFiles;
               Map map = (Map)result;
               int value3 = 0;

               for(int j = itemFiles.length; value3 < j; ++value3) {
                  Object obj = files5[value3];
                  int value4 = 0;
                  PaginatedGui paginatedGui;
                  if (!((File)obj).isDirectory()) {
                     PaginatedGui paginatedGui2 = this.gui;
                     paginatedGui = this.createItemFileGUI(paginatedGui2, (File)obj);
                     if (paginatedGui == null) {
                        paginatedGui = null;
                     }
                  } else if (itemsView.type == NexoInventory.Type.DIRECTORY) {
                     paginatedGui = (itemsView.new DirectoryGui((File)obj, this.gui, this.searchFilter)).createDirectoryFileGUI();
                     if (paginatedGui == null) {
                        paginatedGui = null;
                     }
                  } else {
                     paginatedGui = null;
                  }

                  PaginatedGui paginatedGui3 = paginatedGui;
                  map.put(obj, paginatedGui3);
               }

               Map highestUsedSlot = CollectionUtilsKt.filterValuesNotNull(map);
               if (highestUsedSlot.isEmpty()) {
                  int value5 = 0;
                  return null;
               }

               Iterable guiItems = (Iterable)highestUsedSlot.keySet();
               ItemsView itemsView2 = ItemsView.this;
               Iterator iterator = guiItems.iterator();
               Comparable comparable;
               if (!iterator.hasNext()) {
                  comparable = null;
               } else {
                  File it = (File)iterator.next();
                  int value6 = 0;
                  Comparable comparable2 = (Comparable)itemsView2.guiItemSlot(it).getSlot();

                  while(iterator.hasNext()) {
                     File it_8 = (File)iterator.next();
                     int value10 = 0;
                     Comparable comparable3 = (Comparable)itemsView2.guiItemSlot(it_8).getSlot();
                     if (comparable2.compareTo(comparable3) < 0) {
                        comparable2 = comparable3;
                     }
                  }

                  comparable = comparable2;
               }

               Integer number = (Integer)comparable;
               int highestUsedSlot_6 = number != null ? RangesKt.coerceAtLeast((int)(number), highestUsedSlot.keySet().size() - 1) : highestUsedSlot.keySet().size() - 1;
               GuiItem emptyGuiItem = new GuiItem(Material.AIR);
               int value11 = highestUsedSlot_6 + 1;
               ArrayList arrayList = new ArrayList(value11);

               for(int k = 0; k < value11; ++k) {
                  arrayList.add(emptyGuiItem);
               }

               List guiItems_4 = (List)arrayList;
               ItemsView itemsView3 = ItemsView.this;
               Collection collection2 = (Collection)(new ArrayList());

               for(Object entry : highestUsedSlot.entrySet()) {
                  Map.Entry entry2 = (Map.Entry)entry;
                  int value7 = 0;
                  int value8 = 0;
                  File file = (File)entry2.getKey();
                  PaginatedGui gui = (PaginatedGui)entry2.getValue();
                  GuiItemSlot guiItemSlot = itemsView3.guiItemSlot(file);
                  ItemStack itemStack = guiItemSlot.component1();
                  int slot = guiItemSlot.component2();
                  if (slot != -1) {
                     guiItems_4.set(slot, new GuiItem(itemStack, (a0_ -> DirectoryGui.createDirectoryFileGUI$lambda$7$0(gui, a0_))));
                  }

                  Pair pair = slot == -1 ? TuplesKt.to(itemStack, gui) : null;
                  if (pair != null) {
                     Object obj2 = pair;
                     int value9 = 0;
                     collection2.add(obj2);
                  }
               }

               Iterable iterable = (Iterable)((List)collection2);

               for(Object obj3 : iterable) {
                  Pair itxx = (Pair)obj3;
                  ItemStack itemStack = (ItemStack)itxx.component1();
                  PaginatedGui gui = (PaginatedGui)itxx.component2();
                  guiItems_4.set(guiItems_4.indexOf(emptyGuiItem), new GuiItem(itemStack, (a0_ -> DirectoryGui.createDirectoryFileGUI$lambda$8$0(gui, a0_))));
               }

               PaginatedGui paginatedGui4 = this.gui;
               Collection collection3 = (Collection)guiItems_4;
               GuiItem[] guiItems2 = (GuiItem[])collection3.toArray(new GuiItem[0]);
               paginatedGui4.addItem(Arrays.copyOf(guiItems2, guiItems2.length));
               return this.gui;
            }
         }

         return null;
      }

      private final PaginatedGui createItemFileGUI(PaginatedGui parentGui, File file) {
         Iterable iterable = (Iterable)NexoItems.unexcludedItems(file);
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            ItemBuilder it = (ItemBuilder)element;
            int value = 0;
            String str = NexoItems.idFromItem(it);
            if (str != null ? StringsKt.contains((CharSequence)str, (CharSequence)this.searchFilter, true) : false) {
               destination.add(element);
            }
         }

         Collection title = (Collection)((List)destination);
         if (title.isEmpty()) {
            return null;
         } else {
            List items = (List)title;
            YamlConfiguration yamlConfiguration = ItemsView.this.inventory;
            String layoutPath = ItemsView.this.getLayoutPath(file);
            String string = yamlConfiguration.getString("NexoInventory.layout." + layoutPath + ".title");
            if (string == null) {
               string = NexoInventory.TITLE.toString();
            }

            String title_1 = string;
            Component titleComponent = AdventureUtilsKt.deserialize(StringsKt.replace(title_1, "<main_menu_title>", NexoInventory.TITLE.toString(), false));
            PaginatedGui paginatedGui = ItemsView.this.createPaginatedBase(titleComponent).create();
            PaginatedGui gui = paginatedGui;

            for(ItemBuilder builder : (java.lang.Iterable<ItemBuilder>)(java.lang.Iterable)items) {
               ItemStack it = builder.build();
               int value2 = 0;
               ItemStack itemStack = !it.isEmpty() ? it : null;
               if (itemStack != null) {
                  ItemStack builtItem = itemStack;
                  gui.addItem(new GuiItem(builtItem, (a0_ -> DirectoryGui.createItemFileGUI$lambda$3(builder, a0_))));
               }
            }

            if (gui.getPagesNum() > 1) {
               gui.setItem(6, 3, new GuiItem(ItemsView.this.getPreviousPage(), (a0_ -> DirectoryGui.createItemFileGUI$lambda$4(gui, a0_))));
               gui.setItem(6, 7, new GuiItem(ItemsView.this.getNextPage(), (a0_ -> DirectoryGui.createItemFileGUI$lambda$5(gui, a0_))));
            }

            gui.setItem(6, 5, new GuiItem(ItemsView.this.getExitPage(), (a0_ -> DirectoryGui.createItemFileGUI$lambda$6(parentGui, a0_))));
            gui.setItem(6, 9, new GuiItem(ItemsView.this.getSearchItem(), DirectoryGui::createItemFileGUI$lambda$7));
            return gui;
         }
      }

      private static final void _init_$lambda$0(DirectoryGui directoryGui, InventoryClickEvent it) {
         directoryGui.gui.previous();
      }

      private static final void _init_$lambda$1(DirectoryGui directoryGui, InventoryClickEvent it) {
         directoryGui.gui.next();
      }

      private static final void _init_$lambda$2(DirectoryGui directoryGui, InventoryClickEvent it) {
         PaginatedGui paginatedGui = directoryGui.parentGui;
         if (paginatedGui != null) {
            PaginatedGui p = paginatedGui;
            int value = 0;
            p.open(it.getWhoClicked(), p.getCurrentPageNum());
         } else {
            it.getWhoClicked().closeInventory();
         }

      }

      private static final void _init_$lambda$3(InventoryClickEvent it) {
         SearchFilterCallback searchFilterCallback = ItemsView.SearchFilterCallback.INSTANCE;
         HumanEntity whoClicked = it.getWhoClicked();
         searchFilterCallback.openSearchDialogScreen((Player)whoClicked);
      }

      private static final void _init_$lambda$4(DirectoryGui directoryGui, InventoryOpenEvent it) {
         directoryGui.searchFilter = "";
      }

      private static final boolean createDirectoryFileGUI$lambda$0(File it) {
         boolean flag;
         if (!it.isDirectory()) {
            if (!Intrinsics.areEqual(FilesKt.getExtension(it), "yml")) {
               flag = false;
               return flag;
            }
         }

         flag = true;
         return flag;
      }

      private static final void createDirectoryFileGUI$lambda$7$0(PaginatedGui paginatedGui, InventoryClickEvent e) {
         paginatedGui.open(e.getWhoClicked());
      }

      private static final void createDirectoryFileGUI$lambda$8$0(PaginatedGui paginatedGui, InventoryClickEvent e) {
         paginatedGui.open(e.getWhoClicked());
      }

      private static final void createItemFileGUI$lambda$3(ItemBuilder builder, InventoryClickEvent e) {
         PlayerInventory playerInventory = e.getWhoClicked().getInventory();
         ItemStack[] itemStacks = new ItemStack[]{builder.build()};
         playerInventory.addItem(itemStacks);
      }

      private static final void createItemFileGUI$lambda$4(PaginatedGui paginatedGui, InventoryClickEvent it) {
         paginatedGui.previous();
      }

      private static final void createItemFileGUI$lambda$5(PaginatedGui paginatedGui, InventoryClickEvent it) {
         paginatedGui.next();
      }

      private static final void createItemFileGUI$lambda$6(PaginatedGui paginatedGui, InventoryClickEvent it) {
         paginatedGui.open(it.getWhoClicked(), paginatedGui.getCurrentPageNum());
      }

      private static final void createItemFileGUI$lambda$7(InventoryClickEvent it) {
         SearchFilterCallback searchFilterCallback = ItemsView.SearchFilterCallback.INSTANCE;
         HumanEntity whoClicked = it.getWhoClicked();
         searchFilterCallback.openSearchDialogScreen((Player)whoClicked);
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\t\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u0086\u0002J\t\u0010\r\u001a\u00020\u0005H\u0086\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u000e"},
      d2 = {"Lcom/nexomc/nexo/utils/inventories/ItemsView$GuiItemSlot;", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "slot", "", "<init>", "(Lorg/bukkit/inventory/ItemStack;I)V", "getItemStack", "()Lorg/bukkit/inventory/ItemStack;", "getSlot", "()I", "component1", "component2", "com.nexomc:core"}
   )
   private static final class GuiItemSlot {
      @NotNull
      private final ItemStack itemStack;
      private final int slot;

      public GuiItemSlot(@NotNull ItemStack itemStack, int slot) {
         super();
         this.itemStack = itemStack;
         this.slot = slot;
      }

      @NotNull
      public final ItemStack getItemStack() {
         return this.itemStack;
      }

      public final int getSlot() {
         return this.slot;
      }

      @NotNull
      public final ItemStack component1() {
         return this.itemStack;
      }

      public final int component2() {
         return this.slot;
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.nexomc.nexo.utils.inventories.NexoInventory.Type.values().length];
         try { a_0[com.nexomc.nexo.utils.inventories.NexoInventory.Type.DIRECTORY.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.utils.inventories.NexoInventory.Type.FILE.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
