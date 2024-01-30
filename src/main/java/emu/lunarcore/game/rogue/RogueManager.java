package emu.lunarcore.game.rogue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.RogueTalentExcel;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.*;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.proto.RogueAeonInfoOuterClass.RogueAeonInfo;
import emu.lunarcore.proto.RogueInfoDataOuterClass.RogueInfoData;
import emu.lunarcore.proto.RogueInfoOuterClass.RogueInfo;
import emu.lunarcore.proto.RogueScoreRewardInfoOuterClass.RogueScoreRewardInfo;
import emu.lunarcore.proto.RogueSeasonInfoOuterClass.RogueSeasonInfo;
import emu.lunarcore.proto.RogueTalentInfoOuterClass.RogueTalentInfo;
import emu.lunarcore.proto.RogueTalentOuterClass.RogueTalent;
import emu.lunarcore.proto.RogueTalentStatusOuterClass.RogueTalentStatus;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.send.*;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import us.hebi.quickbuf.RepeatedInt;

@Getter
public class RogueManager extends BasePlayerManager {
    private IntSet talents;

    public RogueManager(Player player) {
        super(player);
        this.talents = new IntOpenHashSet();
    }

    public boolean hasTalent(int talentId) {
        return this.getTalents().contains(talentId);
    }

    public boolean enableTalent(int talentId) {
        // Sanity check so we dont enable the same talent
        if (this.getTalents().contains(talentId)) {
            return false;
        }

        // Get talent excel
        RogueTalentExcel excel = GameData.getRogueTalentExcelMap().get(talentId);
        if (excel == null) return false;

        // Verify items
        if (!getPlayer().getInventory().verifyItems(excel.getCost())) {
            return false;
        }

        // Pay items
        getPlayer().getInventory().removeItemsByParams(excel.getCost());

        // Add talent
        RogueTalentData talent = new RogueTalentData(getPlayer(), excel.getTalentID());
        talent.save();

        return getTalents().add(talentId);
    }

    public void startRogue(int areaId, int aeonId, RepeatedInt avatarIdList) {
        // Make sure player already isnt in a rogue instance
        if (getPlayer().getRogueInstance() != null) {
            getPlayer().sendPacket(new PacketStartRogueScRsp());
            return;
        }

        // Get excel
        var rogueAreaExcel = GameData.getRogueAreaExcelMap().get(areaId);
        if (rogueAreaExcel == null || !rogueAreaExcel.isValid()) {
            getPlayer().sendPacket(new PacketStartRogueScRsp());
            return;
        }

        var aeonExcel = GameData.getRogueAeonExcelMap().get(aeonId);

        // Replace lineup
        getPlayer().getLineupManager().replaceLineup(0, ExtraLineupType.LINEUP_ROGUE_VALUE, Arrays.stream(avatarIdList.array()).boxed().toList());
        // Get lineup
        PlayerLineup lineup = getPlayer().getLineupManager().getLineupByIndex(0, ExtraLineupType.LINEUP_ROGUE_VALUE);
        // Make sure this lineup has avatars set
        if (lineup.getAvatars().size() == 0) {
            getPlayer().sendPacket(new PacketStartRogueScRsp());
            return;
        }

        // Get entrance id
        RogueInstance instance = new RogueInstance(getPlayer(), rogueAreaExcel, aeonExcel);
        getPlayer().setRogueInstance(instance);

        // Set starting SP
        boolean extraSP = this.hasTalent(32);

        // Reset hp/sp
        lineup.forEachAvatar(avatar -> {
            avatar.setCurrentHp(lineup, 10000);
            avatar.setCurrentSp(lineup, extraSP ? avatar.getMaxSp() : avatar.getMaxSp() / 2);

            instance.getBaseAvatarIds().add(avatar.getAvatarId());
        });
        lineup.setMp(5); // Set technique points

        // Set first lineup before we enter scenes
        getPlayer().getLineupManager().setCurrentExtraLineup(ExtraLineupType.LINEUP_ROGUE, false);

        getPlayer().sendPacket(new PacketSyncRogueVirtualItemInfoScNotify(getPlayer(), true));
        getPlayer().sendPacket(new PacketSyncRogueStatusScNotify(RogueStatusOuterClass.RogueStatus.ROGUE_STATUS_DOING));
        // Enter rogue
        RogueRoomData room = instance.enterRoom(instance.getStartSiteId());
        if (room == null) {
            // Reset lineup/instance if entering scene failed
            getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
            getPlayer().setRogueInstance(null);
            // Send error packet
            getPlayer().sendPacket(new PacketStartRogueScRsp());
            return;
        }

        LunarCore.getLogger().info("Player {} started rogue instance {}", getPlayer().getName(), instance.getExcel().getRogueAreaID());
        getPlayer().sendPacket(new PacketSyncRogueCommonPendingActionScNotify(RogueBonusSelectInfoOuterClass.RogueBonusSelectInfo.newInstance()
            .addBonusInfo(5)
            .addBonusInfo(4)
            .addBonusInfo(6), getPlayer()));
        getPlayer().sendPacket(new PacketCommonRogueUpdateScNotify(areaId));
        // Done
        getPlayer().sendPacket(new PacketStartRogueScRsp(getPlayer()));
    }

