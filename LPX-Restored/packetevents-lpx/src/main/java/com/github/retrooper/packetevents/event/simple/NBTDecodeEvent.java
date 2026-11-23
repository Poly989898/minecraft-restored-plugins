/*
 * Reconstructed for LPX (fork-only class). Not part of upstream PacketEvents.
 *
 * Upstream PacketEvents is distributed under the GNU GPL v3 (see
 * packetevents-lpx/LICENSE-PacketEvents.txt); this file follows the same license.
 */
package com.github.retrooper.packetevents.event.simple;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.exception.PacketProcessException;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTEnd;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

/**
 * Fired after an NBT tag has been read from a packet.
 * The tag's decoded size is recorded only if it is not {@link NBTEnd} and has no size yet.
 */
public class NBTDecodeEvent extends PacketEvent {

    private final PacketWrapper<?> wrapper;
    private final ProtocolPacketEvent event;
    private final int decodedSizeBytes;
    private NBT nbt;

    public NBTDecodeEvent(PacketWrapper<?> wrapper, ProtocolPacketEvent event, NBT nbt) throws PacketProcessException {
        this(wrapper, event, nbt, nbt.getDecodedSizeBytes());
    }

    public NBTDecodeEvent(PacketWrapper<?> wrapper, ProtocolPacketEvent event, NBT nbt, int decodedSizeBytes) throws PacketProcessException {
        this.wrapper = wrapper;
        this.event = event;
        this.nbt = nbt;
        this.decodedSizeBytes = decodedSizeBytes;
        if (nbt != NBTEnd.INSTANCE && nbt.getDecodedSizeBytes() < 0) {
            nbt.setDecodedSizeBytes(decodedSizeBytes);
        }
    }

    public PacketWrapper<?> getWrapper() {
        return this.wrapper;
    }

    public ProtocolPacketEvent getEvent() {
        return this.event;
    }

    public NBT getNbt() {
        return this.nbt;
    }

    public int getDecodedSizeBytes() {
        return this.decodedSizeBytes;
    }

    public void setNbt(NBT nbt) {
        if (nbt != NBTEnd.INSTANCE && nbt.getDecodedSizeBytes() < 0) {
            nbt.setDecodedSizeBytes(this.decodedSizeBytes);
        }
        this.nbt = nbt;
    }
}
