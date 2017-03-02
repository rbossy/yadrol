# Data types

## Scalar types

### Undef 

`undef` type has a single value: `undef`. This value usually means an
undefined value, or an uninitialized variable.

### Boolean 

`boolean` type has two values: `true` and `false`.

### Integer 

`integer` values are represented as 64-bit signed integers.

### String 

`string` are immutable sequence of Unicode characters.

## Container types 

### List 

Lists are ordered collections of elements. Each element is addressed by
its position in the list. Element positions are 1-based.

### Map 

Maps are collections of key-value pairs. Keys are unique strings within
a map.

The insert order of keys is preserved.

## Functions 

Functions are callable expressions within their own variable scope.
Functions are first-class values, they can be assigned and returned.

# Evaluation model 

## Variable scopes 

Expressions are evaluated within the context of a variable scope. A
variable scope is a mapping from variable names to values. A variable
scope may have a parent scope. When looking for a variable for value
retrieval or assignation, Yadrol will search in the current scope, then
its parent, etc. When assigning to a variable that has not been found,
Yadrol creates it in the current scope.

Some expressions spawn new variable scopes in order to evaluate
sub-expressions.

## Data conversion 

### To boolean 

#### From undef 

`undef` converts to `false`.

#### From integer 

`0` converts to `false`, all other values convert to `true`

#### From string 

The empty string converts to `false`, all other strings convert to
`true`.

#### From list 

The empty list converts to `false`, non-empty lists convert to `true`.

#### From map 

The empty map converts to `false`, non-empty maps convert to `true`.

#### From function 

If the function takes no argument, then its body is evaluated as a
boolean.

Other functions convert to `false`.

### To integer 

#### From undef 

`undef` converts to `0`.

#### From boolean 

`false` converts to `0`.

`true` converts to `1`.

#### From string 

Strings are converted to integer by parsing its contents assuming the
number is written in base 10.

If the contents cannot be parsed as an integer, the the string converts
to `0`.

#### From list 

A list converts into the sum of each of its elements. If an element is
not an integer, then it is converted to an integer.

The empty list converts to `0`

#### From map 

A map converts into the sum of each of its values. If a value is not an
integer, then it is converted to an integer. Map keys do not enter into
account in the conversion.

The empty map converts to `0`

#### From function 

If the function takes no argument, then its body is evaluated as an
integer.

Other functions convert to an undefined integer. Two identical functions
always convert to the same integer.

### To string 

#### From undef 

`undef` converts to the empty string.

#### From boolean 

`false` converts to the empty string.

`true` converts to `"true"`.

#### From integer 

An integer converts to a string that represents the number in base 10.

#### From list 

A list converts to the concatenation of each of its elements. If an
element is not a string, then it is converted to a string.

The empty list converts to the empty string.

#### From map 

A map converts to the concatenation of each of its values. If a value is
not a string, then it is converted to a string. Map keys do not enter
into account in the conversion.

The empty map converts to the empty string.

#### From function 

If the function takes no argument, then its body is evaluated as a
string.

Other functions convert to an undefined string. Two identical functions
always convert to the same string.

### To list 

#### From undef 

`undef` converts into an empy list.

#### From boolean, integer, or string 

A Scalar converts into a list with a single element.

#### From map 

A map converts into a list that contains all the values. The key insert
order is preserved in the list.

#### From function 

If the function takes no argument, then its body is evaluated as a list.

Other functions convert to singleton list as for scalars.

### To map 

#### From undef 

`undef` converts into an empy map.

#### From boolean, integer, or string 

A Scalar converts into a map with a single value, the key is `"_"`.

#### From list 

A list converts into a map that contains all the elements. The keys are
string representations of the element index.

#### From function 

If the function takes no argument, then its body is evaluated as a map.

Other functions convert to singleton map as for scalars.

### To function 

Non-function values convert into a function that takes no argument. The
body of this function evaluates to a value equal to the original.

# Language constructs 

This section details the Yadrol language constructs. Each construct
starts with the syntax of this construct in a block:

<div class="yadrol-code" markdown="1">
**reserved** *placeholder* [ optional ]
</div>

Reserved words, symbols and operators are denoted in **`bold`**.
Placeholders and variable parts of the construct are denoted in
*`italics`*. Optional parts are enclosed between square brackets (`[]`).

## Literals 

### Undef 

<div class="yadrol-code" markdown="1">
**undef**
</div>

### Boolean 

