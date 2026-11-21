package litebans;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.ChatType.Bound;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public final class FabricListenerE implements LitePlatformListenerSub {
   private final Platform b_f;
   private final PunishmentLookupModule a_f;

   public FabricListenerE(@NotNull Platform var1) {
      this.b_f = var1;
      this.a_f = (PunishmentLookupModule)this.b_f.AbstractPlatform(PunishmentLookupModule.class);
   }

   public final Platform a_190() {
      return this.b_f;
   }

   public FabricListenerE b_144() {
      FabricListenerE var4 = this;
      FabricListenerE var2 = var4;
      boolean var3 = false;
      ServerMessageEvents.ALLOW_CHAT_MESSAGE.register(var2::AbstractPlatform);
      ServerMessageEvents.ALLOW_COMMAND_MESSAGE.register(var2::AbstractPlatform);
      return var4;
   }

   public final boolean AbstractPlatform(@NotNull PlayerChatMessage var1, @NotNull ServerPlayer var2, @NotNull Bound var3) {
      LiteSender var7 = this.b_f.AbstractPlatform(var2);
      String var5 = var1.decoratedContent().getString();
      LiteHelper3 var6 = new LiteHelper3(var1, false, 2, null);
      MuteChatTask var10000 = this.a_f.a_216();
      Intrinsics.a_15(var7);
      var10000.AbstractPlatform(var6, var5, var7, true);
      return !var6.a_49();
   }

   public final boolean AbstractPlatform(@NotNull PlayerChatMessage var1, @NotNull CommandSourceStack var2, @NotNull Bound var3) {
      LiteSender var7 = this.b_f.AbstractPlatform(var2);
      String var5 = var1.decoratedContent().getString();
      LiteHelper3 var6 = new LiteHelper3(var1, false, 2, null);
      MuteChatTask var10000 = this.a_f.a_216();
      Intrinsics.a_15(var7);
      MuteChatTask.AbstractPlatform(var10000, var6, var5, var7, false, 8, null);
      return !var6.a_49();
   }

   @Override
   public PlatformListener a_7() {
      return this.b_144();
   }
}
