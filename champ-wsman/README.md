[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-wsman_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-wsman_2.12)

## Web Services-Management (WSMan)

### Features
* Windows Management Instrumentation Query Language (WQL) support
* Alternative simple object-oriented query language for non-Microsoft hosts
* Support for Kerberos and Basic authentication
* Pull and push-based event subscription
* Get and enumerate actions
* Remote shell command execution via the Windows Remote Shell (WinRS)

### Command-based API

Commands extend the WSManCommand trait.

Basic usage:
```scala
  // Have implicit actor system and timeout
  implicit val actorSystem: ActorSystem = ActorSystem("exampleActorSystem")
  implicit val timeoutContext: TimeoutContext = TimeoutContext.default

  // Create an WSMan object
  val wsman = WSMan.create

  // Create a command 
  val command = Get(ResourceUri("http://schemas.microsoft.com/wbem/wsman/1/wmi/root/cimv2/Win32_OperatingSystem"))

  // Create a target for the command 
  val target = WSManTarget(
    WSMan.httpUrlFor("somehost.acme.com", ssl = false),
    PasswordSecurityContext("user", "password", AuthenticationMethod.Kerberos)
  )

  // Run the command  
  val result: Future[WSManInstancesResult] = wsman.executeCommand(target, command)

  // Do something with the result
  // ...
```

For fully working examples see [here](./src/test/scala/com/cyclone/wsman/examples).

_TODO_ 

### Setting up Kerberos
_TODO_ 

### Setting up Push event subscriptions
_TODO_ 

### Java API
TODO - no Java API as yet



