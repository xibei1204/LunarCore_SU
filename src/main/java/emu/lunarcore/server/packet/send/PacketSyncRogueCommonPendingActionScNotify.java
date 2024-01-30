package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.*;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueCommonPendingActionScNotify extends BasePacket {
    public PacketSyncRogueCommonPendingActionScNotify(RogueCommonBuffSelectInfoOuterClass.RogueCommonBuffSelectInfo rogueBuffSelectInfo, Player player) {
        super(CmdId.SyncRogueCommonPendingActionScNotify);

        var data = SyncRogueCommonPendingActionScNotifyOuterClass.SyncRogueCommonPendingActionScNotify.newInstance();

        data.setOCPBNBPAMEN(101);  // TODO: shouldnt be hardcoded
        data.setRogueCommonPendingAction(RogueCommonPendingActionOuterClass.RogueCommonPendingAction.newInstance()
            .setGLFDHAJPJDF(player.getRogueInstance().getTimes())
            .setRogueAction(RogueActionOuterClass.RogueAction.newInstance()
                .setBuffSelectInfo(rogueBuffSelectInfo)));
        player.getRogueInstance().times += 3;

        this.setData(data);
    }

    public PacketSyncRogueCommonPendingActionScNotify(RogueMiracleSelectInfoOuterClass.RogueMiracleSelectInfo rogueMiracleInfo, Player player) {
        super(CmdId.SyncRogueCommonPendingActionScNotify);

        var data = SyncRogueCommonPendingActionScNotifyOuterClass.SyncRogueCommonPendingActionScNotify.newInstance();


        data.setOCPBNBPAMEN(101);  // TODO: shouldnt be hardcoded
        data.setRogueCommonPendingAction(RogueCommonPendingActionOuterClass.RogueCommonPendingAction.newInstance()
            .setGLFDHAJPJDF(player.getRogueInstance().getTimes())
            .setRogueAction(RogueActionOuterClass.RogueAction.newInstance()
                .setMiracleSelectInfo(rogueMiracleInfo)));
        player.getRogueInstance().times += 3;

        this.setData(data);
    }

    public PacketSyncRogueCommonPendingActionScNotify(RogueBonusSelectInfoOuterClass.RogueBonusSelectInfo rogueBonusSelectInfo, Player player) {
        super(CmdId.SyncRogueCommonPendingActionScNotify);

        var data = SyncRogueCommonPendingActionScNotifyOuterClass.SyncRogueCommonPendingActionScNotify.newInstance();


        data.setOCPBNBPAMEN(101);  // TODO: shouldnt be hardcoded
        data.setRogueCommonPendingAction(RogueCommonPendingActionOuterClass.RogueCommonPendingAction.newInstance()
            .setGLFDHAJPJDF(player.getRogueInstance().times)
            .setRogueAction(RogueActionOuterClass.RogueAction.newInstance()
                .setBonusSelectInfo(rogueBonusSelectInfo)));
        player.getRogueInstance().times += 3;

        this.setData(data);
    }

    public PacketSyncRogueCommonPendingActionScNotify(RogueCommonPendingActionOuterClass.RogueCommonPendingAction rogueCommonPendingAction) {
        super(CmdId.SyncRogueCommonPendingActionScNotify);

        var data = SyncRogueCommonPendingActionScNotifyOuterClass.SyncRogueCommonPendingActionScNotify.newInstance();

        data.setOCPBNBPAMEN(101);  // TODO: shouldnt be hardcoded
        data.setRogueCommonPendingAction(rogueCommonPendingAction);

        this.setData(data);
    }


}
