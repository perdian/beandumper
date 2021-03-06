Ever wondered while using JSTL tags during the developing of a JSP page what
content you actually can use within an expression? Take a look at the
following example:

      <html>
          ...
          <c:out value="${flight.destination.airportCode}" />
          ...
      </html>

Okay, so you print the airport code of the destination of a flight. So far,
so good. But what if you want to print other information about the flight as
well? To know what information is available in the flight object you either
have to take a look into the actual source code for the Java class of the
object you're interested in (here: the flight object) or - if you don't have
access to the source code - find someone who can give you that information.
In either case it's not as quick and easy as you'd like.

That's were the BeanDumper comes into play. It's a nice little JSTL tag that
prints the object graph containing all the properties of an object (and the
properties of the properties, and so on).

It's usage is quite simple and involves two steps. First, you have to include
the dependency to the pom.xml of your project:

      <project>
          ...
          <dependency>
              <groupId>de.perdian.apps.beandumper</groupId>
              <artifactId>beandumper</artifactId>
              <version>1.1.0</version>
          </dependency>
          ...
      </project>

Now all you have to do in your JSP file is declare the tag library and insert
the tag at the corresponding location where you want the output to be shown:

      <%@ taglib prefix="bd" uri="http://github.com/perdian/beandumper" %>
      <html>
          ...
          <div id="somewhere-in-your-page">
              <bd:dump root="${flight}" prefix="flight" />
          </div>
          ...
      </html>

Now, the resulting page will output the complete object graph of the flight
object.

      flight = ...
      flight.destination = ...
      flight.destination.airportCode = MUC
      flight.destination.current = <null>
      flight.destination.date = 18. Okt 2013
      flight.destination.gate = A42
      flight.destination.scheduled = 08:05
      flight.destination.status = cancelled
      flight.destination.terminal = <null>
      flight.flightNumber = 1234
      flight.operatingCarrier = <null>
      flight.origin = ...
      flight.origin.airportCode = DUS
      flight.origin.current = <null>
      flight.origin.date = 18. Okt 2013
      flight.origin.gate = A42
      flight.origin.scheduled = 06:55
      flight.origin.status = cancelled
      flight.origin.terminal = <null>

The left part of the each line represents the key that can be used within the
JSTL expression, the right part represents the value that is stored inside the
beans property. So, in our example, if you want to print the status of the
destination gate you could directly copy and pase the expression into your page:

      <html>
          ...
          <div>Destination airport: <c:out value="${flight.destination.airportCode}" /></div>
          <div>Destination gate: <c:out value="${flight.destination.gate}" /></div>
          ...
      </html>

You might want to know why you have to enter the flight key twice into the tag.
This has to do with the way JSP handles object references. The ${flight} tag
will be converted internally to the actual object, which means the tag itself
doesn't know that the object it received was originally named "flight". So, we
also have to pass the name to the tag as prefix. It is explicitely called prefix
because the name is just added to the front of the key. We could also have set
the prefix to "foo" and then the output keys from the list above would have been
like "foo.flightNumber = XY1234". The tag doesn't care, but then you wouldn't be
able to just copy and paste the key - so keep in mind to keep the "root" and the
"prefix" attributes values in sync.

Bonus feature: If you completly omit the "root" and "prefix" attributes, then
the tag will print the complete context, which means all the objects that you
can you in the JSP page - not just a single one you're interested in like we
saw in the first example.

      <%@ taglib prefix="bd" uri="http://github.com/perdian/beandumper" %>
      <html>
          ...
          <div id="bottom-of-your-page">
              <bd:dump />
          </div>
      </html>

If you want to use this feature you should place the tag at the very bottom of
your page since the output might become quite large.

You can also add ignore pattern, which define which parts of the output you do
not want to see in the result:

      <bd:dump>
          <bd:ignore pattern=".*?de\.test\..*" />
      </bd:dump>

This example will remove any output from the result that starts with `de.test.`.
Please be aware, that once an object has been removed, it's children will not be
added to the output as well. For example, take the following regular output:

      flight = ...
      flight.destination = ...
      flight.destination.airportCode = MUC

If we now create the following ignore pattern:

      <bd:dump>
          <bd:ignore pattern="flight\.destination" />
      </bd:dump>

While strictly speaking this should just remove the second line from the output,
it will also remove the third line `flight.destination.airportCode` from the
output, since the `airportCode` property is a direct descendent from the
`flight.destination` object and once an object has been removed from the result
all it's child properties will also never be accessed.

Note:
The output that is generated by the tag doesn't always has to be *exactly* what
you'll see when using the same expression in a c:out or other tag. For regular
Java types (like String, Integer, ...) you can expect it to be very acurate.
However, if you're dealing with complex objects that have very special behaviour
then you might see different output results.

Always remember: The dump tag isn't designed to be a 1:1 mirror of what a JSP
evaluation will produce - it's just a simple and quick way to get information
about an object within the JSP context.