package com.cyclone.wsman.impl.xml

import scala.xml.Elem
import com.cyclone.wsman.impl.Namespace
import com.cyclone.util.OperationDeadline
import com.cyclone.wsman.impl.shell.{ShellCommandId, ShellId}

case class ShellReceiveXML(shellId: ShellId, commandId: ShellCommandId,
  deadline: OperationDeadline) extends RequestXMLWithDeadline {

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
        <a:Action s:mustUnderstand="true">http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Receive</a:Action>
        { maxEnvelopeSizeElt }
        <a:MessageID>{ uuid }</a:MessageID>
        <w:Locale xml:lang="en-US" s:mustUnderstand="false"/>
        { timeoutElt }
        <w:SelectorSet>
          <w:Selector Name="ShellId">{ shellId.id }</w:Selector>
        </w:SelectorSet>
      </s:Header>
      <s:Body>
        <p:Receive xmlns:p={ Namespace.MS_SHELL }>
          <p:DesiredStream CommandId={ commandId.id }>stdout stderr</p:DesiredStream>
        </p:Receive>
      </s:Body>
    </s:Envelope>

}