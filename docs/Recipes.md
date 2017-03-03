# Basics


<div class="yadrol-code" markdown="1">

<span class="yadrol-comment"># Dungeons &amp; Dragons</span>
<br>
[**d**20](http://localhost:8080/yadrol-web?run=true&expression-string=d20)

<span class="yadrol-comment"># Basic Role Playing</span>
<br>
[**d**100](http://localhost:8080/yadrol-web?run=true&expression-string=d100)

<span class="yadrol-comment"># Seldom used, unfortunately</span>
<br>
[**d**8](http://localhost:8080/yadrol-web?run=true&expression-string=d8)

<span class="yadrol-comment"># d13 only exist in hyperdimensional spaces, which is fine for Yadrol</span>
<br>
[**d**13](http://localhost:8080/yadrol-web?run=true&expression-string=d13)

<span class="yadrol-comment"># Barbarians of Lemuria</span>
<br>
[**2d6**](http://localhost:8080/yadrol-web?run=true&expression-string=2d6)

</div>

# Roll and keep

*Roll and keep* mechanics can be simulated with the `highest` and
`lowest` constructs:

<div class="yadrol-code" markdown="1">

<span class="yadrol-comment"># Roll two ten-sided dice, keep best</span>
<br>
[**highest of** 2**d**10](http://localhost:8080/yadrol-web?run=true&expression-string=highest%20of%202d10)

<span class="yadrol-comment"># Roll two ten-sided dice, keep lowest</span>
<br>
[**lowest of** 2**d**10](http://localhost:8080/yadrol-web?run=true&expression-string=lowest%20of%202d10)

<span class="yadrol-comment"># Roll three dice, keep the two highest</span>
<br>
[**highest** 2 **of** 3**d**6](http://localhost:8080/yadrol-web?run=true&expression-string=highest%202%20of%203d6)

<span class="yadrol-comment"># Roll three dice, keep the two lowest</span>
<br>
[**lowest** 2 **of** 3**d**6](http://localhost:8080/yadrol-web?run=true&expression-string=lowest%202%20of%203d6)

</div>

# Count hits in a dice pool

*Count hits* is a type of dice mechanic where one rolls a pool of dice,
then counts how many dice shows a result equal or higher than a target
value. In Yadrol, this can be specified as:


<div class="yadrol-code" markdown="1">

<span class="yadrol-comment"># World of Darkness</span>
<br>
[**count (for** 6**d**10 **if >=** 7**)**](http://localhost:8080/yadrol-web?run=true&expression-string=count%20%28for%206d10%20if%20>%3D%207%29)

</div>

This expression rolls six ten-sided dice and counts how many of them yield seven or more.
Let's break down this expression and see how it works:
* As you already know `6d10` rolls six ten-sided dice.
* The `for`-`if` construct that encloses the dice roll tells Yadrol to apply a condition *for each* dice. The result of the `for`-`if` is the set of dice that satisfy the condition. In our expression the condition is `>= 7`, the dice is seven or more.
* Finally the <code>count</code> keyword tells Yadrol to count the selected dice.

Now let's count 1s in a pool of six-sided dice:

<div class="yadrol-code" markdown="1">

[**count (for** 4**d**6 **if ==** 1**)**](http://localhost:8080/yadrol-web?run=true&expression-string=count%20%28for%204d6%20if%20%3D%3D%201%29)

</div>




# Exploding die

An exploding die is a die that one rolls again when its result is the highest possible.
You specify rerolls to Yadrol with the `repeat` construct:

<div class="yadrol-code" markdown="1">

[**repeat d**6 **if ==** 6](http://localhost:8080/yadrol-web?run=true&expression-string=repeat%20d6%20if%20%3D%3D%206)

</div>

This expression tells Yadrol to roll a six-sided die and repeat the roll *if* the result is 6.
If the result of the first die is 6, then the final result of the roll is the sum of both dice.

In some games, you keep rerolling while the die shows 6, this is sometimes known as a *wild die*.
You can specify it to Yadrol by using the `while` operator instead of `if`:

<div class="yadrol-code" markdown="1">

[**repeat d**6 **while ==** 6](http://localhost:8080/yadrol-web?run=true&expression-string=repeat%20d6%20while%20%3D%3D%206)

</div>

# Reroll 1s

We will use the very same <code>while</code>-<code>if</code> construct to specify another roll:

<div class="yadrol-code" markdown="1">

[**last of (repeat d**12 **if ==** 1)](http://localhost:8080/yadrol-web?run=true&expression-string=last%20of%20%28repeat%20d12%20if%20%3D%3D%201%29)

</div>

As we have seen above, the `repeat` part will roll a twelve-sided die and roll it again if the result is 1.
The `last of` operator tells that we are only interested in the last die rolled.
This expression means << _roll a d12, reroll 1s_ >>.

# Fudge dice

Fudge dice are custom six-sided dice used in games like [Fudge](XXX) and [FATE](XXX).
Two sides are marked with a plus sign, two other sides with a minus sign, and the two remaining sides are blank.
The player usually rolls four dice, count the plus signs and substracts the minus signs.

One way to specify this to Yadrol is:

<div class="yadrol-code" markdown="1">

[4**d[-**1**, -**1**,** 0**,** 0**,** 1**,** 1**]**](http://localhost:8080/yadrol-web?run=true&expression-string=4d%5B-1%2C%20-1%2C%200%2C%200%2C%201%2C%201%5D)

</div>

In Yadrol terms, `[-1, -1, 0, 0, 1, 1]` denotes a collection of values called a *list*.
We have filled this array with two `-1` that represent the sides with a minus sign, two `1` that represent the sides with a plus sign, and two `0` that represent blank sides.
For Yadrol, rolling an array die means randomly picking one element in the array, that is the why the above expression simulates the roll of four Fudge dice.

It has not escaped to the statistics-minded people that a Fudge die could be simulated with a shorter array with the same effect.
Try:

<div class="yadrol-code" markdown="1">

[4**d[-**1**,** 0**,** 1**]**](http://localhost:8080/yadrol-web?run=true&expression-string=4d%5B-1%2C%200%2C%201%5D)

</div>


# Barbarians of Lemuria combat turn

## Let's start

In the excellent Sword & Sorcery game [Barbarians of Lemuria](XXX) (BoL), players play sword-to-hire barbarian in search of fortune.
In the general mechanics of BoL, players roll `2d6` and must beat `9` to succeed.
During a combat turn, the character's blow is resolved this way.
If the attack succeeds, then the opponent loses Hit Points depending on the weapon.

In this section we will simulate an attack and plot the damage inflicted.
We will assume the character is using a sword which makes `d6 + 1` damage:

<div class="yadrol-code" markdown="1">

[**if** 2**d**6 **>=** 9 **then d**6 **+** 1 **else** 0](http://localhost:8080/yadrol-web?run=true&expression-string=if%202d6%20>%3D%209%20then%20d6%20%2B%201%20else%200)

</div>

We introduced here the `if`-`then`-`else` construct.
Inside the `if` part, Yadrol rolls `2d6` and tests either the result is `9` or more.
If the condition is satisfied (the attack is a success), then Yadrol evaluates the `then` part that simulates the damage of a sword: `d6 + 1`.
If the condition is not satisfied (the attack is a failure), then Yadrol evaluates the `else` part that simulates no damage at all: `0`.

Now BoL is a bit more versatile.
Players also benefit from a bonus on their roll depending on their character's _Melee_ ability.
They also suffer a penalty from their opponent's _Defense_ ability.
The target number is still `9`, so assuming a _Melee_ of `2` and an opponent's _Defense_ of `1`:

<div class="yadrol-code" markdown="1">

[**if** 2**d**6 **+** 2 **-** 1 **>=** 9 **then d**6 **+** 1 **else** 0](http://localhost:8080/yadrol-web?run=true&expression-string=if%202d6%20%2B%202%20-%201%20>%3D%209%20then%20d6%20%2B%201%20else%200)

</div>

This is nice but if we need to simulate attacks for characters and opponents we have to change the values in the expression.
Let's write this in another way that makes it easier:

<div class="yadrol-code" markdown="1">

[Melee **=** 2**;**<br>
Defense **=** 1**;**<br>
**if** 2**d**6 **+** Melee **-** Defense **>=** 9 **then d**6 **+** 1 **else** 0](http://localhost:8080/yadrol-web?run=true&expression-string=Melee%20%3D%202;%0ADefense%20%3D%201;%0Aif%202d6%20+%20Melee%20-%20Defense%20>%3D%209%20then%20d6%20%2B%201%20else%200)

</div>

There are several new notions here.
First we specified several expressions separated with semicolons (`;`).
Yadrol evaluates each one in sequence in the same order.
Then, in the two first expressions, values are assigned to variables with the equal (`=`) sign.
Variable names are at your discretion as long as they do not match Yadrol language reserved words, like `highest`, `lowest`, `for`, `repeat`, `if`, `then`, `else`, etc.
Keep in mind that the single letter dee (`d`) is a reserved word.
The expression on the right of the equal sign is evaluated, and the result is stored in the variable.
In this case the expressions are simple constants.

With this version of the attack simulation, we just have to change the values of the variables.
But wait, there's more.

## Now with functions

