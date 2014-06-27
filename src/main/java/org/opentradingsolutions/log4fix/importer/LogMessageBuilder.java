/*
 * The Log4FIX Software License
 * Copyright (c) 2006 - 2011 Brian M. Coyner  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Neither the name of the product (Log4FIX), nor Brian M. Coyner,
 *    nor the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL BRIAN M. COYNER OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package org.opentradingsolutions.log4fix.importer;

import java.util.concurrent.BlockingQueue;

import org.opentradingsolutions.log4fix.core.LogMessage;
import org.opentradingsolutions.log4fix.core.MessageQueueItem;
import org.opentradingsolutions.log4fix.core.MessageQueueItemConstants;

import quickfix.SessionID;

/**
 * @author Brian M. Coyner
 */
public class LogMessageBuilder implements Runnable, MessageQueueItemConstants {

	public static final String EVENT_START = "Start";
	public static final String EVENT_ERROR = "ERROR";
	public static final String EVENT_MESSAGES_IMPORTED = "Messages Imported";
	public static final String EVENT_COMPLETE = "Complete";

	private final BlockingQueue<MessageQueueItem> fixMessages;
	private final ImporterModel model;

	public LogMessageBuilder(ImporterModel model,
			BlockingQueue<MessageQueueItem> fixMessages) {
		this.model = model;
		this.fixMessages = fixMessages;
	}

	public void run() {

		try {
			while (true) {
				MessageQueueItem mqi = fixMessages.take();
				String rawMessage = mqi.getRawMessage();
				if ("DONE".equals(rawMessage)) {
					break;
				} else if (rawMessage.startsWith(EVENT_ERROR)) {
					continue;
				}

				int beginIndex = 2; // 8= takes up 0 and 1... value starts at 2.
				int endIndex = rawMessage.indexOf(LogMessage.SOH_DELIMETER,
						beginIndex);
				String beginString = rawMessage.substring(2, endIndex);

				beginIndex = rawMessage.indexOf("35=") + 3;
				endIndex = rawMessage.indexOf(LogMessage.SOH_DELIMETER,
						beginIndex);

				beginIndex = rawMessage.indexOf("49=") + 3;
				endIndex = rawMessage.indexOf(LogMessage.SOH_DELIMETER,
						beginIndex);
				String senderCompId = rawMessage
						.substring(beginIndex, endIndex);

				beginIndex = rawMessage.indexOf("56=") + 3;
				endIndex = rawMessage.indexOf(LogMessage.SOH_DELIMETER,
						beginIndex);
				String targetCompId = rawMessage
						.substring(beginIndex, endIndex);
				SessionID currentSessionId = new SessionID(beginString,
						senderCompId, targetCompId);

				ImporterMemoryLog logger = model
						.getImporterMemoryLog(currentSessionId);

				if (mqi.getDirection() == Direction.INCOMING) {
					logger.onIncoming(rawMessage);
				} else {
					logger.onOutgoing(rawMessage);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}
}