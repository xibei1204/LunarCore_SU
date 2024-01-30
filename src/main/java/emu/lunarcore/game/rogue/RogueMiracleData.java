package emu.lunarcore.game.rogue;

import emu.lunarcore.proto.RogueMiracleDataInfoOuterClass;
import emu.lunarcore.proto.RogueMiracleInfoDataOuterClass;
import emu.lunarcore.proto.RogueMiracleOuterClass;
import emu.lunarcore.proto.RogueMiracleOuterClass.RogueMiracle;
import lombok.Getter;

@Getter
public class RogueMiracleData {
    private int id;
    private boolean active;

    public RogueMiracleData(int miracleId) {
        this.id = miracleId;
        this.active = true;
    }

    public RogueMiracleDataInfoOuterClass.RogueMiracleDataInfo toProto() {
        var proto = RogueMiracleDataInfoOuterClass.RogueMiracleDataInfo.newInstance()
            .setMiracleId(this.getId());

        return proto;
    }
    public RogueMiracleOuterClass.RogueMiracle toMiracleProto() {
        var proto = RogueMiracleOuterClass.RogueMiracle.newInstance()
            .setMiracleId(this.getId());

        return proto;
    }
}
