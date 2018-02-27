function DynamicAnalyzeWebSocketIO()
{

}
DynamicAnalyzeWebSocketIO.prototype.start = function() {
	var dawsio = this;
	$.ajax({
		url: this.streamPortUrl,
		dataType: 'json',
		cache: false,
		data: {},
		success: function(data, dataType) {
			dawsio.webSocket = new WebSocket("ws://" + location.hostname + ":" + data + "/");
			dawsio.webSocket.onopen = function() {
				dawsio.onTextRecord("Connection opened");
			};
			dawsio.webSocket.onclose = function() {
				dawsio.onTextRecord("Connection closed");
			};
			dawsio.webSocket.onmessage = function(e) {
				dawsio.onRecord(JSON.parse(e.data));
			};
			dawsio.webSocket.onerror = function(e) {
				dawsio.onTextRecord("Error: " + e);
			};
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			dawsio.onTextRecord("Error: " + textStatus);
		},
	});
};
DynamicAnalyzeWebSocketIO.prototype.streamPortUrl = "/api/streamPort";
DynamicAnalyzeWebSocketIO.prototype.webSocket = null;
DynamicAnalyzeWebSocketIO.prototype.onRecord = function(pplcpvPacket) { // abstract
	//Example:
	/* pplcpvPacket = {
		binary: "11110000001010101101001",
		bytes: [4, 6, 188] or null,
		id: 46,
		time: "20180101-000000-000",
		time_int: 3246733,
		binary: "1111000000",
	}; */
};
DynamicAnalyzeWebSocketIO.prototype.onTextRecord = function(text) {}; // abstract
DynamicAnalyzeWebSocketIO.prototype.send = function(packet) {
	//Example:
	/* packet = [2, 4, 2] */
	this.webSocket.send(JSON.stringify(packet));
};
