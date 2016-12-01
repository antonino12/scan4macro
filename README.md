# ScanUnsafe4Macro
<h3>This is a Netbean Project, checking unsafe commands for a Java Macro File.</h3>
<h4>To run, open Netbean and set Running Parameters in Running Settings of Netbean project.</h4>
<h4>Set the paramter like below.</h4>
  <RootPath>\Test\csv\ <RootPath>\Test\boundarySet.java
<h4>Program Output:</h4>
  It prints "macro is safe", iff java file has no unsafed commands.
  It prints "macro contains unsafe commands" with number of lines containg line number and full class name, iff java file has some unsafe commands.
