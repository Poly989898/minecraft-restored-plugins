/*
 * Reconstructed for LPX (fork-only class). Not part of upstream PacketEvents.
 *
 * Upstream PacketEvents is distributed under the GNU GPL v3 (see
 * packetevents-lpx/LICENSE-PacketEvents.txt); this file follows the same license.
 */
package com.github.retrooper.packetevents.protocol.nbt;

import com.github.retrooper.packetevents.exception.PacketLimitException;

/**
 * Counts NBT elements read while decoding one packet, across all tags in that packet.
 * Throws {@link PacketLimitException} with {@link PacketLimitException.Limit#NBT_ELEMENTS}
 * once the total exceeds the configured maximum.
 */
public final class NBTElementCounter {

    private final int max;
    private int count;

    public NBTElementCounter(int max) {
        this.max = max;
    }

    public void add(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can't count a negative amount of NBT elements: " + amount);
        }
        long total = (long) this.count + (long) amount;
        if (total > (long) this.max) {
            throw new PacketLimitException(PacketLimitException.Limit.NBT_ELEMENTS, total, this.max);
        }
        this.count = (int) total;
    }
}
