package litebans;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GeoIpLookup {
   public static final GeoIpLookupCompanion GEO_IP_LOOKUP_COMPANION = new GeoIpLookupCompanion(null);
   private final Platform platform;
   private Object obj2;
   private Method countryMethod;
   private Method getCountryMethod;
   private Method getNameMethod;
   public static final String TEXT = "geoip.unavailable";
   public static final String TEXT_2 = "geoip.error";

   public GeoIpLookup(@NotNull Platform platform, @NotNull File file, @NotNull File file2) {
      this.platform = platform;

      try {
         File[] listFiles2 = file.listFiles((file3, text) -> {
            Intrinsics.checkNotNull(text);
            return StringsKt.checkText2(text, ".jar", false, 2, null);
         });
         Intrinsics.checkNotNull(listFiles2);
         var arrayList = new ArrayList(listFiles2.length);
         int value = 0;

         for (int i = listFiles2.length; value < i; value++) {
            File file4 = listFiles2[value];
            arrayList.add(file4.toURI().toURL());
         }

         Collection collection = arrayList;
         URL[] array = (URL[])collection.toArray(new URL[0]);
         Class class2 = new URLClassLoader(array, Driver.class.getClassLoader()).loadClass("com.maxmind.geoip2.DatabaseReader$Builder");
         Class[] classArray = new Class[]{File.class};
         Constructor constructor = class2.getConstructor(classArray);
         Object[] objs = new Object[]{file2};
         Object instance = constructor.newInstance(objs);
         this.obj2 = instance.getClass().getMethod("build").invoke(instance);
         Class class3 = (Class)this.obj2;
         Intrinsics.checkNotNull(this.obj2);
         class3 = class3.getClass();
         Class[] classArray2 = new Class[]{InetAddress.class};
         this.countryMethod = class3.getMethod("country", classArray2);
         Method method = this.countryMethod;
         if (this.countryMethod == null) {
            Intrinsics.throwUninitializedProperty("");
            method = null;
         }

         Class returnType = method.getReturnType();
         this.getCountryMethod = returnType.getMethod("getCountry");
         Method method2 = this.getCountryMethod;
         if (this.getCountryMethod == null) {
            Intrinsics.throwUninitializedProperty("");
            method2 = null;
         }

         this.getNameMethod = method2.getReturnType().getMethod("getName");
         this.platform.getLogger().info("GeoIP support initialized.");
         String text2 = this.computeText("8.8.8.8");
         if (!Intrinsics.areEqual(text2, "United States")) {
            this.platform.getLogger().warning("GeoIP database test failed. (" + text2 + ')');
         }
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }

   public final String computeText(@Nullable String text) {
      if (text == null) {
         return "geoip.unavailable";
      }

      if (this.obj2 == null) {
         return "geoip.unavailable";
      }

      Object obj = this.obj2;

      try {
         InetAddress byName = InetAddress.getByName(text);
         Method method = this.countryMethod;
         if (this.countryMethod == null) {
            Intrinsics.throwUninitializedProperty("");
            method = null;
         }

         Object[] objs = new Object[]{byName};
         Object invoke2 = method.invoke(obj, objs);
         method = this.getCountryMethod;
         if (this.getCountryMethod == null) {
            Intrinsics.throwUninitializedProperty("");
            method = null;
         }

         Object invoke3 = method.invoke(invoke2);
         method = this.getNameMethod;
         if (this.getNameMethod == null) {
            Intrinsics.throwUninitializedProperty("");
            method = null;
         }

         Object invoke4 = method.invoke(invoke3);
         String text2 = invoke4 instanceof String ? (String)invoke4 : null;
         if ((invoke4 instanceof String ? (String)invoke4 : null) == null) {
            text2 = "geoip.error";
         }

         return text2;
      } catch (Exception e) {
         ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
         ConfigModule.handleConfigModule(module, e, 0, 2, null);
         return "geoip.error";
      }
   }

   public final String computeText2(@Nullable String text) {
      if (text == null) {
         return null;
      }

      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      Settings settings = module.getSettings();
      if (settings.isGeoipEnable()) {
         Lazy lazy = KotlinUtil38.computeLazy(() -> this.computeText(text));
         if (settings.getGeoipBlacklist().length != 0) {
            String[] geoipBlacklist = settings.getGeoipBlacklist();
            int value = 0;
            int length2 = geoipBlacklist.length;

            boolean flag;
            while (true) {
               if (value >= length2) {
                  flag = false;
                  break;
               }

               String text2 = geoipBlacklist[value];
               if (StringsKt.checkText(text2, computeText(lazy), true)) {
                  flag = true;
                  break;
               }

               value++;
            }

            if (flag) {
               return computeText(lazy);
            }
         }

         if (settings.getGeoipWhitelist().length != 0) {
            String[] geoipWhitelist = settings.getGeoipWhitelist();
            int value2 = 0;
            int length3 = geoipWhitelist.length;

            boolean flag2;
            while (true) {
               if (value2 >= length3) {
                  flag2 = true;
                  break;
               }

               String text3 = geoipWhitelist[value2];
               if (StringsKt.checkText(text3, computeText(lazy), true)) {
                  flag2 = false;
                  break;
               }

               value2++;
            }

            if (flag2) {
               return computeText(lazy);
            }
         }
      }

      return null;
   }

   private static final String computeText(Lazy lazy) {
      return (String)lazy.getValue();
   }
}
