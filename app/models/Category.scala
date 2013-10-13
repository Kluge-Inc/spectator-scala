package models

import java.util.Date

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._

import slick.lifted.{Join, MappedTypeMapper}

/**
 * Created with IntelliJ IDEA.
 * User: giko
 * Date: 10/13/13
 * Time: 10:47 AM
 */

case class Category(id: Long, name: String)
case class NewCategory(name: String)
case class CategoryForm(active: Category, categories: List[Category])

object Categories extends Table[Category]("CATEGORY") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def * = id ~ name <> (Category, Category.unapply _)
  def autoInc = name returning id

  def list(implicit s:Session) = Query(Categories).list
  def findById(id: Long)(implicit s:Session) = {
    val query = for {
      c <- Categories if c.id === id
    } yield c
    query.firstOption.get
  }
  def getForm(id: Long)(implicit s:Session) = new  CategoryForm(findById(id), list)
}