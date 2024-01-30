package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.*;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketHandleRogueCommonPendingActionScRsp extends BasePacket {
    public PacketHandleRogueCommonPendingActionScRsp(RogueBonusSelectOuterClass.RogueBonusSelect bonusSelect,
                                                     RogueMiracleSelectOuterClass.RogueMiracleSelect miracleSelect,
                                                     RogueBuffSelectOuterClass.RogueBuffSelect rogueBuffSelect,
                                                     RogueRerollBuffOuterClass.RogueRerollBuff rogueRerollBuff,
                                                     Player player) {
        super(CmdId.HandleRogueCommonPendingActionScRsp);

        var data = HandleRogueCommonPendingActionScRspOuterClass.HandleRogueCommonPendingActionScRsp.newInstance();
        if (bonusSelect != null) {
            data.setBonusSelect(bonusSelect);
        }
        if (miracleSelect != null) {
            data.setMiracleSelect(miracleSelect);
        }
        if (rogueBuffSelect != null) {
            data.setRogueBuffSelect(rogueBuffSelect);
        }
        if (rogueRerollBuff != null) {
            data.setRogueRerollBuff(rogueRerollBuff);
        }
        data.setTimes(player.getRogueInstance().getTimes() - 3);
        this.setData(data);
    }
}
