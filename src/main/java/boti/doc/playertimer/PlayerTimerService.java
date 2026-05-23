package boti.doc.playertimer;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class PlayerTimerService {

    private final Map<UUID, PlayerTimer> timers = new HashMap<>();

    public void tickAllPlayers(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID id = player.getUUID();
            PlayerTimer timer = timers.get(id);

            if (timer == null) {
                continue;
            }

            if (timer.getState() != TimerState.RUNNING) {
                if (timer.isVisible()) {
                    sendActionBar(player, "Time: " + formatTime(timer.getTime()));
                }
                continue;
            }

            if (timer.getMode() == TimerMode.COUNTDOWN) {
                timer.setTime(timer.getTime() - 1);

                if (timer.getTime() <= 0) {
                    timer.setTime(0);
                    timer.setState(TimerState.FINISHED);
                    player.sendSystemMessage(Component.literal("Your time is up."));
                    player.level().playSound(
                            null,
                            player.blockPosition(),
                            SoundEvents.BELL_BLOCK,
                            SoundSource.BLOCKS,
                            1.0f,
                            0.7f
                    );
                }
            } else {
                timer.setTime(timer.getTime() + 1);
            }

            if (timer.isVisible()) {
                sendActionBar(player, "Time: " + formatTime(timer.getTime()));
            }
        }
    }

    public static void sendActionBar(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message), true);
    }

    public static String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static ServerPlayer getPlayerOrNotify(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            return player;
        }

        source.sendFailure(Component.literal("Only players can use this command."));
        return null;
    }

    public int executeStartCountdown(CommandContext<CommandSourceStack> ctx, String duration) {
        try {
            int seconds = TimeParser.parseToSeconds(duration);
            return startCountdown(ctx.getSource(), seconds);
        } catch (IllegalArgumentException e) {
            ctx.getSource().sendFailure(Component.literal(
                    "Invalid duration. Use seconds, mm:ss, hh:mm:ss, or formats like 1h0m10s."
            ));
            return Command.SINGLE_SUCCESS;
        }
    }

    public int startCountup(CommandSourceStack source) {
        ServerPlayer player = getPlayerOrNotify(source);
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }

        UUID id = player.getUUID();

        timers.putIfAbsent(id, new PlayerTimer(TimerMode.COUNTUP, TimerState.READY, false, 0));
        PlayerTimer timer = timers.get(id);

        if (timer.getState() == TimerState.RUNNING) {
            player.sendSystemMessage(Component.literal("Your timer is already running."));
        } else {
            timer.setMode(TimerMode.COUNTUP);
            timer.setTime(0);
            timer.setState(TimerState.RUNNING);
            timer.setVisible(true);
            player.sendSystemMessage(Component.literal("Your timer has been started."));
        }

        return Command.SINGLE_SUCCESS;
    }

    public int startCountdown(CommandSourceStack source, int seconds) {
        ServerPlayer player = getPlayerOrNotify(source);
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }

        UUID id = player.getUUID();

        timers.putIfAbsent(id, new PlayerTimer(TimerMode.COUNTDOWN, TimerState.READY, true, seconds));
        PlayerTimer timer = timers.get(id);

        if (timer.getState() == TimerState.RUNNING) {
            player.sendSystemMessage(Component.literal("Your timer is already running."));
        } else {
            timer.setMode(TimerMode.COUNTDOWN);
            timer.setTime(seconds);
            timer.setState(TimerState.RUNNING);
            timer.setVisible(true);
            player.sendSystemMessage(Component.literal("Your timer has been started."));
        }

        return Command.SINGLE_SUCCESS;
    }

    public int pauseTimer(CommandSourceStack source) {
        ServerPlayer player = getPlayerOrNotify(source);
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }

        PlayerTimer timer = timers.get(player.getUUID());

        if (timer == null) {
            player.sendSystemMessage(Component.literal("You do not have a timer."));
        } else if (timer.getState() == TimerState.RUNNING) {
            timer.setState(TimerState.PAUSED);
            player.sendSystemMessage(Component.literal("Your timer has been paused."));
        } else {
            player.sendSystemMessage(Component.literal("Your timer is not running."));
        }

        return Command.SINGLE_SUCCESS;
    }

    public int resumeTimer(CommandSourceStack source) {
        ServerPlayer player = getPlayerOrNotify(source);
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }

        PlayerTimer timer = timers.get(player.getUUID());

        if (timer == null) {
            player.sendSystemMessage(Component.literal("You do not have a timer."));
        } else if (timer.getState() != TimerState.PAUSED) {
            player.sendSystemMessage(Component.literal("You do not have a paused timer."));
        } else {
            timer.setState(TimerState.RUNNING);
            timer.setVisible(true);
            player.sendSystemMessage(Component.literal("Your timer has been resumed."));
        }

        return Command.SINGLE_SUCCESS;
    }

    public int stopTimer(CommandSourceStack source) {
        ServerPlayer player = getPlayerOrNotify(source);
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }

        PlayerTimer timer = timers.get(player.getUUID());

        if (timer == null) {
            player.sendSystemMessage(Component.literal("You do not have a timer."));
        } else if (timer.getState() == TimerState.RUNNING || timer.getState() == TimerState.PAUSED) {
            timer.setState(TimerState.STOPPED);
            timer.setTime(0);
            timer.setVisible(false);
            player.sendSystemMessage(Component.literal("Your timer has been stopped."));
        } else {
            player.sendSystemMessage(Component.literal("Your timer was not running."));
        }

        return Command.SINGLE_SUCCESS;
    }

    public int resetTimer(CommandSourceStack source) {
        ServerPlayer player = getPlayerOrNotify(source);
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }

        PlayerTimer timer = timers.get(player.getUUID());

        if (timer == null) {
            player.sendSystemMessage(Component.literal("You do not have a timer."));
        } else {
            timer.setState(TimerState.READY);
            timer.setTime(0);
            timer.setVisible(true);
            player.sendSystemMessage(Component.literal("Your timer has been reset."));
        }

        return Command.SINGLE_SUCCESS;
    }

    public int hideTimer(CommandSourceStack source) {
        ServerPlayer player = getPlayerOrNotify(source);
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }

        PlayerTimer timer = timers.get(player.getUUID());

        if (timer == null) {
            player.sendSystemMessage(Component.literal("You do not have a timer."));
            return Command.SINGLE_SUCCESS;
        }

        timer.setVisible(false);
        player.sendSystemMessage(Component.literal("Your timer is now hidden."));

        return Command.SINGLE_SUCCESS;
    }

    public int showTimer(CommandSourceStack source) {
        ServerPlayer player = getPlayerOrNotify(source);
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }

        PlayerTimer timer = timers.get(player.getUUID());

        if (timer == null) {
            player.sendSystemMessage(Component.literal("You do not have a timer."));
            return Command.SINGLE_SUCCESS;
        }

        timer.setVisible(true);
        player.sendSystemMessage(Component.literal("Your timer is now visible."));

        return Command.SINGLE_SUCCESS;
    }

}