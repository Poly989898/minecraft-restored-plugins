package com.nexomc.nexo.nms.commons;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.nexomc.nexo.nms.IPlayerUtils;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0016\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005*\u00020\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0005H\u0016¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/PlayerUtils;", "Lcom/nexomc/nexo/nms/IPlayerUtils;", "<init>", "()V", "toNMS", "Lnet/minecraft/server/level/ServerPlayer;", "kotlin.jvm.PlatformType", "Lorg/bukkit/entity/Player;", "copyGameProfile", "Lcom/mojang/authlib/GameProfile;", "serverPlayer", "com.nexomc:commons"}
)
public class PlayerUtils implements IPlayerUtils {
   public final ServerPlayer toNMS(@NotNull Player $this$toNMS) {
      Intrinsics.checkNotNullParameter($this$toNMS, "<this>");
      return ((CraftPlayer)$this$toNMS).getHandle();
   }

   @NotNull
   public GameProfile copyGameProfile(@NotNull ServerPlayer serverPlayer) {
      Intrinsics.checkNotNullParameter(serverPlayer, "serverPlayer");
      GameProfile gameProfile = new GameProfile(UUID.randomUUID(), serverPlayer.gameProfile.name());
      gameProfile.properties().putAll((Multimap)serverPlayer.gameProfile.properties());
      return gameProfile;
   }
}
