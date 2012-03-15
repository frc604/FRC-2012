Explanation of Unique Concepts
==============================

### StrangeMachine

A StrangeMachine manages the state for a particular component of the robot. They can be used in conjunction with one another to manage state changes involving multiple components, eg, the elevator and pickup positions.

The name "StrangeMachine" originates from the concept of a state machine, used to manage states and state transfers. StrangeMachine is our idea and implementation of how such a thing should work.

A StrangeMachine has multiple "states", which represent the possible conditions the represented components can be in. You can `test` to see if a Machine is in a particular state, and `crank` a Machine toward a particular state; both operations will return whether or not the Machine is currently in the specified state.

In the 2012 robot, we use these to control the **elevator**, the **pickup**, the **shooter and hopper**, and the **turret**.

### RotationProvider

Given a PIDController that controls the turret rotation, a RotationProvider updates the setpoint of that controller, based on external feedback. This updating is done in the `update` method, which is called once per iteration in the main thread. While the PIDController is not, by default, enabled, this updating continues regardless.

Currently, the control structure works as follows:

 - If the turret is in the `HIGH` position, then check if the user is pressing the `AIM_AND_FIRE` button.
 - If so, make sure the PID controller is enabled, and call `update` on the RotationProvider.
 - Once we're aimed, then go ahead and fire.

There are a few RotationProviders currently implemented. The one we've pretty much settled on is the `SlowbroRotationProvider`.

### CameraInterface

A CameraInterface is an abstraction of a method the robot can use to obtain vision data. Currently, the only one is RemoteCameraTCP, which obtains data over a TCP connection, sent from processing software running on the Driver Station.

### Springables

Springables work, in most ways, like regular actuator controller classes. The main difference is the addition of a `reload` method. If the Springable receives input, either through a PIDController or manually, it will put itself in the "sprung" state. When the `reload` method is called -- which it is for each Springable at the end of the main control loop -- one of the following will happen:

 - **if the Springable is sprung:** un-spring it.
 - **if the Springable is not sprung:** set the output to the default output.

This way, if nothing writes to the Springable over the course of a loop iteration, it will automatically switch itself off. This removes this burden from the main control logic, making things much, much simpler in implemenation.

Currently-implemented Springables are `SpringableVictor`, `SpringableDoubleSolenoid`, and `SpringableRelay`. `DualVictor` is also Springable. Victor controllers have an additional `setController` method, which takes a `PIDController`, and skips the reloading when it is enabled, to avoid conflicts in that respect.

### XboxController

We use Xbox 360 controllers to control our robot. There is no standard implementation of this in WPILib, however; usually, you're stuck trying to map the Xbox button numbering to inputs on the Joystick class, looking up what number each button is, flipping back and forth between pages of documentation... Oh, and the triggers are not buttons, but a separate axis.

To ease our development process, the XboxController class was created. It contains enumerations of all axes and buttons, so one can do, for example:

    XboxController controller = new XboxController(1); // 1 = the USB port of the controller
    
    if (controller.getButton(Buttons.A)) {
      // ... control logic here ...
    }
    
    if (controller.getButton(Buttons.EitherTrigger)) {
      // ... control logic here ...
    }

   robotDrive.tankDrive(controller.getAxis(Axis.LEFT_STICK_Y), controller.getAxis(Axis.RIGHT_STICK_Y));

All buttons and axes are implemented. Additionally, there is the `getToggle` convenience method, which only triggers once per button-state-change, eg, pressing it down or letting it go.

### DualVictor

A DualVictor is a class that wraps around two sub-Victors. You can easily control two motors as one, and pass these classes into PIDControllers as an output.

