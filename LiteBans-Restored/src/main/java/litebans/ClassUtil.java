package litebans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ClassUtil {
   private final Platform platform;
   private boolean flag2 = true;
   private boolean flag3 = true;
   private boolean flag4 = false;
   private boolean flag5 = false;
   private String text4;
   private String text5;
   private List list2;

   public ClassUtil(Platform platform) {
      this.platform = platform;
   }

   public static String getJarPath(Class class2) {
      URL url = null;

      try {
         ProtectionDomain protectionDomain = class2.getProtectionDomain();
         CodeSource codeSource = protectionDomain.getCodeSource();
         url = codeSource.getLocation();
         return url.toURI().getPath();
      } catch (URISyntaxException e) {
         e.printStackTrace();
         throw new AssertionError("CodeSource.getLocation(): " + url);
      }
   }

   public static boolean classExists(String text) {
      try {
         Class.forName(text);
         return true;
      } catch (ClassNotFoundException e) {
         return false;
      }
   }

   private static final String stackTraceToString(Throwable error) {
      StringWriter stringWriter = null;

      try {
         stringWriter = new StringWriter();
         writeStackTrace(error, stringWriter);
         return stringWriter.toString();
      } finally {
         if (stringWriter != null) {
            try {
               stringWriter.close();
            } catch (IOException e) {
            }
         }
      }
   }

   private static final void writeStackTrace(Throwable error, StringWriter stringWriter) {
      PrintWriter printWriter = null;

      try {
         printWriter = new PrintWriter(stringWriter);

         while (error != null) {
            printWriter.println(error);
            StackTraceElement[] stackTrace = error.getStackTrace();

            for (StackTraceElement stackTraceElement : stackTrace) {
               printWriter.println("\tat " + stackTraceElement);
            }

            error = error.getCause();
            if (error != null) {
               printWriter.print("Caused by: ");
            }
         }
      } finally {
         if (printWriter != null) {
            printWriter.close();
         }
      }
   }

   public void preloadClasses(String text, boolean flag) {
      for (String text2 : (Iterable<String>) this.findClasses(text, "")) {
         try {
            Class.forName(text2);
            if (flag) {
               Thread.sleep(System.currentTimeMillis() & 16L);
            }
         } catch (Throwable e) {
            this.handleLoadFailure(text2, e);
         }
      }
   }

   public List listClassFiles(File file, List list, String text) {
      File[] listFiles2 = file.listFiles();
      if (listFiles2 == null) {
         return list;
      }

      for (File file2 : listFiles2) {
         if (file2.isDirectory() && !file2.getPath().equals(file.getPath())) {
            String text2 = text.equals("") ? file2.getName() : text + "." + file2.getName();
            this.listClassFiles(file2, list, text2);
         } else if (file2.getName().endsWith(".class")) {
            String name = file2.getName();
            String substring2 = name.substring(0, name.length() - 6);
            list.add(text + "." + substring2);
         }
      }

      return list;
   }

   public List findClasses(String text, String text2) {
      long value = System.nanoTime();
      String replace2 = text2.replace('.', '/');
      if (this.flag2 && this.list2 != null && replace2.equals(this.text5) && text.equals(this.text4)) {
         return this.list2;
      }

      List arrayList = new ArrayList();
      File file = new File(text);
      JarInputStream jarInputStream = null;

      try {
         if (!file.isDirectory()) {
            jarInputStream = new JarInputStream(new FileInputStream(file));

            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
               String name = jarEntry.getName();
               if (name.startsWith(replace2) && name.endsWith(".class")) {
                  String replace3 = name.replace('/', '.');
                  String substring2 = replace3.substring(0, replace3.length() - 6);
                  arrayList.add(substring2);
               }
            }
         } else {
            arrayList = this.listClassFiles(file, arrayList, "");
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (jarInputStream != null) {
            try {
               jarInputStream.close();
            } catch (IOException e2) {
               e2.printStackTrace();
            }
         }
      }

      if (this.flag2) {
         this.text4 = text;
         this.text5 = replace2;
         this.list2 = arrayList;
      }

      if (this.flag4) {
         System.out.println("1: returned in " + NumberFormats.computeText((System.nanoTime() - value) / 1000.0 / 1000.0) + " ms");
      }

      return arrayList;
   }

   public boolean isMixin(String text) {
      return text.contains("mixin.");
   }

   public List computeList(String text, String text2, Class class2) {
      long value = System.nanoTime();
      ArrayList arrayList = new ArrayList();

      for (String text3 : (Iterable<String>) this.findClasses(text, text2)) {
         if (!this.isMixin(text3)) {
            try {
               Class forName2 = Class.forName(text3);
               int modifiers = forName2.getModifiers();
               if (class2.isAssignableFrom(forName2) && !Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers) && forName2 != class2) {
                  arrayList.add(forName2);
               }
            } catch (Throwable e) {
               this.handleLoadFailure(text3, e);
            }
         }
      }

      if (this.flag4) {
         System.out.println("2: returned in " + NumberFormats.computeText((System.nanoTime() - value) / 1000.0 / 1000.0) + " ms");
      }

      return arrayList;
   }

   public List computeList(Class class2, Class[] classArray, Object[] objs) {
      return this.computeList(getJarPath(class2), "", class2, classArray, objs);
   }

   public List computeList(String text, String text2, Class class2, Class[] classArray, Object[] objs) {
      Platform platform = this.platform;
      long value = System.nanoTime();
      List list = this.computeList(text, text2, class2);
      ArrayList arrayList = new ArrayList();
      Constructor constructor = null;

      for (Class class3 : (Iterable<Class>) list) {
         if (class3.getAnnotation(CommandMarker.class) == null && (this.flag5 || class3.getAnnotation(PluginMarker.class) != null)) {
            PlatformMarker annotation = (PlatformMarker) class3.getAnnotation(PlatformMarker.class);
            if (annotation == null || platform == null || annotation.obtainByteValue() == platform.getPlatformId()) {
               try {
                  try {
                     constructor = class3.getDeclaredConstructor(classArray);
                     if (!Modifier.isPublic(constructor.getModifiers())) {
                        constructor.setAccessible(true);
                     }
                  } catch (Throwable e) {
                     constructor = class3.getConstructor(classArray);
                  }

                  if (constructor == null) {
                     throw new NoSuchMethodException("Constructor not found: " + class3.getName() + ' ' + Arrays.toString(classArray));
                  }

                  arrayList.add(constructor.newInstance(objs));
               } catch (Throwable e2) {
                  String text3 = null;
                  if (constructor != null) {
                     text3 = Arrays.toString(constructor.getParameterTypes());
                  }

                  System.err.println(class3.getName() + " does not accept " + text3);
                  e2.printStackTrace();
               }
            }
         }
      }

      if (this.flag4) {
         System.out.println("3: returned in " + NumberFormats.computeText((System.nanoTime() - value) / 1000.0 / 1000.0) + " ms");
      }

      return arrayList;
   }

   public void handleLoadFailure(String text, Throwable error) {
      try {
         if (!this.flag3 || !this.checkThrowable(error)) {
            if (this.platform != null) {
               this.logLoadFailure(text, error);
            } else {
               this.printLoadFailure(text, error);
            }

            throw error;
         }
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void logLoadFailure(String text, Throwable error) {
      this.platform.getLogger().severe("**Failed to load: " + text);
      this.platform.getLogger().severe(stackTraceToString(error));
   }

   private final void printLoadFailure(String text, Throwable error) {
      System.err.println("**Failed to load: " + text);
      System.err.println(stackTraceToString(error));
   }

   public boolean checkThrowable(Throwable error) {
      Throwable cause = error.getCause();
      Throwable error2 = cause == null ? error : cause;
      Throwable cause2 = error2.getCause();
      Throwable error3 = cause2 == null ? error2 : cause2;
      String message = error2.getMessage();
      String message2 = error3.getMessage();
      if (message == null) {
         message = "";
      }

      if (message2 == null) {
         message2 = "";
      }

      return error instanceof ClassNotFoundException
         || error instanceof LinkageError
            && (
               error.getCause() == null
                  || error2 instanceof ClassNotFoundException
                  || error2 instanceof LinkageError
                     && (message.contains("NoClassDefFoundError") || message.contains("ClassNotFoundException") || message.contains("loader constraint violation:"))
                  || error2 instanceof LinkageError && error3 instanceof ClassNotFoundException
                  || error2 instanceof ClassCastException && message2.contains("kotlin.internal.")
            );
   }

   public void handleFlag(boolean flag) {
      this.flag2 = flag;
      if (!flag) {
         this.text4 = null;
         this.text5 = null;
         this.list2 = null;
      }
   }
}
