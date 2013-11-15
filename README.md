Omgrofl-interpteter
===================

Omgrofl interpreter with JIT support

About
-----

This project aims to create a fully-functional interpreter of an esoteric
programming language called Omgrofl. More information on the language itself
can be found here: http://esolangs.org/wiki/Omgrofl

Suggestions or patches are more than welcome.

How it works
------------

The interpreter parses the input source code and transforms it into an abstract
syntax tree and then executes it. The syntax tree can be interpreted directly or
JIT compilation into JVM bytecode can be used.

Currently supported statements
------------------------------

+ Assignment (*variable* iz *variable/value*; *variable* to /dev/null)
+ Char output (rofl *variable*)
+ Char input (stfw *variable*)
+ Infinite loop (rtfm ... brb)
+ Loop break (tldr)
+ Comments (w00t)
+ Sleep (afk *variable/value*)
+ Program exit (stfu)
+ Incrementing / decrementing (lmao/roflmao *variable*)
+ Conditions (wtf *variable/value* iz (nope) liek/uber *variable/value* ... brb)
+ Stack/Queue (n00b/l33t/haxor *variable*)

Implementation details
----------------------

* Unassigned variables are treated as nonexistent. If you try to use a variable
    without assigning a value to it first, an exception will be thrown.
* An exception will also be thrown if you try to take a value off the stack when
    the stack is empty.
* On EOF, 255 is returned as the value.
* JIT may or may not be faster than interpreting Omgrofl directly. It will
  definitely be faster if the program is long and resource-intensive.

License
-------

MIT (see LICENSE)