    public void leaveRogue() {
        if (getPlayer().getRogueInstance() == null) {
            getPlayer().getSession().send(CmdId.LeaveRogueScRsp);
            return;
        }

        // Clear rogue instance
        getPlayer().setRogueInstance(null);

        // Leave scene
        getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
        getPlayer().enterScene(GameConstants.ROGUE_ENTRANCE, 0, false); // Make sure we dont send an enter scene packet here

        // Send packet
        getPlayer().sendPacket(new PacketLeaveRogueScRsp(this.getPlayer()));
    }

    public void quitRogue() {
        if (getPlayer().getRogueInstance() == null) {
            getPlayer().getSession().send(CmdId.QuitRogueScRsp);
            return;
        }

        getPlayer().getRogueInstance().onFinish();

        getPlayer().getSession().send(new PacketQuitRogueScRsp());
        getPlayer().getSession().send(new PacketSyncRogueFinishScNotify(getPlayer()));

        // This isnt correct behavior, but it does the job
        this.leaveRogue();
    }

    public RogueInfo toProto() {
        var schedule = GameDepot.getCurrentRogueSchedule();

        int seasonId = 78;

        Calendar cal = PacketGetRogueScoreRewardInfoScRsp.getCalendar();
        var beginTime = cal.getTime().getTime() / 1000L;
        var endTime = beginTime + 7 * 24 * 60 * 60;

        if (schedule != null) {
            seasonId = schedule.getRogueSeason();
        }

        var score = RogueScoreRewardInfo.newInstance()
                .setPoolId(20 + getPlayer().getWorldLevel()) // TODO pool ids should not change when world level changes
                .setBeginTime(beginTime)
                .setEndTime(endTime)
                .setPoolRefreshed(true)
                .setHasTakenInitialScore(true);

        var season = RogueSeasonInfo.newInstance()
                .setBeginTime(beginTime)
                .setSeasonId(seasonId)
                .setEndTime(endTime);

        var data = RogueInfoData.newInstance()
                .setRogueScoreInfo(score)
                .setCKJPPDKJIOH(JHAHJFNNGHIOuterClass.JHAHJFNNGHI.newInstance()
                    .addAllMazeUnlocked(1,2,3,4,5,6,7,8,9)
                    .setCGAFFPHCNEA(true)
                    .setJPEBPGIEGPO(3)
                    .setOIJBOCHMDKA(9))
                .setEEFGNNFCDNJ(DMBBFODODOFOuterClass.DMBBFODODOF.newInstance().setBLPICCBCKPK(4))
                .setRogueSeasonInfo(season);
        List<Integer> list = getIntegers();
        List<Integer> list2 = new ArrayList<>();
        list2.add(10100);
        list2.add(10101);
        list2.add(10102);
        list2.add(10103);
        list2.add(10104);

        OONJMMOMFHLOuterClass.OONJMMOMFHL mm = OONJMMOMFHLOuterClass.OONJMMOMFHL.newInstance();
        for (int x: list) {
            mm.addAMEONEDBMDL(KDHBAHABJMPOuterClass.KDHBAHABJMP.newInstance()
                .setCMJFDLMBIOK(true)
                .setFNNEECKAFKK(2)
                .setLIHAFMAOKMG(x));
        }
        for (int x: list2) {
            mm.addAMEONEDBMDL(KDHBAHABJMPOuterClass.KDHBAHABJMP.newInstance()
                .setFNNEECKAFKK(2)
                .setLIHAFMAOKMG(x));
        }
        data.setOKAHBMNGJEB(mm);

        var proto = RogueInfo.newInstance()
            .setRogueInfoData(data);

//                .setRogueScoreInfo(score)
//                .setRogueData(data)
//                .setRogueVirtualItemInfo(getPlayer().toRogueVirtualItemsProto())
//                .setSeasonId(seasonId)
//                .setBeginTime(beginTime)
//                .setEndTime(endTime);

        // Path resonance
        var aeonInfo = RogueAeonInfo.newInstance();

//        if (this.hasTalent(1)) {
//            aeonInfo = RogueAeonInfo.newInstance()
//                    .setUnlockAeonNum(GameData.getRogueAeonExcelMap().size());
//
//            for (var aeonExcel : GameData.getRogueAeonExcelMap().values()) {
//                aeonInfo.addAeonIdList(aeonExcel.getAeonID());
//            }
//
//            proto.setRogueAeonInfo(aeonInfo);
//        }

        // Rogue data
        RogueInstance instance = this.getPlayer().getRogueInstance();
        if (instance != null) {
            proto.setRogueCurrentInfo(instance.toProto());
        }

//        // Add areas
//        if (schedule != null) {
//            for (int i = 0; i < schedule.getRogueAreaIDList().length; i++) {
//                var excel = GameData.getRogueAreaExcelMap().get(schedule.getRogueAreaIDList()[i]);
//                if (excel == null) continue;
//
//                var area = RogueArea.newInstance()
//                        .setAreaId(excel.getRogueAreaID())
//                        .setRogueAreaStatus(RogueAreaStatus.ROGUE_AREA_STATUS_FIRST_PASS);
//
//                if (instance != null && excel == instance.getExcel()) {
//                    area.setMapId(instance.getExcel().getMapId());
//                    area.setCurReachRoomNum(instance.getCurrentRoomProgress());
//                    area.setRogueStatus(instance.getStatus());
//                }
//
//                proto.addRogueAreaList(area);
//            }
//        }
        return proto;
    }

