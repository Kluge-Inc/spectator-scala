package controllers

import models._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import com.google.common.io.Files
import play.api.libs.iteratee.Enumerator

object Application extends Controller {
  val Categories = new Categories
  val Documents = new Documents

  def index = DBAction {
    implicit rs =>
      Ok(views.html.index(Query(Categories).list))
  }

  val categoryForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> text()
    )(Category.apply)(Category.unapply)
  )

  def upload = DBAction(parse.multipartFormData) {
    implicit request => {
      val name = request.body.asFormUrlEncoded("name").head
      var file: Array[Byte] = Array.emptyByteArray
      request.body.file("doc").map {
        doc =>
          file = Files.toByteArray(doc.ref.file)
          doc.ref.file.delete()
      }.getOrElse {
        Redirect(routes.Application.index).flashing(
          "error" -> "Missing file")
      }
      val document = new Document(None, name, file)
      Documents.insert(document)
      Redirect(routes.Application.index)
    }
  }

  def download(id: Long) = DBAction {
    implicit rs => {
      val query = for {
        u <- Documents if u.id === id
      } yield u
      val document = query.firstOption.get
      SimpleResult(
        header = ResponseHeader(200, Map("Content-Disposition" -> ("attachment; filename=" + document.name + ".doc"))),
        body = Enumerator(document.file)
      )
    }
  }

  def insert = DBAction {
    implicit rs =>
      val catgory = categoryForm.bindFromRequest.get
      Categories.insert(catgory)

      Redirect(routes.Application.index)
  }

}