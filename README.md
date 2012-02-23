FIRST Team 604's 2012 Robot "Orange"
====================================

### What is this branch?!

This branch houses the development of the actual control structure for the 2012 robot. The stuff in the "java" branch is diluted with things for testing and such.

### What, in the name of all that is good and holy, is a "StrangeMachine"?!

A StrangeMachine manages the state for a particular component of the robot. They can be used in conjunction with one another to manage state changes involving multiple components, eg, the elevator and pickup positions.

A StrangeMachine has multiple "states", which represent the possible conditions the represented components can be in. You can *test* to see if a Machine is in a particular state, and *crank* a Machine toward a particular state; both operations will return whether or not the Machine is currently in the specified state.

### Who is responsible for this madness?!

Blame [Michael](mailto:mdsmtp@gmail.com).
