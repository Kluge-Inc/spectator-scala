package models


import play.api.Play.current

import play.api.db.slick.Config.driver.simple._

import slick.lifted.{Join, MappedTypeMapper}
import java.sql.Date


case class Document(id: Long, name: String, categoryId: Long, var actualVersionId: Long)
case class NewDocument(name: String, categoryId: Long)

object Documents extends Table[Document]("DOCUMENT") {
  type ActualDocument = (Document, Version)

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def actualVersionId = column[Long]("actual_version_id", O.Nullable)
  def categoryId = column[Long]("category_id", O.NotNull)

  def category = foreignKey("CATEGORY_FK", categoryId, Categories)(_.id)
//  def version = foreignKey("VERSION_FK", actualVersionId, Versions)(_.id)


  def * = id ~ name ~ categoryId ~ actualVersionId <> (Document.apply _, Document.unapply _)
  def autoInc = name ~ categoryId ~ actualVersionId returning id

  def getByCategory(id: Long)(implicit s:Session): List[ActualDocument] = {
    val query =  for {
      (d, v) <- Documents innerJoin Versions on (_.actualVersionId === _.id) if d.categoryId === id
    } yield (d, v)

    query.list
  }

  def findById(id: Long)(implicit s:Session): ActualDocument = {
    val query =  for {
      (d, v) <- Documents innerJoin Versions on (_.actualVersionId === _.id) if d.id === id
    } yield (d, v)

    query.firstOption.get
  }

  def findByIdWithVersion(id: Long)(implicit s:Session): (ActualDocument, List[Version]) = {
    (findById(id), Versions.getByDocument(id))
  }

  def insertVersion(document: Document, version: NewVersion)(implicit s:Session)={
    val versionId = Query(Versions.map(_.id).max).first().getOrElse(0L) + 1L
    Versions.insert(new Version(versionId, document.id, version.date, version.version, version.file))
    document.actualVersionId = versionId
    Documents.where(_.id === document.id).update(document)
  }

  def insertWithVersion(document: NewDocument, version: NewVersion)(implicit s:Session) = {
    //FIXME: Pretty crappy, should be different
    val documentId = Query(Documents.map(_.id).max).first().getOrElse(0L) + 1L
    val versionId = Query(Versions.map(_.id).max).first().getOrElse(0L) + 1L
    Documents.insert(new Document(documentId, document.name, document.categoryId, versionId))
    Versions.insert(new Version(versionId, documentId, new Date(1900, 05, 22), "1.0", version.file))
  }
}