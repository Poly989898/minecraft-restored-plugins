package litebans;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.function.KotlinFunction;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FabricCommandSourceV26 implements FabricCommandSource {
   protected final Platform d_f;
   protected MinecraftServer c_f;
   protected final FabricHookC b_f = new FabricHookC();

   public FabricCommandSourceV26(Platform var1) {
      this.d_f = var1;
   }

   @Override
   public void a_1(Object var1, String var2) {
      int var5 = this.b_38();
      GameVersion var4 = var5 >= 770 ? GameVersion.V1_21_5 : GameVersion.V1_16;
      ((ServerPlayer)var1).connection.disconnect((Component)this.b_125().PunishmentTypeSwitchMap2(LiteHelper6.AbstractPlatform(var2, var4)));
   }

   @Override
   public Object f_16() {
      return this.c_f.createCommandSourceStack();
   }

   @Override
   public String a_11(Object var1) {
      if (var1 instanceof CommandSourceStack) {
         ServerPlayer var4 = ((CommandSourceStack)var1).getPlayer();
         return var4 == null ? null : this.a_11(var4.connection);
      }

      if (var1 instanceof Player) {
         InetSocketAddress var5 = (InetSocketAddress)((ServerPlayer)var1).connection.getRemoteAddress();
         InetAddress var3 = var5.getAddress();
         if (var3 != null) {
            return var3.getHostAddress();
         }
      }

      if (var1 instanceof ServerPlayer) {
         SocketAddress var6 = ((ServerPlayer)var1).connection.getRemoteAddress();
         if (var6 instanceof InetSocketAddress) {
            InetAddress var7 = ((InetSocketAddress)var6).getAddress();
            if (var7 != null) {
               return var7.getHostAddress();
            }
         }
      }

      return var1 instanceof ServerGamePacketListenerImpl ? this.a_11(((ServerGamePacketListenerImpl)var1).player) : null;
   }

   @Override
   public boolean a_56() {
      return this.c_f.usesAuthentication();
   }

   @Override
   public boolean e_18() {
      return this.c_f.isSameThread();
   }

   @Override
   public @Nullable List c_30() {
      PlayerList var2 = this.c_f.getPlayerList();
      return var2 == null ? null : var2.getPlayers();
   }

   @Override
   public void a_1(Runnable var1) {
      this.c_f.execute(var1);
   }

   @Override
   public void b_1(Object var1, String var2) {
      this.c_f.getCommands().performPrefixedCommand((CommandSourceStack)var1, var2);
   }

   @Override
   public int b_38() {
      try {
         return SharedConstants.getProtocolVersion();
      } catch (LinkageError var3) {
         this.d_f.getLogger().warning("Unable to determine server protocol version");
         return 770;
      }
   }

   @Override
   public Object d_1(Object var1) {
      return var1 instanceof ServerPlayer ? ((ServerPlayer)var1).createCommandSourceStack() : var1;
   }

   @Override
   public void UInt(Object var1, String var2) {
      if (var1 instanceof CommandSourceStack) {
         ((CommandSourceStack)var1).sendSystemMessage((Component)this.b_125().PunishmentTypeSwitchMap2(var2));
      } else {
         this.UInt(this.d_1(var1), var2);
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
      if (var3 instanceof CommandSourceStack) {
         var4 = ((CommandSourceStack)var3).getTextName();
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
      if (var7 instanceof CommandSourceStack) {
         var5 = ((CommandSourceStack)var7).getTextName();
      } else {
         var5 = var7.toString();
      }

      LiteSender var6 = (LiteSender)var4.get(var5);
      if (var6 == null || var6.c_9() != var7) {
         var6 = new FabricSender((FabricPlugin)this.d_f, var7, var2);
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
      ArgumentBuilder var4 = ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(var1.getName()).requires(var2x -> this.AbstractPlatform(var1, var2x)))
            .executes(var1))
         .then(Commands.argument("args", StringArgumentType.greedyString()).suggests(var1).executes(var1));
      var2.getRoot().getChildren().removeIf(var1x -> AbstractPlatform(var1, var1x));
      var2.register((LiteralArgumentBuilder)var4);
   }

   @Override
   public boolean YamlThreadLocal(@Nullable Object var1, @NotNull String var2) {
      return FabricHelperB.a_4(var1, var2);
   }

   @Override
   public void a_1(MinecraftServer var1) {
      this.c_f = var1;
   }

   public FabricHookC b_125() {
      return this.b_f;
   }

   @Override
   public PlatformHook d_22() {
      return this.b_125();
   }

   private static final boolean AbstractPlatform(BrigadierCommand var0, CommandNode var1) {
      return var1.getName().equals(var0.getName());
   }

   private final boolean AbstractPlatform(BrigadierCommand var1, CommandSourceStack var2) {
      return this.d_f.AbstractPlatform(var2).e_2(var1.getPermission());
   }

   private static final void AbstractPlatform(KotlinFunction var0, CommandDispatcher var1, CommandBuildContext var2, CommandSelection var3) {
      var0.apply(var1);
   }
}
