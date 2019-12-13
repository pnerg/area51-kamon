package org.dmonix.area51.kamon

import java.util.UUID

import kamon.Kamon
import kamon.context.HttpPropagation.{HeaderReader, HeaderWriter}
import kamon.context.{Context, HttpPropagation, _}
import kamon.tag.Lookups

/**
  * Custom codec for adding an ''X-Trace-Token'' header if not present in the context.
  * This codec is configured in the ''service-template-kamon.conf'' file
  * @author Peter Nerg
  */
class XTraceTokenCodec extends Propagation.EntryReader[HeaderReader] with Propagation.EntryWriter[HeaderWriter] {
  private val headerName = "X-Trace-Token"
  /**
    * Checks if the request contains a ''X-Trace-Token'' header, if so we read it and add it to the context.
    * If not we generate a token and add it to the context.
    */
  override def read(reader: HttpPropagation.HeaderReader, context: Context): Context = {
    val token = reader.read(headerName).getOrElse(UUID.randomUUID().toString)
    context.withTag(ContextUtil.xtraceTokenTagName, token)
  }

  /**
    * Writes the X-Trace-Token header if we have a token in the context (which we should have) 
    */
  override def write(context: Context, writer: HeaderWriter): Unit = {
    ContextUtil.xTraceTokenValue.foreach(writer.write(headerName, _))
  }
}

/**
  * Utilities for fetching custom parameters from the Kamon context.
  * @author Peter Nerg
  */
object ContextUtil {
  val xtraceTokenTagName = "x-trace-token"
  
  /**
    * Fetches the value of the header param ''X-Trace-Token'' is such was set in the current request/context/span.
    * @return
    */
  def xTraceTokenValue: Option[String] = Kamon.currentContext().getTag(Lookups.option(xtraceTokenTagName))

  /**
    * Fetches the value of the header param ''X-Trace-Token'' is such was set in the current context else returns ''undefined''.
    * @return
    */
  def xTraceTokenValueOrUndefined: String = xTraceTokenValue getOrElse "undefined"
}
