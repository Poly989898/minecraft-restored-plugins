package com.nexomc.nexo.nms;

import kotlin.Metadata;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b&\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0003\u001a\u00020\u0004X\u0084\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/nms/AbstractTransformer;", "T", "Lcom/nexomc/nexo/nms/Transformer;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lorg/bukkit/entity/Player;)V", "getPlayer", "()Lorg/bukkit/entity/Player;", "com.nexomc:core"}
)
public abstract class AbstractTransformer<T> implements Transformer<T> {
   @NotNull
   private final Player player;

   public AbstractTransformer(@NotNull Player player) {
      super();
      this.player = player;
   }

   @NotNull
   protected final Player getPlayer() {
      return this.player;
   }
}
