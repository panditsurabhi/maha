// Copyright 2017, Yahoo Holdings Inc.
// Licensed under the terms of the Apache License 2.0. Please see LICENSE file in project root for terms.
package com.yahoo.maha.service.factory

import java.nio.charset.StandardCharsets
import java.util
import java.util.UUID

import com.yahoo.maha.service.{DynamicMahaServiceConfig, DynamicWrapper, MahaServiceConfig}
import com.yahoo.maha.service.config.JsonMahaServiceConfig
import com.yahoo.maha.service.error.{JsonParseError, MahaServiceError}
import org.json4s.jackson.JsonMethods.parse
import org.json4s.scalaz.JsonScalaz.fromJSON
import scalaz.{Failure, Success, ValidationNel}

import scala.collection.mutable

/**
 * Created by pranavbhole on 06/06/17.
 */
class MahaServiceTest extends BaseFactoryTest {
  test("Test MahaService Init and Validation test") {
    val jsonString = """{
                       |	"registryMap": {
                       |		"er": {
                       |			"factRegistrationClass": "erFact",
                       |			"dimensionRegistrationClass": "erDim",
                       |			"executors": ["e1", "e2"],
                       |			"generators": ["g1", "g2"],
                       |			"bucketingConfigName": "OtherBucket",
                       |			"utcTimeProviderName": "erUTC",
                       |			"parallelServiceExecutorName": "erPSE",
                       |			"dimEstimatorFactoryClass": "dimEstFactoryClass",
                       |			"dimEstimatorFactoryConfig": "dimEstFactoryConfig",
                       |			"factEstimatorFactoryClass": "factEstFactoryClass",
                       |			"factEstimatorFactoryConfig": "factEstFactoryConfig",
                       |			"defaultPublicFactRevisionMap": {"a": 1, "b": 2},
                       |			"defaultPublicDimRevisionMap": {"a": 1, "b": 2}
                       |		}
                       |	},
                       |	"executorMap": {
                       |		"e1": {
                       |			"factoryClass": "e1Class",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		},
                       |		"e2": {
                       |			"factoryClass": "e2Class",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		}
                       |	},
                       |	"generatorMap": {
                       |		"g1": {
                       |			"factoryClass": "g1Class",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		},
                       |		"g2": {
                       |			"factoryClass": "g2Class",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		}
                       |	},
                       |	"bucketingConfigMap": {
                       |		"erBucket": {
                       |			"factoryClass": "erBucketClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		},
                       |		"irBucket": {
                       |			"factoryClass": "irBucketClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		}
                       |
                       |	},
                       |	"utcTimeProviderMap": {
                       |		"erUTC": {
                       |			"factoryClass": "erUTCClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		},
                       |		"irUTC": {
                       |			"factoryClass": "irUTCClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		}
                       |	},
                       | "parallelServiceExecutorConfigMap": {
                       | "commonExec": {
                       | 			"factoryClass": "irUTCClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |  }
                       | }
                       | ,
                       |   "mahaRequestLoggingConfig" : {
                       |    "factoryClass": "com.yahoo.maha.service.factory.NoopMahaRequestLogWriterFactory",
                       |    "config" : {},
                       |    "isLoggingEnabled" : false
                       |   },
                       |"curatorMap": {
                       |      "default": {
                       |         "factoryClass": "com.yahoo.maha.service.factory.DefaultCuratorFactory",
                       |         "config": {
                       |         }
                       |      },
                       |      "timeshift": {
                       |         "factoryClass": "com.yahoo.maha.service.factory.TimeShiftCuratorFactory",
                       |         "config": {
                       |         }
                       |      }
                       |   }
                       |}""".stripMargin
   val mahaServiceResult = MahaServiceConfig.fromJson(jsonString.getBytes("utf-8"))
    assert(mahaServiceResult.isFailure)
   mahaServiceResult match {
     case f@Failure(_) =>
       assert(f.e.toString().contains("Unable to find parallelServiceExecutor name erpse in map"))
       assert(f.e.toString().contains("Unable to find bucket config name otherbucket in map"))
     case Success(_) => sys.error("Service result should be an error.")
   }
  }

