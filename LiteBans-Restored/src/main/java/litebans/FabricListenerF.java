package litebans;

import com.mojang.authlib.GameProfile;
import litebans.mixin.AccessLoginData_26_;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking.LoginSynchronizer;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.NotNull;

public final class FabricListenerF implements LitePlatformListenerSub {
   private final Platform c_f;
   private final FabricCommandSource a_f;

   public FabricListenerF(@NotNull Platform var1, @NotNull FabricCommandSource var2) {
      this.c_f = var1;
      this.a_f = var2;
   }

   public final Platform d_75() {
      return this.c_f;
   }

   public final FabricCommandSource b_162() {
      return this.a_f;
   }

   public FabricListenerF c_118() {
      FabricListenerF var4 = this;
      FabricListenerF var2 = var4;
      boolean var3 = false;
      ServerLoginConnectionEvents.QUERY_START.register(var2::AbstractPlatform);
      return var4;
   }

   public final void AbstractPlatform(@NotNull ServerLoginPacketListenerImpl var1, @NotNull MinecraftServer var2, @NotNull PacketSender var3, @NotNull LoginSynchronizer var4) {
      PluginModule var10000 = this.c_f.PunishmentTypeSwitchMap2(ListenerModule.class);
      Intrinsics.b_2(var10000, "");
      LiteAInterface2 var8 = (LiteAInterface2)var10000;
      if (var1 instanceof AccessLoginData_26_) {
         GameProfile var6 = ((AccessLoginData_26_)var1).getProfile();
         Connection var7 = ((AccessLoginData_26_)var1).getConnection();
         var8.AbstractPlatform(var1, var4, new PlayerInfo(var6.name(), var6.ConnectionUtil().toString(), this.a_f.a_11(var7)), var7);
      } else {
         StandardKt.a_11();
         throw new CommandAbortException();
      }
   }

   @Override
   public PlatformListener a_7() {
      return this.c_118();
   }
}
