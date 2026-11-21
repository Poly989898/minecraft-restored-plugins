package litebans;

import org.jetbrains.annotations.NotNull;

public final class PlatformState {
   private PlatformState() {
   }

   public final boolean obtainFlag() {
      return PlatformHolder.obtainFlag();
   }

   public final void handleFlag(boolean flag) {
      PlatformHolder.handleFlag(flag);
   }

   public final Platform obtainPlatform() {
      if (PlatformHolder.platform != null) {
         return PlatformHolder.platform;
      }

      Intrinsics.throwUninitializedProperty("");
      return null;
   }

   public final void handlePlatform(@NotNull Platform platform) {
      PlatformHolder.platform = platform;
   }

   public PlatformState(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
