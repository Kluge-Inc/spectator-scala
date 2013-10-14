package controllers

import models.{NewCategory, Documents, Categories}
import play.api.data.Form
import play.api.data.Forms._
import models._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import com.google.common.io.Files
import play.api.libs.iteratee.Enumerator

/**
 * Created with IntelliJ IDEA.
 * User: nchudakov
 * Date: 14.10.13
 * Time: 11:39
 */
object CategoryController extends Controller{
  def category(id: Long) = DBAction {
    implicit rs =>
      Ok(views.html.index(Categories.getForm(id), Documents.getByCategory(id)))
  }

  def addForm = DBAction {
    implicit rs =>
      Ok(views.html.addCategory(Categories.getForm))
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
