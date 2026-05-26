# PlayerTimer Fabric

A Fabric mod providing independent per-player timers for Minecraft servers and singleplayer worlds.

Each player has their own timer which can:
- count up
- count down
- be paused/resumed
- be hidden or shown independently
- be triggered by commands or command blocks

Designed for:
- minigames
- escape rooms
- timed challenges
- parkour
- speedrunning
- multiplayer events
- adventure maps

---

# Features

- Independent timer per player
- Countup and countdown modes
- Brigadier command support
- Pause and resume support
- Timer visibility control
- Countdown completion notification
- Sound effects on completion
- Multiple duration input formats
- Command block compatible using `/execute as`
- Built for Fabric
- Java 25 support

---

# Commands

| Command | Description |
|---|---|
| `/playertimer startcountup` | Start a white countup timer |
| `/playertimer startcountup <color>` | Start a coloured countup timer |
| `/playertimer startcountdown` | Start a 5 minute white countdown |
| `/playertimer startcountdown <duration>` | Start a white countdown with custom duration |
| `/playertimer startcountdown <duration> <color>` | Start a coloured countdown |
| `/playertimer pause` | Pause the timer |
| `/playertimer resume` | Resume a paused timer |
| `/playertimer stop` | Stop the timer and hide it |
| `/playertimer reset` | Reset timer to `00:00` |
| `/playertimer hide` | Hide the timer display |
| `/playertimer show` | Show the timer display |

---

# Duration Formats

The countdown command accepts multiple input formats.

## 1. Seconds

```text
/playertimer startcountdown 300
```

Starts a 300 second countdown.

---

## 2. mm:ss

```text
/playertimer startcountdown 05:30
```

Starts a 5 minute 30 second countdown.

Rules:
- `mm` must be between `0` and `59`
- `ss` must be between `0` and `59`

Examples:

```text
05:30   ✓ valid
59:59   ✓ valid
60:00   ✗ invalid
05:60   ✗ invalid
99:99   ✗ invalid
```

---

## 3. hh:mm:ss

```text
/playertimer startcountdown 01:40:05
```

Starts a 1 hour 40 minute 5 second countdown.

Rules:
- `mm` must be between `0` and `59`
- `ss` must be between `0` and `59`

Examples:

```text
01:40:05   ✓ valid
10:59:59   ✓ valid
01:60:05   ✗ invalid
01:40:60   ✗ invalid
```

---

## 4. Text format

```text
/playertimer startcountdown 1h40m5s
/playertimer startcountdown 10m
/playertimer startcountdown 45s
/playertimer startcountdown 2h
/playertimer startcountdown 1h5s
```

Supports:
- hours (`h`)
- minutes (`m`)
- seconds (`s`)

Any combination may be used.

Rules:
- `m` must be between `0` and `59`
- `s` must be between `0` and `59`

Examples:

```text
1h40m5s   ✓ valid
10m30s    ✓ valid
59m59s    ✓ valid
1h60m     ✗ invalid
10m90s    ✗ invalid
10s49m    ✗ invalid
```

---

# Maximum Duration

The maximum allowed duration is:

```text
2147483647 seconds
```

which is approximately:

```text
68 years
```

This limit exists because the timer internally uses Java `int` values.

---

---

# Timer Colours

Timers may optionally be started with a colour argument.

## Supported colours

|Colour|
|---|
|black|
|dark_blue|
|dark_green|
|dark_aqua|
|dark_red|
|dark_purple|
|gold|
|gray|
|dark_gray|
|blue|
|green|
|aqua|
|red|
|light_purple|
|yellow|
|white|

Examples:

```text
/playertimer startcountup red
/playertimer startcountdown 5m gold
/playertimer startcountdown 01:30 dark_blue
```
---

# Timer States

Internally the mod tracks timer state using a finite state model.

| State | Meaning |
|---|---|
| `READY` | Timer exists but has not started |
| `RUNNING` | Timer is actively counting |
| `PAUSED` | Timer is temporarily halted |
| `STOPPED` | Timer was manually stopped |
| `FINISHED` | Countdown reached zero |

This prevents invalid operations such as:
- resuming finished timers
- starting multiple timers simultaneously
- resuming timers that were stopped

---

# Visibility Behaviour

Timer visibility is independent of timer state.

| Action | Result |
|---|---|
| Start timer | Timer becomes visible |
| Pause timer | Timer remains visible |
| Resume timer | Timer becomes visible |
| Stop timer | Timer becomes hidden |
| Reset timer | Timer remains visible |
| Hide command | Timer becomes hidden |
| Show command | Timer becomes visible |

---

# Countdown Completion

When a countdown reaches zero:

- the timer stops automatically
- the timer enters the `FINISHED` state
- the player receives a notification
- a bell sound is played

---

# Command Block Support

Commands can be triggered via command blocks using Minecraft's `execute` command.

Example:

```mcfunction
execute as @p run playertimer startcountdown 5m
```

This allows:
- buttons
- pressure plates
- redstone systems
- region triggers
- adventure map integration

---

# Building

## Requirements

- Java 25
- Gradle
- Fabric Loader
- Fabric API

---

## Build

```bash
./gradlew build
```

Compiled jars will appear in:

```text
build/libs/
```

---

# Versioning

PlayerTimer uses the following versioning scheme:

```text
[target-minecraft-version]-[mod-version]
```

Example:

```text
26.1.x-1.0
```

Meaning:
- Compatible with Minecraft 26.1.x / API 26.1.x
- Mod release version 1.0

---

# Changelog

## 26.1.x-1.1
- Added coloured timer support
- Added optional colour arguments to timer start commands

## 26.1.x-1.0

- Initial Fabric release
- Ported PlayerTimer from Paper to Fabric
- Added per-player timers
- Added countdown and countup modes
- Added timer state system
- Added visibility system
- Added Brigadier command support
- Added command block compatibility
- Added duration parsing with multiple formats
- Added action bar timer display
- Added countdown completion sounds
- Added Java 25 support
- Refactored timer logic into `PlayerTimerService`

---

# License

MIT License
# PlayerTimer-fabric
