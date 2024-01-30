package emu.lunarcore.server.packet.send;

import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketQuitRogueScRsp extends BasePacket {
    public PacketQuitRogueScRsp() {
        super(CmdId.QuitRogueScRsp);
    }
}
