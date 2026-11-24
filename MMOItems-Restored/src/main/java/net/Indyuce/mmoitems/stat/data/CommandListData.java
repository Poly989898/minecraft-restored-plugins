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

   public CommandListData(@NotNull List<CommandData> var1) {
      this.add(var1);
   }

   public CommandListData(CommandData... var1) {
      this.add(var1);
   }

   public void add(CommandData... var1) {
      for (CommandData var5 : var1) {
         this.commands.add(var5);
      }
   }

   public void add(Collection<CommandData> var1) {
      this.commands.addAll(var1);
   }

   @NotNull
   public List<CommandData> getCommands() {
      return this.commands;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         CommandListData var2 = (CommandListData)var1;
         return this.commands.equals(var2.commands);
      } else {
         return false;
      }
   }

   public void mergeWith(CommandListData var1) {
      this.commands.addAll(var1.commands);
   }

   @NotNull
   public CommandListData clone() {
      return new CommandListData(this.commands);
   }

   @Override
   public boolean isEmpty() {
      return this.commands.isEmpty();
   }

   public CommandListData randomize(MMOItemBuilder var1) {
      return this.clone();
   }
}
