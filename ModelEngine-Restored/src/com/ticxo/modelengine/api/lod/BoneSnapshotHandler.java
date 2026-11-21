package com.ticxo.modelengine.api.lod;

import com.ticxo.modelengine.api.model.render.DisplayBone;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BoneSnapshotHandler {
   private final DisplayBone bone;
   private final Map<UUID, BoneSnapshotHandler.Snapshot> snapshots = new ConcurrentHashMap<>();
   private BoneSnapshotHandler.Snapshot snapshot;

   public void recordSnapshot() {
      int pos = this.bone.getPosition().get().hashCode();
      int lRot = this.bone.getLeftRotation().get().hashCode();
      int scl = this.bone.getScale().get().hashCode();
      int rRot = this.bone.getRightRotation().get().hashCode();
      BoneSnapshotHandler.Snapshot prev = this.snapshot;
      if (prev == null || prev.position != pos || prev.leftRotation != lRot || prev.scale != scl || prev.rightRotation != rRot) {
         this.snapshot = new BoneSnapshotHandler.Snapshot(pos, lRot, scl, rRot);
      }
   }

   public byte getUpdate(UUID uuid) {
      BoneSnapshotHandler.Snapshot lastSnapshot = this.snapshots.get(uuid);
      if (lastSnapshot == this.snapshot) {
         return 0;
      }

      this.snapshots.put(uuid, this.snapshot);
      return lastSnapshot == null ? 15 : lastSnapshot.compare(this.snapshot);
   }

   public void remove(UUID uuid) {
      this.snapshots.remove(uuid);
   }

   public record Snapshot(int position, int leftRotation, int scale, int rightRotation) {
      public byte compare(BoneSnapshotHandler.Snapshot snapshot) {
         return (byte)(
            (this.position != snapshot.position ? 1 : 0)
               | (this.leftRotation != snapshot.leftRotation ? 2 : 0)
               | (this.scale != snapshot.scale ? 4 : 0)
               | (this.rightRotation != snapshot.rightRotation ? 8 : 0)
         );
      }
   }
}
