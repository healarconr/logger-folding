# logger-folding

Plugin for IntelliJ IDEA that folds and unfolds logger method calls in Java and Kotlin files. Supports JUL, slf4j,
Apache Commons Logging,
log4j, Android Util Log, Timber and kotlin-logging out of the box. The names of the classes of other logging
frameworks can be configured using the IDE settings (Tools > Logger folding).

Provides two new actions under the Code > Folding menu:

* Fold logger method calls (Alt Gr + L)
* Unfold logger method calls (Shift + Alt Gr + L)

Collapse by default of logger method calls can be enabled using File > Settings > Editor > General >
Code Folding and checking the Logger method calls checkbox.
