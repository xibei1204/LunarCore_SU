package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.CommonRogueUpdateScNotifyOuterClass;
import emu.lunarcore.proto.RogueUpdateOuterClass;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketCommonRogueUpdateScNotify extends BasePacket {
    public PacketCommonRogueUpdateScNotify(int areaId) {
        super(CmdId.CommonRogueUpdateScNotify);

        var data = CommonRogueUpdateScNotifyOuterClass.CommonRogueUpdateScNotify.newInstance()
            .setRogueUpdate(RogueUpdateOuterClass.RogueUpdate.newInstance()
                .setAreaId(areaId)
                .setLOKIGPFHHIN(101)
                .setKPJNNFIINNB(202));

        this.setData(data);
    }
}
