package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.RogueStatusOuterClass;
import emu.lunarcore.proto.SyncRogueStatusScNotifyOuterClass;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueStatusScNotify extends BasePacket {
    public PacketSyncRogueStatusScNotify(RogueStatusOuterClass.RogueStatus rogueStatus) {
        super(CmdId.SyncRogueStatusScNotify);

        var data = SyncRogueStatusScNotifyOuterClass.SyncRogueStatusScNotify.newInstance()
                .setRogueStatus(rogueStatus);

        this.setData(data);
    }
}
