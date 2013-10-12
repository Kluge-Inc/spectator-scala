package controllers

import models._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.iteratee.Enumerator
import com.google.common.io.Files

/**
 * Created with IntelliJ IDEA.
 * User: giko
 * Date: 10/13/13
 * Time: 12:11 AM
 */

object DocumentsController extends Controller {
  def download(id: Long) = DBAction {
    implicit rs => {
      val query = for {
        d <- Documents if d.id === id
      } yield d
      val document = query.firstOption.get
      SimpleResult(
        header = ResponseHeader(200, Map("Content-Disposition" -> ("attachment; filename=" + document.name + ".doc"))),
        body = Enumerator(document.file)
      )
    }
  }

  def showUpload(id: Long) = DBAction {
    implicit rs =>
      Ok(views.html.addDocument(id))
  }

  def upload(categoryId: Long) = DBAction(parse.multipartFormData) {
    implicit request => {
      val name = request.body.asFormUrlEncoded("name").head
      var file: Array[Byte] = Array.emptyByteArray
      request.body.file("doc").map {
        doc =>
          file = Files.toByteArray(doc.ref.file)
          doc.ref.file.delete()
      }.getOrElse {
        BadRequest("Nope!")
      }
      val document = new Document(None, name, file, categoryId)
      Documents.autoInc.insert(name, file, categoryId)
      Redirect(routes.Application.index)
    }
  }
}