    @NotNull
    private static List<Integer> getIntegers() {
        List<Integer> list = new ArrayList<>();
        list.add(100);
        list.add(110);
        list.add(120);
        list.add(130);
        list.add(131);
        list.add(132);
        list.add(133);
        list.add(134);
        list.add(140);
        list.add(141);
        list.add(142);
        list.add(143);
        list.add(144);
        list.add(150);
        list.add(151);
        list.add(152);
        list.add(153);
        list.add(160);
        list.add(161);
        list.add(162);
        list.add(163);
        list.add(170);
        list.add(171);
        list.add(172);
        list.add(173);
        list.add(180);
        list.add(181);
        list.add(182);
        list.add(183);
        return list;
    }

    public RogueTalentInfo toTalentInfoProto() {
        var proto = RogueTalentInfo.newInstance();

        for (RogueTalentExcel excel : GameData.getRogueTalentExcelMap().values()) {
            var talent = RogueTalent.newInstance()
                    .setTalentId(excel.getTalentID());

            if (this.hasTalent(excel.getTalentID())) {
                talent.setStatus(RogueTalentStatus.ROGUE_TALENT_STATUS_ENABLE);
            } else {
                talent.setStatus(RogueTalentStatus.ROGUE_TALENT_STATUS_UNLOCK);
            }

            proto.addRogueTalent(talent);
        }

        return proto;
    }

    // Database

    public void loadFromDatabase() {
        // Load talent data
        var stream = LunarCore.getGameDatabase().getObjects(RogueTalentData.class, "ownerUid", this.getPlayer().getUid());

        stream.forEach(talent -> {
            this.getTalents().add(talent.getTalentId());
        });
    }
}
