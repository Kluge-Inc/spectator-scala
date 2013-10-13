package models

import java.util.Date

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._

import slick.lifted.{Join, MappedTypeMapper}


case class Document(id: Long, name: String, categoryId: Long, actualVersionId: Long)
case class NewDocument(name: String, categoryId: Long, actualVersionId: Long)




object Documents extends Table[Document]("DOCUMENT") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def actualVersionId = column[Long]("actual_version_id", O.Nullable)
  def categoryId = column[Long]("category_id", O.NotNull)

  def category = foreignKey("CATEGORY_FK", categoryId, Categories)(_.id)
  def version = foreignKey("VERSION_FK", actualVersionId, Versions)(_.id)


  def * = id ~ name ~ categoryId ~ actualVersionId <> (Document.apply _, Document.unapply _)
  def autoInc = id ~ name ~ categoryId ~ actualVersionId returning id

  def getByCategory(id: Long)(implicit s:Session) = {
    val query = for {
      d <- Documents if d.categoryId === id
    } yield d
    query.list
  }
}