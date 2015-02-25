/**
 *  Bidgely
 *
 *  Copyright 2015 bigpunk6
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

definition(
		name: "Bidgely",
		namespace: "bigpunk6",
		author: "bigpunk6",
		description: "Bidgely power usage data upload",
		category: "My Apps",
		iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
		iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
		iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {

	section("House Power Meter") {
		input "power", "capability.powerMeter", title: "Power Meter", required:true, multiple: false
	}
    
    section ("Bidgely API URL") {
		input "apiUrl", "text", title: "API URL", required:true
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(power, "power", handlePowerEvent)
}

def handlePowerEvent(evt) {
	sendValue(evt.value)
}

private sendValue(value) {
    def timeStamp = now().toString() [0..9]

	def postApi = [
		uri: apiUrl,
		headers: ['Content-Type': 'application/xml'],
		body:'<upload version="1.0">'+
             '<meters>'+
                '<meter id="11:22:33:44:55:66" model="API" type="0" description="NAC API">'+
                   '<streams>'+
                      '<stream id="InstantaneousDemand" unit="W" description="Real-Time Demand">'+
                         '<data time="' + timeStamp + '" value="' + value + '" />'+
                      '</stream>'+
                   '</streams>'+
                '</meter>'+
             '</meters>'+
          '</upload>'
        ]

	httpPost(postApi) { response ->
        log.info response
    }
}
