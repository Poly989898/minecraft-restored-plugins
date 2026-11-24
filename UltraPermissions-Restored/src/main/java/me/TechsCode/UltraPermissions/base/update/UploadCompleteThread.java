package me.TechsCode.UltraPermissions.base.update;

import java.io.File;

public abstract class UploadCompleteThread implements Runnable {
   private File file;
   private Thread thread;

   public UploadCompleteThread(File file) {
      this.file = file;
      this.thread = new Thread(this);
      this.thread.start();
   }

   @Override
   public void run() {
      boolean flag = true;

      for (long i = this.file.length(); flag; i = this.file.length()) {
         try {
            Thread.sleep(1000L);
         } catch (InterruptedException exception) {
            exception.printStackTrace();
         }

         if (i == this.file.length()) {
            flag = false;
         }
      }

      this.onComplete();
   }

   public abstract void onComplete();
}
