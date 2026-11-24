package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkullTextureData implements StatData, RandomStatData<SkullTextureData> {
   private Object profile;

   public SkullTextureData(Object var1) {
      this.profile = var1;
   }

   @Nullable
   public Object getGameProfile() {
      return this.profile;
   }

   public void setGameProfile(@Nullable Object var1) {
      this.profile = var1;
   }

   @NotNull
   public SkullTextureData clone() {
      return new SkullTextureData(this.profile);
   }

   @Override
   public boolean isEmpty() {
      return this.profile == null;
   }

   public SkullTextureData randomize(MMOItemBuilder var1) {
      return this;
   }
}
