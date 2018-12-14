package com.cyclone.ipmi.tool.command

import com.cyclone.akka.ActorSystemShutdown
import com.cyclone.ipmi.protocol.fru.StandardFru
import com.cyclone.ipmi.tool.command.FruPrintTool.FruInfo
import org.scalatest.{Inside, Matchers, WordSpecLike}

/**
  * Tests [[FruPrintTool]]
  */
class FruPrintToolCommandTestEx
  extends BaseToolCommandTest
    with WordSpecLike
    with Inside
    with Matchers
    with ActorSystemShutdown {

  "fru print" must {
    "calculate chunk sizes correctly" when {
      "full chunks" in {
        FruPrintTool.Command.chunkSizesAndOffsetsFor(32).toSeq shouldBe (0 until 2).map(i => (16, i * 16))
        FruPrintTool.Command.chunkSizesAndOffsetsFor(256).toSeq shouldBe (0 until 16).map(i => (16, i * 16))
      }

      "partial chunks" in {
        FruPrintTool.Command.chunkSizesAndOffsetsFor(35).toSeq shouldBe (0 until 2).map(i => (16, i * 16)) :+ (3, 32)
        FruPrintTool.Command.chunkSizesAndOffsetsFor(259).toSeq shouldBe (0 until 16).map(i => (16, i * 16)) :+ (3, 256)
      }

      "no chunks" in {
        FruPrintTool.Command.chunkSizesAndOffsetsFor(0).toSeq shouldBe Seq.empty
      }

      "one chunk" in {
        FruPrintTool.Command.chunkSizesAndOffsetsFor(8).toSeq shouldBe Seq((8, 0))
        FruPrintTool.Command.chunkSizesAndOffsetsFor(16).toSeq shouldBe Seq((16, 0))
      }
    }

    "work" in new Fixture {
      inside(executeCommand(FruPrintTool.Command).fruInfos.head) {
        case FruInfo(_, _, Some(fru: StandardFru), None) => fru.boardInfo.get.manufacturer shouldBe "Super Micro"
      }
    }
  }

}
