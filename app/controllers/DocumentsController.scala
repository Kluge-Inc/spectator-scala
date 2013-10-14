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
 * User: giko
 * Date: 10/13/13
 * Time: 12:11 AM
 */

object DocumentsController extends Controller {
  def download(id: Long) = DBAction {
    implicit rs =>
      val document = Documents.findById(id)
      SimpleResult(
        header = ResponseHeader(200, Map("Content-Disposition" -> ("attachment; filename=" + document._1.name + ".doc"))),
        body = Enumerator(document._2.file)
      )
  }

  def showUploadToCategory(id: Long) = DBAction {
    implicit rs =>
      Ok(views.html.addDocument(Categories.getForm(id), None))
  }


  def show(id: Long) = DBAction {
    implicit rs => {
      val document = Documents.findByIdWithVersion(id)
      Ok(views.html.document(Categories.getForm(document._1._1.categoryId), document))
    }
  }


  def uploadToCategory(categoryId: Long) = DBAction(parse.multipartFormData) {
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
      Documents.insertWithVersion(new NewDocument(name, categoryId), new NewVersion(null, "1.0", file))
      Redirect(routes.CategoryController.category(categoryId))
    }
  }
}
