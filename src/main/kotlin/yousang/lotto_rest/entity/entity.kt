package yousang.lotto_rest.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Date

@Entity
@Table(name = "lotto_results")
data class LottoResult(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val totSellamnt: Long,
    val returnValue: String,
    val drwNoDate: Date,
    val firstWinamnt: Long,
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int,
    val bnusNo: Int,
    val firstAccumamnt: Long,
    val drwNo: Int,
    val firstPrzwnerCo: Int
)

@Entity
@Table(name = "predict_lotto_results")
data class LottoPredictResult(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val predictDrwNo: Int,
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int,
    val predictPer: BigDecimal,
    val predictEpoch: BigInteger? = null
)

@Entity
@Table(name = "annuity_lotto_results")
data class AnnuityLottoResult(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val drwNo: Int,
    val drwNoDate: Date,
    val group: Int,
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int,
    val bonusNo1: Int,
    val bonusNo2: Int,
    val bonusNo3: Int,
    val bonusNo4: Int,
    val bonusNo5: Int,
    val bonusNo6: Int,
)

@Entity
@Table(name = "predict_annuity_lotto_results")
data class AnnuityLottoPredictResult(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val predictDrwNo: Int,
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int,
    val predictPer: BigDecimal,
    val predictEpoch: BigInteger? = null
)