  test("Test MahaService Validation: Expects Success in registry Validation should fail to load factory class") {
    val jsonString = """{
                       |	"registryMap": {
                       |		"er": {
                       |			"factRegistrationClass": "erFact",
                       |			"dimensionRegistrationClass": "erDim",
                       |			"executors": ["e1", "e2"],
                       |			"generators": ["g1", "g2"],
                       |			"bucketingConfigName": "erBucket",
                       |			"utcTimeProviderName": "erUTC",
                       |			"parallelServiceExecutorName": "erParallelExec",
                       |			"dimEstimatorFactoryClass": "dimEstFactoryClass",
                       |			"dimEstimatorFactoryConfig": "dimEstFactoryConfig",
                       |			"factEstimatorFactoryClass": "factEstFactoryClass",
                       |			"factEstimatorFactoryConfig": "factEstFactoryConfig",
                       |			"defaultPublicFactRevisionMap": {"a": 1, "b": 2},
                       |			"defaultPublicDimRevisionMap": {"a": 1, "b": 2}
                       |		}
                       |	},
                       |	"executorMap": {
                       |		"e1": {
                       |			"factoryClass": "e1Class",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		},
                       |		"e2": {
                       |			"factoryClass": "e2Class",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		}
                       |	},
                       |	"generatorMap": {
                       |		"g1": {
                       |			"factoryClass": "g1Class",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		},
                       |		"g2": {
                       |			"factoryClass": "g2Class",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		}
                       |	},
                       |	"bucketingConfigMap": {
                       |		"erBucket": {
                       |			"factoryClass": "erBucketClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		},
                       |		"irBucket": {
                       |			"factoryClass": "irBucketClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		}
                       |
                       |	},
                       |	"utcTimeProviderMap": {
                       |		"erUTC": {
                       |			"factoryClass": "erUTCClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		},
                       |		"irUTC": {
                       |			"factoryClass": "irUTCClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		}
                       |	},
                       | "parallelServiceExecutorConfigMap": {
                       | "erParallelExec": {
                       | 			"factoryClass": "irUTCClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |  },
                       |   "irParallelExec": {
                       | 			"factoryClass": "irUTCClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |  }
                       | }
                       | ,
                       |  "mahaRequestLoggingConfig" : {
                       |    "factoryClass": "com.yahoo.maha.service.factory.NoopMahaRequestLogWriterFactory",
                       |    "config" : {},
                       |    "isLoggingEnabled" : false
                       |   },
                       |"curatorMap": {
                       |      "default": {
                       |         "factoryClass": "com.yahoo.maha.service.factory.DefaultCuratorFactory",
                       |         "config": {
                       |         }
                       |      },
                       |      "timeshift": {
                       |         "factoryClass": "com.yahoo.maha.service.factory.TimeShiftCuratorFactory",
                       |         "config": {
                       |         }
                       |      }
                       |   }
                       |}""".stripMargin
    val mahaServiceResult = MahaServiceConfig.fromJson(jsonString.getBytes("utf-8"))
    assert(mahaServiceResult.isFailure)
    mahaServiceResult.leftMap {
      list=>
        val errorList = list.map(a=> a.message).list.toList
        assert(errorList.containsSlice(Seq("Failed to construct factory : erBucketClass", "Failed to construct factory : irBucketClass")))
    }
  }

