// See LICENSE.SiFive for license details.

package freechips.rocketchip.tile

import chisel3._

import freechips.rocketchip.config.Parameters

case class CustomCSR(id: Int, mask: BigInt, init: Option[BigInt])

object CustomCSR {
  def constant(id: Int, value: BigInt): CustomCSR = CustomCSR(id, BigInt(0), Some(value))
}

class CustomCSRIO(implicit p: Parameters) extends CoreBundle {
  val wen = Bool()
  val wdata = UInt(xLen.W)
  val value = UInt(xLen.W)
}

class CustomCSRs(implicit p: Parameters) extends CoreBundle {
  // Not all cores have these CSRs, but those that do should follow the same
  // numbering conventions.  So we list them here but default them to None.
  protected def bpmCSRId = 0x7c0
  protected def bpmCSR: Option[CustomCSR] = None

  protected def chickenCSRId = 0x7c1
  protected def chickenCSR: Option[CustomCSR] = None

  //yh-// If you override this, you'll want to concatenate super.decls
  //yh-def decls: Seq[CustomCSR] = bpmCSR.toSeq ++ chickenCSR

  //yh+begin
  protected def wyfyConfigCSRId = 0x430
  protected def wyfyConfigCSR: Option[CustomCSR] = None

  protected def hbtBaseAddrCSRId = 0x431
  protected def hbtBaseAddrCSR: Option[CustomCSR] = None

  protected def hbtNumWayCSRId = 0x432
  protected def hbtNumWayCSR: Option[CustomCSR] = None

  protected def numSignedInstCSRId = 0x433
  protected def numSignedInstCSR: Option[CustomCSR] = None

  protected def numUnsignedInstCSRId = 0x434
  protected def numUnsignedInstCSR: Option[CustomCSR] = None

  protected def numBndStrCSRId = 0x435
  protected def numBndStrCSR: Option[CustomCSR] = None

  protected def numBndClrCSRId = 0x436
  protected def numBndClrCSR: Option[CustomCSR] = None

  protected def numBndSrchCSRId = 0x437
  protected def numBndSrchCSR: Option[CustomCSR] = None

  protected def numPacmaCSRId = 0x438
  protected def numPacmaCSR: Option[CustomCSR] = None

  protected def numXpacmCSRId = 0x439
  protected def numXpacmCSR: Option[CustomCSR] = None

  protected def numBndStrFailCSRId = 0x43a
  protected def numBndStrFailCSR: Option[CustomCSR] = None

  protected def numBndClrFailCSRId = 0x43b
  protected def numBndClrFailCSR: Option[CustomCSR] = None

  protected def numBndChkFailCSRId = 0x43c
  protected def numBndChkFailCSR: Option[CustomCSR] = None

  protected def numCacheHitCSRId = 0x43d
  protected def numCacheHitCSR: Option[CustomCSR] = None

  protected def numCacheMissCSRId = 0x43e
  protected def numCacheMissCSR: Option[CustomCSR] = None

  def enableWYFY          = getOrElse(wyfyConfigCSR, _.value(0), false.B)
  def hbt_base_addr       = getOrElse(hbtBaseAddrCSR, _.value, UInt(xLen.W))
  def hbt_num_way         = getOrElse(hbtNumWayCSR, _.value, UInt(xLen.W))

  def num_signed_inst     = getOrElse(numSignedInstCSR, _.value, UInt(xLen.W))
  def num_unsigned_inst   = getOrElse(numUnsignedInstCSR, _.value, UInt(xLen.W))
  def num_bndstr          = getOrElse(numBndStrCSR, _.value, UInt(xLen.W))
  def num_bndclr          = getOrElse(numBndClrCSR, _.value, UInt(xLen.W))
  def num_bndsrch         = getOrElse(numBndSrchCSR, _.value, UInt(xLen.W))
  def num_pacma           = getOrElse(numPacmaCSR, _.value, UInt(xLen.W))
  def num_xpacm           = getOrElse(numXpacmCSR, _.value, UInt(xLen.W))
  def num_bndstr_fail     = getOrElse(numBndStrFailCSR, _.value, UInt(xLen.W))
  def num_bndclr_fail     = getOrElse(numBndClrFailCSR, _.value, UInt(xLen.W))
  def num_bndchk_fail     = getOrElse(numBndChkFailCSR, _.value, UInt(xLen.W))
  def num_cache_hit       = getOrElse(numCacheHitCSR, _.value, UInt(xLen.W))
  def num_cache_miss      = getOrElse(numCacheMissCSR, _.value, UInt(xLen.W))

  // If you override this, you'll want to concatenate super.decls
  def decls: Seq[CustomCSR] = bpmCSR.toSeq ++ chickenCSR ++ wyfyConfigCSR ++ hbtBaseAddrCSR ++
                                hbtNumWayCSR ++ numSignedInstCSR ++ numUnsignedInstCSR ++
                                numBndStrCSR ++ numBndClrCSR ++ numBndSrchCSR ++
                                numPacmaCSR ++ numXpacmCSR ++
                                numBndStrFailCSR ++ numBndClrFailCSR ++ numBndChkFailCSR ++
                                numCacheHitCSR ++ numCacheMissCSR
  //yh+end


  val csrs = Vec(decls.size, new CustomCSRIO)

  def flushBTB = getOrElse(bpmCSR, _.wen, false.B)
  def bpmStatic = getOrElse(bpmCSR, _.value(0), false.B)
  def disableDCacheClockGate = getOrElse(chickenCSR, _.value(0), false.B)
  def disableICacheClockGate = getOrElse(chickenCSR, _.value(1), false.B)
  def disableCoreClockGate = getOrElse(chickenCSR, _.value(2), false.B)
  def disableSpeculativeICacheRefill = getOrElse(chickenCSR, _.value(3), false.B)
  def suppressCorruptOnGrantData = getOrElse(chickenCSR, _.value(9), false.B)

  protected def getByIdOrElse[T](id: Int, f: CustomCSRIO => T, alt: T): T = {
    val idx = decls.indexWhere(_.id == id)
    if (idx < 0) alt else f(csrs(idx))
  }

  protected def getOrElse[T](csr: Option[CustomCSR], f: CustomCSRIO => T, alt: T): T =
    csr.map(c => getByIdOrElse(c.id, f, alt)).getOrElse(alt)

}
