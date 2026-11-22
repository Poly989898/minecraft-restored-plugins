package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;

public class CMILeaderboard<T extends Number & Comparable<T>> {
  private final Map<UUID, T> balances = new ConcurrentHashMap<>();
  private volatile List<Entry<UUID, T>> sorted = Collections.emptyList();
  private volatile Map<UUID, Integer> rankMap = Collections.emptyMap();
  private final AtomicBoolean dirty = new AtomicBoolean(false);
  private final AtomicBoolean running = new AtomicBoolean(false);
  private CMITask task;
  private boolean calculateTotal = false;
  private volatile T total;

  public CMILeaderboard(long longValue) {
    this(longValue, false);
  }

  public CMILeaderboard(long longValue, boolean flag) {
    this.calculateTotal = flag;
    this.task =
        CMIScheduler.runTimerAsync(
            CMI.getInstance(),
            () -> {
              if (this.dirty.get()) {
                if (this.running.compareAndSet(false, true)) {
                  CompletableFuture.runAsync(
                      () -> {
                        try {
                          this.dirty.set(false);
                          this.build();
                        } finally {
                          this.running.set(false);
                        }
                      });
                }
              }
            },
            0L,
            longValue);
  }

  public void setValue(UUID uuid, T value) {
    Number number = this.balances.put(uuid, (T) value);
    if (number == null) {
      if (this.calculateTotal) {
        this.addToTotal((T) value);
      }

      this.dirty.set(true);
    } else {
      if (!number.equals(value)) {
        if (this.calculateTotal) {
          this.subtractFromTotal((T) number);
          this.addToTotal((T) value);
        }

        this.dirty.set(true);
      }
    }
  }

  private void addToTotal(T value) {
    if (this.total == null) {
      this.total = (T) value;
    } else {
      this.total = this.sum(this.total, (T) value);
    }
  }

  private void subtractFromTotal(T value) {
    if (this.total != null) {
      this.total = this.subtract(this.total, (T) value);
    }
  }

  private T sum(T value, T tSecond) {
    if (value instanceof Long) {
      return (T) (Object) (value.longValue() + tSecond.longValue());
    } else if (value instanceof Integer) {
      return (T) (Object) (value.intValue() + tSecond.intValue());
    } else if (value instanceof Double) {
      return (T) (Object) (value.doubleValue() + tSecond.doubleValue());
    } else if (value instanceof Float) {
      return (T) (Object) (value.floatValue() + tSecond.floatValue());
    } else if (value instanceof Short) {
      return (T) (Object) ((short) (value.shortValue() + tSecond.shortValue()));
    } else if (value instanceof BigDecimal) {
      return (T) (Object) (((BigDecimal) value).add((BigDecimal) tSecond));
    } else {
      throw new IllegalStateException("Unsupported number type: " + value.getClass());
    }
  }

  private T subtract(T value, T tSecond) {
    if (value instanceof Long) {
      return (T) (Object) (value.longValue() - tSecond.longValue());
    } else if (value instanceof Integer) {
      return (T) (Object) (value.intValue() - tSecond.intValue());
    } else if (value instanceof Double) {
      return (T) (Object) (value.doubleValue() - tSecond.doubleValue());
    } else if (value instanceof Float) {
      return (T) (Object) (value.floatValue() - tSecond.floatValue());
    } else if (value instanceof Short) {
      return (T) (Object) ((short) (value.shortValue() - tSecond.shortValue()));
    } else if (value instanceof BigDecimal) {
      return (T) (Object) (((BigDecimal) value).subtract((BigDecimal) tSecond));
    } else {
      throw new IllegalStateException("Unsupported number type: " + value.getClass());
    }
  }

  public Entry<UUID, T> getByPlace(int value) {
    var list = this.sorted;
    return value > 0 && value <= list.size() ? (Entry) list.get(value - 1) : null;
  }

  public UUID getUUIDByPlace(int value) {
    var entry = this.getByPlace(value);
    return entry != null ? (UUID) entry.getKey() : null;
  }

  public T getValueByPlace(int value) {
    var entry = this.getByPlace(value);
    return (T) (Object) ((entry != null ? entry.getValue() : null));
  }

  public T getValue(UUID uuid) {
    return this.balances.get(uuid);
  }

  public Map<UUID, T> getValues() {
    var list = this.sorted;
    LinkedHashMap<UUID, Number> linkedHashMap = new LinkedHashMap(list.size());

    for (Entry entry : list) {
      linkedHashMap.put((UUID) entry.getKey(), (Number) entry.getValue());
    }

    return (Map<UUID, T>) (Map) linkedHashMap;
  }

  public List<Entry<UUID, T>> getTop(int value) {
    var list = this.sorted;
    return list.subList(0, Math.min(value, list.size()));
  }

  public List<Entry<UUID, T>> getRange(int value, int count) {
    var list = this.sorted;
    int countSecond = list.size();
    value = Math.max(0, value);
    count = Math.min(countSecond, count + 1);
    return value >= count ? Collections.emptyList() : list.subList(value, count);
  }

  public int countSince(long longValue) {
    var list = this.sorted;
    int value = 0;
    int count = list.size() - 1;
    int countSecond = list.size();

    while (value <= count) {
      int countThird = value + count >>> 1;
      long time = ((Number) ((Entry) list.get(countThird)).getValue()).longValue();
      if (time >= longValue) {
        value = countThird + 1;
      } else {
        countSecond = countThird;
        count = countThird - 1;
      }
    }

    return countSecond;
  }

  public int getRank(UUID uuid) {
    return this.rankMap.getOrDefault(uuid, -1);
  }

  public int getRecordCount() {
    return this.rankMap.size();
  }

  public CompletableFuture<T> rebuild() {
    return CompletableFuture.supplyAsync(() -> this.build());
  }

  private T build() {
    var arrayList = new ArrayList<>(this.balances.entrySet());
    if (arrayList.isEmpty()) {
      return null;
    }

    Collections.sort(
        arrayList,
        Collections.reverseOrder(
            new Comparator<Entry<UUID, T>>() {
              public int compare(Entry<UUID, T> entry, Entry<UUID, T> entrySecond) {
                int value =
                    ((Comparable) entry.getValue()).compareTo((Number) entrySecond.getValue());
                return value != 0
                    ? value
                    : ((UUID) entry.getKey()).compareTo((UUID) entrySecond.getKey());
              }
            }));
    HashMap<UUID, Integer> hashMap = new HashMap(arrayList.size());

    for (int count = 0; count < arrayList.size(); count++) {
      hashMap.put((UUID) ((Entry) arrayList.get(count)).getKey(), count + 1);
    }

    this.sorted = new ArrayList<>(arrayList);
    this.rankMap = new HashMap<>(hashMap);
    this.dirty.set(false);
    this.onRebuildComplete(this.total);
    return this.total;
  }

  protected void onRebuildComplete(T value) {}

  public void shutdown() {
    if (this.task != null) {
      this.task.cancel();
      this.task = null;
    }
  }

  public boolean isCalculateTotal() {
    return this.calculateTotal;
  }

  public void setCalculateTotal(boolean flag) {
    this.calculateTotal = flag;
  }
}
