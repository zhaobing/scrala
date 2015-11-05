package com.gaocegege.scrala.core.spider.impl

import com.gaocegege.scrala.core.spider.Spider
import com.gaocegege.scrala.core.scheduler.impl.DefaultScheduler
import com.gaocegege.scrala.core.downloader.impl.HttpDownloader
import com.gaocegege.scrala.core.downloader.Downloader
import com.gaocegege.scrala.core.common.request.Request
import org.apache.http.client.methods.HttpGet
import com.gaocegege.scrala.core.common.request.impl.HttpRequest
import com.gaocegege.scrala.core.common.response.impl.HttpResponse

/**
 * Default spider
 * @author gaoce
 */
abstract class DefaultSpider extends Spider {
  private val scheduler: DefaultScheduler = new DefaultScheduler
  private val downloader: HttpDownloader = new HttpDownloader

  def request(url: String, callback: (HttpResponse) => Unit): Unit = {
    logger.info("[Request-create]-Url: " + url)
    val newRequest = new HttpRequest(new HttpGet(url), callback)
    scheduler.push(newRequest)
  }

  def begin(): Unit = {
    startUrl.map { str => scheduler.push(new HttpRequest(new HttpGet(str), this.parse)) }
    run
  }

  def run(): Unit = {
    while (true) {
      logger.debug("[Running-]-scheduler: " + scheduler.count())
      for (i <- 1 to scheduler.count()) {
        downloader.download(scheduler.pop())
      }
    }

  }
}
