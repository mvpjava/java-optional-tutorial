package com.mvpjava;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/*
  Java Optional Demo for ...
  "MVP Java" Blog Post www.mvpjava.com Blog Post http://mvpjava.com/java-optional/
  and "MVP Java" YouTube Video https://www.youtube.com/c/MVPJava
 */
public class OptionalDemo {

    private final Flight flight = new Flight("SAT3455");
    private static final int DEFAULT_CONFLICT_ID = -1;
    private static final Conflict DEFAULT_CONFLICT = new Conflict(DEFAULT_CONFLICT_ID);

    public static void main(String[] args) {
        OptionalDemo demo = new OptionalDemo();
//      Un-comment one at a time as per tutorial

//        demo.theProblem();
//          demo.theSolutionInTherory();
//        demo.HowToCreateOptionals();
//        demo.getValuesOutOfOptionalsUnguarded();
//        demo.HowToRetrieveValuesFromOptionalSimpleImperative();
//        demo.optionalsFunctionalMap();

//        demo.getOrElseAlternative();
//        demo.getOrElseGetAlternative();
//        demo.getOptionalOrDefaultOptional();
//        demo.getOrElseThrowAlternative();
//
//        demo.listOfOptionals();
//        demo.listOfOptionalStreamFlatMap();
    }

    void theProblem() {
        //lets pretend we want the flight to go up to FL 360 
        int actualFlightLevel = 35000;
        flight.setFlightLevel(actualFlightLevel); //current Flight Level (FL)

        int requestingFlightLevel = 36000;

        if (actualFlightLevel != requestingFlightLevel) {
            Conflict conflict = flight.probeForConfictPossiblyReturningNull();
            sendToAllDistributedApplications(conflict);
        }
    }

    void theSolutionInTherory() {
        //lets pretend we want the flight to go up to FL 360 
        int actualFlightLevel = 350;
        int requestingFlightLevel = 360;
        flight.setFlightLevel(actualFlightLevel); //current Flight Level (FL)

        if (actualFlightLevel != requestingFlightLevel) {
            Optional<Conflict> conflict = flight.probeForConflictReturningOptional();
            //commented out to show that cant pass null by accident anymore
            //sendToAllDistributedApplications(conflict);
        }
    }

    /* Three ways to create an Optional directly via static factory methods */
    void HowToCreateOptionals() {
        Conflict nullConflict = null;
        Conflict realConflict = new Conflict();

        //#1 (try/catch from demo sake)
        try {
            //Only use Optional.of() if 100% sure reference is not null
            Optional<Conflict> conflictOf = Optional.of(nullConflict);
        } catch (NullPointerException npe) {
            System.out.println("Optional.of() threw NullPointerException because past in null");
        }

        //#2
        //safer, does not throw a NullPointerException if you pass it null
        Optional<Conflict> conflictOfNullable = Optional.ofNullable(nullConflict);

        //#3
        //Sometimes you want to return this from a method instead of null
        Optional<Conflict> emptyConflict = Optional.empty();

        //DO NOT EVER DO THIS, defeats the entire purpose!!!
        //Optional.of(null);
    }
    
    /*
     Bad because using Optional.get() without checking if first value present.
     Might be ok, might not. your taking a chance
     */
    void getValuesOutOfOptionalsUnguarded() {
        Optional<Conflict> optionalConflict = flight.probeForConflictReturningOptional(); //random conflict returned
        //try/catch from demo sake
        try {
            Conflict conflict = optionalConflict.get();
            System.out.println("conflict id: " + conflict.getConflictId());

        } catch (NoSuchElementException nsee) {
            System.out.println("Un-guarded get()...got a NoSuchElementException exception!");
        }
    }
    /*
    Without use of Optional, written in the classical if/else == null way.
    versus entry level Optonal, imperative way
     */
    void HowToRetrieveValuesFromOptionalSimpleImperative() {
        //simple, non-Optional way
        Conflict conflict = flight.probeForConfictPossiblyReturningNull();
        if (conflict != null) {
            System.out.println("Conflict detected: " + conflict.getConflictId());
        } else {
            System.out.println("No Conflict");
        }

        //versus Optional - imperative way
        Optional<Conflict> optionalConflict = flight.probeForConflictReturningOptional();
        if (optionalConflict.isPresent()) {
            System.out.println("Conflict id: " + optionalConflict.get());
        } else {
            System.out.println("missing Conflict id");
        }
    }
    
