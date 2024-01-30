package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.*;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketHandleRogueCommonPendingActionScRsp;

@Opcodes(CmdId.HandleRogueCommonPendingActionCsReq)
public class HandlerHandleRogueCommonPendingActionCsReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var proto = HandleRogueCommonPendingActionCsReqOuterClass.HandleRogueCommonPendingActionCsReq.parseFrom(data);

        if (proto.hasRollBuff()) {
            // re-roll buff
            if (session.getPlayer().getRogueInstance().getBuffSelect() != null && session.getPlayer().getRogueInstance().getBuffSelect().hasRerolls()) {
                session.getPlayer().getRogueInstance().decreaseMoney(30);
                session.getPlayer().getRogueInstance().rogueAction = RogueActionOuterClass.RogueAction.newInstance()
                    .setBuffSelectInfo(session.getPlayer().getRogueInstance().rollBuffSelect().toProto());
                session.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(null,
                    null,
                    null,
                    RogueRerollBuffOuterClass.RogueRerollBuff.newInstance().setBuffSelectInfo(session.getPlayer().getRogueInstance().getBuffSelect().toProto()),
                    session.getPlayer()));
            }
        } else if (proto.hasBonusSelectResult()) {
            // TODO
            session.getPlayer().getRogueInstance().rogueAction = null;
            session.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(RogueBonusSelectOuterClass.RogueBonusSelect.newInstance(),
                null,
                null,
                null,
                session.getPlayer()));
            session.getPlayer().getRogueInstance().createBuffSelect(1);
        } else if (proto.hasMiracleSelectResult()) {
            session.getPlayer().getRogueInstance().rogueAction = null;
            session.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(null,
                RogueMiracleSelectOuterClass.RogueMiracleSelect.newInstance(),
                null,
                null,
                session.getPlayer()));
            session.getPlayer().getRogueInstance().selectMiracle(proto.getMiracleSelectResult().getMiracleId());
        } else if (proto.hasBuffSelectResult()) {
            session.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(null,
                null,
                RogueBuffSelectOuterClass.RogueBuffSelect.newInstance(),
                null,
                session.getPlayer()));
            session.getPlayer().getRogueInstance().selectBuff(proto.getBuffSelectResult().getBuffId());
        }
    }
}
