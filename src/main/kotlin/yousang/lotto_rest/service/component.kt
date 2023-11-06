package yousang.lotto_rest.service

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import yousang.lotto_rest.repository.AnnuityLottoPredictResultRepository
import yousang.lotto_rest.repository.AnnuityLottoResultRepository
import yousang.lotto_rest.repository.LottoPredictResultRepository
import yousang.lotto_rest.repository.LottoResultRepository

@Component
class LottoServiceComponent(
    val webClientBuilder: WebClient.Builder,
    val lottoResultRepository: LottoResultRepository,
    val lottoPredictResultRepository: LottoPredictResultRepository,
    val annuityLottoResultRepository: AnnuityLottoResultRepository,
    val annuityLottoPredictResultRepository: AnnuityLottoPredictResultRepository
)