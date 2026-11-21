package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00010\u0004H\u0086\bø\u0001\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u0005"},
   d2 = {"withOp", "", "Lorg/bukkit/entity/Player;", "block", "Lkotlin/Function0;", "com.nexomc:core"}
)
public final class PlayerUtilsKt {
   public static final void withOp(@NotNull Player player, @NotNull Function0<Unit> block) {
      boolean originalOp = player.isOp();
      player.setOp(true);

      try {
         Object obj = ResultShim.ok(block.invoke());
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      player.setOp(originalOp);
   }
}
