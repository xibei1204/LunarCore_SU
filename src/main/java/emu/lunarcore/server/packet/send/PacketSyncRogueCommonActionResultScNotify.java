package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.*;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueCommonActionResultScNotify extends BasePacket {

    public PacketSyncRogueCommonActionResultScNotify(RogueActionResultOuterClass.RogueActionResult action) {
        super(CmdId.SyncRogueCommonActionResultScNotify);

        var data = SyncRogueCommonActionResultScNotifyOuterClass.SyncRogueCommonActionResultScNotify.newInstance()
            .setAction(action);

        this.setData(data);
    }

    public PacketSyncRogueCommonActionResultScNotify(RogueBuffSourceOuterClass.RogueBuffSource source, int addBuffId, int level) {
        super(CmdId.SyncRogueCommonActionResultScNotify);

        var data = SyncRogueCommonActionResultScNotifyOuterClass.SyncRogueCommonActionResultScNotify.newInstance()
            .setOCPBNBPAMEN(101)
            .setAction(RogueActionResultOuterClass.RogueActionResult.newInstance()
                .setSource(source)
                .setActionData(RogueActionResultDataOuterClass.RogueActionResultData.newInstance()
                    .setAddBuffList(RogueBuffDataOuterClass.RogueBuffData.newInstance()
                        .setBuffId(addBuffId)
                        .setLevel(level))));

        this.setData(data);
    }

    public PacketSyncRogueCommonActionResultScNotify(RogueBuffSourceOuterClass.RogueBuffSource source, RogueMiracleOuterClass.RogueMiracle miracle) {
        super(CmdId.SyncRogueCommonActionResultScNotify);

        var data = SyncRogueCommonActionResultScNotifyOuterClass.SyncRogueCommonActionResultScNotify.newInstance()
            .setOCPBNBPAMEN(101)
            .setAction(RogueActionResultOuterClass.RogueActionResult.newInstance()
                .setSource(source)
                .setActionData(RogueActionResultDataOuterClass.RogueActionResultData.newInstance()
                    .setAddMiracleList(RogueMiracleDataOuterClass.RogueMiracleData.newInstance()
                        .setRogueMiracle(miracle))));

        this.setData(data);
    }
}
