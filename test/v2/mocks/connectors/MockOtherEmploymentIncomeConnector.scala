/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v2.mocks.connectors

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import shared.connectors.DownstreamOutcome
import uk.gov.hmrc.http.HeaderCarrier
import v2.connectors.OtherEmploymentIncomeConnector
import v2.models.request.otherEmploymentIncome.{DeleteOtherEmploymentIncomeRequest, RetrieveOtherEmploymentIncomeRequest}
import v2.models.response.retrieveOtherEmployment.RetrieveOtherEmploymentResponse

import scala.concurrent.{ExecutionContext, Future}

trait MockOtherEmploymentIncomeConnector extends TestSuite with MockFactory {

  val otherEmploymentIncomeConnector: OtherEmploymentIncomeConnector =
    mock[OtherEmploymentIncomeConnector]

  object OtherEmploymentIncomeConnector {

    def deleteOtherEmploymentIncome(request: DeleteOtherEmploymentIncomeRequest): CallHandler[Future[DownstreamOutcome[Unit]]] = {
      (
        otherEmploymentIncomeConnector
          .deleteOtherEmploymentIncome(_: DeleteOtherEmploymentIncomeRequest)(
            _: HeaderCarrier,
            _: ExecutionContext,
            _: String
          )
        )
        .expects(request, *, *, *)
    }

    def retrieveOtherEmploymentIncome(
        request: RetrieveOtherEmploymentIncomeRequest): CallHandler[Future[DownstreamOutcome[RetrieveOtherEmploymentResponse]]] = {
      (
        otherEmploymentIncomeConnector
          .retrieveOtherEmploymentIncome(_: RetrieveOtherEmploymentIncomeRequest)(
            _: HeaderCarrier,
            _: ExecutionContext,
            _: String
          )
        )
        .expects(request, *, *, *)
    }

  }

}
