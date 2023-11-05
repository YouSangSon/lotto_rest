package yousang.lotto_rest.entity

import jakarta.persistence.*
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
    val predictDrwNo: Int,
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int,
)