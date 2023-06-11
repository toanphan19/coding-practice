from dataclasses import dataclass


@dataclass
class Stats:
    atk: int
    crit_rate: float
    crit_dmg: float
    element_dmg_boost: float
    break_effect: float


@dataclass
class Rotation:
    basic: int
    skill: int
    ult: int
    other: int


@dataclass
class Character:
    basic_dmg: float
    skill_dmg: float
    ult_dmg: float
    other_dmg: float

    stats: Stats

    def dmg_per_rotation(self, rotation: Rotation) -> int:
        base_dmg = self.stats.atk * self.multiplier_per_rotation(rotation)

        crit_multiplier = 1 + self.stats.crit_rate / 100 * self.stats.crit_dmg / 100

        final_dmg = (
            base_dmg * crit_multiplier * (1 + self.stats.element_dmg_boost / 100)
        )
        return int(final_dmg)

    def multiplier_per_rotation(self, rotation: Rotation) -> float:
        return (
            self.basic_dmg * rotation.basic
            + self.skill_dmg * rotation.skill
            + self.ult_dmg * rotation.ult
            + self.other_dmg * rotation.other
        )


class SilverWolf(Character):
    def __init__(self, stats: Stats):
        super().__init__(0.8, 1.96, 3.8, 0, stats)


def break_dmg(multiplier: float, break_effect: float) -> int:
    base_break_dmg = 3767  # character level 80
    return int(base_break_dmg * multiplier * (1 + break_effect / 100))


char = Character(
    0.8,
    1.96,
    3.8,
    0,
    Stats(
        atk=2500,
        crit_rate=50,
        crit_dmg=100,
        element_dmg_boost=53,
        break_effect=0,
    ),
)

if __name__ == "__main__":
    print("Silver Wolf")
    rotation = Rotation(2, 1, 1, 0)
    # rotation = Rotation(0, 3, 1, 0)
    print(rotation)

    # builds = [
    #     Stats(
    #         atk=2000,
    #         crit_rate=50,
    #         crit_dmg=100,
    #         element_dmg_boost=45,
    #         break_effect=96,
    #     ),
    #     Stats(
    #         atk=2500,
    #         crit_rate=50,
    #         crit_dmg=100,
    #         element_dmg_boost=55,
    #         break_effect=0,
    #     ),
    # ]

    # Compare 1 crit-DPS with with Tingyun buffs vs 1 crit-DPS + 1 break-DPS
    # builds = [
    #     Stats(
    #         atk=3000 + 600,
    #         crit_rate=70,
    #         crit_dmg=120,
    #         element_dmg_boost=55 + 30 + 40,
    #         break_effect=0,
    #     ),
    #     Stats(
    #         atk=3000,
    #         crit_rate=70,
    #         crit_dmg=120,
    #         element_dmg_boost=55,
    #         break_effect=0,
    #     ),
    #     Stats(
    #         atk=2700,
    #         crit_rate=10,
    #         crit_dmg=60,
    #         element_dmg_boost=45,
    #         break_effect=180,
    #     ),
    # ]

    builds = [
        # Base
        Stats(
            atk=2697,
            crit_rate=40,
            crit_dmg=90,
            element_dmg_boost=8,
            break_effect=140,
        ),
        # With ATK rope
        Stats(
            atk=2697 + 480,
            crit_rate=40 + 6,
            crit_dmg=90,
            element_dmg_boost=8,
            break_effect=140 + 10,
        ),
        # With Quantum rope (bad substats)
        Stats(
            atk=2697,
            crit_rate=40,
            crit_dmg=90,
            element_dmg_boost=8 + 38.8,
            break_effect=140,
        ),
        # With Break set, ATK rope
        Stats(
            atk=2697 + 480 - 280,
            crit_rate=40,
            crit_dmg=90,
            element_dmg_boost=8,
            break_effect=140 + 36,
        ),
    ]

    for stats in builds:
        char = SilverWolf(stats)

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
