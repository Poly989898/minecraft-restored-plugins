package litebans;

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.NonNull;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FabricConsoleSender implements LiteSender {
   private final FabricPlugin f_f;
   private final WeakReference g_f;
   private final Supplier k_f;
   private final String c_f;
   private final boolean j_f;
   private UUID i_f = b_f;
   private String e_f = null;
   private String d_f;

   public FabricConsoleSender(FabricPlugin var1, Object var2, @Nullable Supplier var3) {
      this.f_f = var1;
      this.g_f = new WeakReference<>(var2);
      this.k_f = var3;
      this.j_f = var2 instanceof class_2168 && ((class_2168)var2).method_43737();
      if (this.j_f) {
         this.c_f = ((class_2168)var2).method_9214();
      } else {
         this.c_f = "[Console]";
      }
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof FabricConsoleSender && ((FabricConsoleSender)var1).d_8().equals(this.d_8());
   }

   @Override
   public void c_3(@NotNull String var1) {
      Object var3 = this.c_9();
      if (var3 instanceof class_2168 && !var1.isEmpty()) {
         this.f_f.g_3().b_1(this.c_9(), var1);
      }
   }

   @Override
   public String g_7() {
      return this.i_5();
   }

   @Override
   public Object c_9() {
      Object var2 = this.g_f.get();
      if (this.k_f == null) {
         return var2;
      } else {
         return var2 != null ? var2 : this.k_f.get();
      }
   }

   @Override
   public @Nullable String b_15() {
      String var3 = this.d_f;
      if (var3 == null) {
         Object var2 = this.c_9();
         this.d_f = var3 = this.f_f.g_3().a_11(var2);
      }

      return var3;
   }

   @Override
   public String i_5() {
      return this.c_f;
   }

   @Override
   public @Nullable String h_5() {
      return null;
   }

   @Override
   public @NonNull UUID d_8() {
      UUID var4 = this.i_f;
      if (var4.equals(b_f) && this.j_f) {
         Object var2 = this.c_9();
         if (var2 != null) {
            UUID var3 = this.a_20().method_5667();
            if (var3 != null) {
               this.i_f = var3;
               return var3;
            }
         }
      }

      return var4;
   }

   @Override
   public String a_19() {
      String var2 = this.e_f;
      return var2 != null && !var2.equals("00000000-0000-0000-0000-000000000000") ? var2 : (this.e_f = this.d_8().toString());
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
            } else if (this.f_f.d_3() && (this.j_f || !(var3 instanceof class_2168))) {
               return var3 instanceof class_2168 && this.j_3() ? (Boolean)Permissions.check(this.d_8(), var1).get() : false;
            } else {
               return ((class_2168)var3).method_9259(3);
            }
         }
      } catch (Throwable var5) {
         throw SneakyThrow.<RuntimeException>rethrow(var5);
      }
   }

   @Override
   public boolean f_6() {
      return !this.j_f;
   }

   @Override
   public boolean j_3() {
      if (this.j_f) {
         Object var2 = this.c_9();
         if (var2 != null) {
            return !this.a_20().method_14239();
         }
      }

      return this.c_9() != null;
   }

   private final class_3222 a_20() {
      return ((class_2168)this.c_9()).method_44023();
   }

   @Override
   public boolean e_8() {
      return this.j_f;
   }

   @Override
   public void d_1(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("reason is marked non-null but is null");
      }

      Object var3 = this.c_9();
      if (var3 != null) {
         if (this.e_8()) {
            if (this.f_f.SyncModule()) {
               this.f_f.g_3().a_1(this.a_20(), var1);
            } else {
               this.f_f.UInt(() -> this.e_1(var1));
            }
         }
      }
   }

   @Override
   public void b_3(String var1) {
      Object var3 = this.c_9();
      this.f_f.g_3().UInt(var3, var1);
   }

   @Override
   public void a_8(String var1) {
      GameVersion var3 = GameVersion.V1_16;
      if (this.f_f.g_3().b_38() >= 770) {
         var3 = GameVersion.V1_21_5;
      }

      this.b_3(ChatComponentDeserializer.AbstractPlatform(var3).a_2(LiteHelper7.a_13(var1)));
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

               this.f_f.g_3().AbstractPlatform(this.a_20(), var4, var2);
            }
         }
      } catch (Throwable var7) {
         throw SneakyThrow.<RuntimeException>rethrow(var7);
      }
   }

   private final void e_1(String var1) {
      this.d_1(var1);
   }
}
