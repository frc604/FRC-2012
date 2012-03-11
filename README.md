 FIRST Team 604's 2012 Robot "Orange"
====================================

### What is this branch?

This branch houses the development of the actual control structure for the 2012 robot. The stuff in the "java" branch is diluted with things for testing and such.

### What, in the name of all that is good and holy, is a "StrangeMachine"?

A StrangeMachine manages the state for a particular component of the robot. They can be used in conjunction with one another to manage state changes involving multiple components, eg, the elevator and pickup positions.

A StrangeMachine has multiple "states", which represent the possible conditions the represented components can be in. You can `test` to see if a Machine is in a particular state, and `crank` a Machine toward a particular state; both operations will return whether or not the Machine is currently in the specified state.

### Okay, but what's a "RotationProvider"?

Given a PIDController that controls the turret rotation, a RotationProvider updates the setpoint of that controller, based on external feedback. This updating is done in the `update` method, which is called once per iteration in the main thread. While the PIDController is not, by default, enabled, this updating continues regardless.

Currently, the control structure works as follows:

 - If the turret is in the `HIGH` position, then check if the user is pressing the `AIM_AND_FIRE` button.
 - If so, make sure the PID controller is enabled and check if we're on target; if not, make sure the PID controller is disabled.
 - If the `AIM_AND_FIRE` button is pressed, and we're on target, then go ahead and fire.

There are two currently-implemented RotationProviders, mostly for testing purposes:

 - **DummyRotationProvider:** Sets the controller to a setpoint of 0.
 - **NaiveRotationProvider:** A naive implementation that takes a CameraInterface and a Gyro360 as inputs.

### You mentioned a "CameraInterface". What's that again?

A CameraInterface is an abstraction of a method the robot can use to obtain vision data. Currently, in this branch, the only one is RemoteCameraTCP, which obtains data over a TCP connection. The goal is to have another one, too, for on-board processing, in a worst-case senario. There's an out-of-date one in the vision-testing branch that does just that, but it needs some revitalizing and getting-up-to-speed. Something to take care of later.

### I have the strange compulsion to ask what a "SpringableVictor" is.

Really? How odd...

A SpringableVictor works, in most ways, like a Victor. The only difference is the addition of a `reload` method. If the SpringableVictor receives input, either through a PIDController or manually, it will put itself in the "sprung" state. When the `reload` method is called -- which it is for each SpringableVictor at the end of the main control loop -- one of the following will happen:

 - **if the SpringableVictor is sprung:** un-spring it.
 - **if the SpringableVictor is not sprung:** set the output to 0.

 This way, if nothing writes to the SpringableVictor over the course of a loop iteration, it will automatically switch itself off. This removes this burden from the main control logic, making things much, much simpler in implemenation.

### Who is responsible for this madness?!

Blame [Michael](mailto:mdsmtp@gmail.com).
