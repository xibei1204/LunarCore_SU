package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.send.PacketEnterRogueMapRoomScRsp;
import us.hebi.quickbuf.RepeatedInt;

import java.util.Arrays;

@Command(label = "rogue", aliases = {"rg"}, permission = "player.rogue", requireTarget = true, desc = "/rogue <join/leave/buff> - join/leave/buff the rogue event.")
public class RogueCommand implements CommandHandler {
    @Override
    public void execute(CommandArgs args) {
        var target = args.getTarget();

        if (target == null) {
            // Send help message
            return;
        }

        if (args.size() == 0) {
            // Send help message
            return;
        }

        switch (args.get(0)) {
            case "join":
                // Join the rogue event
                target.getRogueManager().startRogue(Integer.parseInt(args.get(1)), Integer.parseInt(args.get(2)),
                    RepeatedInt.newInstance(Arrays.stream(target.getLineupManager().getCurrentLineup().getAvatars().toArray()).mapToInt(i -> Integer.parseInt(i.toString())).toArray()));
                break;
            case "leave":
                // Leave the rogue event
                target.getRogueManager().quitRogue();
                break;
            case "buff":
                // Buff the rogue event
                if (args.size() == 2) {
                    target.getRogueInstance().createBuffSelect(Integer.parseInt(args.get(1)));
                    return;
                } else {
                    target.getRogueInstance().createBuffSelect(3);
                }
                break;
            case "buffid":
                // Buff the rogue event
                if (args.size() == 2) {
                    target.getRogueInstance().addBuff(Integer.parseInt(args.get(1)), 2);
                    return;
                }
                break;
            case "miracle":
                // Miracle the rogue event
                if (args.size() == 2) {
                    target.getRogueInstance().createMiracleSelect(Integer.parseInt(args.get(1)));
                } else {
                    target.getRogueInstance().createMiracleSelect(1);
                }
                break;
            case "miracleid":
                // Miracle the rogue event
                if (args.size() == 2) {
                    target.getRogueInstance().addMiracle(Integer.parseInt(args.get(1)));
                    return;
                }
                break;
            case "room":
                try {
                    var en = target.getRogueInstance().enterRoom(Integer.parseInt(args.get(1)));
                    if (en == null) {
                        // Send help message
                        return;
                    }
                    target.sendPacket(new PacketEnterRogueMapRoomScRsp(target, en));
                } catch (Exception e) {
                    // Send help message
                    return;
                }
                break;
            default:
                // Send help message
                break;
        }
    }
}
