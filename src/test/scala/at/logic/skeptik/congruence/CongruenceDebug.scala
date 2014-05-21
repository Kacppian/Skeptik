package at.logic.skeptik.congruence

import at.logic.skeptik.parser.ProofParserVeriT
import at.logic.skeptik.algorithm.congruence
import at.logic.skeptik.expression.formula._
import at.logic.skeptik.expression._
import at.logic.skeptik.algorithm.congruence._
import at.logic.skeptik.algorithm.dijkstra._
import at.logic.skeptik.proof._
import scala.collection.mutable.{HashMap => MMap}

object CongruenceDebug {
  def main(args: Array[String]):Unit = {
//    val proof = ProofParserVeriT.read("F:/Proofs/QF_UF/QG-classification/qg6/iso_icl_sk004.smt2")
//    CongruenceTest(proof)
    val testcase = 4
    
    val t = o
    
    val a = new Var("a",t)
    val a1 = new Var("a1",t)
    val a2 = new Var("a2",t)
    val a3 = new Var("a3",t)
    val b = new Var("b",t)
    val b1 = new Var("b1",t)
    val b2 = new Var("b2",t)
    val b3 = new Var("b3",t)
    val c = new Var("c",t)
    val c1 = new Var("c1",t)
    val c2 = new Var("c2",t)
    val c3 = new Var("c3",t)
    
    val d = new Var("d",t)
    val e = new Var("e",t)
    
    val f = new Var("f",Arrow(t,t))
    
    val x = new Var("x",Arrow(t,t))
    
    val op = new Var("op",Arrow(t,Arrow(t,t)))
    val e4 = new Var("e4",t)
    val skc1 = new Var("skc1",t)
    val e3 = new Var("e3",t)
    val e1 = new Var("e1",t)
    
//    println(App(x,b).t)
//    
//    val z1 = App(f,App(x,b))
//    val z2 = App(f,App(x,c))
//    
//    println(z1 + " type: " + z1.t)
//    println(z2 + " type: " + z2.t+"\n")
    
//    Eq(App(f,a),App(f,a))
    
    val eqReferences = MMap[(E,E),EqW]()
    
    var con = new Congruence(eqReferences)
    
    val dij = new EquationDijkstra
    
    testcase match {
      case 0 => {
        //a = b; b = c; f(a) = d; f(c) = e; 
        
        val t1 = new App(f,a)
        val t2 = new App(f,c)
        
        val eq1 = EqW(a,b,eqReferences)
        val eq2 = EqW(b,c,eqReferences)
        
        val eq3 = EqW(t1,d,eqReferences)
        val eq4 = EqW(t2,e,eqReferences)
        
        println("Input: " + List(eq1,eq2,eq3,eq4).mkString(","))
        
        con = con.addEquality(eq1)
        con = con.addEquality(eq2)
        con = con.addEquality(eq3)
        con = con.addEquality(eq4)
        
//        con = con.resolveDeducedQueue
        
        
        
        println(con.find)
        println(con.g)
        val path = dij(e,d,con.g)
    //    println(t1 + " = " + t2 + " because: " + con.explain(t1, t2).collectLabels)
        println(path)
        println(e + " = " + d + " because: " + path.originalEqs)
        
        val eqRef = con.eqReferences
        
        println("\n"+path.toProof(eqReferences).get)
//        println("trans chain?: " + con.pathTreetoProof(path))
      }
      case 1 => {
        
        //a = b1, b1 = b2, b2 = b3, b3 = c, f(a1)=a, f(c1) = c, a1 = c1
//        val t1 = App(App(f,a1),a1)
//        val t2 = App(App(f,c1),c1)
        
        val t1 = App(f,a1)
        val t2 = App(f,c1)
        
        con = con.addEquality(EqW(a,b1,eqReferences))
        con = con.addEquality(EqW(b1,b2,eqReferences))
        con = con.addEquality(EqW(b2,b3,eqReferences))
        con = con.addEquality(EqW(b3,c,eqReferences))
        con = con.addEquality(EqW(t1,a,eqReferences))
        con = con.addEquality(EqW(t2,c,eqReferences))
        con = con.addEquality(EqW(a1,c1,eqReferences))
        
//        con.resolveDeduced
        
        println(con.find)
        println(con.g)
        
        val path = dij(a,c,con.g)
        println("distance of " + c + " to " + a + ": " + dij.distances.getOrElse(c,Integer.MAX_VALUE))
        println(a + " = " + c + " because: " + path.originalEqs)
        
        con = con.resolveDeducedQueue
//        con = con.resolveDeduced(t1, t2)
        val path2 = dij(a,c,con.g)
        println("distance after " + c + " to " + a + ": " + dij.distances.getOrElse(c,Integer.MAX_VALUE))
        println(a + " = " + c + " because: " + path2.originalEqs)
        val eqRef = con.eqReferences
        println("\n"+path.toProof(eqReferences).get)
      }
      case 2 => {
        //a1 = b1, a1 = c1, f(a1) = a, f(b1) = b, f(c1) = c
        val t1 = App(f,a1)
        val t2 = App(f,b1)
        val t3 = App(f,c1)
        
        con = con.addEquality(EqW(a1,b1,eqReferences))
        con = con.addEquality(EqW(a1,c1,eqReferences))
        con = con.addEquality(EqW(t1,a,eqReferences))
        con = con.addEquality(EqW(t2,b,eqReferences))
        con = con.addEquality(EqW(t3,c,eqReferences))
        
//        con.resolveDeduced
        
        val path = dij(a,c,con.g)
        println(con.g)
        println("distance of " + c + " to " + a + ": " + dij.distances.getOrElse(c,Integer.MAX_VALUE))
        println(a + " = " + c + " because: " + path.originalEqs)
        val eqRef = con.eqReferences
        println("\n"+path.toProof(eqReferences).get)
      }
      
      case 3 => {
        //g(l,h) = d, c = d, g(l,d) = a, e = c, e = b, b = h
        
        val g = new Var("g",Arrow(t,Arrow(t,t)))
        val l = new Var("l",t)
//        val i = new Var("i",Arrow(i,i))
        val h = new Var("h",t)
        
        val t1 = App(App(g,l),h)
        val t2 = App(App(g,l),d)
        
        val eqs = List(EqW(t1,d,eqReferences),EqW(c,d,eqReferences),EqW(t2,a,eqReferences),EqW(e,c,eqReferences),EqW(e,b,eqReferences),EqW(b1,b,eqReferences),EqW(b1,h,eqReferences))
        println("===Input: " + eqs.mkString(","))
        con = eqs.foldLeft(con)({(A,B) => A.addEquality(B)})
//        con = con.addEquality(Eq(t1,d))
//        con = con.addEquality(Eq(c,d))
//        con = con.addEquality(Eq(t2,a))
//        con = con.addEquality(Eq(e,c))
//        con = con.addEquality(Eq(e,b))
//        con = con.addEquality(Eq(b1,b))
//        con = con.addEquality(Eq(b1,h))
        
//        con = con.resolveDeducedQueue
        
        
        
//        con = con.resolveDeducedQueue
        
        val path = dij(a,b,con.g)
        println(con.g)
        println("distance of " + a + " to " + b + ": " + dij.distances.getOrElse(b,Integer.MAX_VALUE))
        println(a + " = " + b + " because: " + path.originalEqs)
        val eqRef = con.eqReferences
        
        val eqs2 = List(EqW(d,c1,eqReferences),EqW(c1,c2,eqReferences),EqW(c2,c3,eqReferences),EqW(c3,h,eqReferences))
        println("\n\n\n===Input: " + (eqs ++ eqs2).mkString(","))
        con = eqs2.foldLeft(con)({(A,B) => A.addEquality(B)})
//        con = con.addEquality(Eq(d,c1))
////        con = con.addEquality(Eq(c1,c2))
//        con = con.addEquality(Eq(c1,c2))
//        con = con.addEquality(Eq(c2,c3))
//        con = con.addEquality(Eq(c3,h))
        
        con = con.resolveDeducedQueue
        
        val eqRefs2 = con.eqReferences
        
        val dij2 = new EquationDijkstra
        
        val path2 = dij2(a,b,con.g)
        println(con.g)
        println("distance of " + a + " to " + b + ": " + dij2.distances.getOrElse(b,Integer.MAX_VALUE))
        println(a + " = " + b + " because: " + path2.originalEqs)
        
//        println("trans chain?: " + path2.toProof)
        
        
        println("\n"+path.toProof(eqReferences).get)
        
        println("\n"+path2.toProof(eqReferences).get)
      }
      case 4 => {
        //(op e4 e3),(op e3 e3) from
        //((op (op e4 skc1) (op skc1 e4)) = (op e3 e3)), ((op (op e4 skc1)) = (op e3)), (e4 = (op e4 e3)), (e3 = (op e1 e4)), (e4 = (op (op e4 skc1) (op skc1 e4))), ((op e1 e4) = (op skc1 e4)))
        //((op (op e4 skc1)) = (op e3)), (e4 = (op e4 e3)), ((op (op e3 skc1) (op skc1 e3)) = e3), (e3 = (op e1 e4)), ((op (op e3 skc1) (op skc1 e3)) = (op e4 skc1)), (e4 = (op (op e4 skc1) (op skc1 e4))), ((op e1 e4) = (op skc1 e4))
        
        
        //((op e1 e3) = e1), (e4 = (op e4 e3)), ((op (op e3 skc1) (op skc1 e3)) = e3), (e1 = skc1), (e3 = (op e1 e4)), (e4 = (op e3 e1)), ((op e1 e3) = (op skc1 e3)), (e4 = (op (op e4 skc1) (op skc1 e4))), ((op e1 e4) = (op skc1 e4))
        
        val subt1 = App(App(op,e4),skc1) // (op e4 skc1)
        val subt2 = App(App(op,skc1),e4) // (op skc1 e4)
        val t1 = App(App(op,subt1),subt2) //(op (op e4 skc1) (op skc1 e4))
        val t2 = App(App(op,e3),e3) // (op e3 e3)
        val t3 = App(op,subt1) //(op (op e4 skc1))
        val t4 = App(op,e3) //(op e3)
        val t5 = App(App(op,e1),e4) //(op e1 e4)
        val t6 = App(App(op,e4),e3) //(op e4 e3)
        val t7 = App(App(op,e1),skc1) //(op e1 e3)
        val subt3 = App(App(op,e3),skc1) //(op e3 skc1)
        val subt4 = App(App(op,skc1),e3) //(op skc1 e3)
        val t8 = App(App(op,subt3),subt4) // (op (op e3 skc1) (op skc1 e3))
        val t9 = App(App(op,e1),e4) //(op e1 e4)
        val t10 = App(App(op,e3),e1) //(op e3 e1)
        
        val eq1 = EqW(t7,e1,eqReferences) // (op e1 e3) = e1)
        val eq2 = EqW(e4,t6,eqReferences) // (e4 = (op e4 e3))
        val eq3 = EqW(t8,e3,eqReferences) // ((op (op e3 skc1) (op skc1 e3)) = e3)
        val eq4 = EqW(e1,skc1,eqReferences) // (e1 = skc1)
        val eq5 = EqW(e3,t5,eqReferences) // (e3 = (op e1 e4))
        val eq6 = EqW(e4,t10,eqReferences) // (e4 = (op e3 e1))
        val eq7 = EqW(t7,subt4,eqReferences)// ((op e1 e3) = (op skc1 e3))
        val eq8 = EqW(e4,t1,eqReferences) //(e4 = (op (op e4 skc1) (op skc1 e4)))
        val eq9 = EqW(t5,subt2,eqReferences) //((op e1 e4) = (op skc1 e4))
        val allEqs = List(eq1,eq2,eq3,eq4,eq5,eq6,eq7,eq8,eq9)
        
        println(allEqs)
        
        con = con.addAll(allEqs)
        con = con.resolveDeducedQueue
        
        val eqRef = con.eqReferences
        
        val path = con.explain(t2,t6)
        println(path)
        println(path.get.toProof(eqReferences))
      }
      case 5 => {
        //((op e3 e1),(op e3 e3)) 
        //((e3 = (op e4 e4)), (e4 = skc1), (e4 = (op e3 e1)), ((op e4 e4) = (op skc1 e4)), (e4 = (op (op e4 skc1) (op skc1 e4)))
        
        val t1 = App(App(op,e4),e4) //(op e4 e4)
        val t2 = App(App(op,e3),e1) //(op e3 e1)
        val t3 = App(App(op,skc1),e4) // (op skc1 e4)
        val subt1 = App(App(op,e4),skc1) // (op e4 skc1)
        val t4 = App(App(op,subt1),t3) //(op (op e4 skc1) (op skc1 e4))
        
        val t5 = App(App(op,e3),e3) //(op e3 e3)
        
        val eq1 = EqW(e3,t1,eqReferences)
        val eq2 = EqW(e4,skc1,eqReferences)
        val eq3 = EqW(e4,t2,eqReferences)
        val eq4 = EqW(t1,t3,eqReferences)
        val eq5 = EqW(e4,t4,eqReferences)
        val allEqs = List(eq1,eq2,eq3,eq4,eq5)
        
        println(allEqs)
        
        con = con.addAll(allEqs)
        println(con.g)
//        con = con.resolveDeducedQueue
//        println("XXXXXXXXXXXXXXXXXX BEFORE ADDING")
        con = con.addNode(t5)
//        println("XXXXXXXXXXXXXXXXXX AFTER ADDING")
        val eqRef = con.eqReferences
        val path = con.explain(t5,t2)
        
//        println(path.get.toProof)
        
        val path2 = con.explain(e3,subt1)
        println(path2)
//        println(path2.get.toProof)
        
//        println(con.g)
        
//        println(dij(e1,e1,con.g))
        
        println(path)
//        println("transitivity chain:\n" + path.get.transChainProof.get)
        println("BUILDING PROOF")
        val x = path.get.toProof(eqReferences).get
        println("full proof:\n" + x)
      }
      case 6 => {
        val op = new Var("op",Arrow(t,Arrow(t,t)))
        val e3 = new Var("e3",t)
        val e4 = new Var("e4",t)
        val skc1 = new Var("skc1",t)
        
        val t1 = App(App(op,e4),e4) //(op e4 e4)
        val t2 = App(App(op,e4),skc1) // (op e4 skc1)
        
        val eq1 = EqW(e3,t1,eqReferences)
        val eq2 = EqW(e4,skc1,eqReferences)
        val allEqs = List(eq1,eq2)
        
        con = con.addAll(allEqs)
        con = con.addNode(t2)
        val eqRef = con.eqReferences
        val path = con.explain(e3,t2)
        println(path.get.toString(false))
        println("transitivity chain:\n" + path.get.toProof(eqReferences).get)
      }
    }
  }
}