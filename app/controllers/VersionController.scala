package controllers

import models._
import play.api.db.slick._
import play.api.mvc._
import play.api.Play.current
import com.google.common.io.Files
import java.sql.Date
import difflib.DiffUtils
import scala.io.Source
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import play.api.libs.iteratee.Enumerator


/**
 * Created with IntelliJ IDEA.
 * User: nchudakov
 * Date: 14.10.13
 * Time: 16:59
 */
object VersionController extends Controller {
  def showUpdate(id: Long) = DBAction {
    implicit rs => {
      val document = Documents.findById(id)
      Ok(views.html.addVersion(Categories.getForm(document._1.categoryId), document))
    }
  }


  def update(documentId: Long) = DBAction(parse.multipartFormData) {
    implicit request => {
      val document = Documents.findById(documentId)
      val version = request.body.asFormUrlEncoded("version").head
      var file: Array[Byte] = Array.emptyByteArray
      request.body.file("doc").map {
        doc =>
          file = Files.toByteArray(doc.ref.file)
          doc.ref.file.delete()
      }.getOrElse {
        BadRequest("Nope!")
      }

      Documents.insertVersion(document._1, new NewVersion(new Date(1900, 5, 20), version, file))

      Redirect(routes.DocumentsController.show(documentId))
    }
  }

  def download(id: Long) = DBAction {
    implicit rs => {
      val document = Versions.byIdWithDocument(id)

      SimpleResult(
        header = ResponseHeader(200, Map("Content-Disposition" -> ("attachment; filename=" + java.net.URLEncoder.encode(document._1.name, "UTF-8") + "-" + document._2.version + ".doc"))),
        body = Enumerator(document._2.file)
      )
    }
  }

  def diff(id1: Long, id2: Long) = DBAction {
    //FIXME: Returns shit
    implicit rs => {
      val firstVersion = Versions.findById(id1).get
      val secondVersion = Versions.findById(id2).get

      val firstLines = Source.fromBytes(firstVersion.file)(scala.io.Codec.UTF8).getLines().toList
      val secondLines = Source.fromBytes(secondVersion.file)(scala.io.Codec.UTF8).getLines().toList

      val diffs = DiffUtils.diff(ListBuffer(firstLines: _*), ListBuffer(secondLines: _*))
      Ok(diffs.getDeltas.get(0).toString)
    }
  }
}
