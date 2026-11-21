package com.nexomc.nexo.compatibilities.worldedit;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import kotlin.Metadata;
import kotlin.text.StringsKt;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0001B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u001e\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0016J\u001c\u0010\u000b\u001a\u0004\u0018\u00010\u00022\u0006\u0010\b\u001a\u00020\u00072\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\u0010\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u0002¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/worldedit/CustomBlocksFactory;", "Lcom/sk89q/worldedit/internal/registry/InputParser;", "Lcom/sk89q/worldedit/world/block/BaseBlock;", "<init>", "()V", "getSuggestions", "Ljava/util/stream/Stream;", "", "input", "context", "Lcom/sk89q/worldedit/extension/input/ParserContext;", "parseFromInput", "createBaseBlockFromCustomBlock", "customBlock", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "com.nexomc:core"}
)
public final class CustomBlocksFactory extends InputParser<BaseBlock> {
   public CustomBlocksFactory() {
      super(WorldEdit.getInstance());
   }

   @NotNull
   public Stream<String> getSuggestions(@NotNull String input, @NotNull ParserContext context) {
      Object[] blockIDs = NexoBlocks.blockIDs();
      Object obj = blockIDs;
      Collection destination = (Collection)(new ArrayList());
      int value = 0;

      for(int i = blockIDs.length; value < i; ++value) {
         Object element = ((Object[])obj)[value];
         int value2 = 0;
         if (StringsKt.contains((CharSequence)element, (CharSequence)input, false)) {
            destination.add(element);
         }
      }

      Stream stream = ((List)destination).stream();
      return stream;
   }

   @Nullable
   public BaseBlock parseFromInput(@NotNull String input, @Nullable ParserContext context) {
      String blockId = StringsKt.removePrefix(input, (CharSequence)"nexo:");
      CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(blockId);
      if (customBlockMechanic == null) {
         return null;
      } else {
         CustomBlockMechanic customBlock = customBlockMechanic;
         BlockData blockData = customBlock.getBlockData();
         if (blockData != null) {
            BlockData it = blockData;
            int value = 0;
            BlockState blockState2 = BukkitAdapter.adapt(it);
            if (blockState2 != null) {
               BlockState blockState = blockState2;
               return blockState.toBaseBlock();
            }
         }

         return null;
      }
   }

   private final BaseBlock createBaseBlockFromCustomBlock(CustomBlockMechanic customBlock) {
      BlockData blockData = customBlock.getBlockData();
      BaseBlock toBaseBlock = BukkitAdapter.adapt(blockData).toBaseBlock();
      return toBaseBlock;
   }
}
