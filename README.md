# Log4FIX/J - FIX (Financial Information Exchange) Message Parser/ Viewer

## Build Instructions

Requires: Java 6, Ant 1.8+

All thirdparty JARs and licenses are included


    $ cd /path/to/Log4FIX-J
    $ ant dist

## Execute

    $ cd /path/to/Log4FIX-J/build/stage/bin
    $ java -jar log4fix-2.0.0.jar

After the Log4FIX UI launches press the "Import" button, select a FIX log file to view the log. The search bars in the top bar are responsible for searching session titles and FIX message contents respectively. The latter may be very slow when processing large log files. Consider setting the LIVE_SEARCH property in the CONFIG file (see below) to false.

*WARNING:* The original program assumed that the first message in each session was from the user, and used that to determine which messages are being received and which are outgoing. I replaced this with an equally inelegant solution of searching the surrounding log message for the strings `Sending` or `Receiving`. (There is also support for the shorthand `SEND` and `RECV`.) If your log messages do not contain one of these two strings, this will not work.

## Tests

These don't currently work all that well, as they have not been updated to reflect the changes to the code. Expect a number of test failures while building; this is to be expected.

## Sample Messages

~~There are a handful of sample messages located in the __src/test/resources/logs__ directory.~~ The sample messages do not work because of the changes to how it determines message directionality.

## CONFIG

The program accepts an optional CONFIG file in the same directory as the compiled executable .jar, allowing users to set options such as setting colors of UI elements and configuring the behavior of searches.

Sample CONFIG file:

    --PROPERTIES--
    // PROPERTY : value
    
    //LIVE_TABS : true
    //LIVE_SEARCH : true
    
    --COLORS--
    // COLOR : r,g,b
    
    //IN_COLOR : 255,255,255
    //OUT_COLOR : 255,255,255
    //IN_TEXT : 255,255,255
    //OUT_TEXT : 255,255,255
    //IN_SELECT_COLOR : 255,255,255
    //OUT_SELECT_COLOR : 255,255,255
    //IN_SELECT_TEXT : 255,255,255
    //OUT_SELECT_TEXT : 255,255,255
    //DATA : 255,255,255
    //HEADER : 255,255,255
    //TRAILER : 255,255,255

`LIVE_TABS` : whether searching by tab titles will update as the search box is updated. Usually not slow enough to warrant setting to false (default = true)

`LIVE_SEARCH` : whether searching by FIX message contents will update as the search box is updated. Often gets very slow (default = true)

`IN_COLOR` : the color of incoming messages

`OUT_COLOR` : the color of outgoing messages

`*_SELECT` : the color of a given type of message when selected

`*_TEXT` : the color of text in a given type of message

`DATA` : the color of data fields in the right table

`HEADER` : the color of header fields in the right table

`TRAILER` : the color of trailer fields in the right table