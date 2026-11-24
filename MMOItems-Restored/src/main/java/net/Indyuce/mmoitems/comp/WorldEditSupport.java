package net.Indyuce.mmoitems.comp;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import net.Indyuce.mmoitems.api.util.MushroomState;

public class WorldEditSupport {
   public WorldEditSupport() {
      WorldEdit.getInstance().getBlockFactory().register(new WorldEditSupport.WECustomBlockInputParser());
   }

   public class WECustomBlockInputParser extends InputParser<BaseBlock> {
      public WECustomBlockInputParser() {
         super(WorldEdit.getInstance());
      }

      public BaseBlock parseFromInput(String text, ParserContext parserContext) {
         text = text.toLowerCase();
         if (!text.startsWith("mmoitems-")) {
            return null;
         }

         int count;
         try {
            count = Integer.parseInt(text.split("-")[1]);
         } catch (NumberFormatException exception) {
            return null;
         }

         CustomBlock block = MMOItems.plugin.getCustomBlocks().getBlock(count);
         if (block == null) {
            return null;
         }

         MushroomState state = block.getState();
         BlockType blockType;
         switch (state.getType()) {
            case MUSHROOM_STEM:
               blockType = BlockTypes.MUSHROOM_STEM;
               break;
            case BROWN_MUSHROOM_BLOCK:
               blockType = BlockTypes.BROWN_MUSHROOM_BLOCK;
               break;
            case RED_MUSHROOM_BLOCK:
               blockType = BlockTypes.RED_MUSHROOM_BLOCK;
               break;
            default:
               return null;
         }

         BlockState blockState = blockType.getDefaultState()
            .with((Property)blockType.getPropertyMap().get("up"), state.getSide("up"))
            .with((Property)blockType.getPropertyMap().get("down"), state.getSide("down"))
            .with((Property)blockType.getPropertyMap().get("north"), state.getSide("north"))
            .with((Property)blockType.getPropertyMap().get("south"), state.getSide("south"))
            .with((Property)blockType.getPropertyMap().get("east"), state.getSide("east"))
            .with((Property)blockType.getPropertyMap().get("west"), state.getSide("west"));
         return blockState.toBaseBlock();
      }
   }
}