  test("Invalid MahaServiceEngine") {
    val h2dbId = UUID.randomUUID().toString.replace("-","")

    val invalidJson = s"""
                         |{
                         |   "registryMap": {
                         |      "er": {
                         |         "factRegistrationClass": "com.yahoo.maha.service.example.SampleFactSchemaRegistrationFactory",
                         |         "dimensionRegistrationClass": "com.yahoo.maha.service.example.SampleDimensionSchemaRegistrationFactory",
                         |         "executors": [
                         |            "oracleExec",
                         |            "druidExec"
                         |         ],
                         |         "generators": [
                         |            "oracle",
                         |            "druid"
                         |         ],
                         |         "bucketingConfigName": "erBucket",
                         |         "utcTimeProviderName": "erUTC",
                         |         "parallelServiceExecutorName": "erParallelExec",
                         |         "dimEstimatorFactoryClass": "com.yahoo.maha.service.factory.DefaultDimCostEstimatorFactory",
                         |         "dimEstimatorFactoryConfig": "",
                         |         "factEstimatorFactoryClass": "com.yahoo.maha.service.factory.DefaultFactCostEstimatorFactory",
                         |         "factEstimatorFactoryConfig": "",
                         |         "defaultPublicFactRevisionMap": {
                         |            "student_performance": 0
                         |         },
                         |         "defaultPublicDimRevisionMap": {
                         |            "student": 0
                         |         }
                         |      }
                         |   },
                         |   "executorMap": {
                         |      "oracleExec": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.OracleQueryExecutoryFactory",
                         |         "config": {
                         |            "dataSourceFactoryClass": "com.yahoo.maha.service.factory.HikariDataSourceFactory",
                         |            "dataSourceFactoryConfig": {
                         |               "driverClassName": "org.h2.Driver",
                         |               "jdbcUrl": "jdbc:h2:mem:$h2dbId;MODE=Oracle;DB_CLOSE_DELAY=-1",
                         |               "username": "sa",
                         |               "passwordProviderFactoryClassName": "com.yahoo.maha.service.factory.PassThroughPasswordProviderFactory",
                         |               "passwordProviderConfig": [
                         |                  {
                         |                     "key": "value"
                         |                  }
                         |               ],
                         |               "passwordKey": "h2.test.database.password",
                         |               "poolName": "test-pool",
                         |               "maximumPoolSize": 10,
                         |               "minimumIdle": 1,
                         |               "autoCommit": true,
                         |               "connectionTestQuery": "SELECT 1 FROM DUAL",
                         |               "validationTimeout": 1000000,
                         |               "idleTimeout": 1000000,
                         |               "maxLifetime": 10000000,
                         |               "dataSourceProperties": [
                         |                  {
                         |                     "key": "propertyKey",
                         |                     "value": "propertyValue"
                         |                  }
                         |               ]
                         |            },
                         |            "jdbcConnectionFetchSize": 10,
                         |            "lifecycleListenerFactoryClass": "com.yahoo.maha.service.factory.NoopExecutionLifecycleListenerFactory",
                         |            "lifecycleListenerFactoryConfig": [
                         |               {
                         |                  "key": "value"
                         |               }
                         |            ]
                         |         }
                         |      },
                         |      "druidExec": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.DruidQueryExecutoryFactory",
                         |         "config": {
                         |            "druidQueryExecutorConfigFactoryClassName": "com.yahoo.maha.service.factory.DefaultDruidQueryExecutorConfigFactory",
                         |            "druidQueryExecutorConfigJsonConfig": {
                         |               "maxConnectionsPerHost": 100,
                         |               "maxConnections": 10000,
                         |               "connectionTimeout": 140000,
                         |               "timeoutRetryInterval": 100,
                         |               "timeoutThreshold": 9000,
                         |               "degradationConfigName": "TestConfig",
                         |               "url": "http://broker.druid.test.maha.com",
                         |               "headers": {
                         |                  "key": "value"
                         |               },
                         |               "readTimeout": 10000,
                         |               "requestTimeout": 10000,
                         |               "pooledConnectionIdleTimeout": 10000,
                         |               "timeoutMaxResponseTimeInMs": 30000,
                         |               "enableRetryOn500": true,
                         |               "retryDelayMillis": 1000,
                         |               "maxRetry": 3,
                         |               "enableFallbackOnUncoveredIntervals" : true
                         |            },
                         |            "lifecycleListenerFactoryClass": "com.yahoo.maha.service.factory.NoopExecutionLifecycleListenerFactory",
                         |            "lifecycleListenerFactoryConfig": [
                         |               {
                         |                  "key": "value"
                         |               }
                         |            ],
                         |            "resultSetTransformersFactoryClassName": "com.yahoo.maha.service.factory.DefaultResultSetTransformersFactory",
                         |            "resultSetTransformersFactoryConfig": [
                         |               {
                         |                  "key": "value"
                         |               }
                         |            ]
                         |         }
                         |      }
                         |   },
                         |   "generatorMap": {
                         |      "oracle": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.OracleQueryGeneratorFactory",
                         |         "config": {
                         |            "partitionColumnRendererClass": "com.yahoo.maha.service.factory.DefaultPartitionColumnRendererFactory",
                         |            "partitionColumnRendererConfig": [
                         |               {
                         |                  "key": "value"
                         |               }
                         |            ],
                         |            "literalMapperClass": "com.yahoo.maha.service.factory.DefaultOracleLiteralMapperFactory",
                         |            "literalMapperConfig": [
                         |               {
                         |                  "key": "value"
                         |               }
                         |            ]
                         |         }
                         |      },
                         |      "druid": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.DruidQueryGeneratorFactory",
                         |         "config": {
                         |            "queryOptimizerClass": "com.yahoo.maha.service.factory.DefaultDruidQueryOptimizerFactory",
                         |            "queryOptimizerConfig": [
                         |               {
                         |                  "key": "value"
                         |               }
                         |            ],
                         |            "dimCardinality": 40000,
                         |            "maximumMaxRows": 5000,
                         |            "maximumTopNMaxRows": 400,
                         |            "maximumMaxRowsAsync": 100000
                         |         }
                         |      }
                         |   },
                         |   "bucketingConfigMap": {
                         |      "erBucket": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.DefaultBucketingConfigFactory",
                         |         "config": [{
                         |	  "cube": "student_performance",
                         |		"internal": [{
                         |			"revision": 0,
                         |      "percent": 10
                         |		}, {
                         |      "revision": 1,
                         |      "percent": 90
                         |    }],
                         |		"external": [{
                         |			"revision": 0,
                         |      "percent": 90
                         |		}, {
                         |      "revision": 1,
                         |      "percent": 10
                         |		}],
                         |    "dryRun": [{
                         |			"revision": 0,
                         |      "percent": 10,
                         |      "engine" : ["Fake"]
                         |		}, {
                         |      "revision": 1,
                         |      "percent": 10
                         |    }],
                         |    "userWhiteList": [{
                         |      "user" : "uid",
                         |      "revision": 0
                         |    }]
                         |}]
                         |      },
                         |      "irBucket": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.DefaultBucketingConfigFactory",
                         |         "config": [{
                         |	  "cube": "student_performance",
                         |		"internal": [{
                         |			"revision": 0,
                         |      "percent": 10
                         |		}, {
                         |      "revision": 1,
                         |      "percent": 90
                         |    }],
                         |		"external": [{
                         |			"revision": 0,
                         |      "percent": 90
                         |		}, {
                         |      "revision": 1,
                         |      "percent": 10
                         |		}],
                         |    "dryRun": [{
                         |			"revision": 0,
                         |      "percent": 100,
                         |      "engine" : "Fake"
                         |		}, {
                         |      "revision": 1,
                         |      "percent": 10
                         |    }],
                         |    "userWhiteList": [{
                         |      "user" : "uid",
                         |      "revision": 0
                         |    }]
                         |}]
                         |      }
                         |   },
                         |   "utcTimeProviderMap": {
                         |      "erUTC": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.PassThroughUTCTimeProviderFactory",
                         |         "config": {
                         |            "k": "v"
                         |         }
                         |      },
                         |      "irUTC": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.PassThroughUTCTimeProviderFactory",
                         |         "config": {
                         |            "k": "v"
                         |         }
                         |      }
                         |   },
                         |   "parallelServiceExecutorConfigMap": {
                         |      "erParallelExec": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.DefaultParallelServiceExecutoryFactory",
                         |         "config": {
                         |            "rejectedExecutionHandlerClass": "com.yahoo.maha.service.factory.DefaultRejectedExecutionHandlerFactory",
                         |            "rejectedExecutionHandlerConfig": "",
                         |            "poolName": "maha-test-pool",
                         |            "defaultTimeoutMillis": 10000,
                         |            "threadPoolSize": 3,
                         |            "queueSize": 3
                         |         }
                         |      },
                         |      "irParallelExec": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.DefaultParallelServiceExecutoryFactory",
                         |         "config": {
                         |            "rejectedExecutionHandlerClass": "com.yahoo.maha.service.factory.DefaultRejectedExecutionHandlerFactory",
                         |            "rejectedExecutionHandlerConfig": "",
                         |            "poolName": "maha-test-pool",
                         |            "defaultTimeoutMillis": 10000,
                         |            "threadPoolSize": 3,
                         |            "queueSize": 3
                         |         }
                         |      }
                         |   },
                         |   "mahaRequestLoggingConfig" : {
                         |    "factoryClass": "com.yahoo.maha.service.factory.KafkaMahaRequestLogWriterFactory",
                         |    "config" : {
                         |      "kafkaBrokerList" : "",
                         |      "bootstrapServers" : "",
                         |      "producerType" : "",
                         |      "serializerClass" : "" ,
                         |      "requestRequiredAcks" : "",
                         |      "kafkaBlockOnBufferFull" : "",
                         |      "batchNumMessages" : "" ,
                         |      "topicName" : "",
                         |      "bufferMemory" : "",
                         |      "maxBlockMs" : ""
                         |    },
                         |    "isLoggingEnabled" : false
                         |   },
                         |   "curatorMap": {
                         |      "default": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.DefaultCuratorFactory",
                         |         "config": {
                         |         }
                         |      },
                         |      "timeshift": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.TimeShiftCuratorFactory",
                         |         "config": {
                         |         }
                         |      },
                         |      "drilldown": {
                         |         "factoryClass": "com.yahoo.maha.service.factory.DrillDownCuratorFactory",
                         |         "config": {
                         |         }
                         |      }
                         |   }
                         |}
                         |
                       |	""".stripMargin
    val mahaServiceResult = MahaServiceConfig.fromJson(invalidJson.getBytes("utf-8"))
    assert(mahaServiceResult.isFailure)
  }

