package litebans;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.class_7471;
import net.minecraft.class_2556.class_7602;
import org.jetbrains.annotations.NotNull;

public final class FabricListenerA implements LitePlatformListenerSub {
   private final Platform a_f;
   private final PunishmentLookupModule b_f;

   public FabricListenerA(@NotNull Platform var1) {
      this.a_f = var1;
      this.b_f = (PunishmentLookupModule)this.a_f.AbstractPlatform(PunishmentLookupModule.class);
   }

   public final Platform b_42() {
      return this.a_f;
   }

   public FabricListenerA a_61() {
      FabricListenerA var4 = this;
      FabricListenerA var2 = var4;
      boolean var3 = false;
      ServerMessageEvents.ALLOW_CHAT_MESSAGE.register(var2::AbstractPlatform);
      ServerMessageEvents.ALLOW_COMMAND_MESSAGE.register(var2::AbstractPlatform);
      return var4;
   }

   public final boolean AbstractPlatform(@NotNull class_7471 var1, @NotNull class_3222 var2, @NotNull class_7602 var3) {
      LiteSender var7 = this.a_f.AbstractPlatform(var2);
      String var5 = var1.method_46291().getString();
      LiteHelper3 var6 = new LiteHelper3(var1, false, 2, null);
      MuteChatTask var10000 = this.b_f.a_216();
      Intrinsics.a_15(var5);
      Intrinsics.a_15(var7);
      var10000.AbstractPlatform(var6, var5, var7, true);
      return !var6.a_49();
   }

   public final boolean AbstractPlatform(@NotNull class_7471 var1, @NotNull class_2168 var2, @NotNull class_7602 var3) {
      LiteSender var7 = this.a_f.AbstractPlatform(var2);
      String var5 = var1.method_46291().getString();
      LiteHelper3 var6 = new LiteHelper3(var1, false, 2, null);
      MuteChatTask var10000 = this.b_f.a_216();
      Intrinsics.a_15(var5);
      Intrinsics.a_15(var7);
      MuteChatTask.AbstractPlatform(var10000, var6, var5, var7, false, 8, null);
      return !var6.a_49();
   }

   @Override
   public PlatformListener a_7() {
      return this.a_61();
   }
}
