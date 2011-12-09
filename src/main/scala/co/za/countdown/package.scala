package co.za

import org.joda.time.DateTime

package object countdown {
  implicit def dataTimeToMillis(d: DateTime): Long = d.getMillis
}
