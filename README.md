
# individuals-employments-income-api

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

The Individuals Employments Income API allows a developer to create, amend, retrieve and delete data relating to Employments Income.

## Development Setup

Run the microservice from the console using: `sbt run` (starts on port 7765 by default)

Start the service manager profile: `sm2 --start MTDFB_INDIVIDUALS_EMPLOYMENTS_INCOME`

## Run Tests

Run unit tests: `sbt test`

Run integration tests: `sbt it/test`

## Viewing Open API Spec (OAS) docs

To view documentation locally, ensure the API is running, and run api-documentation-frontend:
`./run_local_with_dependencies.sh`
Then go to http://localhost:9680/api-documentation/docs/openapi/preview and use this port and version:
`http://localhost:7765/api/conf/2.0/application.yaml`

## Changelog

You can see our changelog [here](https://github.com/hmrc/income-tax-mtd-changelog)

## Support and Reporting Issues

You can create a GitHub issue [here](https://github.com/hmrc/income-tax-mtd-changelog/issues)

## API Reference / Documentation

Available on
the [HMRC Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/individuals-employments-income-api)

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").