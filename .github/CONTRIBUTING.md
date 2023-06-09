Contributing to Embulk
=======================

Please read the following guideline before making a Pull Request. We'd love your conscientious contribution!


EEP before Pull Request
------------------------

Making a change in the Embulk SPI would always have a risk of breaking compatibility.

Follow our [EEP process](https://github.com/embulk/embulk/blob/master/docs/eeps/eep-0001.md) whenever you wanted to make a change in the Embulk SPI.


License
--------

By contributing your code and/or statements to the Embulk repository, you agree to license your contribution under the same license terms of the repository itself, the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).


Coding Style
-------------

Embulk's Java code follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) except for the following points:

* +4 spaces for block indentation.
* 120 characters as a columns limit.


Commit Messages
----------------

Commits to the Embulk repository should follow [Git's Commit Guidelines](https://git-scm.com/book/en/v2/Distributed-Git-Contributing-to-a-Project) with minor exceptions. Our guideline is summarized as follows:

* Start your commit message with a single line that describes the changeset concisely.
* Leave the second line blank, followed by a more detailed explanation from the third line.
* Have your motivation for the change, and contrast its implementation with previous behavior.
* Limit the length of each line: 100 characters are acceptable, but it's worth making it shorter.
* Use imperative present tense in your commit messages, especially in the first line.
