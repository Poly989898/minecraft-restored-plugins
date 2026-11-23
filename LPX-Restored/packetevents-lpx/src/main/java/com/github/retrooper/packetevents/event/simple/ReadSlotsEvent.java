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
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

/**
 * Fired when a packet's slot collection size has been read (for example the
 * window click packet's slot map), before the slots themselves are decoded.
 */
public class ReadSlotsEvent extends PacketEvent {

    private final PacketWrapper<?> wrapper;
    private final ProtocolPacketEvent event;
    private final int slotsCount;

    public ReadSlotsEvent(PacketWrapper<?> wrapper, ProtocolPacketEvent event, int slotsCount) throws PacketProcessException {
        this.wrapper = wrapper;
        this.event = event;
        this.slotsCount = slotsCount;
    }

    public PacketWrapper<?> getWrapper() {
        return this.wrapper;
    }

    public ProtocolPacketEvent getEvent() {
        return this.event;
    }

    public int getSlotsCount() {
        return this.slotsCount;
    }
}
