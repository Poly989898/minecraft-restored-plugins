package com.nexomc.nexo.pack.multipack.gui;

import com.nexomc.libs.gui.builder.gui.ChestGuiBuilder;
import com.nexomc.libs.gui.guis.Gui;
import com.nexomc.libs.gui.guis.GuiItem;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.pack.multipack.MultipackManager;
import com.nexomc.nexo.pack.multipack.PackTemplate;
import com.nexomc.nexo.pack.multipack.PlayerPackData;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0006\u0010\u0013\u001a\u00020\u0014J\b\u0010\u0015\u001a\u00020\u0011H\u0002J\u0018\u0010\u0016\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\rH\u0002J\u0012\u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010\u0017\u001a\u00020\rH\u0002J\u0010\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u0017\u001a\u00020\rH\u0002J\b\u0010\u001b\u001a\u00020\u0014H\u0002J\b\u0010\u001c\u001a\u00020\u0014H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b0\tX\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\f\u001a\u0014\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001d"},
   d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackGui;", "", "config", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackMenuConfig;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lcom/nexomc/nexo/pack/multipack/gui/MultipackMenuConfig;Lorg/bukkit/entity/Player;)V", "pending", "", "", "", "renderedPositions", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotConfig;", "", "", "gui", "Lcom/nexomc/libs/gui/guis/Gui;", "committed", "open", "", "buildGui", "applySlot", "slot", "currentItemStack", "Lorg/bukkit/inventory/ItemStack;", "dispatchClick", "refreshStatefulSlots", "commit", "com.nexomc:core"}
)
public final class MultipackGui {
   @NotNull
   private final MultipackMenuConfig config;
   @NotNull
   private final Player player;
   @NotNull
   private final Map<String, Boolean> pending;
   @NotNull
   private final Map<MultipackSlotConfig, List<Integer>> renderedPositions;
   private Gui gui;
   private boolean committed;

