package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetRogueScoreRewardInfoScRspOuterClass.GetRogueScoreRewardInfoScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

public class PacketGetRogueScoreRewardInfoScRsp extends BasePacket {

    public PacketGetRogueScoreRewardInfoScRsp(Player player) {
        super(CmdId.GetRogueScoreRewardInfoScRsp);

        var data = GetRogueScoreRewardInfoScRsp.newInstance();


        Calendar cal = getCalendar();
        var start = cal.getTime().getTime() / 1000L;
        var end = start + 7 * 24 * 60 * 60;
        data.getMutableScoreRewardInfo()
            .setPoolId(20 + player.getWorldLevel()) // TODO pool ids should not change when world level changes
            .setPoolRefreshed(true)
            .setBeginTime(start)
            .setEndTime(end)
            .setHasTakenInitialScore(false);

        this.setData(data);
    }

    public GetRogueScoreRewardInfoScRsp toProto() {
        return (GetRogueScoreRewardInfoScRsp) super.getData();
    }

    @NotNull
    public static Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal;
    }
}
