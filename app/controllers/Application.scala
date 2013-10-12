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

  def index() = category(1)

  def category(id: Long) = DBAction {
    implicit rs =>
      Ok(views.html.index(Documents.getByCategory(id)))
  }

  val categoryForm = Form(
    mapping(
      "name" -> text()
    )(NewCategory.apply)(NewCategory.unapply)
  )

  def insert = DBAction {
    implicit rs =>
      val catgory = categoryForm.bindFromRequest.get
      Categories.autoInc insert catgory.name

      Redirect(routes.Application.index)
  }
}