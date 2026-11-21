package io.lumine.mythic.bukkit.utils.version;

import com.google.common.collect.ImmutableSet;
import io.lumine.mythic.bukkit.utils.collections.Indexing;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum NMSVersion {
   NONE {
      @Override
      protected String getPackageComponent() {
         return ".";
      }
   },
   v1_8_R1(MinecraftVersion.of(1, 8, 0)),
   v1_8_R2(MinecraftVersion.of(1, 8, 3)),
   v1_8_R3(MinecraftVersion.of(1, 8, 4), MinecraftVersion.of(1, 8, 5), MinecraftVersion.of(1, 8, 6), MinecraftVersion.of(1, 8, 7), MinecraftVersion.of(1, 8, 8)),
   v1_9_R1(MinecraftVersion.of(1, 9, 0), MinecraftVersion.of(1, 9, 2)),
   v1_9_R2(MinecraftVersion.of(1, 9, 4)),
   v1_10_R1(MinecraftVersion.of(1, 10, 0), MinecraftVersion.of(1, 10, 2)),
   v1_11_R1(MinecraftVersion.of(1, 11, 0), MinecraftVersion.of(1, 11, 1), MinecraftVersion.of(1, 11, 2)),
   v1_12_R1(MinecraftVersion.of(1, 12, 0), MinecraftVersion.of(1, 12, 1), MinecraftVersion.of(1, 12, 2)),
   v1_13_R1(MinecraftVersion.of(1, 13, 0)),
   v1_13_R2(MinecraftVersion.of(1, 13, 1), MinecraftVersion.of(1, 13, 2)),
   v1_14_R1(
      MinecraftVersion.of(1, 14, 0), MinecraftVersion.of(1, 14, 1), MinecraftVersion.of(1, 14, 2), MinecraftVersion.of(1, 14, 3), MinecraftVersion.of(1, 14, 4)
   ),
   v1_15_R1(MinecraftVersion.of(1, 15, 0), MinecraftVersion.of(1, 15, 1), MinecraftVersion.of(1, 15, 2)),
   v1_16_R1(MinecraftVersion.of(1, 16, 0), MinecraftVersion.of(1, 16, 1)),
   v1_16_R2(MinecraftVersion.of(1, 16, 2)),
   v1_17_R1(MinecraftVersion.of(1, 17, 0)),
   v1_18_R1(MinecraftVersion.of(1, 18, 0)),
   v1_19_R1(MinecraftVersion.of(1, 19, 0), MinecraftVersion.of(1, 19, 1)),
   v1_19_R2(MinecraftVersion.of(1, 19, 2)),
   v1_19_R3(MinecraftVersion.of(1, 19, 4)),
   v1_20_R1(MinecraftVersion.of(1, 20, 0), MinecraftVersion.of(1, 20, 1)),
   v1_20_R2(MinecraftVersion.of(1, 20, 2)),
   v1_20_R3(MinecraftVersion.of(1, 20, 3), MinecraftVersion.of(1, 20, 4)),
   v1_20_R4(MinecraftVersion.of(1, 20, 5), MinecraftVersion.of(1, 20, 6), MinecraftVersion.of(1, 20, 7)),
   v1_21_R1(MinecraftVersion.of(1, 21, 0), MinecraftVersion.of(1, 21, 1)),
   v1_21_R2(MinecraftVersion.of(1, 21, 2), MinecraftVersion.of(1, 21, 3)),
   v1_21_R3(MinecraftVersion.of(1, 21, 4)),
   v1_21_R4(MinecraftVersion.of(1, 21, 5)),
   v1_21_R5(MinecraftVersion.of(1, 21, 6), MinecraftVersion.of(1, 21, 7), MinecraftVersion.of(1, 21, 8)),
   v1_21_R6(MinecraftVersion.of(1, 21, 9), MinecraftVersion.of(1, 21, 10)),
   v1_21_R7(MinecraftVersion.of(1, 21, 11), MinecraftVersion.of(1, 21, 12)),
   v26_1(MinecraftVersion.of(26, 1, 0), MinecraftVersion.of(26, 1, 1), MinecraftVersion.of(26, 1, 2)),
   v26_2(MinecraftVersion.of(26, 2, 0), MinecraftVersion.of(26, 2, 1), MinecraftVersion.of(26, 2, 2)),
   v26_3(MinecraftVersion.of(26, 3, 0), MinecraftVersion.of(26, 3, 1), MinecraftVersion.of(26, 3, 2));

   private final Set<MinecraftVersion> minecraftVersions;
   private final String nmsPrefix;
   private final String obcPrefix;
   private static final Map<MinecraftVersion, NMSVersion> MC_TO_NMS = Indexing.buildMultiple(values(), NMSVersion::getMinecraftVersions);

   private NMSVersion(MinecraftVersion... minecraftVersions) {
      this.minecraftVersions = ImmutableSet.copyOf(minecraftVersions);
      this.nmsPrefix = "net.minecraft.server" + this.getPackageComponent();
      this.obcPrefix = "org.bukkit.craftbukkit" + this.getPackageComponent();
   }

   protected String getPackageComponent() {
      return "." + this.name() + ".";
   }

   public Set<MinecraftVersion> getMinecraftVersions() {
      return this.minecraftVersions;
   }

   @Nonnull
   public String nms(String className) {
      return this.nmsPrefix + className;
   }

   @Nonnull
   public Class<?> nmsClass(String className) throws ClassNotFoundException {
      return Class.forName(this.nms(className));
   }

   @Nonnull
   public String obc(String className) {
      return this.obcPrefix + className;
   }

   @Nonnull
   public Class<?> obcClass(String className) throws ClassNotFoundException {
      return Class.forName(this.obc(className));
   }

   @Nullable
   public static Optional<NMSVersion> forMinecraftVersion(MinecraftVersion minecraftVersion) {
      return Optional.ofNullable(MC_TO_NMS.get(minecraftVersion));
   }

   @Nullable
   public static Optional<NMSVersion> forCurrentVersion() {
      return Optional.ofNullable(MC_TO_NMS.get(MinecraftVersion.getRuntimeVersion()));
   }
}
