package litebans;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.KotlinFunction;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.class_155;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2535;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_3324;
import net.minecraft.class_7157;
import net.minecraft.class_2170.class_5364;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class FabricCommandSourceV121 implements FabricCommandSource {
   protected final Platform d_f;
   protected MinecraftServer c_f;
   protected final PlatformHook a_f = new FabricHookA();

   public FabricCommandSourceV121(Platform var1) {
      this.d_f = var1;
   }

   @Override
   public PlatformHook d_22() {
      return this.a_f;
   }

   @Override
   public void a_1(Object var1, String var2) {
      int var5 = this.b_38();
      GameVersion var4 = var5 >= 770 ? GameVersion.V1_21_5 : GameVersion.V1_16;
      ((class_3222)var1).field_13987.method_52396((class_2561)this.d_22().PunishmentTypeSwitchMap2(LiteHelper6.AbstractPlatform(var2, var4)));
   }

   @Override
   public Object f_16() {
      return this.c_f.method_3739();
   }

   @Override
   public String a_11(Object var1) {
      if (var1 instanceof class_2168) {
         class_3222 var4 = ((class_2168)var1).method_44023();
         return var4 == null ? null : this.a_11(var4.field_13987);
      }

      if (var1 instanceof class_3244) {
         InetSocketAddress var5 = (InetSocketAddress)((class_3244)var1).method_48107();
         if (var5 != null) {
            InetAddress var3 = var5.getAddress();
            if (var3 != null) {
               return var3.getHostAddress();
            }
         }
      }

      if (var1 instanceof class_2535) {
         SocketAddress var6 = ((class_2535)var1).method_10755();
         if (var6 instanceof InetSocketAddress) {
            InetAddress var7 = ((InetSocketAddress)var6).getAddress();
            if (var7 != null) {
               return var7.getHostAddress();
            }
         }
      }

      return null;
   }

   @Override
   public boolean a_56() {
      return this.c_f.method_3828();
   }

   @Override
   public boolean e_18() {
      return this.c_f.method_18854();
   }

   @Override
   public List c_30() {
      class_3324 var2 = this.c_f.method_3760();
      return var2 == null ? Collections.emptyList() : var2.method_14571();
   }

   @Override
   public void a_1(Runnable var1) {
      this.c_f.method_19537(var1);
   }

   @Override
   public void b_1(Object var1, String var2) {
      this.c_f.method_3734().method_44252((class_2168)var1, var2);
   }

   @Override
   public int b_38() {
      try {
         return class_155.method_31372();
      } catch (LinkageError var3) {
         this.d_f.getLogger().warning("Unable to determine server protocol version");
         return 770;
      }
   }

   @Override
   public Object d_1(Object var1) {
      return var1 instanceof class_3222 ? ((class_3222)var1).method_64396() : var1;
   }

   @Override
   public void UInt(Object var1, String var2) {
      if (var1 instanceof class_2168) {
         ((class_2168)var1).method_45068((class_2561)this.d_22().PunishmentTypeSwitchMap2(var2));
      }
   }

   @Override
   public void AbstractPlatform(Object var1, String var2, byte[] var3) {
      FabricHelperA.a_1(var1, var2, var3);
   }

   @Override
   public LiteSender c_2(Object var1) {
      Object var3 = this.d_1(var1);
      String var4;
      if (var3 instanceof class_2168) {
         var4 = ((class_2168)var3).method_9214();
      } else {
         var4 = var3.toString();
      }

      return (LiteSender)((FabricPlugin)this.d_f).a_4().b_f.remove(var4);
   }

   @Override
   public LiteSender b_3(Object var1) {
      return this.AbstractPlatform(var1, null);
   }

   @Override
   public LiteSender AbstractPlatform(Object var1, @Nullable Supplier var2) {
      PluginLifecycle var8 = ((FabricPlugin)this.d_f).a_4();
      Map var4 = var8.b_f;
      Object var7 = this.d_1(var1);
      String var5;
      if (var7 instanceof class_2168) {
         var5 = ((class_2168)var7).method_9214();
      } else {
         var5 = var7.toString();
      }

      LiteSender var6 = (LiteSender)var4.get(var5);
      if (var6 == null || var6.c_9() != var7) {
         var6 = new FabricConsoleSender((FabricPlugin)this.d_f, var7, var2);
         var8.f_11();
         var4.put(var5, var6);
      }

      return var6;
   }

   @Override
   public void AbstractPlatform(KotlinFunction var1) {
      CommandRegistrationCallback.EVENT.register((CommandRegistrationCallback)(var1x, var2, var3) -> AbstractPlatform(var1, var1x, var2, var3));
   }

   @Override
   public void a_1(BrigadierCommand var1, CommandDispatcher var2) {
      ArgumentBuilder var4 = ((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247(var1.getName()).requires(var2x -> this.AbstractPlatform(var1, var2x)))
            .executes(var1))
         .then(class_2170.method_9244("args", StringArgumentType.greedyString()).suggests(var1).executes(var1));
      var2.getRoot().getChildren().removeIf(var1x -> a_1(var1, var1x));
      var2.register((LiteralArgumentBuilder)var4);
   }

   @Override
   public boolean YamlThreadLocal(@Nullable Object var1, @Nullable String var2) {
      return !(var1 instanceof class_2168) ? false : ((class_2168)var1).method_9259(3);
   }

   @Override
   public void a_1(MinecraftServer var1) {
      this.c_f = var1;
   }

   private static final boolean a_1(BrigadierCommand var0, CommandNode var1) {
      return var1.getName().equals(var0.getName());
   }

   private final boolean AbstractPlatform(BrigadierCommand var1, class_2168 var2) {
      return this.d_f.AbstractPlatform(var2).e_2(var1.getPermission());
   }

   private static final void AbstractPlatform(KotlinFunction var0, CommandDispatcher var1, class_7157 var2, class_5364 var3) {
      var0.apply(var1);
   }
}
