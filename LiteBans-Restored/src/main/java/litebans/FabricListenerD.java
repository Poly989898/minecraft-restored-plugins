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

public final class FabricListenerD implements LitePlatformListenerSub {
   private final Platform a_f;
   private final FabricCommandSource c_f;

   public FabricListenerD(@NotNull Platform var1, @NotNull FabricCommandSource var2) {
      this.a_f = var1;
      this.c_f = var2;
   }

   public final Platform b_131() {
      return this.a_f;
   }

   public final FabricCommandSource a_177() {
      return this.c_f;
   }

   public FabricListenerD c_97() {
      FabricListenerD var4 = this;
      FabricListenerD var2 = var4;
      boolean var3 = false;
      ServerLoginConnectionEvents.QUERY_START.register(var2::a_1);
      return var4;
   }

   public final void a_1(@NotNull class_3248 var1, @NotNull MinecraftServer var2, @NotNull LoginPacketSender var3, @NotNull LoginSynchronizer var4) {
      PluginModule var10000 = this.a_f.PunishmentTypeSwitchMap2(ListenerModule.class);
      Intrinsics.b_2(var10000, "");
      LiteAInterface2 var8 = (LiteAInterface2)var10000;
      if (var1 instanceof AccessLoginData_1_21) {
         GameProfile var6 = ((AccessLoginData_1_21)var1).getProfile();
         class_2535 var7 = ((AccessLoginData_1_21)var1).getConnection();
         var8.AbstractPlatform(var1, var4, new PlayerInfo(var6.name(), var6.ConnectionUtil().toString(), this.c_f.a_11(var7)), var7);
      } else {
         StandardKt.a_11();
         throw new CommandAbortException();
      }
   }

   @Override
   public PlatformListener a_7() {
      return this.c_97();
   }
}
