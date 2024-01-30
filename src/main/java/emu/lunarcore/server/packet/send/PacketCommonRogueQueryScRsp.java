package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.CommonRogueQueryOuterClass;
import emu.lunarcore.proto.CommonRogueQueryScRspOuterClass;
import emu.lunarcore.proto.RogueQueryOuterClass;
import emu.lunarcore.proto.RogueUpdateOuterClass;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketCommonRogueQueryScRsp extends BasePacket {
    public PacketCommonRogueQueryScRsp(Player player) {
        super(CmdId.CommonRogueQueryScRsp);

        var data = CommonRogueQueryScRspOuterClass.CommonRogueQueryScRsp.newInstance()
                .setCommonRogueQuery(CommonRogueQueryOuterClass.CommonRogueQuery.newInstance()
                    .setRogueUpdate(RogueUpdateOuterClass.RogueUpdate.newInstance()
                        .setAreaId(player.getRogueInstance().getAreaId())
                        .setLOKIGPFHHIN(202)
                        .setKPJNNFIINNB(202))
                    .setRogueQuery(RogueQueryOuterClass.RogueQuery.newInstance()
                        .addAllFFJHNGIINMN(2, 0, 1)));

        this.setData(data);
    }
}