   public MultipackGui(@NotNull MultipackMenuConfig config, @NotNull Player player) {
      super();
      this.config = config;
      this.player = player;
      this.renderedPositions = (Map)(new LinkedHashMap());
      MultipackManager manager = NexoPlugin.Companion.instance().multipackManager();
      Map current = PlayerPackData.INSTANCE.getSelections((OfflinePlayer)this.player);
      Iterable templates = (Iterable)manager.getTemplates();
      int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(templates, 10)), 16);
      Map destination = (Map)(new LinkedHashMap(capacity));

      for(Object element : templates) {
         PackTemplate template = (PackTemplate)element;
         int value = 0;
         String id = template.getId();
         Boolean flag = (Boolean)current.get(template.getId());
         Pair pair = TuplesKt.to(id, flag != null ? flag : template.getDefault());
         destination.put(pair.getFirst(), pair.getSecond());
      }

      this.pending = MapsKt.toMutableMap(destination);
   }

   public final void open() {
      this.gui = this.buildGui();
      Gui gui2 = this.gui;
      if (gui2 == null) {
         Intrinsics.throwUninitializedPropertyAccessException("gui");
         gui2 = null;
      }

      gui2.open((HumanEntity)this.player);
   }

   private final Gui buildGui() {
      Gui gui2 = ((ChestGuiBuilder)((ChestGuiBuilder)((ChestGuiBuilder)((ChestGuiBuilder)Gui.gui().rows(this.config.getRows())).title(this.config.getTitle())).disableAllInteractions()).inventory(MultipackGui::buildGui$lambda$0)).create();
      Gui gui = gui2;
      gui.setCloseGuiAction((a0_ -> MultipackGui.buildGui$lambda$1(this, (InventoryCloseEvent)a0_)));

      for(MultipackSlotConfig slot : this.config.getSlots()) {
         this.applySlot(gui, slot);
      }

      return gui;
   }

   private final void applySlot(Gui gui, MultipackSlotConfig slot) {
      List positions = (List)(new ArrayList());
      int dy = 0;

      for(int i = slot.getSize().y; dy < i; ++dy) {
         int dx = 0;

         for(int j = slot.getSize().x; dx < j; ++dx) {
            int col = slot.getOffset().x + dx;
            int row = slot.getOffset().y + dy;
            if ((1 <= col ? col < 10 : false) && (1 <= row ? row <= this.config.getRows() : false)) {
               boolean isFirst = dx == 0 && dy == 0;
               ItemStack item = this.currentItemStack(slot);
               ItemStack itemStack;
               if (!isFirst && slot.getAllSlotsExceptFirstEmpty()) {
                  itemStack = MultipackSlotItem.Companion.emptyPlaceholder(item);
               } else {
                  itemStack = item;
                  if (item == null) {
                     MultipackGui multipackGui = this;
                     int value = 0;
                     Logs.logWarn("Multipack GUI slot <aqua>" + slot.getId() + "</aqua> could not resolve item for current state, skipping cell.", false);
                     continue;
                  }
               }

               ItemStack stack = itemStack;
               gui.setItem(row, col, new GuiItem(stack, (a0_ -> MultipackGui.applySlot$lambda$1(this, slot, (InventoryClickEvent)a0_))));
               if (!slot.getAllSlotsExceptFirstEmpty() || isFirst) {
                  ((Collection)positions).add((row - 1) * 9 + (col - 1));
               }
            }
         }
      }

      if (!((Collection)positions).isEmpty()) {
         this.renderedPositions.put(slot, positions);
      }

   }

   private final ItemStack currentItemStack(MultipackSlotConfig slot) {
      MultipackSlotAction action = slot.getAction();
      MultipackSlotItem multipackSlotItem;
      if (action instanceof MultipackSlotAction.Toggle) {
         multipackSlotItem = Intrinsics.areEqual(this.pending.get(((MultipackSlotAction.Toggle)action).getTarget()), true) ? ((MultipackSlotAction.Toggle)action).getEnabledItem() : ((MultipackSlotAction.Toggle)action).getDisabledItem();
      } else {
         if (!(action instanceof MultipackSlotAction.Cycling)) {
            throw new NoWhenBranchMatchedException();
         }

         Iterable entries = (Iterable)((MultipackSlotAction.Cycling)action).getEntries();
         Iterator iterator = entries.iterator();

         Object obj;
         while(true) {
            if (!iterator.hasNext()) {
               obj = null;
               break;
            }

            Object element = iterator.next();
            MultipackSlotAction.CyclingEntry it = (MultipackSlotAction.CyclingEntry)element;
            int value = 0;
            if (Intrinsics.areEqual(this.pending.get(it.getTemplateId()), true)) {
               obj = element;
               break;
            }
         }

         multipackSlotItem = (MultipackSlotAction.CyclingEntry)obj != null ? ((MultipackSlotAction.CyclingEntry)obj).getItem() : null;
      }

      MultipackSlotItem stateItem = multipackSlotItem;
      multipackSlotItem = stateItem;
      if (stateItem == null) {
         multipackSlotItem = slot.getBaseItem();
      }

      return multipackSlotItem != null ? multipackSlotItem.buildItemStack() : null;
   }

   private final void dispatchClick(MultipackSlotConfig slot) {
      MultipackManager manager = NexoPlugin.Companion.instance().multipackManager();
      MultipackSlotAction action = slot.getAction();
      if (action instanceof MultipackSlotAction.Toggle) {
         Iterable templates = (Iterable)manager.getTemplates();
         Iterator iterator = templates.iterator();

         Object obj;
         while(true) {
            if (!iterator.hasNext()) {
               obj = null;
               break;
            }

            Object obj2 = iterator.next();
            PackTemplate it = (PackTemplate)obj2;
            int value = 0;
            if (Intrinsics.areEqual(it.getId(), ((MultipackSlotAction.Toggle)action).getTarget()) && !it.getRequired()) {
               obj = obj2;
               break;
            }
         }

         PackTemplate packTemplate = (PackTemplate)obj;
         if ((PackTemplate)obj == null) {
            return;
         }

         PackTemplate template = packTemplate;
         Boolean flag = (Boolean)this.pending.get(((MultipackSlotAction.Toggle)action).getTarget());
         boolean newState = !(flag != null ? flag : template.getDefault());
         this.pending.put(((MultipackSlotAction.Toggle)action).getTarget(), newState);
         if (newState) {
            for(PackTemplate other : manager.getTemplates()) {
               if (template.conflictsWith(other)) {
                  this.pending.put(other.getId(), false);
               }
            }
         }
      } else {
         if (!(action instanceof MultipackSlotAction.Cycling)) {
            throw new NoWhenBranchMatchedException();
         }

         Iterable entries = (Iterable)((MultipackSlotAction.Cycling)action).getEntries();
         int value8 = 0;
         Collection destination = (Collection)(new ArrayList());
         int value9 = 0;

         for(Object element : entries) {
            int value2 = 0;
            MultipackSlotAction.CyclingEntry entry = (MultipackSlotAction.CyclingEntry)element;
            int value3 = 0;
            Iterable iterable = (Iterable)manager.getTemplates();
            Iterator iterator2 = iterable.iterator();

            Object obj3;
            while(true) {
               if (!iterator2.hasNext()) {
                  obj3 = null;
                  break;
               }

               Object obj4 = iterator2.next();
               PackTemplate it = (PackTemplate)obj4;
               int value4 = 0;
               if (Intrinsics.areEqual(it.getId(), entry.getTemplateId()) && !it.getRequired()) {
                  obj3 = obj4;
                  break;
               }
            }

            String str = (PackTemplate)obj3 != null ? ((PackTemplate)obj3).getId() : null;
            if (str != null) {
               Object obj5 = str;
               int value5 = 0;
               destination.add(obj5);
            }
         }

         List validIds = (List)destination;
         if (validIds.isEmpty()) {
            return;
         }

         value8 = 0;
         value9 = 0;
         Iterator iterator3 = validIds.iterator();

         int value6;
         while(true) {
            if (!iterator3.hasNext()) {
               value6 = -1;
               break;
            }

            Object item = iterator3.next();
            String it = (String)item;
            int value7 = 0;
            if (Intrinsics.areEqual(this.pending.get(it), true)) {
               value6 = value9;
               break;
            }

            ++value9;
         }

         int value10 = value6;
         int currentIdx = Math.max(value8, value10);

         for(String tid : (java.lang.Iterable<String>)(java.lang.Iterable)validIds) {
            this.pending.put(tid, false);
         }

         this.pending.put((String)validIds.get((currentIdx + 1) % validIds.size()), true);
      }

      this.refreshStatefulSlots();
   }

   private final void refreshStatefulSlots() {
      for(Map.Entry entry : this.renderedPositions.entrySet()) {
         MultipackSlotConfig slot = (MultipackSlotConfig)entry.getKey();
         List positions = (List)entry.getValue();
         ItemStack itemStack = this.currentItemStack(slot);
         if (itemStack != null) {
            ItemStack stack = itemStack;
            Iterable iterable = (Iterable)positions;
            int index = 0;

            for(Object item : iterable) {
               int i = index++;
               if (i < 0) {
                  CollectionsKt.throwIndexOverflow();
               }

               int pos = ((Number)item).intValue();
               int value = 0;
               Gui gui2 = this.gui;
               if (gui2 == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("gui");
                  gui2 = null;
               }

               ItemStack itemStack2;
               if (i == 0) {
                  itemStack2 = stack;
               } else {
                  itemStack2 = stack.clone();
               }

               gui2.updateItem(pos, itemStack2);
            }
         }
      }

   }

   private final void commit() {
      if (!this.committed) {
         this.committed = true;
         MultipackManager manager = NexoPlugin.Companion.instance().multipackManager();
         Iterable templates = (Iterable)manager.getTemplates();
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(templates, 10)));

         for(Object item : templates) {
            PackTemplate it = (PackTemplate)item;
            int value = 0;
            destination.add(it.getId());
         }

         Set relevant = CollectionsKt.toSet((Iterable)((List)destination));
         Map selections = PlayerPackData.INSTANCE.getSelections((OfflinePlayer)this.player);
         LinkedHashMap result = new LinkedHashMap();

         for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)selections.entrySet()) {
            String it = (String)entry.getKey();
            int value2 = 0;
            if (relevant.contains(it)) {
               result.put(entry.getKey(), entry.getValue());
            }
         }

         Map currentSelections = (Map)result;
         Map map = this.pending;
         LinkedHashMap linkedHashMap = new LinkedHashMap();

         for(Map.Entry entry2 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            String it = (String)entry2.getKey();
            int value3 = 0;
            if (relevant.contains(it)) {
               linkedHashMap.put(entry2.getKey(), entry2.getValue());
            }
         }

         map = (Map)linkedHashMap;
         if (!Intrinsics.areEqual(currentSelections, map)) {
            PlayerPackData.INSTANCE.setSelections(this.player, this.pending);
            this.player.sendRichMessage("<green>Applying multipack changes...");
            NexoPlugin.Companion.instance().packServer().sendPack(this.player);
         }
      }
   }

   private static final Inventory buildGui$lambda$0(Component title, InventoryHolder owner, int size) {
      return Bukkit.createInventory(owner, size, title);
   }

   private static final void buildGui$lambda$1(MultipackGui multipackGui, InventoryCloseEvent it) {
      multipackGui.commit();
   }

   private static final void applySlot$lambda$1(MultipackGui multipackGui, MultipackSlotConfig slot, InventoryClickEvent event) {
      if (event.getWhoClicked() instanceof Player) {
         multipackGui.dispatchClick(slot);
      }

   }
}
