package controllers

import play.api.mvc._

object Application extends Controller {

  def index() = CategoryController.category(1)
}