<div class="yadrol-code" markdown="1">
**false**
<br>
**true**
</div>

### Integer 

<div class="yadrol-code" markdown="1">
**[0-9]+**
</div>

### String 

<div class="yadrol-code" markdown="1">
**"** *...* **"**
</div>

#### Character escape 

Characters may be escaped with a backslash (`\`).

| Escape                               | Effect                               |
|:------------------------------------:|:-------------------------------------|
| `\n`                                 | newline                              |
| `\r`                                 | carriage return                      |
| `\t`                                 | horizontal tab                       |
| `\x`                                 | character *x*                        |


#### Expression interpolation 

A sequence of character enclosed by a pair of dollar signs (`$`) is
parsed as an expression and replaced by evaluating this expression as a
string.

## Identifier 

<div class="yadrol-code" markdown="1">
**[A-Z_a-z][0-9A-Z_a-z\]+**
<br>
**'** *...* **'**
</div>

Identifiers start with a letter or an undescore character and may be
followed by zero or several letters, digits, or underscore. Identifiers
that contain any other characters must be enclosed between single
quotes.

An identifier is an expression that evaluates to the value assigned to a
variable in the current scope or its parent.

## List constructor 

<div class="yadrol-code" markdown="1">
**[]**
<br>
**[** *item* **,** *...* **]**
</div>

Evaluate each *item* expression and return a list containing the
results.

## Map constructor 

<div class="yadrol-code" markdown="1">
**{}**
<br>
**{**
*name*
**:**
*item*
**,**
*...*
**}**
</div>

Evaluate each *item* expression and return a map containing the results,
the name of each item is specified by the *name* identifiers.

## Lambda 

<div class="yadrol-code" markdown="1">

**fun (**
[
*name*
**,**
*...*
[
*name*
**:**
*defval*
**,**
*...*
]
]
**) {**
*body*
**}**
</div>

Create a function where *name* are argument identifiers, and *body* is
the function body expression. Default values can be specified with
*defval* expressions. Default values are evaluated immediately in the
current scope. *body* is evaluated each time the function is called with
a fresh scope with the current scope as parent.

## Call 

<div class="yadrol-code" markdown="1">
*expr*
**(**
[
*pos*
**,**
*...*
[
*name*
**:**
*val*
**,**
*...*
]
]
**)**
</div>

Evaluate *expr* as a function and call it with *pos* expressions as
positional arguments, and *name* identifiers and *val* expressions as
named arguments.

## Subscript 

<div class="yadrol-code" markdown="1">

*container*
**[**
*index*
**]**

</div>

<div class="yadrol-code" markdown="1">

*map*
**.**
*name*

</div>

In the first form, *container* and *index* are evaluated. The returned
value depends on the type of *container* and of *index*:

If *container* is a list, and *index* an integer, then return the
element in the list at position *index*. The list position start at 1,
the first element has position 1. If *index* is zero or negative, or if
*index* is greater than the length of the list, then the subscript
expression raises an error.

If *container* is a map, and *index* a string, then return the element
in the map named *index*. If there is no element in the map named
*index*, then return `undef`.

If *index* is a list, then return a list with as many elements in
*index*. Each element is the subscript of *container* with the *index*
element.

If *index* is a map, then return a map with the same names as in
*index*. Each element is the subscript of *container* with the *index*
element.

If *index* is `undef`, a boolean, or a function, then the subscript
expression raises an error.

In the second form, *name* is an identifier. If *map* is a map, then
return the element named *name*, otherwise raise an error.

## Assignment 

<div class="yadrol-code" markdown="1">

*lvalue* = *rvalue*

</div>

Evaluate *rvalue* and assigns the result to the *lvalue* expression.

### To variable 

If *lvalue* is an identifier, then the value is stored in the specified
variable in the current scope.

### To subscript 

If *lvalue* is a subscript, then the value is stored in the container
specified by the subscript.

### To list constructor 

If *lvalue* is a list constructor, then *rvalue* is converted into a
list. Each value of this list is then assigned to the corresponding
element of *lvalue*.

### To map constructor 

If *lvalue* is a map constructor, then *rvalue* is converted into a map.
Each value of this map is then assigned to the element of *lvalue* with
the same name.

## Control structures 

### Grouping 

<div class="yadrol-code" markdown="1">

**(**
*expr*
**)**

</div>

Parentheses are used to override operator precedence.

### Sequence 

#### Soft sequence 

<div class="yadrol-code" markdown="1">

*expr*
**;**
*expr*
[
**;**
*...*
]

</div>

In the sequence, the expressions are evaluated in order. The value of
the sequence is the value of the last expression in the sequence, the
values of the preceding expressions are discarded.

#### Hard sequence 

<div class="yadrol-code" markdown="1">

*expr*
<br>
**\-\--**
<br>
*expr*
<br>
[
<br>
**\-\--**
<br>
*...*
<br>
]

</div>

Hard sequences are like soft sequences, each expression is evaluated one
after the other all in the same scope. The difference is that the Yadrol
application only records and outputs the result of the last expression.

### Conditional 

<div class="yadrol-code" markdown="1">

**if**
*condition*
**then**
*texpr*
**else**
*fexpr*

</div>

*condition* is evaluated as a boolean. If the result is `true`, then
*texpr* is evaluated, otherwise *fexpr* is evaluated.

### For loops 

<div class="yadrol-code" markdown="1">

[
*out*
]
**for**
[
[
*index*
**,**
]
*item*
**in**
]
*container*
[
**if**
*condition*
]

</div>

Evaluates *container* as a list or a map, then iterates through the
elements to produce a list or a map.

If *out* is specified, then it is evaluated for each element of
*container* and the values are stored in the result. If *out* is
omitted, then the result contains the elements of *container*.

If *condition* is specified, then it is evaluated as a boolean for each
element of *container*. An element is added to the result only if the
value is `true`.

*container* is evaluated in the current scope. *out* and *condition* are
evaluated in a fresh scope, with the current scope as parent. The
current *container* item is stored in the variable specified by the
*item* identifier. If *item* is omitted, then the current element is
stored in a variable named `_`.

If the *index* identifier is specified, then the index of the current
element is stored in this variable. When *container* is a map, then
*index* contains the name of the current element. If *index* is omitted,
then the index of the current element is not available.

If *condition* is a comparison between *item* and something, then the
left part of the comparison can be omitted.

Both *out* and *condition* are optional, but either one must be present.

### Repeat loops 

<div class="yadrol-code" markdown="1">

**repeat**
*expr*
**if**
*condition*
<br>
**repeat**
*expr*
**while**
*condition*
[
**limit**
*number*
]
<br>
**while**
*condition*
**repeat**
*expr*
[
**limit**
*number*
]

</div>

In the first form, evaluate *expr*, then evaluates *condition* as a
boolean. If the result of *condition* is `true`, then evaluate *expr*
again. The result is an array containing the first, and possibly the
second, result of the evaluation of *expr*.

In the second form, *expr* and *condition* are evaluated again until
*condition* evaluates to `false`. The result is a list with at least one
element containing the successive values of *expr*.

The third form resembles the second but *condition* is evaluated before
*expr*. The result is a list, possibly empty, containing the successive
values of *expr*.

*expr* and *condition* are evaluated in a fresh scope with the current
scope as parent. In the first and second forms the result of the last
evaluation of *expr* is accessible to *cond* through the variable named
`_`.

Repeat loops cand be limited with the `limit` clause. *limit* must be an
integer literal, it is not an evaluated expression.

If *condition* is a comparison between *item* and something, then the
left part of the comparison can be omitted.

## Boolean operators 

<div class="yadrol-code" markdown="1">

*expr*
**or**
*expr*
<br>
*expr*
**and**
*expr*
<br>
**not**
*expr*

</div>

Apply logical operations on expressions. `or` and `and` operators are
short-circuited. The `or` operator does not evaluate the right operand
if the left operand evaluates as `true`. The `and` operator does not
evaluate the right operand if the left operand evaluates as `false`.

## Comparison operators 

### Arithmetic comparison 

<div class="yadrol-code" markdown="1">

*left*
**==**
*right*
<br>
*left*
**!=**
*right*
<br>
*left*
**&lt;**
*right*
<br>
*left*
**&gt;**
*right*
<br>
*left*
**&lt;=**
*right*
<br>
*left*
**&gt;=**
*right*

</div>

Evaluate *left* and *right* as integers, then return `true` if the
comparison is verified, otherwise `false`.

### General comparison 

<div class="yadrol-code" markdown="1">

*left*
**===**
*right*
<br>
*left*
**!==**
*right*

</div>

Evaluate *left* and *right*, then return `true` if the comparison is
verified, otherwise `false`.

The `===` operator is verified if both operands are of the same type and
represent the same value. Lists are considered equal, if and only if
they are of the same length and each corresponding element is equal.
Maps are considered equal, if and only if they have the same set of
names and each corresponding element is equal. Functions are considered
equal, if and only if their argument sets are equal, and their bodies
are equal, and their parent scopes are the same.

## Arithmetic operators 

<div class="yadrol-code" markdown="1">

*expr*
**+**
*expr*
<br>
*expr*
**-**
*expr*
<br>
*expr*
**\***
*expr*
<br>
*expr*
**/**
*expr*
<br>
*expr*
**%**
*expr*

</div>

The left and right expressions are evaluated as integers, then the
arthmetic operator is applied.

| Operator                             | Operation                            |
|:------------------------------------:|:-------------------------------------|
| **`+`**                              | addition                             |
| **`-`**                              | substraction                         |
| **`*`**                              | multiplication                       |
| **`/`**                              | integer division                     |
| **`%`**                              | division remainder                   |

## Dice 

### Single die 

<div class="yadrol-code" markdown="1">

**d**
*type*

</div>

Evaluate *type* and return a die roll of the specified type.

The roll depends on the type of *type*:

| Type                                 | Roll                                 |
|:-------------------------------------|:-------------------------------------|
| **integer**                              | A random integer between 1 and *type*, inclusive                    |
| **list**                                 | A random element in the list, the list remains unchanged               |
| **map**                                  | A random entry in the map, the map remains unchanged, the result is a single-entry map                |
| **function**                             | Call the function with empty arguments                            |

#### Space after `d` 

The space between the `d` operator and the die type is unnecessary for
the following constructs: - an integer literal - a list or map
constructor - an expression between parentheses - an identifier that
starts with an uppercase letter

### Multiple dice 

<div class="yadrol-code" markdown="1">

*number*
**d**
*type*

</div>

Evaluate *number* as an integer and roll as many dice of type *type*,
then return a list of length *number* with all the results.

The roll procedure depends on *type* and follows the same rules as in
single die.

If *type* is a function, and this function accepts an argument named
`N`, then this function is called with *number* as argument. It is the
function responsibility to return a list of size *number*.

#### Space before `d` 

The space between *number* and the `d` operator is unnecessary for the
following constructs: - an integer literal - a list or map constructor -
an expression between parentheses - an identifier that ends with an
uppercase letter

## Draw 

<div class="yadrol-code" markdown="1">

**draw**
[
*number*
]
**from**
*expr*

</div>

Evaluate *expr* as a list, then randomly selects one or several
elements. The returned elements are removed from *expr*.

If *number* is omitted, then only one element is drawn and returned.

If *number* is specified, then a list is returned with the drawn
elements.

## Best of 

<div class="yadrol-code" markdown="1">

**highest**
[
*number*
]
**of**
*expr*
<br>
**lowest**
[
*number*
]
**of**
*expr*
<br>
**first**
[
*number*
]
**of**
*expr*
<br>
**last**
[
*number*
]
**of**
*expr*

</div>

Evaluate *expr* as a list, then selects one or several elements.

If *number* is omitted, then the result is the selected element in
*expr*. If *expr* is empty, then return `undef`.

If *number* is specified, the result is a list of size at most *number*
with the selected elements. The result list may be lower than *number*
if *expr* has not enough elements. The elements in the result are in the
same order than the original *expr* list.

## Append 

<div class="yadrol-code" markdown="1">

*head*
**&lt;&lt;**
*tail*

</div>

Evaluate *head* and *tail*. If *head* is a list, then convert *tail*
into a list and appends all its elements to the end of *head*.

If *head* is a map, then convert *tail* into a map and appends all its
entries to *head*. If an entry with the same name already exists in
*head* then it is updated with the value in *tail*.

The return value is the modified *head*.

## Range 

<div class="yadrol-code" markdown="1">

*start*
**..**
*end*

</div>

Evaluate *start* and *end* as integers, then return a list with all
values in the range between *start* and *end*. *start* and *end* are
included.

If *start* is lower than *end*, then the result contains all values in
ascending order.

If *start* is greater than *end*, then the result contains all values in
descending order.

If *start* equals *end*, then the result is a single element list.

## Count 

<div class="yadrol-code" markdown="1">

**count**
*expr*

</div>

Evaluate *expr*, then return its size. The size of a value depends on
its type:

| Type                                 | Size                             |
|:-------------------------------------|:---------------------------------|
| **`undef`**                              | `0`                                  |
| **integer, string, boolean**    | `1`                                  |
| **list**                                 | number of elements                   |
| **map**                                  | number of entries                    |

## List reorder 

<div class="yadrol-code" markdown="1">

**sorted**
*expr*
<br>
**reversed**
*expr*
<br>
**shuffled**
*expr*

</div>

Evaluate *expr* as a list, then returns a new list containing the same
elements in the specified order:

| Specification                    | Order                            |
|:---------------------------------|:---------------------------------|
| **`sorted`**                             | sorted in ascending order            |
| **`reversed`**                           | reverse order of the original list   |
| **`shuffled`**                           | random order                         |

## Conversion 

<div class="yadrol-code" markdown="1">

**string**
*expr*
<br>
**boolean**
*expr*
<br>
**integer**
*expr*
<br>
**list**
*expr*
<br>
**map**
*expr*

</div>

Evaluate *expr* forcing the evaluation type.

## Output 

<div class="yadrol-code" markdown="1">

**roll**
*expr*
[
*type*
]
[
**"**
*name*
**"**
]
<br>
**sample**
*expr*
[
*type*
]
[
**"**
*name*
**"**
]

</div>

Require to the Yadrol application the output of the evaluation of
*expr*. `roll` specifies a single evaluation output, `sample` specifies
a multiple evaluation with a distribution output.

*type* is one of `boolean`, `string`, `integer`, `list` or `map`. If
specified, then the application will coerce the evaluation type.

*name* is a string that is used by the application to display the
output. If omitted, then the application will generate one based on
*expr*.

## Import 

<div class="yadrol-code" markdown="1">

**import**
[
*obj*
**=**
]
*address*
[
**:**
*name*
*name*
*...*
]

</div>

Look for a stream of Yadrol expressions, and evaluates it.

*address* is an identifier that represents the location where Yadrol
will look for the stream.

The expression in the stream is evaluated in a fresh scope, then this
scope is merged in the current scope. Therefore the imported variables
will not be visible outside the current scope.

*name* are identifiers that specify the names in the import scope that
will be merged. If no name is specified, then all variables are visible
in the current scope.

*obj* is an identifier, if it is specified then the scope in which the
stream is evaluated is not merged into the current scope. Instead the
scope is stored as an object in the variable named *obj* in the current
scope. The imported names are accessible through subscript constructs.

### Addresses 

The command-line tool accept the following addresses:

-   standard library name, see XXX
-   path to a Yadrol file, relative paths will be searched in the input
    paths
-   URL that points to a Yadrol file

## Scopes 

<div class="yadrol-code" markdown="1">

**local**
<br>
**outer**
<br>
**global**

</div>

Return a variable scope as a map. Changes in the map are reflected on
the scope, and vice versa.

`local` returns the current variable scope. `outer` returns the parent
of the current variable scope. `global` returns the root variable scope.

## Native 

<div class="yadrol-code" markdown="1">

**native**
*class*

</div>

Evaluate an expression written in native Java. The fully qualified name
of the Java class is specified by the identifier *class*. This class
must implement `org.phatonin.yadrol.core.Expression`.

# Operator precedence 

| Operators                    | Associativity                         |
|:----------------------------:|:-------------------------------------:|
| `---`                            | yes                                     |
| `;`                              | yes                                     |
| `import` `roll` `sample`         | no                                      |
| `=`                              | no                                      |
| `if` `then` `else` `for` `in` `repeat` `while`    | no                                      |
| `or`                             | yes                                     |
| `and`                            | yes                                     |
| `not`                            | no                                      |
| `===` `!==` `==` `!=` `>` `<` `>=` `<=`    | no                                      |
| `in`                             | no                                      |
| `<<`                             | no                                      |
| `..`                             | no                                      |
| `+` `-`                          | yes                                     |
| `*` `/` `%`                    | yes                                     |
| `+` `-` <sup>1</sup>                      | no                                      |
| `highest` `lowest` `first` `last` `of`       | no                                      |
| `draw`                           | no                                      |
| `d`                              | no                                      |
| `count` `sorted` `reversed` `shuffled`      | no                                      |
| `string` `boolean` `integer` `list` `map`    | no                                        |
| `.` `[]`<sup>2</sup> `()`<sup>3</sup>              | yes                                     |

<sup>1</sup> Unary sign operators
<sup>2</sup> Subscript brackets
<sup>3</sup> Function call
