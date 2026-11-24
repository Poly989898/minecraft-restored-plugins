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

      public BaseBlock parseFromInput(String var1, ParserContext var2) {
         var1 = var1.toLowerCase();
         if (!var1.startsWith("mmoitems-")) {
            return null;
         }

         int var4;
         try {
            var4 = Integer.parseInt(var1.split("-")[1]);
         } catch (NumberFormatException var8) {
            return null;
         }

         CustomBlock var5 = MMOItems.plugin.getCustomBlocks().getBlock(var4);
         if (var5 == null) {
            return null;
         }

         MushroomState var6 = var5.getState();
         BlockType var3;
         switch (var6.getType()) {
            case MUSHROOM_STEM:
               var3 = BlockTypes.MUSHROOM_STEM;
               break;
            case BROWN_MUSHROOM_BLOCK:
               var3 = BlockTypes.BROWN_MUSHROOM_BLOCK;
               break;
            case RED_MUSHROOM_BLOCK:
               var3 = BlockTypes.RED_MUSHROOM_BLOCK;
               break;
            default:
               return null;
         }

         BlockState var7 = var3.getDefaultState()
            .with((Property)var3.getPropertyMap().get("up"), var6.getSide("up"))
            .with((Property)var3.getPropertyMap().get("down"), var6.getSide("down"))
            .with((Property)var3.getPropertyMap().get("north"), var6.getSide("north"))
            .with((Property)var3.getPropertyMap().get("south"), var6.getSide("south"))
            .with((Property)var3.getPropertyMap().get("east"), var6.getSide("east"))
            .with((Property)var3.getPropertyMap().get("west"), var6.getSide("west"));
         return var7.toBaseBlock();
      }
   }
}
