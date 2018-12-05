package com.cyclone.wsman.impl.xml

import scala.xml.Elem
import com.cyclone.wsman.impl.Namespace
import com.cyclone.util.OperationDeadline
import com.cyclone.wsman.command.WSManRunShellCommand

case class ShellCommandXML(
  commandQuery: WSManRunShellCommand,
  shellId: String,
  deadline: OperationDeadline
) extends RequestXMLWithDeadline {
  import RequestXML._

  // @formatter:off
  override def xml(toAddress: String): Elem =
    <s:Envelope xmlns:s={ Namespace.SOAP } xmlns:a={ Namespace.ADDRESSING } xmlns:w={ Namespace.WSMAN }>
      <s:Header>
        <a:To>{ toAddress }</a:To>
        <w:ResourceURI s:mustUnderstand="true">{ Namespace.MS_COMMAND }</w:ResourceURI>
        <a:ReplyTo>
          <a:Address s:mustUnderstand="true">{ Namespace.REPLY_ANON }</a:Address>
        </a:ReplyTo>
        <a:Action s:mustUnderstand="true">http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Command</a:Action>
        { maxEnvelopeSizeElt }
        <a:MessageID>{ uuid }</a:MessageID>
        <w:Locale xml:lang="en-US" s:mustUnderstand="false"/>
        <w:OptionSet xmlns:xsi={ Namespace.XMLSCHEMA_INSTANCE }>
          <w:Option Name="WINRS_CONSOLEMODE_STDIN">TRUE</w:Option>
          <w:Option Name="WINRS_SKIP_CMD_SHELL">FALSE</w:Option>
        </w:OptionSet>
        { timeoutElt }
        <w:SelectorSet>
          <w:Selector Name="ShellId">{ shellId }</w:Selector>
        </w:SelectorSet>
      </s:Header>
      <s:Body>
        <p:CommandLine xmlns:p={ Namespace.MS_SHELL }>
          <p:Command>{ commandQuery.command }</p:Command>
          {
            for (arg <- commandQuery.arguments) yield <p:Arguments>{ arg }</p:Arguments>
          }
        </p:CommandLine>
      </s:Body>
    </s:Envelope>
}