package at.logic.skeptik.algorithm.compressor

import at.logic.skeptik.proof.Proof
import at.logic.skeptik.proof.sequent.SequentProofNode
import at.logic.skeptik.parser.ProofParserSPASS
import at.logic.skeptik.parser.ParserException
import scala.io.Source
import at.logic.skeptik.parser.SequentParser
import at.logic.skeptik.judgment.immutable.{ SeqSequent => Sequent }
import at.logic.skeptik.proof.sequent.lk.{ R, Axiom, UncheckedInference }
import at.logic.skeptik.expression._
import collection.mutable.{ HashMap => MMap, HashSet => MSet }
import at.logic.skeptik.proof.sequent.resolution.FindDesiredSequent
import at.logic.skeptik.proof.sequent.resolution.UnifyingResolution
import at.logic.skeptik.proof.sequent.resolution.Contraction
import java.io.PrintWriter
import java.io.File
import at.logic.skeptik.proof.sequent.resolution.FOSubstitution

object LPARExperimentDriver extends checkProofEquality {

  def countNonResolutionNodes(p: Proof[SequentProofNode]): Int = {
    var count = 0
    for (n <- p.nodes) {
      if (!n.isInstanceOf[UnifyingResolution]) {
        count = count + 1
      }
    }
    count
  }

    def countFOSub(p: Proof[SequentProofNode]): Int = {
    var count = 0
    for (n <- p.nodes) {
      if (n.isInstanceOf[FOSubstitution]) {
        count = count + 1
      }
    }
    count
  }
  def countResolutionNodes(p: Proof[SequentProofNode]): Int = {
    var count = 0
    for (n <- p.nodes) {
      if (n.isInstanceOf[UnifyingResolution]) {
        count = count + 1
      }
    }
    count
  }

  def getProblems(file: String, path: String): MSet[String] = {
    val outTj = MSet[String]()
    val etempT = new PrintWriter("FORPIErrorResults.log")

    for (line <- Source.fromFile(file).getLines()) {
           val newProblemN = path + "GoodProofs\\" + line.substring(0, 3) + "\\" + line + ".spass"
      println(newProblemN)
      outTj.add(newProblemN)
    }
    outTj
  }

  def main(args: Array[String]): Unit = {

    val path = "C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Experiments\\NoMRR\\"
    val proofList ="C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Experiments\\NoMRR\\all_good_nov10.txt" 
      //"C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\non-first-order-proofs-clean.txt"

    val problemSetS = getProblems(proofList, path)

    //    ProofParserSPASS.read("C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\FiniteHU\\TPTP-v6.1.0\\Problems\\ARI\\ARI241=1.spass, C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\PropositionalQuotes\\TPTP-v6.1.0\\Problems\\PUZ\\PUZ031+3.spass, C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\PropositionalQuotes\\TPTP-v6.1.0\\Problems\\SYN\\SYN387+1.spass, C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\PropositionalQuotes\\TPTP-v6.1.0\\Problems\\SYN\\SYN359+1.spass, C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\PropositionalQuotes\\TPTP-v6.1.0\\Problems\\GRA\\GRA072^1.spass, C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\FiniteHU\\TPTP-v6.1.0\\Problems\\GRA\\GRA050^1.spass, C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\PropositionalQuotes\\TPTP-v6.1.0\\Problems\\SYN\\SYN384+1.spass, C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\PropositionalQuotes\\TPTP-v6.1.0\\Problems\\ARI\\ARI049=1.spass, C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\PropositionalQuotes\\TPTP-v6.1.0\\Problems\\SYN\\SYN188-1.spass, C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\PropositionalQuotes\\TPTP-v6.1.0\\Problems\\SYN\\SYN356+1.spass, C:\\Users\\Jan\\Documents\\Google Summer of Code 2014\\Non-first-order Files\\FiniteHU\\TPTP-v6.1.0\\Problems\\ARI\\ARI224=1.spass")

    println(problemSetS)
    println(problemSetS.size)

    var errorCount = 0
    var totalCountT = 0

    //    val elogger = new PrintWriter("errors.elog")
    //    val eTypeLogger = new PrintWriter("errorTypes.elog")
    //    val eProblemsLogger = new PrintWriter("errorProblems.elog")
    val etempT = new PrintWriter("LPARResults-FORPI-Jun9.log")
    val header = "proof,compressed?,length,resOnlyLength,compressedLengthAll,compressedLengthResOnly,compressTime,compressRatio,compressSpeed,compressRatioRes,compressSpeedRes"
    etempT.println(header)
    etempT.flush
    val noDataString = ",-1,-1,-1,-1,-1,-1,-1,-1"

    for (probY <- problemSetS) {
      totalCountT = totalCountT + 1
      try {

        val proofToTest = ProofParserSPASS.read(probY)

        val proofLength = proofToTest.size
        val numRes = countResolutionNodes(proofToTest)
        val startTime = System.nanoTime
        val compressedProof = FORecyclePivotsWithIntersection(proofToTest)

        if (compressedProof.root.conclusion.ant.size != 0 || compressedProof.root.conclusion.suc.size != 0) {
          etempT.println(probY + ",0," + proofLength + "," + numRes + noDataString + "-ERROR")
          etempT.flush
        } else {

          val endTime = System.nanoTime
          val runTime = endTime - startTime
          val compressedLengthAll = compressedProof.size
          val compressedLengthResOnly = countResolutionNodes(compressedProof)

          val compressionRatio = (proofLength - compressedLengthAll) / proofLength.toDouble
          val compressionSpeed = (proofLength - compressedLengthAll) / runTime.toDouble

          val compressionRatioRes = (numRes - compressedLengthResOnly) / proofLength.toDouble
          val compressionSpeedRes = (numRes - compressedLengthResOnly) / runTime.toDouble
          val numSub = countFOSub(compressedProof)

          if (compressionRatioRes < 0) {
            etempT.println(probY + ",0," + proofLength + "," + numRes + noDataString)
            etempT.flush
          } else {

            etempT.println(probY + ",1," + proofLength + "," + numRes + "," + compressedLengthAll + ","
              + compressedLengthResOnly + "," + runTime + "," + compressionRatio + "," + compressionSpeed + "," + compressionRatioRes + "," + compressionSpeedRes +","+numSub)
            etempT.flush
          }
        }
      } catch {
        case e: CompressionException => {
          val proofToTest = ProofParserSPASS.read(probY)

          val proofLength = proofToTest.size
          val numRes = countResolutionNodes(proofToTest)

          etempT.println(probY.substring(79) + ",0," + proofLength + "," + numRes + noDataString)
          etempT.flush
          
        }
        case _: Throwable => {
          errorCount = errorCount + 1
        }
      }

    }

        println("errors: " + errorCount)

    println("total: " + totalCountT)

    //    elogger.flush
    //    eTypeLogger.flush
    //    eProblemsLogger.flush
  }
}



