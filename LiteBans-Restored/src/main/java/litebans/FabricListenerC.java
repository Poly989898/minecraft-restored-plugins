package litebans;

import com.mojang.authlib.GameProfile;
import litebans.mixin.AccessLoginData_1_20;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking.LoginSynchronizer;
import net.minecraft.class_2535;
import net.minecraft.class_3248;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class FabricListenerC implements LitePlatformListenerSub {
   private final Platform c_f;
   private final FabricCommandSource a_f;

   public FabricListenerC(@NotNull Platform var1, @NotNull FabricCommandSource var2) {
      this.c_f = var1;
      this.a_f = var2;
   }

   public final Platform b_118() {
      return this.c_f;
   }

   public final FabricCommandSource a_162() {
      return this.a_f;
   }

   public FabricListenerC c_88() {
      FabricListenerC var4 = this;
      FabricListenerC var2 = var4;
      boolean var3 = false;
      ServerLoginConnectionEvents.QUERY_START.register(var2::AbstractPlatform);
      return var4;
   }

   public final void AbstractPlatform(@NotNull class_3248 var1, @NotNull MinecraftServer var2, @NotNull PacketSender var3, @NotNull LoginSynchronizer var4) {
      PluginModule var10000 = this.c_f.PunishmentTypeSwitchMap2(ListenerModule.class);
      Intrinsics.b_2(var10000, "");
      LiteAInterface2 var8 = (LiteAInterface2)var10000;
      if (var1 instanceof AccessLoginData_1_20) {
         GameProfile var6 = ((AccessLoginData_1_20)var1).getProfile();
         class_2535 var7 = ((AccessLoginData_1_20)var1).getConnection();
         var8.AbstractPlatform(var1, var4, new PlayerInfo(var6.getName(), var6.getId().toString(), this.a_f.a_11(var7)), var7);
      } else {
         StandardKt.a_11();
         throw new CommandAbortException();
      }
   }

   @Override
   public PlatformListener a_7() {
      return this.c_88();
   }
}
