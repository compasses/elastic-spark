package programming.fp

import programming.fp.List._

/**
  * Created by I311352 on 3/29/2017.
  */
object foldtrick {
  def foldRightViaFoldLeft[A,B](l: List[A], z: B)(f: (A,B) => B): B =
    foldLeft(reverse(l), z)((b,a) => f(a,b))

  def foldRightViaFoldLeft_1[A,B](l: List[A], z: B)(f: (A,B) => B): B =
    foldLeft(l, (b:B) => b)((g,a) => b => g(f(a,b)))(z)

  def foldLeftViaFoldRight[A,B](l: List[A], z: B)(f: (B,A) => B): B =
    foldRight(l, (b:B) => b)((a,g) => b => g(f(b,a)))(z)

  // Here is the same function with much more description
  def foldLeftViaFoldRight_1[A,B](as: List[A], outerIdent: B)(combiner: (B, A) => B): B = {

    // foldLeft processes items in the reverse order from foldRight.  It's
    // cheating to use reverse() here because that's implemented in terms of
    // foldLeft!  Instead, wrap each operation in a simple identity function to
    // delay evaluation until later and stack (nest) the functions so that the
    // order of application can be reversed.  We'll call the type of this
    // particular identity/delay function BtoB so we aren't writing B => B
    // everywhere:
    type BtoB = B => B

    // Here we declare a simple instance of BtoB according to the above
    // description.  This function will be the identity value for the inner
    // foldRight.
    def innerIdent:BtoB = (b:B) => b

    // For each item in the 'as' list (the 'a' parameter below), make a new
    // delay function which will use the combiner function (passed in above)
    // when it is evaluated later.  Each new function becomes the input to the
    // previous function (delayFunc).
    //
    //                        This much is just the type signature
    //                  ,-------^-------.
    def combinerDelayer:(A, BtoB) => BtoB =
    (a: A, delayFunc: BtoB) => (b:B) => delayFunc(combiner(b, a))
    // `----------v---------'    `----------------v---------------'
    //         Paramaters                 The returned function

    // Pass the original list 'as', plus the simple identity function and the
    // new combinerDelayer to foldRight.  This will create the functions for
    // delayed evaluation with an combiner inside each one, but will not
    // apply any of those functions.
    def go:BtoB = foldRight(as, innerIdent)(combinerDelayer)

    // This forces all the evaluations to take place
    go(outerIdent)
  }

}