Control Structure Overview
==========================

 - `robotInit()`: This method is called to initialize all sensors, actuators, controllers, StrangeMachines, etc.
 - `autonomous()`: This method contains all our autonomous code. The code is run in a loop, which is interrupted if autonomous mode somehow ends before everything is done. A state counter is used to keep track of control flow.
 - `operatorControl()`: This method contains a control loop which is used to operate the robot during the Teleoperated period of the match. This includes driving, balancing assist, turret, elevator, and pickup control, aiming and shooting, balancing assist, and other functions of the robot. Heavy use is made of StrangeMachines to manage transfers between various states of the pickup, turret, and elevator. For example, the elevator cannot go down fully while the pickup is up; conversely, the pickup cannot go up until the elevator is a safe distance away. The turret must be stowed in the sideways position before the elevator can go down, and the turret cannot be facing forward at the same time that the pickup is down, lest we risk violating rule `<G21>`, regarding multiple appendages extending beyond the frame parameter at the same time.

### Philosophy: Modularity and Reusability

The robot control code is designed with the principles of modularity and reusability in mind. Wherever possible, code that is not specific to the robot is kept apart from that which is, in a separate package of utility classes, which are continually added to as additional functionality is required. For example, rather than processing raw controller inputs from the Joystick interface into those corresponding to actions on the Xbox controller, an Xbox-specific class was implemented, which does all this for us, and added to the utility classes. Instead of manually updating two motors with the exact same value, in cases where two motors control one component, like the elevator, a DualVictor class was written to handle this automatically. Rather than making sure each and every motor was reset to an output of zero in all code paths that did not update its value, the idea of a SpringableVictor was conceived, which automatically did this if no value was sent in one iteration of the control loop, reducing code complexity and time-to-implementation exponentially. From this, an entire class of Springables was born, from the SpringableDoubleSolenoid to the SpringableRelay, with new ones being added as needed.

As well as increasing maintainability, this strategy produces a sizeable repository of reusable code, which can then be drawn upon in later years. Instead of implementing the same thing over and over again, we can simply find a utility class that does what we need, or write a new one if one does not already exist, further growing our pool of pre-written code to draw from, and allowing us to focus more on the particulars of each season's challenge.

### Collaboration and Documentation

On a robotics team such as ours -- with 75 members and counting -- a single individual will not be the only one who has to look at, and work with, their code. Additionally, multiple programmers must collaborate effectively, and be able to work on different components simultaneously. As a result, we use a source control system called *git* to track multiple versions and branches of our codebase, and intelligently merge in concurrent modifications by different members of the team. Our source repositories are hosted remotely on GitHub, a company which specializes in hosting for teams using the git system. GitHub is free for open source projects, but paid for private repositories. However, this year, the company generously donated a free "bronze" subscription to our team, granting us ten private repositories for our general use.

This, then, solves the problem of *collaboration*. But it does not matter that someone can easily get your code, if then cannot understand what it does. To remedy this, we meticulously document our codebase through the standardized *Javadoc* system. Such documentation can be done inline, in the same files as our code, removing the need for switching back-and-forth between applications during development. At the same time, from our source files, a complete reference can be generated, detailing the proper operation of each class, method, and so on. For the 2012 season, this generated documentation, if printed out, totals around 250 pages.

### Aiming

Vision processing on the cRIO is *extremely slow*. To avoid this, the actual processing is performed on the driver station computer, and the results are streamed back to the robot in the form of a TCP connection. The robot uses this data to aim and fire.

See section "The Vision Code" further on for a more complete explanation of this.

### Encoders and PID

We make heavy use of encoders to track the positions of various components of the robot, such as the elevator and the turret. To, for example, point the turret at a specific angle, or move the elevator to a specific height, PID, which, given a current position and a target position, will output a power value to get you to said target position, is employed.

PID accepts three coefficients as parameters: the *proportional*, the *integral*, and the *derivative*. Collectively, these are referred to as "gains". For the elevator, different gains are required for upward and downward movement. To make switching between them as efficient as possible, we have our own implementation of an `UpDownPIDController` as a utility class.

For the turret, the PID receives input based on encoder "clicks", but receives a setpoint in degrees, representing an angle to face. A simple conversion factor can be applied to convert between the two; rather than putting this in every place we need to set the setpoint, however, there is a reuseable `ConvertingPIDController` class, which does this automatically.
