package models

/**
 * Created with IntelliJ IDEA.
 * User: giko
 * Date: 10/13/13
 * Time: 2:08 PM
 */

import java.sql.Date

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._

import slick.lifted.{Join, MappedTypeMapper}
import models.Documents.ActualDocument


case class Version(id: Long, documentId: Long, date: Date, version: String, file: Array[Byte])
case class NewVersion(date: Date, version: String, file: Array[Byte])




object Versions extends Table[Version]("VERSION") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def documentId = column[Long]("document_id", O.NotNull)
  def date =  column[Date]("date", O.NotNull)
  def version =  column[String]("version", O.NotNull)
  def file = column[Array[Byte]]("file", O.NotNull)

  def document = foreignKey("DOCUMENT_FK", documentId, Documents)(_.id)

  def * = id ~ documentId ~ date ~ version ~ file <> (Version.apply _, Version.unapply _)
  def autoInc = id ~ documentId ~ date ~ version ~ file returning id

  def byDocument = createFinderBy(_.documentId)
  def byId = createFinderBy(_.id)

  def byIdWithDocument(id:Long)(implicit s:Session) : ActualDocument = {
    val query =  for {
      (d, v) <- Documents innerJoin Versions on (_.id === _.documentId) if v.id === id
    } yield (d, v)

    query.firstOption.get
  }

  def findById(id: Long)(implicit s:Session) = byId(id).firstOption

  def getByDocument(id: Long)(implicit s:Session) = byDocument(id).list()
}