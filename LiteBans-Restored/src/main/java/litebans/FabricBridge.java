package litebans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.jetbrains.annotations.NotNull;

public final class FabricBridge implements PlatformBridge {
   private final Platform a_f;

   public FabricBridge(@NotNull Platform var1) {
      this.a_f = var1;
   }

   @Override
   public Platform c_7() {
      return this.a_f;
   }

   public ModContainer a_4(@NotNull String var1) {
      Iterable var8 = FabricLoader.getInstance().getAllMods();
      boolean var3 = false;
      Iterator var4 = var8.iterator();

      Object var10000;
      while (true) {
         if (var4.hasNext()) {
            Object var5 = var4.next();
            ModContainer var6 = (ModContainer)var5;
            boolean var7 = false;
            if (!Intrinsics.a_2(var6.getMetadata().getName(), var1)) {
               continue;
            }

            var10000 = var5;
            break;
         }

         var10000 = null;
         break;
      }

      return (ModContainer)var10000;
   }

   @Override
   public List b_1(@NotNull String var1) {
      Iterable var18 = FabricLoader.getInstance().getAllMods();
      boolean var3 = false;
      Iterable var4 = var18;
      var var5 = new ArrayList(CollectionsKt.AbstractPlatform((Iterable)var18, 10));
      boolean var6 = false;

      for (Object var8 : var4) {
         ModContainer var9 = (ModContainer)var8;
         ArrayList var17 = var5;
         boolean var10 = false;
         var17.add(var9.getMetadata());
      }

      var var19 = var5;
      boolean var21 = false;
      Iterable var23 = var19;
      var var25 = new ArrayList();
      boolean var27 = false;

      for (Object var31 : var23) {
         ModMetadata var33 = (ModMetadata)var31;
         boolean var35 = false;
         var var11 = var33.getDependencies();
         boolean var12 = false;
         boolean var10000;
         if (var11 instanceof Collection && ((Collection)var11).isEmpty()) {
            var10000 = false;
         } else {
            Iterator var13 = var11.iterator();

            while (true) {
               if (!var13.hasNext()) {
                  var10000 = false;
                  break;
               }

               Object var14 = var13.next();
               ModDependency var15 = (ModDependency)var14;
               boolean var16 = false;
               if (Intrinsics.a_2(var15.getModId(), var1)) {
                  var10000 = true;
                  break;
               }
            }
         }

         if (var10000) {
            var25.add(var31);
         }
      }

      var var20 = var25;
      boolean var22 = false;
      Iterable var24 = var20;
      var var26 = new ArrayList(CollectionsKt.AbstractPlatform((Iterable)var20, 10));
      boolean var28 = false;

      for (Object var32 : var24) {
         ModMetadata var34 = (ModMetadata)var32;
         ArrayList var37 = var26;
         boolean var36 = false;
         var37.add(var34.getName());
      }

      return var26;
   }

   @Override
   public Collection a_14() {
      return CollectionsKt.e_62();
   }

   @Override
   public String b_11() {
      return FabricLoader.getInstance().getModContainer("fabric-api").map(var1 -> a_1(var0, var1)).orElse("null");
   }

   @Override
   public void AbstractPlatform(@NotNull Object var1, @NotNull CharSequence var2, @NotNull String var3, boolean var4, @NotNull String var5) {
      Object var7 = var1;
      if (var7 instanceof FabricPreLoginEvent) {
         ((FabricPreLoginEvent)var1).a_98(var2.toString());
      } else if (var7 instanceof LiteHelper3) {
         ((LiteHelper3)var1).a_7(true);
      }
   }

   @Override
   public PlayerInfo AbstractPlatform(@NotNull Object var1, boolean var2) {
      if (var1 instanceof FabricPreLoginEvent) {
         return ((FabricPreLoginEvent)var1).a_213();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public Void a_6(@NotNull Object var1) {
      throw new UnsupportedOperationException();
   }

   public Void AbstractPlatform(@NotNull Object var1, @NotNull CharSequence var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean a_5(@NotNull String var1) {
      return PlayerKicker.AbstractPlatform(this, var1);
   }

   private static final String AbstractPlatform(ModContainer var0) {
      return var0.getMetadata().getVersion().getFriendlyString();
   }

   private static final String a_1(Function1 var0, Object var1) {
      return (String)var0.a_10(var1);
   }

   @Override
   public Object c_1(String var1) {
      return this.a_4(var1);
   }

   @Override
   public String a_5(Object var1) {
      return (String)this.a_6(var1);
   }

   @Override
   public void a_1(Object var1, CharSequence var2) {
      this.AbstractPlatform(var1, var2);
   }
}
