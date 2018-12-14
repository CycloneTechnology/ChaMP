name := "champ-ipmi"

// Exclude some of the large number of enumerations...
coverageExcludedPackages :=
  "com\\.cyclone\\.ipmi\\.command\\..*;" +
  "com\\.cyclone\\.ipmi\\.tool\\.command\\..*;" +
  "com\\.cyclone\\.ipmi\\.protocol\\.sdr\\..*;" +
  "com\\.cyclone\\.ipmi\\.protocol\\.readingoffset\\..*;" +
  "com\\.cyclone\\.ipmi\\.protocol\\.fru\\..*"
