package com.cyclone.wsman.impl.http
import com.cyclone.util.MockeryComponent
import com.ning.http.client.AsyncHttpClient

trait MockAsyncHttpClientComponent extends AsyncHttpClientComponent {
  self: MockeryComponent =>
  lazy val asyncHttpClient = mockery.mock(classOf[AsyncHttpClient])
}
