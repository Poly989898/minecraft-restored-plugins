package com.nexomc.customblockdata;

import com.nexomc.customblockdata.events.CustomBlockDataMoveEvent;
import com.nexomc.customblockdata.events.CustomBlockDataRemoveEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0002J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\bH\u0002J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\rH\u0002J\u0010\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\rH\u0002J\u0018\u0010\u0012\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u001e\u0010\u0015\u001a\u00020\u00102\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u00172\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u001e\u0010\u0019\u001a\u00020\u00102\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\b0\u00172\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\f\u0010\u001b\u001a\u00020\u0010*\u00020\u001cH\u0007J\f\u0010\u001d\u001a\u00020\u0010*\u00020\u001eH\u0007J\f\u0010\u001f\u001a\u00020\u0010*\u00020 H\u0007J\f\u0010!\u001a\u00020\u0010*\u00020\"H\u0007J\f\u0010!\u001a\u00020\u0010*\u00020#H\u0007J\f\u0010$\u001a\u00020\u0010*\u00020%H\u0007J\f\u0010&\u001a\u00020\u0010*\u00020'H\u0007J\f\u0010&\u001a\u00020\u0010*\u00020(H\u0007J\f\u0010)\u001a\u00020\u0010*\u00020*H\u0007J\f\u0010+\u001a\u00020\u0010*\u00020,H\u0007J\f\u0010-\u001a\u00020\u0010*\u00020.H\u0007J\u001e\u0010&\u001a\u00020\u00102\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\b0\u00172\u0006\u0010\u0013\u001a\u00020/H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00060"},
   d2 = {"Lcom/nexomc/customblockdata/BlockDataListener;", "Lorg/bukkit/event/Listener;", "plugin", "Lorg/bukkit/plugin/Plugin;", "<init>", "(Lorg/bukkit/plugin/Plugin;)V", "customDataPredicate", "Lkotlin/Function1;", "Lorg/bukkit/block/Block;", "", "getCbd", "Lcom/nexomc/customblockdata/CustomBlockData;", "event", "Lorg/bukkit/event/block/BlockEvent;", "block", "callAndRemove", "", "blockEvent", "callEvent", "bukkitEvent", "Lorg/bukkit/event/Event;", "callAndRemoveBlockStateList", "blockStates", "", "Lorg/bukkit/block/BlockState;", "callAndRemoveBlockList", "blocks", "onBreak", "Lorg/bukkit/event/block/BlockBreakEvent;", "onPlace", "Lorg/bukkit/event/block/BlockPlaceEvent;", "onEntity", "Lorg/bukkit/event/entity/EntityChangeBlockEvent;", "onExplode", "Lorg/bukkit/event/block/BlockExplodeEvent;", "Lorg/bukkit/event/entity/EntityExplodeEvent;", "onBurn", "Lorg/bukkit/event/block/BlockBurnEvent;", "onPiston", "Lorg/bukkit/event/block/BlockPistonExtendEvent;", "Lorg/bukkit/event/block/BlockPistonRetractEvent;", "onFade", "Lorg/bukkit/event/block/BlockFadeEvent;", "onStructure", "Lorg/bukkit/event/world/StructureGrowEvent;", "onFertilize", "Lorg/bukkit/event/block/BlockFertilizeEvent;", "Lorg/bukkit/event/block/BlockPistonEvent;", "custom-block-data"}
)
public final class BlockDataListener implements Listener {
   @NotNull
   private final Plugin plugin;
   @NotNull
   private final Function1<Block, Boolean> customDataPredicate;

   public BlockDataListener(@NotNull Plugin plugin) {
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super();
      this.plugin = plugin;
      this.customDataPredicate = BlockDataListener::customDataPredicate$lambda$0;
   }

   private final CustomBlockData getCbd(BlockEvent event) {
      Block var10001 = event.getBlock();
      Intrinsics.checkNotNullExpressionValue(var10001, "getBlock(...)");
      return this.getCbd(var10001);
   }

   private final CustomBlockData getCbd(Block block) {
      return new CustomBlockData(block, this.plugin);
   }

   private final void callAndRemove(BlockEvent blockEvent) {
      if (this.callEvent(blockEvent)) {
         this.getCbd(blockEvent).clear();
      }

   }

