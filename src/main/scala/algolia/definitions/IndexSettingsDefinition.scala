/*
 * Copyright (c) 2016 Algolia
 * http://www.algolia.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package algolia.definitions

import algolia.http.{GET, HttpPayload, PUT}
import algolia.objects.IndexSettings
import algolia.responses.Task
import algolia.{AlgoliaClient, Executable}
import org.json4s.Formats
import org.json4s.native.Serialization._

import scala.concurrent.{ExecutionContext, Future}

case class IndexSettingsDefinition(index: String)(implicit val formats: Formats) extends Definition {

  def `with`(settings: IndexSettings) = IndexChangeSettingsDefinition(index, settings)

  override private[algolia] def build(): HttpPayload = {
    HttpPayload(
      GET,
      Seq("1", "indexes", index, "settings"),
      queryParameters = Some(Map("getVersion" -> "2")),
      isSearch = false
    )
  }

}

case class IndexChangeSettingsDefinition(index: String, settings: IndexSettings)(implicit val formats: Formats) extends Definition {
  override private[algolia] def build(): HttpPayload = {
    HttpPayload(
      PUT,
      Seq("1", "indexes", index, "settings"),
      body = Some(write(settings)),
      isSearch = false
    )
  }
}

trait IndexSettingsDsl {

  implicit val formats: Formats

  case object settings {

    def of(index: String) = IndexSettingsDefinition(index)

  }

  case object changeSettings {

    def of(index: String) = IndexSettingsDefinition(index)

  }

  implicit object IndexSettingsDefinitionExecutable extends Executable[IndexSettingsDefinition, IndexSettings] {
    override def apply(client: AlgoliaClient, settings: IndexSettingsDefinition)(implicit executor: ExecutionContext): Future[IndexSettings] = {
      client request[IndexSettings] settings.build()
    }
  }

  implicit object IndexChangeSettingsDefinitionExecutable extends Executable[IndexChangeSettingsDefinition, Task] {
    override def apply(client: AlgoliaClient, settings: IndexChangeSettingsDefinition)(implicit executor: ExecutionContext): Future[Task] = {
      client request[Task] settings.build()
    }
  }

}