  test("Dependency tree creation") {
    val jsonString = s"""{
                       |	"registryMap": {
                       |		"er": {
                       |			"factRegistrationClass": "erFact",
                       |			"dimensionRegistrationClass": "erDim",
                       |			"executors": ["e1", "e2"],
                       |			"generators": ["g1", "g2"],
                       |			"bucketingConfigName": "OtherBucket",
                       |			"utcTimeProviderName": "erUTC",
                       |			"parallelServiceExecutorName": "erPSE",
                       |			"dimEstimatorFactoryClass": "dimEstFactoryClass",
                       |			"dimEstimatorFactoryConfig": "dimEstFactoryConfig",
                       |			"factEstimatorFactoryClass": "factEstFactoryClass",
                       |			"factEstimatorFactoryConfig": "factEstFactoryConfig",
                       |			"defaultPublicFactRevisionMap": {"a": 1, "b": 2},
                       |			"defaultPublicDimRevisionMap": {"a": 1, "b": 2}
                       |		}
                       |	},
                        "executorMap": {
                       |      "oracleExec": {
                       |         "factoryClass": "com.yahoo.maha.service.factory.OracleQueryExecutoryFactory",
                       |         "config": {
                       |            "dataSourceFactoryClass": "com.yahoo.maha.service.factory.HikariDataSourceFactory",
                       |            "dataSourceFactoryConfig": {
                       |               "driverClassName": "org.h2.Driver",
                       |               "jdbcUrl": "jdbc:h2:mem:someDBID;MODE=Oracle;DB_CLOSE_DELAY=-1",
                       |               "username": "sa",
                       |               "passwordProviderFactoryClassName": "com.yahoo.maha.service.factory.PassThroughPasswordProviderFactory",
                       |               "passwordProviderConfig": [
                       |                  {
                       |                     "key": "value"
                       |                  }
                       |               ],
                       |               "passwordKey": "h2.test.database.password",
                       |               "poolName": "test-pool",
                       |               "maximumPoolSize": 10,
                       |               "minimumIdle": 1,
                       |               "autoCommit": true,
                       |               "connectionTestQuery": "SELECT 1 FROM DUAL",
                       |               "validationTimeout": 1000000,
                       |               "idleTimeout": 1000000,
                       |               "maxLifetime": 10000000,
                       |               "dataSourceProperties": [
                       |                  {
                       |                     "key": "propertyKey",
                       |                     "value": "propertyValue"
                       |                  }
                       |               ]
                       |            },
                       |            "jdbcConnectionFetchSize": 10,
                       |            "lifecycleListenerFactoryClass": "com.yahoo.maha.service.factory.NoopExecutionLifecycleListenerFactory",
                       |            "lifecycleListenerFactoryConfig": [
                       |               {
                       |                  "key": "value"
                       |               }
                       |            ]
                       |         }
                       |      },
                       |      "druidExec": {
                       |         "factoryClass": "com.yahoo.maha.service.factory.DruidQueryExecutoryFactory",
                       |         "config": {
                       |            "druidQueryExecutorConfigFactoryClassName": "com.yahoo.maha.service.factory.DefaultDruidQueryExecutorConfigFactory",
                       |            "druidQueryExecutorConfigJsonConfig": {
                       |               "maxConnectionsPerHost": 100,
                       |               "maxConnections": 10000,
                       |               "connectionTimeout": 140000,
                       |               "timeoutRetryInterval": 100,
                       |               "timeoutThreshold": 9000,
                       |               "degradationConfigName": "TestConfig",
                       |               "url": "http://broker.druid.test.maha.com",
                       |               "headers": {
                       |                  "key": "value"
                       |               },
                       |               "readTimeout": "%D%(druid.read.timeout, 30000)",
                       |               "requestTimeout": 10000,
                       |               "pooledConnectionIdleTimeout": 10000,
                       |               "timeoutMaxResponseTimeInMs": 30000,
                       |               "enableRetryOn500": true,
                       |               "retryDelayMillis": 1000,
                       |               "maxRetry": 3,
                       |               "enableFallbackOnUncoveredIntervals" : true
                       |            },
                       |            "lifecycleListenerFactoryClass": "com.yahoo.maha.service.factory.NoopExecutionLifecycleListenerFactory",
                       |            "lifecycleListenerFactoryConfig": [
                       |               {
                       |                  "key": "value"
                       |               }
                       |            ],
                       |            "resultSetTransformersFactoryClassName": "com.yahoo.maha.service.factory.DefaultResultSetTransformersFactory",
                       |            "resultSetTransformersFactoryConfig": [
                       |               {
                       |                  "key": "value"
                       |               }
                       |            ],
                       |            "authHeaderProviderFactoryClassName": "com.yahoo.maha.service.factory.NoopAuthHeaderProviderFactory",
                       |"authHeaderProviderFactoryConfig" : {
                       |          "domain" : "Maha",
                       |          "service" :"MahaProviderService",
                       |          "privateKeyName" : "sa",
                       |          "privateKeyId" : "sa"
                       |        }
                       |         }
                       |      }
                       |   },
                       |	"generatorMap": {
                       |		"g1": {
                       |			"factoryClass": "g1Class",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		},
                       |		"g2": {
                       |			"factoryClass": "g2Class",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		}
                       |	},
                       |	"bucketingConfigMap": {
                       |      "erBucket": {
                       |         "factoryClass": "com.yahoo.maha.service.factory.DefaultBucketingConfigFactory",
                       |         "config": [{
                       |	  "cube": "student_performance",
                       |		"internal": [{
                       |			"revision": 0,
                       |      "percent": 10
                       |		}, {
                       |      "revision": 1,
                       |      "percent": 90
                       |    }],
                       |		"external": [{
                       |			"revision": 0,
                       |      "percent": "%D%(student_performance.external.rev0.percent, 90)"
                       |		}, {
                       |      "revision": 1,
                       |      "percent": "%D%(student_performance.external.rev1.percent, 10)"
                       |		}],
                       |    "dryRun": [{
                       |			"revision": 0,
                       |      "percent": 10
                       |		}, {
                       |      "revision": 1,
                       |      "percent": 10
                       |    }],
                       |    "userWhiteList": [{
                       |      "user" : "uid",
                       |      "revision": 0
                       |    }]
                       |}]
                       |      },
                       |      "irBucket": {
                       |         "factoryClass": "com.yahoo.maha.service.factory.DefaultBucketingConfigFactory",
                       |         "config": [{
                       |	  "cube": "student_performance",
                       |		"internal": [{
                       |			"revision": 0,
                       |      "percent": 10
                       |		}, {
                       |      "revision": 1,
                       |      "percent": 90
                       |    }],
                       |		"external": [{
                       |			"revision": 0,
                       |      "percent": 90
                       |		}, {
                       |      "revision": 1,
                       |      "percent": 10
                       |		}],
                       |    "dryRun": [{
                       |			"revision": 0,
                       |      "percent": 100
                       |		}, {
                       |      "revision": 1,
                       |      "percent": 10
                       |    }],
                       |    "userWhiteList": [{
                       |      "user" : "uid",
                       |      "revision": 0
                       |    }]
                       |}]
                       |      }
                       |   },
                       |	"utcTimeProviderMap": {
                       |		"erUTC": {
                       |			"factoryClass": "erUTCClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		},
                       |		"irUTC": {
                       |			"factoryClass": "irUTCClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |		}
                       |	},
                       | "parallelServiceExecutorConfigMap": {
                       | "commonExec": {
                       | 			"factoryClass": "irUTCClass",
                       |			"config": {
                       |				"k": "v"
                       |			}
                       |  }
                       | }
                       | ,
                       |   "mahaRequestLoggingConfig" : {
                       |    "factoryClass": "com.yahoo.maha.service.factory.NoopMahaRequestLogWriterFactory",
                       |    "config" : {},
                       |    "isLoggingEnabled" : false
                       |   },
                       |"curatorMap": {
                       |      "default": {
                       |         "factoryClass": "com.yahoo.maha.service.factory.DefaultCuratorFactory",
                       |         "config": {
                       |         }
                       |      },
                       |      "timeshift": {
                       |         "factoryClass": "com.yahoo.maha.service.factory.TimeShiftCuratorFactory",
                       |         "config": {
                       |         }
                       |      }
                       |   }
                       |}""".stripMargin

    //println(jsonString)
    val x: com.yahoo.maha.service.DynamicMahaServiceConfig = null
    val jsonMahaServiceConfigResult: ValidationNel[MahaServiceError, JsonMahaServiceConfig] =
      fromJSON[JsonMahaServiceConfig](parse(new String(jsonString.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8))).leftMap {
      nel => nel.map(err => JsonParseError(err.toString))
    }

    val jsonMahaConfig = jsonMahaServiceConfigResult.toOption.get

    val namedObjectMap = new mutable.HashMap[String, Object]()
    val bucketConfigMapResult = MahaServiceConfig.initBucketingConfig(jsonMahaConfig.bucketingConfigMap)
    val executorMapResult = MahaServiceConfig.initExecutors(jsonMahaConfig.executorMap)
    for ((key, value) <- bucketConfigMapResult.toOption.get) {
      namedObjectMap.+=((key, value))
    }
    for ((key, value) <- executorMapResult.toOption.get) {
      namedObjectMap.+=((key, value))
    }

    val dependencyTree = DynamicMahaServiceConfig.createDependencyTree(jsonMahaServiceConfigResult.toOption.get, namedObjectMap.toMap)
    println("Dependency Tree: ")
    dependencyTree.foreach(f => {
      println(s"Key: ${f._1}, dependent objects: ${f._2}")
    })
  }

