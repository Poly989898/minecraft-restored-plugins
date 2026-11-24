package net.Indyuce.mmoitems.stat.data.type;

import org.jetbrains.annotations.NotNull;

public interface Mergeable<S extends Mergeable<S>> extends StatData {
   void mergeWith(S var1);

   @NotNull
   S clone();
}
