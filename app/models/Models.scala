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
  //val Categories= new Categories
//  val Computers = new
}

case class Category(id: Long, name: String)
case class NewCategory(name: String)
case class CategoryForm(active: Category, categories: List[Category])

case class Document(id: Long, name: String, file: Array[Byte], categoryId: Long)
case class NewDocument(name: String, file: Array[Byte], categoryId: Long)


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

object Documents extends Table[Document]("DOCUMENT") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def file = column[Array[Byte]]("file", O.NotNull)

  def categoryId = column[Long]("category_id", O.NotNull)
  def category = foreignKey("CATEGORY_FK", categoryId, Categories)(_.id)

  def * = id ~ name ~ file ~ categoryId <>(Document.apply _, Document.unapply _)
  def autoInc = name ~ file ~ categoryId returning id

  def getByCategory(id: Long)(implicit s:Session) = {
    val query = for {
      d <- Documents if d.categoryId === id
    } yield d
    query.list
  }
}