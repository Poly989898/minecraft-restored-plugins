package com.nexomc.nexo.nms.v1_21_R11;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R11/PlayerUtils;", "Lcom/nexomc/nexo/nms/commons/PlayerUtils;", "<init>", "()V", "copyGameProfile", "Lcom/mojang/authlib/GameProfile;", "serverPlayer", "Lnet/minecraft/server/level/ServerPlayer;", "com.nexomc:v1_21_R11"}
)
public final class PlayerUtils extends com.nexomc.nexo.nms.commons.PlayerUtils {
   @NotNull
   public GameProfile copyGameProfile(@NotNull ServerPlayer serverPlayer) {
      Intrinsics.checkNotNullParameter(serverPlayer, "serverPlayer");
      return new GameProfile(UUID.randomUUID(), serverPlayer.gameProfile.name(), serverPlayer.gameProfile.properties());
   }
}
