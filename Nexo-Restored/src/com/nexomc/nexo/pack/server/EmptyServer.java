package com.nexomc.nexo.pack.server;

import kotlin.Metadata;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\b\u0010\b\u001a\u00020\tH\u0016¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/pack/server/EmptyServer;", "Lcom/nexomc/nexo/pack/server/NexoPackServer;", "<init>", "()V", "sendPack", "", "player", "Lorg/bukkit/entity/Player;", "packUrl", "", "com.nexomc:core"}
)
public final class EmptyServer implements NexoPackServer {
   public void sendPack(@NotNull Player player) {
   }

   @NotNull
   public String packUrl() {
      return "";
   }
}
