package controllers

import models._
import play.api.db.slick._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.iteratee.Enumerator
import com.google.common.io.Files
import java.sql.Date

/**
 * Created with IntelliJ IDEA.
 * User: nchudakov
 * Date: 14.10.13
 * Time: 16:59
 */
object VersionController extends Controller{
  def showUpdate(id: Long) = DBAction {
    implicit rs => {
      val document = Documents.findById(id)
      Ok(views.html.addVersion(Categories.getForm(document._1.categoryId), document))
    }
  }


  def update(documentId: Long) = DBAction(parse.multipartFormData) {
    implicit request => {
      val document = Documents.findById(documentId)
      val version = request.body.asFormUrlEncoded("version").head
      var file: Array[Byte] = Array.emptyByteArray
      request.body.file("doc").map {
        doc =>
          file = Files.toByteArray(doc.ref.file)
          doc.ref.file.delete()
      }.getOrElse {
        BadRequest("Nope!")
      }

      Documents.insertVersion(document._1, new NewVersion(new Date(1900, 5, 20), version, file))

      Redirect(routes.DocumentsController.show(documentId))
    }
  }
}
