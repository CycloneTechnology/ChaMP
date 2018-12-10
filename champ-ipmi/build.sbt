name := "champ-ipmi"

// Mostly enumerations and commands we have no way of testing currently...
coverageExcludedPackages :=
  "com\\.cyclone\\.ipmi\\.command\\..*;" +
  "com\\.cyclone\\.ipmi\\.tool\\.command\\..*;" +
  "com\\.cyclone\\.ipmi\\.sdr\\..*;" +
  "com\\.cyclone\\.ipmi\\.fru\\..*"
