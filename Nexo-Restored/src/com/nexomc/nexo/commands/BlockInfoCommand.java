package com.nexomc.nexo.commands;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.chorusblock.ChorusBlockFactory;
import com.nexomc.nexo.mechanics.custom_block.chorusblock.ChorusBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanicFactory;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanicFactory;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Iterator;
import kotlin.Metadata;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0000¢\u0006\u0002\b\n¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/commands/BlockInfoCommand;", "", "<init>", "()V", "sendBlockInfo", "", "sender", "Lorg/bukkit/command/CommandSender;", "itemId", "", "sendBlockInfo$com_nexomc_core", "com.nexomc:core"}
)
public final class BlockInfoCommand {
   @NotNull
   public static final BlockInfoCommand INSTANCE = new BlockInfoCommand();

   private BlockInfoCommand() {
   }

   public final void sendBlockInfo$com_nexomc_core(@NotNull CommandSender sender, @NotNull String itemId) {
      sender.sendRichMessage("<dark_aqua>ItemID: <aqua>" + itemId);
      if (NexoBlocks.isNexoNoteBlock(itemId)) {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         while(true) {
            MechanicFactory mechanicFactory;
            if (iterator.hasNext()) {
               MechanicFactory it = (MechanicFactory)iterator.next();
               int value = 0;
               mechanicFactory = it;
               if (!(it instanceof NoteBlockMechanicFactory)) {
                  mechanicFactory = null;
               }

               it = (NoteBlockMechanicFactory)mechanicFactory;
               if (it == null) {
                  continue;
               }

               mechanicFactory = it;
            } else {
               mechanicFactory = null;
            }

            mechanicFactory = mechanicFactory;
            if (mechanicFactory == null) {
               return;
            }

            NoteBlockMechanic mechanic = (NoteBlockMechanic)(mechanicFactory.getMechanic(itemId));
            if (mechanic == null) {
               return;
            }

            NoteBlock blockData = mechanic.getBlockData();
            if (blockData == null) {
               return;
            }

            NoteBlock data = blockData;
            sender.sendRichMessage("<dark_aqua>Instrument: " + data.getInstrument());
            sender.sendRichMessage("<dark_aqua>Note: " + data.getNote().getId());
            sender.sendRichMessage("<dark_aqua>Powered: " + data.isPowered());
            break;
         }
      } else if (NexoBlocks.isNexoStringBlock(itemId)) {
         MechanicsManager mechanicsManager2 = MechanicsManager.INSTANCE;
         Iterator iterator2 = ((Iterable)mechanicsManager2.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         MechanicFactory mechanicFactory2;
         while(true) {
            if (iterator2.hasNext()) {
               MechanicFactory mechanicFactory4 = (MechanicFactory)iterator2.next();
               int value2 = 0;
               mechanicFactory2 = mechanicFactory4;
               if (!(mechanicFactory4 instanceof StringBlockMechanicFactory)) {
                  mechanicFactory2 = null;
               }

               mechanicFactory4 = (StringBlockMechanicFactory)mechanicFactory2;
               if (mechanicFactory4 == null) {
                  continue;
               }

               mechanicFactory2 = mechanicFactory4;
               break;
            }

            mechanicFactory2 = null;
            break;
         }

         mechanicFactory2 = mechanicFactory2;
         if (mechanicFactory2 == null) {
            return;
         }

         StringBlockMechanic stringBlockMechanic = (StringBlockMechanic)(mechanicFactory2.getMechanic(itemId));
         if (stringBlockMechanic == null) {
            return;
         }

         Tripwire tripwire = stringBlockMechanic.getBlockData();
         if (tripwire == null) {
            return;
         }

         Tripwire data = tripwire;
         sender.sendRichMessage("<dark_aqua>Facing: " + data.getFaces());
         sender.sendRichMessage("<dark_aqua>Powered: " + data.isPowered());
         sender.sendRichMessage("<dark_aqua>Disarmed: " + data.isDisarmed());
         sender.sendRichMessage("<dark_aqua>Attached: " + data.isAttached());
      } else if (NexoBlocks.isNexoChorusBlock(itemId)) {
         MechanicsManager mechanicsManager3 = MechanicsManager.INSTANCE;
         Iterator iterator3 = ((Iterable)mechanicsManager3.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         MechanicFactory mechanicFactory3;
         while(true) {
            if (iterator3.hasNext()) {
               MechanicFactory mechanicFactory5 = (MechanicFactory)iterator3.next();
               int value3 = 0;
               mechanicFactory3 = mechanicFactory5;
               if (!(mechanicFactory5 instanceof ChorusBlockFactory)) {
                  mechanicFactory3 = null;
               }

               mechanicFactory5 = (ChorusBlockFactory)mechanicFactory3;
               if (mechanicFactory5 == null) {
                  continue;
               }

               mechanicFactory3 = mechanicFactory5;
               break;
            }

            mechanicFactory3 = null;
            break;
         }

         mechanicFactory3 = mechanicFactory3;
         if (mechanicFactory3 == null) {
            return;
         }

         ChorusBlockMechanic chorusBlockMechanic = (ChorusBlockMechanic)(mechanicFactory3.getMechanic(itemId));
         if (chorusBlockMechanic == null) {
            return;
         }

         MultipleFacing multipleFacing = chorusBlockMechanic.getBlockData();
         if (multipleFacing == null) {
            return;
         }

         MultipleFacing data = multipleFacing;
         sender.sendRichMessage("<dark_aqua>Facing : " + data.getFaces());
      }

      Logs.newline();
   }
}
