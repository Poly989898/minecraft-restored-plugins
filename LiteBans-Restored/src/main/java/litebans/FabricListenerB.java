package litebans;

import com.mojang.authlib.GameProfile;
import litebans.mixin.AccessLoginData_1_21;
import net.fabricmc.fabric.api.networking.v1.LoginPacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking.LoginSynchronizer;
import net.minecraft.class_2535;
import net.minecraft.class_3248;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class FabricListenerB implements LitePlatformListenerSub {
   private final Platform b_f;
   private final FabricCommandSource c_f;

   public FabricListenerB(@NotNull Platform var1, @NotNull FabricCommandSource var2) {
      this.b_f = var1;
      this.c_f = var2;
   }

   public final Platform a_144() {
      return this.b_f;
   }

   public final FabricCommandSource b_103() {
      return this.c_f;
   }

   public FabricListenerB d_53() {
      FabricListenerB var4 = this;
      FabricListenerB var2 = var4;
      boolean var3 = false;
      ServerLoginConnectionEvents.QUERY_START.register(var2::AbstractPlatform);
      return var4;
   }

   public final void AbstractPlatform(@NotNull class_3248 var1, @NotNull MinecraftServer var2, @NotNull LoginPacketSender var3, @NotNull LoginSynchronizer var4) {
      PluginModule var10000 = this.b_f.PunishmentTypeSwitchMap2(ListenerModule.class);
      Intrinsics.b_2(var10000, "");
      LiteAInterface2 var8 = (LiteAInterface2)var10000;
      if (var1 instanceof AccessLoginData_1_21) {
         GameProfile var6 = ((AccessLoginData_1_21)var1).getProfile();
         class_2535 var7 = ((AccessLoginData_1_21)var1).getConnection();
         var8.AbstractPlatform(var1, var4, new PlayerInfo(var6.getName(), var6.getId().toString(), this.c_f.a_11(var7)), var7);
      } else {
         StandardKt.a_11();
         throw new CommandAbortException();
      }
   }

   @Override
   public PlatformListener a_7() {
      return this.d_53();
   }
}
