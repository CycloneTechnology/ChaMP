[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-wsman_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-wsman_2.12)

## Web Services-Management (WSMan)

### Features
* Windows Management Instrumentation Query Language (WQL) support.
* Alternative simple object-oriented query language (e.g. for non-Microsoft hosts).
* Support for Kerberos and Basic authentication.
* Pull and push-based event subscription (push based subscription against Akka Http web server).
* Get and enumerate actions.
* Remote shell command execution via the Windows Remote Shell (WinRS).

### Running commands

Commands extend the ```WSManCommand``` trait.

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

### Subscribing to events

Subscriptions to WS-Management events use Akka Streams. The ```WSMan``` ```subscribe``` method 
will return a ```Source``` which can be run with a ```Sink``` of your choosing.
 
Unsubscription will occur automatically when the stream completes (e.g. through using a ```KillSwitch```).   

```scala
  // Subscribe to the Windows event log...
  val source: Source[SubscriptionItem, SubscriptionId] = wsman.subscribe(
    WSManTarget(
      WSMan.httpUrlFor(host, ssl = false),
      PasswordSecurityContext(username, password, AuthenticationMethod.Kerberos)
    ),
    SubscribeByWQL(
      "SELECT * FROM __InstanceCreationEvent WITHIN 1" +
        " WHERE TargetInstance ISA 'Win32_NTLogEvent'"
    )
  )
     
  source.runWith(Sink.foreach(println))
```

By default, subscription is pull based, that is we repeatedly send SOAP packets to poll the device for events.

Push based subscription is also supported with a little more configuration. It requires a web server
and a means to forward events received as HTTP POST requests to the required subscribers. An implementation 
using a Akka Http web server is included but this could be extended to work with other web servers. 

For fully working examples see [here](./src/test/scala/com/cyclone/wsman/examples).

### See also 
* [Notes on Kerberos configuration](KERBEROS.md)
* [Notes on setting up openwsman on Linux](OPENWSMAN.md)

### Java API
TODO - no Java API as yet. Sorry.
