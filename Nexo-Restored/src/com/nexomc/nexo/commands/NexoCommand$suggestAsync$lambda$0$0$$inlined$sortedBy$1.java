package com.nexomc.nexo.commands;

import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import kotlin.text.StringsKt;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class NexoCommand$suggestAsync$lambda$0$0$$inlined$sortedBy$1 implements Comparator<Object> {
   private final CommandContext $ctx$inlined;
   public NexoCommand$suggestAsync$lambda$0$0$$inlined$sortedBy$1(CommandContext commandContext) {
      this.$ctx$inlined = commandContext;
   }

   public final int compare(Object a, Object b) {
      String it = (String)a;
      int value = 0;
      String input = this.$ctx$inlined.getInput();
      Comparable comparable = (Comparable)StringsKt.startsWith(it, input, false);
      it = (String)b;
      Comparable comparable2 = comparable;
      value = 0;
      input = this.$ctx$inlined.getInput();
      return ComparisonsKt.compareValues(comparable2, (Comparable)StringsKt.startsWith(it, input, false));
   }
}
