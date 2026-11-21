package litebans;

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.NonNull;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FabricSender implements LiteSender {
   private final FabricPlugin d_f;
   private final WeakReference e_f;
   private final Supplier c_f;
   private final String i_f;
   private final boolean k_f;
   private UUID j_f = b_f;
   private String h_f = null;
   private String f_f;

   public FabricSender(FabricPlugin var1, Object var2, @Nullable Supplier var3) {
      this.d_f = var1;
      this.e_f = new WeakReference<>(var2);
      this.c_f = var3;
      this.k_f = var2 instanceof ServerPlayer || var2 instanceof CommandSourceStack && ((CommandSourceStack)var2).isPlayer();
      if (this.k_f && var2 instanceof ServerPlayer) {
         this.i_f = ((ServerPlayer)var2).getPlainTextName();
      } else if (var2 instanceof CommandSourceStack) {
         this.i_f = ((CommandSourceStack)var2).getTextName();
      } else {
         this.i_f = "?";
      }
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof FabricSender && ((FabricSender)var1).d_8().equals(this.d_8());
   }

   @Override
   public void c_3(@NotNull String var1) {
      Object var3 = this.c_9();
      if (var3 instanceof CommandSourceStack && !var1.isEmpty()) {
         this.d_f.g_3().b_1(var3, var1);
      }
   }

   @Override
   public String g_7() {
      return this.i_5();
   }

   @Override
   public Object c_9() {
      Object var2 = this.e_f.get();
      if (this.c_f == null) {
         return var2;
      } else {
         return var2 != null ? var2 : this.c_f.get();
      }
   }

   @Override
   public @Nullable String b_15() {
      String var3 = this.f_f;
      if (var3 == null) {
         Object var2 = this.c_9();
         this.f_f = var3 = this.d_f.g_3().a_11(var2);
      }

      return var3;
   }

   @Override
   public String i_5() {
      return this.i_f;
   }

   @Override
   public @Nullable String h_5() {
      return null;
   }

   @Override
   public @NonNull UUID d_8() {
      UUID var4 = this.j_f;
      if (var4.equals(b_f) && this.k_f) {
         Object var2 = this.c_9();
         if (var2 != null) {
            UUID var3 = this.b_138().getUUID();
            if (var3 != null) {
               this.j_f = var3;
               return var3;
            }
         }
      }

      return var4;
   }

   @Override
   public String a_19() {
      String var2 = this.h_f;
      return var2 != null && !var2.equals("00000000-0000-0000-0000-000000000000") ? var2 : (this.h_f = this.d_8().toString());
   }

   @Override
   public boolean e_2(@Nullable String var1) {
      try {
         if (var1 == null) {
            return true;
         } else {
            Object var3 = this.c_9();
            if (var3 == null) {
               return false;
            } else if (this.d_f.d_3() && (this.k_f || !(var3 instanceof CommandSourceStack))) {
               return var3 instanceof CommandSourceStack && this.j_3() ? (Boolean)Permissions.check(this.d_8(), var1).get() : false;
            } else {
               return this.d_f.g_3().YamlThreadLocal(var3, var1);
            }
         }
      } catch (Throwable var5) {
         throw SneakyThrow.<RuntimeException>rethrow(var5);
      }
   }

   @Override
   public boolean f_6() {
      return !this.k_f;
   }

   @Override
   public boolean j_3() {
      Object var2 = this.c_9();
      return this.k_f && var2 != null ? !this.b_138().hasDisconnected() : var2 != null;
   }

   private final ServerPlayer b_138() {
      CommandSourceStack var2 = (CommandSourceStack)this.c_9();
      return var2 == null ? null : var2.getPlayer();
   }

   @Override
   public boolean e_8() {
      return this.k_f;
   }

   @Override
   public void d_1(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("reason is marked non-null but is null");
      }

      Object var3 = this.c_9();
      if (var3 != null) {
         if (this.e_8()) {
            if (this.d_f.SyncModule()) {
               this.d_f.g_3().a_1(this.b_138(), var1);
            } else {
               this.d_f.UInt(() -> this.e_11(var1));
            }
         }
      }
   }

   @Override
   public void b_3(String var1) {
      Object var3 = this.c_9();
      this.d_f.g_3().UInt(var3, var1);
   }

   @Override
   public void a_8(String var1) {
      if (!this.k_f) {
         Object var3 = this.c_9();
         if (var3 != null) {
            ((CommandSourceStack)var3).sendSystemMessage((Component)this.d_f.g_3().d_22().AbstractPlatform(LiteColor.a_16(var1)));
         }
      } else {
         GameVersion var4 = GameVersion.V1_16;
         if (this.d_f.g_3().b_38() >= 770) {
            var4 = GameVersion.V1_21_5;
         }

         this.b_3(ChatComponentDeserializer.AbstractPlatform(var4).a_2(LiteHelper7.a_13(var1)));
      }
   }

   @Override
   public void AbstractPlatform(String var1, byte[] var2) {
      try {
         Object var5 = this.c_9();
         if (var5 != null) {
            if (this.e_8()) {
               String var4;
               if (var1.equals("BungeeCord")) {
                  var4 = "bungeecord:main";
               }

               this.d_f.g_3().AbstractPlatform(this.b_138(), var4, var2);
            }
         }
      } catch (Throwable var7) {
         throw SneakyThrow.<RuntimeException>rethrow(var7);
      }
   }

   private final void e_11(String var1) {
      this.d_1(var1);
   }
}