    /* Now using a function style of programming. The map method
      extracts the value of the Optional (if present) as per the 
      mapper function (lambda/method reference in the case below)and return 
      an Optional of that value ... Optional<Integer>.  
     
      If the Optional value is not present in Optional, map returns an empty Optional .
      ifPresentOrElse - Since Java 9
        -will execute the "if" portion via its Consumer when the Optional has a value,
        -will execute the "Else" portion via a Runnable when Optional has no value
    
    Note: if you don't care for "Else" then just use the ifPresent​(Consumer<? super T> action)
     */
    void optionalsFunctionalMap() {
        Optional<Conflict> optionalConflict = flight.probeForConflictReturningOptional();
        optionalConflict
                .map(Conflict::getConflictId)
                .ifPresentOrElse(System.out::println,
                        () -> System.out.println("No Conflicts detected"));
    }
    
    //END OF PART 1

    /* 
      .orElse() ALWAYS gets invoked, even if Optional is present. 
      Possible performance overhead ..maybe, depends on what your creating.
     */
    Conflict getOrElseAlternative() {
        Optional<Conflict> optionalConflict = flight.probeForConflictReturningOptional();
        Conflict conflict = optionalConflict.orElse(getDefaultConflict());

        System.out.println("orElse conflict id: " + conflict.getConflictId());
        return conflict;
    }

    /* Better to use a Supplier for creation of expensive objects.
       Now Supplier .. getDefaultConflict() ONLY invoked when Optional is empty
       Great for returning a default value
     */
    Conflict getOrElseGetAlternative() {
        Optional<Conflict> optionalConflict = flight.probeForConflictReturningOptional();
        Conflict conflict = optionalConflict.orElseGet(() -> getDefaultConflict());

        System.out.println("orElseGet conflict id: " + conflict.getConflictId());
        return conflict;
    }

    /*
      New to Java 9
      Uses a Supplier to Produce an "Optional" BUT ONLY IF the Optional is empty.
      Not to be confused with orElseGet which has the Supplier return the Value
      Great for returning a default Optional
     */
    Optional<Conflict> getOptionalOrDefaultOptional() {
        Optional<Conflict> optionalConflict = flight.probeForConflictReturningOptional();//random Conflict
        return optionalConflict.or(() -> Optional.of(getDefaultConflict()));
    }

    /* Exceptions are expensive to create therefore only create them if
       Optional is empty with Supplier.
    
       orElseThrow() with NO Supplier (not shown below) will throw a 
       NoSuchElementException if Optional is empty - since Java 10
     */
    Conflict getOrElseThrowAlternative() {
        Optional<Conflict> optionalConflict = flight.probeForConflictReturningOptional();//random Conflict
        return optionalConflict.orElseThrow(NoConflictException::new); //might throw, random!
    }

   /*
      List of Optional<Conflict>
    */
    void listOfOptionals() {
        Optional<Conflict> optConflict1 = Optional.of(new Conflict(1));
        Optional<Conflict> optConflict2 = Optional.empty();
        Optional<Conflict> optConflict3 = Optional.of(new Conflict(2));

        List<Optional<Conflict>> listOptConflicts = List.of(optConflict1, optConflict2, optConflict3);
        List<Conflict> conflicts
                = listOptConflicts.stream()
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());

        conflicts.forEach(System.out::println);
    }

    /*
       flatmap which nicely reduces fiter/map Optional operations with Optional::stream
       which was added to Java 9
    
       Will end up with a stream of streams if using just "map" ...
       - listOfOptionalConflicts.stream() creates: Stream<Optional<Conflict>> 
       - if used just "map" would create: Stream<Stream<Conflict>> 
       - but now with flatMap creates: Stream<Conflict> 
    
       Optional::stream ...
            - creates a Stream<Conflict> if the Optional value is present
                thus replacing isPresent/Get
            - creates an empty Stream<> if Optional value is empty
     */
    void listOfOptionalStreamFlatMap() {
        Conflict nullConflict = null;
        Optional<Conflict> optionalc1 = Optional.empty();
        Optional<Conflict> optionalc2 = Optional.of(new Conflict());
        Optional<Conflict> optionalc3 = Optional.ofNullable(nullConflict);

        List<Optional<Conflict>> listOfOptionalConflicts = List.of(optionalc1, optionalc2, optionalc3);

        List<Conflict> actualConflicts
                = listOfOptionalConflicts.stream() //Stream<Optional<Conflict>>
                        .flatMap(Optional::stream) //Stream<Conflict>
                        .collect(Collectors.toList());

        actualConflicts.forEach(System.out::println);
    }

    Conflict getDefaultConflict() {
        System.out.println("getDefaultConflict{} has been called");
        return DEFAULT_CONFLICT;
    }

    private void sendToAllDistributedApplications(Conflict conflict) {
        //stub just to drive point home
        conflict.getConflictId(); //null? possible yes!
    }

    class NoConflictException extends RuntimeException {

    }
}
