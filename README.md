Omgrofl-interpteter
===================

Omgrofl interpreter written in Java

About
-----

This project aims to create a fully-functional interpreter of an esoteric
programming language called Omgrofl. More information on the language itself
can be found here: http://esolangs.org/wiki/Omgrofl

Suggestions or patches are more than welcome.

How it works
------------

The interpreter parses the input source code and transforms it into an abstract
syntax tree. It then traverses the tree and runs each instruction.

Getting started
---------------

To compile, run these commands on the command line:
```
git clone https://github.com/OlegSmelov/omgrofl-interpreter.git
cd omgrofl-interpreter
ant
```

Run an example:
```
cd dist
java -jar Omgrofl.jar ../examples/alphabet.omgrofl
```

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
* Indentation and whitespace don't matter. The only thing that matters is that
    every statement is written on a separate line.
* **If there is more than one statement on a line, an exception will be thrown.**
    This doesn't follow the language specification, but I believe this is a good
    decision, because a) honest mistakes will be found faster, and b) people new
    to the language will know if they've written anything wrong.

License
-------

MIT (see LICENSE)
