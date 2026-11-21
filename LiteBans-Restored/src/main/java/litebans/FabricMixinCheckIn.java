package litebans;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class FabricMixinCheckIn implements IMixinConfigPlugin {
   private String b_f;

   public void onLoad(String var1) {
      this.b_f = var1;
   }

   public String getRefMapperConfig() {
      return "";
   }

   public boolean shouldApplyMixin(String var1, String var2) {
      if (this.b_f != null && !var2.startsWith(this.b_f)) {
         return true;
      }

      if (var2.contains("_")) {
         String var6 = var2.substring(var2.indexOf("_") + 1);
         String var4 = ((ModContainer)FabricLoader.getInstance().getModContainer("fabric-api").get())
               .getMetadata()
               .getVersion()
               .getFriendlyString()
               .split("\\+")[1]
            .replace(".", "_");
         if (var6.endsWith("_")) {
            int var5 = Integer.parseInt(var4.split("_")[0]);
            return var5 >= Integer.parseInt(var6.replace("_", ""));
         } else {
            return var4.contains(var6);
         }
      } else {
         return true;
      }
   }

   public void acceptTargets(Set var1, Set var2) {
   }

   public List getMixins() {
      return null;
   }

   public void preApply(String var1, ClassNode var2, String var3, IMixinInfo var4) {
   }

   public void postApply(String var1, ClassNode var2, String var3, IMixinInfo var4) {
   }
}