  test("Dynamic objects test") {
    val jsonString = s"""{
                        |	"registryMap": {
                        |		"er": {
                        |			"factRegistrationClass": "com.yahoo.maha.service.example.SampleFactSchemaRegistrationFactory",
                        |			"dimensionRegistrationClass": "com.yahoo.maha.service.example.SampleDimensionSchemaRegistrationFactory",
                        |			"executors": ["druidExec", "oracleExec"],
                        |			"generators": ["oracle", "druid"],
                        |			"bucketingConfigName": "erBucket",
                        |			"utcTimeProviderName": "erUTC",
                        |			"parallelServiceExecutorName": "erPSE",
                        |			"dimEstimatorFactoryClass": "com.yahoo.maha.service.factory.DefaultDimCostEstimatorFactory",
                        |			"dimEstimatorFactoryConfig": "",
                        |			"factEstimatorFactoryClass": "com.yahoo.maha.service.factory.DefaultFactCostEstimatorFactory",
                        |			"factEstimatorFactoryConfig": "",
                        |			"defaultPublicFactRevisionMap": {},
                        |			"defaultPublicDimRevisionMap": {}
                        |		}
                        |	},
                        "executorMap": {
                        |      "oracleExec": {
                        |         "factoryClass": "com.yahoo.maha.service.factory.OracleQueryExecutoryFactory",
                        |         "config": {
                        |            "dataSourceFactoryClass": "com.yahoo.maha.service.factory.HikariDataSourceFactory",
                        |            "dataSourceFactoryConfig": {
                        |               "driverClassName": "org.h2.Driver",
                        |               "jdbcUrl": "jdbc:h2:mem:someDBID;MODE=Oracle;DB_CLOSE_DELAY=-1",
                        |               "username": "sa",
                        |               "passwordProviderFactoryClassName": "com.yahoo.maha.service.factory.PassThroughPasswordProviderFactory",
                        |               "passwordProviderConfig": [
                        |                  {
                        |                     "key": "value"
                        |                  }
                        |               ],
                        |               "passwordKey": "h2.test.database.password",
                        |               "poolName": "test-pool",
                        |               "maximumPoolSize": 10,
                        |               "minimumIdle": 1,
                        |               "autoCommit": true,
                        |               "connectionTestQuery": "SELECT 1 FROM DUAL",
                        |               "validationTimeout": 1000000,
                        |               "idleTimeout": 1000000,
                        |               "maxLifetime": 10000000,
                        |               "dataSourceProperties": [
                        |                  {
                        |                     "key": "propertyKey",
                        |                     "value": "propertyValue"
                        |                  }
                        |               ]
                        |            },
                        |            "jdbcConnectionFetchSize": 10,
                        |            "lifecycleListenerFactoryClass": "com.yahoo.maha.service.factory.NoopExecutionLifecycleListenerFactory",
                        |            "lifecycleListenerFactoryConfig": [
                        |               {
                        |                  "key": "value"
                        |               }
                        |            ]
                        |         }
                        |      },
                        |      "druidExec": {
                        |         "factoryClass": "com.yahoo.maha.service.factory.DruidQueryExecutoryFactory",
                        |         "config": {
                        |            "druidQueryExecutorConfigFactoryClassName": "com.yahoo.maha.service.factory.DefaultDruidQueryExecutorConfigFactory",
                        |            "druidQueryExecutorConfigJsonConfig": {
                        |               "maxConnectionsPerHost": 100,
                        |               "maxConnections": 10000,
                        |               "connectionTimeout": 140000,
                        |               "timeoutRetryInterval": 100,
                        |               "timeoutThreshold": 9000,
                        |               "degradationConfigName": "TestConfig",
                        |               "url": "http://broker.druid.test.maha.com",
                        |               "headers": {
                        |                  "key": "value"
                        |               },
                        |               "readTimeout": "%D%(druid.read.timeout, 30000)",
                        |               "requestTimeout": 10000,
                        |               "pooledConnectionIdleTimeout": 10000,
                        |               "timeoutMaxResponseTimeInMs": 30000,
                        |               "enableRetryOn500": true,
                        |               "retryDelayMillis": 1000,
                        |               "maxRetry": 3,
                        |               "enableFallbackOnUncoveredIntervals" : true
                        |            },
                        |            "lifecycleListenerFactoryClass": "com.yahoo.maha.service.factory.NoopExecutionLifecycleListenerFactory",
                        |            "lifecycleListenerFactoryConfig": [
                        |               {
                        |                  "key": "value"
                        |               }
                        |            ],
                        |            "resultSetTransformersFactoryClassName": "com.yahoo.maha.service.factory.DefaultResultSetTransformersFactory",
                        |            "resultSetTransformersFactoryConfig": [
                        |               {
                        |                  "key": "value"
                        |               }
                        |            ],
                        |            "authHeaderProviderFactoryClassName": "com.yahoo.maha.service.factory.NoopAuthHeaderProviderFactory",
                        |"authHeaderProviderFactoryConfig" : {
                        |          "domain" : "Maha",
                        |          "service" :"MahaProviderService",
                        |          "privateKeyName" : "sa",
                        |          "privateKeyId" : "sa"
                        |        }
                        |         }
                        |      }
                        |   },
                        |	"generatorMap": {
                        |		"oracle": {
                        |         "factoryClass": "com.yahoo.maha.service.factory.OracleQueryGeneratorFactory",
                        |         "config": {
                        |            "partitionColumnRendererClass": "com.yahoo.maha.service.factory.DefaultPartitionColumnRendererFactory",
                        |            "partitionColumnRendererConfig": [
                        |               {
                        |                  "key": "value"
                        |               }
                        |            ],
                        |            "literalMapperClass": "com.yahoo.maha.service.factory.DefaultOracleLiteralMapperFactory",
                        |            "literalMapperConfig": [
                        |               {
                        |                  "key": "value"
                        |               }
                        |            ]
                        |         }
                        |      },
                        |      "druid": {
                        |         "factoryClass": "com.yahoo.maha.service.factory.DruidQueryGeneratorFactory",
                        |         "config": {
                        |            "queryOptimizerClass": "com.yahoo.maha.service.factory.DefaultDruidQueryOptimizerFactory",
                        |            "queryOptimizerConfig": [
                        |               {
                        |                  "key": "value"
                        |               }
                        |            ],
                        |            "dimCardinality": 40000,
                        |            "maximumMaxRows": 5000,
                        |            "maximumTopNMaxRows": 400,
                        |            "maximumMaxRowsAsync": 100000
                        |         }
                        |      }
                        |	},
                        |	"bucketingConfigMap": {
                        |      "erBucket": {
                        |         "factoryClass": "com.yahoo.maha.service.factory.DefaultBucketingConfigFactory",
                        |         "config": [{
                        |	  "cube": "student_performance",
                        |		"internal": [{
                        |			"revision": 0,
                        |      "percent": 10
                        |		}, {
                        |      "revision": 1,
                        |      "percent": 90
                        |    }],
                        |		"external": [{
                        |			"revision": 0,
                        |      "percent": "%D%(student_performance.external.rev0.percent, 90)"
                        |		}, {
                        |      "revision": 1,
                        |      "percent": "%D%(student_performance.external.rev1.percent, 10)"
                        |		}],
                        |    "dryRun": [{
                        |			"revision": 0,
                        |      "percent": 10
                        |		}, {
                        |      "revision": 1,
                        |      "percent": 10
                        |    }],
                        |    "userWhiteList": [{
                        |      "user" : "uid",
                        |      "revision": 0
                        |    }]
                        |}]
                        |      },
                        |      "irBucket": {
                        |         "factoryClass": "com.yahoo.maha.service.factory.DefaultBucketingConfigFactory",
                        |         "config": [{
                        |	  "cube": "student_performance",
                        |		"internal": [{
                        |			"revision": 0,
                        |      "percent": 10
                        |		}, {
                        |      "revision": 1,
                        |      "percent": 90
                        |    }],
                        |		"external": [{
                        |			"revision": 0,
                        |      "percent": 90
                        |		}, {
                        |      "revision": 1,
                        |      "percent": 10
                        |		}],
                        |    "dryRun": [{
                        |			"revision": 0,
                        |      "percent": 100
                        |		}, {
                        |      "revision": 1,
                        |      "percent": 10
                        |    }],
                        |    "userWhiteList": [{
                        |      "user" : "uid",
                        |      "revision": 0
                        |    }]
                        |}]
                        |      }
                        |   },
                        |	"utcTimeProviderMap": {
                        |		"erUTC": {
                        |			"factoryClass": "com.yahoo.maha.service.factory.PassThroughUTCTimeProviderFactory",
                        |			"config": {
                        |				"k": "v"
                        |			}
                        |		},
                        |		"irUTC": {
                        |			"factoryClass": "com.yahoo.maha.service.factory.PassThroughUTCTimeProviderFactory",
                        |			"config": {
                        |				"k": "v"
                        |			}
                        |		}
                        |	},
                        | "parallelServiceExecutorConfigMap": {
                        | "erPSE": {
                        "factoryClass": "com.yahoo.maha.service.factory.DefaultParallelServiceExecutoryFactory",
                        |         "config": {
                        |            "rejectedExecutionHandlerClass": "com.yahoo.maha.service.factory.DefaultRejectedExecutionHandlerFactory",
                        |            "rejectedExecutionHandlerConfig": "",
                        |            "poolName": "maha-test-pool",
                        |            "defaultTimeoutMillis": 10000,
                        |            "threadPoolSize": 3,
                        |            "queueSize": 3
                        |         }
                        |  }
                        | }
                        | ,
                        |   "mahaRequestLoggingConfig" : {
                        |    "factoryClass": "com.yahoo.maha.service.factory.NoopMahaRequestLogWriterFactory",
                        |    "config" : {},
                        |    "isLoggingEnabled" : false
                        |   },
                        |"curatorMap": {
                        |      "default": {
                        |         "factoryClass": "com.yahoo.maha.service.factory.DefaultCuratorFactory",
                        |         "config": {
                        |         }
                        |      },
                        |      "timeshift": {
                        |         "factoryClass": "com.yahoo.maha.service.factory.TimeShiftCuratorFactory",
                        |         "config": {
                        |         }
                        |      }
                        |   }
                        |}""".stripMargin

    //println(jsonString)

    val dynamicMahaServiceConfig = DynamicMahaServiceConfig.fromJson(jsonString.getBytes(StandardCharsets.UTF_8))
    println(dynamicMahaServiceConfig)
    println(dynamicMahaServiceConfig.toOption.get.registry.get("er").get.bucketSelector.getBucketingConfig.getConfig("student_performance").get.externalBucketPercentage)

  }

  test("ByteBuddy") {
    val testObj = new ByteBuddyTest("old-value")
    val dynamicClass = DynamicWrapper.getDynamicClass(testObj)
    val instance = dynamicClass.getConstructor(classOf[String]).newInstance("new-value").asInstanceOf[ByteBuddyTest]
    dynamicClass.getField("currentObject").set(instance, testObj)
    instance.someMethod()
    dynamicClass.getField("currentObject").set(instance, new ByteBuddyTest("new-value"))
    instance.someMethod()
  }

}

class ByteBuddyTest(s: String) {

  def someMethod(): Unit = {
    println("Called someMethod: " + s)
  }
  override def toString: String = s
}