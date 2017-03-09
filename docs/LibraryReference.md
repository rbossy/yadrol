{% capture url %}http://yadrol-phatonin.rhcloud.com/?run=true&expression-string={% endcapture %}

Library Reference
=================

<div class="include-toc">
</div>

## std

The *std* automatically imported, theere is no need to import it.

### Exploding dice

<div class="yadrol-code" markdown="1">

<span class="yadrol-comment"># Exploding 10-sided die</span>
<br>
[**d**X10]({{url}}dX10)

<span class="yadrol-comment"># Two exploding 10-sided die</span>
<br>
[2**d**X10]({{url}}2dX10)

</div>

The `std` library exports the following exploding dice: `X4`, `X6`, `X8`, `X10`, `X12`, `X20`.
These dice explode only once.

### Unlimited exploding dice

<div class="yadrol-code" markdown="1">

<span class="yadrol-comment"># Unlimited exploding 6-sided die</span>
<br>
[**d**XX6]({{url}}dXX6)

<span class="yadrol-comment"># Two unlimited exploding 6-sided die</span>
<br>
[2**d**XX6]({{url}}2dXX6)

</div>

The `std` library exports the following unlimited exploding dice: `XX4`, `XX6`, `XX8`, `XX10`, `XX12`, `XX20`.
These dice continue exploding while they show the highest value.

### Imploding dice

<div class="yadrol-code" markdown="1">

<span class="yadrol-comment"># Imploding 8-sided die</span>
<br>
[**d**I8]({{url}}dI8)

<span class="yadrol-comment"># Two imploding 8-sided die</span>
<br>
[2**d**I8]({{url}}2dI8)

</div>

The `std` library exports the following imploding dice: `I4`, `I6`, `I8`, `I10`, `I12`, `I20`.
These dice explode or implode only once.

## Barbarians of Lemuria

<div class="yadrol-code" markdown="1">

**import** BoL

</div>

### Action resolution

`BoL` exports a function named `Resolve` with the following signature:

<div class="yadrol-code" markdown="1">

Resolve**(**Ability**,** Diff**:** 0**)**

</div>

`Ability` is the character's ability including career bonuses, `Diff` the difficulty of the action (*Moderate* by default).
The function returns a boolean that indicates either the action is successful, according to *Barbarians of Lemuria* rules.

### Difficulty and Range

The variables `Difficulty` and `Range` define standard difficulty and range ratings respectively:

| Difficulty | Range |
|:-----------|:------|
| `Difficulty.Easy` | `Range.PointBlank` |
| `Difficulty.Moderate` | `Range.Close` |
| `Difficulty.Hard` | `Range.Medium` |
| `Difficulty.Formidable` | `Range.Long` |
| `Difficulty.Mighty` | `Range.Distant` |
| `Difficulty.Thongorean` | `Range.Extreme` |

The entries of `Difficulty` and `Range` can be used directly with `Resolve`:

<div class="yadrol-code" markdown="1">

[Resolve**(**2, Difficulty**.**Hard**)**]({{url}}import%20BoL%0A---%0AResolve(2,%20Difficulty.Hard))

</div>

### Attack

The `Attack` function resolves an attack from acharacter to their target.
It starts resolving the strike, then, if successful, applies damage.
The returned value is the amount of damage.

<div class="yadrol-code" markdown="1">

Attack**(**Melee**,** Defense**,** Damage**:** Weapon**.**Fist**,** Diff**:** Difficulty**.**Moderate**)**

</div>

`Melee` and `Defenseè` are the aptitude of the striking character and their target respectively.
`Damage` is a function that represents the damage of the weapon carried by the character.
`Diff` is the base difficulty.

### Weapons

The `Weapons` variable is a map containing *Barbarians of Lemuria* standard weapons.

| Weapon |
|:-------|
| `Weapon.Fist` |
| `Weapon.Dagger` |
| `Weapon.Sword` |
| `Weapon.Axe` |
| `Weapon.Club` |
| `Weapon.Mace` |
| `Weapon.Spear` |
| `Weapon.Flail` |
| `Weapon.ValkarthanSword` |
| `Weapon.GreatAxe` |
| `Weapon.Staff` |
| `Weapon.Sling` |
| `Weapon.ShortBow` |
| `Weapon.Bow` |
| `Weapon.Crossbow` |
| `Weapon.Warbow` |

The entries of `Weapon` can be used directly with `Attack`:

<div class="yadrol-code" markdown="1">

[Attack**(**2**,** 1**,** Weapon**.**Sword**)**]({{url}}import%20BoL%0A---%0AAttack(2,%201,%20Weapon.Sword))

[Attack**(**2**,** 1**,** Weapon**.**Bow, Range**.**Long**)**]({{url}}import%20BoL%0A---%0AAttack(2,%201,%20Weapon.Bow,%20Range.Long))

</div>
