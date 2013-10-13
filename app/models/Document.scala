package models

import java.util.Date

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._

import slick.lifted.{Join, MappedTypeMapper}


case class Document(id: Long, name: String, file: Array[Byte], categoryId: Long)
case class NewDocument(name: String, file: Array[Byte], categoryId: Long)




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