package emu.lunarcore.server.packet.recv;

import emu.lunarcore.LunarCore;
import emu.lunarcore.game.rogue.RogueRoomData;
import emu.lunarcore.proto.EnterRogueMapRoomCsReqOuterClass.EnterRogueMapRoomCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketEnterRogueMapRoomScRsp;

@Opcodes(CmdId.EnterRogueMapRoomCsReq)
public class HandlerEnterRogueMapRoomCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = EnterRogueMapRoomCsReq.parseFrom(data);

        RogueRoomData enteredRoom = null;
        if (session.getPlayer().getRogueInstance() != null) {
            LunarCore.getLogger().info("Player {} entered rogue room {}", session.getPlayer().getName(), req.getSiteId());
            enteredRoom = session.getPlayer().getRogueInstance().enterRoom(req.getSiteId());
        }

        if (enteredRoom != null) {
            LunarCore.getLogger().info("Player {} entered rogue room {}", session.getPlayer().getName(), req.getSiteId());
            session.send(new PacketEnterRogueMapRoomScRsp(session.getPlayer(), enteredRoom));
        } else {
            LunarCore.getLogger().info("Player {} tried to enter rogue room {} {} but failed", session.getPlayer().getName(), req.getRoomId(), req.getSiteId());
            session.send(CmdId.EnterRogueMapRoomScRsp);
        }
    }

}
