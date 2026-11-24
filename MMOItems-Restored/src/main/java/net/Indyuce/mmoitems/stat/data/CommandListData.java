package net.Indyuce.mmoitems.stat.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;

public class CommandListData implements StatData, Mergeable<CommandListData>, RandomStatData<CommandListData> {
   @NotNull
   private final List<CommandData> commands = new ArrayList<>();

   public CommandListData(@NotNull List<CommandData> commandDatas) {
      this.add(commandDatas);
   }

   public CommandListData(CommandData... commandDatas) {
      this.add(commandDatas);
   }

   public void add(CommandData... commandDatas) {
      for (CommandData commandData : commandDatas) {
         this.commands.add(commandData);
      }
   }

   public void add(Collection<CommandData> commandDatas) {
      this.commands.addAll(commandDatas);
   }

   @NotNull
   public List<CommandData> getCommands() {
      return this.commands;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         CommandListData commandListData = (CommandListData)obj;
         return this.commands.equals(commandListData.commands);
      } else {
         return false;
      }
   }

   public void mergeWith(CommandListData commandListData) {
      this.commands.addAll(commandListData.commands);
   }

   @NotNull
   public CommandListData clone() {
      return new CommandListData(this.commands);
   }

   @Override
   public boolean isEmpty() {
      return this.commands.isEmpty();
   }

   public CommandListData randomize(MMOItemBuilder mmoItemBuilder) {
      return this.clone();
   }
}
