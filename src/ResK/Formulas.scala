package ResK

object logicalConstants {
  import ResK.expressions._
  val andS = "&"
  def andC = Var(andS, o -> (o -> o))

  val allS = "A"
  def allC(t:T) = Var("A", (t -> o ) -> o)
  
  val negS = "-"
  def negC = Var(negS, o -> o)
  
  def isLogicalConnective(c:E) = c match {
    case c: Var => if (c.name == "&" || 
                       c.name == "A") true else false
    case _ => false
  }
}

object positions {
  type Position = List[Int]
}

object formulas {
  import expressions._
  import logicalConstants._
  import positions._
  import formulaAlgorithms._

  object Atom {
    def apply(p: E, args: List[E]) = {
      val atom = (p /: args)((p,a) => App(p,a))
      require(atom.t == o)
      atom
    }
    def unapply(e:E) = e match {
      case e: Var if e.t == o => Some((e,Nil))
      case e: App if e.t == o => {
        val r @ (p,args) = unapplyRec(e)
        if (isLogicalConnective(p)) None 
        else Some(r)
      }
      case _ => None
    }
    private def unapplyRec(e: App): (E,List[E]) = e.function match {
      case a : App => {
          val (predicate, firstArgs) = unapplyRec(a)
          return (predicate, firstArgs ::: (e.argument::Nil))
      }
      case _ => return (e.function, e.argument::Nil) 
    } 
  }
  
  object And {
    def apply(f1: E, f2: E) = App(App(andC,f1),f2)
    def unapply(e:E) = e match {
      case App(App(c,f1),f2) if c syntaticEquals andC => Some((f1,f2))
      case _ => None
    }  
  }
  
  object Neg {
    def apply(f: E) = App(negC,f)
    def unapply(e:E) = e match {
      case App(c,f) if c =*= negC => Some(f)
      case _ => None
    }  
  }
  
  object All {
    def apply(v:Var, f:E) = App(allC(v.t), Abs(v,f))
    def apply(f:E, v:Var, pl:List[Position]) = {
      // ToDo: check that the terms in all positions are syntactically equal.
      val h = (f /: pl)((e,p) => deepApply(t => v.copy, e, p)) // This could be made more efficient by traversing the formula only once, instead of traversing it once for each position.
      App(allC(v.t), Abs(v,h))
    }
    def apply(f:E, v:Var, t:E) = {
      val h = deepApplyAll(x => v.copy, f, t)
      App(allC(v.t), Abs(v,h))
    }
    def unapply(e:E) = e match {
      case App(q, Abs(v,f)) if q syntaticEquals allC(v.t) => Some((v,f))
      case _ => None
    }  
  }
}


object formulaAlgorithms {
  import expressions._
  import positions._
  import formulas._
  
  def deepApplyAll(f:E=>E, e:E, t:E):E = if (e syntaticEquals t) f(e) else e match {
    case v: Var => v.copy
    case App(g,a) => App(deepApplyAll(f,g,t),deepApplyAll(f,a,t))
    case Abs(v,g) => Abs(deepApplyAll(f,v,t).asInstanceOf[Var],deepApplyAll(f,g,t))
  }
  
  def deepApply(f:E=>E, e:E, p:Position):E = (e,p) match {
    case (e,Nil) => f(e)
    case (Atom(p,args),n::tail) => {
      val newArg = deepApply(f,args(n-1),tail)
      val newArgs = (args.dropRight(args.length-n+1):::(newArg::args.drop(n))).map(x=>x.copy)
      Atom(p.copy,newArgs)
    }
    case (And(a1,a2),1::tail) => And(deepApply(f,a1,tail),a2.copy)
    case (And(a1,a2),2::tail) => And(a1.copy,deepApply(f,a2,tail))
    case (All(v,q),1::Nil) => All(f(v).asInstanceOf[Var],q.copy)
    case (All(v,q),2::tail) => All(v.copy,deepApply(f,q,tail))
    case _ => throw new Exception("deepApply: provided position seems to be an invalid position in the formula")
  }
}