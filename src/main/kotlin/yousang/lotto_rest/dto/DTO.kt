package yousang.lotto_rest.dto

import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Date


data class ApiResponse(
    val statusCode: Int,
    val message: String,
    val data: Any? = null
)

data class LottoNumberResult(
    val bnusNo: Int,
    val drwNo: Int,
    val drwNoDate: String,
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int,
    val firstAccumamnt: Long,
    val firstPrzwnerCo: Int,
    val firstWinamnt: Long,
    val totSellamnt: Long
)

data class LottoPredictNumberResult(
    val predictDrwNo: Int? = null,
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int,
    val predictPer: BigDecimal? = null,
    val predictEpoch: BigInteger? = null
)

data class AnnuityLotteryWinningNumbers(
    val group: Int,
    val nums: List<Int>
)

data class AnnuityLotteryResult(
    val roundNum: Int,
    val drawDate: Date,
    val winNums: AnnuityLotteryWinningNumbers,
    val bonusNums: List<Int>
)