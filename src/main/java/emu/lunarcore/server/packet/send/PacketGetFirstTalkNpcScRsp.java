package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.FirstNpcTalkInfoOuterClass;
import emu.lunarcore.proto.GetFirstTalkNpcScRspOuterClass;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import us.hebi.quickbuf.RepeatedInt;

public class PacketGetFirstTalkNpcScRsp extends BasePacket {
    public PacketGetFirstTalkNpcScRsp(RepeatedInt npcId) {
        super(CmdId.GetFirstTalkNpcScRsp);

        var proto = GetFirstTalkNpcScRspOuterClass.GetFirstTalkNpcScRsp.newInstance();
        for (var id : npcId) {
            proto.addNpcTalkInfoList(FirstNpcTalkInfoOuterClass.FirstNpcTalkInfo.newInstance().setNpcId(id));
        }

        this.setData(proto);
    }
}
