/*
 * Copyright 2023 HM Revenue & Customs
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

package v2.controllers

import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.MockAuditService
import v2.controllers.validators.MockAddCustomEmploymentValidatorFactory
import v2.mocks.services.MockAddCustomEmploymentService
import v2.models.request.addCustomEmployment.{AddCustomEmploymentRequest, AddCustomEmploymentRequestBody}
import v2.models.response.addCustomEmployment.AddCustomEmploymentResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AddCustomEmploymentControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockSharedAppConfig
    with MockAddCustomEmploymentService
    with MockAuditService
    with MockAddCustomEmploymentValidatorFactory {

  val taxYear: String      = "2019-20"
  val employmentId: String = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  val requestBodyJson: JsValue = Json.parse(
    """
      |{
      |  "employerRef": "123/AZ12334",
      |  "employerName": "AMD infotech Ltd",
      |  "startDate": "2019-01-01",
      |  "cessationDate": "2020-06-01",
      |  "payrollId": "124214112412",
      |  "occupationalPension": false
      |}
    """.stripMargin
  )

  val addCustomEmploymentRequestBody: AddCustomEmploymentRequestBody = AddCustomEmploymentRequestBody(
    employerRef = Some("123/AZ12334"),
    employerName = "AMD infotech Ltd",
    startDate = "2019-01-01",
    cessationDate = Some("2020-06-01"),
    payrollId = Some("124214112412"),
    occupationalPension = false
  )

  val requestData: AddCustomEmploymentRequest = AddCustomEmploymentRequest(
    nino = Nino(validNino),
    taxYear = TaxYear.fromMtd(taxYear),
    body = addCustomEmploymentRequestBody
  )

  val responseData: AddCustomEmploymentResponse = AddCustomEmploymentResponse(
    employmentId = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"
  )

  val responseJson: JsValue = Json.parse(
    s"""
       |{
       |   "employmentId": "$employmentId"
       |}
    """.stripMargin
  )

  "AddCustomEmploymentController" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockAddCustomEmploymentService
          .addEmployment(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestBodyJson),
          maybeExpectedResponseBody = Some(responseJson),
          maybeAuditResponseBody = Some(responseJson)
        )
      }
    }

    "return the error as per spec" when {
      "parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError, Some(requestBodyJson))
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockAddCustomEmploymentService
          .addEmployment(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotEndedError))))

        runErrorTestWithAudit(RuleTaxYearNotEndedError, Some(requestBodyJson))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller = new AddCustomEmploymentController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockAddCustomEmploymentValidatorFactory,
      service = mockAddCustomEmploymentService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.addEmployment(validNino, taxYear)(fakePostRequest(requestBodyJson))

    MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("allowTemporalValidationSuspension.enabled" -> true)).atLeastOnce()
    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "AddACustomEmployment",
        transactionName = "add-a-custom-employment",
        detail = new GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          versionNumber = apiVersion.name,
          params = Map("nino" -> validNino, "taxYear" -> taxYear),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
