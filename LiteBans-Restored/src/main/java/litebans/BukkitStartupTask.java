package litebans;

class BukkitStartupTask implements Runnable {
   final BukkitPlugin bukkitPlugin;

   BukkitStartupTask(BukkitPlugin bukkitPlugin) {
      this.bukkitPlugin = bukkitPlugin;
   }

   @Override
   public void run() {
      try {
         if (!this.bukkitPlugin.obtainFlag8()) {
            Thread.sleep(1250L);
         }

         ClassUtil classUtil = new ClassUtil(this.bukkitPlugin);
         classUtil.preloadClasses(ClassUtil.getJarPath(ClassUtil.class), true);
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