   private final boolean callEvent(BlockEvent blockEvent) {
      Block var10001 = blockEvent.getBlock();
      Intrinsics.checkNotNullExpressionValue(var10001, "getBlock(...)");
      return this.callEvent(var10001, (Event)blockEvent);
   }

   private final boolean callEvent(Block block, Event bukkitEvent) {
      return CustomBlockData.Companion.hasCustomBlockData(block, this.plugin) && !CustomBlockData.Companion.isProtected(block, this.plugin) ? (new CustomBlockDataRemoveEvent(this.plugin, block, bukkitEvent)).callEvent() : false;
   }

   private final void callAndRemoveBlockStateList(List<? extends BlockState> blockStates, Event bukkitEvent) {
      Iterable $this$forEach$iv = (Iterable)blockStates;

      for(Object element$iv : $this$forEach$iv) {
         BlockState it = (BlockState)element$iv;
         int var8 = 0;
         Function1 var10000 = this.customDataPredicate;
         Block var10001 = it.getBlock();
         Intrinsics.checkNotNullExpressionValue(var10001, "getBlock(...)");
         if ((Boolean)var10000.invoke(var10001)) {
            var10001 = it.getBlock();
            Intrinsics.checkNotNullExpressionValue(var10001, "getBlock(...)");
            this.callAndRemove(var10001, bukkitEvent);
         }
      }

   }

   private final void callAndRemoveBlockList(List<? extends Block> blocks, Event bukkitEvent) {
      Iterable $this$forEach$iv = (Iterable)blocks;

      for(Object element$iv : $this$forEach$iv) {
         Block it = (Block)element$iv;
         int var8 = 0;
         if ((Boolean)this.customDataPredicate.invoke(it)) {
            this.callAndRemove(it, bukkitEvent);
         }
      }

   }

