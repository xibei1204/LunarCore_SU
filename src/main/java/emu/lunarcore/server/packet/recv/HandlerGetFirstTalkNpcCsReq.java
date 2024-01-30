package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetFirstTalkNpcCsReqOuterClass;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetFirstTalkNpcScRsp;

@Opcodes(CmdId.GetFirstTalkNpcCsReq)
public class HandlerGetFirstTalkNpcCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var proto = GetFirstTalkNpcCsReqOuterClass.GetFirstTalkNpcCsReq.parseFrom(data);
        session.send(new PacketGetFirstTalkNpcScRsp(proto.getNpcId()));
    }

}
