Basics {#_basics}
======

[`d20`](http://localhost:8080/yadrol-web?run=true&expression-string=d20)

[`d100`](http://localhost:8080/yadrol-web?run=true&expression-string=d100)

[`d8`](http://localhost:8080/yadrol-web?run=true&expression-string=)

[`d13`](http://localhost:8080/yadrol-web?run=true&expression-string=d13)

[`2d6`](http://localhost:8080/yadrol-web?run=true&expression-string=2d6)

Roll and keep {#_roll_and_keep}
=============

*Roll and keep* mechanics can be simulated with the `highest` and
`lowest` constructs:

[`highest of 2d10`](http://localhost:8080/yadrol-web?run=true&expression-string=highest%20of%202d10)

[`lowest of 2d10`](http://localhost:8080/yadrol-web?run=true&expression-string=lowest%20of%202d10)

[`highest 2 of 3d6`](http://localhost:8080/yadrol-web?run=true&expression-string=highest%202%20of%203d6)

[`lowest 2 of 3d6`](http://localhost:8080/yadrol-web?run=true&expression-string=lowest%202%20of%203d6)

Count hits in a dice pool {#_count_hits_in_a_dice_pool}
=========================

*Count hits* is a type of dice mechanic where one rolls a pool of dice,
then counts how many dice shows a result equal or higher than a target
value. In Yadrol, this can be specified as:

[`count (for 6d10 if >= 7)`](http://localhost:8080/yadrol-web?run=true&expression-string=count%20(for%206d10%20if%20>=%207))
