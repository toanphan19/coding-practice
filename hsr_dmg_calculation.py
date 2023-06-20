from dataclasses import dataclass
from typing import Callable


@dataclass
class Stats:
    atk: int
    crit_rate: float
    crit: float
    element_boost: float
    break_effect: float


@dataclass
class Rotation:
    basic: int
    skill: int
    ult: float  # float so that it's a bit easier to calculate
    followup: float
    dot: float
    other: float


@dataclass
class Multiplier:
    basic: float
    skill: float
    ult: float
    followup: float
    dot: float
    other: float


@dataclass
class Character:
    multiplier: Multiplier
    stats: Stats

    def dmg_per_rotation(self, rotation: Rotation) -> int:
        base_dmg = self.stats.atk * multiplier_per_rotation_without_dot(
            self.multiplier, rotation
        )
        crit_multiplier = 1 + self.stats.crit_rate / 100 * self.stats.crit / 100
        standard_dmg = base_dmg * (1 + self.stats.element_boost / 100) * crit_multiplier

        dot_dmg = (
            self.stats.atk
            * self.multiplier.dot
            * rotation.dot
            * (1 + self.stats.element_boost / 100)
        )

        final = standard_dmg + dot_dmg
        return int(final)


@dataclass
class SilverWolf(Character):
    multiplier = Multiplier(
        basic=1,
        skill=1.96,
        ult=3.8,
        followup=0,
        dot=0,
        other=0,
    )


@dataclass
class Kafka(Character):
    multiplier = Multiplier(
        basic=1,
        skill=1.6,
        ult=0.8,
        followup=1.4,
        dot=2.9,
        other=0,
    )

    multiplier_1_target = multiplier
    multiplier_3_targets = Multiplier(
        basic=1,
        skill=1.6 + 0.6 * 2,
        ult=0.8 * 3,
        followup=1.4,
        dot=2.9,
        other=0,
    )


def break_dmg(multiplier: float, break_effect: float) -> int:
    base_break = 3767  # character level 80
    return int(base_break * multiplier * (1 + break_effect / 100))


def multiplier_per_rotation(multiplier: Multiplier, rotation: Rotation) -> float:
    return (
        multiplier_per_rotation_without_dot(multiplier, rotation)
        + multiplier.dot * rotation.dot
    )


def multiplier_per_rotation_without_dot(
    multiplier: Multiplier, rotation: Rotation
) -> float:
    return (
        multiplier.basic * rotation.basic
        + multiplier.skill * rotation.skill
        + multiplier.ult * rotation.ult
        + multiplier.followup * rotation.followup
        + multiplier.other * rotation.other
    )


def multiplier_breakdown(
    multiplier: Multiplier, rotation: Rotation
) -> dict[str, float]:
    total = multiplier_per_rotation(multiplier, rotation)
    return {
        "basic": multiplier.basic * rotation.basic / total,
        "skill": multiplier.skill * rotation.skill / total,
        "ult": multiplier.ult * rotation.ult / total,
        "followup": multiplier.followup * rotation.followup / total,
        "dot": multiplier.dot * rotation.dot / total,
        "other": multiplier.other * rotation.other / total,
    }


def compare_builds(multiplier: Multiplier, builds: list[Stats], rotation: Rotation):
    print(rotation)
    for stats in builds:
        char = Character(multiplier, stats)

        print("")
        print(char.stats)
        print(
            "DMG per rotation:",
            char.dmg_per_rotation(rotation),
        )
        print("Break DMG:", break_dmg(10.25, char.stats.break_effect))
        print(
            "Total DMG:",
            char.dmg_per_rotation(rotation) + break_dmg(10.25, char.stats.break_effect),
        )


#
# Test characters
#
def test_silver_wolf():
    print("Silver Wolf")
    rotation = Rotation(2, 1, 1, 0, 0, 0)
    # rotation = Rotation(0, 3, 1, 0)

    builds = [
        # Base
        Stats(
            atk=2697,
            crit_rate=40,
            crit=90,
            element_boost=8,
            break_effect=140,
        ),
        # With ATK rope
        Stats(
            atk=2697 + 480,
            crit_rate=40 + 6,
            crit=90,
            element_boost=8,
            break_effect=140 + 10,
        ),
        # With Quantum rope (bad substats)
        Stats(
            atk=2697,
            crit_rate=40,
            crit=90,
            element_boost=8 + 38.8,
            break_effect=140,
        ),
        # With Break set, ATK rope
        Stats(
            atk=2697 + 480 - 280,
            crit_rate=40,
            crit=90,
            element_boost=8,
            break_effect=140 + 36,
        ),
    ]
    compare_builds(SilverWolf.multiplier, builds, rotation)


def test_kafka():
    print("Kafka")
    # DOT is 5 ticks + 1 tick from ult + 4 tick from skill
    rotation = Rotation(0, 4, 1, 4, 5 + 1 + 0.75 * 4, 0)
    print(multiplier_breakdown(Kafka.multiplier, rotation))

    builds = [
        # Base
        Stats(
            atk=2800,
            crit_rate=5,
            crit=50,
            element_boost=72,
            break_effect=0,
        ),
        # Pure ATK
        Stats(
            atk=2800 + int(1100 * (0.432 + 24 * 0.034)),
            crit_rate=5,
            crit=50,
            element_boost=72,
            break_effect=0,
        ),
        # Crit
        Stats(
            atk=2800,
            crit_rate=5 + 32.4 + 12 * 2.5,
            crit=50 + 12 * 5.1,
            element_boost=72,
            break_effect=0,
        ),
    ]
    compare_builds(Kafka.multiplier, builds, rotation)


if __name__ == "__main__":
    # test_silver_wolf()
    test_kafka()
