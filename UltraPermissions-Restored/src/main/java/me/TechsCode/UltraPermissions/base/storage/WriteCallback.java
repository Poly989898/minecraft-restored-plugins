package me.TechsCode.UltraPermissions.base.storage;

public interface WriteCallback {
   void onSuccess();

   void onFailure(Exception exception);

   class EmptyWriteCallback implements WriteCallback {
      public EmptyWriteCallback() {
      }

      @Override
      public void onSuccess() {
      }

      @Override
      public void onFailure(Exception exception) {
      }
   }
}
