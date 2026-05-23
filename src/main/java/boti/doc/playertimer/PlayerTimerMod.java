package boti.doc.playertimer;

import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.commands.Commands;

/*
 * Commands:
 * /playertimer startcountup
 * /playertimer startcountdown [duration]
 * /playertimer pause
 * /playertimer resume
 * /playertimer stop
 * /playertimer reset
 * /playertimer hide
 * /playertimer show
*/

public class PlayerTimerMod implements ModInitializer {

    private int ticksUntilTimerUpdate = 20;
    private final PlayerTimerService timerService = new PlayerTimerService();

    @Override
    public void onInitialize() {
        registerCommands();
        registerTimerTick();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(Commands.literal("playertimer")
                        .then(Commands.literal("startcountup")
                                .executes(ctx -> timerService.startCountup(ctx.getSource()))
                        )

                        .then(Commands.literal("startcountdown")
                                .executes(ctx -> timerService.startCountdown(ctx.getSource(), 300))

                                .then(Commands.argument("duration", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String duration = StringArgumentType.getString(ctx, "duration");
                                            return timerService.executeStartCountdown(ctx, duration);
                                        })
                                )
                        )

                        .then(Commands.literal("pause")
                                .executes(ctx -> timerService.pauseTimer(ctx.getSource()))
                        )

                        .then(Commands.literal("resume")
                                .executes(ctx -> timerService.resumeTimer(ctx.getSource()))
                        )

                        .then(Commands.literal("stop")
                                .executes(ctx -> timerService.stopTimer(ctx.getSource()))
                        )

                        .then(Commands.literal("reset")
                                .executes(ctx -> timerService.resetTimer(ctx.getSource()))
                        )

                        .then(Commands.literal("hide")
                                .executes(ctx -> timerService.hideTimer(ctx.getSource()))
                        )

                        .then(Commands.literal("show")
                                .executes(ctx -> timerService.showTimer(ctx.getSource()))
                        )
                )
        );
    }

    private void registerTimerTick() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ticksUntilTimerUpdate--;

            if (ticksUntilTimerUpdate > 0) {
                return;
            }

            ticksUntilTimerUpdate = 20;

            timerService.tickAllPlayers(server);
        });
    }

}
