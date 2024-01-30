package emu.lunarcore.server.packet.send;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.RogueBuffExcel;
import emu.lunarcore.proto.GetRogueHandbookDataScRspOuterClass.GetRogueHandbookDataScRsp;
import emu.lunarcore.proto.RogueHandbookBuffOuterClass;
import emu.lunarcore.proto.RogueHandbookDataOuterClass.RogueHandbookData;
import emu.lunarcore.proto.RogueHandbookMiracleOuterClass;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetRogueHandbookDataScRsp extends BasePacket {

    public PacketGetRogueHandbookDataScRsp() {
        super(CmdId.GetRogueHandbookDataScRsp);

        var handbook = RogueHandbookData.newInstance();
        for (var buff : GameDepot.getRogueRandomBuffList()) {
            handbook.addBuffList(RogueHandbookBuffOuterClass.RogueHandbookBuff.newInstance()
                .setBuffId(buff.getId()));
        }
        for (var miracle : GameDepot.getRogueRandomMiracleList()) {
            handbook.addMiracleList(RogueHandbookMiracleOuterClass.RogueHandbookMiracle.newInstance()
                .setMiracleId(miracle.getId())
                .setIsUnlocked(true));
        }
        for (var buff : GameDepot.getRogueAeonBuffs().values()) {
            handbook.addBuffList(RogueHandbookBuffOuterClass.RogueHandbookBuff.newInstance()
                .setBuffId(buff.getId()));
        }
        for (var buff : GameDepot.getRogueAeonEnhanceBuffs().values()) {
            for (var enhance : buff) {
                handbook.addBuffList(RogueHandbookBuffOuterClass.RogueHandbookBuff.newInstance()
                    .setBuffId(enhance.getId()));
            }
        }

        var data = GetRogueHandbookDataScRsp.newInstance();
        data.setHandbookInfo(handbook);
        this.setData(data);
    }
}
