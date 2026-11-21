package it.ytnoos.lpx.check.checks.window;

import com.github.retrooper.packetevents.protocol.item.HashedStack;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

public class WindowE extends PlayerCheck<WrapperPlayClientClickWindow> {
   private int maxHashedSlotChanges;
   private int maxHashedComponentsPerStack;

   public WindowE(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.maxHashedSlotChanges = this.getInt("max-hashed-slot-changes", 90);
      this.maxHashedComponentsPerStack = this.getInt("max-hashed-components-per-stack", 128);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, WrapperPlayClientClickWindow wrapperPlayClientClickWindow) {
      Map map = wrapperPlayClientClickWindow.getHashedSlots();
      if (map != null) {
         if (map.size() > this.maxHashedSlotChanges) {
            this.raiseViolation(
               wrapperPlayClientClickWindow,
               new FlagInfo[]{
                  FlagInfo.of("hashed-slot-changes", map.size()),
                  FlagInfo.of("max-hashed-slot-changes", this.maxHashedSlotChanges),
                  FlagInfo.of("click-type", wrapperPlayClientClickWindow.getWindowClickType().name())
               }
            );
            return CheckResult.DENY;
         }

         for (Object var13_o : map.entrySet()) { Entry entry = (Entry) var13_o;
            CheckResult checkResult = this.checkHashedStack(wrapperPlayClientClickWindow, "slot[" + entry.getKey() + "]", (HashedStack)((Optional)entry.getValue()).orElse(null));
            if (checkResult == CheckResult.DENY) {
               return checkResult;
            }
         }
      }

      Optional optional = wrapperPlayClientClickWindow.getCarriedHashedStack();
      return this.checkHashedStack(wrapperPlayClientClickWindow, "carried", optional == null ? null : (HashedStack)optional.orElse(null));
   }

   private CheckResult checkHashedStack(WrapperPlayClientClickWindow wrapperPlayClientClickWindow, Object object, HashedStack hashedStack) {
      if (hashedStack == null) {
         return CheckResult.ALLOW;
      } else {
         int value = hashedStack.getComponents().getAddedComponents().size();
         int value2 = hashedStack.getComponents().getRemovedComponents().size();
         int value3 = value + value2;
         if (value3 > this.maxHashedComponentsPerStack) {
            this.raiseViolation(
               wrapperPlayClientClickWindow,
               new FlagInfo[]{
                  FlagInfo.of("identifier", object),
                  FlagInfo.of("item", hashedStack.getItem().getName().toString()),
                  FlagInfo.of("added", value),
                  FlagInfo.of("removed", value2),
                  FlagInfo.of("component-changes", value3),
                  FlagInfo.of("max-component-changes", this.maxHashedComponentsPerStack)
               }
            );
            return CheckResult.DENY;
         } else {
            return CheckResult.ALLOW;
         }
      }
   }

}