   private final void callAndRemove(Block block, Event bukkitEvent) {
      if (this.callEvent(block, bukkitEvent)) {
         this.getCbd(block).clear();
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onBreak(@NotNull BlockBreakEvent $this$onBreak) {
      Intrinsics.checkNotNullParameter($this$onBreak, "<this>");
      this.callAndRemove((BlockEvent)$this$onBreak);
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onPlace(@NotNull BlockPlaceEvent $this$onPlace) {
      Intrinsics.checkNotNullParameter($this$onPlace, "<this>");
      CustomBlockData.Companion var10000 = CustomBlockData.Companion;
      Block var10001 = $this$onPlace.getBlock();
      Intrinsics.checkNotNullExpressionValue(var10001, "getBlock(...)");
      if (!var10000.isDirty(var10001)) {
         this.callAndRemove((BlockEvent)$this$onPlace);
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onEntity(@NotNull EntityChangeBlockEvent $this$onEntity) {
      Intrinsics.checkNotNullParameter($this$onEntity, "<this>");
      if ($this$onEntity.getTo() != $this$onEntity.getBlock().getType()) {
         Block var10001 = $this$onEntity.getBlock();
         Intrinsics.checkNotNullExpressionValue(var10001, "getBlock(...)");
         this.callAndRemove(var10001, (Event)$this$onEntity);
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onExplode(@NotNull BlockExplodeEvent $this$onExplode) {
      Intrinsics.checkNotNullParameter($this$onExplode, "<this>");
      List var10001 = $this$onExplode.blockList();
      Intrinsics.checkNotNullExpressionValue(var10001, "blockList(...)");
      this.callAndRemoveBlockList(var10001, (Event)$this$onExplode);
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onExplode(@NotNull EntityExplodeEvent $this$onExplode) {
      Intrinsics.checkNotNullParameter($this$onExplode, "<this>");
      List var10001 = $this$onExplode.blockList();
      Intrinsics.checkNotNullExpressionValue(var10001, "blockList(...)");
      this.callAndRemoveBlockList(var10001, (Event)$this$onExplode);
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onBurn(@NotNull BlockBurnEvent $this$onBurn) {
      Intrinsics.checkNotNullParameter($this$onBurn, "<this>");
      this.callAndRemove((BlockEvent)$this$onBurn);
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onPiston(@NotNull BlockPistonExtendEvent $this$onPiston) {
      Intrinsics.checkNotNullParameter($this$onPiston, "<this>");
      List var10001 = $this$onPiston.getBlocks();
      Intrinsics.checkNotNullExpressionValue(var10001, "getBlocks(...)");
      this.onPiston(var10001, (BlockPistonEvent)$this$onPiston);
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onPiston(@NotNull BlockPistonRetractEvent $this$onPiston) {
      Intrinsics.checkNotNullParameter($this$onPiston, "<this>");
      List var10001 = $this$onPiston.getBlocks();
      Intrinsics.checkNotNullExpressionValue(var10001, "getBlocks(...)");
      this.onPiston(var10001, (BlockPistonEvent)$this$onPiston);
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onFade(@NotNull BlockFadeEvent $this$onFade) {
      Intrinsics.checkNotNullParameter($this$onFade, "<this>");
      if ($this$onFade.getBlock().getType() != Material.FIRE) {
         if ($this$onFade.getNewState().getType() != $this$onFade.getBlock().getType()) {
            this.callAndRemove((BlockEvent)$this$onFade);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onStructure(@NotNull StructureGrowEvent $this$onStructure) {
      Intrinsics.checkNotNullParameter($this$onStructure, "<this>");
      List var10001 = $this$onStructure.getBlocks();
      Intrinsics.checkNotNullExpressionValue(var10001, "getBlocks(...)");
      this.callAndRemoveBlockStateList(var10001, (Event)$this$onStructure);
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onFertilize(@NotNull BlockFertilizeEvent $this$onFertilize) {
      Intrinsics.checkNotNullParameter($this$onFertilize, "<this>");
      List var10001 = $this$onFertilize.getBlocks();
      Intrinsics.checkNotNullExpressionValue(var10001, "getBlocks(...)");
      this.callAndRemoveBlockStateList(var10001, (Event)$this$onFertilize);
   }

   private final void onPiston(List<? extends Block> blocks, BlockPistonEvent bukkitEvent) {
      LinkedHashMap map = new LinkedHashMap();
      BlockFace var10000 = bukkitEvent.getDirection();
      Intrinsics.checkNotNullExpressionValue(var10000, "getDirection(...)");
      BlockFace direction = var10000;
      Iterable $this$forEach$iv = (Iterable)blocks;
      Function1 predicate$iv = this.customDataPredicate;
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv : $this$forEach$iv) {
         if ((Boolean)predicate$iv.invoke(element$iv$iv)) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      $this$forEach$iv = (Iterable)((List)destination$iv$iv);

      for(Object element$iv : $this$forEach$iv) {
         Block block = (Block)element$iv;
         CustomBlockData cbd = new CustomBlockData(block, this.plugin);
         if (!cbd.isEmpty() && !cbd.isProtected()) {
            if (block.getPistonMoveReaction() == PistonMoveReaction.BREAK) {
               this.callAndRemove(block, (Event)bukkitEvent);
            } else {
               Block var29 = block.getRelative(direction);
               Intrinsics.checkNotNullExpressionValue(var29, "getRelative(...)");
               Block destBlock = var29;
               CustomBlockDataMoveEvent moveEvent = new CustomBlockDataMoveEvent(this.plugin, block, destBlock, (Event)bukkitEvent);
               if (moveEvent.callEvent()) {
                  ((Map)map).put(destBlock, cbd);
               }
            }
         }
      }

      Set var30 = map.entrySet();
      Intrinsics.checkNotNullExpressionValue(var30, "<get-entries>(...)");
      $this$forEach$iv = (Iterable)CollectionsKt.reversed((Iterable)var30);

      for(Object element$iv : $this$forEach$iv) {
         Map.Entry var22 = (Map.Entry)element$iv;
         Intrinsics.checkNotNull(var22);
         Object var31 = var22.getKey();
         Intrinsics.checkNotNullExpressionValue(var31, "component1(...)");
         Block block = (Block)var31;
         var31 = var22.getValue();
         Intrinsics.checkNotNullExpressionValue(var31, "component2(...)");
         CustomBlockData cbd = (CustomBlockData)var31;
         cbd.copyTo(block, this.plugin);
         cbd.clear();
      }

   }

   private static final boolean customDataPredicate$lambda$0(BlockDataListener this$0, Block block) {
      Intrinsics.checkNotNullParameter(block, "block");
      return CustomBlockData.Companion.hasCustomBlockData(block, this$0.plugin);
   }
}
