package litebans;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking.LoginSynchronizer;
import net.minecraft.class_2535;
import net.minecraft.class_2561;
import net.minecraft.class_3248;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(AbstractPlatform = 3)
public final class FabricLoginListener extends ListenerModule implements LiteAInterface2 {
   public FabricLoginListener(@NotNull Platform var1) {
      super(var1);
   }

   @Override
   public void c_2() {
      this.a_f.PeriodicTaskModule();
   }

   @Override
   public boolean d_2() {
      return this.a_f.EntryQueueModule() == 3 && ClassUtil.a_75("net.minecraft.class_3248");
   }

   @Override
   public void e_2() {
   }

   @Override
   public void a_1() {
      ConfigModule var5 = (ConfigModule)this.a_f.AbstractPlatform(ConfigModule.class);
      var5.a_1();
      FabricLoginListener var2 = this;
      boolean var3 = false;
      Platform var10000 = var2.a_f;
      Intrinsics.b_2(var2.a_f, "");
      FabricCommandSource var4 = ((FabricPlugin)var10000).g_3();
      if (!var2.a_f.AbstractPlatform("net.fabricmc.fabric.impl.gamerule.rpc.FabricTypedRule", new FabricSupplierB(var2, var4))
         && !var2.a_f.AbstractPlatform("net.fabricmc.fabric.api.networking.v1.LoginPacketSender", new FabricSupplierA(var2, var4))) {
         Platform var10002 = var2.a_f;
         Intrinsics.a_15(var4);
         new FabricListenerC(var10002, var4).c_88();
      }

      if (var5.f_49().p_5()) {
         new FabricListenerA(this.a_f).a_61();
      }
   }

   public final void PunishmentTypeSwitchMap2(@NotNull class_3248 var1, @NotNull LoginSynchronizer var2, @NotNull PlayerInfo var3, @NotNull class_2535 var4) {
      BroadcastModule var10 = (BroadcastModule)this.a_f.AbstractPlatform(BroadcastModule.class);
      ConfigModule var6 = (ConfigModule)this.a_f.AbstractPlatform(ConfigModule.class);
      DatabaseModule var7 = (DatabaseModule)this.a_f.AbstractPlatform(DatabaseModule.class);
      Platform var10000 = this.a_f;
      Intrinsics.b_2(this.a_f, "");
      FabricPlugin var8 = (FabricPlugin)var10000;
      FabricPreLoginEvent var9 = new FabricPreLoginEvent(var3, var1, null, 4, null);
      var2.waitFor(CompletableFuture.runAsync(() -> AbstractPlatform(var6, var10, var9, var8, var1, var3, var7)));
   }

   public void AbstractPlatform(@Nullable class_3248 var1, @Nullable LoginSynchronizer var2, @Nullable PlayerInfo var3, @Nullable class_2535 var4) {
      Intrinsics.a_15(var1);
      Intrinsics.a_15(var2);
      Intrinsics.a_15(var3);
      Intrinsics.a_15(var4);
      this.PunishmentTypeSwitchMap2(var1, var2, var3, var4);
   }

   private static final void AbstractPlatform(ConfigModule var0, BroadcastModule var1, FabricPreLoginEvent var2, FabricPlugin var3, class_3248 var4, PlayerInfo var5, DatabaseModule var6) {
      ConfigModule var7 = var0;
      boolean var8 = false;
      if (var7.g_34()) {
         ConfigModule var13 = var7;
         boolean var9 = false;
         var13.a_24("Login " + var5);
      }

      var1.c_121().b_5(var2);
      Object var14 = null;
      GameVersion var15 = GameVersion.V1_21_5;
      if (var3.b_4() < 770) {
         var15 = GameVersion.V1_16;
      }

      if (var2.c_120() != null) {
         Object var10000 = var3.g_3().d_22().PunishmentTypeSwitchMap2(LiteHelper6.AbstractPlatform(var2.c_120(), var15));
         Intrinsics.b_2(var10000, "");
         class_2561 var16 = (class_2561)var10000;
         var4.method_14380(var16);
      } else {
         LockdownHandler var19 = var1.e_64();
         if (var19 == null) {
            return;
         }

         LockdownHandler var17 = var19;
         LockdownHandler var18 = var17;
         boolean var10 = false;
         if (var18.c_67() != null && var3.d_3() && !(Boolean)Permissions.check(UUID.fromString(var5.g_12()), "litebans.lockdown.bypass").get()) {
            String var11 = var17.a_2(var6.u_7(), false);
            Object var20 = var3.g_3().d_22().PunishmentTypeSwitchMap2(LiteHelper6.AbstractPlatform(var11, var15));
            Intrinsics.b_2(var20, "");
            class_2561 var12 = (class_2561)var20;
            var4.method_14380(var12);
         }
      }
   }

   @Override
   public void AbstractPlatform(Object var1, Object var2, Object var3, Object var4) {
      this.AbstractPlatform((class_3248)var1, (LoginSynchronizer)var2, (PlayerInfo)var3, (class_2535)var4);
   }
}
