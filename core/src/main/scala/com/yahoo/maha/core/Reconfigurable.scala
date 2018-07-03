// Copyright 2017, Yahoo Holdings Inc.
// Licensed under the terms of the Apache License 2.0. Please see LICENSE file in project root for terms.
package com.yahoo.maha.core

trait Reconfigurable {
  def reconfigure(updatedConfigs: Map[String, Object])
}
