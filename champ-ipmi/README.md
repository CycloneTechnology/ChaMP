[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-ipmi_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-ipmi_2.12)

## Intelligent Platform Management Interface (IPMI)

### Features
* Implements many of the standard commands from the IPMI specification.
* Implements many additional commands providing facilities similar to those of the popular
*ipmitool* command-line interface. In particular this provides:
   * Commands to provide Field-Replaceable Unit (FRU) information.
   * Commands to query Sensor Data Record (SDR) information.
   * Commands to retrieve filtered sensor values.
* Uses RMCP+ to securely connect using UDP to remote baseboard management controllers (BMCs) over the LAN.
* Supports strong RAKP+ security.
* Fully non-blocking IO.
* Supports IPMI versions 1.5 and 2.0.

### Command-based API
The main Scala API is the ```Ipmi``` trait. This has a number of methods that allow commands to be executed:
* ```executeCommand``` methods allow executing standard IPMI commands. These implement the ```IpmiStandardCommand``` trait.
* ```executeToolCommand``` methods allow executing so-called tool commands. These implement the ```IpmiToolCommand``` trait.

#### Basic Usage
```scala
  // Have implicit actor system and timeout
  implicit val actorSystem: ActorSystem = ActorSystem("exampleActorSystem")
  implicit val timeoutContext: TimeoutContext = TimeoutContext.default

  val ipmi = Ipmi.create
  
  // Create a command 
  val command = GetChassisStatus.Command

  // Create a target for the command 
  val target = IpmiTarget.LAN.forHost(
    host = "192.168.1.123",
    credentials = IpmiCredentials("user", "password")
  )

  // Run the command  
  val result: Future[GetChassisStatus.CommandResult] = ipmi.executeCommand(target, command)
  
  // Do something with the result
  // ...
```

#### Running multiple commands 
In order to execute multiple commands within a negotiated session, 
the ```withContext``` method 
(which implements the loan pattern) on the ```Ipmi``` trait  can be used:

```scala    
  // Have implicit actor system and timeout
  implicit val actorSystem: ActorSystem = ActorSystem("exampleActorSystem")
  implicit val timeoutContext: TimeoutContext = TimeoutContext.default

  val ipmi = Ipmi.create
  
  // Create a target for the command 
  val target = IpmiTarget.LAN.forHost(
      host = "192.168.1.123", 
      credentials = IpmiCredentials("user", "password")
    )

  // Run some commands...  
  val futureResult = ipmi.withContext(target) { implicit ctx =>
    val result1 = for {
      status       <- ipmi.executeCommand(GetChassisStatus.Command)
      powerOnHours <- ipmi.executeCommand(GetPohCounter.Command)
    } yield {
      // Do something with the results...
    } 
    
    // Do some other stuff...  
  }
```

Note for those functional purists who prefer their Futures generally not to complete with failure, 
there are also methods (e.g. ```executeCommandOrError```) that return a ```Future[IpmiError \/ Result]```
(where ```\/``` is the Scalaz disjunction).

For fully working examples see [here](./src/test/scala/com/cyclone/ipmi/examples).

#### Timeouts
The ```TimeoutContext``` class, which is implicitly passed to 
execution methods, allows an overall timeout to be specified 
for a command or for a sequence of commands (e.g. using the ```withContext``` 
methods). 

The main part of this is the ```deadline``` property. This is created through 
factory methods on the ```OperationDeadline``` trait's companion object.
These allow creation of absolute deadlines 
(see ```fromNow```) for when a sequence of commands taken together must complete
within before the deadline expires, 
as well as simple timeouts (see ```reusableTimeout```) which 
will be applied separately to each of a sequence of commands.  

The other part is the ```requestTimeouts``` property. This (while still being subject to the deadline)
allows internal UDP requests to be retried when no response is received from the BMC or 
when error codes received indicate that (e.g. the device is busy) and that the request should be retried following a 
short back-off.

### Java API
TODO - no Java API as yet. Sorry.
