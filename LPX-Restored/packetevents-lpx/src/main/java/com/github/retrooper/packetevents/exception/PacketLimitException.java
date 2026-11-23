/*
 * Reconstructed for LPX (fork-only class). Not part of upstream PacketEvents.
 *
 * Upstream PacketEvents is distributed under the GNU GPL v3 (see
 * packetevents-lpx/LICENSE-PacketEvents.txt); this file follows the same license.
 */
package com.github.retrooper.packetevents.exception;

/**
 * Thrown when a value read from the wire exceeds a configured limit.
 * Extends {@link RuntimeException} directly (not {@link PacketProcessException}),
 * and does not record a stack trace, matching the original fork.
 */
public class PacketLimitException extends RuntimeException {

    private final Limit limit;
    private final long value;
    private final long max;

    public PacketLimitException(Limit limit, long value, long max) {
        super(limit.getDescription() + " exceeded (" + value + " > " + max + ")", null, false, false);
        this.limit = limit;
        this.value = value;
        this.max = max;
    }

    public Limit getLimit() {
        return this.limit;
    }

    public long getValue() {
        return this.value;
    }

    public long getMax() {
        return this.max;
    }

    public enum Limit {
        ITEM_DEPTH("Item depth"),
        ITEMS_PER_PACKET("Items per packet"),
        COLLECTION_SIZE("Collection size"),
        MAP_SIZE("Map size"),
        ARRAY_SIZE("Array size"),
        STRING_LENGTH("String length"),
        BYTE_ARRAY_LENGTH("Byte array length"),
        LENGTH_PREFIXED_SIZE("Length-prefixed size"),
        PAYLOAD_SIZE("Payload size"),
        BOOK_PAGES("Book pages"),
        NBT_SIZE("NBT size"),
        NBT_DEPTH("NBT depth"),
        NBT_ELEMENTS("NBT elements per packet");

        private final String description;

        Limit(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }
    }
}
