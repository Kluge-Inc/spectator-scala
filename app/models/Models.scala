package models

import java.util.Date

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._

import slick.lifted.{Join, MappedTypeMapper}

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

/** Data Access Object trait
  *
  *  Used to create the DAOs: Companies and Computers
  */
private[models] trait DAO {
  val Categories= new Categories
//  val Computers = new
}

case class Category(id: Option[Long], name: String)

case class Document(id: Option[Long] = None, name: String, file: Array[Byte])

class Categories extends Table[Category]("CATEGORY") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def * = id.? ~ name <>(Category.apply _, Category.unapply _)
  def autoInc = * returning id
}

class Documents extends Table[Document]("DOCUMENT") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def file = column[Array[Byte]]("file", O.NotNull)
  //def categoryId = column[Long]("category_id", O.NotNull)
  def * = id.? ~ name ~ file <>(Document.apply _, Document.unapply _)
  def autoInc = * returning id
}

object Categories extends DAO {
  def insert(category: Category)(implicit s:Session){
    Categories.autoInc.insert(category)
  }
}
