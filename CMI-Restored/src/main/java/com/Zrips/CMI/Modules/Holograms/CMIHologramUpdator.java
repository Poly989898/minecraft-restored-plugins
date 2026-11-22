package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;

public class CMIHologramUpdator {
  private static final ConcurrentHashMap<UUID, CMIHologramUpdator.Worker> WORKERS =
      new ConcurrentHashMap<>();
  private static CMITask task = null;

  public static void start() {
    if (task == null) {
      task =
          CMIScheduler.runTimerAsync(
              CMI.getInstance(),
              () -> {
                try {
                  recheckAll();
                } catch (Throwable throwable) {
                  throwable.printStackTrace();
                }
              },
              1L,
              1L);
    }
  }

  public static CompletableFuture<Void> requestUpdate(
      UUID uuid, CMIHologram cmiHologram, CMIHologramUpdateType... cmiHologramUpdateTypes) {
    CMIHologramUpdator.Worker worker =
        WORKERS.computeIfAbsent(uuid, CMIHologramUpdator.Worker::new);
    EnumSet enumSet =
        cmiHologramUpdateTypes.length == 0
            ? EnumSet.noneOf(CMIHologramUpdateType.class)
            : EnumSet.copyOf(Arrays.asList(cmiHologramUpdateTypes));
    return enumSet.isEmpty()
        ? CompletableFuture.completedFuture(null)
        : worker.enqueue(cmiHologram, enumSet);
  }

  private static void recheckAll() {
    CMI.getInstance()
        .getHologramManager()
        .getPlayerInUpdateRangeSnapshot()
        .forEach(
            (uuid, set) -> {
              CMIHologramUpdator.Worker worker =
                  WORKERS.computeIfAbsent(uuid, CMIHologramUpdator.Worker::new);
              set.forEach(
                  cmiHologram -> {
                    var cycleSet = cmiHologram.initializeAutoCycle();
                    worker.enqueue(cmiHologram, cycleSet);
                  });
            });
    WORKERS
        .entrySet()
        .removeIf(
            entry -> {
              CMIHologramUpdator.Worker entryWorker = entry.getValue();
              return !entryWorker.run() && entryWorker.isEmpty();
            });
  }

  public static void shutdown() {
    CMITask cmiTask = task;
    if (cmiTask != null) {
      cmiTask.cancel();
      task = null;
    }

    WORKERS
        .values()
        .forEach(worker -> worker.failAll(new IllegalStateException("System shutdown")));
    WORKERS.clear();
  }

  public static void remove(UUID uuid) {
    CMIHologramUpdator.Worker worker = WORKERS.remove(uuid);
    if (worker != null) {
      worker.failAll(new IllegalStateException("Worker removed"));
    }
  }

  private static class Request {
    private final Set<CMIHologramUpdateType> types = EnumSet.noneOf(CMIHologramUpdateType.class);
    private final CompletableFuture<Void> future = new CompletableFuture<>();

    private Request() {}

    synchronized void addAll(Set<CMIHologramUpdateType> set) {
      this.types.addAll(set);
    }

    synchronized Set<CMIHologramUpdateType> snapshot() {
      return this.types.isEmpty()
          ? EnumSet.noneOf(CMIHologramUpdateType.class)
          : EnumSet.copyOf(this.types);
    }

    CompletableFuture<Void> getFuture() {
      return this.future;
    }

    void complete() {
      this.future.complete(null);
    }
  }

  private static class Worker {
    private final UUID playerUUID;
    private final ConcurrentHashMap<CMIHologram, CMIHologramUpdator.Request> pending =
        new ConcurrentHashMap<>();

    public Worker(UUID uuid) {
      this.playerUUID = uuid;
    }

    public CompletableFuture<Void> enqueue(
        CMIHologram cmiHologram, Set<CMIHologramUpdateType> set) {
      CMIHologramUpdator.Request request =
          this.pending.compute(
              cmiHologram,
              (hologramKey, existingRequest) -> {
                if (existingRequest == null) {
                  CMIHologramUpdator.Request newRequest = new CMIHologramUpdator.Request();
                  newRequest.addAll(set);
                  return newRequest;
                } else {
                  existingRequest.addAll(set);
                  return (CMIHologramUpdator.Request) existingRequest;
                }
              });
      return request.getFuture();
    }

    public boolean run() {
      HashMap<CMIHologram, com.Zrips.CMI.Modules.Holograms.CMIHologramUpdator.Request> hashMap =
          new HashMap();
      this.pending.forEach(
          (cmiHologram, request) -> {
            CMIHologramUpdator.Request pendingRequest = this.pending.remove(cmiHologram);
            if (pendingRequest != null) {
              hashMap.put(cmiHologram, pendingRequest);
            }
          });
      if (hashMap.isEmpty()) {
        return false;
      }

      hashMap.forEach(
          (object, request) -> {
            try {
              object
                  .updateCycle(this.playerUUID, request.snapshot())
                  .whenComplete(
                      (value, error) -> {
                        if (error != null) {
                          request.getFuture().completeExceptionally(error);
                        } else if (!request.getFuture().isDone()) {
                          request.complete();
                        }
                      });
            } catch (Throwable throwable) {
              request.getFuture().completeExceptionally(throwable);
            }
          });
      return true;
    }

    public void failAll(Throwable throwable) {
      var hashMap = new HashMap<>(this.pending);
      this.pending.clear();
      hashMap.values().forEach(object -> object.getFuture().completeExceptionally(throwable));
    }

    public boolean isEmpty() {
      return this.pending.isEmpty();
    }
  }
}
